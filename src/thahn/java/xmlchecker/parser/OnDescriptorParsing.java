package thahn.java.xmlchecker.parser;


/**
 *
 * @author th0720.ahn
 *
 */
public interface OnDescriptorParsing {
	
	public void onStart();
	public void onTag(DescriptorTag tag);
	public void onValue(DescriptorTag tag);
}
