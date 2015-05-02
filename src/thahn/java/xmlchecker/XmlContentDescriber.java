package thahn.java.xmlchecker;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescriber;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ide.IDE;

import thahn.java.xmlchecker.editors.EditorForm;
import thahn.java.xmlchecker.util.MyUtils;

public class XmlContentDescriber implements IContentDescriber {//extends XMLContentDescriber {

	private static final QualifiedName[] SUPPORTED_OPTIONS = new QualifiedName[] {};//IContentDescription.CHARSET, IContentDescription.BYTE_ORDER_MARK};
	
	/*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.content.IContentDescriber#describe(java.io.
     * InputStream, org.eclipse.core.runtime.content.IContentDescription)
     */
    @Override
    public int describe(InputStream contents, IContentDescription description) throws IOException {
    	int ret = INVALID;
    	
        if (MyUtils.getXmlCheckerProject(contents) != null) {
        	ret = VALID;
//        } else if (description == null) {
//        	ret = INDETERMINATE;
        }
        return ret;
    }
    
	@Override
	public QualifiedName[] getSupportedOptions() {
		return SUPPORTED_OPTIONS;
	}
}
