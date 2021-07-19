package ghidraplugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import ghidra.GhidraApplicationLayout;
import ghidra.GhidraLauncher;

class PluginUtils {

	static final String PROPERTY_NAME = "GHIDRA_INSTALL_DIR";

	private PluginUtils() {
	}

	static File getInstallDir() {
		return new File(System.getenv(PROPERTY_NAME));
	}

	static String getExtensionScriptPath() {
		try {
			return getInstallDir().getCanonicalPath() + "/support/buildExtension.gradle";
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static URL getUtilityURL() {
		try {
			return new File(getInstallDir(), "/Ghidra/Framework/Utility/lib/Utility.jar").toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	static List<String> getClasspathList() {
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
