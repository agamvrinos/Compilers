import syntaxtree.AllocationExpression;
import syntaxtree.AndExpression;
import syntaxtree.ArrayAllocationExpression;
import syntaxtree.ArrayAssignmentStatement;
import syntaxtree.ArrayLength;
import syntaxtree.ArrayLookup;
import syntaxtree.AssignmentStatement;
import syntaxtree.BracketExpression;
import syntaxtree.ClassDeclaration;
import syntaxtree.ClassExtendsDeclaration;
import syntaxtree.CompareExpression;
import syntaxtree.Expression;
import syntaxtree.ExpressionList;
import syntaxtree.ExpressionTerm;
import syntaxtree.FalseLiteral;
import syntaxtree.FormalParameter;
import syntaxtree.Goal;
import syntaxtree.Identifier;
import syntaxtree.IfStatement;
import syntaxtree.IntegerLiteral;
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
import syntaxtree.VarDeclaration;
import syntaxtree.WhileStatement;
import visitor.GJDepthFirst;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class LoweringVisitor extends GJDepthFirst<DataBlock, DataBlock>{
		
		
	static String buffer;
	String current_class;
	String current_method;
	List<List<String>> call_temps2;
	int index;
	
		
	public LoweringVisitor() {
		Utils.temp_counter = Utils.getMaxFieldsNum();
		Utils.label_counter = 0;
		
		// map local variables to TEMPS
		Utils.mapAllLocals();	
		
		call_temps2 = new ArrayList<>();
		index = -1;
	}
	
	/**
    * f0 -> MainClass()
    * f1 -> ( TypeDeclaration() )*
    * f2 -> <EOF>
    */
	public DataBlock visit(Goal n, DataBlock argu) {
	  
	  n.f0.accept(this, argu);
	  n.f1.accept(this, argu);
	  n.f2.accept(this, argu);
	  
	  // Emit the buffer
//	  System.out.println(buffer);

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
    public DataBlock visit(MainClass n, DataBlock argu) {
		buffer = "MAIN\n"; 
		
		DataBlock db = new DataBlock();
		db.name_flag = true;
		
		DataBlock answer_db = n.f1.accept(this, db);
		current_class = answer_db.name;
		current_method = "main";
		
		for (Map.Entry<String,SymbolTable> entry : Utils.symbolTables.entrySet()) {

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
		
		n.f11.accept(this, db);
		n.f14.accept(this, db);
		n.f15.accept(this, argu);
		  
		buffer += "END\n"; 
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
    public DataBlock visit(ClassDeclaration n, DataBlock argu){

    	DataBlock db = new DataBlock();
		db.name_flag = true;
    	
		DataBlock asnwer_db = n.f1.accept(this, db);
		current_class = asnwer_db.name;
		
        n.f3.accept(this, db);
        n.f4.accept(this, argu);
        
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
  	    
    public DataBlock visit(ClassExtendsDeclaration n, DataBlock argu) {
        
    	DataBlock db = new DataBlock();
    	db.name_flag = true;
    	
    	DataBlock answer_db;
    	
    	answer_db = n.f1.accept(this, db);
    	String name = answer_db.name;
    	current_class = name;
    	answer_db = n.f3.accept(this, db);
        
        n.f5.accept(this, db);
        n.f6.accept(this, argu);
        
        return null;
    }
    
    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public DataBlock visit(VarDeclaration n, DataBlock argu) {
    	DataBlock db = new DataBlock();
    	db.name_flag = true;
    	
    	n.f0.accept(this, db);
    	n.f1.accept(this, db);
    	
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
    public DataBlock visit(MethodDeclaration n, DataBlock argu) {
    	DataBlock answer_db;
    	DataBlock db = new DataBlock();
    	db.name_flag = true;
    	
		n.f1.accept(this, db);
		answer_db = n.f2.accept(this, db);
		String method_name = answer_db.name;
		
		MethodType method = Utils.symbolTables.get(current_class).lookupMethod(method_name);
		Integer params_num  = 0;
		current_method = method_name;
		
		if (method != null)
			params_num = method.getArgumensSize();
			
		buffer += Utils.getMethodLabel(current_class, method_name) + " [" + (params_num + 1) + "]" + "\n" + "BEGIN\n";
		
		n.f3.accept(this, argu);
		n.f4.accept(this, argu);
		n.f7.accept(this, argu);
		n.f8.accept(this, argu);
		answer_db = n.f10.accept(this, argu);
		
		buffer += "RETURN\n\t" + answer_db.temp + "\n";
		buffer += "END\n";
	  
		return null;
	}
    
    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public DataBlock visit(FormalParameter n, DataBlock argu) {
    	DataBlock db = new DataBlock();
    	db.name_flag = true;
    	n.f0.accept(this, db);
    	n.f1.accept(this, db);
    	return null;
    }

    /**
     * f0 -> Block()						//=== DONE ===//
     *       | AssignmentStatement()		//=== DONE ===//
     *       | ArrayAssignmentStatement()	//=== DONE ===//
     *       | IfStatement()				//=== DONE ===//
     *       | WhileStatement()				//=== DONE ===//
     *       | PrintStatement()				//=== DONE ===//
     */
    public DataBlock visit(Statement n, DataBlock argu) {
    	DataBlock db = new DataBlock();
    	db.load_flag = true;
    	
    	return n.f0.accept(this, db);
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public DataBlock visit(AssignmentStatement n, DataBlock argu) {
    	
    	DataBlock db = new DataBlock();
    	db.load_flag = true;
    	
    	DataBlock answer_db;
    	argu.lvalue = true;
    	
    	answer_db =  n.f0.accept(this, argu);
    	
    	String var_name = answer_db.name;
    	String var_temp = answer_db.temp;
       
    	answer_db = n.f2.accept(this, db);
    	String value = answer_db.temp;
       
    	// If local or method argument ~> Just MOVE
    	if (Utils.symbolTables.get(current_class).info.localCheck(current_method, var_name) != null || Utils.symbolTables.get(current_class).info.argCheck(current_method, var_name) != null){
    		buffer += "\tMOVE " + var_temp + " " + value + "\n";
    	}
		// If field ~> STORE
		else {
			String offset = Utils.symbolTables.get(current_class).getOffsetField(var_name);
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
    public DataBlock visit(ArrayAssignmentStatement n, DataBlock argu) {
    	DataBlock answer_db;
    	
    	answer_db = n.f0.accept(this, argu);
    	String array_address = answer_db.temp;
    	answer_db = n.f2.accept(this, argu);
    	String index_to_look = answer_db.temp;
    	String array_size = Utils.getTemp();
    	String in_bounds_label = Utils.getLabel();
    	String safe_label = Utils.getLabel();
    	answer_db = n.f5.accept(this, argu);
    	String value_to_assign = answer_db.temp;
    	
    	buffer += "\tHLOAD " + array_size + " " + array_address + " 0\n";	// Load array size from index 0
    	String minus = Utils.getTemp();
    	
    	// OutOfBounds Check
    	buffer += "\tMOVE " + minus + " 1";
    	String compare = Utils.getTemp();
    	buffer += "\tMOVE " + compare + " LT " + index_to_look + " " + array_size + "\n";	// index < array_size
    	String reverse = Utils.getTemp();
    	buffer += "\tMOVE " + reverse + " MINUS " + minus + " " + compare + "\n";
    	buffer += "\tCJUMP " + reverse + " " + in_bounds_label + "\n"; 
    	buffer += "\tERROR\n";
    	
    	buffer += in_bounds_label + "\tNOOP\n";
    	
    	// Negative Index Check
    	String zero = Utils.getTemp();
    	buffer += "\tMOVE " + zero + " 0\n";
    	String negative_compare = Utils.getTemp();
    	buffer += "\tMOVE " + negative_compare + " LT " + index_to_look + " " + zero + "\n";	// index < 0
    	buffer += "\tCJUMP " + negative_compare + " " + safe_label + "\n"; 
    	buffer += "\tERROR\n";
    	buffer += safe_label + "\tNOOP\n";
    	
    	// No Problem
    	String result = Utils.getTemp();
    	String test = Utils.getTemp();
    	
    	buffer += "\tMOVE " + test + " " + array_address + "\n";
    	buffer += "\tMOVE " + result + " TIMES " + index_to_look + " 4\n";
    	buffer += "\tMOVE " + result + " PLUS " + result + " 4\n";
    	buffer += "\tMOVE " + test + " PLUS " + test + " " + result + "\n";
    	buffer += "\tHSTORE " + test + " 0 " + value_to_assign + "\n";
    	
    	return null;
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
    public DataBlock visit(IfStatement n, DataBlock argu) {
    	String label = Utils.getLabel();
    	String label2 = Utils.getLabel();
    	
    	DataBlock answer_db;
    	answer_db = n.f2.accept(this, argu);
    	String cond_temp = answer_db.temp;
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
    public DataBlock visit(WhileStatement n, DataBlock argu) {
    	DataBlock answer_db;
    	
    	String top_label = Utils.getLabel();
    	String end_label = Utils.getLabel();
    	buffer += top_label + "\tNOOP\n";			// Start of while statement
    	
    	answer_db = n.f2.accept(this, argu);
    	String cond_temp = answer_db.temp;
    	
    	buffer += "\tCJUMP " + cond_temp + " " + end_label + "\n";
    	
    	answer_db = n.f4.accept(this, argu);
    	
    	buffer += "\tJUMP " + top_label + "\n";
    	buffer += end_label + "\tNOOP\n";			// End of while statement
    	
    	return null;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public DataBlock visit(PrintStatement n, DataBlock argu) {
       
    	DataBlock answer_db = n.f2.accept(this,argu);
    	String temp_to_print = answer_db.temp;
    	buffer += "\tPRINT " + temp_to_print + "\n";
    	return null;
    }

    /**
     * f0 -> AndExpression()			//=== DONE ===//
     *       | CompareExpression()		//=== DONE ===//
     *       | PlusExpression()			//=== DONE ===//
     *       | MinusExpression()		//=== DONE ===//
     *       | TimesExpression()		//=== DONE ===//
     *       | ArrayLookup()			//=== DONE ===//
     *       | ArrayLength()			//=== DONE ===//
     *       | MessageSend()			//=== DONE ===//
     *       | Clause()					//=== DONE ===//
     */
    public DataBlock visit(Expression n, DataBlock argu) {
    	DataBlock db = new DataBlock();
    	db.load_flag = true;
    	return n.f0.accept(this, db);
    }

    /**
     * f0 -> Clause()
     * f1 -> "&&"
     * f2 -> Clause()
     */
    public DataBlock visit(AndExpression n, DataBlock argu) {
    	DataBlock answer_db;
    	
    	String label = Utils.getLabel();
    	String result_temp = Utils.getTemp();
    	
    	answer_db = n.f0.accept(this, argu);
    	String lvalue_temp = answer_db.temp;
    	
    	buffer += "\tMOVE " + result_temp + " " + lvalue_temp + "\n";
    	buffer += "\tCJUMP " + result_temp + " " + label + "\n";
    	
    	answer_db = n.f2.accept(this, argu);
		String rvalue_temp = answer_db.temp;
    	
    	// branch not taken
    	buffer += "\tMOVE " + result_temp + " " + rvalue_temp + "\n";
    	
    	buffer += label + "\tNOOP\n";
    	
    	answer_db = new DataBlock();
 	   	answer_db.temp = result_temp;
        return answer_db;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public DataBlock visit(CompareExpression n, DataBlock argu) {
       
    	DataBlock answer_db;
    	
    	answer_db = n.f0.accept(this, argu);
    	String lvalue_temp = answer_db.temp;
        
		answer_db = n.f2.accept(this, argu);
		String rvalue_temp = answer_db.temp;
		
		String result_temp = Utils.getTemp();
       
		buffer += "\tMOVE " + result_temp + " LT " + lvalue_temp + " " + rvalue_temp + "\n";
	   
		answer_db = new DataBlock();
 	   	answer_db.temp = result_temp;
        return answer_db;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public DataBlock visit(PlusExpression n, DataBlock argu) {
    	DataBlock answer_db;
    	
    	answer_db = n.f0.accept(this, argu);
    	String lvalue_temp = answer_db.temp;
        
		answer_db = n.f2.accept(this, argu);
		String rvalue_temp = answer_db.temp;
		
		String result_temp = Utils.getTemp();
       
		buffer += "\tMOVE " + result_temp + " PLUS " + lvalue_temp + " " + rvalue_temp + "\n";
	   
		answer_db = new DataBlock();
 	   	answer_db.temp = result_temp;
        return answer_db;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public DataBlock visit(MinusExpression n, DataBlock argu) {
    	DataBlock answer_db;
    	
    	answer_db = n.f0.accept(this, argu);
    	String lvalue_temp = answer_db.temp;
        
		answer_db = n.f2.accept(this, argu);
		String rvalue_temp = answer_db.temp;
		
		String result_temp = Utils.getTemp();
		
		buffer += "\tMOVE " + result_temp + " MINUS " + lvalue_temp + " " + rvalue_temp + "\n";
		   
		answer_db = new DataBlock();
 	   	answer_db.temp = result_temp;
        return answer_db;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public DataBlock visit(TimesExpression n, DataBlock argu) {
    	DataBlock answer_db;
    	
    	answer_db = n.f0.accept(this, argu);
    	String lvalue_temp = answer_db.temp;
        
		answer_db = n.f2.accept(this, argu);
		String rvalue_temp = answer_db.temp;
				
        String result_temp = Utils.getTemp();
        
 	   	buffer += "\tMOVE " + result_temp + " TIMES " + lvalue_temp + " " + rvalue_temp + "\n";
 	   
 	   	answer_db = new DataBlock();
 	   	answer_db.temp = result_temp;
        return answer_db;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public DataBlock visit(ArrayLookup n, DataBlock argu) {
    	DataBlock answer_db;
    	answer_db = n.f0.accept(this, argu);
    	String array_address = answer_db.temp;
    	answer_db = n.f2.accept(this, argu);
    	String index_to_look = answer_db.temp;
    	String array_size = Utils.getTemp();
    	String in_bounds_label = Utils.getLabel();
    	String safe_label = Utils.getLabel();
    	
    	buffer += "\tHLOAD " + array_size + " " + array_address + " 0\n";	// Load array size from index 0
    	String minus = Utils.getTemp();
    	
    	// OutOfBounds Check
    	buffer += "\tMOVE " + minus + " 1";
    	String compare = Utils.getTemp();
    	buffer += "\tMOVE " + compare + " LT " + index_to_look + " " + array_size + "\n";	// index < array_size

    	String reverse = Utils.getTemp();
    	buffer += "\tMOVE " + reverse + " MINUS " + minus + " " + compare + "\n";
    	buffer += "\tCJUMP " + reverse + " " + in_bounds_label + "\n"; 
    	buffer += "\tERROR\n";
    	
    	buffer += in_bounds_label + "\tNOOP\n";
    	
    	// Negative Index Check
    	String zero = Utils.getTemp();
    	buffer += "\tMOVE " + zero + " 0\n";
    	String negative_compare = Utils.getTemp();
    	buffer += "\tMOVE " + negative_compare + " LT " + index_to_look + " " + zero + "\n";	// index < 0
    	buffer += "\tCJUMP " + negative_compare + " " + safe_label + "\n"; 
    	buffer += "\tERROR\n";
    	buffer += safe_label + "\tNOOP\n";
    	
    	// No Problem :)
    	String result = Utils.getTemp();
    	String bytes_off = Utils.getTemp();
    	buffer += "\tMOVE " + bytes_off + " TIMES " + index_to_look + " 4\n";
    	buffer += "\tMOVE " + bytes_off + " PLUS " + bytes_off + " 4\n";
    	buffer += "\tMOVE " + result + " PLUS " + array_address + " " + bytes_off + "\n";
    	buffer += "\tHLOAD " + result + " " + result + " 0\n";
    	
    	answer_db.temp = result;
    	return answer_db;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public DataBlock visit(ArrayLength n, DataBlock argu) {
    	
    	DataBlock answer_db = n.f0.accept(this, argu);
    	String array_address = answer_db.temp;
    	String size = Utils.getTemp();
    	buffer += "\tHLOAD " + size + " " + array_address + " 0\n";
    	answer_db.temp = size;
    	return answer_db;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public DataBlock visit(MessageSend n, DataBlock argu) {
    	
    	index ++;
    	call_temps2.add(index, new ArrayList<String>());
    	
    	DataBlock answer_db;
    	
    	String res_temp = Utils.getTemp();
        String v_temp = Utils.getTemp();
        String m_temp = Utils.getTemp();
        
    	answer_db = n.f0.accept(this, argu);
    	String object_temp = answer_db.temp;
    	String class_name = answer_db.belongs_to;
    	
    	DataBlock db = new DataBlock(); db.name_flag = true;
    	answer_db = n.f2.accept(this, db);
    	String method_name = answer_db.name;
    	String method_offset = Utils.symbolTables.get(class_name).info.getOffsetMethod(method_name);
    	String method_type = Utils.symbolTables.get(class_name).info.getMethodType(method_name);
    	

    	buffer += "\tHLOAD " + v_temp + " " + object_temp + " 0\n";
    	buffer += "\tHLOAD " + m_temp + " " + v_temp + " " + method_offset + "\n";
    	
    	n.f4.accept(this, argu);
    	
    	buffer += "\tMOVE " + res_temp + " CALL " + m_temp + "( " + object_temp + " ";
    	
    	// Build method call argument temps
		for (int i = 0; i < call_temps2.get(index).size(); i ++)
    		buffer += call_temps2.get(index).get(i) + " ";
    	
    	buffer += ")\n";
    	
    	// Clear for the next method call
    	call_temps2.remove(index);
    	index --;
    	
    	answer_db = new DataBlock();
    	answer_db.temp = res_temp;
    	answer_db.belongs_to = method_type;
    	
    	return answer_db;
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    public DataBlock visit(ExpressionList n, DataBlock argu) {
    	
    	DataBlock answer_db =  n.f0.accept(this, argu);
    	call_temps2.get(index).add(answer_db.temp);
    	
    	n.f1.accept(this, argu);
    	return null;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public DataBlock visit(ExpressionTerm n, DataBlock argu) {
    	
    	n.f0.accept(this, argu);
    	DataBlock answer_db =  n.f1.accept(this, argu);
    	call_temps2.get(index).add(answer_db.temp);
    	return null;
    }
    
    /**
     * f0 -> IntegerLiteral()	//=== DONE ===//
     *       | TrueLiteral()	//=== DONE ===//
     *       | FalseLiteral()	//=== DONE ===//
     *       | Identifier()		//=== DONE ===//
     *       | ThisExpression()	//=== DONE ===//
     *       | ArrayAllocationExpression()	//=== DONE ===//
     *       | AllocationExpression()		//=== DONE ===//
     *       | BracketExpression()			//=== DONE ===//
     */
    public DataBlock visit(PrimaryExpression n, DataBlock argu) {
       return n.f0.accept(this, argu);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public DataBlock visit(IntegerLiteral n, DataBlock argu) {
    	String int_temp = Utils.getTemp();
    	buffer += "\tMOVE " + int_temp + " " + n.f0.toString() + "\n";
    	DataBlock answer_db = new DataBlock();
    	answer_db.temp = int_temp;
    	return answer_db;
    }

    /**
     * f0 -> "true"
     */
    public DataBlock visit(TrueLiteral n, DataBlock argu) {
    	String bool_temp = Utils.getTemp();
    	buffer += "\tMOVE " + bool_temp + " 1\n";
    	DataBlock answer_db = new DataBlock();
    	answer_db.temp = bool_temp;
    	return answer_db;
    }

    /**
     * f0 -> "false"
     */
    public DataBlock visit(FalseLiteral n, DataBlock argu) {
    	String bool_temp = Utils.getTemp();
    	buffer += "\tMOVE " + bool_temp + " 0\n";
    	DataBlock answer_db = new DataBlock();
    	answer_db.temp = bool_temp;
    	return answer_db;
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public DataBlock visit(Identifier n, DataBlock argu) {
    	/*
    	 *Check[1]: Check if I just want the name of the identifier
    	 *Check[2]: Check if identifier is a "local variable"
    	 *Check[3]: Check if identifier is a "method argument"
    	 *Check[4]: Check if identifier is a "class field" 
    	*/
    	String name = n.f0.toString();
    	String temp = null;
    	
    	MethodType method;
    	
    	if (argu != null && (argu.getNameFlag())){
    		DataBlock answer_db = new DataBlock();
    		answer_db.name = name;
    		return answer_db;
    	}
    	else {
    		// Local Check
    		method = Utils.symbolTables.get(current_class).info.localCheck(current_method, name);
    		if (method == null){
    			
    			// Argument Check
    			method = Utils.symbolTables.get(current_class).info.argCheck(current_method, name);
    			if (method == null) {
    				
    				// Field Check
    				FieldType field = Utils.symbolTables.get(current_class).fieldCheck(name);
			    	
			    	if (field != null){
			    		if (argu != null && (argu.load_flag == true)){
			    			temp = Utils.getTemp();
			    			String offset = Utils.symbolTables.get(current_class).getOffsetField(name);
			    			String type = field.getType();
			    			
			    			// Load only if rvalue. ex. x = y: load y only
			    			if (argu.lvalue == false)
			    				buffer += "\tHLOAD " + temp + " TEMP 0 " + offset + "\n"; 
			    			
			    			return new DataBlock(name, temp, type);
			    		}
			    		else {
			    			temp = Utils.getTemp();
			    			return new DataBlock(name, temp, current_class);
			    		}
			    	}
    			}
    			else {
    				temp = method.getArguTemp(name);;
    				String type = method.getTypeOfParameter(name);
    				return new DataBlock(name, temp, type);
    			}
    		}
    		else {
    			temp = method.getLocalTemp(name);
    			String type = method.getTypeOfLocal(name);
    			return new DataBlock(name, temp, type);
    		}
    	}
    	return null;
    }
    
    /**
     * f0 -> "this"
     */
    public DataBlock visit(ThisExpression n, DataBlock argu) {
    	DataBlock answer_db = new DataBlock();
    	answer_db.temp = "TEMP 0";
    	answer_db.belongs_to = current_class;
    	return answer_db;
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public DataBlock visit(ArrayAllocationExpression n, DataBlock argu) {
    	DataBlock answer_db;
    	String no_error_label = Utils.getLabel();
    	
    	answer_db = n.f3.accept(this, argu);
    	String size_temp = answer_db.temp;
    	String size_check_temp = Utils.getTemp();
    	
    	// Check Possible negative array size
    	buffer += "\tMOVE " + size_check_temp + " LT " + size_temp + " 0\n";
    	buffer += "\tCJUMP " + size_check_temp + " " + no_error_label + "\n";
    	buffer += "\tERROR\n";
    	
    	// No error
    	buffer += no_error_label + "\tNOOP\n";
    	
    	String plus_one_temp = Utils.getTemp();
    	buffer += "\tMOVE " + plus_one_temp + " PLUS " + size_temp + " 1\n";	// Increase by 1 to store the size
    	String bytes_size = Utils.getTemp();
    	buffer += "\tMOVE " + bytes_size + " TIMES " + plus_one_temp + " 4\n";	// (size + 1) * 4
    	String arr_address = Utils.getTemp();
    	buffer += "\tMOVE " + arr_address + " HALLOCATE " + bytes_size + "\n";	// Allocate array
    	buffer += "\tHSTORE " + arr_address + " 0 " + size_temp + "\n";			// Store size at index 0
    	
    	String FINAL_arr_address = Utils.getTemp();
    	
    	// Array Initialization
    	String offset = Utils.getTemp();
    	buffer += "\tMOVE " + offset + " 4\n" ;	// start from index = 1
    	String loop_start = Utils.getLabel();
    	String loop_end = Utils.getLabel();
    	buffer += loop_start + "\tNOOP\n";		// label for the start of the loop
    	String end_check = Utils.getTemp();
    	buffer += "\tMOVE " + end_check + " LT " + offset + " " + bytes_size + "\n";					
    	buffer += "\tCJUMP " + end_check + " " + loop_end + "\n";	// branch taken ~> LOOP END
    	String init_temp = Utils.getTemp();
    	buffer += "\tMOVE " + init_temp + " 0\n";
    	
    	buffer += "\tMOVE " + FINAL_arr_address + " PLUS " + arr_address + " " + offset + "\n";
    	buffer += "\tHSTORE " + FINAL_arr_address + " 0 " + init_temp + "\n"; // set element to zero
    	
    	buffer += "\tMOVE " + offset + " PLUS " + offset + " 4\n";	// increase offset for the next array index
    	buffer += "\tJUMP " + loop_start + "\n";
    	buffer += loop_end + "\tNOOP\n";		// label for the end of the loop
    	
    	DataBlock db = new DataBlock();
    	db.temp = arr_address;
    	return db;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public DataBlock visit(AllocationExpression n, DataBlock argu) {
       
       String class_name = n.f1.f0.toString();
       Integer fields_bytes = (Utils.symbolTables.get(class_name).getMergedFieldsCount() +  1) * 4;
       
       String object_address = Utils.getTemp();
       String vTable_label = Utils.getTemp();
       String vTable_address = Utils.getTemp();
       
       // Allocate Object
       buffer += "\tMOVE " + object_address + " HALLOCATE " + fields_bytes + "\n" ;		// Allocate space for the object
       buffer += "\tMOVE " + vTable_label + " " + Utils.getGlobalLabel(class_name) + "\n";	// Get vTable label for this class
       buffer += "\tHLOAD " + vTable_address + " " + vTable_label + " 0\n";				// Load vTable using its label
       buffer += "\tHSTORE " + object_address + " 0 " + vTable_address + "\n";			// Store vTable address at object's 1st position
       
       // Initialize fields through Spiglet Loop
       String FINAL_obj_address = Utils.getTemp();
       String offset = Utils.getTemp();
       buffer += "\tMOVE " + offset + " 4\n" ;	// start from index = 1
       String loop_start = Utils.getLabel();
       String loop_end = Utils.getLabel();
       buffer += loop_start + "\tNOOP\n";		// label for the start of the loop
       String end_check = Utils.getTemp();
       buffer += "\tMOVE " + end_check + " LT " + offset + " " + fields_bytes + "\n";					
       buffer += "\tCJUMP " + end_check + " " + loop_end + "\n";	// branch taken ~> LOOP END
       String init_temp = Utils.getTemp();
       buffer += "\tMOVE " + init_temp + " 0\n";
		
       buffer += "\tMOVE " + FINAL_obj_address + " PLUS " + object_address + " " + offset + "\n";
       buffer += "\tHSTORE " + FINAL_obj_address + " 0 " + init_temp + "\n"; // set element to zero
		
       buffer += "\tMOVE " + offset + " PLUS " + offset + " 4\n";	// increase offset for the next array index
       buffer += "\tJUMP " + loop_start + "\n";
       buffer += loop_end + "\tNOOP\n";		// label for the end of the loop
		   
       DataBlock answer_db = new DataBlock();
       answer_db.temp = object_address;
       answer_db.belongs_to = class_name;
       
       return answer_db;	// Return address of the object allocated
    }

    /**
     * f0 -> "!"
     * f1 -> Clause()
     */
    public DataBlock visit(NotExpression n, DataBlock argu) {
    	String temp = Utils.getTemp();
    	String result = Utils.getTemp();
    	
    	DataBlock answer_db = n.f1.accept(this, argu);
    	
    	buffer += "\tMOVE " + temp + " 1\n";
    	buffer += "\tMOVE " + result + " MINUS " + temp + " " + answer_db.temp + "\n";
    	answer_db.temp = result;
    	
    	return answer_db;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public DataBlock visit(BracketExpression n, DataBlock argu) {
    	DataBlock answer_db = n.f1.accept(this, argu);
    	return answer_db;
    }
	    
}
