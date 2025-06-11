package com.techno.valley.project2.feature.post.usecase

import com.techno.valley.project2.feature.post.config.StorageProperties
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Service
class FileStorageService(
    private val storageProperties: StorageProperties,
) {
    fun store(file: MultipartFile): String {
        val filename = UUID.randomUUID().toString() + "_" + file.originalFilename
        val targetPath = Paths.get(storageProperties.location).resolve(filename)

        Files.createDirectories(targetPath.parent)
        file.transferTo(targetPath)

        // إرجاع مسار نسبي لاستخدامه لاحقًا في عرض الصور أو الملفات
        return "/uploads/$filename"
    }
}
