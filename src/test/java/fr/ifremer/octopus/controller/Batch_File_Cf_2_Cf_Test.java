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
public class Batch_File_Cf_2_Cf_Test extends AbstractBatch_X_2_Y_Test{
	private static final Logger logger = LogManager.getLogger(Batch_File_Cf_2_Cf_Test.class);
	protected static  String dir ="cfpoint";
	/**
	 * split all CDIs in n mono station cf files
	 */
	@Test
	public void file_cf_2_cf_Mono_emptyCDI() {
		inFormat="cfpoint";
		in="cfpoint/input/profile/diap.nc";
		out = "profile/file_cf_2_cf_Mono_emptyCDI";
		type= "mono";
		outFormat = "cfpoint";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
	}
	
	/**
	 * split 2 cdis in 2 mono station cf files
	 */
	@Test
	public void file_cf_2_cf_Mono_2CDI() {
		inFormat="cfpoint";
		in="cfpoint/input/profile/diap.nc";
		out = "profile/file_cf_2_cf_Mono_2CDI";
		type= "mono";
		outFormat = "cfpoint";
		cdiList="FI35200110014_00020_H09,FI35200110014_00022_H09";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	/**
	 * nothing to do
	 */
	@Test
	public void file_cf_2_cf_Multi_emptyCDI() {
		
		inFormat="cfpoint";
		in="cfpoint/input/profile/diap.nc";
		out = "profile/file_cf_2_cf_Multi_emptyCDI.cf";
		type= "multi";
		outFormat = "cfpoint";
		expectOutputExist=false;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
	}
	/**
	 * split 2 cdis in 1 multi station cf file
	 */
	@Test
	public void file_cf_2_cf_Multi_2CDI() {
		
		inFormat="cfpoint";
		in="cfpoint/input/profile/diap.nc";
		out = "profile/file_cf_2_cf_Multi_2CDI.cf";
		type= "multi";
		outFormat = "cfpoint";
		cdiList="FI35200110014_00020_H09,FI35200110014_00022_H09";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
	}

	@Override
	protected String getInputDir() {
		return "cfpoint";
	}
	@Override
	protected String getTmpDir() {
		return "cfpoint";
	}
	
}
