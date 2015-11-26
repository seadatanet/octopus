package fr.ifremer.octopus.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class Batch_File_Med_2_Odv_Test extends AbstractBatch_X_2_Y_Test{
	private static final Logger logger = LogManager.getLogger(Batch_File_Med_2_Odv_Test.class);
	protected static  String dir ="medatlas";
	
	/**
	 * 1 med -> n cf
	 * cdi list : empty
	 */
	@Test
	public void file_profile_med_2_odv_Mono_emptyCDI() {
		inFormat="medatlas";
		in="medatlas/input/profile/diap";
		out = "profile/file_profile_med_2_odv_Mono_emptyCDI";
		type= "mono";
		outFormat = "odv";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
	}
	/**
	 * 1 med -> 1 cf
	 * cdi list : empty
	 */
	@Test
	public void file_profile_med_2_odv_Multi_emptyCDI() {
		inFormat="medatlas";
		in="medatlas/input/profile/diap";
		out = "profile/file_profile_med_2_odv_Multi_emptyCDI.txt";
		type= "multi";
		outFormat = "odv";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	/**
	 * 1 med -> n cf
	 * cdi list : 2
	 */
	@Test
	public void file_profile_med_2_odv_Mono_2CDI() {
		inFormat="medatlas";
		in="medatlas/input/profile/diap";
		out = "profile/file_profile_med_2_odv_Mono_2CDI";
		type= "mono";
		outFormat = "odv";
		cdiList="FI35200110014_00020_H09,FI35200110014_00022_H09";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	@Test
	public void file_profile_med_2_odv_Mono_2CDI_withoutRef() {
		inFormat="medatlas";
		in="medatlas/input/profile/diap_inputNoRef";
		out = "profile/file_profile_med_2_odv_Mono_2CDI_inputNoRef";
		type= "mono";
		outFormat = "odv";
		cdiList="FI29200110014_00020_H09,FI29200110014_00022_H09";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	@Test
	public void file_profile_med_2_odv_Multi_2CDI() {
		inFormat="medatlas";
		in="medatlas/input/profile/diap";
		out = "profile/file_profile_med_2_odv_Multi_2CDI.txt";
		type= "multi";
		outFormat = "odv";
		cdiList="FI35200110014_00020_H09,FI35200110014_00022_H09";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	
/**
 * ===================
 */
	
	/**
	 * 1 med -> n cf
	 * cdi list : empty
	 */
	@Test
	public void file_trajectory_med_2_odv_Mono_emptyCDI() {
		inFormat="medatlas";
		in="medatlas/input/trajectory/M_trajectory_bath";
		out = "trajectory/file_trajectory_med_2_odv_Mono_emptyCDI";
		type= "mono";
		outFormat = "odv";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
	}
	/**
	 * 1 med -> 1 cf
	 * cdi list : empty
	 */
	@Test
	public void file_trajectory_med_2_odv_Multi_emptyCDI() {
		inFormat="medatlas";
		in="medatlas/input/trajectory/M_trajectory_bath";
		out = "trajectory/file_trajectory_med_2_odv_Multi_emptyCDI.txt";
		type= "multi";
		outFormat = "odv";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	/**
	 * 1 med -> n cf
	 * cdi list : 2
	 */
	@Test
	public void file_trajectory_med_2_odv_Mono_2CDI() {
		inFormat="medatlas";
		in="medatlas/input/trajectory/M_trajectory_bath";
		out = "trajectory/file_trajectory_med_2_odv_Mono_2CDI";
		type= "mono";
		outFormat = "odv";
		cdiList="FI35199480001_00001_H09,FI35199480002_00001_H09";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	@Test
	public void file_trajectory_med_2_odv_Multi_2CDI() {
		inFormat="medatlas";
		in="medatlas/input/trajectory/M_trajectory_bath";
		out = "trajectory/file_trajectory_med_2_odv_Multi_2CDI.txt";
		type= "multi";
		outFormat = "odv";
		cdiList="FI35199480001_00001_H09,FI35199480002_00001_H09";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	@Test
	public void file_profile_med_2_odv_Multi_2CDI_stefane() {
		inFormat="medatlas";
		in="medatlas/input/profile/2010030170.ctd";
		out = "profile/file_profile_med_2_odv_Multi_2CDI_stefane.txt";
		type= "multi";
		outFormat = "odv";
		cdiList="FI35201003017_00001_H10,FI35201003017_00002_H10";
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
		return "odv";
	}
}
