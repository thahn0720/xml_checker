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
public class AttrContentAssistHelper extends BaseContentAssistHelper {
	
	public static final String									ATTR_SEPARATOR 		= " ";
	public static final String									NS_SEPARATOR 		= ":";
	public static final String									ATTR_FORMAT 		= "%s=\"\"";
	public static final String									ATTR_START_POINT	= "\"";
	
	public AttrContentAssistHelper(String stdDesPath) {
		super(stdDesPath);
	}

	@Override
	public void computeProposals(List<TagInfo> tagLists, String qualifier, int documentOffset, List<CompletionProposal> propList) {
		List<String> tagNameLists = new ArrayList<String>();
		for (TagInfo tagInfo : tagLists) {
			tagNameLists.add(tagInfo.getTagName());
		}
		
		try {
			qualifier = qualifier.replace("\n", " ");
			qualifier = qualifier.replace("\t", " ");
			// remove start tag
			// return : [0] : tag, [1] attr
			String[] splitted = qualifier.substring(1).split(AttrContentAssistHelper.ATTR_SEPARATOR);
			String tagName = splitted[0];
			String attrName = splitted.length > 1 ? 
					(splitted[splitted.length-1].contains(ATTR_START_POINT) ? "" : splitted[splitted.length-1]) 
						: "";
			int offsetPad = 0;
			if (attrName.contains(AttrValueContentAssistHelper.ATTR_VALUE_START_POINT_PREFIX)) {
				attrName = attrName.replace(AttrValueContentAssistHelper.ATTR_VALUE_START_POINT_PREFIX, "");
				offsetPad += AttrValueContentAssistHelper.ATTR_VALUE_START_POINT_PREFIX.length();
			}
					
			NodeMap<DescriptorTag> stdTag = descriptor().parser().getTree().
										getNode(tagNameLists.toArray(new String[tagNameLists.size()]));
			List<DescriptorTag> candidates = new ArrayList<>();
			List<String> proposalRet = new ArrayList<String>();
			
			if (descriptor().containsAllAroundTagName(tagName)) {
				candidates.add(descriptor().getAllAroundTag(tagName));
			}
			candidates.add(stdTag.getData());
			
			boolean isAttrEmpty = MyStrings.isEmpty(attrName);
			for (int j = 0; j < candidates.size(); j++) {
				DescriptorTag tag = candidates.get(j);
				for (Attribute attr : tag.getAttrs()) {
					String candiAttrName = attr.getQualifiedName();
					if (candiAttrName.contains(DescriptorStandard.ATTR_MY_PREFIX)) {
						continue;
					}
					if (isAttrEmpty) {
						proposalRet.add(candiAttrName);
					}  else if (candiAttrName.startsWith(attrName)) {
						proposalRet.add(candiAttrName);
					}
				}
			}
			
			Collections.sort(proposalRet);

			for (String retAttrName : proposalRet) {
				String displayAttrName = null;
				if (retAttrName.contains(NS_SEPARATOR)) {
					displayAttrName = retAttrName.substring(retAttrName.indexOf(NS_SEPARATOR) + 1);
				} else {
					displayAttrName = retAttrName;
				}
				
				String additionalInfo = displayAttrName;
				Attribute attr = stdTag.getData().getAttrOfAttr(retAttrName, DescriptorStandard.ATTR_DESCRIPTION);
				if (attr != null) {
					additionalInfo = attr.getValue(); 
				}
				
				String text = String.format(ATTR_FORMAT, retAttrName);
				CompletionProposal proposal = new CompletionProposal(text, documentOffset - (attrName.length() + offsetPad)
						, (attrName.length() + offsetPad)
						, text.length()-1, XmlCheckerPlugin.getImage(MyConstants.ATTRIBUTE_PATH), displayAttrName, null, additionalInfo);
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
