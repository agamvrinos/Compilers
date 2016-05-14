import java.util.*;
import java.util.List;

public class MethodType {
	
	private String name;
	private String type_ret;
	
	private List<FieldType> parameters;
	private List<FieldType> locals;
	private Map<String, String> locals_to_temps;
	private Map<String, String> args_to_temps;
	
	public MethodType(String name, String type_ret, List<FieldType> par, List<FieldType> loc) {
		
		this.parameters = new ArrayList<>();
		this.locals = new ArrayList<>();
		this.locals_to_temps = new HashMap<>();
		this.args_to_temps = new HashMap<>();
		
		this.name = name;
		this.type_ret = type_ret;
		this.parameters = par;
		this.locals = loc;
		
		if (this.parameters != null)
			for (FieldType p : parameters)	// add params mapping
				args_to_temps.put(p.getName(), "TEMP " + (parameters.indexOf(p) + 1));
	}
	
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type_ret;
	}
	
	public Integer getArgumensSize(){
		if (parameters == null)
			return 0;

		return parameters.size();
	}
	
	public void mapLocals(){
		if (this.locals != null){
			for (FieldType p : locals)	// add locals mapping
				locals_to_temps.put(p.getName(), Utils.getTemp());
		}
	}
	
	public boolean existLocal(String name){
		for (FieldType type: locals){
			if (type.getName().equals(name))
				return true;
		}
		return false;
	}
	
	public boolean existArg(String name){
		if (parameters != null){
			for (FieldType type: parameters){
				if (type.getName().equals(name))
					return true;
			}
		}
		return false;
	}
	
	public String getLocalTemp(String var_name){
		return locals_to_temps.get(var_name);
	}
	
	public String getArguTemp(String var_name){
		return args_to_temps.get(var_name);
	}
	
	public String getTypeOfLocal(String var_name){
		for (int i = 0; i < locals.size(); i ++){
			if (locals.get(i).getName().equals(var_name))
				return locals.get(i).getType();
		}
		System.out.println("LOCAL NOT FOUND");
		return null;
	}
	
	public String getTypeOfParameter(String var_name){
		for (int i = 0; i < parameters.size(); i ++){
			if (parameters.get(i).getName().equals(var_name))
				return parameters.get(i).getType();
		}
		System.out.println("PARAMETER NOT FOUND");
		return null;
	}
	
	void printMethodType(){
		
		System.out.println("******************************");
		System.out.println("Method Name = " + name);
		System.out.println("Method RetVal = " + type_ret);
		System.out.println("******************************");
		System.out.println("Method Arguments");
		System.out.println("******************************");
		if (parameters != null)
		for (FieldType f: parameters)
			f.printFieldType();
		System.out.println("******************************");
		System.out.println("Method Locals");
		System.out.println("******************************");
		for (FieldType f: locals)
			f.printFieldType();
		for (Map.Entry<String, String> map : locals_to_temps.entrySet()){
			System.out.println("Local: " + map.getKey() + ", Temp: " + map.getValue());
		}
		for (Map.Entry<String, String> map : args_to_temps.entrySet()){
			System.out.println("ARG: " + map.getKey() + ", Temp: " + map.getValue());
		}
	}
}
