package thahn.java.xmlchecker.editors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;

import thahn.java.xmlchecker.MyConstants;
import thahn.java.xmlchecker.editors.indent.IndentGuideHelper;
import thahn.java.xmlchecker.parser.standard.DescriptorContainer;
import thahn.java.xmlchecker.parser.standard.DescriptorStandard;
import thahn.java.xmlchecker.preference.XmlCheckerPrefs;
import thahn.java.xmlchecker.preference.StandardXmlInfo;
import thahn.java.xmlchecker.util.MyUtils;

public class EditorForm extends FormEditor {
	// FIXME : prefs 수정 후 열린 editor는 닫은 후 다시 열어야 설정 적용됨
	public static final String 									CONTENT_TYPE_ID = "thahn.java.descriptorchecker.editor.contenttype.xml";
	public static final String 									FORM_ID = "thahn.java.descriptorchecker.editors.XMLEditor.form";
	
	private StructuredTextEditor 								mTextEditor;
	private int 												mSourceEditorIndex;

	/** Keeps track of dirty code from source editor. */
	private boolean 											mSourceDirty = false;

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
        if (input instanceof IFileEditorInput) {
            IFileEditorInput fileInput = (IFileEditorInput) input;
            IFile ifile = fileInput.getFile();
            File file = ifile.getFullPath().toFile();
            try (FileInputStream fio = new FileInputStream(file)) {
        		if (!FORM_ID.equals(IDE.getDefaultEditor(ifile).getId())) {
        			IDE.setDefaultEditor(ifile, FORM_ID);
        		}
        		
        		// considering root tag of this file, add std xml info by this file name in Container.
        		DescriptorStandard desStd = MyUtils.getXmlCheckerProject(fio);
        		if (desStd != null) {
        			DescriptorContainer.getInstance().put(file.getAbsolutePath(), desStd);
        		} else {
        			for (StandardXmlInfo std : XmlCheckerPrefs.getPrefs().getStdXmlInfos()) {
        				String fileName = std.getCondition().getFileName();
        				if (file.getName().matches(fileName)) {
        					DescriptorContainer.getInstance().put(file.getAbsolutePath(), desStd);
        					break;
        				}
					}
        		}
            } catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		
		super.init(site, input);
	}

	@Override
	protected void addPages() {
		mTextEditor = new XmlEditor();
		mTextEditor.setEditorPart(this);
		
		try {
			mSourceEditorIndex = addPage(mTextEditor, getEditorInput());
			setPageText(mSourceEditorIndex, mTextEditor.getTitle());
			IndentGuideHelper.getInstance().registerIfNot();
			
			setPartName(mTextEditor.getTitle());
		} catch (final PartInitException e) {
			e.printStackTrace();
		}

		// add listener for changes of the document source
		getDocument().addDocumentListener(new IDocumentListener() {

			@Override
			public void documentAboutToBeChanged(final DocumentEvent event) {
				// nothing to do
			}

			@Override
			public void documentChanged(final DocumentEvent event) {
				mSourceDirty = true;
			}
		});
	}

	@Override
	public void doSaveAs() {
		// not allowed
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		if (getActivePage() != mSourceEditorIndex) {
			updateSourceFromModel();
		}

		mTextEditor.doSave(monitor);
	}

	@Override
	protected void pageChange(final int newPageIndex) {
		// check for update from the source code
		if ((getActivePage() == mSourceEditorIndex) && (mSourceDirty))
			updateModelFromSource();

		// check for updates to be propagated to the source code
		if (newPageIndex == mSourceEditorIndex)
			updateSourceFromModel();

		// switch page
		super.pageChange(newPageIndex);

		// update page if needed
		final IFormPage page = getActivePageInstance();
		if (page != null) {
			// TODO update form page with new model data
			page.setFocus();
		}
	}

	private void updateModelFromSource() {
		// TODO update source code for source viewer using new model data
		mSourceDirty = false;
	}

	private void updateSourceFromModel() {
		// TODO update source page from model
		// getDocument().set("new source code");
		mSourceDirty = false;
	}

	private IDocument getDocument() {
		final IDocumentProvider provider = mTextEditor.getDocumentProvider();
		return provider.getDocument(getEditorInput());
	}

	private IFile getFile() {
		final IEditorInput input = getEditorInput();
		if (input instanceof FileEditorInput)
			return ((FileEditorInput) input).getFile();

		return null;
	}

	private String getContent() {
		return getDocument().get();
	}
	
    /**
     * Invokes content assist in this editor at the given offset
     *
     * @param offset the offset to invoke content assist at, or -1 to leave
     *            caret alone
     */
    public void invokeContentAssist(int offset) {
        ISourceViewer textViewer = getStructuredSourceViewer();
        if (textViewer instanceof StructuredTextViewer) {
            StructuredTextViewer structuredTextViewer = (StructuredTextViewer) textViewer;
            int operation = ISourceViewer.CONTENTASSIST_PROPOSALS;
            boolean allowed = structuredTextViewer.canDoOperation(operation);
            if (allowed) {
                if (offset != -1) {
                    StyledText textWidget = textViewer.getTextWidget();
                    // Clamp text range to valid offsets.
                    IDocument document = textViewer.getDocument();
                    int documentLength = document.getLength();
                    offset = Math.max(0, Math.min(offset, documentLength));
                    textWidget.setSelection(offset, offset);
                }
                structuredTextViewer.doOperation(operation);
            }
        }
    }
    
    /**
     * Returns the ISourceViewer associated with the Structured Text editor.
     */
    public final ISourceViewer getStructuredSourceViewer() {
        if (mTextEditor != null) {
            // We can't access mDelegate.getSourceViewer() because it is protected,
            // however getTextViewer simply returns the SourceViewer casted, so we
            // can use it instead.
            return mTextEditor.getTextViewer();
        }
        return null;
    }

    /**
     * Return the {@link StructuredTextEditor} associated with this XML editor
     *
     * @return the associated {@link StructuredTextEditor}
     */
    public StructuredTextEditor getStructuredTextEditor() {
        return mTextEditor;
    }
}
