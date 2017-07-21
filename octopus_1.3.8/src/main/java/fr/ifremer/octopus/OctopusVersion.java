package fr.ifremer.octopus;

import java.io.InputStream;
import java.util.Properties;

public class OctopusVersion {

	protected static String version = null;
	
	public static String getVersion() {
		
		   if (version != null) {
		        return version;
		    }
		   
		   // try to load from maven properties first
	    try {
	        Properties p = new Properties();
	        InputStream is = OctopusVersion.class.getResourceAsStream("/META-INF/maven/fr.ifremer/octopus/pom.properties");
	        if (is != null) {
	            p.load(is);
	            version = p.getProperty("version", "");
	        }
	    } catch (Exception e) {
	        // ignore
	    }

	    // fallback to using Java API
	    if (version == null) {
	        Package aPackage = OctopusVersion.class.getPackage();
	        if (aPackage != null) {
	            version = aPackage.getImplementationVersion();
	            if (version == null) {
	                version = aPackage.getSpecificationVersion();
	            }
	        }
	    }

	    if (version == null) {
	        // we could not compute the version so use a blank
	        version = "";
	    }

	    return version;
	}
}
