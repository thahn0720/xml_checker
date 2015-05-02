package thahn.java.xmlchecker.util;

import java.io.IOException;
import java.util.HashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * 
 * @author th0720.ahn
 *
 */
public class JsonMapper {
	/**
	 * 
	 * @param root
	 * @param key
	 * @param value primitive type is not allowed 
	 * @return
	 */
	public static String makeJson(String root, String key, Object value) {
		return String.format("{\"%s\": {\"%s\": %s}}", root, key, makeJson(value));
	}
	
	public static String makeJson(String root, String key, int value) {
		return String.format("{\"%s\": {\"%s\": %d}}", root, key, value);
	}
	
	/**
	 * 
	 * @param root
	 * @param value in value's object, ignore @jsonRootName
	 * @return
	 */
	public static String makeJson(String key, Object value) {
		return String.format("{\"%s\": %s}", key, makeJson(value));
	}
	
	public static String makeJson(Object value) {
		return makeJson(value, false);
	}
	
	public static String makeJson(Object value, boolean withRoot) {
		ObjectMapper mapper = getJsonWriteMapper(withRoot);
		try {
			return mapper.writeValueAsString(value);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param 1. values *[key, value] <br> 
	 * 		  2.root, values *[key, value] <br> 
	 * @return
	 */
	public static String makeJsonObject(Object ...values) {
		int startIndex = 0;
		Object root = null;
		
		if (values.length % 2 != 0) {
			startIndex = 1;
			root = values[0];
		}
		
		HashMap<Object, Object> map = new HashMap<>();
		for (int i = startIndex; i < values.length; i+=2) {
			map.put(values[i], values[i+1]);
		}
		
		if (root == null) {
			return makeJson(map);
		} 
		return makeJson(String.valueOf(root), map);
	}
	
	/**
	 * 
	 * @param *[value] 
	 * @return
	 */
	public static String makeJsonValues(Object ...values) {
		int startIndex = 0;
		Object root = null;
		
		if (values.length % 2 != 0) {
			startIndex = 1;
			root = values[0];
		}
		
		HashMap<Object, Object> map = new HashMap<>();
		for (int i = startIndex; i < values.length; i+=2) {
			map.put(values[i], values[i+1]);
		}
		
		if (root == null) {
			return makeJson(map);
		} 
		return makeJson(String.valueOf(root), map);
	}
	
	public static <R> R readJsonObject(String value, Class<R> returnType) throws JsonParseException, JsonMappingException, IOException {
		R ret = null;
		try {
			ret = readJsonObject(value, returnType, true);
		} catch (Exception e) {
//			e.printStackTrace();
			ret = readJsonObject(value, returnType, false);
		}
		return ret;
	}
	
	public static <R> R readJsonObject(String value, Class<R> returnType, boolean withRoot) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = getJsonReadMapper(withRoot);
		R ret = mapper.readValue(value, returnType);
		return ret;
	}
	
	public static String getPrettyJson(String obj) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object json = mapper.readValue(obj, Object.class);
			String indented = mapper.defaultPrettyPrintingWriter().writeValueAsString(json);
			return indented;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static ObjectMapper getJsonWriteMapper(boolean withRoot) {
		ObjectMapper mapper = new ObjectMapper();
		if (withRoot) { 
			mapper.configure(SerializationConfig.Feature.WRAP_ROOT_VALUE, true);
		}
		mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
		mapper.enable(SerializationConfig.Feature.REQUIRE_SETTERS_FOR_GETTERS);
		mapper.setSerializationInclusion(Inclusion.NON_NULL);
		
		return mapper;
	}
	
	public static ObjectMapper getJsonReadMapper(boolean withRoot) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE, withRoot);
		mapper.configure(DeserializationConfig.Feature.AUTO_DETECT_FIELDS, true);
		mapper.configure(DeserializationConfig.Feature.USE_ANNOTATIONS, true);
		mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		mapper.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		return mapper;
	}
}
