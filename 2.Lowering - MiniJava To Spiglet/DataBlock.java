
public class DataBlock {
	String name;
	String temp;
	String belongs_to;
	boolean name_flag = false;	// To get just the name of the identifier
	boolean load_flag = false;	// possible field load
	boolean lvalue = false;		// if true, dont load if field
	
	public DataBlock() {}
	
	public DataBlock(String name, String temp, String belongs_to) {
		this.name = name;
		this.temp = temp;
		this.belongs_to = belongs_to;
	}
	
	public boolean getNameFlag(){
		return name_flag;
	}
}
