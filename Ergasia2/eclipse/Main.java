import syntaxtree.*;
import visitor.*;
import java.io.*;
import java.util.Map;
import java.util.Set;

class Main {
	
	public static Map<String, SymbolTable> globalScope;
	public static Map<SymbolTable, SymbolTable> localScopes;
	
	public static void main (String [] args){

		FileInputStream fis = null;
		try{
			fis = new FileInputStream("LinkedList.java");
			MiniJavaParser parser = new MiniJavaParser(fis);
			System.err.println("Program parsed successfully.");
			
			Goal root = parser.Goal();
			
			//==================== PHASE 1======================
			ClassNamesVisitor eval = new ClassNamesVisitor();
			Set<String> class_names = root.accept(eval, null);	// get phase1 results
			//==================== PHASE 2======================
			SymbolTableVisitor eval2 = new SymbolTableVisitor(class_names);	// pass phase1 results to 2nd visitor
			root.accept(eval2, null);
			//==================== PHASE 3======================
			TypeCheckVisitor eval3 = new TypeCheckVisitor();
			root.accept(eval3, null);
			//==================================================
			
			System.out.println("=======================");
			for (String s: class_names){
				System.out.println(s);
			}
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
