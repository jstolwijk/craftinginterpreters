package lox

import lox.TokenType.*

class Scanner {
    private var start = 0
    private var current = 0
    private var line = 1
    private var length = 0

    private val isAtEnd: Boolean
        get() = current >= length

    private val keywords = mapOf(
        "and" to AND,
        "class" to CLASS,
        "else" to ELSE,
        "false" to FALSE,
        "for" to FOR,
        "fun" to FUN,
        "if" to IF,
        "nil" to NIL,
        "or" to OR,
        "lox.print" to PRINT,
        "return" to RETURN,
        "super" to SUPER,
        "this" to THIS,
        "true" to TRUE,
        "var" to VAR,
        "while" to WHILE
    )

    fun scan(source: String): List<Result<Token>> {
        length = source.length

        val tokens = mutableListOf<Result<Token>?>()

        while (!isAtEnd) {
            start = current
            advance()
            tokens += source.scanToken()
        }

        return tokens.filterNotNull() + Result.Success(Token(EOF, "", "", line))
    }

    private fun String.scanToken(): Result<Token>? {
        val char = get(current - 1)
        val (tokenType, literal) = when (char) {
            '(' -> LEFT_PAREN to null
            ')' -> RIGHT_PAREN to null
            '{' -> LEFT_BRACE to null
            '}' -> RIGHT_BRACE to null
            ',' -> COMMA to null
            '.' -> DOT to null
            '-' -> MINUS to null
            '+' -> PLUS to null
            ';' -> SEMICOLON to null
            '*' -> STAR to null
            '!' -> if (nextCharIs('=')) BANG_EQUAL to null else BANG to null
            '=' -> if (nextCharIs('=')) EQUAL_EQUAL to null else EQUAL to null
            '<' -> if (nextCharIs('=')) LESS_EQUAL to null else LESS to null
            '>' -> if (nextCharIs('=')) GREATER_EQUAL to null else GREATER to null
            '/' -> when {
                nextCharIs('/') -> {
                    takeUnless { it == '\n' || isAtEnd }
                    null to null
                }
                nextCharIs('*') -> {
                    var nesting = 1
                    while(nesting > 0) {
                        when {
                            isAtEnd -> return Result.Error(
                                line = line,
                                where = "",
                                message = "Unterminated comment block."
                            )
                            peek() == '/' && peekNext() == '*' -> {
                                advance()
                                advance()
                                nesting ++
                            }
                            peek() == '*' && peekNext() == '/' -> {
                                advance()
                                advance()
                                nesting --
                            }
                            else -> advance()
                        }
                    }
                    null to null
                }
                else -> {
                    SLASH to null
                }
            }
            ' ', '\r', '\t' -> null to null
            '\n' -> (null to null).also { line++ }
            '"' -> {
                val string = getString() ?: return Result.Error(
                    line = line,
                    where = "",
                    message = "Unexpected character."
                )

                STRING to string
            }
            else -> when {
                char.isDigit -> {
                    NUMBER to getNumber()
                }
                char.isAlpha -> {
                    getIdentifierTokenType() to null
                }
                else -> {
                    return Result.Error(
                        line = line,
                        where = "",
                        message = "Unterminated string."
                    )
                }
            }
        }

        return tokenType?.let {
            Result.Success(
                Token(
                    type = it,
                    lexeme = substring(start, current),
                    literal = literal ?: "",
                    line = line
                )
            )
        }
    }

    private val Char.isAlphaNumeric
        get() = isAlpha || isDigit

    private val Char.isAlpha
        get() = this in ('a'..'z') || this in ('A'..'Z') || this == '_'

    private val Char.isDigit
        get() = this in ('0'..'9')

    private fun String.getIdentifierTokenType(): TokenType {
        takeWhile { it.isAlphaNumeric }

        return keywords[substring(start, current)] ?: IDENTIFIER
    }

    private fun String.getNumber(): Double {
        takeWhile { it.isDigit }

        if (peek() == '.' && peekNext().isDigit) {
            advance()
            takeWhile { it.isDigit }
        }
        return substring(start, current).toDouble()
    }

    private fun String.getString(): String? {
        takeWhile { it != '"' && !isAtEnd }

        if (isAtEnd) {
            return null
        }

        advance()

        return substring(start + 1, current - 1)
    }

    private fun String.peek(): Char {
        return if (isAtEnd) '\u0000' else get(current)
    }

    private fun String.peekNext(): Char {
        return if (current + 1 >= length) '\u0000' else get(current + 1)
    }

    private fun String.nextCharIs(char: Char): Boolean {
        if (isAtEnd) return false
        if (get(current) != char) return false

        advance()
        return true
    }

    private fun String.takeUnless(predicate: (Char) -> Boolean) {
        takeWhile { !predicate(it) }
    }

    private fun String.takeWhile(predicate: (Char) -> Boolean) {
        while (predicate(peek())) {
            if(peek() == '\n') line ++
            advance()
        }
    }

    private fun advance() = run { current++ }
}

