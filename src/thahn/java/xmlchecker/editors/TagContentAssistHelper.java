package thahn.java.xmlchecker.editors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.contentassist.CompletionProposal;

import thahn.java.xmlchecker.MyConstants;
import thahn.java.xmlchecker.XmlCheckerPlugin;
import thahn.java.xmlchecker.marker.MarkerHelper;
import thahn.java.xmlchecker.marker.MarkerType;
import thahn.java.xmlchecker.message.ErrorMessage;
import thahn.java.xmlchecker.parser.Attribute;
import thahn.java.xmlchecker.parser.DescriptorTag;
import thahn.java.xmlchecker.parser.exception.NotSupportedTagMapException;
import thahn.java.xmlchecker.parser.standard.DescriptorStandard;
import thahn.java.xmlchecker.parser.standard.NodeMap;
import thahn.java.xmlchecker.util.MyStrings;

/**
 *
 * @author th0720.ahn
 *
 */
public class TagContentAssistHelper extends BaseContentAssistHelper {

	public static final String											COMMENT_PREFIX 			= "<!";
	public static final String											TAG_PREFIX 				= "<";
	public static final String											END_TAG_PREFIX 			= "</";
	
	public static final String											TAG_FORMAT 				= "<%s></%s>";
	public static final String											END_TAG_FORMAT 			= "</%s>";
	public static final String											COMMENT_FORMAT 			= "<!--  -->";
	
	public static final String											COMMENT_DISPLAY_NAME 	= "Comment";

	public TagContentAssistHelper(String stdDesPath) {
		super(stdDesPath);
	}

	@Override
	public void computeProposals(List<TagInfo> tagLists, String qualifier, int documentOffset, List<CompletionProposal> propList) {
		List<String> tagNameLists = new ArrayList<String>();
		for (TagInfo tagInfo : tagLists) {
			tagNameLists.add(tagInfo.getTagName());
		}
		
		try {
			int qlen = qualifier.length();
			String format = null;
			boolean isComment = qualifier.startsWith(COMMENT_PREFIX);
			boolean containsSlush = qualifier.startsWith(END_TAG_PREFIX);
			String tempQualifier = null;
			if (MyStrings.isEmpty(qualifier)) {
				isComment = true;
				format = TAG_FORMAT;
				tempQualifier = new String(qualifier);
			} else if (containsSlush) {
				format = END_TAG_FORMAT;
				tempQualifier = qualifier.substring(2);
			} else {
				if (qualifier.equals(TAG_PREFIX)) {
					isComment = true;
				}
				format = TAG_FORMAT;
				tempQualifier = qualifier.substring(1);
			}
			
			NodeMap<DescriptorTag> descriptorTag = descriptor().parser().getTree()
										.getNode(tagNameLists.toArray(new String[tagNameLists.size()]));
			List<String> candidates = new ArrayList<String>();
			List<String> proposalRet = new ArrayList<String>();
			if (containsSlush) {
				descriptorTag = descriptorTag.getParent();
				proposalRet.add(tagNameLists.get(tagNameLists.size()-1));
			} else {
				for (NodeMap<DescriptorTag> item : descriptorTag.getAllChildren()) {
					Attribute attr = item.getData().getAttrOfAttr(DescriptorStandard.ATTR_IGNORED);
					if (attr != null && Boolean.parseBoolean(attr.getValue())) {
						continue;
					} else {
						candidates.add(item.getData().getTagName());
					}
				}
				
				Set<String> allAroundCandidates = descriptor().getAllAroundTagName();
				if (MyStrings.isEmpty(tempQualifier)) {
					proposalRet = candidates;
					if (allAroundCandidates != null) { 
						proposalRet.addAll(allAroundCandidates);
					}
				} else {
					for (String candidate : candidates) {
						if (candidate.startsWith(tempQualifier)) {
							proposalRet.add(candidate);
						}
					}
					if (allAroundCandidates != null) {
						for (String allAroundTagName : allAroundCandidates) {
							if (allAroundTagName.startsWith(tempQualifier)) {
								proposalRet.add(allAroundTagName);
							}
						}
					}
				}
				
				Collections.sort(proposalRet);
			}
			
			for (int i = 0; i < proposalRet.size(); i++) {
				String tag = proposalRet.get(i);
				NodeMap<DescriptorTag> node = descriptorTag.getChild(tag);
				Attribute attr = null;
				if (node == null) {
					attr = descriptor().getAllAroundTag(tag).getAttr(DescriptorStandard.ATTR_DESCRIPTION);
				} else {
					attr = node.getData().getAttr(DescriptorStandard.ATTR_DESCRIPTION);
				}
						
				String text = null;
				int cursorPos = 0;
				if (containsSlush) {
					text = String.format(format, tag);
					cursorPos = text.length();
				} else {
					text = String.format(format, tag, tag);
					cursorPos = text.length()/2;
				}
				CompletionProposal proposal = new CompletionProposal(text, documentOffset - qlen, qlen
						, cursorPos, XmlCheckerPlugin.getImage(MyConstants.TAG_PATH), tag
						, null, attr != null?attr.getValue():tag);
				propList.add(proposal);
			}
			
			if (isComment) {
				addCommentTagProposals(tagLists, qualifier, documentOffset, propList);
			}
		} catch (NotSupportedTagMapException e) {
			NodeMap<DescriptorTag> node = (NodeMap<DescriptorTag>) e.getNode();
			String wrongKey = e.getWrongKey();
			MarkerHelper.getInstance().problem(XmlEditor.getResource(), MarkerType.TAGNAME_PROBLEM
					,ErrorMessage.invalidTagName(wrongKey, node.getData().getTagName()), 1, 0, 0);
		}
	}
	
	public void addCommentTagProposals(List<TagInfo> tagLists, String qualifier, int documentOffset, List<CompletionProposal> propList) {
		int qlen = qualifier.length();
		String text = COMMENT_FORMAT;
		int cursorPos = text.length()/2 + 1;
		CompletionProposal proposal = new CompletionProposal(text, documentOffset - qlen, qlen
				, cursorPos, XmlCheckerPlugin.getImage(MyConstants.COMMENT_PATH), COMMENT_DISPLAY_NAME, null, null);
		propList.add(proposal);
	}
}
