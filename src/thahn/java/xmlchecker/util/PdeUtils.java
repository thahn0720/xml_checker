package thahn.java.xmlchecker.util;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author th0720.ahn
 *
 */
public class PdeUtils {
	
    /**
     * Returns the file for the current editor, if any.
     *
     * @return the file for the current editor, or null if none
     */
    public static IFile getActiveFile() {
        IEditorPart editor = getActiveEditor();
        if (editor != null) {
            IEditorInput input = editor.getEditorInput();
            if (input instanceof IFileEditorInput) {
                IFileEditorInput fileInput = (IFileEditorInput) input;
                return fileInput.getFile();
            }
        }

        return null;
    }
    
    public static IContainer getActiveFolder() {
        IEditorPart editor = getActiveEditor();
        if (editor != null) {
            IEditorInput input = editor.getEditorInput();
            if (input instanceof IFileEditorInput) {
                IFileEditorInput fileInput = (IFileEditorInput) input;
                return fileInput.getFile().getParent();
            }
        }

        return null;
    }
	
    /**
     * Returns the current editor (the currently visible and active editor), or null if
     * not found
     *
     * @return the current editor, or null
     */
    public static IEditorPart getActiveEditor() {
        IWorkbenchWindow window = getActiveWorkbenchWindow();
        if (window != null) {
            IWorkbenchPage page = window.getActivePage();
            if (page != null) {
                return page.getActiveEditor();
            }
        }

        return null;
    }
    
    /**
     * Returns the current active workbench, or null if not found
     *
     * @return the current window, or null
     */
    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        if (window == null) {
            IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
            if (windows.length > 0) {
                window = windows[0];
            }
        }

        return window;
    }
    
    public static IPath getCurrentPath() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof JavaElement) {
				IPath path = ((JavaElement) firstElement).getPath();
				return path;
			} else if (firstElement instanceof IFile) {
				IFile file = ((IFile) firstElement);
				return file.getFullPath();
			}
		}
		
        return null;
    }
    
    public static IProject getProject(String projectName) {
    	return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
    }
}
