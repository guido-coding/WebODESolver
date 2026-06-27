package equationparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import equationparser.InputCharType.CharType;

public class EquationParser {
	
	private static final boolean suppressOutput = true;

	/*
	public static void main(String arg[]) {
		long start = System.nanoTime();
		
		//String equation = "-1*(-3+((-5-1)*2)^2)  / 0.5+1-cos(x)";
		String equation = "sin(1^2)";
		//String equation = "sin ( 3 / 3 * x )";
		
		try {
			EquationParser parser = new EquationParser(equation);
			long end = System.nanoTime();
			long duration = end - start;
			double time = duration / 1000000;
			if (!suppressOutput) {			
				System.out.println("time needed to parse equation: " + time + " ms");
			}
			
			start = System.nanoTime();
			Map<String, Double> varValues = new HashMap<String, Double>();
			varValues.put("x", 5.0);
			double value = parser.resolveEquation(varValues);
			
			end = System.nanoTime();
			duration = end - start;
			time = duration / 1000000;
			if (!suppressOutput) {	
				System.out.println("time needed to solve equation: " + time + " ms");
			}
			System.out.println("Value: " + value);
			
			varValues = new HashMap<String, Double>();
			varValues.put("x", 1.0);
			value = parser.resolveEquation(varValues);
			System.out.println("Value: " + value);
			
			
			System.out.println("Value: " + EquationParser.resolveEquation(equation, varValues));
			
			
		} catch (InvalidEquationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	*/
	
	
	public static void main(String arg[]) {
		test("2*(-1*y+1)");
		test("sin(5)+1");
		test("3*(-2.5-1)-1+5*2/(1/5)");
		test(".5+10");
		test("-.5-1");
		test("(1)");
		test("x");
		test("(x+2)^-0.1");
		test("((x+2)^0.1)-(1 + cos2)*-6");
		test("sin2^2");
		test("sin(2^2)");
		test("x2 + 2");
		
	}
	
	private static void test(String equation) {
		Map<String, Double> varValues = new HashMap<String, Double>();
		varValues.put("x", 1.0);
		varValues.put("y", -1.0);

		try {
			System.out.println(EquationParser.resolveEquation(equation, varValues));
		} catch (InvalidEquationException e) {
			e.printStackTrace();
		}
	}
	
	
	
	private final String equation;
	private final Token[] equationTokens;
	private final List<VariableValueUpdateListener> variableValueUpdateListeners;
	
	/**
	 * 
	 * @param equation equation to evaluate. 
	 * Supported operators are ^ (power), +, -, *, /, sin, cos, tan, log (log10), mod (modulus), sqrt (square root), (, ). 
	 * Equation variable names can be used but cannot contain numbers or symbols * / - + ^ ( ) .
	 * White spaces in the equation are ignored.
	 * Floating point numbers can be used, "." should be used as the decimal separation symbol.
	 * @throws InvalidEquationException in case an error is found in the equation (e.g. mismatch in parenthesis, invalid numeric values such as 0.12.1) and parsing the equation is unsuccessful.
	 */
	public EquationParser(String equation) throws InvalidEquationException {
		this.equation = equation;
		variableValueUpdateListeners = new ArrayList<VariableValueUpdateListener>();
		String[] tokens = tokenize(equation);
		
		Token[] t = Token.createTokens(tokens, variableValueUpdateListeners);
		if (!suppressOutput) {			
			System.out.println("result Tokens:");
			System.out.println("-------------");
			for (Token token : t) {
				System.out.println(token);
			}
			System.out.println("-------------");
		}
		
		equationTokens = shuntingYard(t);
		if (!suppressOutput) {			
			System.out.println("result Tokens:");
			System.out.println("-------------");
			for (Token token : equationTokens) {
				System.out.println(token);
			}
			System.out.println("-------------");
		}
	}
	
	/**
	 * Resolve equation 
	 * @param varValues Map containing variable name, value pairs
	 * @return result
	 * @throws InvalidEquationException in case an error is found in the equation (e.g. variable names are used for which no values are specified).
	 */
	public double resolveEquation(Map<String, Double> varValues) throws InvalidEquationException {
		//Set variable values
		for (VariableValueUpdateListener l : variableValueUpdateListeners) {
			l.valuesUpdated(varValues);
		}
		
		return reversePolish(equationTokens);
	}

	/**
	 * Resolve equation, assuming that no variable names are used. In case variable names are used, use resolveEquation(Map<String, Double> varValues) instead
	 * @return result
	 * @throws InvalidEquationException in case an error is found in the equation (e.g. equation contains variable names).
	 */
	public double resolveEquation() throws InvalidEquationException {
		return reversePolish(equationTokens);
	}
	
	/**
	 * Resolve equation, assuming that no variable names are used. In case variable names are used, use resolveEquation((String equation, Map<String, Double> varValues) instead.
	 * For every call, the equation is parsed again. For multiple equation evaluations it is therefore recommended to create an EquationParser object and call resolveEquation() instead for performance reasons.
	 * @param equation equation to evaluate
	 * @return result
	 * @throws InvalidEquationException in case an error is found in the equation (e.g. mismatch in parenthesis, invalid numeric values such as 0.12.1) and parsing the equation is unsuccessful.
	 */
	public static double resolveEquation(String equation) throws InvalidEquationException {
		EquationParser parser = new EquationParser(equation);
		return parser.resolveEquation();
	}
	
	/**
	 * Resolve equation.
	 * For every call, the equation is parsed again. For multiple equation evaluations it is therefore recommended to create an EquationParser object and call resolveEquation(Map<String, Double> varValues) instead for performance reasons.
	 * @param equation equation to evaluate
	 * @param varValues Map containing variable name, value pairs
	 * @return result
	 * @throws InvalidEquationException in case an error is found in the equation (e.g. mismatch in parenthesis, invalid numeric values such as 0.12.1) and parsing the equation is unsuccessful.
	 */
	public static double resolveEquation(String equation, Map<String, Double> varValues) throws InvalidEquationException {
		EquationParser parser = new EquationParser(equation);
		for (VariableValueUpdateListener l : parser.variableValueUpdateListeners) {
			l.valuesUpdated(varValues);
		}
		return parser.resolveEquation();
	}
	
	/**
	 * 
	 * @return string containing equation
	 */
	public String getEquation() {
		return equation;
	}
	
	@Override
	public boolean equals(Object o) {
		if (! (o instanceof EquationParser)) {
			return false;
		}
		EquationParser e = (EquationParser) o;
		return equation.equals(e.equation);
	}
	
	@Override
	public String toString() {
		return equation;
	}
	
	
	/**
	 * See https://en.wikipedia.org/wiki/Reverse_Polish_notation for algorithm
	 */
	private double reversePolish(Token[] tokens) throws InvalidEquationException {
		Stack<Token> output = new Stack<Token>();
		
		for (Token token : tokens) {
			if (token.isNumeric()) {
				output.push(token);
			} else if (token.isOperator()) {
				double[] arguments = new double[Operators.getNumberOfArguments(token.getOperator())];
				
				if (output.size() < arguments.length) {
					throw new InvalidEquationException("Invalid equation");
				}
				for (int i=arguments.length-1; i>=0; i--) {
					arguments[i] = output.pop().getNumericValue();
				}
				
				double value = Operators.performOperation(token.getOperator(), arguments);
				output.push(new NumericToken(value));
			}
		}
		
		if (output.size() != 1) throw new InvalidEquationException("Invalid equation. More than one token left");
		return output.pop().getNumericValue();
	}
	
	/**
	 * See https://en.wikipedia.org/wiki/Shunting_yard_algorithm for algorithm
	 */
	private Token[] shuntingYard(Token[] tokens) throws InvalidEquationException {
		List<Token> output = new ArrayList<Token>();
		Stack<Token> operatorStack = new Stack<Token>();
		
		for (Token token : tokens) {
			if (token.isNumeric()) {
				output.add(token);
			} else if (token.isOperator()) {
				String o1 = token.getOperator();
				//chekc if left parenthesis, then push to operator stack
				if (o1.equals("(")) {
					operatorStack.push(token);
				} else if (o1.equals(")")) {
					//if right parenthesis, then pop/push all operators to output until left parenthesis is encountered
					while (!operatorStack.isEmpty()) {
						String o2 = operatorStack.peek().getOperator();
						if (o2.equals("(")) {
							operatorStack.pop();
							break;
						} else {
							output.add(operatorStack.pop());
						}
					}
				} else {
					//add operator based on precedence
					while (!operatorStack.isEmpty()) {
						String o2 = operatorStack.peek().getOperator();
						if (o2.equals("(")) {
							break;
						}
						if (Operators.hasHigherPrecedence(o2, o1) || 
								(Operators.hasEqualPrecedence(o2, o1) && Operators.isLeftAssociative(o1))) {
							output.add(operatorStack.pop());
						} else {
							break;
						}
					}
					operatorStack.push(token);
				}		
			}
		}
		
		while (!operatorStack.isEmpty()) {
			output.add(operatorStack.pop());
		}
		
		return output.toArray(new Token[0]);
	}
	
	
	
	private String[] tokenize(String equation) {
		//remove white space
		String eq = equation.trim().replaceAll(" ", "");
		
		
		List<String> tokens = new ArrayList<String>();
		
		//tokenize equation string
		int i0 = 0;
		CharType previousType = CharType.uninitialized;
		CharType type = InputCharType.getCharType(eq.charAt(0),previousType);
		for (int i1 = 1; i1 < eq.length(); i1++) {
			previousType = type;
			
			char c = eq.charAt(i1);
			CharType t = InputCharType.getCharType(c,previousType);

			if (t != type || t == CharType.parenthesis) {
				//push to token list
				tokens.add(eq.substring(i0, i1));
				i0 = i1;
				type = t;
			} 
		}
		if (eq.length() > i0) {
			tokens.add(eq.substring(i0));
		}
		
		return tokens.toArray(new String[0]);
	}
	
	
	

}