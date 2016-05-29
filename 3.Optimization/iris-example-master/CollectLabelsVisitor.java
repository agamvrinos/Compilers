import java.util.*;
import syntaxtree.*;
import visitor.GJDepthFirst;

public class CollectLabelsVisitor extends GJDepthFirst<Map<String, Integer>, String>{

		Map<String, Integer> labels_index;
		Integer counter = 0;
		
		public CollectLabelsVisitor() {
			labels_index = new HashMap<>();
		}
		
	   /**
	    * f0 -> "MAIN"
	    * f1 -> StmtList()
	    * f2 -> "END"
	    * f3 -> ( Procedure() )*
	    * f4 -> <EOF>
	    */
	   public Map<String, Integer> visit(Goal n, String argu) {
		   	n.f1.accept(this, argu);
		   	n.f3.accept(this, argu);
		   	
		   	return labels_index;
	   }

	   /**
	    * f0 -> ( ( Label() )? Stmt() )*
	    */
	   public Map<String, Integer> visit(StmtList n, String argu) {
		   return n.f0.accept(this, "label");
	   }
	   
	   /**
	    * f0 -> Label()
	    * f1 -> "["
	    * f2 -> IntegerLiteral()
	    * f3 -> "]"
	    * f4 -> StmtExp()
	    */
	   public Map<String, Integer> visit(Procedure n, String argu) {
	      counter = 0;
	      n.f2.accept(this, argu);
	      n.f4.accept(this, argu);
	      return null;
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
	   public Map<String, Integer> visit(Stmt n, String argu) {
		   n.f0.accept(this, null);
		   counter ++;
		   return null;
	   }

	   /**
	    * f0 -> "BEGIN"
	    * f1 -> StmtList()
	    * f2 -> "RETURN"
	    * f3 -> SimpleExp()
	    * f4 -> "END"
	    */
	   public Map<String, Integer> visit(StmtExp n, String argu) {
		   n.f1.accept(this, argu);
		   n.f3.accept(this, argu);
		   counter ++;
		   return null;
	   }

	   /**
	    * f0 -> <IDENTIFIER>
	    */
	   public Map<String, Integer> visit(Label n, String argu) {
		   String name = n.f0.toString();
		   if (argu != null && argu.equals("label")){
			   System.out.println("ADDING name: " + name + " to index: " + (counter + 1));
			   labels_index.put(name, counter + 1);
		   }
		   return null;
	   }
}