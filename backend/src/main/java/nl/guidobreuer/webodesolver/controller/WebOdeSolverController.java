package nl.guidobreuer.webodesolver.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import equationparser.InvalidEquationException;
import nl.guidobreuer.webodesolver.dto.GraphDTO;
import nl.guidobreuer.webodesolver.dto.IntegrationInstructionDTO;
import nl.guidobreuer.webodesolver.dto.ModelConstantsDTO;
import nl.guidobreuer.webodesolver.dto.ParamEstimationDTO;
import nl.guidobreuer.webodesolver.exception.InvalidInputException;
import nl.guidobreuer.webodesolver.model.DatasetBuilder;
import nl.guidobreuer.webodesolver.model.ParameterEstimator;
import nl.guidobreuer.webodesolver.model.Integrator;
import nl.guidobreuer.webodesolver.model.ODEModel;


@RestController
public class WebOdeSolverController {
	
	
	@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4173", "https://apps.guidobreuer.nl"})
	@PostMapping("/plotuserdata")
	public List<GraphDTO> plotUserdata(
			@RequestBody Map<String, Object> body) throws InvalidEquationException, InvalidInputException {

		long start = System.currentTimeMillis();
		
		IntegrationInstructionDTO integrationInstruction = IntegrationInstructionDTO.createIntegrationInstruction(body);
		ODEModel model = new ODEModel(integrationInstruction.odeModel());
		
		//load user data to fit model to
		Map<String, double[]> expdata = null;
		try {
			expdata = DatasetBuilder.mapData(integrationInstruction.expdata());
		} catch (NumberFormatException | IOException e) {
			throw new IllegalStateException("No (valid) data provided to be used to fit model.");
		}
		if (expdata == null) {
			throw new IllegalStateException("No input data provided that can be shown.");
		}
		
		//create output
		List<GraphDTO> output = DatasetBuilder.createDataset();
		DatasetBuilder.appendUserdataDataset(output, expdata, model);
		
		long end = System.currentTimeMillis();
		System.out.print(DateFormat.getDateTimeInstance().format(new Date()) + ": --- ");
		System.out.println("duration: " + (end-start) + " ms");
		
		return output;
	}
	
	
	
	@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4173", "https://apps.guidobreuer.nl"})
	@PostMapping("/estimateparams")
	public ParamEstimationDTO estimateParams(
			@RequestBody Map<String, Object> body) throws InvalidEquationException, InvalidInputException {

		long start = System.currentTimeMillis();
		
		//extract all parameters and setup model
		IntegrationInstructionDTO integrationInstruction = IntegrationInstructionDTO.createIntegrationInstruction(body);
		ODEModel model = new ODEModel(integrationInstruction.odeModel());
		Map<String, ModelConstantsDTO> modelParams = ModelConstantsDTO.createModelConstantsMap(integrationInstruction.odeModel().constants());
		
		//load user data to fit model to
		Map<String, double[]> expdata = null;
		try {
			expdata = DatasetBuilder.mapData(integrationInstruction.expdata());
		} catch (NumberFormatException | IOException e) {
			throw new IllegalStateException("No (valid) data provided to be used to fit model.");
		}
		if (expdata == null) {
			throw new IllegalStateException("No input data provided that is needed to fit parameters. Either integrate the model without fitting or specify input data.");
		}
		
		
		//estimate parameters
		ParameterEstimator estimator = ParameterEstimator.getParameterEstimator(
				body.get("optimizationalgorithm").toString(),
				integrationInstruction.integrationParameters().algorithm(),
				integrationInstruction.integrationParameters().start(), 
				integrationInstruction.integrationParameters().end(), 
				integrationInstruction.integrationParameters().stepsize(), 
				model,
				expdata);
		Map<String, Double> estimatedParameters = estimator.estimateParameters(modelParams);
		
		
		//set model parameters to estimated parameters
		modelParams.keySet().forEach(var -> {
			model.setConstantValue(var, estimatedParameters.get(var));
		});

		//integrate model
		Integrator integrator = new Integrator(integrationInstruction.integrationParameters().algorithm());
		List<Map<String, Double>> result = integrator.integrate(integrationInstruction.integrationParameters().start(), integrationInstruction.integrationParameters().end(), integrationInstruction.integrationParameters().stepsize(), model);
		
		//create output
		List<GraphDTO> output = DatasetBuilder.createDataset(model);
		DatasetBuilder.appendModelDataset(output, result, model);
		DatasetBuilder.appendUserdataDataset(output, expdata, model);
		
		long end = System.currentTimeMillis();
		System.out.print(DateFormat.getDateTimeInstance().format(new Date()) + ": --- ");
		System.out.println("duration: " + (end-start) + " ms");
		
		return new ParamEstimationDTO(
				output,
				estimatedParameters
				);
	}
	
	
	
	@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4173", "https://apps.guidobreuer.nl"})
	@PostMapping("/integrate")
	public List<GraphDTO> integrate(
			@RequestBody Map<String, Object> body) throws InvalidEquationException, InvalidInputException {

		long start = System.currentTimeMillis();
		
		IntegrationInstructionDTO integrationInstruction = IntegrationInstructionDTO.createIntegrationInstruction(body);
		
		ODEModel model = new ODEModel(integrationInstruction.odeModel());
		Integrator integrator = new Integrator(integrationInstruction.integrationParameters().algorithm());
		List<Map<String, Double>> result = integrator.integrate(integrationInstruction.integrationParameters().start(), integrationInstruction.integrationParameters().end(), integrationInstruction.integrationParameters().stepsize(), model);
		
		//System.out.println(result.size());
		
		Map<String, double[]> expdata = null;
		try {
			expdata = DatasetBuilder.mapData(integrationInstruction.expdata());
		} catch (NumberFormatException | IOException e) {
			System.err.println(e.getMessage());
		}
		
		//List<GraphDTO> output = DatasetBuilder.buildDataset(result, model, expdata);
		
		List<GraphDTO> output = DatasetBuilder.createDataset(model);
		DatasetBuilder.appendModelDataset(output, result, model);
		DatasetBuilder.appendUserdataDataset(output, expdata, model);
		
		
		long end = System.currentTimeMillis();
		System.out.print(DateFormat.getDateTimeInstance().format(new Date()) + ": --- ");
		System.out.println("duration: " + (end-start) + " ms");
		
		return output;

	}
	
	
	@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:4173", "https://apps.guidobreuer.nl"})
	@PostMapping("/integrate/{variable}")
	public List<GraphDTO> variable(
			@RequestBody Map<String, Object> body,
			@PathVariable String variable) throws InvalidEquationException, InvalidInputException {
		
		long start = System.currentTimeMillis();
		//System.out.println("Performing sensitivity analysis for: " + variable);
		
		IntegrationInstructionDTO integrationInstruction = IntegrationInstructionDTO.createIntegrationInstruction(body);
		ODEModel model = new ODEModel(integrationInstruction.odeModel());
		Integrator integrator = new Integrator(integrationInstruction.integrationParameters().algorithm());
		
		//get user data
		Map<String, double[]> expdata = null;
		try {
			expdata = DatasetBuilder.mapData(integrationInstruction.expdata());
		} catch (NumberFormatException | IOException e) {
			System.err.println(e.getMessage());
		}

		
		//create output
		List<GraphDTO> output = DatasetBuilder.createDataset(model);
		DatasetBuilder.appendUserdataDataset(output, expdata, model);

		
		
		//model data
		double baseValue = model.getConstantValue(variable);
		
		for (double d = 0.5; d <= 1.51; d += 0.25) {
			double constvalue = baseValue * d;
			model.setConstantValue(variable, constvalue);
			List<Map<String, Double>> result = integrator.integrate(
					integrationInstruction.integrationParameters().start(), 
					integrationInstruction.integrationParameters().end(), 
					integrationInstruction.integrationParameters().stepsize(), 
					model);
			
			//String suffix = variable + "=" + constvalue + " " + model.getConstantUoM(variable);
			String suffix = "%s = %6.3f %s".formatted(variable, constvalue,  model.getConstantUoM(variable));
			DatasetBuilder.appendModelDataset(output, result, model, suffix);
		}
		
		
		long end = System.currentTimeMillis();
		System.out.print(DateFormat.getDateTimeInstance().format(new Date()) + ": --- ");
		System.out.println("duration: " + (end-start) + " ms");
		
		return output;
	}
	
	
	
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleInvalidInputException(Exception ex) {
		ex.printStackTrace();
		
		
		String errorMessage = ex.getClass().getSimpleName() + ": " + ex.getMessage();
		
		return ResponseEntity
				    .status(HttpStatus.INTERNAL_SERVER_ERROR)
				    .body(errorMessage);
	}

}
