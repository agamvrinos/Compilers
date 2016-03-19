import java.io.InputStream;
import java.io.IOException;

class CalculatorParser {

    private int lookaheadToken;
    private InputStream in;

//=====================================================================
	public CalculatorParser(InputStream in) throws IOException {
		this.in = in;
		lookaheadToken = in.read();
	}
//=====================================================================
	public void parse() throws IOException, ParseError {
		exp();
		if (lookaheadToken != '\n' && lookaheadToken != -1)
		    throw new ParseError();
	}
//=====================================================================
	private void consume(int symbol) throws IOException, ParseError {

		System.out.println((char)lookaheadToken + " vs " + (char)symbol);
		if (lookaheadToken != symbol)
			throw new ParseError("Syntax Error at: " + "\"\\n\"" + "\nExpected: " + "\")\"");

		lookaheadToken = in.read();

		// skip spaces
		while (lookaheadToken == ' ' && lookaheadToken != -1 && lookaheadToken != '\n'){
			lookaheadToken = in.read();
		}

		System.out.println("Sto consume: " + (char)lookaheadToken);

		if (lookaheadToken == -1 || lookaheadToken == '\n')
			System.out.println("End of File");

    }
//=====================================================================
	private void exp() throws IOException, ParseError {
		/*exp -> term exp2*/

		// if look_ahead_table[exp,lookaheadToken] == error then throw
		if (lookaheadToken != '(' && (lookaheadToken < '0' || lookaheadToken > '9'))
			throw new ParseError("Syntax error at: " + (char)lookaheadToken);

		System.out.println("Mesa sto exp: " + (char)lookaheadToken);

		term();
		exp2();

    }
//=====================================================================
	private void term() throws IOException, ParseError {
		/*term -> factor term2*/

		if (lookaheadToken == '+' || lookaheadToken == '-' || lookaheadToken == '*' || lookaheadToken == '/' || lookaheadToken == ')' || lookaheadToken == -1 )
			throw new ParseError("Parse Error3");

		System.out.println("Mesa sto term: " + (char)lookaheadToken);

		factor();
		term2();

    }
//=====================================================================
private void exp2() throws IOException, ParseError {
	/*exp2 -> + term exp2
		    | - term exp2
		    | ε             */


	if (lookaheadToken == '*' || lookaheadToken == '/' || lookaheadToken == '(' || (lookaheadToken > '0' && lookaheadToken < '9') )
		throw new ParseError("Parse Error4");

	System.out.println("Mesa sto exp2: " + (char)lookaheadToken);

	if (lookaheadToken == '+' || lookaheadToken == '-')
	{
		consume(lookaheadToken);
		term();
		exp2();
	}

	// ε case
	return ;
}
//=====================================================================
private void factor() throws IOException, ParseError {
	/*term -> factor term2*/
	System.out.println("Mesa sto factor: " + (char)lookaheadToken);

	if (lookaheadToken == '+' || lookaheadToken == '-' || lookaheadToken == '*' || lookaheadToken == '/' || lookaheadToken == ')' || lookaheadToken == -1 || lookaheadToken == '\n')
		throw new ParseError("Parse Error");

	if (lookaheadToken == '('){
		consume('(');
		exp();
		consume(')');
	}
	else
		consume(lookaheadToken);
}
//=====================================================================
private void term2() throws IOException, ParseError {
	/*term2 -> * factor term2
		     | / factor term2
		     | ε*/
	System.out.println("Mesa sto term2: " + (char)lookaheadToken);

	if (lookaheadToken == '(' || (lookaheadToken > '0' && lookaheadToken < '9') )
		throw new ParseError("Parse Error2");

	if (lookaheadToken == '*' || lookaheadToken == '/')
	{
		consume(lookaheadToken);
		factor();
		term2();
	}

	return;
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
