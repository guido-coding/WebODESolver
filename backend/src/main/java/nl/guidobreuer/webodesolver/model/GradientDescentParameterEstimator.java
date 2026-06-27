package nl.guidobreuer.webodesolver.model;

import java.util.HashMap;
import java.util.Map;

import equationparser.InvalidEquationException;
import nl.guidobreuer.webodesolver.dto.ModelConstantsDTO;

class GradientDescentParameterEstimator extends ParameterEstimator  {
	
	
	private final double ALPHA = 0.005;

	GradientDescentParameterEstimator(String algorithm, double start, double end, double stepsize, ODEModel model,
			Map<String, double[]> expdata) {
		super(algorithm, start, end, stepsize, model, expdata);
	}

	
	
	@Override
	protected ParamEstimationCycleResult nextParamCycle(Map<String, ModelConstantsDTO> modelParamRanges,
			Map<String, Double> currentParamValues, double currentSSQ) throws InvalidEquationException {

		//clone parameter map
		Map<String, Double> paramValues = new HashMap<String, Double>();
		currentParamValues.keySet().forEach(var -> {
			paramValues.put(var, currentParamValues.get(var).doubleValue());
		});
		
		Map<String, Double> gradients = new HashMap<String, Double>();
		
		
		//reference ssq
		double refSSQ =  calculateSSQ(integrate(paramValues));
		
		//calculate gradients
		for (String var : paramValues.keySet()) {
			//ignore parameters not to be included
			if (!modelParamRanges.get(var).include()) {
				continue;
			}
			
			//set adjusted parameter value to calcualte gradient
			double oldParamValue = paramValues.get(var);
			double newParamValue = oldParamValue + ALPHA * (modelParamRanges.get(var).max() - modelParamRanges.get(var).min());
			paramValues.put(var, newParamValue);
			
			double ssq = calculateSSQ(integrate(paramValues));
			double gradient = ssq - refSSQ; //negative gradient indicates improvement, positive gradient indicates worse result.
			gradients.put(var, gradient);
			
			//reset model parameter to old value for next loop iteration
			paramValues.put(var, oldParamValue);
		}
		
		
		//update parameters
		for (String var : paramValues.keySet()) {
			ModelConstantsDTO paramrange = modelParamRanges.get(var);
			
			//ignore parameters not to be included
			if (!paramrange.include()) {
				continue;
			}
			
			double gradient = gradients.get(var);
			double newValue = paramValues.get(var);
			if (gradient < 0) {
				newValue += ALPHA * (paramrange.max() - paramrange.min());
			} else {
				newValue -= ALPHA * (paramrange.max() - paramrange.min());
			}
			if (newValue > paramrange.max()) {
				newValue = paramrange.max();
			} else if (newValue < paramrange.min()) {
				newValue = paramrange.min();
			}
			paramValues.put(var, newValue);
		}
	
		
		
		return new ParamEstimationCycleResult(
				paramValues,
				refSSQ,
				true);
	}
	
	

}
