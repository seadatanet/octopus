package fr.ifremer.octopus;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.utils.PreferencesManager;
import software.version.SoftwareState;
import software.version.SoftwareState.STATE;
import software.version.SoftwareVersionClient;

public class OctopusVersion {
	static final Logger LOGGER = LogManager.getLogger(OctopusVersion.class.getName());
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
	
	public static SoftwareState check () {
		SoftwareVersionClient client;
		SoftwareState state = null;
		try
		{                      
			client = new SoftwareVersionClient(new URL("http://www.ifremer.fr/SoftwareVersionSoap/SoftwareVersionWebService?wsdl"));                                


			state = client.getSoftwareState("OCTOPUS", getVersion());
			if(state.getState() == STATE.LAST_VERSION){
				LOGGER.info(state.getDescription());
			}else{
				ResourceBundle messages = ResourceBundle.getBundle("bundles/messages", PreferencesManager.getInstance().getLocale());
				String[] lastDate = state.getLastVersionDay().split("-");
				String dialogMessage1=MessageFormat.format(messages.getString("rootController.OctopusVersionCurrent"), state.getVersion()  );
				String dialogMessage2=MessageFormat.format(messages.getString("rootController.OctopusVersionUpdateAvailable"), state.getLastVersion(), lastDate[0], lastDate[1], lastDate[2]);
				
				LOGGER.info(dialogMessage1+ " "+dialogMessage2);
			}


		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		return state;
	}
}
