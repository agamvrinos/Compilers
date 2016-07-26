# Compilers
2016 Compilers projects

Course Project

Design and implementation of a compiler for the MiniJava language (a small subset of Java)

To implement the compiler you will use the tools JavaCC and JTB

The implementation for phases 2 and 3 of the project will be done in Java utilizing the visitor pattern
Part 	Description 	Deadline
1 	Implementation of a LL(1) parser for a simple calculatorand a translator to S-expressions for a language for string operations 	24/03/2016
2 	Semantic Check (MiniJava) 	17/04/2016
3 	Generating intermediate code (MiniJava -> Spiglet) 	15/05/2016
4 	Static analysis and optimizations (Spiglet (-> Spiglet)) 	05/06/2016
Homework 4 - Static Analysis and Optimizations

In your final project, you will implement static program analysis algorithms over the Spiglet intermediate language program. The analysis will compute all the information needed to perform optimizations, but you do not need to actually optimize the Spiglet code.

Specifically, you will implement 5 analyses:

    Live range computation: which variables are "live" at which instruction.

    Dead code computation: which assignment statements are dead code, because they assign to a non-live variable. This is a straightforward client of the previous analysis.

    Constant propagation: at a certain instruction, which program variables are guaranteed to have a constant value, and what is that constant.

    Copy propagation: at a certain instruction, which program variables are guaranteed to hold identical values.

    Basic block computation: which instructions belong in the same basic block, or, equivalently, which instructions form a beginning of a basic block and for each instruction which is the next one in the same basic block (unless the instruction is the last one).

You do not have to implement your own data-flow framework to express these analyses. Instead, you will use an implementation of the Datalog language called IRIS. This is probably the easiest way to express general fixpoint algorithms without having to worry about the details of their evaluation. You can see the IRIS Datalog syntax here. You can also use the IRIS online demo and test your Datalog programs there.

That is, your visitors over the Spiglet code will emit tuples for the following relations:

    instruction(method_name, i_counter, instruction): method_name is the name of the method the instruction belongs to, i_counter is the sequence number of the instruction in the method and instruction is the string representation of the instruction.

For instance:

instruction("A_foo", 1, "MOVE TEMP 2 HALLOCATE 12").
instruction("A_foo", 2, "MOVE TEMP 3 TEMP 2").
instruction("A_foo", 3, "MOVE TEMP 4 10").

    var(method_name, variable): method_name is the name of the method the variable belongs to and variable is the string representation of a local variable (a Spiglet "TEMP x").

For instance:

var("A_foo", "TEMP 2").
var("A_foo", "TEMP 3").
var("A_foo", "TEMP 4").

    next(method_name, i_counter, j_counter): instruction with sequence number i_counter has instruction with sequence number j_counter as a possible next. If i_counter corresponds to a CJUMP instruction, it will have multiple next instructions.

For instance:

next("A_foo", 1, 2).

    varMove(method_name, i_counter, variable1, variable2): instruction with sequence number i_counter is a MOVE from temporary variable variable2 to temporary variable variable1 in method method_name.

For instance:

varMove("A_foo", 2, "TEMP 3", "TEMP 2").

    constMove(method_name, i_counter, variable, constant): instruction with sequence number i_counter is a MOVE of constant value (integer or label) constant to variable variable in method method_name.

For instance:

constMove("A_foo", 3, "TEMP 4", 10).

    varUse(method_name, i_counter, variable): instruction with sequence number i_counter uses variable variable in method method_name. There may be multiple instructions that produce uses of a variable variable. E.g., MOVE, CALL, CJUMP, HSTORE, HLOAD, etc. We group them all together since they don't need to be distinguished for the specific analyses you are implementing.

For instance:

varUse("A_foo", 2, "TEMP 2").

    varDef(method_name, i_counter, variable): instruction with sequence number i_counter defines (i.e., assigns) variable variable in method method_name. There are, again, multiple instructions that assign to a variable variable: E.g., HLOAD, MOVE, etc.

For instance:

varDef("A_foo", 1, "TEMP 2").
varDef("A_foo", 2, "TEMP 3").
varDef("A_foo", 3, "TEMP 4").

If you see a need, you can modify the aforementioned schema in order to express additional information regarding labels e.g., you can add additional fields to existing relations or introduce new relations to the schema. But every such change should be well-justified. The current schema should be sufficient for all your analyses.

The above relations are the input to the Datalog code that computes your analyses. The Datalog code should be just a few rules per analysis, but you need to think about them carefully to ensure they are correct.

Example Datalog computations:

cJumpInstruction(?m, ?i) :- next(?m, ?i, ?j),
                            next(?m, ?i, ?k),
                            ?i+1 = ?k,
                            ?j != ?k.

I.e., an instruction is a (non-trivial) CJUMP iff its possible next instructions are its immediate next in program order and a different instruction.

jumpInstruction(?m, ?i) :- next(?m, ?i, ?j),
                           ?i+1 = ?k,
                           ?j != ?k,
                           !next(?m, ?i, ?k).

I.e., an instruction is a (non-trivial) JUMP iff it has a next instruction that is not its immediate next in program order. (Note that this elides trivial jumps, which jump to the immediately next instruction. These are not real jumps for program analysis purposes.)

Bonus part (20% extra credit): As a bonus part to this project you can apply the optimizations identified by your analyses to actual Spiglet code. That is, given an input Spiglet file, your program should apply the necessary transformations and produce an optimized Spiglet file based on the optimization opportunities you discovered.

You will receive more examples of Datalog analyses in lecture and lab.
Homework 3 – MiniJava Generating intermediate code

In this part of the project you have to write visitors that convert MiniJava code into an intermediate language which we are going to call Spiglet. You can see most of the elements of this language by reading its grammar (BNF form, JavaCC form). Below is some explanation and elaboration:

    Methods are defined as "function_name [arguments]" and they take as arguments the temporary variables "TEMP 0", "TEMP 1" etc. Identifiers "TEMP x" are used thereafter as temporary variables, temporary values, for expression calculations, etc.
    The expression "CALL simpleExpression (temp_1 temp_2 ... temp_n)" calls the function located at the memory location equal to the value of simpleExpression and is passed as arguments the values of temp_1, temp_2, etc. Each function call is dynamic so the location of the memory function is known only at runtime. The result of the CALL expression is the value returned by the function.
    The expression "HALLOCATE simpleExpression" takes as argument an integer value and allocates dynamically as many memory slots as this value. Typically the value will be a multiple of 4, since both addresses and integers are 4 bytes. Each time you allocate an object, you should also allocate space for its virtual table. (Or, alternatively, you should allocate one global virtual table per class, using labels. But this is not what the examples do.),
    The command "MOVE temp expression" stores the value of the expression “expression” to a temporary variable temp (a "TEMP x").
    The command "HSTORE temp_1 integer_literal temp_2" stores the value of temp_2 in a memory location with base address temp_1 and offset integer_literal. This can be used both for array element assignments and object field assignments.
    The command "HLOAD temp_1 temp_2 integer_literal" reads the value of the memory location with base address temp_2 and offset integer_literal to the temporary variable temp_1. This will be used not only to read data from arrays and class member fields but also to call methods by reading their address from the virtual table.
    The command "JUMP label" makes a jump to label. Labels are practically memory locations and we consider them to be integer expressions with their value being the memory location to which they refer.
    The command "CJUMP temp label" moves forward to the next command (branch not taken) only if the value of temp is exactly equal to 1, otherwise it makes a jump to label (branch taken).
    The expression "LT temp simpleExpression" checks whether temp is less than simpleExpression, and similarly for PLUS, MINUS, TIMES, which execute addition, subtraction, and multiplication. To check whether an object is null you can use the expression "LT temp 1".
    The "ERROR" command terminates the program with an error. One possible reason for this to happen is if the program tries to access an array out-of-bounds.
    The "NOOP" command does nothing and can be used as filler wherever a command is expected but no action needs to be performed.

If you do not remember or haven't seen how a virtual table (v-table) is constructed, essentially it forms a table structure pointed by the first 4 bytes of an object and defines an offset for each function. Consider a function foo in position 0 and bar in position 1 of the table (with actual offset 4). If a method is overridden, the overriding version is inserted in the same location of the virtual table as the overridden version. Virtual calls are implemented by finding the address of the function to call through the virtual table. If we wanted to depict this in C, imagine that object obj is located at location x and we are calling foo that is in the 3rd position of the v-table. The function that is going to be called is in memory location x[0][12].

Many things that this description may lack can be found by reading the grammar of Spiglet. It is small enough and understandable. You can also understand the form of Spiglet by examining the examples given here (corresponding to the earlier MiniJava examples from HW2). You can use this program in order to "pretty-print" the Spiglet code you produce, for readability(*). To ensure that your output is legal Spiglet code, you must use this parser by typing "java -jar spp.jar < [Input-program]". If the program is syntactically legal Spiglet, you will receive the message "Program parsed successfully".

Additionally you can use this interpreter to "run" your Spiglet code and see if it produces correct results. (The interpreter is more permissive than the parser: it misses several restrictions of Spiglet.) The correct results can be seen by comparing the output that javac and java produce with the output you get from executing this Spiglet interpreter on your output code.

Your program should run as follows: java [MainClassName] [file1.java] [file2.java] ... [fileN.java]

That is, your program must compile to Spiglet all .java files given as arguments. Moreover, the outputs must be stored in files named file1.spg, file2.spg, ... fileN.spg respectively.

(*) run: java -jar pretty-printer.jar [file1.spg] [file2.spg] ... [fileN.spg] This command stores the outputs at file1-pretty.spg, file2-pretty.spg, etc. s S
Homework 2 – MiniJava Static Checking (Semantic Analysis)

This homework introduces your semester project, which consists of building a compiler for MiniJava, a subset of Java. MiniJava is designed so that its programs can be compiled by a full Java compiler like javac.

Here is a partial, textual description of the language. Much of it can be safely ignored (most things are well defined in the grammar or derived from the requirement that each MiniJava program is also a Java program):

    MiniJava is fully object-oriented, like Java. It does not allow global functions, only classes, fields and methods. The basic types are int, boolean, and int [] which is an array of int. You can build classes that contain fields of these basic types or of other classes. Classes contain methods with arguments of basic or class types, etc.

    MiniJava supports single inheritance but not interfaces. It does not support  function overloading, which means that each method name must be unique. In addition, all methods are inherently polymorphic (i.e., “virtual” in C++ terminology). This means that foo can be defined in a subclass if it has the same return type and arguments as in the parent, but it is an error if it exists with other arguments or return type in the parent. Also all methods must have a return type--there are no void methods. Fields in the base and derived class are allowed to have the same names, and are essentially different fields.

    All MiniJava methods are “public” and all fields “protected”. A class method cannot access fields of another class, with the exception of its superclasses. Methods are visible, however. A class's own methods can be called via “this”. E.g., this.foo(5) calls the object's own foo method, a.foo(5) calls the foo method of object a. Local variables are defined only at the beginning of a method. A name cannot be repeated in local variables (of the same method) and cannot be repeated  in fields (of the same class). A local variable x shadows a field x of the surrounding class.

    In MiniJava, constructors and destructors are not defined. The new operator  calls a default void constructor. In addition, there are no inner classes and there are no static methods or fields. By exception, the pseudo-static method “main” is handled specially in the grammar. A MiniJava program is a file that begins with a special class that contains the main method and specific arguments that are not used. The special class has no fields. After it, other classes are defined that can have fields and methods.

    Notably, an A class can contain a field of type B, where B is defined later in the file. But when we have "class B extends A”, A must be defined before B. As you'll notice in the grammar, MiniJava offers very simple ways to construct expressions and only allows < comparisons. There are no lists of operations, e.g., 1 + 2 + 3, but a method call on one object may be used as an argument for another method call. In terms of logical operators, MiniJava allows the logical and ("&&") and the logical not ("!"). For int arrays, the assignment and [] operators are allowed, as well as the a.length expression, which returns the size of array a. We have “while” and “if” code blocks. The latter are always followed by an “else”. Finally, the assignment "A a = new B();" when B extends A is correct, and the same applies when a method expects a parameter of type A and a B instance is given instead.

The MiniJava grammar in BNF can be downloaded here. You can make small changes to grammar, but you must accept everything that MiniJava accepts and reject  anything that is rejected by the full Java language. Making changes is not recommended because it will make your job harder in subsequent homework assignments. Normally you won't need to touch the grammar.

The MiniJava grammar in JavaCC form is here. You will use the JTB tool to convert it into a grammar that produces class hierarchies. Then you will write one or more visitors who will take control over the MiniJava input file and will tell whether it is semantically correct, or will print an error message. It isn’t necessary for the compiler to report precisely what error it encountered and compilation can end at the first error. But you should not miss errors or report errors in correct programs.

The visitors you will build should be subclasses of the visitors generated by JTB, but they may also contain methods and fields to hold information during static checking, to transfer information from one visitor to the next, etc. In the end, you will have a Main class that runs the semantic analysis initiating the parser that was produced by  JavaCC and executing the visitors you wrote. You will turn in your grammar file, if you have made changes, otherwise just the code produced by JavaCC and JTB  alongside your own classes that implement the visitors, etc. and a Main. The Main should parse and statically check all the MiniJava files that are given as arguments.

There will be a tutorial for JavaCC and JTB. You can use these files as MiniJava examples and to test your program. Obviously you are free to make up your own files, however the homework will be graded purely on how your compiler performs on all the files we will test it against (both the above sample files and others). You can share ideas and test files, but obviously you are not allowed to share code.

Your program should run as follows:

java [MainClassName] [file1] [file2] ... [fileN]

That is, your program must perform semantic analysis on all files given as arguments.

May the Force be with you!
Homework 1 - Calculator grammar
Part 1

For the first part of this homework you should implement a simple calculator. The calculator should accept expressions with addition, subtraction, multiplication, and division operators, as well as parentheses. The grammar (for single-digit numbers) is summarized in:

exp -> num | exp op exp | (exp)

op -> + | - | * | /

num -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9

You need to change this grammar to support priority between addition and multiplication, to remove the left recursion for LL parsing, etc.

This part of the homework is divided in two parts:

    You have to write (and provide in a .txt of .pdf file) the FIRST+ & FOLLOW sets for the LL(1) version of the above grammar. In the end you will summarize them in a single lookahead table (include a row for every derivation in your final grammar).

    You have to write a recursive descent parser in Java that reads expressions and computes the values or prints "parse error" if there is a syntax error. You don't need to identify blank space or multi-digit number. You can read the symbols one by one (as in the C getchar() function). The expression must end with a newline or EOF.

Part 2

In the second part of this homework you will implement a parser and translator for a language supporting string operations. The language supports the concatanation operator over strings, function definitions and calls, conditionals (if-else i.e, every if must be followed by an else), and the following logical expressions:

    string equality (string1 = string2): Whether string1 is equal to string2.
    is-substring-of (string1 in string2): Whether string1 is a substring of/is contained in string2.

All values in the language are strings.

Your parser, based on a context-free grammar, will translate the input language into S-expressions, i.e., fully parenthesized expressions with the operator in the first position. You will use JavaCUP for the generation of the parser combined either with a hand-written lexer or a generated-one (e.g., using JFlex, which is encouraged).

You will infer the desired syntax of the input and output languages from the examples below. The output language is a subset of Scheme so it can be interpreted by mit-scheme or online scheme interpreters like this, if you want to test your output.

There is no need to perform type checking for the argument types or a check for the number of function arguments. You can assume that the program input will always be semantically correct.

Note that extra parentheses are fine in the input syntax but not in the S-expression language. For instance "x" is not the same as "(x)" in the S-expression language: the latter means a call to function "x".
Example #1

Input:

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

Output (S-expressions):

(define (name) "John")                      // function name is defined. This is a comment and not part of the output
(name)                                     // function name is called

(define (surname) "Doe")
(define (fullname first_name sep last_name)
    (string-append (string-append first_name sep) last_name))

(surname)
(fullname (name) " " (surname))

Example #2

Input:

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

Output (S-expressions):

(define (name) "John")
(define (repeat x) (string-append x x))

(define (cond_repeat c x)
    (if (equal? c "yes")
        (repeat x)
        x))

(cond_repeat "yes" (name))
(cond_repeat "no" "Jane")

Example #3

Input:

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

Output (S-expressions):

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

You can use the following (inefficient but elegant) definition of substring? to test the s-expression code generated for the in conditional operator:

(define (substring? string1 string2)
    (let ((len1 (string-length string1))
          (len2 (string-length string2)))
      (cond ((> len1 len2) #f)
            ((string=? string1 (substring string2 0 len1)) #t)
            (else (substring? string1 (substring string2 1 len2))))))

