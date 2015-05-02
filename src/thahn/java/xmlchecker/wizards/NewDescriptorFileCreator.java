package thahn.java.xmlchecker.wizards;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.ide.IDE;

import thahn.java.xmlchecker.MyConstants;
import thahn.java.xmlchecker.editors.EditorForm;
import thahn.java.xmlchecker.util.PdeUtils;

public class NewDescriptorFileCreator {

	public NewDescriptorFileCreator() {
	}

	public boolean createNewDescriptorFile(IPath path) {
		boolean ret = false;
		File file = path.toFile();
		if (!file.exists()) {
			ret = true;
			try {
				file.createNewFile();
				BufferedInputStream templateBis = new BufferedInputStream(NewDescriptorFileCreator.class.getResourceAsStream(MyConstants.DESCRIPTOR_TEMPLATE_PATH));
				BufferedOutputStream desBos = new BufferedOutputStream(new FileOutputStream(file));
				
				int read = 0;
				byte[] data = new byte[1024];
				while ((read = templateBis.read(data)) != -1) {
					desBos.write(data, 0, read);
				}
				
				templateBis.close();
				desBos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
    			IDE.setDefaultEditor(ResourcesPlugin.getWorkspace().getRoot().getFile(path)
    					, EditorForm.FORM_ID);
				PdeUtils.getProject(path.segment(0)).refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
		return ret;
	}
}
