import java.util.Map;
import java.util.Set;

import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.Goal;
import syntaxtree.MainClass;
import visitor.GJDepthFirst;

public class SymbolTableVisitor extends GJDepthFirst<Integer, Integer>{
		
		/**
	    * f0 -> MainClass()
	    * f1 -> ( TypeDeclaration() )*
	    * f2 -> <EOF>
	    */
		public Integer visit(Goal n, Integer argu) {
		  
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
	    public Integer visit(MainClass n, Integer argu) {
			  
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
	    public Integer visit(ClassDeclaration n, Integer argu){
	    	
	    	System.out.println("HAHAH");
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
	    public Integer visit(ClassExtendsDeclaration n, Integer argu) {
	        
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
	   
}
