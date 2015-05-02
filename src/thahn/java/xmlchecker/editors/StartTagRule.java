package thahn.java.xmlchecker.editors;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;

/**
 *
 * @author th0720.ahn
 *
 */
public class StartTagRule extends MultiLineRule {

	public StartTagRule(IToken token) {
		this(token, false);
	}

	protected StartTagRule(IToken token, boolean endAsWell) {
		super("<", endAsWell ? "/>" : ">", token);
	}

	protected boolean sequenceDetected(ICharacterScanner scanner,
			char[] sequence, boolean eofAllowed) {
		int c = scanner.read();
		if (sequence[0] == '<') {
			if (c == '?') {
				// processing instruction - abort
				scanner.unread();
				return false;
			}
			if (c == '!') {
				scanner.unread();
				// comment - abort
				return false;
			}

		} else if (sequence[0] == '>') {
			scanner.unread();
		}
		return super.sequenceDetected(scanner, sequence, eofAllowed);
	}
}