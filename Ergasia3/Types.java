import java.util.*;

public class Types {
	List<SymbolType> fields;
	List<SymbolType> methods;
	int methods_counter;
	int fields_counter;
	
	public Types() {
		fields = new ArrayList<>();
		methods = new ArrayList<>();
		
		methods_counter = 0;
		fields_counter = 0;
	}
	
	boolean addField(SymbolType stype){
		if (existsFieldCheck(stype.name) != null)
			return false;
		
		stype.offset = fields_counter;
		fields.add(stype);
		fields_counter++;
		
		return true;
	}
	
	boolean addMethod(SymbolType stype){
		
		if (existsMethodCheck(stype.name) != null){
			return false;
		}
		
		stype.offset = methods_counter;
		methods.add(stype);
		methods_counter ++;
		return true;
	}
	
	SymbolType existsFieldCheck(String name){
		for (SymbolType st : fields)	// Search for this name
			if (st.name.equals(name))
				return st;				// Found same name (Redeclaration)
		
		return null;	// Name not found, Add is possible
	}
	
	SymbolType existsMethodCheck(String name){
		for (SymbolType st : methods)	// Search for this name
			if (st.name.equals(name))
				return st;				// Found same name (Redeclaration)
		
		return null;	// Name not found, Add is possible
	}
	
	public int getMethods_counter() {
		return methods_counter;
	}
	
	public int getFields_counter() {
		return fields_counter;
	}
	
	void printTypes(String scope_name){
	      
		System.out.println("----------------------------------------");
		System.out.println("Printing methods for scope " + scope_name);
		System.out.println("----------------------------------------");
		
		for (SymbolType type : methods)
			type.printType();
			
		
		System.out.println("----------------------------------------");
		System.out.println("Printing fields for scope " + scope_name);
		System.out.println("----------------------------------------");
		
		for (SymbolType type : fields)
			type.printType();
		
		System.out.println("=========================================================================");
	}
}
