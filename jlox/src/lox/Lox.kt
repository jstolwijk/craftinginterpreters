package lox

import grammar_generator.toPrettyString
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

        val errors = result.filterIsInstance<Result.Error<Token>>()

        if(errors.isEmpty()) {
            println(Parser(result.filterIsInstance<Result.Success<Token>>().map { it.value }).parse()?.toPrettyString())
        } else {
            errors.first().print()
        }
    }
}

fun runFile(filePath: String) {
    val result = run(File(filePath).readText())
    result.filterIsInstance<Result.Error<Token>>()
        .first {
            it.print()
            exitProcess(65)
        }
}

fun run(source: String): List<Result<Token>> {
    val tokens = Scanner().scan(source)

    tokens.map {
        println(it)
    }

    return tokens
}

sealed class Result<T> {
    data class Success<T>(val value: T): Result<T>()
    data class Error<T>(val line: Int, val where: String = "", val message: String): Result<T>()
}

fun <T> Result.Error<T>.print() = System.err.println("[line $line] Error$where: $message")
