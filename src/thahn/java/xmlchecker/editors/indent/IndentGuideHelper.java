package thahn.java.xmlchecker.editors.indent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProviderExtension4;
import org.eclipse.ui.texteditor.ITextEditor;

import thahn.java.xmlchecker.editors.EditorForm;

/**
 * 
 * @author th0720.ahn
 *
 */
public class IndentGuideHelper {

	private IPainter 										painter;
	private boolean 										isRegisterd = false;
	private	static IndentGuideHelper						instance;
	
	private IndentGuideHelper() {
	}

	public static IndentGuideHelper getInstance() {
		if (instance == null) {
			instance = new IndentGuideHelper();
		}
		return instance;
	}
	
	public synchronized void registerIfNot() {
		if (!isRegisterd) {
			isRegisterd = true;
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					IWorkbench workbench = PlatformUI.getWorkbench();
					IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
					if (window != null) {
						IWorkbenchPage page = window.getActivePage();
						if (page != null) {
							IEditorPart part = page.getActiveEditor();
							if (part != null) {
								addListener(part);
							}
						}
						window.getPartService().addPartListener(new PartListener());
					}
					workbench.addWindowListener(new WindowListener());
				}
			});
		}
	}
	
	private class PartListener implements IPartListener2 {

		public void partActivated(IWorkbenchPartReference partRef) {
		}

		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}

		public void partClosed(IWorkbenchPartReference partRef) {
		}

		public void partDeactivated(IWorkbenchPartReference partRef) {
		}

		public void partOpened(IWorkbenchPartReference partRef) {
			IWorkbenchPart part = partRef.getPart(false);
			if (part instanceof IEditorPart) {
				addListener((IEditorPart) part);
			}
		}

		public void partHidden(IWorkbenchPartReference partRef) {
		}

		public void partVisible(IWorkbenchPartReference partRef) {
		}

		public void partInputChanged(IWorkbenchPartReference partRef) {
		}
	}

	private class WindowListener implements IWindowListener {

		public void windowActivated(IWorkbenchWindow window) {
		}

		public void windowDeactivated(IWorkbenchWindow window) {
		}

		public void windowClosed(IWorkbenchWindow window) {
		}

		public void windowOpened(IWorkbenchWindow window) {
			if (window != null) {
				IWorkbenchPage page = window.getActivePage();
				if (page != null) {
					IEditorPart part = page.getActiveEditor();
					if (part != null) {
						addListener(part);
					}
				}
				window.getPartService().addPartListener(new PartListener());
			}
		}
	}

	private void addListener(IEditorPart part) {
		if (part instanceof EditorForm) {
			IContentType contentType = null;
			ITextEditor textEditor = (ITextEditor) ((EditorForm) part).getStructuredTextEditor();
			IDocumentProvider provider = textEditor.getDocumentProvider();
			if (provider instanceof IDocumentProviderExtension4) {
				try {
					contentType = ((IDocumentProviderExtension4) provider)
							.getContentType(textEditor.getEditorInput());
				} catch (CoreException e) {
				}
			}
			if (contentType == null) {
				return;
			}
//			String id = contentType.getId();
//			String type = IContentTypeManager.CT_TEXT;
//			String[] types = type.split("\\|");
//			List<String> contentTypes = new LinkedList<String>();
//			for (int i = 0; i < types.length; i++) {
//				contentTypes.add(types[i]);
//			}
//			if (!contentTypes.contains(id)) {
//				return;
//			}
			Class<?> editor = textEditor.getClass();
			while (!editor.equals(AbstractTextEditor.class)) {
				editor = editor.getSuperclass();
			}
			try {
				Method method = editor.getDeclaredMethod("getSourceViewer", //$NON-NLS-1$
						(Class[]) null);
				method.setAccessible(true);
				Object viewer = method.invoke(textEditor, (Object[]) null);
				if (viewer instanceof ITextViewerExtension2) {
					painter = new IndentGuidePainter((ITextViewer) viewer);
					((ITextViewerExtension2) viewer).addPainter(painter);
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}
