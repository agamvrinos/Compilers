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
		buffer = "MAIN:\n"; 
		
		for (Map.Entry<String,SymbolTable> entry : Main.globalScope.entrySet()) {
			
			String class_name = entry.getKey();
			SymbolTable table = entry.getValue();
			
			Integer cnt = table.sym_table.getMethods_counter();
			if (cnt != null) System.out.println("COUNTER FOR " + class_name + ": " + cnt);
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
		  
		buffer += "END:\n"; 
		return null;
    }
	    
}
