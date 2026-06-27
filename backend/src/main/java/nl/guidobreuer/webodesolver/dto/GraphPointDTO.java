package nl.guidobreuer.webodesolver.dto;

public class GraphPointDTO {

	private final double x;
	private final double y;
	
	public GraphPointDTO(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
}
