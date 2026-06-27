package nl.guidobreuer.webodesolver.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import equationparser.InvalidEquationException;
import nl.guidobreuer.webodesolver.dto.ModelConstantsDTO;

class MonteCarloParameterEstimator extends ParameterEstimator {

	
	MonteCarloParameterEstimator(String algorithm, double start, double end, double stepsize, ODEModel model,
			Map<String, double[]> expdata) {
		super(algorithm, start, end, stepsize, model, expdata);
	}
	
	
	@Override
	protected ParamEstimationCycleResult nextParamCycle(Map<String, ModelConstantsDTO> modelParamRanges, Map<String, Double> currentParamValues, double currentSSQ) throws InvalidEquationException {
		
		//create model parameter set
		Map<String, Double> modelParamSet = new HashMap<String, Double>();
		modelParamRanges.keySet().forEach(var -> {
			ModelConstantsDTO mcDTO = (modelParamRanges.get(var));
			if (mcDTO.include()) {
				double varValue = Math.random() * (mcDTO.max() - mcDTO.min()) + mcDTO.min();
				modelParamSet.put(var, varValue);				
			} else {
				modelParamSet.put(var, mcDTO.defaultValue());
			}
		});
		
		//integrate and calcualte ssq
		List<Map<String, Double>> result = integrate(modelParamSet);
		double ssq = calculateSSQ(result);
		
		//if fit is better, update parameters
		if (ssq < currentSSQ) {
			currentSSQ = ssq;
			return new ParamEstimationCycleResult(
					modelParamSet,
					ssq,
					true);
		} else {
			return new ParamEstimationCycleResult(
					modelParamSet,
					ssq,
					false);
		}
	}
	
}
