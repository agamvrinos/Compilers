import java.util.*;

public class SymbolTable {
	
	public Types sym_table; 
	public String scope_name;
	
	public SymbolTable() {
		sym_table = new Types();
		
		// Parent will point to null
		Main.localScopes.put(this, null);
	}
	
	public SymbolTable(String scope_name) {
		sym_table = new Types();
		
		this.scope_name = scope_name;
	}
	
	SymbolTable enterScope(String new_scope_name){
		
		SymbolTable new_table = new SymbolTable(new_scope_name);	// new kid symbol table
		Main.localScopes.put(new_table, this);						// make the kid point to the parent
		
		return new_table;
	}
	
	
	//====================================================================================================
	boolean insertField(SymbolType stype){
		
		if (!sym_table.addField(stype))	// Add field
			return false;			// False, if field name exists
		
		return true;
	}	
	//====================================================================================================
	boolean insertMethod(SymbolType stype){
		
		if (!sym_table.addMethod(stype))	// Add field
				return false;				// False, if field name exists

		return true;
	}	
	//====================================================================================================
	SymbolType lookup(String name, String kind){
		
		// Start from current SymTable
		SymbolTable temp = this;
		
		// While top limit not reached
		while (temp != null){
			
			Types types = null;
			types = sym_table;
			
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
		System.out.println("SCOPE NAME: " + scope_name);
		Types types = sym_table;
		if (types != null){
			types.printTypes(scope_name);
		}
	}
}
