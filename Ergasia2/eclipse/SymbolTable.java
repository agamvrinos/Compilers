import java.util.*;

public class SymbolTable {
	
	SymbolTable parent;
	Map<String, Set<SymbolType>> sym_table;
	String scopeName;	// 1 to N match so i keep it here
	
	public SymbolTable(String scope) {
		parent = null;
		sym_table = new HashMap<>();
		scopeName = scope;
	}
	
	public SymbolTable(String scope, SymbolTable p) {
		parent = p;
		scopeName = scope;
		sym_table = new HashMap<>();
	}
	
	SymbolTable enterScope(String scope){
		SymbolTable new_table = new SymbolTable(scope, this);
		return new_table;
	}
	
	boolean insert(SymbolType type){
		if (sym_table.containsKey(scopeName)){		// uparxei to key
			
			if (sym_table.get(scopeName).contains(type)){	// an to value pou paw na valw uparxei
				System.out.println("Redeclaration");		// redeclaration error
				return false;
			}
			sym_table.get(scopeName).add(type);		// ara vale sto uparxon value to new element
		}
		else {
			Set<SymbolType> val = new HashSet<SymbolType>();	// den uparxei
			val.add(type);							// ara ftiakse new kai vale ekei tin timi
			
			sym_table.put(scopeName,val);
		}
		
		return true;
	}	
	
	boolean lookup(String type){
//		while (parent != null){
//			String scope = sym_table.getKey();
//		}
		return false;
	}
	
	SymbolTable exitScope(){
		return parent;
	}
	
	void printSymbolTable(){
	      
	      for (Map.Entry<String, Set<SymbolType>> entry : sym_table.entrySet()) {
	    	    String key = entry.getKey();
	    	    Set<SymbolType> val = entry.getValue();
	    	    
	    	    System.out.println("Scope: " + key);
	    	    System.out.println("------------------------");
	    	    
	    	    for (SymbolType type : val){
	    	    	type.printType();
	    	    }
	    	    System.out.println("=========================");
	    	}
	}
}
