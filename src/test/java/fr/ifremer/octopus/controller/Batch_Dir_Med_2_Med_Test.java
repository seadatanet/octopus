package fr.ifremer.octopus.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * usage: octopus
 -i <arg>     (mandatory) input path
 -o <arg>     (mandatory) output path
 -f <arg>     (mandatory) output format: <medatlas>, <odv> or <cfpoint>
 -t <arg>     (mandatory) output type: <mono> or <multi>
 -cdi <arg>   (optionnal) list of local_cdi_id, eg <FI35AAB, FI35AAC>, all
              cdi are exported if this argument is ommited


 * @author altran
 *
 */
public class Batch_Dir_Med_2_Med_Test  extends AbstractBatch_X_2_Y_Test {
	private static final Logger logger = LogManager.getLogger(Batch_Dir_Med_2_Med_Test.class);
	protected static  String dir ="medatlas";
	
	@BeforeClass
	public static void beforeClass(){
		inFormat="medatlas";
		outFormat = "medatlas";
	}
	
	/**
	 * 1 med -> n cf
	 * cdi list : empty
	 */
	@Test
	public void dir_profile_med_2_med_Mono_emptyCDI() {
		in="medatlas/input/profile";
		out = "profile/dir_profile_med_2_med_Mono_emptyCDI";
		type= "split";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
	}
	/**
	 * 1 med dir of n files-> nothing to do
	 * cdi list : empty
	 */
	@Test
	public void dir_profile_med_2_med_Multi_emptyCDI() {
		in="medatlas/input/profile";
		out = "profile/dir_profile_med_2_med_Multi_emptyCDI";
		type= "keep";
		expectOutputExist = true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out,  logger);
	}
	/**
	 * 1 med -> n cf
	 * cdi list : 2
	 */
	@Test
	public void dir_profile_med_2_med_Mono_2CDI() {
		in="medatlas/input/profile";
		out = "profile/dir_profile_med_2_med_Mono_2CDI";
		type= "split";
		cdiList = "FI35200110014_00020_H09,FI35200110014_00022_H09,FI29200110014_00020_H09,FI29200110014_00022_H09";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
	}
	
	@Test
	public void dir_profile_med_2_med_Multi_2CDI() {
		in="medatlas/input/profile";
		out = "profile/dir_profile_med_2_med_Multi_2CDI";
		type= "keep";
		cdiList = "FI35200110014_00020_H09,FI35200110014_00022_H09,FI29200110014_00020_H09,FI29200110014_00022_H09";
		expectOutputExist=true;
		
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
	}
	
	@Override
	protected String getInputDir() {
		return "medatlas";
	}
	@Override
	protected String getTmpDir() {
		return "medatlas";
	}
	
}
