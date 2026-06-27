package nl.guidobreuer.webodesolver.dto;

import java.util.Map;

import nl.guidobreuer.webodesolver.exception.InvalidInputException;

public record IntegrationInstructionDTO(
		IntegrationParametersDTO integrationParameters,
		ODEModelDTO odeModel,
		String expdata) {

	/*
	 * {
	 * 	"integrationParams":
	 * 		{
	 * 			"start":0,
	 * 			"end":1,
	 * 			"stepsize":0.1
	 * 		},
	 * 	"model":
	 * 		{
	 * 			"equations":
	 * 				[
	 * 					{"variable":"x","equation":"fds","initialValue":"1"},
	 * 					{"variable":"y","equation":"fddfsdfsdfsdfsdfsdfsdfss","initialValue":"1"}
	 * 				],
	 * 			"dependentVar":"t"
	 * 		},
	 * 	"expdata": "experimental data"
	 * }
	 * 
	 */
	
	
	@SuppressWarnings("unchecked")
	public static IntegrationInstructionDTO createIntegrationInstruction(Map<String, Object> body) throws InvalidInputException {
		
		IntegrationParametersDTO integrationParameters = IntegrationParametersDTO.createIntegrationParameters(
				(Map<String, ?>) body.get("integrationParams"));
		
		ODEModelDTO odeModel = ODEModelDTO.createODEModel((Map<String, ?>) body.get("model"));
		
		String expdata = (String) body.get("expdata");
		
		return new IntegrationInstructionDTO(integrationParameters, odeModel, expdata);
	}
	
}
