import syntaxtree.*;
import java.io.*;
import java.util.Map;
import java.util.Set;

class Main {
	
	public static Map<String, SymbolTable> globalScope;
	public static Map<SymbolTable, SymbolTable> localScopes;
	public static Map<String, Map<String,SymbolTable>> mapping;
	
	public static void main (String [] args){

		FileInputStream fis = null;
		for (int i = 0; i < args.length; i ++){
		try{
			
			System.out.println("=============================================================");
			System.out.println("Program Name: " + args[i]);
			System.out.println("=============================================================");
			fis = new FileInputStream(args[i]);
			MiniJavaParser parser = new MiniJavaParser(fis);
			
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
			
//			System.err.println("Program " + args[i] + " TypeChecked successfully.");

		}
		catch(ParseException ex){

			System.out.println(ex.getMessage());
		}
		catch(FileNotFoundException ex){
			System.err.println(ex.getMessage());
		}
		catch(RuntimeException ex){
			System.out.println("=============================================================");
			System.out.println("ERROR AT PROGRAM: " + args[i]);
			System.out.println(ex.getMessage());
			System.out.println("=============================================================");
			continue;
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
}
