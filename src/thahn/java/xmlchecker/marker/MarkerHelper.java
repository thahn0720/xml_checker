package thahn.java.xmlchecker.marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.MarkerUtilities;

/**
 *
 * @author th0720.ahn
 *
 */
public class MarkerHelper {

	private static MarkerHelper 						mInstance;
	
	public static MarkerHelper getInstance() {
		if (mInstance == null) {
			mInstance = new MarkerHelper();
		}
		return mInstance;
	}
	
	@SuppressWarnings("unchecked")
	public void problem(IResource resource, MarkerType type, String message, int lineNumber, int charStart, int charEnd) {
		try {
			if (!contains(resource, type, message)) {
				IMarker marker = resource.createMarker(type.getType());
				marker.setAttribute(IMarker.MESSAGE, message);
	//			marker.setAttribute(IMarker.LOCATION, lineNumber);
				marker.setAttribute(IMarker.LINE_NUMBER, (Integer) lineNumber);
				marker.setAttribute(IMarker.CHAR_START, charStart);
				marker.setAttribute(IMarker.CHAR_END, charEnd);
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public void removeProblemMarker(IResource resource, MarkerType type) {
		try {
			resource.deleteMarkers(type.getType(), true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public void removeAll(IResource resource) {
		for (MarkerType type : MarkerType.values()) {
			getInstance().removeProblemMarker(resource, type);
		}
	}
	
	public boolean contains(IResource resource, MarkerType type, String message) throws CoreException {
		boolean ret = false;
		for (IMarker marker : resource.findMarkers(type.getType(), true, IResource.DEPTH_INFINITE)) {
			try {
				if (marker.getResource().getFullPath().toString().equals(resource.getFullPath().toString())
						&& String.valueOf(marker.getAttribute(IMarker.MESSAGE)).equals(message)) {
					ret = true;
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		} 
		return ret;
	}
}
