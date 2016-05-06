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
import syntaxtree.VarDeclaration;
import visitor.GJDepthFirst;
import java.util.*;

public class LoweringVisitor extends GJDepthFirst<String, String>{
		
		
	String buffer;
	String current_class;
	Integer temp_counter;
	Integer label_counter;
		
	public LoweringVisitor() {
		temp_counter = 0;
		label_counter = 0;
	}
	
	// Returns new TEMP
	public String getTemp(){
		temp_counter ++;
		String temp = "TEMP " + temp_counter.toString();
		return temp;
	}
	
	// Returns new LABEL
	public String getLabel(){
		label_counter ++;
		String label = "L " + temp_counter.toString();
		return label;
	}
	
	// Returns new GLOBAL vTable LABEL
	public String getGlobalLabel(String class_name){
		String label = class_name + "_vTable";
		return label;
	}
	
	// Returns new GLOBAL vTable LABEL
	public String getMethodLabel(String class_name, String method_name){
		String label = class_name + "_" + method_name;
		return label;
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
	  
	  
	  // Emit the buffer
	  System.out.println(buffer);

	  
	  return null;
	}
	//===============================================================================
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
		buffer = "MAIN\n"; 
		current_class = n.f1.f0.toString();
		
		for (Map.Entry<String,SymbolTable> entry : Main.globalScope.entrySet()) {
			
			String class_name = entry.getKey();
			SymbolTable table = entry.getValue();
			SymbolTable parent = Main.localScopes.get(Main.globalScope.get(class_name));
			
			System.out.println("CHILD NAME: " + table.scope_name);
			if (parent != null)
				System.out.println("PARENT NAME: " + parent.scope_name);
			else 
				System.out.println("PARENT NAME: " + "null");
			Integer cnt = Utils.getMergedMethodNum(table, parent);
			
//			System.out.println("COUNTER FOR " + class_name + ": " + cnt);
			cnt *= 4;	// vtable space to allocate
			
			// Create vTable label
			String vtable_label = getGlobalLabel(class_name);
	
			// Move vTable address to a TEMP
			String temp = getTemp();
			buffer += "\tMOVE " + temp + " " + vtable_label + "\n";
			
			// Allocate vTable
			String temp2 = getTemp();
			buffer += "\tMOVE " + temp2 + " HALLOCATE " + cnt.toString() + "\n";
			
			// Store vTable to the Global label address
			buffer += "\tHSTORE " + temp + " 0 " + temp2 + "\n";
			
			// Fill data (methods) to the vTables 
			List <String> merged_methods = Utils.getMergedMethods(table, parent);
			
			Integer off = 0;
			for (String st : merged_methods){
				String methodtemp = getTemp();
				buffer += "\tMOVE " + methodtemp + " " + st + "\n";
				buffer += "\tHSTORE " + temp2 + " " + off + " " + methodtemp + "\n";
				off += 4;
			}
			
			
			buffer += "\t=====================================================\n";
			
	  	}
		
    	n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f5.accept(this, argu);
		n.f6.accept(this, argu);
		n.f8.accept(this, argu);
		n.f11.accept(this, argu);
		n.f14.accept(this, argu);
		n.f15.accept(this, argu);
		  
		buffer += "END\n"; 
		return null;
    }
    //===============================================================================  
	/**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
    public String visit(ClassDeclaration n, String argu){
    	
    	String name = n.f1.f0.toString();
    	
    	current_class = name;
    	
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        
        return null;
    }
    //===============================================================================
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
        
    	String name = n.f1.f0.toString();
    	String extended_name = n.f3.f0.toString();
    	current_class = name;
    	
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        n.f5.accept(this, argu);
        n.f6.accept(this, name);
        n.f7.accept(this, argu);
        
        return null;
    }
    //===============================================================================
    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    public String visit(MethodDeclaration n, String argu) {
		
		String method_name = n.f2.f0.toString();
		
		buffer += getMethodLabel(current_class, method_name) + "\n";
		n.f0.accept(this, argu);
		n.f1.accept(this, argu);
		n.f2.accept(this, argu);
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
	   
	   buffer += "END\n";
	  
	   
	   return null;
	}
	    
}
