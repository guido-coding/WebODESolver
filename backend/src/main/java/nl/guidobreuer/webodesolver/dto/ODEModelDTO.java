package nl.guidobreuer.webodesolver.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nl.guidobreuer.webodesolver.exception.InvalidInputException;

public record ODEModelDTO(String dependentVar, String uom, List<ODEEquationDTO> equations, List<Map<String, Object>> constants) {
	
	
	
	@SuppressWarnings("unchecked")
	public static ODEModelDTO createODEModel(Map<String, ?> map) throws InvalidInputException {
		String dependentVar = (String) map.get("dependentVar");
		Object uom = map.get("uom");
		

		List<ODEEquationDTO> equations = new ArrayList<ODEEquationDTO>();
		List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("equations");
		
		list.forEach(l -> {			
			if (l != null) {				
				equations.add(ODEEquationDTO.createODEEquationDTO(l));
			}
		});
		
		if (equations.isEmpty()) {
			throw new InvalidInputException("No equations provided as input.");
		}
		
		Object constantsObject = map.get("constants");
		List<Map<String, Object>> constants;
		if (constantsObject == null) {
			constants = new ArrayList<Map<String, Object>>();
		} else {
			constants = (List<Map<String, Object>>) constantsObject;
		}
		
		
		return new ODEModelDTO(
				dependentVar, 
				uom == null ? "" : uom.toString(),
				equations, 
				constants);
	}

}
