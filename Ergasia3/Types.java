import java.util.*;

public class Types {
	List<SymbolType> fields;
	List<SymbolType> methods;
	
	public Types() {
		fields = new ArrayList<>();
		methods = new ArrayList<>();
	}
	
	boolean addField(SymbolType stype){
		if (existsFieldCheck(stype.name) != null)
			return false;
		
		fields.add(stype);
		return true;
	}
	
	boolean addMethod(SymbolType stype){
		
		if (existsMethodCheck(stype.name) != null)
			return false;
		
		methods.add(stype);
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
	
	void printTypes(String scope_name){
	      
		System.err.println("----------------------------------------");
		System.err.println("Printing methods for scope " + scope_name);
		System.err.println("----------------------------------------");
		
		for (SymbolType type : methods)
			type.printType();
			
		
		System.err.println("----------------------------------------");
		System.err.println("Printing fields for scope " + scope_name);
		System.err.println("----------------------------------------");
		
		for (SymbolType type : fields)
			type.printType();
		
		System.out.println("=========================================================================");
	}
}
