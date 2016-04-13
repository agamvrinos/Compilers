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

public class SymbolTableVisitor extends GJDepthFirst<String, String>{
		
		String current_class;
		Set<String> class_names; 		// phase1 class names
		List<String> method_parameter_names;
		List<String> method_parameter_types;
		SymbolTable table = null;
		
		public SymbolTableVisitor(Set<String> class_names) {
			this.class_names = class_names;
			Main.globalScope = new HashMap<String, SymbolTable>();
			Main.localScopes = new HashMap<SymbolTable, SymbolTable>();
			Main.mapping = new HashMap<String, Map<String, SymbolTable>>();
			method_parameter_names = new ArrayList<String>();
			method_parameter_types = new ArrayList<String>();
			
			Main.globals = new HashMap<String, String>();
		}
		
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
		
		void printMapping(){
			System.out.println("*****************************");
			System.out.println("Mapping");
			System.out.println("*****************************");
			for (Map.Entry<String, Map<String, SymbolTable>> entry : Main.mapping.entrySet()) {
	    	    String key = entry.getKey();
	    	    Map<String, SymbolTable> s = entry.getValue();
	    	    
	    	    System.out.println("ClassName: " + key);
	    	    for (Map.Entry<String, SymbolTable> entry2 : s.entrySet()) {
	    	    	String method_key = entry2.getKey();
	    	    	SymbolTable method_st = entry2.getValue();
	    	    	System.out.print("method: " + method_key + " method table: " + method_st.scope_name);
	    	    	System.out.println();
	    	    }
	    	   
	    	    System.out.println("------------------------");
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
		  printMapping();
		  
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
	    	
	    	String name = n.f1.f0.toString();
    		table = new SymbolTable();
	    	table.scope_name = name;
	    	
	    	Main.globalScope.put(name, table);
	    	Main.mapping.put(name, new HashMap<String, SymbolTable>());	// "classname" -> null
	    	Main.mapping.get(name).put("main", table);
	    	
	    	Main.globals.put(name, null);
	    	
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
	    	
	    	String name = n.f1.f0.toString();
    		table = new SymbolTable();
	    	table.scope_name = name;
	    	
	    	Main.globalScope.put(name, table);
	    	Main.mapping.put(name, new HashMap<String, SymbolTable>());	// "classname" -> null
	    	
	    	current_class = name;
	    	
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
	        
	    	String name = n.f1.f0.toString();
	    	String extended_name = n.f3.f0.toString();
	    	current_class = name;
	    	
	    	table = new SymbolTable();
	    	table.scope_name = name;
	    	
	    	Main.globalScope.put(name, table);
	    	Main.localScopes.put(table, Main.globalScope.get(extended_name));
	    	Main.mapping.put(name, new HashMap<String, SymbolTable>());	// "classname" -> null
	    	
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
	    
	    /**
	     * f0 -> Type()
	     * f1 -> Identifier()
	     * f2 -> ";"
	    */
	    public String visit(VarDeclaration n, String argu) {
	    	
	       String var_type = n.f0.accept(this, argu);
	       String var_name = n.f1.accept(this, argu);
	       n.f2.accept(this, argu);
	       
	       // if type = classType
	       if (!var_type.equals("int") && !var_type.equals("int[]") && !var_type.equals("boolean")){
	    	// then check for possible forward declaration
	    	   if (!class_names.contains(var_type))				
	    		   // no forward declaration
	    		   throw new RuntimeException(var_type + " cannot be resolved to a type");	
	       }
	       
	       if (argu != null)
		       if (argu.equals("method"))
		    	   if (method_parameter_names.contains(var_name))
		    		   throw new RuntimeException("Duplicate local variable " + var_name);	
	       
	       SymbolType t = new SymbolType("variable", var_name, var_type);
	       
	       
	       if (!table.insert(t))
	    	   throw new RuntimeException("Variable Redeclaration Error");	// same type name case
	       
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
	       n.f0.accept(this, argu);
	       String method_type = n.f1.accept(this, argu);
	       String method_name = n.f2.accept(this, argu);
	       n.f3.accept(this, argu);
	       String val = n.f4.accept(this, argu);
	       
	       if (val == null){
	    	   System.out.println("no method parameters");
	       }
	       
	       
	       // check for same method from parent (inheritance case)
	       if (argu != null){
	    	   SymbolTable parent = Main.localScopes.get(Main.globalScope.get(argu));
	    	   SymbolType parentType = parent.lookup(method_name, "method");
	    	   
	    	   // an vrika method ston patera me to idio onoma
	    	   if (parentType != null){
	    		   if (parentType.type.equals(method_type)){
	    			   if (parentType.parameters.size() == method_parameter_types.size()){
	    				   for (int i = 0; i < parentType.parameters.size(); i++){
	    					   if (!parentType.parameters.get(i).equals(method_parameter_types.get(i)))
	    						   throw new RuntimeException("Error at inheritance method");
	    				   }
	    			   }
	    			   else throw new RuntimeException("Error at inheritance method");
	    		   }
	    		   else throw new RuntimeException("Error at inheritance method");
	    	   }
	    		  
	    	   
	    	   System.out.println(parent.scope_name);
	       }
	       
	       SymbolType t = new SymbolType("method", method_name, method_type, method_parameter_types);	// create method type
	       
	       if (!table.insert(t))											// insert method type to cur scope
	    	   throw new RuntimeException("Method Redeclaration Error");	// same method name case
	       
	       table = table.enterScope(method_name);	
	       
	       // create "method_name" -> methodST mapping
	       Main.mapping.get(current_class).put(method_name, table);
	       
	       for (int i = 0; i < method_parameter_names.size(); i++){
	    	   table.insert(new SymbolType("variable", method_parameter_names.get(i), method_parameter_types.get(i)));
	       }
	       
	       n.f5.accept(this, argu);
	       n.f6.accept(this, argu);
	       n.f7.accept(this, "method");
	       n.f8.accept(this, argu);
	       n.f9.accept(this, argu);
	       n.f10.accept(this, argu);
	       n.f11.accept(this, argu);
	       n.f12.accept(this, argu);
	       
	       
	       table = table.exitScope();
	       
	       method_parameter_names.clear();	// empty set for next method
	       method_parameter_types.clear();
	       
	       return null;
	    }
	    //////////////////////////////////////////////////////////////////////////
	    /**
	     * f0 -> FormalParameter()
	     * f1 -> FormalParameterTail()
	     */
	    public String visit(FormalParameterList n, String argu) {
	       n.f0.accept(this, argu);
	       n.f1.accept(this, argu);
	       
	       return null;
	    }
	    //////////////////////////////////////////////////////////////////////////
	    /**
	     * f0 -> Type()
	     * f1 -> Identifier()
	     */
	    public String visit(FormalParameter n, String argu) {
	    	
	       String parameter_type = n.f0.accept(this, argu);	// we only need the type
	       String parameter_name = n.f1.accept(this, argu);
	       
	       if (method_parameter_names.contains(parameter_name))		// checking for duplicate parameters in
	    	   throw new RuntimeException("Duplicate parameter " + parameter_name);	// method declaration
	       else {
	    	   method_parameter_names.add(parameter_name);	// keep it to check redeclaration of parameters
	    	   method_parameter_types.add(parameter_type);
	       }
	       
	       return null;
	    }
	    //////////////////////////////////////////////////////////////////////////
	    /**
	     * f0 -> ( FormalParameterTerm() )*
	     */
	    public String visit(FormalParameterTail n, String argu) {
	       return n.f0.accept(this, argu);
	    }
	    //////////////////////////////////////////////////////////////////////////
	    /**
	     * f0 -> ","
	     * f1 -> FormalParameter()
	     */
	    public String visit(FormalParameterTerm n, String argu) {
	       n.f0.accept(this, argu);
	       String parameter_type = n.f1.accept(this, argu);
	       return null;
	    }
	    //////////////////////////////////////////////////////////////////////////
	    /**
	     * f0 -> <IDENTIFIER>
	    */
	    public String visit(Identifier n, String argu) {
	    	n.f0.accept(this, argu);
	    	return n.f0.toString();
	    }
	    //////////////////////////////////////////////////////////////////////////
	    /**
	     * f0 -> "boolean"
	    */
	    public String visit(BooleanType n, String argu) {
	       n.f0.accept(this, argu);
	       return n.f0.toString();
	    }
	    
	    /**
	     * f0 -> "int"
	    */
	    public String visit(IntegerType n, String argu) {
	       n.f0.accept(this, argu);
	       return n.f0.toString();
	    }
	    /**
	     * f0 -> "int"
	     * f1 -> "["
	     * f2 -> "]"
	    */
	    public String visit(ArrayType n, String argu) {
	       String s = n.f0.toString();
	       s = s + n.f1.toString();
	       s = s + n.f2.toString();
	    		   
	       n.f0.accept(this, argu);
	       n.f1.accept(this, argu);
	       n.f2.accept(this, argu);
	       return s;
	    }
	    
	    /**
	     * f0 -> ArrayType()
	     *       | BooleanType()
	     *       | IntegerType()
	     *       | Identifier()
	    */
	    public String visit(Type n, String argu) {
	    	
	       String s = n.f0.accept(this, argu);
	       
	       return s;
	    }
	   
}
