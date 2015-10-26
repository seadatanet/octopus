package fr.ifremer.octopus.controller;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.ifremer.octopus.model.OctopusModel.OUTPUT_TYPE;
import fr.ifremer.sismer_tools.seadatanet.Format;

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
public class BatchArgumentsTest {
	private static final Logger logger = LogManager.getLogger(BatchArgumentsTest.class);
	private static  String pwd ;
	
	@BeforeClass
	public static void before(){
		pwd = new File("##").getAbsolutePath().replace("#", "") + "src/test/resources/";
	}
	
	@Test
	public void goodArgsTest() {
		BatchController b = null ;
		String in="-i "+pwd+"medatlas/input/profile/diap";
		String out = "-o "+pwd+"outArgs";
		String[] args = new String[]{in, out, "-f medatlas", "-t mono"};
		logArgs(args);
		try{
			b = new BatchController(args, true);
			Assert.assertTrue(b.model.getInputPath().equals(pwd+"medatlas/input/profile/diap"));
			Assert.assertTrue(b.model.getOutputPath().equals(pwd+"outArgs"));
			Assert.assertTrue(b.model.getOutputFormat().equals(Format.MEDATLAS_SDN));
			Assert.assertTrue(b.model.getOutputType().equals(OUTPUT_TYPE.MONO));
		}catch (Exception e){
			logger.error("JUNIT TEST ERROR");
		
		}
		try {
			FileUtils.deleteDirectory(new File(pwd+"outArgs"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Test
	public void EmptyInputTest() {
		BatchController b = null ;
		String in="-i ";
		String out = "-o "+pwd+"out";
		String[] args = new String[]{in, out, "-f medatlas", "-t mono"};
		logArgs(args);
		try{
			b = new BatchController(args, true);
		}catch (Exception e){
			logger.info(e.getMessage());
			Assert.assertTrue(e.getMessage().endsWith("input path is empty"));
		}
	}
	@Test
	public void emptyOutputTest(){
		BatchController b = null ;
		String in="-i "+pwd+"medatlas/input/profile/diap";
		String out = "-o ";
		String[] args = new String[]{in, out, "-f medatlas", "-t mono"};
		logArgs(args);
		try{
			b = new BatchController(args, true);
		}catch (Exception e){
			logger.info(e.getMessage());
			Assert.assertTrue(e.getMessage().endsWith("output path is empty"));
		}
	}
	@Test
	public void unrecognizedOutputFormatTest(){
		BatchController b = null ;
		String in="-i "+pwd+"medatlas/input/profile/diap";
		String out = "-o "+pwd+"out";
		String[] args = new String[]{in, out, "-f toto", "-t mono"};
		logArgs(args);
		try{
			b = new BatchController(args, true);
		}catch (Exception e){
			logger.info(e.getMessage());
			Assert.assertTrue(e.getMessage().endsWith("unrecognized output format"));
		}
	}
	@Test
	public void unrecognizedOutputTypeTest(){
		BatchController b = null ;
		String in="-i "+pwd+"medatlas/input/profile/diap";
		String out = "-o "+pwd+"out";
		String[] args = new String[]{in, out, "-f medatlas", "-t toto"};
		logArgs(args);
		try{
			b = new BatchController(args, true);
		}catch (Exception e){
			logger.info(e.getMessage());
			Assert.assertTrue(e.getMessage().endsWith("unrecognized output type"));
		}
	}
	@Test
	public void badMedExtension() {
		boolean success=false;
		BatchController b = null ;
		String in="-i "+pwd+"medatlas/input/profile/diap";
		String out = "-o "+pwd+"out.toto";
		String[] args = new String[]{in, out, "-f medatlas", "-t multi","-c FI35200110014_00020_H09,FI35200110014_00022_H09"};
		logArgs(args);
		try{
			b = new BatchController(args, true);
			success=true;
		}catch (Exception e){
			logger.error("JUNIT TEST ERROR");
			logger.error(e.getMessage());
//			success=e.getMessage().contains("output file extension is not valid");
		}
		new File(pwd+"out.toto").delete();
		Assert.assertTrue(success);
	}
	@Test
	public void badOdvExtension() {
		boolean success=false;
		BatchController b = null ;
		String in="-i "+pwd+"medatlas/input/profile/diap";
		String out = "-o "+pwd+"out.toto";
		String[] args = new String[]{in, out, "-f odv", "-t multi"};
		logArgs(args);
		try{
			b = new BatchController(args, true);
		}catch (Exception e){
			logger.error("JUNIT TEST ERROR");
			logger.error(e.getMessage());
			success=e.getMessage().contains("output file extension is not valid");
		}
		new File(pwd+"out.toto").delete();
		Assert.assertTrue(success);
	}
	@Test
	public void badCfpointExtension() {
		boolean success=false;
		BatchController b = null ;
		String in="-i "+pwd+"medatlas/input/profile/diap";
		String out = "-o "+pwd+"out.toto";
		String[] args = new String[]{in, out, "-f cfpoint", "-t multi"};
		logArgs(args);
		try{
			b = new BatchController(args, true);
		}catch (Exception e){
			logger.error("JUNIT TEST ERROR");
			logger.error(e.getMessage());
			success=e.getMessage().contains("output file extension is not valid");
		
		}
		new File(pwd+"out.toto").delete();
		Assert.assertTrue(success);
	}
	private void logArgs(String[] args){
		String line = "";
		for (String s : args){
			line+= " "+ s;
		}
		logger.info("");
		logger.info(">>> " +line);
	}
}
