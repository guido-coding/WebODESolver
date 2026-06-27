package nl.guidobreuer.webodesolver.dto;

import java.util.List;

public class GraphDTO {
	
	private final List<DatasetDTO> datasets;
	private final OptionsDTO options;
	private final String variable;
	
	public GraphDTO(
			List<DatasetDTO> datasets,
			OptionsDTO options,
			String variable) {
		
		this.datasets = datasets;
		this.options = options;
		this.variable = variable;
	}

	public List<DatasetDTO> getDatasets() {
		return datasets;
	}

	public OptionsDTO getOptions() {
		return options;
	}
	
	public String getVariable() {
		return variable;
	}
	
}
