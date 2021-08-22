package lox

import grammar_generator.Expression
import grammar_generator.Expression.Binary
import lox.TokenType.*
import java.math.BigDecimal


class Parser(private var tokens: List<Token>) {
    private var currentIndex = 0

    fun parse(): Expression? {
        return try {
            expression()
        } catch (error: ParserError) {
            println(error)
            null
        }
    }

    private fun expression(): Expression {
        return equality()
    }

    private fun equality(): Expression {
        var expr: Expression = comparison()
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            val operator: Token = previous()
            val right = comparison()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun comparison(): Expression {
        var expr: Expression = term()

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            val operator: Token = previous()
            val right = term()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun term(): Expression {
        var expr = factor()
        while (match(MINUS, PLUS)) {
            val operator = previous()
            val right = factor()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun factor(): Expression {
        var expr = unary()
        while (match(SLASH, STAR)) {
            val operator = previous()
            val right = unary()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expression {
        return if (match(BANG, MINUS)) {
            val operator = previous()
            val right = unary()
            Expression.Unary(operator, right)
        } else {
            primary()
        }
    }

    private fun primary(): Expression {
        return when {
            match(FALSE) -> Expression.Literal.False
            match(TRUE) -> Expression.Literal.True
            match(NIL) -> Expression.Literal.Nil
            match(NUMBER) -> Expression.Literal.LiteralNumber(previous().literal as? BigDecimal ?: handleError(previous(),"Literal '${previous().literal}' is not a number"))
            match(STRING) -> Expression.Literal.LiteralString(previous().literal as? String ?: handleError(previous(),"Literal '${previous().literal}' is not a string"))
            match(LEFT_PAREN) -> {
                val expr = expression()

                consumeOrError(
                    tokenType = RIGHT_PAREN,
                    errorMessage = "Expect ')' after expression."
                )

                return Expression.Grouping(expr)
            }
            else -> handleError(peek(), "Expected expression")
        }
    }

    private fun consumeOrError(tokenType: TokenType, errorMessage: String): Token {
        if (check(tokenType)) return advance()
        handleError(peek(), errorMessage)
    }

    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    private fun check(type: TokenType): Boolean {
        return if (isAtEnd()) false else peek().type === type
    }

    private fun advance(): Token {
        if (!isAtEnd()) currentIndex++
        return previous()
    }

    private fun isAtEnd(): Boolean {
        return peek().type === EOF
    }

    private fun peek(): Token {
        return tokens[currentIndex]
    }

    private fun previous(): Token {
        return tokens[currentIndex - 1]
    }

    private fun handleError(token: Token, message: String): Nothing {
        throw ParserError(token, message)
    }
}

data class ParserError(
    val token: Token,
    override val message: String
) : RuntimeException(message)