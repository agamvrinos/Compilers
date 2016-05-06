import java.util.*;

public class MethodType {
	
	private String name;
	private String type;
	private Integer offset;
	private List<String> parameters;
	
	public MethodType(String name, String type, List<String> parameters) {
		this.name = name;
		this.type = type;
		this.parameters = new ArrayList<String>();
		
		for (String arg : parameters)
			this.parameters.add(arg);
	}
	
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public Integer getOffset() {
		return offset;
	}
	
	public Integer getArgsSize() {
		return this.parameters.size();
	}
	
	public List<String> getParameters() {
		return parameters;
	}
	
	void printMethodType(){
		
		System.out.println("******************************");
		System.out.println("Method Name = " + name);
		System.out.println("Method RetVal = " + type);
		System.out.println("Method Offset = " + offset);
		
		if (parameters != null){
			for(String e: parameters)
				System.out.println("Parameter type: " + e);
		}
	}
}
