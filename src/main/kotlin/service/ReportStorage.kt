package dev.alepando.service

import dev.alepando.connection.ConnectionReport
import dev.alepando.model.Classroom
import dev.alepando.model.Machine
import org.springframework.stereotype.Service
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
}
