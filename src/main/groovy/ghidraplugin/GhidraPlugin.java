package ghidraplugin;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import groovy.lang.GroovyObject;
import org.gradle.api.file.FileCollection;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.plugins.ide.api.XmlFileContentMerger;
import org.gradle.plugins.ide.eclipse.model.Classpath;
import org.gradle.plugins.ide.eclipse.model.ClasspathEntry;
import org.gradle.plugins.ide.eclipse.model.FileReference;
import org.gradle.plugins.ide.eclipse.model.Library;

public class GhidraPlugin implements Plugin<Project> {

	@SuppressWarnings({"unchecked", "rawtypes"})
	public void apply(Project project) {
		try {
			ClassLoader cl = getClass().getClassLoader();
			Method m = cl.getClass().getDeclaredMethod("addURL", URL.class);
			m.invoke(cl, PluginUtils.getUtilityURL());
			run(project);
			project.getTasks().register("vscode", (Class) cl.loadClass("ghidraplugin.tasks.VscodeSetupTask"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void run(Project project) {
		configureDependencies(project);
		GroovyObject ext = (GroovyObject) project.property("ext");
		ext.setProperty(PluginUtils.PROPERTY_NAME, System.getenv(PluginUtils.PROPERTY_NAME));
		project.apply(Map.of("plugin", "java"));
		project.apply(Map.of("plugin", "eclipse"));
		project.apply(Map.of("from", PluginUtils.getExtensionScriptPath()));
		GroovyObject obj = (GroovyObject) project.getExtensions().getByName("eclipse");
		obj = (GroovyObject) obj.getProperty("classpath");
		XmlFileContentMerger merger = (XmlFileContentMerger) obj.getProperty("file");
		merger.whenMerged((Classpath cp) -> updateEntries(cp));
	}

	private void updateEntries(Classpath cp) {
		File fp = new File(PluginUtils.getInstallDir(), "docs/GhidraAPI_javadoc.zip");
		FileReference javaDocs = cp.fileReference(fp);
		for (ClasspathEntry entry : cp.getEntries()) {
			if (entry instanceof Library) {
				Library lib = (Library) entry;
				lib.setJavadocPath(javaDocs);
				File src = new File(lib.getPath().replace(".jar", "-src.zip"));
				if (src.exists()) {
					lib.setSourcePath(cp.fileReference(src));
				}
			}
		}
	}

	private void configureDependencies(Project project) {
		FileCollection fc = project.files(PluginUtils.getClasspathList().toArray());
		project.getDependencies().add("implementation", fc);
		project.getDependencies().add("testImplementation", fc);
	}
}
