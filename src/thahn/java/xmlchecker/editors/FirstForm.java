package thahn.java.xmlchecker.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class FirstForm extends FormPage {

	/**
	 * Create the form page.
	 * 
	 * @param id
	 * @param title
	 */
	public FirstForm(final String id, final String title) {
		super(id, title);
	}

	/**
	 * Create the form page.
	 * 
	 * @param editor
	 * @param id
	 * @param title
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter id "Some id"
	 * @wbp.eval.method.parameter title "Some title"
	 */
	public FirstForm(final FormEditor editor, final String id, final String title) {
		super(editor, id, title);
	}

	/**
	 * Create contents of the form.
	 * 
	 * @param managedForm
	 */
	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		final FormToolkit toolkit = managedForm.getToolkit();
		final ScrolledForm form = managedForm.getForm();
		form.setText("First Editor Page");
		final Composite body = form.getBody();
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);
	}
}
