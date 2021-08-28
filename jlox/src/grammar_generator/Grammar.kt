package grammar_generator

import lox.Token
import java.math.BigDecimal

sealed class Stmt {
    data class Expression(val expr: Expr): Stmt()
    data class Print(val expr: Expr) : Stmt()
}

sealed class Expr {
    sealed class Literal : Expr() {
        data class LiteralNumber(val value: BigDecimal) : Literal()
        data class LiteralString(val value: String) : Literal()
        object True : Literal()
        object False : Literal()
        object Nil : Literal()
    }

    data class Unary(val operator: Token, val right: Expr) : Expr()

    data class Binary(
        val left: Expr,
        val operator: Token,
        val right: Expr
    ) : Expr()

    data class Grouping(val expr: Expr) : Expr()
}