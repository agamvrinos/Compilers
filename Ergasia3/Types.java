import java.util.*;

public class Types {
	List<FieldType> fields;
	List<MethodType> methods;
	int methods_counter;
	int fields_counter;
	
	public Types() {
		fields = new ArrayList<>();
		methods = new ArrayList<>();
		
		methods_counter = 0;
		fields_counter = 0;
	}
	
	boolean addField(FieldType ftype){
		if (existsFieldCheck(ftype.getName()) != null)
			return false;
		
		ftype.setOffset(fields_counter);
		fields.add(ftype);
		fields_counter++;
		
		return true;
	}
	
	boolean addMethod(MethodType mtype){
		
		if (existsMethodCheck(mtype.getName()) != null){
			return false;
		}
		
		mtype.setOffset(methods_counter);
		methods.add(mtype);
		methods_counter ++;
		return true;
	}
	
	FieldType existsFieldCheck(String name){
		for (FieldType st : fields)		// Search for this name
			if (st.getName().equals(name))
				return st;				// Found same name (Redeclaration)
		
		return null;					// Name not found, Add is possible
	}
	
	MethodType existsMethodCheck(String name){
		for (MethodType st : methods)	// Search for this name
			if (st.getName().equals(name))
				return st;				// Found same name (Redeclaration)
		
		return null;	// Name not found, Add is possible
	}
	
	public int getMethods_counter() {
		return methods_counter;
	}
	
	public int getFields_counter() {
		return fields_counter;
	}
	
	public List<MethodType> getMethods() {
		return methods;
	}
	
	void printTypes(String scope_name){
	      
		System.out.println("----------------------------------------");
		System.out.println("Printing methods for scope " + scope_name);
		System.out.println("----------------------------------------");
		
		for (MethodType type : methods)
			type.printMethodType();
			
		
		System.out.println("----------------------------------------");
		System.out.println("Printing fields for scope " + scope_name);
		System.out.println("----------------------------------------");
		
		for (FieldType type : fields)
			type.printFieldType();
		
		System.out.println("=========================================================================");
	}
}
