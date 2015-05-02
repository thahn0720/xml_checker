package thahn.java.xmlchecker.wizards;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import thahn.java.xmlchecker.MyConstants;
import thahn.java.xmlchecker.util.MyStrings;
import thahn.java.xmlchecker.util.PdeUtils;

/**
 * 
 * @author th0720.ahn
 *
 */
public class NewDescriptorFileWizard extends Wizard implements INewWizard {

	private NewDescriptorFilePage mMainPage;
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		mMainPage = new NewDescriptorFilePage();
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(mMainPage);
	}

	@Override
	public boolean canFinish() {
		return super.canFinish();
	}
	
	@Override
	public boolean performFinish() {
		boolean ret = !MyStrings.isNullorEmpty(mMainPage.getFileName());
		IPath path = PdeUtils.getCurrentPath();
		if (ret && path.segmentCount() > 0) {
			try {
				String name = mMainPage.getFileName();
				if (!name.endsWith(MyConstants.CMS_XML_EXTENSTION)) {
					name = new StringBuilder(name).append(".").append(MyConstants.CMS_XML_EXTENSTION).toString();
				}
				if (path.toFile().isFile()) {
					path = path.removeLastSegments(1);
				} 
				path = path.append(name);
				NewDescriptorFileCreator descriptorCreator = new NewDescriptorFileCreator();
				ret = descriptorCreator.createNewDescriptorFile(path);
			} catch (Exception ioe) {
				ioe.printStackTrace();
				ret = false;
			}
		}
		return ret;
	}
}
