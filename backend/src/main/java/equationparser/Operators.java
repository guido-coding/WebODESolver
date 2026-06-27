package equationparser;

import java.util.HashMap;
import java.util.Map;

/**
 * Supported operators:
 * ^
 * -
 * +
 * *
 * /
 * sin
 * cos
 * tan
 * log (log10)
 * mod (modulus)
 * sqrt (square root)
 * 
 */
class Operators {
	
	private static class OperatorParameters {
		
		private final int precedence;
		private final Associativity associativity;
		private final int arguments;
		private final Operation operation;
		
		private enum Associativity {
			LEFT,
			RIGHT
		}
		
		private OperatorParameters(int precedence, Associativity associativity, int arguments, Operation operation) {
			this.precedence = precedence;
			this.associativity = associativity;
			this.arguments = arguments;
			this.operation = operation;
		}
		

		private int getPrecedence() {
			return precedence;
		}
		
		private Associativity getAssociativity() {
			return associativity;
		}
		private int getNumberOfArguments() {
			return arguments;
		}
		
		private Operation getOperation() {
			return operation;
		}
		
	}
	
	
	private static final Map<String, OperatorParameters> operatorMap;
	
	static {
		operatorMap = new HashMap<String, OperatorParameters>();
		
		operatorMap.put("^", new OperatorParameters(4, OperatorParameters.Associativity.RIGHT, 2, new Operation() {

			@Override
			public double performOperation(double[] values) {
				return Math.pow(values[0], values[1]);
			}
			
		}));
		
		operatorMap.put("*", new OperatorParameters(3, OperatorParameters.Associativity.LEFT, 2, new Operation() {

			@Override
			public double performOperation(double[] values) {
				return values[0] * values[1];
			}
			
		}));
		operatorMap.put("/", new OperatorParameters(3, OperatorParameters.Associativity.LEFT, 2, new Operation() {

			@Override
			public double performOperation(double[] values) {
				return values[0] / values[1];
			}
			
		}));
		operatorMap.put("+", new OperatorParameters(2, OperatorParameters.Associativity.LEFT, 2, new Operation() {

			@Override
			public double performOperation(double[] values) {
				return values[0]+values[1];
			}
			
		}));
		operatorMap.put("-", new OperatorParameters(2, OperatorParameters.Associativity.LEFT, 2, new Operation() {

			@Override
			public double performOperation(double[] values) {
				return values[0] - values[1];
			}
			
		}));
		operatorMap.put("sin", new OperatorParameters(5, OperatorParameters.Associativity.RIGHT, 1, new Operation() {

			@Override
			public double performOperation(double[] values) {
				return Math.sin(values[0]);
			}
			
		}));
		operatorMap.put("cos", new OperatorParameters(5, OperatorParameters.Associativity.RIGHT, 1, new Operation() {

			@Override
			public double performOperation(double[] values) {
				return Math.cos(values[0]);
			}
			
		}));
		operatorMap.put("tan", new OperatorParameters(5, OperatorParameters.Associativity.RIGHT, 1, new Operation() {

			@Override
			public double performOperation(double[] values) {
				return Math.tan(values[0]);
			}
			
		}));
		operatorMap.put("mod", new OperatorParameters(2, OperatorParameters.Associativity.LEFT, 2, new Operation() {

			@Override
			public double performOperation(double[] values) {
				return values[0] % values[1];
			}
			
		}));
		operatorMap.put("log", new OperatorParameters(5, OperatorParameters.Associativity.RIGHT, 1, new Operation() {

			@Override
			public double performOperation(double[] values) {
				return Math.log10(values[0]);
			}
			
		}));	
		operatorMap.put("sqrt", new OperatorParameters(4, OperatorParameters.Associativity.RIGHT, 1, new Operation() {

			@Override
			public double performOperation(double[] values) {
				return Math.sqrt(values[0]);
			}
			
		}));		
	}
	
	static boolean isValidOperator(String operator) {
		return operatorMap.containsKey(operator);
	}
	
	
	static boolean hasHigherPrecedence(String operator1, String operator2) throws InvalidEquationException {
		
		if (operatorMap.get(operator1) == null) throw new InvalidEquationException("invalid operator: " + operator1);
		if (operatorMap.get(operator2) == null) throw new InvalidEquationException("invalid operator: " + operator2);
		
		int o1 = operatorMap.get(operator1).getPrecedence();
		int o2 = operatorMap.get(operator2).getPrecedence();
		return o1 > o2;
	}
	
	static boolean hasEqualPrecedence(String operator1, String operator2) throws InvalidEquationException {
		if (operatorMap.get(operator1) == null) throw new InvalidEquationException("invalid operator: " + operator1);
		if (operatorMap.get(operator2) == null) throw new InvalidEquationException("invalid operator: " + operator2);
		
		int o1 = operatorMap.get(operator1).getPrecedence();
		int o2 = operatorMap.get(operator2).getPrecedence();
		return o1 == o2;
	}
	
	static boolean isLeftAssociative(String operator) throws InvalidEquationException {
		if (operatorMap.get(operator) == null) throw new InvalidEquationException("invalid operator: " + operator);
		return operatorMap.get(operator).getAssociativity() == OperatorParameters.Associativity.LEFT;
	}
	
	static int getNumberOfArguments(String operator) throws InvalidEquationException {
		if (operatorMap.get(operator) == null) throw new InvalidEquationException("invalid operator: " + operator);
		return operatorMap.get(operator).getNumberOfArguments();
	}
	
	static double performOperation(String operator, double[] values) throws InvalidEquationException {
		if (operatorMap.get(operator) == null) throw new InvalidEquationException("invalid operator: " + operator);
		return operatorMap.get(operator).getOperation().performOperation(values);
	}
	
	
}
