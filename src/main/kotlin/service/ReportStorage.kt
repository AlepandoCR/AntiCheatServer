package dev.alepando.service

import dev.alepando.connection.ConnectionReport
import dev.alepando.model.Classroom
import dev.alepando.model.Machine
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.concurrent.ConcurrentHashMap

@Service
object ReportStorage {

    private val classrooms = ConcurrentHashMap<String, Classroom>()

    fun store(report: ConnectionReport) {
        val classroom = classrooms.computeIfAbsent(report.room) {
            Classroom(it, mutableMapOf())
        }

        val machine = Machine(
            uid = report.uid,
            hostname = report.hostname,
            user = report.user,
            activeConnections = report.activeConnections,
            activeApps = report.activeApps,
            timestamp = report.timestamp
        )

        classroom.machines[report.uid] = machine
    }

    fun getAllClassrooms(): List<Classroom> {
        return classrooms.values.toList()
    }

    fun storeScreenshots(uid: String, screenshots: List<MultipartFile?>) {
        val baseDir = File("D:\\Apps\\antiCheatServer\\screenshots")
        val sessionDir = File(baseDir, uid)

        if (!sessionDir.exists()) {
            val created = sessionDir.mkdirs()
            println("Created new directory: $created")
        }

        screenshots.forEachIndexed { index, file ->
            if (file != null) {
                val outFile = File(sessionDir, "$index.jpg")
                println("Saving file to: ${outFile.absolutePath}")
                try {
                    file.transferTo(outFile)  // Sobrescribe si existe
                } catch (e: Exception) {
                    println("Error saving file ${outFile.name}: ${e.message}")
                }
            }
        }
    }
}
