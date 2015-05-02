package thahn.java.xmlchecker.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import thahn.java.xmlchecker.XmlCheckerPlugin;
import thahn.java.xmlchecker.editor.EditorParser;
import thahn.java.xmlchecker.editor.NodeList;
import thahn.java.xmlchecker.editor.TrieList;
import thahn.java.xmlchecker.marker.MarkerHelper;
import thahn.java.xmlchecker.marker.MarkerType;
import thahn.java.xmlchecker.message.ErrorMessage;
import thahn.java.xmlchecker.parser.Attribute;
import thahn.java.xmlchecker.parser.DescriptorTag;
import thahn.java.xmlchecker.parser.Pair;
import thahn.java.xmlchecker.parser.standard.DescriptorContainer;
import thahn.java.xmlchecker.parser.standard.DescriptorStandard;
import thahn.java.xmlchecker.parser.standard.TrieMap;
import thahn.java.xmlchecker.preference.StandardXmlInfo;

public class XmlEditor extends StructuredTextEditor { 

	private static IResource 									sResource;
	private String												mFilePath;
	private DescriptorStandard								mDescriptorStandard;
	private EditorParser 									mCurrentEditorParser 	= new EditorParser();
	private XmlConfiguration									mXmlConfiguration;
	
	public XmlEditor() {
		super();
	}
	
	@Override
	protected void setSourceViewerConfiguration(SourceViewerConfiguration config) {
		if (config instanceof XmlConfiguration) {
			super.setSourceViewerConfiguration(config);
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		sResource = (IResource) getEditorInput().getAdapter(IResource.class);
		mFilePath = sResource.getFullPath().toFile().getAbsolutePath();
		mXmlConfiguration = new XmlConfiguration(mFilePath);
		setSourceViewerConfiguration(mXmlConfiguration);
		mDescriptorStandard = DescriptorContainer.descriptor(mFilePath);
		XmlCheckerPlugin.getDefault().switchContext(true);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		XmlCheckerPlugin.getDefault().switchContext(false);
	}

	public ISourceViewer getSourceViewer2() {
		return getSourceViewer();
	}
	
	public static IResource getResource() {
		return sResource;
	}

	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor);
		MarkerHelper.getInstance().removeAll(getResource());
		
		try {
			IEditorInput input = getEditorInput();
			if (input instanceof FileEditorInput) {
			    IFile file = ((FileEditorInput) input).getFile();
			    // 1. xml grammar check
			    if (mCurrentEditorParser.parse(file.getLocationURI().toURL().getPath())) {
			    	TrieMap<DescriptorTag> standardTree = mDescriptorStandard.parser().getTree();
			    	standardTree.clearDepth();
			    	TrieList<DescriptorTag> contentsTree = mCurrentEditorParser.getTree();
			    	// 2. standard decriptor tag name check
			    	if (validateTagName(contentsTree, standardTree)) {
			    		MarkerHelper.getInstance().removeProblemMarker(getResource(), MarkerType.TAGNAME_PROBLEM);
			    	}
			    	// 3. dependency attribute check
			    	if (validateDependency(contentsTree)) {
			    		MarkerHelper.getInstance().removeProblemMarker(getResource(), MarkerType.DEPENDENCY_PROBLEM);
			    	}
			    	// 4. regular expression check
			    	if (validateRegExp(contentsTree)) {
			    		MarkerHelper.getInstance().removeProblemMarker(getResource(), MarkerType.DEPENDENCY_PROBLEM);
			    	}
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean validateTagName(TrieList<DescriptorTag> contentsTree, TrieMap<DescriptorTag> standardTree) {
		boolean ret = true;
		Attribute rootAttr = standardTree.getRoot().getAttrOfAttr(DescriptorStandard.ATTR_IGNORED);
		if ((rootAttr != null && Boolean.parseBoolean(rootAttr.getValue()))
				|| contentsTree.getRoot().getTagName().equals(standardTree.getRoot().getTagName())) {
    		NodeList<DescriptorTag> children = contentsTree.getChildren();
    		if (!validateDescriptor(children, standardTree)) {
    			ret = false;
    		}
    	} else {
    		ret = false;
    		MarkerHelper.getInstance().problem(getResource(), MarkerType.TAGNAME_PROBLEM
    				, ErrorMessage.invalidRootTagName(standardTree.getRoot().getTagName())
    				, contentsTree.getRoot().getStartTagPosition().getLine()
    				, contentsTree.getRoot().getStartTagPosition().getStart()
    				, contentsTree.getRoot().getStartTagPosition().getEnd());
    	}
		
		return ret;
	}
	
	private boolean validateDescriptor(NodeList<DescriptorTag> children, TrieMap<DescriptorTag> standardTree) {
//		add required
		boolean ret = true;
		for (String tagName : children.keySet().toArray(new String[children.keySet().size()])) {
			if (!standardTree.containsInCurrentDepth(tagName) && !mDescriptorStandard.containsAllAroundTagName(tagName)) {
				String errorMessage = ErrorMessage.invalidTagName(tagName, children.getData().getTagName());
				for (NodeList<DescriptorTag> child : children.getChild(tagName)) {
					MarkerHelper.getInstance().problem(getResource(), MarkerType.TAGNAME_PROBLEM
							, errorMessage, child.getData().getStartTagPosition().getLine()
							, child.getData().getStartTagPosition().getStart(), child.getData().getStartTagPosition().getEnd());
				}
				ret = false;
			} else {
				standardTree.pushDepth(tagName);
				for (NodeList<DescriptorTag> children2 : children.getChild(tagName)) {
					if (!validateDescriptor(children2, standardTree)) {
						ret = false;
					}
				}
				standardTree.popDepth();
			}
		}
		return ret;
	}

	private boolean validateDependency(TrieList<DescriptorTag> contentsTree) {
		boolean ret = true;
		for (Pair<String[], String[]> pair : mDescriptorStandard.getDependencyList()) {
    		List<String> dependencyList = new ArrayList<>();
			List<NodeList<DescriptorTag>> source = contentsTree.getChildren(pair.getFirst());
			for (NodeList<DescriptorTag> node : source) {
				dependencyList.add(node.getData().getTagValue());
			}
			List<NodeList<DescriptorTag>> target = contentsTree.getChildren(pair.getSecond());
			for (NodeList<DescriptorTag> node : target) {
				String value = node.getData().getTagValue();
				if (!dependencyList.contains(value)) {
					ret = false;
					MarkerHelper.getInstance().problem(getResource(), MarkerType.DEPENDENCY_PROBLEM
							, ErrorMessage.valueDependencyError(value), node.getData().getValuePosition().getLine()
							, node.getData().getValuePosition().getStart(), node.getData().getValuePosition().getEnd());
				}
			}
    	}		
		return ret;
	}
	
	private boolean validateRegExp(TrieList<DescriptorTag> contentsTree) {
		boolean ret = true;
		for (Pair<String[], String> pair : mDescriptorStandard.getRegExpList()) {
			String pattern = pair.getSecond();
			List<NodeList<DescriptorTag>> source = contentsTree.getChildren(pair.getFirst());
			for (NodeList<DescriptorTag> node : source) {
				String value = node.getData().getTagValue();
				if (!value.matches(pattern)) {
					ret = false;
					MarkerHelper.getInstance().problem(getResource(), MarkerType.REGEXP_PROBLEM
							, ErrorMessage.valueRegExpNotMatch(value, pattern) , node.getData().getValuePosition().getLine()
							, node.getData().getValuePosition().getStart(), node.getData().getValuePosition().getEnd());
				}
			}
		}		
		return ret;
	}
}

