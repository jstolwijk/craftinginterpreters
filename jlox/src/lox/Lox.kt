package lox

import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    when {
        args.size > 1 -> println("Usage: jlox [script]").also { exitProcess(64) }
        args.size == 1 -> runFile(args.single())
        else -> runPrompt()
    }
}

fun runPrompt() {
    while (true) {
        print("> ")
        val result = run(readLine()!!)

        if(result is Result.Error) {
            result.print()
        }
    }
}

fun runFile(filePath: String) {
    val result = run(File(filePath).readText())

    if(result is Result.Error) {
        result.print()
        exitProcess(65)
    }
}

fun run(source: String): Result<String> {
    val tokens = Scanner().scan(source)

    tokens.map {
        println(it)
    }

    return Result.Success("ok")
}


sealed class Result<T> {
    data class Success<T>(val value: T): Result<T>()
    data class Error<T>(val line: Int, val where: String = "", val message: String): Result<T>()
}

fun <T> Result.Error<T>.print() = System.err.println("[line $line] Error$where: $message")
