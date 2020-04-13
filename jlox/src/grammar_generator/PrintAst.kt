package grammar_generator

import grammar_generator.Expression.*
import grammar_generator.Expression.Literal.*

fun Expression.toPrettyString(): String {
    return when(this) {
        is Binary -> parenthesize(operator.lexeme, left, right)
        is Literal -> when(this) {
            is LiteralNumber -> value.stripTrailingZeros().toString()
            is LiteralString -> value
            is True -> "true"
            is False -> "false"
            is Nil -> "nil"
        }
        is Grouping ->  parenthesize("group", expression)
        is Unary -> parenthesize(operator.lexeme, right)
    }
}

private fun parenthesize(name: String, vararg expressions: Expression): String {
    val items = listOf(name) + expressions.map { it.toPrettyString() }
    return items.joinToString(prefix = "(", postfix = ")", separator = " ")
}

// TEST FUNCTION
//fun main() {
//    println(
//        Expression.Binary(
//            left = Expression.Unary(
//                operator = Token(TokenType.MINUS, "-", null, 1),
//                right = Expression.Literal.LiteralNumber(BigDecimal("123.00"))
//            ),
//            operator = Token(TokenType.STAR, "*", null, 1),
//            right = Expression.Grouping(Expression.Literal.LiteralNumber(BigDecimal("45.67")))
//        ).toPrettyString()
//    )
//}

