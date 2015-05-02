package thahn.java.xmlchecker.wizards;

import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import thahn.java.xmlchecker.util.MyStrings;

/**
 * 
 * @author th0720.ahn
 *
 */
public class NewProjectWizard extends Wizard implements INewWizard {

	private NewProjectPage mMainPage;
	private IProject mProject;	
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		mMainPage = new NewProjectPage();
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
		boolean ret = !MyStrings.isNullorEmpty(mMainPage.getProjectName()); 
		if (ret) {
			try {
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				String name = mMainPage.getProjectName();
				mProject = root.getProject(name);

				NewProjectCreator creator = new NewProjectCreator(this, name, mProject.getFullPath().toFile().getAbsolutePath());
				creator.createCmsDescriptorProjects();

				try {
					mProject.refreshLocal(DEPTH_INFINITE, new NullProgressMonitor());
				} catch (CoreException e) {
					e.printStackTrace();
				}

				ret = true;
			} catch (Exception ioe) {
				ioe.printStackTrace();
				ret = false;
			}
		}
		return ret;
	}
}
