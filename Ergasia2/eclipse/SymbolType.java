import java.util.*;

public class SymbolType {
	String kind;		// kind: var, method, classType
	String name;		// name: var or method name
	String type;		// Type: integer, boolean
	List<String> parameters;	// if method then parameters else null
	
	SymbolType(String kind, String name, String type){	// simple var
		this.kind = kind;
		this.name = name;
		this.type = type;
		parameters = null;
	}
	
	SymbolType(String kind, String name, String type, List<String> parameters){	// method
		this.kind = kind;
		this.name = name;
		this.type = type;
		
		this.parameters = new ArrayList<String>();
		
		for (String arg : parameters){
			this.parameters.add(arg);
		}
		
	}
	
	
	void printType(){
		
		System.out.println("******************************");
		System.out.println("Kind = " + kind);
		System.out.println("Name = " + name);
		System.out.println("Type|RetVal = " + type);
		
		if (kind.equals("method")){
			for(String e: parameters)
				System.out.println("Parameter type: " + e);
		}
	}
	
}
