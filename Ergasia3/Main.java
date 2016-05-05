import syntaxtree.*;
import java.io.*;
import java.util.*;

class Main {
	
	public static Map<String, SymbolTable> globalScope;
	public static Map<SymbolTable, SymbolTable> localScopes;
	public static Map<String, Map<String,SymbolTable>> mapping;
	
	public static void main (String [] args){

	    FileInputStream fis = null;
		try{
			
			fis = new FileInputStream("TestInput.java");
			MiniJavaParser parser = new MiniJavaParser(fis);
			
			Goal root = parser.Goal();
			
			//==================== PHASE 1======================
			ClassNamesVisitor eval = new ClassNamesVisitor();
			Set<String> class_names = root.accept(eval, null);				// Get phase1 results
			//==================== PHASE 2======================
			SymbolTableVisitor eval2 = new SymbolTableVisitor(class_names);	// Pass phase1 results to 2nd visitor
			root.accept(eval2, null);
			//==================== PHASE 3======================
			LoweringVisitor eval3 = new LoweringVisitor();					// Build Spiglet representation	
			root.accept(eval3, null);
			//==================================================
			
//			eval2.printGlobalScopes();
//			eval2.printLocalScopes();
//			eval2.printAllSymbolTables();
//			eval2.printMapping();

		}
		catch(ParseException ex){

			System.out.println(ex.getMessage());
		}
		catch(FileNotFoundException ex){
			System.err.println(ex.getMessage());
		}
		
		finally{
			try{
				if(fis != null) fis.close();
			}
			catch(IOException ex){
				System.err.println(ex.getMessage());
			}
		}
	}
}
