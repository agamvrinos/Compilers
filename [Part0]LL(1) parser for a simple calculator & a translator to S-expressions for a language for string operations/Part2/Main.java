import java.io.*;
import java_cup.runtime.Symbol;

public class Main {

	public static void main(String argv[]) {

		try {
			System.out.println("=============================");
			System.out.println("Lexing ["+argv[0]+"]");
			System.out.println("=============================");
			Scanner scanner = new Scanner((new FileReader(argv[0])));

			parser p = new parser(scanner);
			Object result = p.parse().value;

			// Symbol s;
			// do {
			// 	s = scanner.debug_next_token();
			// 	//   System.out.println("----------------------------------");
			// 	//   System.out.println("token: "+s);
			// 	//   System.out.println("----------------------------------");
			// } while (s.sym != sym.EOF);

			System.out.println("No errors.");
		}
		catch (Exception e) {
			e.printStackTrace(System.out);
			System.exit(1);
		}
	}
}
