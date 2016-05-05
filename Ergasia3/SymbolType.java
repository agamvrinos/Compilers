import java.util.*;

public class SymbolType {
	String name;
	String type;
	Integer offset;
	List<String> parameters;
	
	SymbolType(String name, String type){	// Simple variable
		this.name = name;
		this.type = type;
		this.offset = -1;
		parameters = null;
	}
	
	SymbolType(String name, String type, List<String> parameters){	// Method
		this.name = name;
		this.type = type;
		
		this.parameters = new ArrayList<String>();
		
		for (String arg : parameters)
			this.parameters.add(arg);
		
	}
	
	void printType(){
		
		System.out.println("******************************");
		System.out.println("Name = " + name);
		System.out.println("Type|RetVal = " + type);
		System.out.println("Offset = " + offset);
		
		if (parameters != null){
			for(String e: parameters)
				System.out.println("Parameter type: " + e);
		}
	}
	
}
