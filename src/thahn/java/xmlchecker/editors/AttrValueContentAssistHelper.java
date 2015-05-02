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
import thahn.java.xmlchecker.parser.standard.TrieMap;
import thahn.java.xmlchecker.util.MyStrings;

/**
 *
 * @author th0720.ahn
 *
 */
public class AttrValueContentAssistHelper extends BaseContentAssistHelper {
	
	public static final String									ATTR_VALUE_START_POINT_PREFIX	= "=";
	public static final String									ATTR_VALUE_START_POINT			= "\"";
	
	public AttrValueContentAssistHelper(String stdDesPath) {
		super(stdDesPath);
	}
	
	// <tag attr="" or <tag attr = "
	@Override
	public void computeProposals(List<TagInfo> tagLists, String qualifier, int documentOffset, List<CompletionProposal> propList) {
		List<String> tagNameLists = new ArrayList<String>();
		for (TagInfo tagInfo : tagLists) {
			tagNameLists.add(tagInfo.getTagName());
		}
		
		try {
			int qlen = qualifier.length();
			// remove start tag
			// return : [0] : tag, [1] full attr name
			String[] tempQualifier = qualifier.split(ATTR_VALUE_START_POINT);
			String[] splitted = tempQualifier[0].substring(1).split(AttrContentAssistHelper.ATTR_SEPARATOR);
			String tagName = splitted[0].trim();
			String attrName = getLastAttrName(qualifier);
			String valueName = new String();
			if (!MyStrings.isNullorEmpty(tempQualifier[tempQualifier.length - 1]) && !tempQualifier[tempQualifier.length - 1].contains(ATTR_VALUE_START_POINT_PREFIX)) {
				valueName = tempQualifier[tempQualifier.length - 1].trim();
			}
			
			NodeMap<DescriptorTag> stdTag = descriptor().parser().getTree().
										getNode(tagNameLists.toArray(new String[tagNameLists.size()]));
			List<DescriptorTag> candidates = new ArrayList<>();
			List<String> proposalRet = new ArrayList<String>();
			
			candidates.add(stdTag.getData());
			
			for (int j = 0; j < candidates.size(); j++) {
				DescriptorTag tag = candidates.get(j);
				for (Attribute attr : tag.getAttrs()) {
					if (attrName.equals(attr.getQualifiedName())) {
						String value = attr.getValue();
						if (!MyStrings.isNullorEmpty(value) && !isTagOrAttrRegExpValue(value)) {
							for (String str : splitTagOrAttrValue(value)) {
								proposalRet.add(str);
							}	
						}
					}
				}
			}
			
			Collections.sort(proposalRet);
			for (String retValueName : proposalRet) {
				CompletionProposal proposal = new CompletionProposal(retValueName, documentOffset - valueName.length()
						, valueName.length()
						, retValueName.length(), XmlCheckerPlugin.getImage(MyConstants.VARIABLE_PATH), retValueName, null, null);
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
