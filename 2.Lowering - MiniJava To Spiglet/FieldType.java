
public class FieldType {
	private String name;
	private String type;
	
	public FieldType(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	void printFieldType(){
		System.out.println("Field: " + type + " " + name);
	}
}
