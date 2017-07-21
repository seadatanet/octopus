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
public class Batch_File_Odv_2_Odv_Test extends AbstractBatch_X_2_Y_Test {
	private static final Logger logger = LogManager.getLogger(Batch_File_Odv_2_Odv_Test.class);
	protected static  String dir ="odv";
	
	
	/**
	 * 1 med -> n cf
	 * cdi list : empty
	 */
	@Test
	public void file_odv_2_odv_Mono_emptyCDI() {
		inFormat="odv";
		in="odv/input/profile/diap.txt";
		out = "profile/file_odv_2_odv_Mono_emptyCDI";
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
	public void file_odv_2_odv_Multi_emptyCDI() {
		inFormat="odv";
		in="odv/input/profile/diap.txt";
		out = "profile/file_odv_2_odv_Multi_emptyCDI.txt";
		type= "keep";
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
	public void file_odv_2_odv_Mono_2CDI() {
		inFormat="odv";
		in="odv/input/profile/diap.txt";
		out = "profile/file_odv_2_odv_Mono_2CDI";
		type= "split";
		outFormat = "odv";
		cdiList="FI35200110014_00020_H09,FI35200110014_00022_H09";
		expectOutputExist=true;
		
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
	}
	@Test
	public void file_odv_2_odv_Mono_nonExistingCDI() {
		inFormat="odv";
		in="odv/input/profile/diap.txt";
		out = "profile/file_odv_2_odv_Mono_nonExistingCDI";
		type= "split";
		outFormat = "odv";
		cdiList="toto";
		expectOutputExist=false;
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
	}
	
	
	
	@Test
	public void file_odv_2_odv_Multi_2CDI() {
		inFormat="odv";
		in="odv/input/profile/diap.txt";
		out = "profile/file_odv_2_odv_Multi_2CDI.txt";
		type= "keep";
		outFormat = "odv";
		cdiList="FI35200110014_00020_H09,FI35200110014_00022_H09";
		
		expectOutputExist=true;
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	@Test
	public void file_odv_2_odv_withBOM() {
		/** check an ODV file in UTF-8 with a BOM (byte order mark)
		https://fr.wikipedia.org/wiki/Indicateur_d'ordre_des_octets
		 DO NOT COPY OR RENAME THIS ODV TEST FILE!!!
		 To check if a file has a BOM, use the linux cmd "file":
		sophie@debian:~/Documents/workspaces/SISMER/lunaFx/octopus/src/test/resources/odv/input/profile$ file -b 00563439_ODV_withBOM.txt 
		-> result is: UTF-8 Unicode (with BOM) text
		 **/
		inFormat="odv";
		in="odv/input/profile/00563439_ODV_withBOM.txt";
		out = "profile/00563439_ODV_withBOM.txt";
		type= "keep";
		outFormat = "odv";
		
		expectOutputExist=true;
		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);
		
		
	}
	@Override
	protected String getInputDir() {
		return "odv";
	}
	@Override
	protected String getTmpDir() {
		return "odv";
	}
}
