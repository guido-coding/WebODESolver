package nl.guidobreuer.webodesolver.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.guidobreuer.webodesolver.util.Util;

public record ModelConstantsDTO(String name, String uom, double defaultValue, double min, double max, boolean include) {

	
	private static ModelConstantsDTO createModelConstantsDTO(Map<String, Object> data) {
		
		Object min = data.get("min");
		Object max = data.get("max");
		Object include = data.get("include");
		Object uom = data.get("uom");
		
		return new ModelConstantsDTO(
				data.get("varName").toString(),
				uom == null ? "" : uom.toString(),
				Util.extractDouble(data.get("varValue")),
				min == null ? Util.extractDouble(data.get("varValue")) : Util.extractDouble(min),
				max == null ? Util.extractDouble(data.get("varValue")) : Util.extractDouble(max),
				include == null ? false : Util.extractBoolean(include)
				);
	}
	
	public static Map<String, ModelConstantsDTO> createModelConstantsMap(List<Map<String, Object>> data) {
		Map<String, ModelConstantsDTO> map = new HashMap<String, ModelConstantsDTO>();
		
		for (Map<String, Object> m : data) {
			if (m == null) {
				continue;
			}
			ModelConstantsDTO dto = createModelConstantsDTO(m);
			map.put(
					dto.name, 
					dto);
		}
		
		return map;
	}
	
	
	
	
	
	
}
