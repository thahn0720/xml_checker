package thahn.java.xmlchecker.preference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import thahn.java.xmlchecker.XmlCheckerPlugin;
import thahn.java.xmlchecker.parser.standard.DescriptorContainer;
import thahn.java.xmlchecker.preference.StandardXmlInfo.StandardXmlInfos;
import thahn.java.xmlchecker.preference.StandardXmlInfo.StandardXmlInfos.StandardXmlInfosBuilder;
import thahn.java.xmlchecker.util.JsonMapper;
import thahn.java.xmlchecker.util.MyStrings;

/**
 *
 * @author th0720.ahn
 *
 */
public final class XmlCheckerPrefs extends AbstractPreferenceInitializer {
	
    public final static String 				PREFS_STANDARD_DESCRIPTOR_STD_TABLE 			= XmlCheckerPlugin.PLUGIN_ID + ".stdtable"; 
    public final static String 				PREFS_STANDARD_DESCRIPTOR_STD_TABLE_DEFAULT		= "{}"; 
    
    public final static String 				PREFS_STANDARD_DESCRIPTOR_ROOT_TAG_DEFAULT 		= "vnfd"; 

    /** singleton instance */
    private final static XmlCheckerPrefs 			sThis = new XmlCheckerPrefs();

    /** default store, provided by eclipse */
    private IPreferenceStore 				mStore;

    private List<StandardXmlInfo> 			mStdXmlInfoList;

    public static void init(IPreferenceStore preferenceStore) {
        sThis.mStore = preferenceStore;
        // sThis.cleanAllPrroperty();
    }
    
    public static XmlCheckerPrefs getPrefs() {
        return sThis;
    }
    
    @Override
	public void initializeDefaultPreferences() {
    	IPreferenceStore store = XmlCheckerPlugin.getDefault().getPreferenceStore();
        initializeStoreWithDefaults(store);
	}
    
    public void initializeStoreWithDefaults(IPreferenceStore store) {
    	getStdXmlInfos();
    }

    /**
     * when property is changed, called
     * @param event
     */
    public synchronized void loadValues(PropertyChangeEvent event) {
        // get the name of the property that changed, if any
    	if (event == null) {
    		return;
    	}
    	
    	if (event.getProperty().equals(XmlCheckerPrefs.PREFS_STANDARD_DESCRIPTOR_STD_TABLE)) {
    		DescriptorContainer.getInstance().refresh();
    	}
    }

    public synchronized void setPrefValue(String key, String value) {
    	mStore.setValue(key, value);
    }
    
    public synchronized String getStringPref(String key) {
    	return mStore.getString(key);
    }
    
    public List<StandardXmlInfo> getStdXmlInfos() {
    	if (mStdXmlInfoList == null) {
    		try {
				setStdXmlInfos(getStdXmlInfosObject(mStore.getString(PREFS_STANDARD_DESCRIPTOR_STD_TABLE)));
				if (mStdXmlInfoList == null) {
		    		mStdXmlInfoList = new ArrayList<>();
		    	}
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	return mStdXmlInfoList;
    }
    
    public List<StandardXmlInfo> getStdXmlInfosObject(String json) throws JsonParseException, JsonMappingException, IOException {
    	String value = json;
		if (MyStrings.isNullorEmpty(value)) {
			value = PREFS_STANDARD_DESCRIPTOR_STD_TABLE_DEFAULT;
		}
		return JsonMapper.readJsonObject(value, StandardXmlInfos.class).getLists();
    }
    
    public void setStdXmlInfos(List<StandardXmlInfo> lists) {
    	String value = getStdXmlInfosJson(lists);
    	mStore.setValue(PREFS_STANDARD_DESCRIPTOR_STD_TABLE, value);
    }
    
    public String getStdXmlInfosJson(List<StandardXmlInfo> lists) {
    	String value = PREFS_STANDARD_DESCRIPTOR_STD_TABLE_DEFAULT;
    	if (lists != null) {
    		mStdXmlInfoList = lists;
    		value = JsonMapper.makeJson(StandardXmlInfos.builder().lists(lists).build());
    	} else {
    		mStdXmlInfoList = new ArrayList<>();
    	}
    	return value;
    }
    
    public void cleanAllPrroperty() {
    	mStore.setValue(PREFS_STANDARD_DESCRIPTOR_STD_TABLE, "");
    }
}
