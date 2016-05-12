import java.util.*;

public class Utils {
	
	public static Map<String, SymbolTable> symbolTables;
	public static Integer temp_counter;
	public static Integer label_counter;
	
	// Returns new TEMP
	public static String getTemp(){
		Utils.temp_counter ++;
		String temp = "TEMP " + Utils.temp_counter.toString();
		return temp;
	}
		
	// Returns new LABEL
	public static String getLabel(){
		label_counter ++;
		String label = "L" + Utils.label_counter.toString();
		return label;
	}
	
	// Returns new GLOBAL vTable LABEL
	public static String getGlobalLabel(String class_name){
		String label = class_name + "_vTable";
		return label;
	}
	
	// Returns new GLOBAL vTable LABEL
	public static String getMethodLabel(String class_name, String method_name){
		String label = class_name + "_" + method_name;
		return label;
	}
		
	public static Integer getMaxFieldsNum(){
		Integer max = 0;
		for (Map.Entry<String, SymbolTable> map : Utils.symbolTables.entrySet()){

			if (map.getValue().info.getMaxArgumentsSize() > max)
				max = map.getValue().info.getMaxArgumentsSize(); 
		}
		return max;
	}
	
	public static void mapAllLocals() {
		for (Map.Entry<String, SymbolTable> map: symbolTables.entrySet()){
			String key = map.getKey();
			SymbolTable val = map.getValue();
			
			val.info.mapAllLocals();
		}
	}
	
	public static void printSymTables(){
		for (Map.Entry<String, SymbolTable> map: symbolTables.entrySet()){
			String key = map.getKey();
			SymbolTable val = map.getValue();
			
			 val.printSymbolTable();
		}
	}
	
}
