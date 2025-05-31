package dev.alepando.controller

import dev.alepando.connection.ConnectionReport
import dev.alepando.model.Classroom
import dev.alepando.service.ReportStorage
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.UUID

@RestController
@RequestMapping("/api")
object AntiCheatController {

    private val baseDir = File("D:\\Apps\\antiCheatServer\\screenshots")
    private val tempDir = File(baseDir, "temp")

    init {
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
    }

    @PostMapping("/report", consumes = ["multipart/form-data"])
    fun receiveReport(
        @RequestPart("report") report: ConnectionReport,
        @RequestPart("screenshot0", required = false) screenshot0: MultipartFile?,
        @RequestPart("screenshot1", required = false) screenshot1: MultipartFile?,
        @RequestPart("screenshot2", required = false) screenshot2: MultipartFile?
    ) {
        ReportStorage.store(report)
        ReportStorage.storeScreenshots(report.uid, listOf(screenshot0, screenshot1, screenshot2))
    }

    @GetMapping("/status")
    fun getStatus(): List<Classroom> {
        return ReportStorage.getAllClassrooms()
    }

    @GetMapping("/screenshot/{uid}/{index}")
    fun getScreenshot(
        @PathVariable uid: String,
        @PathVariable index: Int,
        @RequestParam(value = "refresh", required = false) refresh: Boolean?,
        response: HttpServletResponse
    ) {
        cleanOldTempFiles(uid)

        val originalFile = File(baseDir, "$uid/$index.jpg")
        if (!originalFile.exists()) {
            response.status = 404
            return
        }

        val tempFolder = File(tempDir, uid)
        if (!tempFolder.exists()) tempFolder.mkdirs()

        val tempFile = File(tempFolder, "$index-${UUID.randomUUID()}.jpg")

        if (refresh == true || !tempFile.exists()) {
            try {
                Files.copy(originalFile.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
            } catch (e: Exception) {
                response.status = 500
                return
            }
        } else {
            val files = tempFolder.listFiles { f -> f.name.startsWith("$index-") }
            if (files != null && files.isNotEmpty()) {
                val latest = files.maxByOrNull { it.lastModified() }!!
                serveFile(latest, response)
                return
            }
        }

        serveFile(tempFile, response)
    }

    private fun serveFile(file: File, response: HttpServletResponse) {
        try {
            response.contentType = "image/jpeg"
            file.inputStream().use { input ->
                response.outputStream.use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            response.status = 500
        }
    }

    private fun cleanOldTempFiles(uid: String, maxAgeMinutes: Long = 5) { // borra fotos que no se modificaron por 5 min
        val tempFolder = File(tempDir, uid)
        if (!tempFolder.exists()) return

        val now = System.currentTimeMillis()
        tempFolder.listFiles()?.forEach { file ->
            if (now - file.lastModified() > maxAgeMinutes * 60 /* segundos por minuto */ * 1000 /* milis a sg */) {
                file.delete()
            }
        }
    }

}
