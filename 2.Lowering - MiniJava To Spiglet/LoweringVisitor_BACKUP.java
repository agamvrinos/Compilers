import syntaxtree.AllocationExpression;
import syntaxtree.AndExpression;
import syntaxtree.ArrayAllocationExpression;
import syntaxtree.ArrayAssignmentStatement;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.ArrayType;
import syntaxtree.AssignmentStatement;
import syntaxtree.Block;
import syntaxtree.BooleanType;
import syntaxtree.BracketExpression;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.Clause;
import syntaxtree.CompareExpression;
import syntaxtree.Expression;
import syntaxtree.ExpressionList;
import syntaxtree.ExpressionTail;
import syntaxtree.ExpressionTerm;
import syntaxtree.FalseLiteral;
import syntaxtree.FormalParameter;
import syntaxtree.FormalParameterList;
import syntaxtree.FormalParameterTail;
import syntaxtree.FormalParameterTerm;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.IfStatement;
import syntaxtree.IntegerLiteral;
import syntaxtree.IntegerType;
import syntaxtree.MainClass;
import syntaxtree.MessageSend;
import syntaxtree.MethodDeclaration;
import syntaxtree.MinusExpression;
import syntaxtree.NotExpression;
import syntaxtree.PlusExpression;
import syntaxtree.PrimaryExpression;
import syntaxtree.PrintStatement;
import syntaxtree.Statement;
import syntaxtree.ThisExpression;
import syntaxtree.TimesExpression;
import syntaxtree.TrueLiteral;
import syntaxtree.Type;
import syntaxtree.VarDeclaration;
import syntaxtree.WhileStatement;
import visitor.GJDepthFirst;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class LoweringVisitor_BACKUP extends GJDepthFirst<String, String>{
		
		
	String buffer;
	String current_class;
	String current_method;
	
		
	public LoweringVisitor_BACKUP() {
		Utils.temp_counter = Utils.getMaxFieldsNum();
		Utils.label_counter = 0;
		Utils.mapAllLocals();	// maps local vars to temps
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
		current_class = n.f1.accept(this, "name");
		current_method = "main";
		
		for (Map.Entry<String,SymbolTable> entry : Utils.symbolTables.entrySet()) {

			buffer += "\t=================== Vtable Creation ====================\n";
			String class_name = entry.getKey();
			SymbolTable table = entry.getValue();
			
			Integer cnt = table.getMergedMethodsCount();
			
			cnt *= 4;	// vtable space to allocate
			
			// Create vTable label
			String vtable_label = Utils.getGlobalLabel(class_name);
	
			// Move vTable address to String TEMP
			String temp = Utils.getTemp();
			buffer += "\tMOVE " + temp + " " + vtable_label + "\n";
			
			// Allocate vTable
			String temp2 = Utils.getTemp();
			buffer += "\tMOVE " + temp2 + " HALLOCATE " + cnt.toString() + "\n";
			
			// Store vTable to the Global label address
			buffer += "\tHSTORE " + temp + " 0 " + temp2 + "\n";
			
			// Fill data (methods) to the vTables 
			List <String> merged_methods = table.getMergedMethods();
			
			Integer off = 0;
			for (String st : merged_methods){
				String methodtemp = Utils.getTemp();
				buffer += "\tMOVE " + methodtemp + " " + st + "\n";
				buffer += "\tHSTORE " + temp2 + " " + off + " " + methodtemp + "\n";
				off += 4;
			}
	  	}
		
		n.f11.accept(this, "name");
		n.f14.accept(this, "name");
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
    	
        n.f1.accept(this, "name");
        n.f3.accept(this, argu);
        n.f4.accept(this, argu);
        
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
    	
        n.f1.accept(this, "name");
        n.f3.accept(this, "name");
        n.f5.accept(this, argu);
        n.f6.accept(this, name);
        
        return null;
    }
    //===============================================================================
    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public String visit(VarDeclaration n, String argu) {
    	String var_type = n.f0.accept(this, "name");
    	String var_name = n.f1.accept(this, "name");
    	
    	return null;
    }
    
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
		MethodType method = Utils.symbolTables.get(current_class).lookupMethod(method_name);
		Integer params_num  = 0;
		current_method = method_name;
		
		if (method != null)
			params_num = method.getArgumensSize();
			
		
		buffer += "\t=====================================================\n";
		buffer += Utils.getMethodLabel(current_class, method_name) + " [" + (params_num + 1) + "]" + "\n" + "BEGIN\n";
		
		n.f1.accept(this, "name");
		n.f2.accept(this, "name");
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f7.accept(this, argu);
		n.f8.accept(this, argu);
		n.f10.accept(this, argu);
		
		buffer += "END\n";
	  
	   
	   return null;
	}
    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    public String visit(FormalParameterList n, String argu) {
       String _ret=null;
       n.f0.accept(this, argu);
       n.f1.accept(this, argu);
       return _ret;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public String visit(FormalParameter n, String argu) {
       String _ret=null;
       n.f0.accept(this, "name");
       n.f1.accept(this, "name");
       return _ret;
    }

    /**
     * f0 -> ( FormalParameterTerm() )*
     */
    public String visit(FormalParameterTail n, String argu) {
       return n.f0.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public String visit(FormalParameterTerm n, String argu) {
       String _ret=null;
       n.f0.accept(this, argu);
       n.f1.accept(this, argu);
       return _ret;
    }

    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public String visit(Type n, String argu) {
       return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(ArrayType n, String argu) {
       String _ret=null;
       n.f0.accept(this, argu);
       n.f1.accept(this, argu);
       n.f2.accept(this, argu);
       return _ret;
    }

    /**
     * f0 -> "boolean"
     */
    public String visit(BooleanType n, String argu) {
       return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "int"
     */
    public String visit(IntegerType n, String argu) {
       return n.f0.accept(this, argu);
    }

    /**
     * f0 -> Block()
     *       | AssignmentStatement()		//=== DONE ===//
     *       | ArrayAssignmentStatement()
     *       | IfStatement()				//=== DONE ===//
     *       | WhileStatement()
     *       | PrintStatement()
     */
    public String visit(Statement n, String argu) {
    	return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public String visit(Block n, String argu) {
       String _ret=null;
       n.f0.accept(this, argu);
       n.f1.accept(this, argu);
       n.f2.accept(this, argu);
       return _ret;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public String visit(AssignmentStatement n, String argu) {
    	
       String var_temp = n.f0.accept(this, argu);
       String value = n.f2.accept(this, "loadfirst");
       
       String [] arr = var_temp.split("_");
       
       if (arr.length == 1){
	       buffer += "\t================ MOVE ASSIGNMENT ================\n";
	       buffer += "\tMOVE " + var_temp + " " + value + "\n";
       }
       else if (arr.length == 2){
    	   
    	   String var_name = arr[1];
    	   System.out.println(var_name + " HEHEHEH");
    	   String offset = Utils.symbolTables.get(current_class).getOffsetField(var_name);
    	   System.out.println(offset + " HEHEHEH");
    	   buffer += "\t================ STORE ASSIGNMENT ================\n";
    	   buffer += "\tHSTORE TEMP 0 " + offset + " " + value + "\n";
       }
	   return null;
    }
    
    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    public String visit(ArrayAssignmentStatement n, String argu) {
    	String _ret=null;
       n.f0.accept(this, argu);
       n.f1.accept(this, argu);
       n.f2.accept(this, argu);
       n.f3.accept(this, argu);
       n.f4.accept(this, argu);
       n.f5.accept(this, argu);
       n.f6.accept(this, argu);
       return _ret;
    }
    
    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    public String visit(IfStatement n, String argu) {
    	String label = Utils.getLabel();
    	String label2 = Utils.getLabel();
    	
    	String cond_temp = n.f2.accept(this, "loadfirst");
    	buffer += "\t================== IF STATEMENT ==================\n";
    	buffer += "\tCJUMP " + cond_temp + " " + label + "\n";
    	
    	n.f4.accept(this, argu);				// If branch not taken generate code for if
    	buffer += "\tJUMP " + label2 + "\n";	// Go the end of if statement
    	
    	buffer += label + "\tNOOP\n";			// Else if branch is taken
    	
    	n.f6.accept(this, argu);				// Generate code for else
    	buffer += label2 + "\tNOOP\n";			// End of if statement
    	
    	
    	return null;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public String visit(WhileStatement n, String argu) {
    	String _ret=null;
       n.f0.accept(this, argu);
       n.f1.accept(this, argu);
       n.f2.accept(this, argu);
       n.f3.accept(this, argu);
       n.f4.accept(this, argu);
       return _ret;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public String visit(PrintStatement n, String argu) {
       
       String temp_to_print = n.f2.accept(this, argu);
//       buffer += "\tPRINT " + temp_to_print + "\n";
       return null;
    }

    /**
     * f0 -> AndExpression()			//=== DONE ===//
     *       | CompareExpression()		//=== DONE ===//
     *       | PlusExpression()			//=== DONE ===//
     *       | MinusExpression()		//=== DONE ===//
     *       | TimesExpression()		//=== DONE ===//
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | Clause()
     */
    public String visit(Expression n, String argu) {
       return n.f0.accept(this, argu);
    }

    /**
     * f0 -> Clause()
     * f1 -> "&&"
     * f2 -> Clause()
     */
    public String visit(AndExpression n, String argu) {
    	String label = Utils.getLabel();
    	String result_temp = Utils.getTemp();
    	String lvalue_temp = n.f0.accept(this, "loadfirst");
    	
    	buffer += "\t================== AND EXPRESSION ==================\n";
    	buffer += "\tMOVE " + result_temp + " " + lvalue_temp + "\n";
    	buffer += "\tCJUMP " + result_temp + " " + label + "\n";
    	
    	String rvalue_temp = n.f2.accept(this, "loadfirst");
    	
    	// branch not taken
    	buffer += "\tMOVE " + result_temp + " " + rvalue_temp + "\n";
    	
    	buffer += label + "\tNOOP\n";
    	
    	return result_temp;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public String visit(CompareExpression n, String argu) {
       
       String lvalue_temp = n.f0.accept(this, "loadfirst");
       String rvalue_temp = n.f2.accept(this, "loadfirst");
       String result_temp = Utils.getTemp();
       
       buffer += "\t================= COMP EXPRESSION ==================\n";
	   buffer += "\tMOVE " + result_temp + " LT " + lvalue_temp + " " + rvalue_temp + "\n";
	   
       return result_temp;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public String visit(PlusExpression n, String argu) {
       String lvalue_temp = n.f0.accept(this, "loadfirst");
       String rvalue_temp = n.f2.accept(this, "loadfirst");
       String result_temp = Utils.getTemp();
       
       buffer += "\t================= PLUS EXPRESSION ==================\n";
	   buffer += "\tMOVE " + result_temp + " PLUS " + lvalue_temp + " " + rvalue_temp + "\n";
	   
       return result_temp;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public String visit(MinusExpression n, String argu) {
		String lvalue_temp = n.f0.accept(this, "loadfirst");
		String rvalue_temp = n.f2.accept(this, "loadfirst");
		String result_temp = Utils.getTemp();
		
		buffer += "\t================= MINUS EXPRESSION ==================\n";
		buffer += "\tMOVE " + result_temp + " MINUS " + lvalue_temp + " " + rvalue_temp + "\n";
		   
		return result_temp;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public String visit(TimesExpression n, String argu) {
    	String lvalue_temp = n.f0.accept(this, "loadfirst");
        String rvalue_temp = n.f2.accept(this, "loadfirst");
        String result_temp = Utils.getTemp();
        
        buffer += "\t================= TIMES EXPRESSION ==================\n";
 	   	buffer += "\tMOVE " + result_temp + " TIMES " + lvalue_temp + " " + rvalue_temp + "\n";
 	   
        return result_temp;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public String visit(ArrayLookup n, String argu) {
       String _ret=null;
       n.f0.accept(this, argu);
       n.f1.accept(this, argu);
       n.f2.accept(this, argu);
       n.f3.accept(this, argu);
       return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public String visit(ArrayLength n, String argu) {
       String _ret=null;
       n.f0.accept(this, argu);
       n.f1.accept(this, argu);
       n.f2.accept(this, argu);
       return _ret;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public String visit(MessageSend n, String argu) {
        String res_temp = Utils.getTemp();
        
    	String object_temp = n.f0.accept(this, "loadfirst");
    	String method_name = n.f2.accept(this, "name");
    	String method_offset = Utils.symbolTables.get(current_class).info.getOffsetMethod(method_name);
    	
    	buffer += "\tHLOAD " + res_temp + " " + object_temp + " 0\n";
    	n.f4.accept(this, argu);
    	return null;
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    public String visit(ExpressionList n, String argu) {
       String _ret=null;
       n.f0.accept(this, argu);
       n.f1.accept(this, argu);
       return _ret;
    }

    /**
     * f0 -> ( ExpressionTerm() )*
     */
    public String visit(ExpressionTail n, String argu) {
       return n.f0.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public String visit(ExpressionTerm n, String argu) {
       String _ret=null;
       n.f0.accept(this, argu);
       n.f1.accept(this, argu);
       return _ret;
    }
    
    /**
     * f0 -> NotExpression()
     *       | PrimaryExpression()
     */
    public String visit(Clause n, String argu) {
       return n.f0.accept(this, argu);
    }

    /**
     * f0 -> IntegerLiteral()	//=== DONE ===//
     *       | TrueLiteral()	//=== DONE ===//
     *       | FalseLiteral()	//=== DONE ===//
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | BracketExpression()
     */
    public String visit(PrimaryExpression n, String argu) {
       return n.f0.accept(this, argu);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public String visit(IntegerLiteral n, String argu) {
    	String int_temp = Utils.getTemp();
    	buffer += "\t================= INTEGER ====================\n";
    	buffer += "\tMOVE " + int_temp + " " + n.f0.toString() + "\n";
    	return int_temp;
    }

    /**
     * f0 -> "true"
     */
    public String visit(TrueLiteral n, String argu) {
    	String bool_temp = Utils.getTemp();
    	buffer += "\t================= BOOLEAN ====================\n";
    	buffer += "\tMOVE " + bool_temp + " 1\n";
    	return bool_temp;
    }

    /**
     * f0 -> "false"
     */
    public String visit(FalseLiteral n, String argu) {
    	String bool_temp = Utils.getTemp();
    	buffer += "\t================= BOOLEAN ====================\n";
    	buffer += "\tMOVE " + bool_temp + " 0\n";
    	return bool_temp;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public String visit(Identifier n, String argu) {
    	String name = n.f0.toString();
    	String temp = null;
    	
    	System.out.println("MOU IRTHE: " + name);
    	System.out.println("CURENT METHOD: " + current_method);
    	System.out.println("==========================================");
    	MethodType method;
    	//1ST CHECK IF LOCAL
    	//2ND CHECK IF ARG
    	//3RD CHECK IF CLASS FIELD
    	if (argu != null && argu.equals("name")){
    		System.out.println("MESA STO NAME: " + n.f0.toString());
    		return n.f0.toString();
    	}
    		
    	else {
    		method = Utils.symbolTables.get(current_class).info.localCheck(current_method, name);
    		if (method == null){
    			System.out.println("NO LOCAL " + n.f0.toString());
    			method = Utils.symbolTables.get(current_class).info.argCheck(current_method, name);
    			if (method == null) {
    				System.out.println("NO ARG " + name);
    				FieldType field = Utils.symbolTables.get(current_class).fieldCheck(name);
			    	if (field == null) {
			    		System.out.println("NO FIELD " + name);
			    	}
			    	else {
			    		if (argu != null && argu.equals("loadfirst")){
			    			temp = Utils.getTemp();
			    			String offset = Utils.symbolTables.get(current_class).getOffsetField(name);
			    			buffer += "\t================ LOAD FIELD =================\n";
			    			buffer += "\tHLOAD " + temp + " TEMP 0 " + offset + "\n"; 
			    		}
			    		else {
			    			temp = Utils.getTemp() + "_" + name;
			    		}
			    			
	    				System.out.println("FIELD " + n.f0.toString());
			    	}
    			}
    			else {
    				temp = method.getArguTemp(name);;
    				System.out.println("ARG " + n.f0.toString());
    				System.out.println("RET TEMP: " + temp);
    			}
    		}
    		else {
    			System.out.println("LOCAL " + n.f0.toString());
    			temp = method.getLocalTemp(name);
    			System.out.println("RET TEMP: " + temp);
    		}
    		return temp;
    	}
    	
    	
    }
    
    /**
     * f0 -> "this"
     */
    public String visit(ThisExpression n, String argu) {
       return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public String visit(ArrayAllocationExpression n, String argu) {
    	String _ret=null;
       n.f0.accept(this, argu);
       n.f1.accept(this, argu);
       n.f2.accept(this, argu);
       n.f3.accept(this, argu);
       n.f4.accept(this, argu);
       return _ret;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public String visit(AllocationExpression n, String argu) {
       
       String class_name = n.f1.f0.toString();
       Integer fields_bytes = (Utils.symbolTables.get(class_name).getMergedFieldsCount() +  1) * 4;
       
       String object_address = Utils.getTemp();
       String vTable_label = Utils.getTemp();
       String vTable_address = Utils.getTemp();
       String init_value = Utils.getTemp();
       
       buffer += "\t====================== NEW OBJECT ======================\n";
       buffer += "\tMOVE " + object_address + " HALLOCATE " + fields_bytes + "\n" ;		// Allocate space for the object
       buffer += "\tMOVE " + vTable_label + " " + Utils.getGlobalLabel(class_name) + "\n";	// Get vTable label for this class
       buffer += "\tHLOAD " + vTable_address + " " + vTable_label + " 0\n";				// Load vTable using its label
       buffer += "\tHSTORE " + object_address + " 0 " + vTable_address + "\n";			// Store vTable address at object's 1st position
       
       if (fields_bytes > 4){
	       buffer += "\tMOVE " + init_value + " 0" + "\n";									// Move zero to a temp
	       for (int i = 4; i < fields_bytes; i = i + 4)
	    	   buffer += "\tHSTORE " + object_address + " " + i + " " + init_value + "\n";	// Initialize every field of the object
       }
       
       return object_address;	// Return address of the object allocated
    }

    /**
     * f0 -> "!"
     * f1 -> Clause()
     */
    public String visit(NotExpression n, String argu) {
    	String _ret=null;
       n.f0.accept(this, argu);
       n.f1.accept(this, argu);
       return _ret;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public String visit(BracketExpression n, String argu) {
    	String _ret=null;
       n.f0.accept(this, argu);
       n.f1.accept(this, argu);
       n.f2.accept(this, argu);
       return _ret;
    }
	    
}
