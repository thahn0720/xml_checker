package thahn.java.xmlchecker;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import thahn.java.xmlchecker.preference.XmlCheckerPrefs;
import thahn.java.xmlchecker.util.PdeUtils;

/**
 * 
 * @author th0720.ahn
 *
 */
public class XmlCheckerPlugin extends AbstractUIPlugin {

	public static final String 									PLUGIN_ID 		= "thahn.java.descriptorchecker"; //$NON-NLS-1$

	private static XmlCheckerPlugin 									sPlugin;
	private static Image 										sCmsLogo;
	private IContextActivation 									mContextActivateion;
	
	/**
	 * The constructor
	 */
	public XmlCheckerPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		sPlugin = this;
		
        // get the eclipse store
        IPreferenceStore eclipseStore = getPreferenceStore();
        XmlCheckerPrefs.init(eclipseStore);

        // set the listener for the preference change
        eclipseStore.addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                // load the new preferences
                XmlCheckerPrefs.getPrefs().loadValues(event);
            }
        });
        // load preferences.
        XmlCheckerPrefs.getPrefs().loadValues(null /*event*/);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		sPlugin = null;
		super.stop(context);
	}
	
	public void workbenchStarted() {
//		checkStandardDesLoc();
	}

//	private void checkStandardDesLoc() {
//		String desLocation = CmsPrefs.getPrefs().getStandardDesLoc();
//		if (MyStrings.isNullorEmpty(desLocation)) {
//			PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(
//                    getDisplay().getActiveShell(),
//                    CmsPreferencePage.PREFERENCE_MAIN_ID, //$NON-NLS-1$ preferencePageId
//                    null,  // displayedIds
//                    null); // data
//            dialog.open();
//		}
//	}
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static XmlCheckerPlugin getDefault() {
		return sPlugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public static Image getImage(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path).createImage();
	}
	
    /**
     * Displays an error dialog box. This dialog box is ran asynchronously in the ui thread,
     * therefore this method can be called from any thread.
     * @param title The title of the dialog box
     * @param message The error message
     */
    public final static void displayError(final String title, final String message) {
        // get the current Display
        final Display display = getDisplay();

        // dialog box only run in ui thread..
        display.asyncExec(new Runnable() {
            @Override
            public void run() {
                Shell shell = display.getActiveShell();
                MessageDialog.openError(shell, title, message);
            }
        });
    }

    /**
     * Displays a warning dialog box. This dialog box is ran asynchronously in the ui thread,
     * therefore this method can be called from any thread.
     * @param title The title of the dialog box
     * @param message The warning message
     */
    public final static void displayWarning(final String title, final String message) {
        // get the current Display
        final Display display = getDisplay();

        // dialog box only run in ui thread..
        display.asyncExec(new Runnable() {
            @Override
            public void run() {
                Shell shell = display.getActiveShell();
                MessageDialog.openWarning(shell, title, message);
            }
        });
    }

    /**
     * Display a yes/no question dialog box. This dialog is opened synchronously in the ui thread,
     * therefore this message can be called from any thread.
     * @param title The title of the dialog box
     * @param message The error message
     * @return true if OK was clicked.
     */
    public final static boolean displayPrompt(final String title, final String message) {
        // get the current Display and Shell
        final Display display = getDisplay();

        // we need to ask the user what he wants to do.
        final boolean[] result = new boolean[1];
        display.syncExec(new Runnable() {
            @Override
            public void run() {
                Shell shell = display.getActiveShell();
                result[0] = MessageDialog.openQuestion(shell, title, message);
            }
        });
        return result[0];
    }

    /**
     * Returns the current display, if any
     *
     * @return the display
     */
    public static Display getDisplay() {
        synchronized (XmlCheckerPlugin.class) {
            if (sPlugin != null) {
                IWorkbench bench = sPlugin.getWorkbench();
                if (bench != null) {
                    Display display = bench.getDisplay();
                    if (display != null) {
                        return display;
                    }
                }
            }
        }

        Display display = Display.getCurrent();
        if (display != null) {
            return display;
        }

        return Display.getDefault();
    }
    
    public static Image getCmsLogo() {
    	if (sCmsLogo == null) {
    		sCmsLogo = imageDescriptorFromPlugin(PLUGIN_ID, MyConstants.LOGO_PATH).createImage();
    	}
		return sCmsLogo;
	}
    
    public static boolean isInCmsEditor() {
    	if (MyConstants.CMS_XML_EXTENSTION.equalsIgnoreCase(PdeUtils.getActiveFile().getFileExtension())) {
			return true;
		}
    	return false;
    }
    
	public void switchContext(boolean onFocus) {
		if (onFocus) {
			XmlCheckerPlugin.getDisplay().syncExec(new Runnable() {

				public void run() {
					IContextService contextService = ((IContextService) PlatformUI.getWorkbench()
							.getService(IContextService.class));
					mContextActivateion = contextService.activateContext(MyConstants.CMS_XML_CONTEXT);
				}
			});
		} else {
			XmlCheckerPlugin.getDisplay().syncExec(new Runnable() {

				public synchronized void run() {
					if (mContextActivateion != null) {
						IWorkbench workbench = PlatformUI.getWorkbench();
						if (workbench != null) {
							mContextActivateion.getContextService().deactivateContext(mContextActivateion);
						}
						mContextActivateion = null;				
					}
				}
			});
		}
	}
	
    /**
     * Logs a message to the default Eclipse log.
     *
     * @param severity The severity code. Valid values are: {@link IStatus#OK},
     * {@link IStatus#ERROR}, {@link IStatus#INFO}, {@link IStatus#WARNING} or
     * {@link IStatus#CANCEL}.
     * @param format The format string, like for {@link String#format(String, Object...)}.
     * @param args The arguments for the format string, like for
     * {@link String#format(String, Object...)}.
     */
    public static void log(int severity, String format, Object ... args) {
        if (format == null) {
            return;
        }

        String message = String.format(format, args);
        Status status = new Status(severity, PLUGIN_ID, message);

        if (getDefault() != null) {
            getDefault().getLog().log(status);
        } else {
            // During UnitTests, we generally don't have a plugin object. It's ok
            // to log to stdout or stderr in this case.
            (severity < IStatus.ERROR ? System.out : System.err).println(status.toString());
        }
    }

    /**
     * Logs an exception to the default Eclipse log.
     * <p/>
     * The status severity is always set to ERROR.
     *
     * @param exception the exception to log.
     * @param format The format string, like for {@link String#format(String, Object...)}.
     * @param args The arguments for the format string, like for
     * {@link String#format(String, Object...)}.
     */
    public static void log(Throwable exception, String format, Object ... args) {
        String message = null;
        if (format != null) {
            message = String.format(format, args);
        } else {
            message = "";
        }
        Status status = new Status(IStatus.ERROR, PLUGIN_ID, message, exception);

        if (getDefault() != null) {
            getDefault().getLog().log(status);
        } else {
            // During UnitTests, we generally don't have a plugin object. It's ok
            // to log to stderr in this case.
            System.err.println(status.toString());
        }
    }

    /**
     * Prints one or more error message to the android console.
     * @param objects the objects to print through their <code>toString</code> method.
     */
    public static void printErrorToConsole(Object... objects) {
        printErrorToConsole((String)null, objects);
    }

    /**
     * Prints one or more error message to the android console.
     * @param project The project to which the message is associated. Can be null.
     * @param objects the objects to print through their <code>toString</code> method.
     */
    public static void printErrorToConsole(IProject project, Object... objects) {
        String tag = project != null ? project.getName() : null;
        printErrorToConsole(tag, objects);
    }
}
