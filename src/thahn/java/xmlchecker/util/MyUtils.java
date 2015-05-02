package thahn.java.xmlchecker.util;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.internal.content.XMLRootHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import thahn.java.xmlchecker.parser.Attribute;
import thahn.java.xmlchecker.parser.standard.DescriptorContainer;
import thahn.java.xmlchecker.parser.standard.DescriptorStandard;
import thahn.java.xmlchecker.preference.XmlCheckerPrefs;
import thahn.java.xmlchecker.preference.StandardXmlInfo;

public class MyUtils {

	@SuppressWarnings("restriction")
	public static DescriptorStandard getXmlCheckerProject(InputStream contents) {
		DescriptorStandard ret = null;
		try (BufferedInputStream bio = new BufferedInputStream(contents)) {
			XMLRootHandler xmlHandler = new XMLRootHandler(true);
			if (xmlHandler.parseContents(new InputSource(bio))) {
				String root = xmlHandler.getRootName();
				// String dtd = xmlHandler.getDTD();
				// String namespace = xmlHandler.getRootNamespace();
				
				for (StandardXmlInfo stdXmlInfo : XmlCheckerPrefs.getPrefs().getStdXmlInfos()) {
					if (stdXmlInfo.getRootTag().contains(root)) {
						ret = DescriptorContainer.descriptor(stdXmlInfo.getStandardXmlPath());
						break;
					}
				} 
				
				if (ret == null) {
					for (DescriptorStandard std : DescriptorContainer.getInstance().values()) {
						Attribute rootIgnoredAttr = std.parser().getTree().getRoot().getAttr(DescriptorStandard.ATTR_IGNORED);
						if (rootIgnoredAttr != null && Boolean.parseBoolean(rootIgnoredAttr.getValue()) 
								&& std.getAllAroundTag(root) != null) {
							ret = std;
							break;
						}
					}
				}
			}
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	@SuppressWarnings("restriction")
	public static String getXmlRootTag(InputStream contents) {
		String ret = null;
		try (BufferedInputStream bio = new BufferedInputStream(contents)) {
			XMLRootHandler xmlHandler = new XMLRootHandler(true);
			if (xmlHandler.parseContents(new InputSource(bio))) {
				ret = xmlHandler.getRootName();
			}
        } catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			// e.printStackTrace();
		}
		
		return ret;
	}
}
