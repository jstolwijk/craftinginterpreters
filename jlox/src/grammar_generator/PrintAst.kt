package grammar_generator

import grammar_generator.Expr.*
import grammar_generator.Expr.Literal.*
import lox.Token
import lox.TokenType
import java.math.BigDecimal
import java.math.MathContext


fun Expr.toPrettyString(): String {
    return when(this) {
        is Binary -> "(${operator.lexeme} ${left.toPrettyString()} ${right.toPrettyString()})"
        is Literal -> when(this) {
            is LiteralNumber -> value.stripTrailingZeros().toString()
            is LiteralString -> value
            is True -> "true"
            is False -> "false"
            is Nil -> "nil"
        }
        is Grouping ->  "(group ${expr.toPrettyString()})"
        is Unary -> "(${operator.lexeme} ${right.toPrettyString()})"
    }
}

fun Expr.toReversePolishNotation(): String {
    return when(this) {
        is Binary -> "${left.toReversePolishNotation()} ${right.toReversePolishNotation()} ${operator.lexeme}"
        is Literal -> when(this) {
            is LiteralNumber -> value.stripTrailingZeros().toString()
            is LiteralString -> value
            is True -> "true"
            is False -> "false"
            is Nil -> "nil"
        }
        is Grouping -> expr.toReversePolishNotation()
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

