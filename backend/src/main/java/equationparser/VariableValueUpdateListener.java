package equationparser;

import java.util.Map;

public interface VariableValueUpdateListener {

	public void valuesUpdated(Map<String, Double> newValues);
	
}
