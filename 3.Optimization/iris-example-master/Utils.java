
public class Utils {
	static Integer counter = 0;

	static Integer getNextCounter(){
		counter ++;
		return counter;
	}
	
	static Integer getFakeNextCounter(){
		Integer cur = counter;
		cur++;
		return cur;
	}
	
	static Integer getFakeNextNextCounter(){
		Integer cur = counter;
		cur += 2;
		return cur;
	}
	
	static void resetCounter(){
		counter = 0;
	}
}
