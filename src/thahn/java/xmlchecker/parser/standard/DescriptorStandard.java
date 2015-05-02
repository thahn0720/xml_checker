package thahn.java.xmlchecker.parser.standard;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import thahn.java.xmlchecker.XmlCheckerPlugin;
import thahn.java.xmlchecker.parser.Attribute;
import thahn.java.xmlchecker.parser.DescriptorTag;
import thahn.java.xmlchecker.parser.OnDescriptorParseListener;
import thahn.java.xmlchecker.parser.Pair;
import thahn.java.xmlchecker.preference.XmlCheckerPrefs;
import thahn.java.xmlchecker.util.MyStrings;

/**
 *
 * @author th0720.ahn
 *
 */
public class DescriptorStandard {
	//********************************************************************************************************************
	// attribute (tag, tag value, attr, attr value)
	// TODO : convert string to enum
	//********************************************************************************************************************
	public static final String											ATTR_MY_PREFIX				= "__";
	/** tag value */
	public static final String											ATTR_DEPENDENCY				= "__dependency";
	/** tag */
	public static final String											ATTR_ALL_AROUND				= "__all_around";
	/** tag, attr */
	public static final String											ATTR_DESCRIPTION			= "__description";
	/** tag, attr */
	public static final String											ATTR_REQUIRED				= "__required";
	/** root tag */
	public static final String											ATTR_IGNORED				= "__ignored";
	//********************************************************************************************************************
	
	public static final String											REG_EXP_VALUE_PREFIX		= "@";
	
	private String														mStdPath;
	private StandardParser 											mParser 					= new StandardParser();
	private List<Pair<String[], String[]>> 								mDependencyList;
	private List<Pair<String[], String>> 								mRegExpList;
	private Map<String, DescriptorTag> 									mAllAroundList;
	private long														mLastModified				= -1;
	
	public DescriptorStandard(String path) {
		mStdPath = path;
	}
	
	public void validate() {
		File file = new File(mStdPath);
		if (!file.exists()) {
			XmlCheckerPlugin.displayError("Not Exist : Standard Descriptor"
					, "The Standard Descriptor Does Not Exist : " + file.exists());
		} else {
			long lastModified = file.lastModified();
			if (mLastModified != lastModified) {
				mLastModified = file.lastModified();
				mParser.parse(mStdPath, mOnCmsDescriptorParsing);
			} 
		}
	}
	
	public StandardParser parser() {
		return mParser;
	}
	
	public List<Pair<String[], String[]>> getDependencyList() {
		return mDependencyList;
	}

	public List<Pair<String[], String>> getRegExpList() {
		return mRegExpList;
	}

	private OnDescriptorParseListener mOnCmsDescriptorParsing = new OnDescriptorParseListener() {
		
		@Override
		public void onStart() {
			mDependencyList = new ArrayList<>();
			mRegExpList = new ArrayList<>();
			mAllAroundList = new HashMap<>();
		}

		@Override
		public void onTag(DescriptorTag tag) {
			for (int i = 0; i < tag.getAttrs().size(); i++) {
				Attribute attr = tag.getAttrs().get(i);
				String attrName = attr.getQualifiedName();
				
				if (!attrName.contains(ATTR_MY_PREFIX)) {
					continue;
				}
				
				switch (attrName) {
				case ATTR_DEPENDENCY:
					String[] currentTagDepth = getCurrentDepth();
					String[] dependencyDepth = attr.getValue().trim().split("\\."); 
					mDependencyList.add(Pair.of(dependencyDepth, currentTagDepth));
					// TODO : dependency value content assist
					break;
				case ATTR_ALL_AROUND:
					mAllAroundList.put(tag.getTagName(), tag);
					// TODO : value content assist
					break;
				case ATTR_DESCRIPTION:
					break;
				case ATTR_REQUIRED:
					break;
				default:
					// candidate for tag's attr  
					// if (attrName.endsWith(ATTR_DESCRIPTION)) {
					// 	String prefix = attrName.substring(0, attrName.length() - ATTR_DESCRIPTION.length());
					// } else if (attrName.endsWith(ATTR_REQUIRED)) {
					// 	String prefix = attrName.substring(0, attrName.length() - ATTR_REQUIRED.length());
					// }
				}
			}
		}

		@Override
		public void onValue(DescriptorTag tag) {
			String value = tag.getTagValue();
			if (!MyStrings.isNullorEmpty(value) && value.startsWith(REG_EXP_VALUE_PREFIX)) {
				value = value.substring(REG_EXP_VALUE_PREFIX.length());
				String[] depth = getCurrentDepth();
				mRegExpList.add(Pair.of(depth, value));
			}
		}
	};
	
	private String[] getCurrentDepth() {
		return mParser.getTree().getCurrentDepth().toArray(new String[mParser.getTree().getCurrentDepth().size()]);
	}
	
	public DescriptorTag getAllAroundTag(String tagName) {
		return mAllAroundList.get(tagName);
	}
	
	public Set<String> getAllAroundTagName() {
		return mAllAroundList.keySet();
	}
	
	public boolean containsAllAroundTagName(String tagName) {
		return mAllAroundList.containsKey(tagName);
	}
}
