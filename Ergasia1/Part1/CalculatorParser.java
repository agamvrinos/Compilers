import java.io.InputStream;
import java.io.IOException;

class CalculatorParser {

    private int lookaheadToken;
    private InputStream in;
	private double right_val = 0;
	private double result = 0;


//=====================================================================
	public CalculatorParser(InputStream in) throws IOException {
		this.in = in;
		lookaheadToken = in.read();
	}
//=====================================================================
	public void parse() throws IOException, ParseError {
		double final_res = exp();

		System.out.println("Final Result: " + cutDigits(final_res));

		if (lookaheadToken != '\n' && lookaheadToken != -1)
		    throw new ParseError();
	}
//=====================================================================
	private void consume(int symbol) throws IOException, ParseError {

		if (lookaheadToken != symbol)
			throw new ParseError("Syntax Error at: " + "\"\\n\"" + "\nExpected: " + "\")\"");

		lookaheadToken = in.read();

		// skip spaces
		while (lookaheadToken == ' ' && lookaheadToken != -1 && lookaheadToken != '\n'){
			lookaheadToken = in.read();
		}

		if (lookaheadToken == -1 || lookaheadToken == '\n')
			System.out.println("End of File");

    }
//=====================================================================
	private double exp() throws IOException, ParseError {
		/*exp -> term exp2*/

		// if look_ahead_table[exp,lookaheadToken] == error then throw
		if (lookaheadToken != '(' && (lookaheadToken < '0' || lookaheadToken > '9'))
			throw new ParseError("Syntax error at: " + (char)lookaheadToken);

		System.out.println("==========================================");
		System.out.println("Mesa sto exp: " + (char)lookaheadToken);
		System.out.println("==========================================");

		double x = term();
		double y = exp2(x);

		System.out.println("res: " + y);

		return y;
    }
//=====================================================================
	private double term() throws IOException, ParseError {
		/*term -> factor term2*/

		// if anything else but number or ( then error
		if (lookaheadToken != '(' && (lookaheadToken < '0' || lookaheadToken > '9'))
			throw new ParseError("Parse Error3");

		System.out.println("==========================================");
		System.out.println("Mesa sto term: " + (char)lookaheadToken);
		System.out.println("==========================================");

		double res = factor();


		res = term2(res);

		return res;

    }
//=====================================================================
private double exp2(double token) throws IOException, ParseError {
	/*exp2 -> + term exp2
		    | - term exp2
		    | ε             */

	System.out.println("==========================================");
	System.out.println("Mesa sto exp2: " + (char)lookaheadToken);
	System.out.println("==========================================");

	// ε case
	if (lookaheadToken == ')' || lookaheadToken == -1 || lookaheadToken == '\n')
		return token;

	// if anything else but + or - then error
	if (lookaheadToken != '+' && lookaheadToken != '-' )
		throw new ParseError("Syntax error at: " + (char)lookaheadToken + "\nExpected: " + "\"+\" or \"-\"");

	// if i get here i have + or - for sure
	int operator = lookaheadToken;

	consume(lookaheadToken);

	right_val = term();

	if (operator == '+'){
		result = token + right_val;
	}
	else if (operator == '-'){
		result = token - right_val;
	}
	System.out.println("========================");
	System.out.println("right_val: " + right_val);
	System.out.println("========================");
	System.out.println("Result: " + result);

	result = exp2(result);

	return result;
}
//=====================================================================
private double factor() throws IOException, ParseError {
	/*term -> factor term2*/

	System.out.println("==========================================");
	System.out.println("Mesa sto factor: " + (char)lookaheadToken);
	System.out.println("==========================================");

	if (lookaheadToken != '(' && (lookaheadToken < '0' || lookaheadToken > '9'))
		throw new ParseError("Parse Error");

	if (lookaheadToken == '('){
		consume('(');
		result = exp() + '0';	// un-digitize
		System.out.println("Mesa sto result: " + digitize(result));
		consume(')');
	}
	else {
		result = lookaheadToken;
		consume(lookaheadToken);
	}
	return digitize(result);
}
//=====================================================================
private double term2(double token) throws IOException, ParseError {
	/*term2 -> * factor term2
		     | / factor term2
		     | ε*/

	System.out.println("==========================================");
	System.out.println("Mesa sto term2: " + (char)lookaheadToken);
	System.out.println("==========================================");

	// ε case
	if (lookaheadToken == ')' || lookaheadToken == '+' || lookaheadToken == '-' || lookaheadToken == -1 || lookaheadToken == '\n')
		return token;

	// if anything but * or / then error
	if (lookaheadToken != '*' && lookaheadToken != '/')
		throw new ParseError("Parse Error2");

	// if i get here i have * or / for sure
	int operator = lookaheadToken;

	consume(lookaheadToken);

	right_val = factor();

	if (operator == '*'){
		result = token * right_val;
	}
	else if (operator == '/'){
		result = token / right_val;
	}

	result = term2(result);

	System.out.println("Result: " + result);

	return result;
}
//===========================
//==== support functions ====
//===========================
private double digitize(double ascii_num){
	return ascii_num - '0';
}

public static String cutDigits(double num) {
    if((int) num == num) return Integer.toString((int) num); //for you, StackOverflowException
    return String.valueOf(num); 							 //and for you, Christian Kuetbach
}
//=====================================================================
	public static void main(String[] args) {
		try {
		    CalculatorParser parser = new CalculatorParser(System.in);
			parser.parse();
			// System.out.println(parser.parse());

			System.out.println("========================");
			System.out.println("Successful parse!");
			System.out.println("========================");

		}
		catch (IOException e) {
		    System.err.println(e.getMessage());
		}
		catch(ParseError err){
		    System.err.println(err.getMessage());
		}
	}
}
