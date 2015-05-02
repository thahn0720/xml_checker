package thahn.java.xmlchecker.welcome;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

import thahn.java.xmlchecker.XmlCheckerPlugin;

/**
 *
 * @author th0720.ahn
 *
 */
public class XmlCheckerStartup implements IStartup, IWindowListener {
	
    @Override
    public void earlyStartup() {
        XmlCheckerPlugin.getDefault().workbenchStarted();
    }

    private static Version getVersion(Plugin plugin) {
        @SuppressWarnings("cast") // Cast required in Eclipse 3.5; prevent auto-removal in 3.7
        String version = (String) plugin.getBundle().getHeaders().get(Constants.BUNDLE_VERSION);
        // Parse the string using the Version class.
        return new Version(version);
    }

	@Override
	public void windowActivated(IWorkbenchWindow arg0) {
	}

	@Override
	public void windowClosed(IWorkbenchWindow arg0) {
	}

	@Override
	public void windowDeactivated(IWorkbenchWindow arg0) {
	}

	@Override
	public void windowOpened(IWorkbenchWindow arg0) {
	}
}
