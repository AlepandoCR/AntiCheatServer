package dev.alepando

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AntiCheatServerApplication

fun main(args: Array<String>) {
    runApplication<AntiCheatServerApplication>(*args)
}

// este es el server con spring, solo recibe los datos y los deja para el front end