import java.util.*;
import java.util.List;

public class ClassInfo {
	List<FieldType> fields;		// Class fields
	List<MethodType> methods;	// Class methods
	List<String> vTable;
	
	public ClassInfo() {
		fields = new ArrayList<>();
		methods = new ArrayList<>();
		vTable = new ArrayList<>();
	}
	
	public void addField(FieldType ftype){
		fields.add(ftype);
	}
	
	public void addMethod(MethodType mtype){
		methods.add(mtype);
	}
	
	List<String> getMethodNames(){
		List<String> names = new ArrayList<>();
		for (MethodType method : methods){
			names.add(method.getName());
		}
		return names;
	}
	
	List<String> getFieldNames(){
		List<String> names = new ArrayList<>();
		for (FieldType field : fields){
			names.add(field.getName());
		}
		return names;
	}
	
	public List<MethodType> getMethods() {
		return methods;
	}
	
	public List<FieldType> getFields() {
		return fields;
	}
	
	public Integer getFieldsSize() {
		return fields.size();
	}
	
	public MethodType searchMethod(String method_name){
		for (MethodType method : methods){
			if (method.getName().equals(method_name))
				return method;
		}
		return null;
	}
	
	public String getOffsetMethod(String method_name){
		System.out.println(vTable.size() + " SIZE RE MLK");
		for (Integer i = 0; i < vTable.size(); i ++){
			System.out.println("METHOD NAME GAMW: " + vTable.get(i).split("@")[1]);
			if(vTable.get(i).split("@")[1].contains(method_name)){
				Integer off = i * 4;
				return off.toString();
			}
		}
		System.out.println("METHOD NOT FOUND");
		return null;
	}
	
	public Integer getMaxArgumentsSize(){
		Integer max = 0;
		for (MethodType m: methods){
			if (m.getArgumensSize() > max)
				max = m.getArgumensSize();
		}
		return max;
	}
	
	public void mapAllLocals(){
		for (MethodType method : methods){
			method.mapLocals();
		}
	}
	
	public MethodType localCheck(String method_name, String var_name) {
		for (MethodType method : methods){
			if (method.getName().equals(method_name))
				if (method.existLocal(var_name))
					return method;
		}
		return null;
	}
	
	public MethodType argCheck(String method_name, String var_name) {
		for (MethodType method : methods){
			if (method.getName().equals(method_name))
				if (method.existArg(var_name))
					return method;
		}
		return null;
	}
	
	public FieldType fieldCheck(String name){
		for (FieldType field : fields){
			if (field.getName().equals(name))
				return field;
		}
		return null;
	}
	
	void printInfo(){
		System.out.println("============================");
		System.out.println("PRINTING FIELDS");
		System.out.println("============================");
		for (int i = 0; i < fields.size(); i ++){
			fields.get(i).printFieldType();
		}
		System.out.println("============================");
		System.out.println("PRINTING METHODS");
		System.out.println("============================");
		for (int i = 0; i < methods.size(); i ++){
			methods.get(i).printMethodType();
		}
	}
}
