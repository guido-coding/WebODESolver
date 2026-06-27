package nl.guidobreuer.webodesolver.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import equationparser.InvalidEquationException;
import nl.guidobreuer.webodesolver.exception.NaNResultException;

public class Integrator {
	
	private static final long MAX_DURATION = 2000; //ms
	
	private final IntegrationAlgorithm algorithm;
	
	public Integrator() {
		this(new RK4());
	}
	
	public Integrator(IntegrationAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
	
	public Integrator(String algorithm) {
		this(getAlgorithm(algorithm));
	}
	
	private static IntegrationAlgorithm getAlgorithm(String algorithm) {
		switch(algorithm) {
		case "RK4": return new RK4();
		case "Euler": return new Euler();
		default: return new RK4();
		}
	}
	
	private static final int ADJUSTMENT_FACTOR_UP = 2;
	private static final int ADJUSTMENT_FACTOR_DOWN = 4;
	private static final int MAX_ADJUSTMENT_COUNTER = 10;
	
	public List<Map<String, Double>> integrate(double start, double end, double stepsize, ODEModel model) throws InvalidEquationException {
		
		long startTime = System.currentTimeMillis();
		
		Map<String, Double> initialValues = new HashMap<String, Double>();
		initialValues.putAll(model.getInitialValues());
		initialValues.put(model.getDependentVar(), start);
		LinkedList<Map<String, Double>> result = new LinkedList<Map<String, Double>>();
		result.add(initialValues);
		
		Map<String, Double> nextResult = new HashMap<String, Double>();
		double t = start+stepsize;
		double currentStepsize = stepsize;
		int adjustmentCounter = 0;
		int rejected = 0;
		while (t <= end) {
			
			if (System.currentTimeMillis() - startTime >= MAX_DURATION) {
				//abort
				System.err.println("Integration aborted because max duration is exceeded.");
				break;
			}
			
			//integrate
			boolean nonAllowedZeroDetected = false;
			
			try {
				Map<String, Double> differentials = algorithm.getDifferentials(model, result.getLast(), currentStepsize);
				
				//calculate next timepoint
				for (String var : differentials.keySet()) {
					
					double resultvalue = result.getLast().get(var).doubleValue() + differentials.get(var)*currentStepsize;
					if (resultvalue < 0 && !model.isAllowedNegative(var)) {
						nonAllowedZeroDetected = true;
	
						break;
					}
					
					nextResult.put(var, resultvalue);
				}
			} catch(NaNResultException nan) {
				System.err.println("Integration aborted");
				break;
			}
			
			if (nonAllowedZeroDetected) {
				//reject result and recalculate using smaller step size
				nextResult.clear();
				currentStepsize = currentStepsize / ADJUSTMENT_FACTOR_DOWN;
				rejected++;
				continue;
			} 
			
			//add results
			Map<String, Double> timepoint = new HashMap<String, Double>();
			timepoint.putAll(nextResult);
			timepoint.put(model.getDependentVar(), t);
			result.add(timepoint);
			nextResult.clear();
			t += currentStepsize;
			
			
			//adjust stepsize back up
			if (currentStepsize < stepsize) {	
				adjustmentCounter++;
				if (adjustmentCounter > MAX_ADJUSTMENT_COUNTER) {					
					currentStepsize = currentStepsize * ADJUSTMENT_FACTOR_UP;
					if (currentStepsize > stepsize) currentStepsize = stepsize;
					adjustmentCounter = 0;
				}
			}
			
		}
		//System.out.println("Total rejected " + rejected);
		return result;
	}

}
