
public class DataBlock {
	String name;
	String temp;
	String belongs_to;
	boolean name_flag = false;
	boolean load_flag = false;
	boolean lvalue = false;
	
	public DataBlock() {
	}
	
	public DataBlock(String name, String temp, String belongs_to) {
		this.name = name;
		this.temp = temp;
		this.belongs_to = belongs_to;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setTemp(String temp) {
		this.temp = temp;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTemp() {
		return temp;
	}
	
	public boolean getFlag(){
		return name_flag;
	}
}
