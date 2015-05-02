package thahn.java.xmlchecker.preference;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonRootName;

import thahn.java.xmlchecker.util.MyStrings;

/**
 * 
 * @author th0720.ahn
 *
 */
@JsonRootName("standardXmlInfo")
public class StandardXmlInfo {

	public static final String ROOT_TAG_SEPARATOR = ",";
	
	private String standardXmlPath;
	private List<String> rootTag;
	private StandardCondition condition;

	private StandardXmlInfo() {
	}
	
	public static StandardXmlInfoBuilder builder() {
		return new StandardXmlInfoBuilder();
	}

	public String getStandardXmlPath() {
		return standardXmlPath;
	}

	public List<String> getRootTag() {
		return rootTag;
	}
	
	public StandardCondition getCondition() {
		return condition;
	}

	public static String joinRootTagString(List<String> list) {
		return MyStrings.join(list, ROOT_TAG_SEPARATOR);
	}
	
	public static List<String> splitRootTagString(String text) {
		return MyStrings.split(text, StandardXmlInfo.ROOT_TAG_SEPARATOR);
	}
	
	@Override
	public String toString() {
		return "StandardXmlInfo [standardXmlPath=" + standardXmlPath
				+ ", rootTag=" + rootTag + "]";
	}

	public static class StandardXmlInfoBuilder {
		
		private StandardXmlInfo	m = new StandardXmlInfo();
		
		private StandardXmlInfoBuilder() {
		}
		
		public StandardXmlInfoBuilder standardXmlPath(String path) {
			m.standardXmlPath = path;
			return this;
		}
		
		public StandardXmlInfoBuilder standardCondition(StandardCondition condition) {
			m.condition = condition;
			return this;
		}
		
		public StandardXmlInfoBuilder rootTag(List<String> rootTag) {
			m.rootTag = rootTag;
			return this;
		}
		
		public StandardXmlInfoBuilder addRootTag(String rootTag) {
			if (m.rootTag == null) {
				m.rootTag = new ArrayList<>();
			}
			m.rootTag.add(rootTag);
			return this;
		}
		
		public StandardXmlInfoBuilder from(StandardXmlInfo info) {
			m = info;
			return this;
		}
		
		public StandardXmlInfo build() {
			return m;
		}
	}
	
	public static class StandardXmlInfos {

		@JsonProperty("standardXmlInfos")
		private List<StandardXmlInfo> lists;

		private StandardXmlInfos() {
		}
		
		public static StandardXmlInfosBuilder builder() {
			return new StandardXmlInfosBuilder();
		}
		
		public List<StandardXmlInfo> getLists() {
			return lists;
		}
		
		public static class StandardXmlInfosBuilder {

			private StandardXmlInfos m = new StandardXmlInfos();

			private StandardXmlInfosBuilder() {
			}

			public StandardXmlInfosBuilder lists(List<StandardXmlInfo> lists) {
				m.lists = lists;
				return this;
			}
			
			public StandardXmlInfos build() {
				return m;
			}
		}
	}
}
