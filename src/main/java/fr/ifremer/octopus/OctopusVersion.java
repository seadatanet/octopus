package fr.ifremer.octopus;

import java.net.URL;
import java.text.MessageFormat;
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
	
	/**
	 * Retrieve the application version (from Maven version).
	 * 
	 * @return the current Octopus version.
	 */
	public static String getVersion() {

		if (version != null) {
			return version;
		}

		ResourceBundle about = ResourceBundle.getBundle("bundles/about", PreferencesManager.getInstance().getLocale());

		version = about.getString("about.version");

		return version;
	}
	
	public static SoftwareState check () {
		SoftwareVersionClient client;
		SoftwareState state = null;
		try
		{                      
			client = new SoftwareVersionClient(new URL("https://www.seadatanet.org/SoftwareVersionSoap/SoftwareVersionWebService?wsdl"));                                

			state = client.getSoftwareState("OCTOPUS", getVersion());
			ResourceBundle messages = ResourceBundle.getBundle("bundles/messages", PreferencesManager.getInstance().getLocale());
			if(state.getState() == STATE.LAST_VERSION){
				LOGGER.info(state.getDescription());
			}else if(state.getState() == STATE.OLD_VERSION || state.getState() == STATE.UNKNOWN_VERSION){
				// lastVersionInstant is guaranteed to be set for these states
				String[] lastDate = state.getLastVersionDay().split("-");
				String dialogMessage1=MessageFormat.format(messages.getString("rootController.OctopusVersionCurrent"), state.getVersion());
				String dialogMessage2=MessageFormat.format(messages.getString("rootController.OctopusVersionUpdateAvailable"), state.getLastVersion(), lastDate[0], lastDate[1], lastDate[2]);
				LOGGER.info(dialogMessage1+ " "+dialogMessage2);
			}else{
				// SOAP_ERROR or UNKNOWN_SOFTWARE : service unreachable or returned no data
				LOGGER.warn(messages.getString("rootController.OctopusVersionUnreachable"));
				return null;
			}


		}
		catch(Exception e)
		{
			ResourceBundle messages = ResourceBundle.getBundle("bundles/messages", PreferencesManager.getInstance().getLocale());
			LOGGER.warn(messages.getString("rootController.OctopusVersionUnreachable"));
			return null;
		}
		return state;
	}
}
