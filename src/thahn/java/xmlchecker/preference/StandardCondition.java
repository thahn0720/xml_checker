package thahn.java.xmlchecker.preference;

/**
 * 
 * @author th0720.ahn
 *
 */
public class StandardCondition {
	
	/** regular expression */
	private String fileName;

	private StandardCondition() {
	}
	
	public static StandardConditionBuilder builder() {
		return new StandardConditionBuilder();
	}

	public String getFileName() {
		return fileName;
	}
	
	public static class StandardConditionBuilder {
		private StandardCondition m = new StandardCondition();
		
		private StandardConditionBuilder() {
		}
		
		public StandardConditionBuilder fileName(String fileName) {
			m.fileName = fileName;
			return this;
		}
		
		public StandardConditionBuilder from(StandardCondition from) {
			m = from;
			return this;
		}
		
		public StandardCondition build() {
			return m;
		}
	}
}
