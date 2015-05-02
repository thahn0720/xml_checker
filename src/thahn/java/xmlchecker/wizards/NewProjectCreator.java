package thahn.java.xmlchecker.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import thahn.java.xmlchecker.nature.DescriptorNature;

/**
 * 
 * @author th0720.ahn
 *
 */
public class NewProjectCreator  {

	public final static String 											WS_SEP 				= "/";
	public final static String 											WS_ROOT 			= WS_SEP;
	public final static String 											SRC_DIRECTORY		= "src";
	private static final String 										BIN_CLASSES_DIRECTORY =
														    				"bin/" + WS_SEP + "classes" + WS_SEP;
	
	private static final String[] 										DEFAULT_DIRECTORIES = new String[] { };
	
	private static final String 										PARAM_PROJECT 		= "PROJECT_NAME";                     
	private static final String 										PARAM_SRC_FOLDER 	= "SRC_FOLDER";
	
	private Wizard														mWizard;
	private String 														mProjectName;
	private String 														mProjectLocation;
	
	public NewProjectCreator(Wizard wizard, String projectName, String projectLocation) {
		mWizard = wizard;
		mProjectName = projectName;
		mProjectLocation = projectLocation;
	}

	/**
     * Creates the android project.
     * @return True if the project could be created.
     */
    public boolean createCmsDescriptorProjects() {
        final ProjectInfo mainData = collectMainPageInfo();

        try {
			mWizard.getContainer().run(true, false, new IRunnableWithProgress() {
				
			    @Override
			    public void run(IProgressMonitor monitor) throws InvocationTargetException,
			    InterruptedException {
			    	createProjectAsync(monitor, mainData, null, true);
			    }
			});
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
        
        return true;
    }
    
    /**
     * Collects all the parameters needed to create the main project.
     * @return A new {@link ProjectInfo} on success. Returns null if the project cannot be
     *    created because parameters are incorrect or should not be created because there
     *    is no main page.
     */
    private ProjectInfo collectMainPageInfo() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IProject project = workspace.getRoot().getProject(mProjectName);
        final IProjectDescription description = workspace.newProjectDescription(project.getName());

        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(PARAM_PROJECT, mProjectName);
        parameters.put(PARAM_SRC_FOLDER, SRC_DIRECTORY);
        
        // create a dictionary of string that will contain name+content.
        // we'll put all the strings into values/strings.xml
        IPath path = new Path(mProjectLocation);
        description.setLocation(path);

        return new ProjectInfo(project, description, parameters);
    }
    
    /**
     * Creates the actual project(s). This is run asynchronously in a different thread.
     *
     * @param monitor An existing monitor.
     * @param mainData Data for main project. Can be null.
     * @param isAndroidProject true if the project is to be set up as a full Android project; false
     * for a plain Java project.
     * @throws InvocationTargetException to wrap any unmanaged exception and
     *         return it to the calling thread. The method can fail if it fails
     *         to create or modify the project or if it is canceled by the user.
     */
    private void createProjectAsync(IProgressMonitor monitor, ProjectInfo mainData, List<ProjectInfo> importData,
            boolean isAndroidProject) throws InvocationTargetException {
        monitor.beginTask("Create CMS Descriptor Project", 100);
        try {
            IProject mainProject = null;

            if (mainData != null) {
                mainProject = createEclipseProject(
                        new SubProgressMonitor(monitor, 50),
                        mainData.getProject(),
                        mainData.getDescription(),
                        mainData.getParameters(),
                        null,
                        isAndroidProject);
                if (mainProject != null) {
                    final IJavaProject javaProject = JavaCore.create(mainProject);
                }
            }
        } catch (CoreException e) {
            throw new InvocationTargetException(e);
        } catch (IOException e) {
            throw new InvocationTargetException(e);
        } finally {
            monitor.done();
        }
    }
    
    /**
     * Creates the actual project, sets its nature and adds the required folders
     * and files to it. This is run asynchronously in a different thread.
     *
     * @param monitor An existing monitor.
     * @param project The project to create.
     * @param description A description of the project.
     * @param parameters Template parameters.
     * @param dictionary String definition.
     * @param isCmsProject true if the project is to be set up as a full Android project; false
     * for a plain Java project.
     * @return The project newly created
     * @throws StreamException
     */
    private IProject createEclipseProject(IProgressMonitor monitor, IProject project, IProjectDescription description,
            Map<String, Object> parameters, ProjectPopulator projectPopulator, boolean isCmsProject) 
            		throws CoreException, IOException {

        // Create project and open it
        project.create(description, new SubProgressMonitor(monitor, 10));
        if (monitor.isCanceled()) throw new OperationCanceledException();

        project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 10));

        // Add the Java and CMS nature to the project
        DescriptorNature.setupProjectNatures(project, monitor, isCmsProject);

        // Create folders in the project if they don't already exist
        addDefaultDirectories(project, WS_ROOT, DEFAULT_DIRECTORIES, monitor);
        String[] sourceFolders;
        if (isCmsProject) {
            sourceFolders = new String[] {
                    (String) parameters.get(PARAM_SRC_FOLDER),
                };
        } else {
            sourceFolders = new String[] {
                    (String) parameters.get(PARAM_SRC_FOLDER)
                };
        }
        addDefaultDirectories(project, WS_ROOT, sourceFolders, monitor);

        if (projectPopulator != null) {
            try {
                projectPopulator.populate(project);
            } catch (InvocationTargetException ite) {
                ite.printStackTrace();
            }
        }

        // Setup class path: mark folders as source folders
        IJavaProject javaProject = JavaCore.create(project);
        setupSourceFolders(javaProject, sourceFolders, monitor);
        // Set output location
//        javaProject.setOutputLocation(project.getFolder(BIN_CLASSES_DIRECTORY).getFullPath(), monitor);
        // Create the reference to the target project

        return project;
    }

    /**
     * Adds default directories to the project.
     *
     * @param project The Java Project to update.
     * @param parentFolder The path of the parent folder. Must end with a
     *        separator.
     * @param folders Folders to be added.
     * @param monitor An existing monitor.
     * @throws CoreException if the method fails to create the directories in
     *         the project.
     */
    private void addDefaultDirectories(IProject project, String parentFolder, String[] folders
    		, IProgressMonitor monitor) throws CoreException {
        for (String name : folders) {
            if (name.length() > 0) {
                IFolder folder = project.getFolder(parentFolder + name);
                if (!folder.exists()) {
                    folder.create(true /* force */, true /* local */,
                            new SubProgressMonitor(monitor, 10));
                }
            }
        }
    }
    
    /**
     * Adds the given folder to the project's class path.
     *
     * @param javaProject The Java Project to update.
     * @param sourceFolders Template Parameters.
     * @param monitor An existing monitor.
     * @throws JavaModelException if the classpath could not be set.
     */
    private void setupSourceFolders(IJavaProject javaProject, String[] sourceFolders,
            IProgressMonitor monitor) throws JavaModelException {
        IProject project = javaProject.getProject();

        // get the list of entries.
        IClasspathEntry[] entries = javaProject.getRawClasspath();

        // remove the project as a source folder (This is the default)
        entries = removeSourceClasspath(entries, project);

        // add the source folders.
        for (String sourceFolder : sourceFolders) {
            IFolder srcFolder = project.getFolder(sourceFolder);

            // remove it first in case.
            entries = removeSourceClasspath(entries, srcFolder);
            entries = addEntryToClasspath(entries, JavaCore.newSourceEntry(srcFolder.getFullPath()));
        }

        javaProject.setRawClasspath(entries, new SubProgressMonitor(monitor, 10));
    }
    
    /**
     * Removes the corresponding source folder from the class path entries if
     * found.
     *
     * @param entries The class path entries to read. A copy will be returned.
     * @param folder The parent source folder to remove.
     * @return A new class path entries array.
     */
    private IClasspathEntry[] removeSourceClasspath(IClasspathEntry[] entries, IContainer folder) {
        if (folder == null) {
            return entries;
        }
        IClasspathEntry source = JavaCore.newSourceEntry(folder.getFullPath());
        int n = entries.length;
        for (int i = n - 1; i >= 0; i--) {
            if (entries[i].equals(source)) {
                IClasspathEntry[] newEntries = new IClasspathEntry[n - 1];
                if (i > 0) System.arraycopy(entries, 0, newEntries, 0, i);
                if (i < n - 1) System.arraycopy(entries, i + 1, newEntries, i, n - i - 1);
                n--;
                entries = newEntries;
            }
        }
        return entries;
    }
    
    /**
     * Adds the given ClasspathEntry object to the class path entries.
     * This method does not check whether the entry is already defined in the project.
     *
     * @param entries The class path entries to read. A copy will be returned.
     * @param newEntry The new class path entry to add.
     * @return A new class path entries array.
     */
    public IClasspathEntry[] addEntryToClasspath(IClasspathEntry[] entries, IClasspathEntry newEntry) {
        int n = entries.length;
        IClasspathEntry[] newEntries = new IClasspathEntry[n + 1];
        System.arraycopy(entries, 0, newEntries, 0, n);
        newEntries[n] = newEntry;
        return newEntries;
    }
    
    /**
     * Structure that describes all the information needed to create a project.
     * This is collected from the pages by {@link NewProjectCreator#createCmsDescriptorProjects()}
     * and then used by
     * {@link NewProjectCreator#createProjectAsync(IProgressMonitor, ProjectInfo, ProjectInfo)}.
     */
    private static class ProjectInfo {
        private final IProject mProject;
        private final IProjectDescription mDescription;
        private final Map<String, Object> mParameters;

        public ProjectInfo(IProject project, IProjectDescription description, Map<String, Object> parameters) {
            mProject = project;
            mDescription = description;
            mParameters = parameters;
        }

        public IProject getProject() {
            return mProject;
        }

        public IProjectDescription getDescription() {
            return mDescription;
        }

        public Map<String, Object> getParameters() {
            return mParameters;
        }
    }
    
    /** Handler which can write contents into a project */
    public interface ProjectPopulator {
        /**
         * Add contents into the given project
         *
         * @param project the project to write into
         * @throws InvocationTargetException if anything goes wrong
         */
        public void populate(IProject project) throws InvocationTargetException;
    }
}
