package equationparser;


class InputCharType {
	
	
	enum CharType {
		uninitialized,
		numeric,
		operator,
		parenthesis,
		text;
	}
	
	static CharType getCharType(char c, CharType previousType) {
		if (isNumeric(c,previousType) ) {
			return CharType.numeric;
		} else if (isOperator(c,previousType)) {
			return CharType.operator;
		} else if (isParenthesis(c)) {
			return CharType.parenthesis;
		} else {
			return CharType.text;
		}
	}
	
	private static boolean isNumeric(char c, CharType previousType) {
		switch (c) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8': 
		case '9':
		case '.':
			if (previousType == CharType.text) {				
				return false;
			} else {
				return true;
			}
		case '-':
			if (previousType == CharType.numeric) {
				return false;
			} else {
				return true;
			}
		default: return false;
		
		}
	}

	private static boolean isOperator(char c, CharType previousType) {
		switch (c) {
		case '+':
		case '*':
		case '/':
		case '^':
			return true;
		case '-':
			if (previousType == CharType.numeric) {
				return true;
			} else {
				return false;
			}
		default: return false;
		
		}
	}
	
	private static boolean isParenthesis(char c) {
		switch(c) {
		case '(':
		case ')':
			return true;
		default: return false;
		}
	}
	
	
}
