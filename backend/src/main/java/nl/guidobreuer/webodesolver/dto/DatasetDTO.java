package nl.guidobreuer.webodesolver.dto;

import java.util.ArrayList;
import java.util.List;

public class DatasetDTO {

	private final String label;
	private final String backgroundColor;
	private final String borderColor;
	private final boolean showLine;
	private final int pointRadius;
	


	private final List<GraphPointDTO> data;
	
	public DatasetDTO(
			String label,
			String backgroundColor,
			String borderColor,
			boolean showLine,
			int pointRadius) {
		
		this.label = label;
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
		this.showLine = showLine;
		this.pointRadius = pointRadius;
		
		data = new ArrayList<GraphPointDTO>();
	}
	
	public void addDatapoint(double x, double y) {
		data.add(new GraphPointDTO(x, y));
	}
	
	public String getLabel() {
		return label;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public boolean getShowLine() {
		return showLine;
	}

	public int getPointRadius() {
		return pointRadius;
	}

	public List<GraphPointDTO> getData() {
		return data;
	}
}
