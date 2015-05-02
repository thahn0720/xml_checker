package thahn.java.xmlchecker.editors.command;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

public class CaretHint {
	
	private int x;
	private int y;
	private int height;

	public static CaretHint capture(StyledText styledText) {
		CaretHint caret = new CaretHint();
		Point location = styledText.getLocationAtOffset(styledText.getCaretOffset());
		location = styledText.toDisplay(location);
		caret.x = location.x;
		caret.y = location.y;
		caret.height = styledText.getBounds().height;
		return caret;
	}

	public static CaretHint capture(Text text){
		CaretHint caret = new CaretHint();
		Point location = text.getCaretLocation();
		location = text.toDisplay(location);
		caret.x = location.x;
		caret.y = location.y;
		caret.height = text.getLineHeight();
		return caret;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getHeight() {
		return height;
	}
}
