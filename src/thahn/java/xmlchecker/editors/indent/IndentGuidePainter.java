package thahn.java.xmlchecker.editors.indent;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ColorUtil;

public class IndentGuidePainter implements IPainter, PaintListener {

	/** Indicates whether this painter is active. */
	private boolean fIsActive = false;
	/** The source viewer this painter is attached to. */
	private ITextViewer fTextViewer;
	/** The viewer's widget. */
	private StyledText fTextWidget;
	/** Tells whether the advanced graphics sub system is available. */
	private final boolean fIsAdvancedGraphicsPresent;

	private int lineAlpha;
	private int lineStyle;
	private int lineWidth;
	private int lineShift;
	private Color foregroundColor;
	private int spaceWidth;
	private boolean drawLeftEnd;
	private boolean drawBlankLine;
	private boolean skipCommentBlock;

	/**
	 * Creates a new painter for the given text viewer.
	 * 
	 * @param textViewer
	 *            the text viewer the painter should be attached to
	 */
	public IndentGuidePainter(ITextViewer textViewer) {
		super();
		fTextViewer = textViewer;
		fTextWidget = textViewer.getTextWidget();
		GC gc = new GC(fTextWidget);
		gc.setAdvanced(true);
		fIsAdvancedGraphicsPresent = gc.getAdvanced();
		gc.dispose();

		lineAlpha = 50;
		lineStyle = SWT.LINE_SOLID;
		lineWidth = 1;
		lineShift = 3;
		foregroundColor = new Color(PlatformUI.getWorkbench().getDisplay(), ColorUtil.getColorValue("0,0,0"));
		drawLeftEnd = true;
		drawBlankLine = false;
		skipCommentBlock = false;
	}

	/*
	 * @see org.eclipse.jface.text.IPainter#dispose()
	 */
	public void dispose() {
		fTextViewer = null;
		fTextWidget = null;
	}

	/*
	 * @see org.eclipse.jface.text.IPainter#paint(int)
	 */
	public void paint(int reason) {
		IDocument document = fTextViewer.getDocument();
		if (document == null) {
			deactivate(false);
			return;
		}
		if (!fIsActive) {
			fIsActive = true;
			fTextWidget.addPaintListener(this);
			redrawAll();
		} else if (reason == CONFIGURATION || reason == INTERNAL) {
			redrawAll();
		} else if (reason == TEXT_CHANGE) {
			// redraw current line only
			try {
				IRegion lineRegion = document
						.getLineInformationOfOffset(getDocumentOffset(fTextWidget
								.getCaretOffset()));
				int widgetOffset = getWidgetOffset(lineRegion.getOffset());
				int charCount = fTextWidget.getCharCount();
				int redrawLength = Math.min(lineRegion.getLength(), charCount
						- widgetOffset);
				if (widgetOffset >= 0 && redrawLength > 0) {
					fTextWidget.redrawRange(widgetOffset, redrawLength, true);
				}
			} catch (BadLocationException e) {
				// ignore
			}
		}
	}

	/*
	 * @see org.eclipse.jface.text.IPainter#deactivate(boolean)
	 */
	public void deactivate(boolean redraw) {
		if (fIsActive) {
			fIsActive = false;
			fTextWidget.removePaintListener(this);
			if (redraw) {
				redrawAll();
			}
		}
	}

	/*
	 * @see
	 * org.eclipse.jface.text.IPainter#setPositionManager(org.eclipse.jface.
	 * text.IPaintPositionManager)
	 */
	public void setPositionManager(IPaintPositionManager manager) {
		// no need for a position manager
	}

	/*
	 * @see
	 * org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events
	 * .PaintEvent)
	 */
	public void paintControl(PaintEvent event) {
		if (fTextWidget != null) {
			handleDrawRequest(event.gc, event.x, event.y, event.width,
					event.height);
		}
	}

	/*
	 * Draw characters in view range.
	 */
	private void handleDrawRequest(GC gc, int x, int y, int w, int h) {
		int startLine = fTextWidget.getLineIndex(y);
		int endLine = fTextWidget.getLineIndex(y + h - 1);
		if (startLine <= endLine && startLine < fTextWidget.getLineCount()) {
			Color fgColor = gc.getForeground();
			LineAttributes lineAttributes = gc.getLineAttributes();
			gc.setForeground(foregroundColor);
			gc.setLineStyle(lineStyle);
			gc.setLineWidth(lineWidth);
			if (fIsAdvancedGraphicsPresent) {
				int alpha = gc.getAlpha();
				gc.setAlpha(this.lineAlpha);
				drawLineRange(gc, startLine, endLine, x, w);
				gc.setAlpha(alpha);
			} else {
				drawLineRange(gc, startLine, endLine, x, w);
			}
			gc.setForeground(fgColor);
			gc.setLineAttributes(lineAttributes);
		}
	}

	/**
	 * Draw the given line range.
	 * 
	 * @param gc
	 *            the GC
	 * @param startLine
	 *            first line number
	 * @param endLine
	 *            last line number (inclusive)
	 * @param x
	 *            the X-coordinate of the drawing range
	 * @param w
	 *            the width of the drawing range
	 */
	private void drawLineRange(GC gc, int startLine, int endLine, int x, int w) {
		int tabs = fTextWidget.getTabs();
		spaceWidth = gc.getAdvanceWidth(' ');

		StyledTextContent content = fTextWidget.getContent();
		for (int line = startLine; line <= endLine; line++) {
			int widgetOffset = fTextWidget.getOffsetAtLine(line);
			if (!isFoldedLine(content.getLineAtOffset(widgetOffset))) {
				String text = fTextWidget.getLine(line);
				int extend = 0;
				if (skipCommentBlock && assumeCommentBlock(text, tabs)) {
					extend -= tabs;
				}
				if (drawBlankLine && text.trim().length() == 0) {
					int prevLine = line;
					while (--prevLine >= 0) {
						text = fTextWidget.getLine(prevLine);
						if (text.trim().length() > 0) {
							int postLine = line;
							int lineCount = fTextWidget.getLineCount();
							while (++postLine < lineCount) {
								String tmp = fTextWidget.getLine(postLine);
								if (tmp.trim().length() > 0) {
									if (countSpaces(text, tabs) < countSpaces(
											tmp, tabs)) {
										extend += tabs;
									}
									break;
								}
							}
							break;
						}
					}
				}
				int count = countSpaces(text, tabs) + extend;
				for (int i = drawLeftEnd ? 0 : tabs; i < count; i += tabs) {
					draw(gc, widgetOffset, i);
				}
			}
		}
	}

	private int countSpaces(String str, int tabs) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			switch (str.charAt(i)) {
			case ' ':
				count++;
				break;
			case '\t':
				int z = tabs - count % tabs;
				count += z;
				break;
			default:
				return count;
			}
		}
		return count;
	}

	private boolean assumeCommentBlock(String text, int tabs) {
		int count = countSpaces(text, tabs);
		count = (count / tabs) * tabs;
		int index = 0;
		for (int i = 0; i < count; i++) {
			switch (text.charAt(index)) {
			case ' ':
				index++;
				break;
			case '\t':
				index++;
				int z = tabs - i % tabs;
				i += z;
				break;
			default:
				i = count;
			}
		}
		text = text.substring(index);
		if (text.matches("^ \\*([ \\t].*|/.*|)$")) {
			return true;
		}
		return false;
	}

	/**
	 * Check if the given widget line is a folded line.
	 * 
	 * @param widgetLine
	 *            the widget line number
	 * @return <code>true</code> if the line is folded
	 */
	private boolean isFoldedLine(int widgetLine) {
		if (fTextViewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) fTextViewer;
			int modelLine = extension.widgetLine2ModelLine(widgetLine);
			int widgetLine2 = extension.modelLine2WidgetLine(modelLine + 1);
			return widgetLine2 == -1;
		}
		return false;
	}

	/**
	 * Redraw all of the text widgets visible content.
	 */
	private void redrawAll() {
		fTextWidget.redraw();
	}

	/**
	 * 
	 * @param gc
	 * @param offset
	 * @param column
	 */
	private void draw(GC gc, int offset, int column) {
		Point pos = fTextWidget.getLocationAtOffset(offset);
		pos.x += column * spaceWidth + lineShift;
		gc.drawLine(pos.x, pos.y, pos.x,
				pos.y + fTextWidget.getLineHeight(offset));
	}

	/**
	 * Convert a document offset to the corresponding widget offset.
	 * 
	 * @param documentOffset
	 *            the document offset
	 * @return widget offset
	 */
	private int getWidgetOffset(int documentOffset) {
		if (fTextViewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) fTextViewer;
			return extension.modelOffset2WidgetOffset(documentOffset);
		}
		IRegion visible = fTextViewer.getVisibleRegion();
		int widgetOffset = documentOffset - visible.getOffset();
		if (widgetOffset > visible.getLength()) {
			return -1;
		}
		return widgetOffset;
	}

	/**
	 * Convert a widget offset to the corresponding document offset.
	 * 
	 * @param widgetOffset
	 *            the widget offset
	 * @return document offset
	 */
	private int getDocumentOffset(int widgetOffset) {
		if (fTextViewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) fTextViewer;
			return extension.widgetOffset2ModelOffset(widgetOffset);
		}
		IRegion visible = fTextViewer.getVisibleRegion();
		if (widgetOffset > visible.getLength()) {
			return -1;
		}
		return widgetOffset + visible.getOffset();
	}

}
