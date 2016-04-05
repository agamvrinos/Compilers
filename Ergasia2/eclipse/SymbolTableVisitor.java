import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.MainClass;
import syntaxtree.Type;
import syntaxtree.TypeDeclaration;
import syntaxtree.VarDeclaration;
import visitor.GJDepthFirst;

public class SymbolTableVisitor extends GJDepthFirst<String, String>{
		
		SymbolTable table = null;
		/**
	    * f0 -> MainClass()
	    * f1 -> ( TypeDeclaration() )*
	    * f2 -> <EOF>
	    */
		
		public String visit(Goal n, String argu) {
		  
		  n.f0.accept(this, argu);
		  n.f1.accept(this, argu);
		  n.f2.accept(this, argu);
		  
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
		   
		/**
	    * f0 -> "class"
	    * f1 -> Identifier()
	    * f2 -> "{"
	    * f3 -> ( VarDeclaration() )*
	    * f4 -> ( MethodDeclaration() )*
	    * f5 -> "}"
	    */
	    public String visit(ClassDeclaration n, String argu){
	    	
	    	if (table == null)
	    		table = new SymbolTable(n.f1.f0.toString());
	    	else
	    		table.enterScope(n.f1.f0.toString());
	    	
	        n.f0.accept(this, argu);
	        n.f1.accept(this, argu);
	        n.f2.accept(this, argu);
	        n.f3.accept(this, argu);
	        n.f4.accept(this, argu);
	        n.f5.accept(this, argu);
	        
	        return null;
	    }
	    
	    /**
	     * f0 -> "class"
	     * f1 -> Identifier()
	     * f2 -> "extends"
	     * f3 -> Identifier()
	     * f4 -> "{"
	     * f5 -> ( VarDeclaration() )*
	     * f6 -> ( MethodDeclaration() )*
	     * f7 -> "}"
	    */
	    public String visit(ClassExtendsDeclaration n, String argu) {
	        
	        n.f0.accept(this, argu);
	        n.f1.accept(this, argu);
	        n.f2.accept(this, argu);
	        n.f3.accept(this, argu);
	        n.f4.accept(this, argu);
	        n.f5.accept(this, argu);
	        n.f6.accept(this, argu);
	        n.f7.accept(this, argu);
	        
	        return null;
	    }
	    
	    /**
	     * f0 -> Type()
	     * f1 -> Identifier()
	     * f2 -> ";"
	    */
	    public String visit(VarDeclaration n, String argu) {
	    	
	       String var_type = n.f0.accept(this, argu);
	       String var_name = n.f1.accept(this, argu);
	       n.f2.accept(this, argu);
	       
	       SymbolType t = new SymbolType("variable", var_name, var_type);
	       
	       t.printType();
	       
	       table.insert(t);
	       return null;
	    }
	    
	    /**
	     * f0 -> <IDENTIFIER>
	     */
	    public String visit(Identifier n, String argu) {
	    
	    	n.f0.accept(this, argu);
	    	
	    	return n.f0.toString();
	    }
	    
	    /**
	     * f0 -> ArrayType()
	     *       | BooleanType()
	     *       | IntegerType()
	     *       | Identifier()
	     */
	    public String visit(Type n, String argu) {
	       n.f0.accept(this, argu);
	       String s = "";
	       
	       if (n.f0.which == 0)
	    	   s = "intArray";
	       else if(n.f0.which == 1)
	    	   s = "boolean";
	       else if(n.f0.which == 2)
	    	   s = "integer";
	       else if(n.f0.which == 3)
	    	   s = "classType";
	       
	       return s;
	    }
	   
}
