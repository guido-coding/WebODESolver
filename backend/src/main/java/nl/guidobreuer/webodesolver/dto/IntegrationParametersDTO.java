package nl.guidobreuer.webodesolver.dto;

import java.util.Map;

import nl.guidobreuer.webodesolver.exception.InvalidInputException;

public record IntegrationParametersDTO(double start, double end, double stepsize, String algorithm) {
	


	
	public static IntegrationParametersDTO createIntegrationParameters(Map<String, ?> integrationParameters) throws InvalidInputException {
		
		
		var startVal = integrationParameters.get("start");
		var endVal = integrationParameters.get("end");
		var stepsizeVal = integrationParameters.get("stepsize");
		
		double start, end, stepsize;
		
		if (startVal instanceof Double) {
			start = ((Double) startVal).doubleValue();
		} else if (startVal instanceof Integer) {
			start = ((Integer) startVal).doubleValue();
		} else if (startVal instanceof String) {
			start = Double.parseDouble((String) startVal);
		} else {
			throw new IllegalArgumentException("Illegal input: " + startVal);
		}
		
		if (endVal instanceof Double) {
			end = ((Double) endVal).doubleValue();
		} else if (endVal instanceof Integer) {
			end = ((Integer) endVal).doubleValue();
		} else if (endVal instanceof String) {
			end = Double.parseDouble((String) endVal);
		} else {
			throw new IllegalArgumentException("Illegal input: " + endVal);
		}
		
		if (stepsizeVal instanceof Double) {
			stepsize = ((Double) stepsizeVal).doubleValue();
		} else if (stepsizeVal instanceof Integer) {
			stepsize = ((Integer) stepsizeVal).doubleValue();
		} else if (stepsizeVal instanceof String) {
			stepsize = Double.parseDouble((String) stepsizeVal);
		} else {
			throw new IllegalArgumentException("Illegal input: " + stepsizeVal);
		}
		
		if (stepsize <= 0) throw new InvalidInputException("Step size cannot be 0 or negative.");
		if (end <= start) throw new InvalidInputException("Integration end point should be more than integration start point.");
		
		return new IntegrationParametersDTO(start, end, stepsize, 
				integrationParameters.get("algorithm").toString());
	}

}
