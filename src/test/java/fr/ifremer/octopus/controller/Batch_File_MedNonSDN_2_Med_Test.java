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
public class Batch_File_MedNonSDN_2_Med_Test  extends AbstractBatch_X_2_Y_Test {
	private static final Logger logger = LogManager.getLogger(Batch_File_MedNonSDN_2_Med_Test.class);
	protected static  String dir ="medatlas";
	/**
	 * 1 med -> n cf
	 * cdi list : empty
	 */
	@Test
	public void file_profile_med_2_med_Mono_emptyCDI() {
		inFormat="medatlas";
		in="medatlas/inputNonSDN/profile/medatlasNonSdn.med";
		out = "profile/file_profile_medNonSDN_2_med_Mono_emptyCDI";
		type= "split";
		outFormat = "medatlas";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
	}
	
//	@Test
	public void file_profile_med_2_odv_Mono_emptyCDI() {
		inFormat="medatlas";
		in="medatlas/inputNonSDN/profile/medatlasNonSdn.med";
		out = "profile/file_profile_medNonSDN_2_odv_Mono_emptyCDI";
		type= "split";
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
	public void file_profile_med_2_med_Multi_emptyCDI() {
		inFormat="medatlas";
		in="medatlas/inputNonSDN/profile/medatlasNonSdn.med";
		out = "profile/file_profile_medNonSDN_2_med_Multi_emptyCDI";
		type= "keep";
		outFormat = "medatlas";
		expectOutputExist = true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	/**
	 * 1 med -> 1 cf
	 * cdi list : empty
	 */
	@Test
	public void file_profile_medCoriolis_2_med_Multi_emptyCDI() {
		inFormat="medatlas";
		in="medatlas/inputNonSDN/profile/coriolis_H10_CO_4900778_20101214_180437.txt";
		out = "profile/file_profile_medNonSDNCoriolis_2_med_Multi_emptyCDI";
		type= "keep";
		outFormat = "medatlas";
		expectOutputExist = true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	/**
	 * 1 med -> n cf
	 * cdi list : 2
	 */
	@Test
	public void file_profile_med_2_med_Mono_2CDI() {
		
		inFormat="medatlas";
		in="medatlas/inputNonSDN/profile/medatlasNonSdn.med";
		out = "profile/file_profile_medNonSDN_2_med_Mono_2CDI";
		type= "split";
		outFormat = "medatlas";
		cdiList="FI35199810007_00002_H09,FI35200110014_00022_H09";
		expectOutputExist=false;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	/**
	 * 1 med -> n cf
	 * cdi list : 2
	 */
	@Test
	public void file_profile_med_2_med_Mono_2CDI_inputNoRef() {
		
		inFormat="medatlas";
		in="medatlas/inputNonSDN/profile/medatlasNonSdn.med";
		out = "profile/file_profile_medNonSDN_2_med_Mono_2CDI_inputNoRef";
		type= "split";
		outFormat = "medatlas";
		cdiList="FI29200110014_00025_H09,FI29200110014_00024_H09";
		expectOutputExist=false;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);

		
		
	}
	@Test
	public void file_profile_med_2_med_Multi_2CDI() {
		
		inFormat="medatlas";
		in="medatlas/input/profile/diap";
		out = "profile/file_profile_med_2_med_Multi_2CDI";
		type= "keep";
		outFormat = "medatlas";
		cdiList="FI35200110014_00020_H09,FI35200110014_00022_H09";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	
	
	/**
	 * 1 med -> n cf
	 * cdi list : empty
	 */
	@Test
	public void file_timeseries_med_2_med_Mono_emptyCDI() {
		inFormat="medatlas";
		in="medatlas/input/timeseries/suva";
		out = "timeseries/file_timeseries_med_2_med_Mono_emptyCDI";
		type= "split";
		outFormat = "medatlas";
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
	public void file_timeseries_med_2_med_Multi_emptyCDI() {
		inFormat="medatlas";
		in="medatlas/input/timeseries/suva";
		out = "timeseries/file_timeseries_med_2_med_Multi_emptyCDI";
		type= "keep";
		outFormat = "medatlas";
		expectOutputExist = true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	/**
	 * 1 med -> n cf
	 * cdi list : 2
	 */
	@Test
	public void file_timeseries_med_2_med_Mono_2CDI() {
		
		inFormat="medatlas";
		in="medatlas/input/timeseries/suva";
		out = "timeseries/file_timeseries_med_2_med_Mono_2CDI";
		type= "split";
		outFormat = "medatlas";
		cdiList="FI35199810007_00002_D09,FI35199810007_00001_D09";
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
	public void file_timeseries_med_2_med_Mono_2CDI_inputNoRef() {
		
		inFormat="medatlas";
		in="medatlas/input/timeseries/suva_inputNoRef";
		out = "timeseries/file_timeseries_med_2_med_Mono_2CDI_inputNoRef";
		type= "split";
		outFormat = "medatlas";
		cdiList="FI35199810007_00002_D09,FI35199810007_00001_D09";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);

		
		
	}
	@Test
	public void file_timeseries_med_2_med_Multi_2CDI() {
		
		inFormat="medatlas";
		in="medatlas/input/timeseries/suva";
		out = "timeseries/file_timeseries_med_2_med_Multi_2CDI";
		type= "keep";
		outFormat = "medatlas";
		cdiList="FI35199810007_00002_D09,FI35199810007_00001_D09";
		
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	
	
	/**
	 * ==============================
	 */
	

	
	/**
	 * 1 med -> n cf
	 * cdi list : empty
	 */
	@Test
	public void file_trajectory_med_2_med_Mono_emptyCDI() {
		inFormat="medatlas";
		in="medatlas/input/trajectory/M_trajectory_bath";
		out = "trajectory/file_trajectory_med_2_med_Mono_emptyCDI";
		type= "split";
		outFormat = "medatlas";
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
	public void file_trajectory_med_2_med_Multi_emptyCDI() {
		inFormat="medatlas";
		in="medatlas/input/trajectory/M_trajectory_bath";
		out = "trajectory/file_trajectory_med_2_med_Multi_emptyCDI";
		type= "keep";
		outFormat = "medatlas";
		expectOutputExist = true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	/**
	 * 1 med -> n cf
	 * cdi list : 2
	 */
	@Test
	public void file_trajectory_med_2_med_Mono_2CDI() {
		
		inFormat="medatlas";
		in="medatlas/input/trajectory/M_trajectory_bath";
		out = "trajectory/file_trajectory_med_2_med_Mono_2CDI";
		type= "split";
		outFormat = "medatlas";
		cdiList="FI35199480001_00001_H09,FI35199480002_00001_H09";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	@Test
	public void file_trajectory_med_2_med_Multi_2CDI() {
		
		inFormat="medatlas";
		in="medatlas/input/trajectory/M_trajectory_bath";
		out = "trajectory/file_trajectory_med_2_med_Multi_2CDI";
		type= "keep";
		outFormat = "medatlas";
		cdiList="FI35199480001_00001_H09,FI35199480002_00001_H09";
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
