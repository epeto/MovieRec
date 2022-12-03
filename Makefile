compile: compileComparadorEnum compileDivisor compileExpresion compileParser compileWorker compileManager compileMain
compileComparadorEnum:
	javac src/utilidades/ComparadorEnum.java -d classes
compileDivisor:
	javac src/utilidades/Divisor.java -d classes
compileExpresion:
	javac -cp classes src/utilidades/Expresion.java -d classes
compileParser:
	javac -cp classes src/utilidades/Parser.java -d classes
compileWorker:
	javac -cp classes src/ManagerWorker/Worker.java -d classes
compileManager:
	javac -cp classes src/ManagerWorker/Manager.java -d classes
compileMain:
	javac -cp classes -p lib --add-modules=ALL-MODULE-PATH src/movies/Main.java -d classes
run:
	java -p lib --add-modules=ALL-MODULE-PATH -cp ./classes movies.Main
clean:
	rm -r classes
