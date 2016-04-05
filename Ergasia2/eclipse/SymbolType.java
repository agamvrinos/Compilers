import java.util.ArrayList;

public class SymbolType {
	String kind;		// kind: var, method, classType
	String name;		// name: var or method name
	String type;		// Type: integer, boolean
	ArrayList<String> parameters;	// if method then parameters else null
	
	SymbolType(String kind, String name, String type){	// simple var
		this.kind = kind;
		this.name = name;
		this.type = type;
		parameters = null;
	}
	
	SymbolType(String kind, String name, String type, ArrayList<String> arg){	// method
		this.kind = kind;
		this.name = name;
		this.type = type;
		
		parameters = new ArrayList<String>();
		
		for (int i = 0; i < arg.size(); i++){
			parameters.add(arg.get(i));
		}
	}
	
	SymbolType(){
		
	}
	
	void printType(){
		
		System.out.println("******************************");
		System.out.println("Kind = " + kind);
		System.out.println("Name = " + name);
		System.out.println("Type|RetVal = " + type);
		
		if (kind == "method"){
			for(String e: parameters)
				System.out.println("Parameter type: " + e);
		}
	}
	
}
