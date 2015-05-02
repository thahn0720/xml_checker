package thahn.java.xmlchecker.wizards;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author th0720.ahn
 *
 */
public class NewDescriptorFilePage extends WizardPage implements ModifyListener, SelectionListener, FocusListener {
	
	private static final int WIZARD_PAGE_WIDTH = 600;
	private static final int FIELD_WIDTH = 300;

	private Text mFileNameText;
	private Label mHelpIcon;
	private Label mTipLabel;

	private ControlDecoration mApplicationDec;

	NewDescriptorFilePage() {
		super("newCmsDescriptor"); 
		setTitle("New CMS Descriptor File");
		setDescription("Creates a new CMS Descriptor File");
	}

	@SuppressWarnings("unused")
	// SWT constructors have side effects and aren't unused
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		GridLayout gl_container = new GridLayout(4, false);
		gl_container.horizontalSpacing = 10;
		container.setLayout(gl_container);

		Label projectLabel = new Label(container, SWT.NONE);
		projectLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
		projectLabel.setText("File Name:");

		mFileNameText = new Text(container, SWT.BORDER);
		GridData gdProjectText = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gdProjectText.widthHint = FIELD_WIDTH;
		mFileNameText.setLayoutData(gdProjectText);
		mFileNameText.addModifyListener(this);
		mFileNameText.addFocusListener(this);
		mApplicationDec = createFieldDecoration(mFileNameText,
				"The file name is shown in project explorer.");

		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		mHelpIcon = new Label(container, SWT.NONE);
		mHelpIcon.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false,
				1, 1));
//		Image icon = IconFactory.getInstance().getIcon("quickfix");
//		mHelpIcon.setImage(icon);
		mHelpIcon.setVisible(false);

		mTipLabel = new Label(container, SWT.WRAP);
		mTipLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

		// Reserve space for 4 lines
		mTipLabel.setText("\n\n\n\n"); //$NON-NLS-1$

		// Reserve enough width to accommodate the various wizard pages up front
		// (since they are created lazily, and we don't want the wizard to
		// dynamically
		// resize itself for small size adjustments as each successive page is
		// slightly
		// larger)
		Label dummy = new Label(container, SWT.NONE);
		GridData data = new GridData();
		data.horizontalSpan = 4;
		data.widthHint = WIZARD_PAGE_WIDTH;
		dummy.setLayoutData(data);
	}

	private ControlDecoration createFieldDecoration(Control control,
			String description) {
		ControlDecoration dec = new ControlDecoration(control, SWT.LEFT);
		dec.setMarginWidth(2);
		FieldDecoration errorFieldIndicator = FieldDecorationRegistry
				.getDefault().getFieldDecoration(
						FieldDecorationRegistry.DEC_INFORMATION);
		dec.setImage(errorFieldIndicator.getImage());
		dec.setDescriptionText(description);
		control.setToolTipText(description);

		return dec;
	}

	@Override
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
		String tip = "";
		if (source == mFileNameText) {
			tip = mApplicationDec.getDescriptionText();
		} 
		mTipLabel.setText(tip);
		mHelpIcon.setVisible(tip.length() > 0);
	}

	@Override
	public void focusLost(FocusEvent e) {
		mTipLabel.setText("");
		mHelpIcon.setVisible(false);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	@Override
	public void modifyText(ModifyEvent e) {
	}

	public String getFileName() {
		return mFileNameText.getText();
	}
}