package com.techno.valley.project2.feature.post.usecase

import com.techno.valley.project2.feature.post.config.StorageProperties
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Service
class FileStorageService(
    private val storageProperties: StorageProperties,
) {
    fun store(file: MultipartFile): String {
        print("222222222")

        val filename = UUID.randomUUID().toString() + "_" + file.originalFilename
        val targetPath = Paths.get(storageProperties.location).resolve(filename)
        print("33333333333")


        Files.createDirectories(targetPath.parent)
        file.transferTo(targetPath)

        print("444444444444")


        return "/uploads/$filename"
    }

    fun store(file: File): String {
        val filename = UUID.randomUUID().toString() + "_" + file.name
        val targetPath = Paths.get(storageProperties.location).resolve(filename)

        Files.createDirectories(targetPath.parent)
        file.copyTo(targetPath.toFile(), overwrite = true)

        return "/uploads/$filename"
    }

    fun delete(path: String) {
        try {
            val filename = Paths.get(path).fileName.toString()
            val fullPath = Paths.get(storageProperties.location).resolve(filename)
            Files.deleteIfExists(fullPath)
        } catch (e: Exception) {
            println("Failed to delete file: ${e.message}")
        }
    }
}
