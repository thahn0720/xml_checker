package thahn.java.xmlchecker.view;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author th0720.ahn
 *
 */
public class MyPopup {

	public interface OnPopupListener {
		public void onOpen();
		public void onClose();
	}
	
	public static void show(Display activeDisplay, final String title, final String message, final String bottom, int x, int y
			, OnPopupListener listener) {
		final Point location = new Point(x, y);
		final Display display = activeDisplay;
		final OnPopupListener onPopupListener = listener;
        display.syncExec(new Runnable() {
        	
            @Override
            public void run() {
            	Shell shell = display.getActiveShell();
            	int shellStyle = PopupDialog.HOVER_SHELLSTYLE;//PopupDialog.INFOPOPUPRESIZE_SHELLSTYLE;
				boolean takeFocusOnOpen = true;
				boolean persistSize = true;
				boolean persistLocation = true;
				boolean showDialogMenu = true;
				boolean showPersistActions = true;
				PopupDialog dialog = new PopupDialog(shell, shellStyle, takeFocusOnOpen, persistSize, persistLocation, showDialogMenu, showPersistActions
						, title, bottom) {
					
					@Override
					protected Control createDialogArea(Composite parent) {
						Composite composite = (Composite) super.createDialogArea(parent);
						Text text = new Text(composite,SWT.SINGLE | SWT.BORDER);
						text.setEditable(false);
						text.setText(message);
						return composite;
					}

					@Override
					protected Point getDefaultLocation(Point initialSize) {
						return location;
					}

					@Override
					public int open() {
						if (onPopupListener != null) {
							onPopupListener.onOpen();
						}
						return super.open();
					}

					@Override
					public boolean close() {
						if (onPopupListener != null) {
							onPopupListener.onClose();
						}
						return super.close();
					}
				};
				dialog.open();
            }
        });
	}
}
