package thahn.java.xmlchecker.parser;


public class Attribute {

	private String localName;
	private String qualifiedName;
	private String value;

	private Attribute() {
	}

	public static AttributeBuilder builder() {
		return new AttributeBuilder();
	}
	
	public String getLocalName() {
		return localName;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public String getValue() {
		return value;
	}

	public static class AttributeBuilder {

		private Attribute m = new Attribute();

		public AttributeBuilder localName(String localName) {
			this.m.localName = localName;
			return this;
		}

		public AttributeBuilder qualifiedName(String qualifiedName) {
			this.m.qualifiedName = qualifiedName;
			return this;
		}
		
		public AttributeBuilder value(String value) {
			this.m.value = value;
			return this;
		}
		
		public AttributeBuilder from(Attribute from) {
			this.m = from;
			return this;
		}
		
		public Attribute build() {
			return m;
		}
	}
}
