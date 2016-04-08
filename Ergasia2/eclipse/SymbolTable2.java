import java.util.*;

public class SymbolTable2 {
	
	SymbolTable2 parent;
	Map<String, Set<SymbolType>> sym_table;
	String scopeName;	// 1 to N match so i keep it here
	
	public SymbolTable2(String scope) {
		parent = null;
		sym_table = new HashMap<>();
		scopeName = scope;
	}
	
	public SymbolTable2(String scope, SymbolTable2 p) {
		parent = p;
		scopeName = scope;
		sym_table = new HashMap<>();
	}
	
	SymbolTable2 enterScope(String scope){
		SymbolTable2 new_table = new SymbolTable2(scope, this);
		return new_table;
	}
	
	boolean insert(SymbolType type){
		if (sym_table.containsKey(scopeName)){				// uparxei to key
			
			Set<SymbolType> syms= sym_table.get(scopeName);
			
			for (SymbolType t: syms){
				if (t.name.equals(type.name) && t.kind.equals(type.kind)){
					System.out.println("Variable name already exists at " + scopeName + " scope");
					System.out.println("Redeclaration");	
					return false;
				}
			}
			sym_table.get(scopeName).add(type);				// ara vale sto uparxon value to new element
		}
		else {
			Set<SymbolType> val = new HashSet<SymbolType>();	// den uparxei
			val.add(type);										// ara ftiakse new kai vale ekei tin timi
			
			sym_table.put(scopeName,val);
		}
		
		return true;
	}	
	
	boolean lookup(String name){
		
		SymbolTable2 temp = this;
		
		while (temp != null){
			Set<SymbolType> syms = temp.sym_table.get(temp.scopeName);
			
			for (SymbolType type: syms){
				if (type.name.equals(name)){
					System.out.println("Found it in " + temp.scopeName + " scope");
					return true;
				}
			}
			System.out.println("DID NOT Found it in " + temp.scopeName + " scope");
			temp = temp.parent;
		}
		
		return false;
	}
	
	SymbolTable2 exitScope(){
		return parent;
	}
	
	void printSymbolTable(){
	      
	      for (Map.Entry<String, Set<SymbolType>> entry : sym_table.entrySet()) {
	    	    String key = entry.getKey();
	    	    Set<SymbolType> val = entry.getValue();
	    	    
	    	    System.out.println("------------------------");
	    	    System.out.println("Scope: " + key);
	    	    System.out.println("------------------------");
	    	    
	    	    for (SymbolType type : val){
	    	    	type.printType();
	    	    }
	    	    System.out.println("=========================");
	    	}
	}
}
