
public class FieldType {
	private String name;
	private String type;
	private Integer offset;
	
	public FieldType(String name, String type) {
		this.name = name;
		this.type = type;
		this.offset = -1;
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
	
	void printFieldType(){
		System.out.println("******************************");
		System.out.println("Field Name = " + name);
		System.out.println("Field Type = " + type);
		System.out.println("Field Offset = " + offset);
	}
}
