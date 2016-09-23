package fr.ifremer.octopus.controller;

import java.io.File;

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
public class Batch_Dir_Med_2_Odv_Test extends AbstractBatch_X_2_Y_Test{
	private static final Logger logger = LogManager.getLogger(Batch_Dir_Med_2_Odv_Test.class);
	protected static  String dir ="medatlas";
	@BeforeClass
	public static void beforeClass(){
		inFormat="medatlas";
		outFormat = "odv";
	}
	/**
	 * 1 med -> n cf
	 * cdi list : empty
	 */
	@Test
	public void dir_profile_med_2_odv_Mono_emptyCDI() {
		in="medatlas"+File.separator+"input"+File.separator+"profile";
		out = "profile"+File.separator+"dir_profile_med_2_odv_Mono_emptyCDI"+File.separator;
		type= "split";
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
	}
	
	//@Test
	public void testPathWin(){
		String[] args = {
		
		"-i", "C:\\Users\\sbregent\\Documents\\DEV\\workspaces\\octopus\\octopus\\src\\test\\resources\\medatlas\\input\\profile",
		"-o", "C:\\Users\\sbregent\\Documents\\DEV\\IFREMER\\OCTOPUS\\out test\\" ,
		"-f", "odv",
		"-t" ,"split"
		};
		try{
			b = new BatchController(args, true);

		}catch (Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			logger.error("JUNIT TEST ERROR");
		}
		 
	}
	/**
	 * 1 med -> 1 cf
	 * cdi list : empty
	 */
	@Test
	public void dir_profile_med_2_odv_Multi_emptyCDI() {
		in="medatlas/input/profile";
		out = "profile/dir_profile_med_2_odv_Multi_emptyCDI";
		type= "keep";
		
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
	}
	/**
	 * 1 med -> n cf
	 * cdi list : 2
	 */
	@Test
	public void dir_profile_med_2_odv_Mono_2CDI() {
		in="medatlas/input/profile";
		out = "profile/dir_profile_med_2_odv_Mono_2CDI";
		type= "split";
		cdiList = "FI35200110014_00020_H09,FI35200110014_00022_H09,FI29200110014_00020_H09,FI29200110014_00022_H09";
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
	}
	@Test
	public void dir_profile_med_2_odv_Multi_2CDI() {
		in="medatlas/input/profile";
		out = "profile/dir_profile_med_2_odv_Multi_2CDI";
		type= "keep";
		cdiList = "FI35200110014_00020_H09,FI35200110014_00022_H09,FI29200110014_00020_H09,FI29200110014_00022_H09";
		
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
