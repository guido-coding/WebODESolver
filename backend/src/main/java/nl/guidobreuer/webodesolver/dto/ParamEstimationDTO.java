package nl.guidobreuer.webodesolver.dto;

import java.util.List;
import java.util.Map;

public class ParamEstimationDTO {

	private final List<GraphDTO> graphdata;
	private final Map<String, Double> estimatedParams;
	


	public ParamEstimationDTO(List<GraphDTO> graphdata, Map<String, Double> estimatedParams) {
		this.graphdata = graphdata;
		this.estimatedParams = estimatedParams;
	}
	
	public List<GraphDTO> getGraphdata() {
		return graphdata;
	}

	public Map<String, Double> getEstimatedParams() {
		return estimatedParams;
	}
	
}
