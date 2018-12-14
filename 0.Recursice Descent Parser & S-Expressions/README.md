# Assignment 0

## Part 1

For the first part of this homework you should implement a simple calculator. The calculator should accept expressions with addition, subtraction, multiplication, and division operators, as well as parentheses. The grammar (for single-digit numbers) is summarized in:

    exp -> num | exp op exp | (exp)

    op -> + | - | * | /

    num -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9

You need to change this grammar to support priority between addition and multiplication, to remove the left recursion for LL parsing, etc.

This part of the homework is divided in two parts:

1.  You have to write (and provide in a .txt of .pdf file) the FIRST+ & FOLLOW sets for the LL(1) version of the above grammar. In the end you will summarize them in a single lookahead table (include a row for every derivation in your final grammar).
    
2.  You have to write a recursive descent parser in Java that reads expressions and computes the values or prints "parse error" if there is a syntax error. You don't need to identify blank space or multi-digit number. You can read the symbols one by one (as in the C getchar() function). The expression must end with a newline or EOF.
## Part 2

In the second part of this homework you will implement a parser and translator for a language supporting string operations. The language supports the concatanation operator over strings, function definitions and calls, conditionals (if-else i.e, every if must be followed by an else), and the following logical expressions:

-   string equality (string1 = string2): Whether string1 is equal to string2.
-   is-substring-of (string1 in string2): Whether string1 is a substring of/is contained in string2.

All values in the language are strings.

Your parser, based on a context-free grammar, will translate the input language into S-expressions, i.e., fully parenthesized expressions with the operator in the first position. You will use JavaCUP for the generation of the parser combined either with a hand-written lexer or a generated-one (e.g., using JFlex, which is encouraged).

You will infer the desired syntax of the input and output languages from the examples below. The output language is a subset of Scheme so it can be interpreted by mit-scheme or online scheme interpreters like [this](http://repl.it/languages/Scheme), if you want to test your output.

There is no need to perform type checking for the argument types or a check for the number of function arguments. You can assume that the program input will always be semantically correct.

Note that extra parentheses are fine in the input syntax but not in the S-expression language. For instance "x" is not the same as "(x)" in the S-expression language: the latter means a call to function "x".

### Example #1

**Input:**

    name()  {
        "John"
    }
    
    name()
    
    surname() {
        "Doe"
    }
    
    fullname(first_name, sep, last_name) {
        first_name + sep + last_name
    }
    
    surname()
    fullname(name(), " ", surname())

**Output (S-expressions):**

    (define (name) "John")      // function name is defined. This is a comment and not part of the output
    (name)                      // function name is called
    
    (define (surname) "Doe")
    (define (fullname first_name sep last_name)
        (string-append (string-append first_name sep) last_name))
    
    (surname)
    (fullname (name) " " (surname))

### Example #2

**Input:**

    name() {
        "John"
    }
    
    repeat(x) {
        x + x
    }
    
    cond_repeat(c, x) {
        if (c = "yes")
            repeat(x)
        else
            x
    }
    
    cond_repeat("yes", name())
    cond_repeat("no", "Jane")

**Output (S-expressions):**

    (define (name) "John")
    (define (repeat x) (string-append x x))
    
    (define (cond_repeat c x)
        (if (equal? c "yes")
            (repeat x)
            x))
    
    (cond_repeat "yes" (name))
    (cond_repeat "no" "Jane")

### Example #3

**Input:**

    may_be_gerund(x) {
        if ("ing" in x)
            "yes"
        else
            "no"
    }
    
    repeat(x) {
        x + x
    }
    
    cond_repeat_gerund(c, x) {
        if (c = "yes")
            if (may_be_gerund(x) = "yes")
                repeat(x)
            else
                x
        else
            x
    
    }
    
    cond_repeat_gerund("yes", "running")
    cond_repeat_gerund("yes", "run")
    cond_repeat_gerund("no", "running")

**Output (S-expressions):**

    (define (may_be_gerund x)
        (if (substring? "ing" x)
            "yes"
            "no"))
    
    (define (repeat x) (string-append x x))
    
    (define (cond_repeat_gerund c x)
        (if (equal? c "yes")
            (if (equal? (may_be_gerund x) "yes")
                (repeat x)
                x)
            x))
    
    (cond_repeat_gerund "yes" "running")
    (cond_repeat_gerund "yes" "run")
    (cond_repeat_gerund "no" "running")

You can use the following (inefficient but elegant) definition of _substring?_ to test the s-expression code generated for the _in_ conditional operator:

    (define (substring? string1 string2)
        (let ((len1 (string-length string1))
              (len2 (string-length string2)))
          (cond ((> len1 len2) #f)
                ((string=? string1 (substring string2 0 len1)) #t)
                (else (substring? string1 (substring string2 1 len2))))))

