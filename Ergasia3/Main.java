import syntaxtree.*;
import java.io.*;
import java.util.*;

class Main {
	
	
	public static void main (String [] args){
		
	    FileInputStream fis = null;
		try{
			
			fis = new FileInputStream("/home/agg/Eclipse/GenerateIR/src/TestInput2.java");
			MiniJavaParser parser = new MiniJavaParser(fis);
			
			Goal root = parser.Goal();
			
			
			//==================== PHASE 1 ======================
			SymbolTableVisitor eval2 = new SymbolTableVisitor();	// Build SymbolTables
			root.accept(eval2, null);
			Utils.printSymTables();
			//==================== PHASE 2 ======================
			LoweringVisitor eval3 = new LoweringVisitor();			// Build Spiglet IR	
			root.accept(eval3, null);
			//===================================================
			
			

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
