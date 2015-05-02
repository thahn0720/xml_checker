package thahn.java.xmlchecker.parser.standard;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import thahn.java.xmlchecker.preference.XmlCheckerPrefs;
import thahn.java.xmlchecker.preference.StandardXmlInfo;

/**
 * 
 * @author th0720.ahn
 *
 */
public class DescriptorContainer {

	/**
	 * key is path
	 */
	private Map<String, DescriptorStandard> mContainer = new HashMap<>(); 
	
	private static DescriptorContainer mInstance;

	public static DescriptorContainer getInstance() {
		if (mInstance == null) {
			mInstance = new DescriptorContainer();
		}
		return mInstance;
	}

	private DescriptorContainer() {
		refresh();
	}
	
	public void refresh() {
		List<StandardXmlInfo> stdXmlInfos = XmlCheckerPrefs.getPrefs().getStdXmlInfos();
		for (StandardXmlInfo standardXmlInfo : stdXmlInfos) {
			String stdXmlPath = standardXmlInfo.getStandardXmlPath();
			DescriptorStandard desStd = get(stdXmlPath);
			if (desStd != null) {
				desStd.validate();
			} else {
				DescriptorStandard std = new DescriptorStandard(stdXmlPath);
				std.validate();
				put(stdXmlPath, makeDescriptor(stdXmlPath));
			}
		}
	}
	
	public static DescriptorStandard descriptor(String path) {
		return getInstance().get(path);
	}

	public DescriptorStandard get(String key) {
		DescriptorStandard std = mContainer.get(key);
		if (std == null) {
			for (StandardXmlInfo stdXmlInfo : XmlCheckerPrefs.getPrefs().getStdXmlInfos()) {
				String stdXmlPath = stdXmlInfo.getStandardXmlPath();
				if (stdXmlPath.equals(key)) {
					put(stdXmlPath, makeDescriptor(stdXmlPath));
				}
			}
		}
		return std;
	}
	
	private DescriptorStandard makeDescriptor(String path) {
		DescriptorStandard std = new DescriptorStandard(path);
		std.validate();
		return std;
	}

	public DescriptorStandard put(String key, DescriptorStandard value) {
		return mContainer.put(key, value);
	}

	public boolean containsKey(DescriptorStandard key) {
		return mContainer.containsKey(key);
	}

	public boolean containsValue(String value) {
		return mContainer.containsValue(value);
	}

	public Set<String> keySet() {
		return mContainer.keySet();
	}

	public Collection<DescriptorStandard> values() {
		return mContainer.values();
	}
}
