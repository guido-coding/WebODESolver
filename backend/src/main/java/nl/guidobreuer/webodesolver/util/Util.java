package nl.guidobreuer.webodesolver.util;

public class Util {
	
	public static double extractDouble(Object valueObject) {
		double value;
		if (valueObject instanceof String) {
			value = Double.parseDouble((String)valueObject);
		} else if (valueObject instanceof Double) {
			value = ((Double) valueObject).doubleValue();
		} else if (valueObject instanceof Integer) {
			value = ((Integer) valueObject).doubleValue();
		} else {
			throw new IllegalArgumentException("Invalid constant");
		}
		return value;
	}

	public static boolean extractBoolean(Object valueObject) {
		boolean value;
		if (valueObject instanceof String) {
			String s = valueObject.toString();
			if (s.equals("true")) {
				value = true;
			} else if (s.equals("false")) {
				value = false;
			} else {
				
				value = false;
			}
		} else if (valueObject instanceof Boolean) {
			value = ((Boolean) valueObject).booleanValue();
		} else {
			value = false;
		}
		return value;
	}
}
