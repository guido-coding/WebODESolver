package nl.guidobreuer.webodesolver.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import equationparser.EquationParser;
import equationparser.InvalidEquationException;
import nl.guidobreuer.webodesolver.dto.ODEEquationDTO;
import nl.guidobreuer.webodesolver.dto.ODEModelDTO;
import nl.guidobreuer.webodesolver.exception.InvalidInputException;
import nl.guidobreuer.webodesolver.exception.NaNResultException;

public class ODEModel {
	
	private final String dependentVar;
	private final String depVarUoM;
	private final List<String> independentVars;
	private final List<ODEEquation> equations;
	private final Map<String, Double> initialValues;
	private final Map<String, Boolean> allowsNegative;
	private final Map<String, Double> constants;
	private final Map<String, String> constantsUoM;

	public ODEModel(ODEModelDTO modelDTO) throws InvalidEquationException, InvalidInputException {
		dependentVar = modelDTO.dependentVar();
		depVarUoM = modelDTO.uom();
		equations = new ArrayList<ODEEquation>();
		initialValues = new HashMap<String, Double>();
		independentVars = new ArrayList<String>();
		allowsNegative = new HashMap<String, Boolean>();
		constants = new HashMap<String, Double>();
		constantsUoM = new HashMap<String, String>();
		
		Set<String> possibleDuplicates = new HashSet<String>();
		possibleDuplicates.add(dependentVar);
		for (ODEEquationDTO eq : modelDTO.equations()) {
			if (!possibleDuplicates.add(eq.variable())) throw new InvalidInputException("Duplicate entries for model variable: " + eq.variable()); 
			
			equations.add(new ODEEquation(eq));
			independentVars.add(eq.variable());
			initialValues.put(eq.variable(), Double.parseDouble(eq.initialValue()));
			allowsNegative.put(eq.variable(), eq.allownegative());
		}
		
		
		possibleDuplicates.clear();
		for (Map<String, Object> constant : modelDTO.constants()) {
			
			if (constant == null) continue;
			
			if (!possibleDuplicates.add(constant.get("varName").toString())) throw new InvalidInputException("Duplicate entries for constant: " + constant.get("varName").toString()); 
			
			Object valueObject = constant.get("varValue");
			double value;
			if (valueObject instanceof String) {
				value = Double.parseDouble((String)valueObject);
			} else if (valueObject instanceof Double) {
				value = ((Double) valueObject).doubleValue();
			} else if (valueObject instanceof Integer) {
				value = ((Integer) valueObject).doubleValue();
			} else {
				throw new IllegalArgumentException("Invalid constant");
			}
			
			
			Object uom = constant.get("uom");
			
			constants.put(
					constant.get("varName").toString(), 
					value);
			constantsUoM.put(
					constant.get("varName").toString(), 
					uom == null ? "" : uom.toString());
		}
		
	}

	public String getDependentVar() {
		return dependentVar;
	}
	
	public List<String> getIndependentVars() {
		return independentVars;
	}

	public List<ODEEquation> getEquations() {
		return equations;
	}

	public Map<String, Double> getInitialValues() {
		return initialValues;
	}
	
	public Map<String, Double> solveEquations(Map<String, Double> parameterValues) throws InvalidEquationException, NaNResultException {
		Map<String, Double> result = new HashMap<String, Double>();
		
		Map<String, Double> inputValues = new HashMap<String, Double>();
		inputValues.putAll(parameterValues);
		inputValues.putAll(constants);
		
		for (ODEEquation eq : equations) {
			result.put(eq.getVariable(), eq.solveEquation(inputValues));
		}
		
		return result;
	}
	
	public boolean isAllowedNegative(String variable) {
		return allowsNegative.get(variable).booleanValue();
	}
	
	public double getConstantValue(String constant) {
		Double val = constants.get(constant);
		if (val == null) throw new IllegalArgumentException("Constant does not exist");
		return val.doubleValue();
	}
	
	public double setConstantValue(String constant, double value) {
		return constants.put(constant, value);
	}

	public String getDepVarUoM() {
		return depVarUoM;
	}
	
	public String getConstantUoM(String constant) {
		return constantsUoM.get(constant);
	}
	
	public String getVariableUoM(String variable) {
		for (ODEEquation eq : equations) {
			if (eq.getVariable().equals(variable)) {
				return eq.getUoM();
			}
		}
		return "";
	}
	
}


class ODEEquation {
	
	private final String variable;
	private final String equationText;
	private final EquationParser equation;
	private final boolean allowNegative;
	private final String uom;
	
	ODEEquation(ODEEquationDTO eq) throws InvalidEquationException {
		variable = eq.variable();
		equationText = eq.equation();
		equation = new EquationParser(equationText);
		allowNegative = eq.allownegative();
		uom = eq.uom();
	}

	String getVariable() {
		return variable;
	}

	EquationParser getEquation() {
		return equation;
	}
	
	double solveEquation(Map<String, Double> parameterValues) throws InvalidEquationException, NaNResultException {
		double value = equation.resolveEquation(parameterValues);
		if (Double.isFinite(value)) {
			return value;
		} else {
			throw new NaNResultException("naN encountered");
		}
	}
	
	boolean allowsNegative() {
		return allowNegative;
	}

	String getUoM() {
		return uom;
	}
	
}