package lox

import java.io.File
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    when {
        args.size > 1 -> println("Usage: jlox [script]").also { exitProcess(64) }
        args.size == 1 -> runFile(args.single())
        else -> runPrompt()
    }
//    val result = run("""
//        var a = "global a";
//        var b = "global b";
//        var c = "global c";
//        {
//          var a = "outer a";
//          var b = "outer b";
//          {
//            var a = "inner a";
//            print a;
//            print b;
//            print c;
//          }
//          print a;
//          print b;
//          print c;
//        }
//        print a;
//        print b;
//        print c;
//    """.trimIndent())
//
//    val statements = Parser(result.filterIsInstance<Result.Success<Token>>().map { it.value }).parse()
//
//    interpret(statements)
}

fun runPrompt() {
    while (true) {
        print("> ")
        val result = run(readLine()!!)

        val errors = result.filterIsInstance<Result.Error<Token>>()

        if(errors.isEmpty()) {
            val statements = Parser(result.filterIsInstance<Result.Success<Token>>().map { it.value }).parse()

            interpret(statements)
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
    return Scanner().scan(source)
}

sealed class Result<T> {
    data class Success<T>(val value: T): Result<T>()
    data class Error<T>(val line: Int, val where: String = "", val message: String): Result<T>()
}

fun <T> Result.Error<T>.print() = System.err.println("[line $line] Error$where: $message")
