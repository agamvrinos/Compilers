import syntaxtree.ArrayType;
import syntaxtree.BooleanType;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.FormalParameter;
import syntaxtree.FormalParameterList;
import syntaxtree.FormalParameterTail;
import syntaxtree.FormalParameterTerm;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.IntegerType;
import syntaxtree.MainClass;
import syntaxtree.MethodDeclaration;
import syntaxtree.Type;
import syntaxtree.TypeDeclaration;
import syntaxtree.VarDeclaration;
import visitor.GJDepthFirst;
import java.util.*;

public class TypeCheckVisitor extends GJDepthFirst<String, String>{
		
		
		void printGlobalScopes(){
			System.out.println("*****************************");
			System.out.println("Printing Global Scopes");
			System.out.println("*****************************");
			for (Map.Entry<String, SymbolTable> entry : Main.globalScope.entrySet()) {
	    	    String key = entry.getKey();
	    	    SymbolTable s = entry.getValue();
	    	    String val = "";
	    	    if (s != null)
	    	    	val = s.scope_name;
	    	    System.out.println("Key: " + key);
    	    	System.out.println("Value: " + ((s == null) ? "null" : val));
	    	    System.out.println("------------------------");
	    	}
		}
		
		void printLocalScopes(){
			
			System.out.println("*****************************");
			System.out.println("Printing Local Scopes");
			System.out.println("*****************************");
			for (Map.Entry<SymbolTable, SymbolTable> entry : Main.localScopes.entrySet()) {
	    	    String key = entry.getKey().scope_name;
	    	    SymbolTable s = entry.getValue();
	    	    String val = "";
	    	    if (s != null)
	    	    	val = s.scope_name;
	    	    System.out.println("Key: " + key);
    	    	System.out.println("Value: " + ((s == null) ? "null" : val));
	    	    System.out.println("------------------------");
	    	}
		}
		
		void printAllSymbolTables(){
			
			System.out.println("*****************************");
			System.out.println("Printing Symbol Tables");
			System.out.println("*****************************");
			for (Map.Entry<SymbolTable, SymbolTable> entry : Main.localScopes.entrySet()) {
				SymbolTable key = entry.getKey();
				key.printSymbolTable();
	    	    
	    	}
		}
		
		/**
	    * f0 -> MainClass()
	    * f1 -> ( TypeDeclaration() )*
	    * f2 -> <EOF>
	    */
		public String visit(Goal n, String argu) {
		  
		  n.f0.accept(this, argu);
		  n.f1.accept(this, argu);
		  n.f2.accept(this, argu);
		  
		  printGlobalScopes();
		  printLocalScopes();
		  printAllSymbolTables();
		  
		  return null;
		}
		
		/**
	    * f0 -> "class"
	    * f1 -> Identifier()
	    * f2 -> "{"
	    * f3 -> "public"
	    * f4 -> "static"
	    * f5 -> "void"
	    * f6 -> "main"
	    * f7 -> "("
	    * f8 -> "String"
	    * f9 -> "["
	    * f10 -> "]"
	    * f11 -> Identifier()
	    * f12 -> ")"
	    * f13 -> "{"
	    * f14 -> ( VarDeclaration() )*
	    * f15 -> ( Statement() )*
	    * f16 -> "}"
	    * f17 -> "}"
	    */
	    public String visit(MainClass n, String argu) {
			  
			n.f0.accept(this, argu);
			n.f1.accept(this, argu);
			n.f2.accept(this, argu);
			n.f3.accept(this, argu);
			n.f4.accept(this, argu);
			n.f5.accept(this, argu);
			n.f6.accept(this, argu);
			n.f7.accept(this, argu);
			n.f8.accept(this, argu);
			n.f9.accept(this, argu);
			n.f10.accept(this, argu);
			n.f11.accept(this, argu);
			n.f12.accept(this, argu);
			n.f13.accept(this, argu);
			n.f14.accept(this, argu);
			n.f15.accept(this, argu);
			n.f16.accept(this, argu);
			n.f17.accept(this, argu);
			  
			return null;
	    }
		   
}
