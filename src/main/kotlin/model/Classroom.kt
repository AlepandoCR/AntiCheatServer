package dev.alepando.model

data class Classroom(
    val name: String,
    val machines: MutableMap<String, Machine>
)

data class Machine(
    val uid: String,
    val hostname: String,
    val user: String,
    val activeConnections: List<String>,
    val activeApps: List<String>,
    val timestamp: Long
)
