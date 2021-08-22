## Chapter 6

Subject: Writing a parser

### Ambiguity

Precedence, determine which operator is evaluated first. Operators with higher precedence are evaluated before operators with lower precedence

Associativity, determines which operator is computed first when having a sequence of the same operator.

Left and right associative:
minus left: `5-3-1` is equivalent to `(5-3)-1`
assignment right: `a = b = c` is equivalent to `a = (b = c)`

| Category   | Operators         | Associates |
|------------|-------------------|------------|
| Equality   | `==` `!=`         | Left       |
| Comparison | `>` `>=` `<` `<=` | Left       |
| Term       | `-` `+`           | Left       |
| Factor     | `/` `*`           | Left       |
| Unary      | `!` `-`           | Right      |

The items in the table above are ordered in ascending precedence order (from low to high) and from `top` to `bottom` (see Recursive descent top down parsing)

Grammar
```
expression → equality
equality → comparison ( ( "!=" | "==" ) comparison )*
comparison → term ( ( ">" | ">=" | "<" | "<=" ) term )*
term → factor ( ( "-" | "+" ) factor )*
factor → unary ( ( "/" | "*" ) unary )*
unary → ( "!" | "-" ) unary | primary
primary → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")"
```

### Recursive descent

The first parser will be a Recursive descent parser. Recursive descent is considered a top-down parser because it starts from the top (expression in our case) and works its way down into the nested sub expressions

Left recursion is problematic for recursive descent
