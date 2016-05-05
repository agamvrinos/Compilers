import java.util.HashSet;
import java.util.Set;

import syntaxtree.*;
import visitor.GJDepthFirst;

public class ClassNamesVisitor extends GJDepthFirst<Set<String>, Integer>{

	public Set<String> class_names;

	public ClassNamesVisitor() {
		class_names = new HashSet<>();
	}
	
	/**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
	public Set<String> visit(Goal n, Integer argu) {
	  
	  n.f0.accept(this, argu);
	  n.f1.accept(this, argu);
	  n.f2.accept(this, argu);
	  
	  return class_names;
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
    public Set<String> visit(MainClass n, Integer argu) {
		   
		class_names.add(n.f1.f0.toString());	// add class name to HashSet
		  
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
    public Set<String> visit(ClassDeclaration n, Integer argu){
    	
    	String class_name = n.f1.f0.toString();
    	
    	if (class_names.contains(class_name))
    		throw new RuntimeException(LineNumberInfo.get(n) + ": Type " + class_name + " already defined");
    	
    	class_names.add(class_name);	// add class name to HashSet
        
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
    public Set<String> visit(ClassExtendsDeclaration n, Integer argu) {
    	
    	String name = n.f1.f0.toString();
    	String extended_class = n.f3.f0.toString();
    	
    	if (class_names.contains(name))
			throw new RuntimeException(LineNumberInfo.get(n) + ": Type " + name + " already defined");
    	
    	if (!class_names.contains(extended_class))
			throw new RuntimeException(LineNumberInfo.get(n) + ": Inheritance Error");
			
    	
    	class_names.add(name);	// add class name to HashSet
        
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