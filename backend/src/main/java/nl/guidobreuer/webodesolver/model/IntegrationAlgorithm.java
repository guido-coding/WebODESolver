package nl.guidobreuer.webodesolver.model;

import java.util.Map;

import equationparser.InvalidEquationException;
import nl.guidobreuer.webodesolver.exception.NaNResultException;

public interface IntegrationAlgorithm {
	
	public Map<String, Double> getDifferentials(ODEModel model, Map<String, Double> values, double stepsize) throws InvalidEquationException, NaNResultException ;

}
