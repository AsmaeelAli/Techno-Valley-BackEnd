package com.techno.valley.project2.config.security.config

import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.post.config.StorageProperties
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.UUID

@Component
class FileScannerService(
    private val storageProperties: StorageProperties
) {

    private val tempUploadDir by lazy { File(storageProperties.tempUploadDir) }
    private val quarantineDir by lazy { File(storageProperties.quarantineDir) }
    private val suspiciousUsersLog by lazy { File(storageProperties.suspiciousLogPath) }

    init {
        if (!tempUploadDir.exists()) tempUploadDir.mkdirs()
        if (!quarantineDir.exists()) quarantineDir.mkdirs()
        if (!suspiciousUsersLog.exists()) suspiciousUsersLog.createNewFile()
    }

    fun scanFile(file: MultipartFile, auth: UsersAuthentication): ScanResult {
        val tempFile = File(tempUploadDir, UUID.randomUUID().toString() + "_" + file.originalFilename)
        file.transferTo(tempFile)

        val process = ProcessBuilder("clamscan", tempFile.absolutePath).start()
        val result = process.inputStream.bufferedReader().readText()
        process.waitFor()

        return if (result.contains("FOUND")) {
            tempFile.copyTo(File(quarantineDir, tempFile.name), overwrite = true)
            tempFile.delete()
            logSuspiciousUser("UserID:${auth.id}  Username:${auth.name}")
            ScanResult.Failed("Want to play?\n" +
                    "Everything is recorded.")
        } else {
            tempFile.delete()
            ScanResult.Clean("Bravo")
        }
    }

    private fun logSuspiciousUser(authInfo: String) {
        suspiciousUsersLog.appendText("[${System.currentTimeMillis()}] $authInfo\n")
    }

    sealed class ScanResult(val message: String) {
        class Clean(message: String) : ScanResult(message)
        class Failed(message: String) : ScanResult(message)
    }
}
