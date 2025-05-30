package dev.alepando.controller

import dev.alepando.connection.ConnectionReport
import dev.alepando.model.Classroom
import dev.alepando.service.ReportStorage
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
object AntiCheatController {

    @PostMapping("/report")
    fun receiveReport(@RequestBody report: ConnectionReport) {
        ReportStorage.store(report)
    }

    @GetMapping("/status")
    fun getStatus(): List<Classroom> {
        return ReportStorage.getAllClassrooms()
    }
}
