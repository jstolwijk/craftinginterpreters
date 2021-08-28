package lox

import grammar_generator.Expr
import grammar_generator.Stmt
import java.lang.RuntimeException
import java.math.BigDecimal
import java.math.MathContext

fun interpret(statements: List<Stmt>) {
    try {
        statements.forEach {
            it.interpret()
        }
    } catch (e: RuntimeException) {
        println("Error: ${e.message}")
    }
}

private fun Stmt.interpret() {
    when(this) {
        is Stmt.Expression -> expr.interpret()
        is Stmt.Print -> println(expr.interpret().toPrettyString())
    }
}

fun Any?.toPrettyString(): String = when(this) {
    is BigDecimal -> toString()
    is String -> "\"$this\""
    is Boolean -> toString()
    null -> "nil"
    else -> ""
}

private fun Expr.interpret() : Any? {
    return when(this) {
        is Expr.Binary -> {
            val left = left.interpret()
            val right = right.interpret()
            return when {
                operator.type == TokenType.GREATER -> left as BigDecimal > right as BigDecimal
                operator.type == TokenType.GREATER_EQUAL -> left as BigDecimal >= right as BigDecimal
                operator.type == TokenType.LESS -> (left as BigDecimal) < (right as BigDecimal)
                operator.type == TokenType.LESS_EQUAL -> left as BigDecimal <= right as BigDecimal
                operator.type == TokenType.MINUS -> left as BigDecimal - right as BigDecimal
                operator.type == TokenType.PLUS && left is BigDecimal && right is BigDecimal -> left + right
                operator.type == TokenType.PLUS && left is String && right is String -> left + right
                operator.type == TokenType.SLASH -> (left as BigDecimal).divide(right as BigDecimal, MathContext.DECIMAL128)
                operator.type == TokenType.STAR -> (left as BigDecimal).multiply(right as BigDecimal, MathContext.DECIMAL128)
                operator.type == TokenType.EQUAL_EQUAL -> isEqual(left, right)
                operator.type == TokenType.BANG_EQUAL -> !isEqual(left, right)
                else -> null
            }
        }
        is Expr.Literal -> when(this) {
            is Expr.Literal.LiteralNumber -> value
            is Expr.Literal.LiteralString -> value
            is Expr.Literal.True -> true
            is Expr.Literal.False -> false
            is Expr.Literal.Nil -> null
        }
        is Expr.Grouping -> expr.interpret()
        is Expr.Unary -> when(operator.type) {
            TokenType.BANG -> !isTruthy(right.interpret())
            TokenType.MINUS -> -(right.interpret() as BigDecimal)
            else -> null
        }
    }
}

private fun isEqual(a: Any?, b: Any?): Boolean {
    if (a == null && b == null) return true
    return if (a == null) false else a == b
}
private fun isTruthy(`object`: Any?): Boolean {
    if (`object` == null) return false
    return if (`object` is Boolean) `object` else true
}
