package nl.guidobreuer.webodesolver.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import equationparser.InvalidEquationException;
import nl.guidobreuer.webodesolver.dto.ModelConstantsDTO;

public abstract class ParameterEstimator {
	
	private final String algorithm;
	private final double start, end, stepsize;
	private final ODEModel model;
	private final Map<String, double[]> expdata;
	
	private static final int MAX_ITERATIONS = 100;
	private static final int MAX_DURATION = 10_000; //ms
	
	ParameterEstimator(String algorithm, double start, double end, double stepsize, ODEModel model, Map<String, double[]> expdata) {
		this.algorithm = algorithm;
		this.start = start;
		this.end = end;
		this.stepsize = stepsize;
		this.model = model;
		this.expdata = expdata;
	}
	
	
	
	public static ParameterEstimator getParameterEstimator(String optimizationAlgorithm, String integrationAlgorithm, double start, double end, double stepsize, ODEModel model,
			Map<String, double[]> expdata) {
		
		switch(optimizationAlgorithm) {
		case "montecarlo": return new MonteCarloParameterEstimator(integrationAlgorithm, start, end, stepsize, model, expdata);
		case "gradientdescent": return new GradientDescentParameterEstimator(integrationAlgorithm, start, end, stepsize, model, expdata);
		default: return new GradientDescentParameterEstimator(integrationAlgorithm, start, end, stepsize, model, expdata);
		}
		
	}

	public Map<String, Double> estimateParameters(Map<String, ModelConstantsDTO> modelParams) throws InvalidEquationException {
		long initTime = System.currentTimeMillis();
		
		Map<String, Double> map = new HashMap<String, Double>();
		
		//initialize with default values
		modelParams.keySet().forEach(var -> {
			map.put(
					var, 
					modelParams.get(var).defaultValue());
		});
		
		
		/*
		 * select combination of parameters
		 * integrate model
		 * calculate sum of squared differences (Ssq)
		 * select lowest ssq
		 * return associated parameters
		 * 
		 */
		
		
		
		
		
		double currentSSQ = Double.MAX_VALUE;
		int i;
		for (i=0; i<MAX_ITERATIONS; i++) {
			
			if (System.currentTimeMillis() - initTime >= MAX_DURATION) {
				System.err.println("Integration aborted due to maximum time exceeded after " + i + " iterations.");
				break;
			}
			
			//get updated set of model parameters
			ParamEstimationCycleResult result = nextParamCycle(modelParams, map, currentSSQ);
			
			//update global set of model parameters
			if (result.parametersChanged()) {				
				Map<String, Double> modelParamSet = result.modelParams();
				modelParamSet.keySet().forEach(var -> {
					map.put(var, modelParamSet.get(var));
				});
				currentSSQ = result.ssq();
			}
		}
		
		return map;
	}
	
	
	
	protected abstract ParamEstimationCycleResult nextParamCycle(Map<String, ModelConstantsDTO> modelParamRanges, Map<String, Double> currentParamValues, double currentSSQ) throws InvalidEquationException;
	
	protected static record ParamEstimationCycleResult(
			Map<String, Double> modelParams,
			double ssq,
			boolean parametersChanged) {
	}
	
	
	protected List<Map<String, Double>> integrate(Map<String, Double> modelParamSet) throws InvalidEquationException {
		//set parameters to model
		modelParamSet.keySet().forEach(var -> {
			model.setConstantValue(var, modelParamSet.get(var));
		});
		
		Integrator integrator = new Integrator(algorithm);
		List<Map<String, Double>> result = integrator.integrate(start, end, stepsize, model);
		return result;
	}
	

	protected double calculateSSQ(List<Map<String, Double>> modelData) {
		return calculateSSQ(expdata, modelData);
	}
	
	private double calculateSSQ(Map<String, double[]> expdata, List<Map<String, Double>> modelData) {
		
		int userData = 0;
		double ssq = 0;
		
		//filter out datapoints before start of model
		double modelStart = modelData.get(0).get(model.getDependentVar()).doubleValue();
		double[] userDepVar = expdata.get(model.getDependentVar());
		int userDataLength = userDepVar.length;
		double currentUserTimepoint = userDepVar[userData];
		
		while (userData < userDataLength) {			
			if (currentUserTimepoint < modelStart) {
				userData++;
				currentUserTimepoint = userDepVar[userData];
			} else {
				break;
			}
		}
			
		
		
		
		for (Map<String, Double> modelDataTimepoint : modelData) {
			double modelTime = modelDataTimepoint.get(model.getDependentVar()).doubleValue();
			
			if (modelTime >= currentUserTimepoint) {
				//matching timepoint, compare and add result to ssq
				
				for (String var : model.getIndependentVars()) {
					double[] userDataArray = expdata.get(var); 
					if (userDataArray == null) {
						continue;
					}
					double userValue = userDataArray[userData];
					double modelvalue = modelDataTimepoint.get(var).doubleValue();
					double sqdiff = Math.pow(userValue - modelvalue, 2);
					ssq += sqdiff;
				}
				
				//setup for next user timepoint
				userData++;
				if (userData >= userDataLength) {
					//all user data processed
					break;
				}
				currentUserTimepoint = expdata.get(model.getDependentVar())[userData];
			}
			
		}
		
		
		return ssq;
	}
	
}
