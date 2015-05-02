package thahn.java.xmlchecker.marker;

/**
 *
 * @author th0720.ahn
 *
 */
public enum MarkerType {
	
	GRAMMAR_PROBLEM("thahn.java.descriptorchecker.marker.xmlProblem.grammar"),
	DEPENDENCY_PROBLEM("thahn.java.descriptorchecker.marker.xmlProblem.dependency"),
	TAGNAME_PROBLEM("thahn.java.descriptorchecker.marker.xmlProblem.tagname"),
	REGEXP_PROBLEM("thahn.java.descriptorchecker.marker.xmlProblem.regexp"),
	REQUIRED_PROBLEM("thahn.java.descriptorchecker.marker.xmlProblem.required"),
	;

	private String type;
	MarkerType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
}
