import java.util.ArrayList;
import java.util.List;

public class Utils {
	
	public static Integer getMergedMethodNum(SymbolTable child, SymbolTable parent){
		List <SymbolType> child_methods = child.sym_table.getMethods();
		List <String> names = new ArrayList<>();
		
		Integer counter = child_methods.size();
		for (SymbolType st : child_methods)
			names.add(st.name);
		
		
		while (parent != null){
			List <SymbolType> parent_methods = parent.sym_table.getMethods();
			for (SymbolType st : parent_methods){
				if (!names.contains(st.name)){
					names.add(st.name);
					counter ++;
				}
			}
			parent = Main.localScopes.get(parent);
		}
		
		return counter;
	}
	
	public static List <String> getMergetMethods(SymbolTable child, SymbolTable parent){
		List <SymbolType> child_methods = child.sym_table.getMethods();
		List <String> names = new ArrayList<>();
		List <String> final_names = new ArrayList<>();
		
		String child_name = child.scope_name;
		
		for (SymbolType st : child_methods){
			names.add(st.name);
			final_names.add(child_name + "_" + st.name);
		}
		
		while (parent != null){	// need to find which methods are inherited
			String parent_name = parent.scope_name;
			List <SymbolType> parent_methods = parent.sym_table.getMethods();
			
			for (SymbolType st : parent_methods){
				if (!names.contains(st.name)){
					names.add(st.name);
					final_names.add(parent_name + "_" + st.name);
				}
			}
			parent = Main.localScopes.get(parent);
		}
		return final_names;
	}
}
