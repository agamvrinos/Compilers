JAVAC=javac
JVM=java
CLASSPATH = lib/iris-0.60.jar:lib/iris-parser-0.60.jar:src/
sources = $(wildcard src/iris/*.java)
classes = $(sources:.java=.class)

all: compile

compile:
	java -jar jtb132di.jar spiglet.jj
	javacc spiglet-jtb.jj
	# javac -classpath lib/iris-0.60.jar:lib/iris-parser-0.60.jar /src/iris/Main.java
	# $(classes)

clean :
	rm -f $(classes)

%.class : %.java
	$(JAVAC) -cp $(CLASSPATH) $<

run-family:
	$(JVM) -cp $(CLASSPATH) iris.Main datalog-examples/family

run-graph:
	$(JVM) -cp $(CLASSPATH) iris.Main datalog-examples/graph

run-spiglet-factorial:
	$(JVM) -cp $(CLASSPATH) iris.Main datalog-examples/spiglet/factorial
