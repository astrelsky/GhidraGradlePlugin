package ghidraplugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import ghidra.GhidraApplicationLayout;
import ghidra.GhidraLauncher;

public class PluginUtils {

	static final String PROPERTY_NAME = "GHIDRA_INSTALL_DIR";

	private PluginUtils() {
	}

	public static File getInstallDir() {
		return new File(System.getenv(PROPERTY_NAME));
	}

	public static File getGhidraFile(String relativePath) {
		return new File(getInstallDir(), relativePath);
	}

	public static String getExtensionScriptPath() {
		try {
			return getGhidraFile("/support/buildExtension.gradle").getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static URL getUtilityURL() {
		try {
			return getGhidraFile("/Ghidra/Framework/Utility/lib/Utility.jar").toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static List<String> getClasspathList() {
		try {
			GhidraApplicationLayout layout = new GhidraApplicationLayout(getInstallDir());
			Method m = GhidraLauncher.class.getDeclaredMethod("buildClasspath", GhidraApplicationLayout.class);
			m.setAccessible(true);
			return (List<String>) m.invoke(null, layout);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
