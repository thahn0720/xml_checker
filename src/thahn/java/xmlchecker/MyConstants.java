package thahn.java.xmlchecker;

import thahn.java.xmlchecker.template.Flag;

public class MyConstants {
	
	public static final String 									LOGO_PATH				= "/icons/cloud.png";
	public static final String 									TAG_PATH				= "/icons/tag_generic_emphasized_obj.gif";
	public static final String 									COMMENT_PATH			= "/icons/tag-macro.gif";
	public static final String 									ATTRIBUTE_PATH			= "/icons/attribute.gif";
	public static final String 									VARIABLE_PATH			= "/icons/variable_view.gif";
	
	/** "/descriptorchecker/template/init.xml" */
	public static final String 									DESCRIPTOR_TEMPLATE_PATH	
															= "/" + Flag.class.getPackage().getName().replace(".", "/") + "/init.xml";
	
	public static final String 									CMS_XML_CONTEXT			= "thahn.java.descriptorchecker.context";	
	public static final String 									COMMAND_TOOLTIP_DES		= "thahn.java.descriptorchecker.command.tooltip.des";	
	public static final String 									CMS_XML_EXTENSTION		= "xml";	
}
