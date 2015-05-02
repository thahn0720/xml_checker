package thahn.java.xmlchecker.parser;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

/**
 *
 * @author th0720.ahn
 *
 */
public class DescriptorTag {
	
	private String tagName;
	private String tagValue;
	private List<Attribute> attrs;
	private TextPosition startTagPosition;
	private TextPosition endTagPosition;
	private TextPosition valuePosition;
	
	private DescriptorTag() {
	}

	public static DescriptorTagBuilder builder() {
		return new DescriptorTagBuilder();
	}
	
	public String getTagName() {
		return tagName;
	}
	
	public String getTagValue() {
		return tagValue;
	}

	public List<Attribute> getAttrs() {
		return attrs;
	}
	
	/**
	 * tag's attr -> __description
	 * @param attrName
	 * @return
	 */
	public Attribute getAttr(String attrName) {
		for (Attribute attr : attrs) {
			if (attr.getQualifiedName().equals(attrName)) {
				return attr;
			}
		}
		return null;
	}
	
	/**
	 * attr's attr -> attr__description
	 * @param standardAttrName
	 * @return
	 */
	public Attribute getAttrOfAttr(String standardAttrName) {
		for (Attribute attr : attrs) {
			if (attr.getQualifiedName().endsWith(standardAttrName)) {
				return attr;
			}
		}
		return null;
	}
	
	public Attribute getAttrOfAttr(String attrName, String standardAttrName) {
		String fullName = attrName + standardAttrName;
		for (Attribute attr : attrs) {
			if (attr.getQualifiedName().equals(fullName)) {
				return attr;
			}
		}
		return null;
	}

	public TextPosition getStartTagPosition() {
		return startTagPosition;
	}

	public TextPosition getEndTagPosition() {
		return endTagPosition;
	}

	public TextPosition getValuePosition() {
		return valuePosition;
	}

	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}
	
	public void setStartTagPosition(int start, int end) {
		this.startTagPosition = TextPosition.builder().from(startTagPosition != null, startTagPosition)
				.start(start).end(end).build();
	}
	
	public void setStartTagPosition(int line, int column, int length) {
		this.startTagPosition = TextPosition.builder().from(startTagPosition != null, startTagPosition)
				.line(line).column(column).length(length).build();
	}
	
	public void setEndTagPosition(int start, int end) {
		this.endTagPosition = TextPosition.builder().from(endTagPosition != null, endTagPosition)
				.start(start).end(end).build();
	}
	
	public void setEndTagPosition(int line, int column, int length) {
		this.endTagPosition = TextPosition.builder().from(endTagPosition != null, endTagPosition)
				.line(line).column(column).length(length).build();
	}
	
	public void setValuePosition(int start, int end) {
		this.valuePosition = TextPosition.builder().from(valuePosition != null, valuePosition)
				.start(start).end(end).build();
	}
	
	public void setValuePosition(int line, int column, int length) {
		this.valuePosition = TextPosition.builder().from(valuePosition != null, valuePosition)
				.line(line).column(column).length(length).build();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DescriptorTag [tagName=").append(tagName)
				.append(", tagValue=").append(tagValue).append(", attrs=")
				.append(attrs).append("]");
		return builder.toString();
	}

	public static class DescriptorTagBuilder {

		private DescriptorTag m = new DescriptorTag();
		
		private DescriptorTagBuilder() {
		}
		
		public DescriptorTagBuilder tagName(String tagName) {
			m.tagName = tagName;
			return this;
		}
		
		public DescriptorTagBuilder tagValue(String tagValue) {
			m.tagValue = tagValue;
			return this;
		}
		
		public DescriptorTagBuilder attributes(Attributes attrs) {
			if (m.attrs == null) {
				m.attrs = new ArrayList<>();
			}
			for (int i = 0; i < attrs.getLength(); i++) {
				m.attrs.add(Attribute.builder().localName(attrs.getLocalName(i)).qualifiedName(attrs.getQName(i))
						.value(attrs.getValue(i)).build());
			}
			return this;
		}
		
		public DescriptorTagBuilder attributes(List<Attribute> attrs) {
			m.attrs = attrs;
			return this;
		}
		
		public DescriptorTagBuilder from(DescriptorTag tag) {
			m = tag;
			return this;
		}
		
		public DescriptorTag build() {
			return m;
		}
	}
	
	public static class TextPosition {
		
		private int line;
		private int column;
		private int length;
		
		private int start;
		private int end;
		
		private TextPosition() {
		}
		
		public static TextPositionBuilder builder() {
			return new TextPositionBuilder();
		}
			
		public int getStart() {
			return start;
		}
		
		public int getEnd() {
			return end;
		}

		public int getLine() {
			return line;
		}

		public int getColumn() {
			return column;
		}

		public int getLength() {
			return length;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("TextPosition [line=").append(line)
					.append(", column=").append(column).append(", length=")
					.append(length).append(", start=").append(start)
					.append(", end=").append(end).append("]");
			return builder.toString();
		}

		public static class TextPositionBuilder {

			private TextPosition m = new TextPosition();

			public TextPositionBuilder start(int offset) {
				this.m.start = offset;
				return this;
			}

			public TextPositionBuilder end(int len) {
				this.m.end = len;
				return this;
			}
			
			public TextPositionBuilder line(int line) {
				this.m.line = line;
				return this;
			}
			
			public TextPositionBuilder column(int column) {
				this.m.column = column;
				return this;
			}
			
			public TextPositionBuilder length(int length) {
				this.m.length = length;
				return this;
			}
			
			public TextPositionBuilder from(TextPosition from) {
				this.m = from;
				return this;
			}
			
			public TextPositionBuilder from(boolean is, TextPosition from) {
				if (is) {
					this.m = from;
				}
				return this;
			}

			public TextPosition build() {
				return m;
			}
		}
	}
}
