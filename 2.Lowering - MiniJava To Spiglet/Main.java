import syntaxtree.*;
import java.io.*;

class Main {
	
	public static void main (String [] args){
		
	    FileInputStream fis = null;
	    for (int i = 0; i < args.length; i ++){
			try{
				String filename = args[i];
				
				fis = new FileInputStream("Inputs/JavaFiles/" + filename);
				
				MiniJavaParser parser = new MiniJavaParser(fis);
				
				Goal root = parser.Goal();
				
				//==================== PHASE 1 ======================
				SymbolTableVisitor eval2 = new SymbolTableVisitor();	// Build SymbolTables
				root.accept(eval2, null);
				//==================== PHASE 2 ======================
				LoweringVisitor eval3 = new LoweringVisitor();			// Build Spiglet IR	
				root.accept(eval3, null);
				//===================================================
				PrintWriter out = new PrintWriter("Results/" + filename.split("\\.")[0] + ".spg");
				out.print(LoweringVisitor.buffer);
				out.flush();
				out.close();
	
			}
			catch(ParseException ex){
				System.out.println(ex.getMessage());}
			catch(FileNotFoundException ex){
				System.err.println(ex.getMessage());}
			finally{
				try{
					if(fis != null) fis.close();}
				catch(IOException ex){
					System.err.println(ex.getMessage());}
			}
		}
	}
}
