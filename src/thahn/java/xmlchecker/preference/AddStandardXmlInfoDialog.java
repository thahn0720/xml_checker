package thahn.java.xmlchecker.preference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import thahn.java.xmlchecker.XmlCheckerPlugin;
import thahn.java.xmlchecker.util.MyUtils;
import thahn.java.xmlchecker.util.MyStrings;
import thahn.java.xmlchecker.view.DialogListener;
import thahn.java.xmlchecker.view.MyDialog;

/**
 * 
 * @author th0720.ahn
 *
 */
public class AddStandardXmlInfoDialog extends MyDialog {
	
	public static final String 								ROOT_TAG_PATTERN		= "[a-zA-Z0-9_+,-]*";
	public static final String 								FILE_NAME_PATTERN		= "[a-zA-Z0-9_+-]*.[a-zA-Z0-9_-]*";
	
	private Text 											standardXmlPathText;
	private Text 											rootTagText;
	private Text 											fileNameText;
	private Label 											fileNameValid;
	private DialogListener									dialogListener;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AddStandardXmlInfoDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setToolTipText("");
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(3, false));
		
		Label lblStandardXmlPath = new Label(container, SWT.NONE);
		lblStandardXmlPath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStandardXmlPath.setText("Standard Xml Path");
		
		standardXmlPathText = new Text(container, SWT.BORDER);
		standardXmlPathText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button stdXmlPathBroweBtn = new Button(container, SWT.NONE);
		stdXmlPathBroweBtn.setText("Browse");
		stdXmlPathBroweBtn.addSelectionListener(browseSelectionListener);
		
		Label lblRootTag = new Label(container, SWT.NONE);
		lblRootTag.setAlignment(SWT.CENTER);
		lblRootTag.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblRootTag.setText("Root Tag");
		
		rootTagText = new Text(container, SWT.BORDER);
		rootTagText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lblFileNameregularExpression = new Label(container, SWT.NONE);
		lblFileNameregularExpression.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFileNameregularExpression.setText("       File Name\n(Regular Expression)");
		
		fileNameText = new Text(container, SWT.BORDER);
		fileNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		fileNameValid = new Label(container, SWT.NONE);
		fileNameValid.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		fileNameValid.setText("Valid");
		
		setDefaultValue(null);
		
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		parent.setTouchEnabled(true);
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(450, 180);
	}
	
	public void setDefaultValue(StandardXmlInfo def) {
		String stdXmlPath = "";
		String rootTag = "";
		String fileName = "[a-zA-Z0-9\\._+-]+";
		if (def != null) {
			stdXmlPath = def.getStandardXmlPath();
			rootTag = StandardXmlInfo.joinRootTagString(def.getRootTag());
			fileName = def.getCondition().getFileName();
		}
		standardXmlPathText.setText(stdXmlPath);
		rootTagText.setText(rootTag);
		fileNameText.setText(fileName);
	}
	
    private boolean checkStandardXmlPath(String path) {
    	boolean ret = false;
        String fileName = path;
        fileName = fileName.trim();
        if (!MyStrings.isNullorEmpty(fileName)) {
        	ret = true;
        } else {
        	XmlCheckerPlugin.displayError("Wrong Path", "wrong file path");
        }
        return ret;
    }
    
	protected boolean checkRootTag(String rootTag) {
		boolean ret = false;
		String text = rootTag;
		if (text.matches(ROOT_TAG_PATTERN)) {
			ret = true;
		} else {
			XmlCheckerPlugin.displayError("Pattern Validation", "Pattern is not corret");
		}
		return ret;
	}
	
	/**
	 * 
	 * @param rootTag is format of regular expression
	 * @return
	 */
	protected boolean checkFileName(String fileName) {
		boolean ret = false;
		String text = fileName;
		try {
			Pattern pattern = Pattern.compile(fileName);
			setFileNameValid(true);
			ret = true;
		} catch (Exception e) {
			setFileNameValid(false);
			XmlCheckerPlugin.displayError("Pattern Validation", "valid regular expression is required : " + e.getMessage());
			// e.printStackTrace();
		}
		return ret;
	}
	
	private void setFileNameValid(boolean valid) {
		if (valid) {
			fileNameValid.setText("Valid");
			fileNameValid.setForeground(XmlCheckerPlugin.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		} else {
			fileNameValid.setText("Invalid");
			fileNameValid.setForeground(XmlCheckerPlugin.getDisplay().getSystemColor(SWT.COLOR_RED));
		}
	}

	public String getRootTagText() {
		return rootTagText.getText();
	}
	
	public String getStdXmlPathText() {
		return standardXmlPathText.getText();
	}
	
	public String getFileNameText() {
		return fileNameText.getText();
	}
	
	public DialogListener getDialogListener() {
		return dialogListener;
	}

	public void setDialogListener(DialogListener dialogListener) {
		this.dialogListener = dialogListener;
	}

	private SelectionListener browseSelectionListener = new SelectionListener() {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
	        if (MyStrings.isNullorEmpty(standardXmlPathText.getText())) {
				dialog.setFileName(standardXmlPathText.getText());
			} 
	        String file = dialog.open();
	        if (file != null) {
	        	standardXmlPathText.setText(file);
	        	try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(file)))) {
					String rootTag = MyUtils.getXmlRootTag(bis);
					if (rootTag != null) {
						rootTagText.setText(rootTag);
					} else {
						XmlCheckerPlugin.displayError("Error", "Xml format is wrong");
					}
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
	        }
		}
		
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	@Override
	protected boolean checkOk() {
		if (checkStandardXmlPath(standardXmlPathText.getText()) && checkRootTag(rootTagText.getText())
				&& checkFileName(fileNameText.getText())
				) {
			dialogListener.ok(this);
			return true;
		}
		return false;
	}

	@Override
	protected void ok() {
		close();
	}

	@Override
	protected boolean cancel() {
		return true;
	}
}
