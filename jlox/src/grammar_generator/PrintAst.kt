package grammar_generator

import grammar_generator.Expression.*
import grammar_generator.Expression.Literal.*
import lox.Token
import lox.TokenType
import java.math.BigDecimal
import java.math.MathContext


fun Expression.interpret(): Any? {
    return when(this) {
        is Binary -> {
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
        is Literal -> when(this) {
            is LiteralNumber -> value
            is LiteralString -> value
            is True -> true
            is False -> false
            is Nil -> null
        }
        is Grouping -> expression.interpret()
        is Unary -> when(operator.type) {
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

fun Expression.toPrettyString(): String {
    return when(this) {
        is Binary -> "(${operator.lexeme} ${left.toPrettyString()} ${right.toPrettyString()})"
        is Literal -> when(this) {
            is LiteralNumber -> value.stripTrailingZeros().toString()
            is LiteralString -> value
            is True -> "true"
            is False -> "false"
            is Nil -> "nil"
        }
        is Grouping ->  "(group ${expression.toPrettyString()})"
        is Unary -> "(${operator.lexeme} ${right.toPrettyString()})"
    }
}

fun Expression.toReversePolishNotation(): String {
    return when(this) {
        is Binary -> "${left.toReversePolishNotation()} ${right.toReversePolishNotation()} ${operator.lexeme}"
        is Literal -> when(this) {
            is LiteralNumber -> value.stripTrailingZeros().toString()
            is LiteralString -> value
            is True -> "true"
            is False -> "false"
            is Nil -> "nil"
        }
        is Grouping -> expression.toReversePolishNotation()
        is Unary -> "${right.toReversePolishNotation()} ${operator.lexeme}"
    }
}

// TEST FUNCTION
fun main() {
    println(
        Binary(
            left = Unary(
                operator = Token(TokenType.MINUS, "-", null, 1),
                right = LiteralNumber(BigDecimal("123.00"))
            ),
            operator = Token(TokenType.STAR, "*", null, 1),
            right = Grouping(LiteralNumber(BigDecimal("45.67")))
        ).toPrettyString()
    )
}

