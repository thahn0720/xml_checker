package thahn.java.xmlchecker.editors;

import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;

public class TextHover extends DefaultTextHover {

	public TextHover(ISourceViewer sourceViewer) {
		super(sourceViewer);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		String text = super.getHoverInfo(textViewer, hoverRegion);
		return text;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return super.getHoverRegion(textViewer, offset);
	}
}
