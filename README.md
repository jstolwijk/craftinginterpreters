# Crafting Interpreters
This repository contains my solutions of the exercises of the [Crafting Interpreters](https://craftinginterpreters.com/) book.

## Complementary material:
* [Chomsky hierarchy](https://en.wikipedia.org/wiki/Chomsky_hierarchy)
* [Compilers: Principles, Techniques, and Tools (the Dragon Book)](https://www.amazon.com/Compilers-Principles-Techniques-Tools-2nd/dp/0321486811)

## Reading list
* [A Lisp/Clojure book](https://en.wikipedia.org/wiki/Clojure)


## Challenges
### Chapter 2

1. Pick an open source implementation of a language you like. Download the source code and poke around in it. Try to find the code that implements the scanner and parser. Are they hand-written, or generated using tools like Lex and Yacc? (.l or .y files usually imply the latter.)


Language: [Typescript](https://github.com/microsoft/TypeScript/tree/master/src/compiler)

Scanner: https://github.com/microsoft/TypeScript/blob/master/src/compiler/scanner.ts

Parser: https://github.com/microsoft/TypeScript/blob/master/src/compiler/parser.ts

The scanner and parser are handwritten in typescript.

2. Just-in-time compilation tends to be the fastest way to implement a dynamically-typed language, but not all of them use it. What reasons are there to not JIT?

The type is unknown at compile time, the compiler has to know the type to be able to optimize the generated code.

3. Most Lisp implementations that compile to C also contain an interpreter that lets them execute Lisp code on the fly as well. Why?

Lisp allows you to [evaluate code at compile time](https://www.gnu.org/software/emacs/manual/html_node/elisp/Eval-During-Compile.html). 

### Chapter 5
#### 1.

#### 2. Visitor pattern in functional languages
My current implementation in kotlin is using pattern matching to walk over visit the expression object's children. I think something similar can be done in haskell.

#### 3. Reverse polish notation
```
fun main() {
    println(
        Binary(
            left = Grouping(Binary(
                left = LiteralNumber(BigDecimal("1")),
                operator = Token(TokenType.PLUS, "+", null, 1),
                right = LiteralNumber(BigDecimal("2"))
            )),
            operator = Token(TokenType.STAR, "*", null, 1),
            right = Grouping(Binary(
                left = LiteralNumber(BigDecimal("4")),
                operator = Token(TokenType.MINUS, "-", null, 1),
                right = LiteralNumber(BigDecimal("3"))
            ))
        ).toReversePolishNotation()
    )
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
```
