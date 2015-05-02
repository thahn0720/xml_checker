package thahn.java.xmlchecker.editors.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import thahn.java.xmlchecker.XmlCheckerPlugin;
import thahn.java.xmlchecker.editors.BaseContentAssistHelper;
import thahn.java.xmlchecker.editors.EditorForm;
import thahn.java.xmlchecker.editors.TextHover;
import thahn.java.xmlchecker.editors.XmlEditor;
import thahn.java.xmlchecker.editors.BaseContentAssistHelper.TagInfo;
import thahn.java.xmlchecker.parser.Attribute;
import thahn.java.xmlchecker.parser.DescriptorTag;
import thahn.java.xmlchecker.parser.exception.NotSupportedTagMapException;
import thahn.java.xmlchecker.parser.standard.DescriptorStandard;
import thahn.java.xmlchecker.util.PdeUtils;
import thahn.java.xmlchecker.view.MyPopup;
import thahn.java.xmlchecker.view.MyPopup.OnPopupListener;

/**
 * 
 * @author th0720.ahn
 *
 */
public class TooltipHandler extends AbstractHandler {

	private TextHover 						mTextHover; 
	private boolean								mOpen			= false;
	
	public TooltipHandler() {
		super();
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (!mOpen && XmlCheckerPlugin.isInCmsEditor()) {
			Control focusControl = Display.getDefault().getFocusControl();
			int caretOffset = 0;
			CaretHint referencePoint = null;

			if (focusControl instanceof StyledText) {
				StyledText styledText = (StyledText) focusControl;
				caretOffset = styledText.getCaretOffset();
				referencePoint = CaretHint.capture(styledText);
			} else if (focusControl instanceof Text) {
				Text text = (Text) focusControl;
				caretOffset = text.getCaretPosition();
				referencePoint = CaretHint.capture(text);
			}
			
			if (referencePoint == null) {
				return null;
			}
			
			ISourceViewer sourceViewer = ((XmlEditor) ((EditorForm) PdeUtils.getActiveEditor()).getStructuredTextEditor()).getSourceViewer2();
			if (mTextHover == null) {
				mTextHover = new TextHover(sourceViewer);  
			}
			String text = mTextHover.getHoverInfo(sourceViewer, mTextHover.getHoverRegion(sourceViewer, caretOffset));
			if (text == null) {
				text = "Nothing to describe";
			} else if (text.contains("changed lines")) {
				text = null;
			}
			// temp code
			// TODO : implements
//			if (text == null) {
//				try {
//					List<TagInfo> tagLists = BaseContentAssistHelper.computeCurrentLocationOver(sourceViewer.getDocument(), caretOffset);
//					List<String> tagNameLists = new ArrayList<String>();
//					for (TagInfo tagInfo : tagLists) {
//						tagNameLists.add(tagInfo.getTagName());
//					}
//					DescriptorTag descriptorTag = CmsDescriptorStandard.parser().getTree().get(tagNameLists.toArray(new String[tagNameLists.size()]));
//					Attribute attr = descriptorTag.getAttr(CmsDescriptorStandard.ATTR_DESCRIPTION);
//					if (attr != null) {
//						text = attr.getValue();
//					}
//				} catch (NotSupportedTagMapException e) {
//					e.printStackTrace();
//				} catch (BadLocationException e) {
//					e.printStackTrace();
//				}
//			}
			//
			MyPopup.show(XmlCheckerPlugin.getDisplay(), "Descriptor", text, "descriptor", referencePoint.getX(), referencePoint.getY()+20
					, mOnPopupListener);
		}
		
		return null;
	}
	
	private OnPopupListener mOnPopupListener = new OnPopupListener() {
		
		@Override
		public void onOpen() {
			mOpen = true;
		}
		
		@Override
		public void onClose() {
			mOpen = false;
		}
	};
}
