package nl.guidobreuer.webodesolver.dto;

import java.util.Map;

public record ODEEquationDTO(
		String variable,
		String uom,
		String equation,
		String initialValue,
		boolean allownegative) {
	
	public static ODEEquationDTO createODEEquationDTO(Map<String, Object> eq) {
		
		Object uom = eq.get("uom");
		
		return new ODEEquationDTO(
				eq.get("variable").toString(),
				uom == null ? "" : uom.toString(),
				eq.get("equation").toString(),
				eq.get("initialValue").toString(),
				eq.containsKey("allownegative") ? ((Boolean) eq.get("allownegative")).booleanValue() : false);
		
	}

}
