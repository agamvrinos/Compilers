import java.util.*;

public class SymbolTable {
	
	Map<String, Types> sym_table;	// Mapping Scope name to Types 
	public String scope_name;
	
	public SymbolTable() {
		sym_table = new HashMap<>();
		
		// Parent will point to null
		Main.localScopes.put(this, null);
	}
	
	public SymbolTable(String scope_name) {
		sym_table = new HashMap<>();
		this.scope_name = scope_name;
		
		// REMOVE THIS LATERRRRRRRRRR// REMOVE THIS LATERRRRRRRRRR// REMOVE THIS LATERRRRRRRRRR
		Main.localScopes = new HashMap<>(); // REMOVE THIS LATERRRRRRRRRR
		// REMOVE THIS LATERRRRRRRRRR// REMOVE THIS LATERRRRRRRRRR// REMOVE THIS LATERRRRRRRRRR
	}
	
	SymbolTable enterScope(String new_scope_name){
		
		SymbolTable new_table = new SymbolTable(new_scope_name);	// new kid symbol table
		Main.localScopes.put(new_table, this);						// make the kid point to the parent
		
		return new_table;
	}
	
	
	//====================================================================================================
	boolean insertField(SymbolType stype){
		
		// If this scope exists
		if (sym_table.containsKey(scope_name)){	
			
			// Get Types
			Types types = sym_table.get(scope_name);	
			
			if (!types.addField(stype))	// Add field
				return false;			// False, if field name exists
		}
		else {
			Types types = new Types();			// Scope match does not exist, Create
			
			// Add it to ArrayList
			types.addField(stype);
			
			// Add it to Scope
			sym_table.put(scope_name,types);
		}
		
		return true;
	}	
	//====================================================================================================
	boolean insertMethod(SymbolType stype){
		
		// If this scope exists
		if (sym_table.containsKey(scope_name)){	
			
			// Get Types
			Types types = sym_table.get(scope_name);	
			
			if (!types.addMethod(stype))	// Add field
				return false;				// False, if field name exists
		}
		else {
			Types types = new Types();		// Scope match does not exist, Create
		
			// Add it to ArrayList
			types.addMethod(stype);
			
			// Add it to Scope
			sym_table.put(scope_name,types);
		}
		
		return true;
	}	
	//====================================================================================================
	SymbolType lookup(String name, String kind){
		
		// Start from current SymTable
		SymbolTable temp = this;
		
		// While top limit not reached
		while (temp != null){
			
			Types types = null;
			types = temp.sym_table.get(temp.scope_name);
			
			// Class might be empty
			if (types == null){
				temp = Main.localScopes.get(temp);
				continue;
			}
			
			if (kind.equals("field")){
				
				// Trying to find field
				SymbolType type = types.existsFieldCheck(name);
				
				if (type != null)	// Not found at this scope
					return type;
				else
					System.out.println("Could not find field \"" + name + "\" in " + temp.scope_name + " scope");
			}
			else if (kind.equals("method")){
				
				// Trying to find method
				SymbolType type = types.existsMethodCheck(name);
				
				if (type != null)	// Not found at this scope
					return type;
				else
					System.out.println("Could not find method \"" + name + "\" in " + temp.scope_name + " scope");
			}
			
			// Go to parent
			temp = Main.localScopes.get(temp);
		}
		
		return null;	// Type not found return null
	}
	
	//====================================================================================================
	SymbolTable exitScope(){
		// return the scope that the current scope points to
		SymbolTable t = Main.localScopes.get(this);
		return t;
	}
	//====================================================================================================
	void printSymbolTable(){
	      sym_table.get(scope_name).printTypes(scope_name);
	}
}
