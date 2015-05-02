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
import thahn.java.xmlchecker.parser.DescriptorTag;
import thahn.java.xmlchecker.parser.exception.NotSupportedTagMapException;
import thahn.java.xmlchecker.parser.standard.DescriptorStandard;
import thahn.java.xmlchecker.parser.standard.NodeMap;
import thahn.java.xmlchecker.parser.standard.TrieMap;
import thahn.java.xmlchecker.util.MyStrings;

/**
 *
 * @author th0720.ahn
 *
 */
public class TagValueContentAssistHelper extends BaseContentAssistHelper {

	public static final String											TAG_PREFIX 				= "<";
	public static final String											TAG_POSTFIX 			= ">";

	public TagValueContentAssistHelper(String stdDesPath) {
		super(stdDesPath);
	}

	// <tag attr="attrValue"> or <tag>
	@Override
	public void computeProposals(List<TagInfo> tagLists, String qualifier, int documentOffset, List<CompletionProposal> propList) {
		List<String> tagNameLists = new ArrayList<String>();
		for (TagInfo tagInfo : tagLists) {
			tagNameLists.add(tagInfo.getTagName());
		}
		
		try {
			int qlen = qualifier.length();
			boolean existAttr = qualifier.indexOf(AttrValueContentAssistHelper.ATTR_VALUE_START_POINT_PREFIX)!=-1?true:false;
			String tagName = null;
			String tagValue = qualifier.substring(qualifier.indexOf(TAG_POSTFIX) + 1).trim();
			if (existAttr) {
				tagName = qualifier.substring(TAG_PREFIX.length(), qualifier.indexOf(AttrContentAssistHelper.ATTR_SEPARATOR));
			} else {
				tagName = qualifier.substring(TAG_PREFIX.length(), qualifier.indexOf(TAG_POSTFIX));
			}
			
			NodeMap<DescriptorTag> stdTag = descriptor().parser().getTree().
										getNode(tagNameLists.toArray(new String[tagNameLists.size()]));
			List<DescriptorTag> candidates = new ArrayList<>();
			if (descriptor().containsAllAroundTagName(tagName)) {
				candidates.add(descriptor().getAllAroundTag(tagName));
			}
			candidates.add(stdTag.getData());
			
			List<String> proposalRet = new ArrayList<String>();
			for (DescriptorTag candidate : candidates) {
				String candiTagValue = candidate.getTagValue();
				if (!MyStrings.isNullorEmpty(candiTagValue) && !isTagOrAttrRegExpValue(candiTagValue)) {
					for (String value : splitTagOrAttrValue(candiTagValue)) {
						if (value.startsWith(tagValue)) {
							proposalRet.add(value);
						}
					}
				}
			}
			
			Collections.sort(proposalRet);
			
			for (int i = 0; i < proposalRet.size(); i++) {
				String tagFullValue = proposalRet.get(i);
				CompletionProposal proposal = new CompletionProposal(tagFullValue, documentOffset - tagValue.length()
						, tagValue.length()
						, tagFullValue.length(), XmlCheckerPlugin.getImage(MyConstants.VARIABLE_PATH), tagFullValue, null, null);
				propList.add(proposal);
			}
		} catch (NotSupportedTagMapException e) {
			NodeMap<DescriptorTag> node = (NodeMap<DescriptorTag>) e.getNode();
			String wrongKey = e.getWrongKey();
			MarkerHelper.getInstance().problem(XmlEditor.getResource(), MarkerType.TAGNAME_PROBLEM
					,ErrorMessage.invalidTagName(wrongKey, node.getData().getTagName()), 1, 0, 0);
		}
	}
}
