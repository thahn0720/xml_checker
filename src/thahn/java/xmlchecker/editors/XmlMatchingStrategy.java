package thahn.java.xmlchecker.editors;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import thahn.java.xmlchecker.util.MyUtils;

public class XmlMatchingStrategy implements IEditorMatchingStrategy {

    @Override
    public boolean matches(IEditorReference editorRef, IEditorInput input) {
    	boolean ret = false;
//        if (input instanceof FileEditorInput) {
//            FileEditorInput fileInput = (FileEditorInput)input;
//            IFile ifile = fileInput.getFile();
//            File file = ifile.getFullPath().toFile();
//            
//            try (BufferedInputStream bio = new BufferedInputStream(new FileInputStream(file))) {
//            	if (CmsUtils.isCmsDescriptorRootTag(bio)) {
//            		ret = true;
//            	} 
//            } catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//        }

        return ret;
    }
}
