import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class SymbolTable {
	String class_name;
	ClassInfo info;
	SymbolTable parent;
	
	
	public SymbolTable(String class_name) {
		
		this.parent = null;
		this.class_name = class_name;
		this.info = new ClassInfo();
		
		Utils.symbolTables.put(class_name, this);
	}
	
	
	MethodType lookupMethod(String method_name){
		SymbolTable temp = this;
		
		while (temp != null){
			MethodType method = temp.info.searchMethod(method_name);
			if (method != null)
				return method;
			temp = temp.parent;
		}
		return null;
	}
	
	SymbolTable enterScope(String new_class_name){
		
		SymbolTable table = new SymbolTable(new_class_name);	// Create new SymbolTable
		table.parent = this;									// Add as parent the current table
		
		return table;
	}
	
	SymbolTable exitScope(){
		return this.parent;
	}
	
	public void addField(FieldType ftype){
		info.addField(ftype);
	}
	
	public void addMethod(MethodType mtype){
		info.addMethod(mtype);
	}

	public Integer getMergedMethodsCount(){
		
		Set<String> names = new HashSet<>();
		SymbolTable temp = this;
		
		while (temp != null){
			List<String> returned_names = new ArrayList<>(temp.info.getMethodNames());
			
			for (String s : returned_names)
				names.add(s);
			
			temp = temp.parent;
		}
		
		return names.size();
	}
	
	public List<String> getMergedMethods(){
		List <String> parent_vTable = getParentVtable();
		List <String> vTable = new ArrayList<>();
		List <String> to_ret2 = new ArrayList<>();
		
		if (parent_vTable == null){
			vTable = new ArrayList<>();
			List<String> method_names = info.getMethodNames();
			for (String method : method_names){
				vTable.add(class_name + "@" + method);
				to_ret2.add(class_name + "_" + method);
			}
			info.vTable = vTable;
			return to_ret2;
		}
		else {
			List <String> to_ret = new ArrayList<>();
			to_ret.addAll(parent_vTable);
			
			List<String> method_names = info.getMethodNames();
			
			for (int i = 0; i < to_ret.size(); i ++){
				if (method_names.contains(to_ret.get(i).split("@")[1]))
					to_ret.set(i, class_name + "_" + to_ret.get(i).split("@")[1]);
			}
			
			for (int i = 0; i < method_names.size(); i ++){
				if (!to_ret.contains(class_name + "_" + method_names.get(i)))
					to_ret.add(class_name + "_" + method_names.get(i));
			}
			
			info.vTable = to_ret;
			return to_ret;
		}
	}
	
	public Integer getMergedFieldsCount(){
		Integer field_counter = 0;
		SymbolTable temp = this;
		
		while (temp != null){
			field_counter += temp.info.getFieldsSize();
			temp = temp.parent;
		}
		
		return field_counter;
	}
	
	public List<String> getParentVtable(){
		if (parent != null)
			return parent.info.vTable; 
		return null;
	}
	

	public FieldType fieldCheck(String name){
		SymbolTable temp = this;
		
		while (temp != null){
			FieldType t = temp.info.fieldCheck(name);
			if (t != null){
				return t;
			}
			temp = temp.parent;
		}
		return null;
	}
	public String getOffsetField(String field_name){
		List <String> total_fields = getTotalFields();
		
		for (Integer i = 0; i < total_fields.size(); i++){
			if (total_fields.get(i).equals(field_name)){
				Integer ret = (i + 1) * 4;
				return ret.toString();
			}
				
		}
		
		System.out.println("FIELD NOT FOUND");
		return null;
	}
	
	public List<String> getTotalFields(){
		SymbolTable temp = this;
		
		List<String> total_fields = new ArrayList<>();
		
		while (temp != null){
			List<String> curr_fields = temp.info.getFieldNames();
			for (int i = 0; i < curr_fields.size(); i ++){
				if (!total_fields.contains(curr_fields.get(i)))
					total_fields.add(curr_fields.get(i));
			}
			temp = temp.parent;
		}
		
		return total_fields;
	}
	
	void printSymbolTable(){
		System.out.println("SymbolTable of Class: " + class_name);
		info.printInfo();
		
		SymbolTable temp = this.parent;
		
		while (temp != null){
			System.out.println("Parent SymbolTable Class: " + temp.class_name);
			temp.info.printInfo();
			temp = temp.parent;
		}
		
	}
}
