package grammar_generator

import lox.Token
import java.math.BigDecimal

sealed class Expression {
    sealed class Literal : Expression() {
        data class LiteralNumber(val value: BigDecimal) : Literal()
        data class LiteralString(val value: String) : Literal()
        object True : Literal()
        object False : Literal()
        object Nil : Literal()
    }

    data class Unary(val operator: Token, val right: Expression) : Expression()

    data class Binary(
        val left: Expression,
        val operator: Token,
        val right: Expression
    ) : Expression()

    data class Grouping(val expression: Expression) : Expression()
}