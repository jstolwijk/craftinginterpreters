package grammar_generator

import java.io.PrintWriter
import kotlin.system.exitProcess


fun main(args: Array<String>) {
    if(args.size != 1) {
        System.err.println("Usage: generate_ast <output directory>");
        exitProcess(1);
    }

    val (outputDir) = args

    defineAst(outputDir, "Expr", listOf(
        "Binary   : Expr left, Token operator, Expr right",
        "Grouping : Expr expression",
        "Literal  : Object value",
        "Unary    : Token operator, Expr right"
    ))
}

fun defineAst(outputDir: String, baseName: String, typeS: List<String>) {
    val path = "$outputDir/$baseName.java"
    val writer = PrintWriter(path, "UTF-8")

    writer.println("package lox;")
    writer.println()
    writer.println("import java.util.List;")
    writer.println()
    writer.println("abstract class $baseName {")

    writer.println("}")
    writer.close()
}