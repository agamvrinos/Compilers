//import syntaxtree.*;
import java.io.*;
import java.util.*;

class Main {
	
	public static Map<String, SymbolTable> globalScope;
	public static Map<SymbolTable, SymbolTable> localScopes;
	public static Map<String, Map<String,SymbolTable>> mapping;
	
	public static void main (String [] args){

		SymbolTable myTable = new SymbolTable("A");
		myTable.insertField(new SymbolType("x", "int"));
		myTable.insertField(new SymbolType("y", "int"));
		
		List<String> parameters = new ArrayList<>();
		parameters.add("int");
		parameters.add("boolean");
		
		myTable.insertMethod(new SymbolType("foo", "int", parameters));
		
		myTable.printSymbolTable();
		
	    myTable = myTable.enterScope("foo");
	    myTable.insertField(new SymbolType("z", "int"));
	    
	    myTable.printSymbolTable();
	    
	    SymbolType m = myTable.lookup("z", "field");
	}
}
