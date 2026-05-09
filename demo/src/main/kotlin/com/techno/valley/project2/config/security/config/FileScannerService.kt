package com.techno.valley.project2.config.security.config

import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.post.config.StorageProperties
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime
import java.util.*

@Component
class FileScannerService(
    private val storageProperties: StorageProperties
) {

    private val tempUploadDir by lazy { File(storageProperties.tempUploadDir) }
    private val quarantineDir by lazy { File(storageProperties.quarantineDir) }
    private val suspiciousUsersLog by lazy { File(storageProperties.suspiciousLogPath) }

    private val allowedMimeTypes = listOf(
        "application/pdf",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",       // .xlsx
        "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .pptx
        "application/zip",   // .zip
        "application/x-rar-compressed", // .rar
        "text/plain", // .txt
        "image/png",
        "image/jpeg"
    )

    private val allowedExtensions = listOf(
        ".pdf", ".docx", ".xlsx", ".pptx", ".txt", ".png", ".jpg", ".jpeg", ".zip", ".rar"
    )

    init {
        if (!tempUploadDir.exists()) tempUploadDir.mkdirs()
        if (!quarantineDir.exists()) quarantineDir.mkdirs()
        if (!suspiciousUsersLog.exists()) suspiciousUsersLog.createNewFile()
    }

    fun scanFile(file: MultipartFile, auth: UsersAuthentication): File? {
        val originalFilename = file.originalFilename ?: return null

        val lowerName = originalFilename.lowercase()
        val hasValidExtension = allowedExtensions.any { lowerName.endsWith(it) }
        if (!hasValidExtension) {
            logSuspiciousUser("❌ Rejected extension: $lowerName | UserID: ${auth.id} | Username: ${auth.name}")
            return null
        }

        val tempFile = File(tempUploadDir, UUID.randomUUID().toString() + "_" + originalFilename)
        file.inputStream.use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        }

        // فقط لصاحب العملية (spring boot)
        tempFile.setReadable(true, true)
        tempFile.setWritable(true, true)

        try {
            val mimeType = Files.probeContentType(tempFile.toPath()) ?: "unknown"
            if (mimeType !in allowedMimeTypes) {
                tempFile.delete()
                logSuspiciousUser("⛔ MimeType Blocked: $mimeType | UserID: ${auth.id} | Username: ${auth.name}")
                return null
            }

            val process = ProcessBuilder("clamdscan", "--no-summary", tempFile.absolutePath).start()
            val result = process.inputStream.bufferedReader().readText()
            process.waitFor()

            return if (result.contains("FOUND")) {
                tempFile.delete()
                logSuspiciousUser("🚨 Virus FOUND | file: $lowerName | UserID: ${auth.id} | Username: ${auth.name}")
                null
            } else {
                tempFile
            }

        } catch (ex: Exception) {
            tempFile.delete()
            logSuspiciousUser("⚠️ Scan Failed | UserID: ${auth.id} | Username: ${auth.name} | Error: ${ex.message}")
            return null
        }
    }

    private fun logSuspiciousUser(info: String) {
        suspiciousUsersLog.appendText("[${LocalDateTime.now()}] $info\n")
    }
}
