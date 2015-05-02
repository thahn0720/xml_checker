package thahn.java.xmlchecker.view;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;

public abstract class MyDialog extends Dialog {

	private DialogListener									dialogListener;
	
	public MyDialog(IShellProvider parentShell) {
		super(parentShell);
	}

	public MyDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected final void okPressed() {
		if (checkOk()) {
			if (dialogListener != null) dialogListener.ok(this);
			ok();
		}
	}
	
	@Override
	protected final void cancelPressed() {
		super.cancelPressed();
		if (dialogListener != null) dialogListener.cancel(this);
		cancel();
	}

	protected abstract boolean checkOk();
	protected abstract void ok();
	/**
	 * @return true : close, false : keep
	 */
	protected abstract boolean cancel();

	public DialogListener getDialogListener() {
		return dialogListener;
	}

	public void setDialogListener(DialogListener dialogListener) {
		this.dialogListener = dialogListener;
	}
}
