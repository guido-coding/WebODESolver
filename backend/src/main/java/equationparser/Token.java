package equationparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

abstract class Token {
	
	abstract boolean isNumeric();
	protected abstract double getNumericValue() throws InvalidEquationException;
	
	abstract boolean isOperator();
	protected abstract String getOperator();

	private static Token createToken(String text, List<VariableValueUpdateListener> variableValueUpdateListeners) throws InvalidEquationException {
		
		if (Operators.isValidOperator(text)) {				
			return new OperatorToken(text);
		} else if(text.equals("(") || text.equals(")")) { 
			return new OperatorToken(text);
		} else {			
			try {
				double value = Double.parseDouble(text);
				return new NumericToken(value);
			} catch(NumberFormatException e) {
				if (variableValueUpdateListeners == null) {					
					throw new InvalidEquationException("Invalid token: " + text + ". If this token is intended as a variable name, call this function providing a List<VariableValueUpdateListener>");
				} else {
					NumericVariableToken token = new NumericVariableToken(text);
					variableValueUpdateListeners.add(token);
					return token;
				}
			}
		}
	}
	
	
	static Token[] createTokens(String[] text, List<VariableValueUpdateListener> variableValueUpdateListeners) throws InvalidEquationException {
		List<Token> tokens = new ArrayList<Token>();
		for (int i=0; i<text.length; i++) {
			Token token = createToken(text[i], variableValueUpdateListeners);
			//change negative sign of token to subtract operator in case numeric token follows a variable name or parenthesis close operator
			if (token instanceof NumericToken && !(token instanceof NumericVariableToken) && !tokens.isEmpty() && token.getNumericValue() < 0) {
				if (tokens.get(tokens.size()-1) instanceof NumericVariableToken 
						|| tokens.get(tokens.size()-1) instanceof OperatorToken && tokens.get(tokens.size()-1).getOperator().equals(")")) {
					tokens.add(createToken("-", variableValueUpdateListeners));
					tokens.add(new NumericToken(token.getNumericValue()*-1));
				} else {
					tokens.add(token);
					//System.err.println(token);
				}
			} else {				
				tokens.add(token);
			}
		}
		return tokens.toArray(new Token[0]);
	}
	
	static Token[] createTokens(String[] text) throws InvalidEquationException {
		return createTokens(text, null);
	}
}



class NumericToken extends Token {

	private final double value;
	
	NumericToken(double value) {
		this.value = value;
	}
	
	@Override
	boolean isNumeric() {
		return true;
	}

	@Override
	protected double getNumericValue() throws InvalidEquationException {
		return value;
	}

	@Override
	boolean isOperator() {
		return false;
	}

	@Override
	protected String getOperator() {
		throw new UnsupportedOperationException("cannot get operator on numeric token");
	}
	
	@Override
	public String toString() {
		return value + " (numeric token)";
	}
	
}

class NumericVariableToken extends NumericToken implements VariableValueUpdateListener {
	
	private final String variableName;
	private Double value;
	
	NumericVariableToken(String variableName) {
		super(0);
		this.variableName = variableName;
		value = null;
	}
	
	@Override
	protected double getNumericValue() throws InvalidEquationException {
		if (value != null) {
			return value.doubleValue();
		} else {
			throw new InvalidEquationException("Variable " + variableName + " not initialized with a value");
		}
	}

	@Override
	public void valuesUpdated(Map<String, Double> newValues) {
		value = newValues.get(variableName);
	}
	
	@Override
	public String toString() {
		return "Variable: " + variableName + ", value: " + value + " (Numeric variable token)";
	}

}





class OperatorToken extends Token {
	
	private final String operator;
	
	OperatorToken(String operator) {
		this.operator = operator;
	}

	@Override
	boolean isNumeric() {
		return false;
	}

	@Override
	protected double getNumericValue() throws InvalidEquationException {
		throw new UnsupportedOperationException("cannot get numeric value on operator token");
	}

	@Override
	boolean isOperator() {
		return true;
	}

	@Override
	protected String getOperator() {
		return operator;
	}
	
	@Override
	public String toString() {
		return operator + " (operator token)";
	}
	
}

