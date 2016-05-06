import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {
	
	public static Integer getMergedMethodNum(SymbolTable child, SymbolTable parent){
		List <MethodType> child_methods = child.table_types.getMethods();
		List <String> names = new ArrayList<>();
		
		Integer counter = child_methods.size();
		for (MethodType st : child_methods)
			names.add(st.getName());
		
		
		while (parent != null){
			List <MethodType> parent_methods = parent.table_types.getMethods();
			for (MethodType st : parent_methods){
				if (!names.contains(st.getName())){
					names.add(st.getName());
					counter ++;
				}
			}
			parent = Main.localScopes.get(parent);
		}
		
		return counter;
	}
	
	public static List<String> getMergedMethods(SymbolTable child, SymbolTable parent){
		
		List<String> parent_vTable = getParentVtable(parent);
		List<String> vTable = new ArrayList<>();
		
		if (parent_vTable == null){	// Top Class
			List <MethodType> child_methods = child.table_types.getMethods();
			for (MethodType mtype : child_methods)
				vTable.add(child.scope_name + "_" + mtype.getName());
			
			// add vTable for this class
			Main.vTables.put(child.scope_name, vTable);
			
			return vTable;
		}
		else {
			List <String> to_ret = new ArrayList<>();
			to_ret.addAll(parent_vTable);
			
			List <MethodType> child_methods = child.table_types.getMethods();
			List <String> names = new ArrayList<>();
			
			for (MethodType st : child_methods)
				names.add(st.getName());
			
			for (int i = 0; i < to_ret.size(); i ++){
				if (names.contains(to_ret.get(i).split("_")[1]))
					to_ret.set(i, child.scope_name + "_" + to_ret.get(i).split("_")[1]);
			}
			
			for (int i = 0; i < names.size(); i ++){
				if (!to_ret.contains(child.scope_name + "_" + names.get(i)))
					to_ret.add(child.scope_name + "_" + names.get(i));
			}
			
			// add vTable for this class
			Main.vTables.put(child.scope_name, to_ret);
			
			return to_ret;
		}
	}
	
	public static List<String> getParentVtable(SymbolTable parent){
		
		if (parent != null){
			return Main.vTables.get(parent.scope_name);
		}
		return null;
	}
	
	void printGlobalScopes(){
		System.out.println("*****************************");
		System.out.println("Printing Global Scopes");
		System.out.println("*****************************");
		for (Map.Entry<String, SymbolTable> entry : Main.globalScope.entrySet()) {
    	    String key = entry.getKey();
    	    SymbolTable s = entry.getValue();
    	    String val = "";
    	    if (s != null)
    	    	val = s.scope_name;
    	    System.out.println("Key: " + key);
	    	System.out.println("Value: " + ((s == null) ? "null" : val));
    	    System.out.println("------------------------");
    	}
	}
	
	void printLocalScopes(){
		
		System.out.println("*****************************");
		System.out.println("Printing Local Scopes");
		System.out.println("*****************************");
		for (Map.Entry<SymbolTable, SymbolTable> entry : Main.localScopes.entrySet()) {
    	    String key = entry.getKey().scope_name;
    	    SymbolTable s = entry.getValue();
    	    String val = "";
    	    if (s != null)
    	    	val = s.scope_name;
    	    System.out.println("Key: " + key);
	    	System.out.println("Value: " + ((s == null) ? "null" : val));
    	    System.out.println("------------------------");
    	}
	}
	
	void printAllSymbolTables(){
		
		System.out.println("*****************************");
		System.out.println("Printing Symbol Tables");
		System.out.println("*****************************");
		for (Map.Entry<SymbolTable, SymbolTable> entry : Main.localScopes.entrySet()) {
			SymbolTable key = entry.getKey();
			key.printSymbolTable();
    	    
    	}
	}
	
	void printMapping(){
		System.out.println("*****************************");
		System.out.println("Mapping");
		System.out.println("*****************************");
		for (Map.Entry<String, Map<String, SymbolTable>> entry : Main.mapping.entrySet()) {
    	    String key = entry.getKey();
    	    Map<String, SymbolTable> s = entry.getValue();
    	    
    	    System.out.println("ClassName: " + key);
    	    for (Map.Entry<String, SymbolTable> entry2 : s.entrySet()) {
    	    	String method_key = entry2.getKey();
    	    	SymbolTable method_st = entry2.getValue();
    	    	System.out.print("method: " + method_key + " method table: " + method_st.scope_name);
    	    	System.out.println();
    	    }
    	   
    	    System.out.println("------------------------");
    	}
	}
	
	static void printVTables(){
		for (Map.Entry<String, List<String>> entry : Main.vTables.entrySet()) {
    	    String key = entry.getKey();
    	    List<String> s = entry.getValue();
    	    
    	    System.out.println("*****************************");
    		System.out.println("vTable of class " + key);
    		System.out.println("*****************************");
    		for (String str: s){
    			System.out.println(str);
    		}
		}
	}
}
