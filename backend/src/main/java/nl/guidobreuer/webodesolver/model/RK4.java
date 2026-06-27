package nl.guidobreuer.webodesolver.model;

import java.util.HashMap;
import java.util.Map;

import equationparser.InvalidEquationException;
import nl.guidobreuer.webodesolver.exception.NaNResultException;

public class RK4 implements IntegrationAlgorithm {

	@Override
	public Map<String, Double> getDifferentials(ODEModel model, Map<String, Double> values, double stepsize)
			throws InvalidEquationException, NaNResultException {

		Map<String, Double> result = new HashMap<String, Double>();
		
		Map<String, Double> k1 = getK1(model, values);
		Map<String, Double> k2 = getK2(model, values, k1, stepsize);
		Map<String, Double> k3 = getK3(model, values, k2, stepsize);
		Map<String, Double> k4 = getK4(model, values, k3, stepsize);
		
		model.getIndependentVars().forEach(var -> {
			result.put(var, 
					(k1.get(var) + 2*k2.get(var) + 2*k3.get(var) + k4.get(var))/6
					);
		});
		
		return result;
	}

	private Map<String, Double> getK1(ODEModel model, Map<String, Double> values) throws InvalidEquationException, NaNResultException {
		return model.solveEquations(values);
	}
	
	private Map<String, Double> getK2(ODEModel model, Map<String, Double> values, Map<String, Double> k1, double stepsize) throws InvalidEquationException, NaNResultException {
		Map<String, Double> k2Input = new HashMap<String, Double>();
		k2Input.put(model.getDependentVar(), 
				values.get(model.getDependentVar() + stepsize/2));
		
		model.getIndependentVars().forEach(var -> {
			k2Input.put(var, 
					values.get(var) + k1.get(var)*stepsize/2);
		});
		
		return model.solveEquations(k2Input);
	}
	
	private Map<String, Double> getK3(ODEModel model, Map<String, Double> values, Map<String, Double> k2, double stepsize) throws InvalidEquationException, NaNResultException {
		Map<String, Double> k3Input = new HashMap<String, Double>();
		k3Input.put(model.getDependentVar(), 
				values.get(model.getDependentVar() + stepsize/2));
		
		model.getIndependentVars().forEach(var -> {
			k3Input.put(var, 
					values.get(var) + k2.get(var)*stepsize/2);
		});
		
		return model.solveEquations(k3Input);
	}
	
	private Map<String, Double> getK4(ODEModel model, Map<String, Double> values, Map<String, Double> k3, double stepsize) throws InvalidEquationException, NaNResultException {
		Map<String, Double> k4Input = new HashMap<String, Double>();
		k4Input.put(model.getDependentVar(), 
				values.get(model.getDependentVar() + stepsize));
		
		model.getIndependentVars().forEach(var -> {
			k4Input.put(var, 
					values.get(var) + k3.get(var)*stepsize);
		});
		
		return model.solveEquations(k4Input);
	}

}
