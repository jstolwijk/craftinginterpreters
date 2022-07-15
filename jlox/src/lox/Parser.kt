package lox

import grammar_generator.Expr
import grammar_generator.Expr.Binary
import grammar_generator.Stmt
import lox.TokenType.*
import java.math.BigDecimal


class Parser(private var tokens: List<Token>) {
    private var currentIndex = 0

    fun parse(): List<Stmt> {
        return mutableListOf<Stmt>().also { statements ->
            try {
                while (!isAtEnd()) {
                    declaration()?.let { statements.add(it) }
                }
            } catch (error: ParserError) {
                println(error)
            }
        }
    }

    private fun declaration(): Stmt? {
        try {
            if(match(VAR)) return varDeclaration()

            return statement()
        } catch (e: ParserError) {
            synchronize()
            System.err.println("ParserError: $e")
            return null
        }
    }

    private fun varDeclaration(): Stmt {
        val identifier = consumeOrError(IDENTIFIER, "Expect variable name.")

        val initializer = if(match(EQUAL)) {
            expression()
        } else {
            null
        }

        return Stmt.Var(identifier, initializer).also {
            consumeOrError(SEMICOLON, "Expect ';' after variable declaration.")
        }
    }

    private fun statement(): Stmt {
        if(match(PRINT)) return printStatement()
        if(match(LEFT_BRACE)) return Stmt.Block(block())

        return expressionStatement()
    }

    private fun block(): List<Stmt> {
        val statements = mutableListOf<Stmt>()

        while(!check(RIGHT_BRACE) && !isAtEnd()) {
            declaration()?.let { statements.add(it) }
        }

        consumeOrError(RIGHT_BRACE, "Expect '}' after block.")
        return statements
    }

    private fun printStatement(): Stmt {
        return Stmt.Print(expression()).also {
            consumeOrError(SEMICOLON, "Expect ';' after value.")
        }
    }

    private fun expressionStatement(): Stmt {
        return Stmt.Expression(expression()).also {
            consumeOrError(SEMICOLON, "Expect ';' after expression.")
        }
    }

    private fun expression(): Expr {
        return assignment()
    }

    private fun assignment(): Expr {
        val expr = equality()

        if(match(EQUAL)) {
            val equals = previous()
            val value = assignment()

            if(expr is Expr.Variable) {
                return Expr.Assign(expr.name, value)
            }

            handleError(equals, "Invalid assignment target.")
        } else {
            return expr
        }
    }

    private fun equality(): Expr {
        var expr: Expr = comparison()
        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            val operator: Token = previous()
            val right = comparison()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun comparison(): Expr {
        var expr: Expr = term()

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            val operator: Token = previous()
            val right = term()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun term(): Expr {
        var expr = factor()
        while (match(MINUS, PLUS)) {
            val operator = previous()
            val right = factor()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun factor(): Expr {
        var expr = unary()
        while (match(SLASH, STAR)) {
            val operator = previous()
            val right = unary()
            expr = Binary(expr, operator, right)
        }
        return expr
    }

    private fun unary(): Expr {
        return if (match(BANG, MINUS)) {
            val operator = previous()
            val right = unary()
            Expr.Unary(operator, right)
        } else {
            primary()
        }
    }

    private fun primary(): Expr {
        return when {
            match(FALSE) -> Expr.Literal.False
            match(TRUE) -> Expr.Literal.True
            match(NIL) -> Expr.Literal.Nil
            match(NUMBER) -> Expr.Literal.LiteralNumber(previous().literal as? BigDecimal ?: handleError(previous(),"Literal '${previous().literal}' is not a number"))
            match(STRING) -> Expr.Literal.LiteralString(previous().literal as? String ?: handleError(previous(),"Literal '${previous().literal}' is not a string"))
            match(LEFT_PAREN) -> {
                val expr = expression()

                consumeOrError(
                    tokenType = RIGHT_PAREN,
                    errorMessage = "Expect ')' after expression."
                )

                return Expr.Grouping(expr)
            }
            match(IDENTIFIER) -> Expr.Variable(previous())
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

    private fun synchronize() {
        advance()
        while (!isAtEnd()) {
            if (previous().type === SEMICOLON) return
            when (peek().type) {
                CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN -> return
                else -> advance()
            }
        }
    }
}

data class ParserError(
    val token: Token,
    override val message: String
) : RuntimeException(message)