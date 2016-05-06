import java.util.*;

public class SymbolTable {
	
	public Types table_types; 
	public String scope_name;
	
	public SymbolTable() {
		table_types = new Types();
		
		// Parent will point to null
		Main.localScopes.put(this, null);
	}
	
	public SymbolTable(String scope_name) {
		table_types = new Types();
		
		this.scope_name = scope_name;
	}
	
	SymbolTable enterScope(String new_scope_name){
		
		SymbolTable new_table = new SymbolTable(new_scope_name);	// new kid symbol table
		Main.localScopes.put(new_table, this);						// make the kid point to the parent
		
		return new_table;
	}
	//====================================================================================================
	boolean insertField(FieldType stype){
		
		if (!table_types.addField(stype))	// Add field
			return false;					// False, if field name exists
		
		return true;
	}	
	//====================================================================================================
	boolean insertMethod(MethodType stype){
		
		if (!table_types.addMethod(stype))	// Add method
				return false;				// False, if method name exists

		return true;
	}
	//====================================================================================================
	MethodType lookupMethod(String name){
		
		// Start from current SymTable
		SymbolTable temp = this;
		
		// While top limit not reached
		while (temp != null){
				
			// Trying to find method
			MethodType type = table_types.existsMethodCheck(name);
			
			if (type != null)	// Not found at this scope
				return type;
			else
				System.out.println("Could not find method \"" + name + "\" in " + temp.scope_name + " scope");
			
			// Go to parent
			temp = Main.localScopes.get(temp);
		}
		return null;	// Type not found return null
	}
	//====================================================================================================
	FieldType lookupField(String name){
		
		// Start from current SymTable
		SymbolTable temp = this;
		
		// While top limit not reached
		while (temp != null){
				
			// Trying to find field
			FieldType type = table_types.existsFieldCheck(name);
			
			if (type != null)	// Not found at this scope
				return type;
			else
				System.out.println("Could not find field \"" + name + "\" in " + temp.scope_name + " scope");
			
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
		table_types.printTypes(scope_name);
	}
}
