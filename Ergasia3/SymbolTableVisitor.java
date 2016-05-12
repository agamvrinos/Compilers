import java.util.*;
import syntaxtree.*;
import visitor.GJDepthFirst;
import java.util.List;

public class SymbolTableVisitor extends GJDepthFirst<String, String>{
		
		SymbolTable table;
		List <FieldType> parameters;
		List <FieldType> locals;
		
		public SymbolTableVisitor() {
			Utils.symbolTables = new HashMap<>();
			parameters = new ArrayList<>();
			locals = new ArrayList<>();
		}
		
		/**
	    * f0 -> MainClass()
	    * f1 -> ( TypeDeclaration() )*
	    * f2 -> <EOF>
	    */
	   public String visit(Goal n, String argu) {
	      String _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      n.f2.accept(this, argu);
	      return _ret;
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
	      
	      
	      String main_name = n.f1.accept(this, argu);
	      table = new SymbolTable(main_name);
//	      Main.symbolTables.put(main_name, table);
	      n.f11.accept(this, argu);
	      n.f14.accept(this, "local_var");
	      n.f15.accept(this, argu);
	      
	      
	      List<FieldType> loc = new ArrayList<>(); loc.addAll(locals);
	      MethodType hardcoded_method = new MethodType("main", "public static void", null, loc);
	      table.addMethod(hardcoded_method);
	      
	      locals.clear();
	      
	      return null;
	   }

	   /**
	    * f0 -> ClassDeclaration()
	    *       | ClassExtendsDeclaration()
	    */
	   public String visit(TypeDeclaration n, String argu) {
	      return n.f0.accept(this, argu);
	   }

	   /**
	    * f0 -> "class"
	    * f1 -> Identifier()
	    * f2 -> "{"
	    * f3 -> ( VarDeclaration() )*
	    * f4 -> ( MethodDeclaration() )*
	    * f5 -> "}"
	    */
	   public String visit(ClassDeclaration n, String argu) {
	      String _ret=null;
	      String class_name = n.f1.accept(this, argu);
	      table = new SymbolTable(class_name);
//	      Main.symbolTables.put(class_name, table);
	      n.f3.accept(this, "class_field");
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
	   public String visit(ClassExtendsDeclaration n, String argu) {
	      String _ret=null;
	      String class_name = n.f1.accept(this, argu);
	      String extends_name = n.f3.accept(this, argu);
//	      table = new SymbolTable(class_name, table);
	      table = table.enterScope(class_name);
//	      Main.symbolTables.put(class_name, table);
	      
	      n.f5.accept(this, "class_field");
	      n.f6.accept(this, argu);
	      
	      
	      
	      return _ret;
	   }

	   /**
	    * f0 -> Type()
	    * f1 -> Identifier()
	    * f2 -> ";"
	    */
	   public String visit(VarDeclaration n, String argu) {
	      String _ret=null;
	      String var_type = n.f0.accept(this, argu);
	      String var_name = n.f1.accept(this, argu);
	      
	      // If called from Class visit method
	      if (argu != null && argu.equals("class_field")){
	    	  table.addField(new FieldType(var_name, var_type));
	      }
	      
	      if (argu != null && argu.equals("local_var")){
	    	  locals.add(new FieldType(var_name, var_type));
	      }
	      
	      return _ret;
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
	      
		  String method_type = n.f1.accept(this, argu);
	      String method_name = n.f2.accept(this, argu);
	      n.f4.accept(this, argu);
	      n.f7.accept(this, "local_var");
	      n.f8.accept(this, argu);
	      n.f10.accept(this, argu);
	      
	      List<FieldType> par = new ArrayList<>(); par.addAll(parameters);
	      List<FieldType> loc = new ArrayList<>(); loc.addAll(locals);
	      MethodType method = new MethodType(method_name, method_type, par, loc);
	      table.addMethod(method);
	      
	      parameters.clear();
	      locals.clear();
	      
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
	      
	      String par_type = n.f0.accept(this, argu);
	      String par_name = n.f1.accept(this, argu);
	      
	      parameters.add(new FieldType(par_name, par_type));
	      return null;
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
	      return "int[]";
	   }

	   /**
	    * f0 -> "boolean"
	    */
	   public String visit(BooleanType n, String argu) {
	      return "boolean";
	   }

	   /**
	    * f0 -> "int"
	    */
	   public String visit(IntegerType n, String argu) {
		  return "int";
	   }

	   /**
	    * f0 -> Block()
	    *       | AssignmentStatement()
	    *       | ArrayAssignmentStatement()
	    *       | IfStatement()
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
	      String _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      n.f2.accept(this, argu);
	      n.f3.accept(this, argu);
	      return _ret;
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
	      String _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      n.f2.accept(this, argu);
	      n.f3.accept(this, argu);
	      n.f4.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> AndExpression()
	    *       | CompareExpression()
	    *       | PlusExpression()
	    *       | MinusExpression()
	    *       | TimesExpression()
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
	      String _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      n.f2.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "<"
	    * f2 -> PrimaryExpression()
	    */
	   public String visit(CompareExpression n, String argu) {
	      String _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      n.f2.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "+"
	    * f2 -> PrimaryExpression()
	    */
	   public String visit(PlusExpression n, String argu) {
	      String _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      n.f2.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "-"
	    * f2 -> PrimaryExpression()
	    */
	   public String visit(MinusExpression n, String argu) {
	      String _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      n.f2.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "*"
	    * f2 -> PrimaryExpression()
	    */
	   public String visit(TimesExpression n, String argu) {
	      String _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      n.f2.accept(this, argu);
	      return _ret;
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
	      String _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      n.f2.accept(this, argu);
	      n.f3.accept(this, argu);
	      n.f4.accept(this, argu);
	      n.f5.accept(this, argu);
	      return _ret;
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
	    * f0 -> IntegerLiteral()
	    *       | TrueLiteral()
	    *       | FalseLiteral()
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
	      return n.f0.accept(this, argu);
	   }

	   /**
	    * f0 -> "true"
	    */
	   public String visit(TrueLiteral n, String argu) {
	      return n.f0.accept(this, argu);
	   }

	   /**
	    * f0 -> "false"
	    */
	   public String visit(FalseLiteral n, String argu) {
	      return n.f0.accept(this, argu);
	   }

	   /**
	    * f0 -> <IDENTIFIER>
	    */
	   public String visit(Identifier n, String argu) {
	      return n.f0.toString();
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
	      String _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      n.f2.accept(this, argu);
	      n.f3.accept(this, argu);
	      return _ret;
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
