import java.util.*;

public class SymbolTable {
	
	Map<String, Set<SymbolType>> sym_table;
	public String scope_name;
	
	public SymbolTable() {
		sym_table = new HashMap<>();
		Main.localScopes.put(this, null);	// o pateras tha dixnei se null
	}
	
	public SymbolTable(String scope_name) {
		sym_table = new HashMap<>();
		this.scope_name = scope_name;
	}
	
	SymbolTable enterScope(String new_scope_name){
		
		SymbolTable new_table = new SymbolTable(new_scope_name);	// new kid symbol table
		Main.localScopes.put(new_table, this);						// vazw to new table na dixnei ston patera
		
		return new_table;
	}
	
	boolean insert(SymbolType type){
		if (sym_table.containsKey(scope_name)){				// uparxei to key
			
			Set<SymbolType> syms = sym_table.get(scope_name);
			
			for (SymbolType t: syms){
				if (t.name.equals(type.name) && t.kind.equals(type.kind)){
					System.out.println("Variable name already exists at " + scope_name + " scope");
					System.out.println("Redeclaration");	
					return false;
				}
			}
			
			sym_table.get(scope_name).add(type);				// ara vale sto uparxon value to new element
		}
		else {
			Set<SymbolType> val = new HashSet<SymbolType>();	// den uparxei
			val.add(type);										// ara ftiakse new kai vale ekei tin timi
			
			sym_table.put(scope_name,val);
		}
		
		return true;
	}	
	
	SymbolType lookup(String name, String kind){
		
		SymbolTable temp = this;
		
		while (temp != null){
			Set<SymbolType> syms = temp.sym_table.get(temp.scope_name);
			
			if (syms == null)
				break;
				
			for (SymbolType type: syms){
				if (type.name.equals(name) && type.kind.equals(kind)){
					System.out.println("Found " + name + " in " + temp.scope_name + " scope");
					return type;
				}
			}
			System.out.println("DID NOT Found it in " + temp.scope_name + " scope");
			temp = Main.localScopes.get(temp);
		}
		
		return null;
	}
	
	String typeCheck(String name, String kind){
		System.out.println("GIVEN TYPE: " + name);
		if (name != null){
			if (name.equals("int") || name.equals("boolean") || name.equals("int[]")){
				return name;
			}
			
			else {
				SymbolType res = this.lookup(name, kind);
				if (res != null){
					return res.type;
				}
				else {
					if (Main.globalScope.containsKey(name)){
						System.out.println("Vrika ton typo se global scope");
						return name;
					}
					else
						return null;
				}
			}
		}
		else return null;
	}
	
	SymbolTable exitScope(){
		// return the scope that the current scope points to
		SymbolTable t = Main.localScopes.get(this);
		return t;
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
