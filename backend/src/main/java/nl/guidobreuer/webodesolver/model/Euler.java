package nl.guidobreuer.webodesolver.model;

import java.util.Map;

import equationparser.InvalidEquationException;
import nl.guidobreuer.webodesolver.exception.NaNResultException;

public class Euler implements IntegrationAlgorithm {

	@Override
	public Map<String, Double> getDifferentials(ODEModel model, Map<String, Double> values, double stepsize) throws InvalidEquationException, NaNResultException {
		return model.solveEquations(values);
	}

}
