package thahn.java.xmlchecker.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Point;

import thahn.java.xmlchecker.editors.BaseContentAssistHelper.TagInfo;
import thahn.java.xmlchecker.util.Pair;

/**
 * 
 * @author th0720.ahn
 *
 */
public class XmlContentAssistProcessor implements IContentAssistProcessor {

	private final static String[] STYLETAGS = new String[] { "b", "i", "code", "strong" };
	private final static String[] STYLELABELS = new String[] { "bold", "italic", "code", "strong" };

	private final static int								TYPE_NONE				= -1;
	private final static int								TYPE_TAG				= 0;
	private final static int								TYPE_ATTR				= 1;
	private final static int								TYPE_TAG_VALUE			= 3;
	private final static int								TYPE_ATTR_VALUE			= 4;
	private final static int								TYPE_TAG_N_TAG_VALUE	= 5;
	
	private TagContentAssistHelper 							mTagAssistHelper;
	private AttrContentAssistHelper 						mAttrAssistHelper;
	private TagValueContentAssistHelper 					mTagValueAssistHelper;
	private AttrValueContentAssistHelper 					mAttrValueAssistHelper;
	
	private String											mStdDesPath;
	
	public XmlContentAssistProcessor() {
		super();
	}
	
	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
		IDocument doc = viewer.getDocument();
		List<TagInfo> tagLists = BaseContentAssistHelper.computeCurrentLocation(doc, documentOffset);
		
		Point selectedRange = viewer.getSelectedRange();
		List<CompletionProposal> propList = new ArrayList<>();

		if (selectedRange.y > 0) {
			try {
				String text = doc.get(selectedRange.x, selectedRange.y);
				computeStyleProposals(text, selectedRange, propList);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else {
			Pair<Integer, String> qualifier = getQualifier(doc, documentOffset);
			switch (qualifier.getFirst()) {
			case TYPE_TAG:
				mTagAssistHelper.computeProposals(tagLists, qualifier.getSecond(), documentOffset, propList);
				break;
			case TYPE_ATTR:
				mAttrAssistHelper.computeProposals(tagLists, qualifier.getSecond(), documentOffset, propList);
				break;
			case TYPE_TAG_VALUE:
				mTagValueAssistHelper.computeProposals(tagLists, qualifier.getSecond(), documentOffset, propList);
				break;
			case TYPE_ATTR_VALUE:
				mAttrValueAssistHelper.computeProposals(tagLists, qualifier.getSecond(), documentOffset, propList);
				break;
			case TYPE_TAG_N_TAG_VALUE:
				mTagAssistHelper.computeProposals(tagLists, "", documentOffset, propList);
				mTagValueAssistHelper.computeProposals(tagLists, qualifier.getSecond(), documentOffset, propList);
				break;
			case TYPE_NONE:
				break;
			}
		}
		ICompletionProposal[] proposals = new ICompletionProposal[propList.size()];
		propList.toArray(proposals);
		return proposals;
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer arg0, int arg1) {
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return "CmsXmlContentAssistProcessor Error";
	}

	private Pair<Integer, String>  getQualifier(IDocument doc, int documentOffset) {
		int type = TYPE_NONE;
		StringBuilder buf = new StringBuilder();
		while (true) {
			try {
				char c = doc.getChar(--documentOffset);
				buf.append(c);
				if (c == '<') {
					String ret = buf.reverse().toString();
					if (type != TYPE_NONE) {
						return Pair.of(type, ret);
					} else if (ret.contains(AttrContentAssistHelper.ATTR_SEPARATOR) && !ret.contains("/")) {
						int index = ret.lastIndexOf("\"");
						if (index != -1) {
							for (int i = index - 1; i >= 0; --i) {
								char c2 = ret.charAt(i);
								if (c2 == '\n' || c2 == '\t' || c2 == ' ') {
									continue;	
								} else if (c2 == '=') {
									return Pair.of(TYPE_ATTR_VALUE, ret);
								} else {
									// case : <tag attr="value"[cursor]
									// can do : 1. new attr content assist <current> 
									//			2. auto complete for end tag '>'
									//          3. do nothing 
									return Pair.of(TYPE_ATTR, ret);
								}
							}
						} else {
							return Pair.of(TYPE_ATTR, ret);
						}
					} else {
						if (ret.contains(">") && !ret.contains("</")) {
							return Pair.of(TYPE_TAG_VALUE, ret);
						} else {
							return Pair.of(TYPE_TAG, ret);
						}
					} 
				} else if (c == '>') {
					// 바로 다음에 오는 태그가 '</'이면 type_tag_value, 다른것이면('<') type_tag
//					<>
//					 요 사이에 아무것도 없을 때 TYPE_TAG_N_TAG_VALUE
//					</>
					int tempOffset = documentOffset;
					while (true) {
						char c2 = doc.getChar(++tempOffset);
						if (c2 == '<') {
							char c3 = doc.getChar(tempOffset+1);
							if (c3 == '/') {
								type = TYPE_TAG_N_TAG_VALUE;
								break;
							} 
						}
					}
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
				return Pair.of(-1, "");
			}
		}
	}

	private void computeStyleProposals(String selectedText, Point selectedRange, List<CompletionProposal> propList) {
		// Loop through all styles
		for (int i = 0; i < STYLETAGS.length; i++) {
			String tag = STYLETAGS[i];
			// Compute replacement text
			String replacement = "<" + tag + ">" + selectedText + "</" + tag
					+ ">";
			// Derive cursor position
			int cursor = tag.length() + 2;
			// Compute a suitable context information
			IContextInformation contextInfo = new ContextInformation(null,
					STYLELABELS[i] + " Style");
			CompletionProposal proposal = new CompletionProposal(replacement,
					selectedRange.x, selectedRange.y, cursor, null,
					STYLELABELS[i], contextInfo, replacement);
			propList.add(proposal);
		}
	}

	public void setStdDesPath(String stdDesPath) {
		this.mStdDesPath = stdDesPath;
		
		mTagAssistHelper = new TagContentAssistHelper(mStdDesPath);
		mAttrAssistHelper = new AttrContentAssistHelper(mStdDesPath);
		mTagValueAssistHelper = new TagValueContentAssistHelper(mStdDesPath);
		mAttrValueAssistHelper = new AttrValueContentAssistHelper(mStdDesPath);
	}
}
