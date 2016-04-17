CUP        = java_cup.Main  <
JFLEX      = jflex
JAVA       = java
JAVAC      = javac
JAR 	   = -cp java-cup-11b.jar

# --------------------------------------------------

all: flex cup compile run

flex:
	$(JFLEX) lexer.flex

cup:
	$(JAVA) $(JAR) $(CUP) parser.cup

compile:
	$(JAVAC) $(JAR) Main.java parser.java Scanner.java sym.java

run:
	$(JAVA) -cp java-cup-11b-runtime.jar:. Main inputs/input.txt

clean:
	rm -f *.class
	rm -f *~
	rm -f Scanner.java
	rm -f parser.java
	rm -f sym.java
	rm -f lexer-output.txt
