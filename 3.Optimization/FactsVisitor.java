import java.util.*;
import syntaxtree.*;
import visitor.GJDepthFirst;

public class FactsVisitor extends GJDepthFirst<String, String>{
	
		static String instructions = "";
		static String nexts = "";
		static String vars = "";
		static String varMoves = "";
		static String constMoves = "";
		static String varUses = "";
		static String varDefs = "";
		
		List<String> call_args;
		Map<String, Integer> labels_index;
		
		Integer mainSize = 0;
		String current_method;
		String label_def;
		
		public FactsVisitor(Map <String, Integer> labels_index) {
			this.labels_index = labels_index;
			call_args = new ArrayList<>();
			label_def = "";
		}
		
		
	   /**
	    * f0 -> "MAIN"
	    * f1 -> StmtList()
	    * f2 -> "END"
	    * f3 -> ( Procedure() )*
	    * f4 -> <EOF>
	    */
	   public String visit(Goal n, String argu) {
		   
		   current_method = "MAIN";
		  
		   n.f1.accept(this, argu);
		   n.f3.accept(this, argu);
		   
//		   System.out.println(instructions);
//		   System.out.println(nexts);
//		   System.out.println(vars);
//		   System.out.println(varMoves);
//		   System.out.println(constMoves);
//		   System.out.println(varUses);
//		   System.out.println(varDefs);
		   
		   return null;
	   }

	   /**
	    * f0 -> ( ( Label() )? Stmt() )*
	    */
	   public String visit(StmtList n, String argu) {
		   if (current_method.equals("MAIN"))
			   mainSize = n.f0.size();
		   
		   return n.f0.accept(this, "label");
	   }

	   /**
	    * f0 -> Label()
	    * f1 -> "["
	    * f2 -> IntegerLiteral()
	    * f3 -> "]"
	    * f4 -> StmtExp()
	    */
	   public String visit(Procedure n, String argu) {
	      String _ret=null;
	      
	      Utils.resetCounter();
	      current_method = n.f0.f0.toString();
//	      System.out.println("Current method: " + current_method);
	      n.f2.accept(this, argu);
	      n.f4.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> NoOpStmt()
	    *       | ErrorStmt()
	    *       | CJumpStmt()
	    *       | JumpStmt()
	    *       | HStoreStmt()
	    *       | HLoadStmt()
	    *       | MoveStmt()
	    *       | PrintStmt()
	    */
	   public String visit(Stmt n, String argu) {
		   String stmt = n.f0.accept(this, argu);

		   stmt = "instruction(\"" + current_method + "\", " + Utils.getNextCounter() + ", \"" + label_def + stmt + "\").\n";
		   instructions += stmt;
		   label_def = "";
		   
		   return stmt;
	   }

	   /**
	    * f0 -> "NOOP"
	    */
	   public String visit(NoOpStmt n, String argu) {
		   String ret;
		   
		   ret = "NOOP";
		   
		   // if main method and this statement is the last one, SKIP next
		   if (current_method.equals("MAIN") && (Utils.getFakeNextCounter() == mainSize))
			   return ret;
		   
		   nexts += "next(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", " + Utils.getFakeNextNextCounter() + ").\n";
		   return ret;
	   }

	   /**
	    * f0 -> "ERROR"
	    */
	   public String visit(ErrorStmt n, String argu) {
		   	String ret;
		   
		   	ret = "ERROR";
		   	
		   	return ret;
	   }

	   /**
	    * f0 -> "CJUMP"
	    * f1 -> Temp()
	    * f2 -> Label()
	    */
	   public String visit(CJumpStmt n, String argu) {
		   String ret;

		   String temp = n.f1.accept(this, argu);
		   String label = n.f2.f0.toString();
		   
		   ret = "CJUMP " + temp + " " + label;
		   
		   varUses += "varUse(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + temp + "\").\n"; 
		   
		   nexts += "next(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", " + labels_index.get(label) + ").\n";

		   // if main method and this statement is the last one, SKIP next
		   if (current_method.equals("MAIN") && (Utils.getFakeNextCounter() == mainSize))
			   return ret;
		   
		   nexts += "next(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", " + (Utils.getFakeNextCounter() + 1) + ").\n";

		   return ret;
	   }

	   /**
	    * f0 -> "JUMP"
	    * f1 -> Label()
	    */
	   public String visit(JumpStmt n, String argu) {
		   String ret;
		   
		   String label = n.f1.f0.toString();
		   
		   ret = "JUMP " + label;
		   
		   nexts += "next(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", " + labels_index.get(label) + ").\n";
		   
		   return ret;
	   }

	   /**
	    * f0 -> "HSTORE"
	    * f1 -> Temp()
	    * f2 -> IntegerLiteral()
	    * f3 -> Temp()
	    */
	   public String visit(HStoreStmt n, String argu) {
		   String ret;
		   String temp1 = n.f1.accept(this, argu);
		   String value = n.f2.accept(this, argu);
		   String temp2 = n.f3.accept(this, argu);
		   
		   ret = "HSTORE " + temp1 + " " + value + " " + temp2;
		   
		   varUses += "varUse(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + temp1 + "\").\n"; 
		   varUses += "varUse(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + temp2 + "\").\n"; 
		   
		   // if main method and this statement is the last one, SKIP next
		   if (current_method.equals("MAIN") && (Utils.getFakeNextCounter() == mainSize))
			   return ret;
		   	
		   nexts += "next(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", " + Utils.getFakeNextNextCounter() + ").\n";
		   
		   return ret;
	   }

	   /**
	    * f0 -> "HLOAD"
	    * f1 -> Temp()
	    * f2 -> Temp()
	    * f3 -> IntegerLiteral()
	    */
	   public String visit(HLoadStmt n, String argu) {
		   String ret;
		   String temp1 = n.f1.accept(this, argu);
		   String temp2 = n.f2.accept(this, argu);
		   String value = n.f3.accept(this, argu);
		   
		   ret = "HLOAD " + temp1 + " " + temp2 + " " + value;
		   
		   varDefs += "varDef(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + temp1 + "\").\n"; 
		   varUses += "varUse(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + temp2 + "\").\n"; 
		   
		   // if main method and this statement is the last one, SKIP next
		   if (current_method.equals("MAIN") && (Utils.getFakeNextCounter() == mainSize))
			   return ret;
		   	
		   nexts += "next(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", " + Utils.getFakeNextNextCounter() + ").\n";
	      
		   
		   
		   return ret;
	   }

	   /**
	    * f0 -> "MOVE"
	    * f1 -> Temp()
	    * f2 -> Exp()
	    */
	   public String visit(MoveStmt n, String argu) {
		   String ret;
		   String temp = n.f1.accept(this, argu);
		   String exp = n.f2.accept(this, argu);
		   
		   ret = "MOVE " + temp + " " + exp;
		   
		   varDefs += "varDef(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + temp + "\").\n"; 
		   if (exp.contains("TEMP") && exp.split(" ").length == 2){
			   varMoves += "varMove(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + temp + "\", \"" + exp + "\").\n";
			   varUses += "varUse(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + exp + "\").\n"; 
		   }
		   
		   if (!exp.contains("TEMP") && exp.split(" ").length == 1 && exp.contains("_"))
			   exp = "\"" + exp + "\"";
		   
		   if (!exp.contains("TEMP") && exp.split(" ").length == 1)
			   constMoves += "constMove(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + temp + "\", " + exp + ").\n";

		   
		   // if main method and this statement is the last one, SKIP next
		   if (current_method.equals("MAIN") && (Utils.getFakeNextCounter() == mainSize))
			   return ret;
		   	
		   nexts += "next(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", " + Utils.getFakeNextNextCounter() + ").\n";
		   
		   return ret;
	   }

	   /**
	    * f0 -> "PRINT"
	    * f1 -> SimpleExp()
	    */
	   public String visit(PrintStmt n, String argu) {
		   String ret;
	      
		   String temp = n.f1.accept(this, argu);
		   
		   ret = "PRINT " + temp;
		   
		   varUses += "varUse(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + temp + "\").\n";
		   
		   // if main method and this statement is the last one, SKIP next
		   if (current_method.equals("MAIN") && (Utils.getFakeNextCounter() == mainSize))
			   return ret;
		   
		   nexts += "next(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", " + Utils.getFakeNextNextCounter() + ").\n";
		   
		   
		   return ret;
	   }

	   /**
	    * f0 -> Call()
	    *       | HAllocate()
	    *       | BinOp()
	    *       | SimpleExp()
	    */
	   public String visit(Exp n, String argu) {
		   return n.f0.accept(this, argu);
	   }

	   /**
	    * f0 -> "BEGIN"
	    * f1 -> StmtList()
	    * f2 -> "RETURN"
	    * f3 -> SimpleExp()
	    * f4 -> "END"
	    */
	   public String visit(StmtExp n, String argu) {
		   String ret;
	      
		   n.f0.accept(this, argu);
		   n.f1.accept(this, argu);
		   n.f2.accept(this, argu);
		   String retval = n.f3.accept(this, argu);
		   n.f4.accept(this, argu);

		   varUses += "varUse(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + retval + "\").\n";
		   
		   ret = "instruction(\"" + current_method + "\", " + Utils.getNextCounter() + ", " + 
				   "\"RETURN " + retval + "\").\n";
		    
		   instructions += ret;
	      
		   return ret;
	   }

	   /**
	    * f0 -> "CALL"
	    * f1 -> SimpleExp()
	    * f2 -> "("
	    * f3 -> ( Temp() )*
	    * f4 -> ")"
	    */
	   public String visit(Call n, String argu) {
	      
		   String call_ret = "CALL ";
		   String address = n.f1.accept(this, argu);
		   call_ret += address + "( ";
	      
		   n.f3.accept(this, "arg");
		   
		   varUses += "varUse(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + address + "\").\n";
		   
		   for (int i = 0; i < call_args.size(); i ++){
			   call_ret += call_args.get(i) + " ";
			   varUses += "varUse(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + call_args.get(i) + "\").\n";
		   }
	      
		   call_ret += ")";
	      
		   call_args.clear();
		   return call_ret;
	   }

	   /**
	    * f0 -> "HALLOCATE"
	    * f1 -> SimpleExp()
	    */
	   public String visit(HAllocate n, String argu) {
	      
		   String value = n.f1.accept(this, argu);
		   
		   if (value.contains("TEMP"))
			   varUses += "varUse(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + value + "\").\n";
		   
		   return "HALLOCATE " + value;
	   }

	   /**
	    * f0 -> Operator()
	    * f1 -> Temp()
	    * f2 -> SimpleExp()
	    */
	   public String visit(BinOp n, String argu) {
		   String operator = n.f0.accept(this, argu);
		   String temp = n.f1.accept(this, argu);
		   String exp = n.f2.accept(this, argu);
		   
		   varUses += "varUse(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + temp + "\").\n";
		   varUses += "varUse(\"" + current_method + "\", " + Utils.getFakeNextCounter() + ", \"" + exp + "\").\n";
	      
		   return operator + " " + temp + " " + exp;
	      
	   }

	   /**
	    * f0 -> "LT"
	    *       | "PLUS"
	    *       | "MINUS"
	    *       | "TIMES"
	    */
	   public String visit(Operator n, String argu) {
		   return n.f0.choice.toString();
	   }

	   /**
	    * f0 -> Temp()
	    *       | IntegerLiteral()
	    *       | Label()
	    */
	   public String visit(SimpleExp n, String argu) {
		   return n.f0.accept(this, null);
	   }

	   /**
	    * f0 -> "TEMP"
	    * f1 -> IntegerLiteral()
	    */
	   public String visit(Temp n, String argu) {
		   String tempNumber = n.f1.accept(this, argu);
		   String total_temp = "TEMP " + tempNumber;
		   
		   // method arguments case
		   if (argu != null && argu.equals("arg"))
			   call_args.add(total_temp);
		   
		   // no duplicates condition
		   if (!vars.contains("var(\"" + current_method + "\", \"" + total_temp + "\").\n"))
			   vars += "var(\"" + current_method + "\", \"" + total_temp + "\").\n"; 
		   
		   return total_temp;
	   }

	   /**
	    * f0 -> <INTEGER_LITERAL>
	    */
	   public String visit(IntegerLiteral n, String argu) {
		   return n.f0.toString();
	   }

	   /**
	    * f0 -> <IDENTIFIER>
	    */
	   public String visit(Label n, String argu) {
		   String name = n.f0.toString();
		   if (argu != null && argu.equals("label")){
//			   System.out.println(argu + ": " + name);
			   label_def = name + " ";
//			   System.out.println("ADDING name: " + name + " to index: " + Utils.getFakeNextCounter());
		   }
		   return name;
	   }
}