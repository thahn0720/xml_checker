package thahn.java.xmlchecker.editors;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;

public class XmlConfiguration extends StructuredTextViewerConfigurationXML {
	
	private TextHover textHover;
	private XmlContentAssistProcessor tagContentAssistProcessor = new XmlContentAssistProcessor();
	
	public XmlConfiguration(String path) {
		tagContentAssistProcessor.setStdDesPath(path);
	}
	
	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		return getCmsTextHover(sourceViewer);
	}

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		return getCmsTextHover(sourceViewer);
	}
	
	private TextHover getCmsTextHover(ISourceViewer sourceViewer) {
		if (textHover == null) {
			textHover = new TextHover(sourceViewer);
		}
		return textHover;
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		IContentAssistant contentAssist = super.getContentAssistant(sourceViewer);
		if (contentAssist instanceof ContentAssistant) {
			ContentAssistant assistant = (ContentAssistant) contentAssist;
			
			String[] contentsTypes = getConfiguredContentTypes(sourceViewer);
			for (String contentsType : contentsTypes) {
				assistant.setContentAssistProcessor(tagContentAssistProcessor, contentsType);
			}
			
			assistant.enableAutoActivation(true);
			assistant.setAutoActivationDelay(0);
			assistant.setProposalPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
			assistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
			assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		}
	    return contentAssist;
	}
	
	@Override
	protected IContentAssistProcessor[] getContentAssistProcessors(ISourceViewer sourceViewer, String partitionType) {
		return null; // return super.getContentAssistProcessors(sourceViewer, partitionType);
	}
}