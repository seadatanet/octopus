package fr.ifremer.octopus.controller;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.ifremer.octopus.model.Format;
import fr.ifremer.octopus.model.OctopusModel.OUTPUT_TYPE;

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
public class BatchTest {
	private static final Logger logger = LogManager.getLogger(BatchTest.class);
	private static  String pwd ;
	
	@BeforeClass
	public static void before(){
		pwd = new File("##").getAbsolutePath().replace("#", "") + "src/test/resources/";
	}
	
	@Test
	public void goodArgsTest() {
		BatchController b = null ;
		String in="-i "+pwd+"medatlas/diap";
		String out = "-o "+pwd+"out";
		String[] args = new String[]{in, out, "-f medatlas", "-t mono"};
		logArgs(args);
		try{
			b = new BatchController(args, true);
			Assert.assertTrue(b.model.getInputPath().getAbsolutePath().equals(pwd+"medatlas/diap"));
			Assert.assertTrue(b.model.getOutputPath().equals(pwd+"out"));
			Assert.assertTrue(b.model.getOutputFormat().equals(Format.MEDATLAS_SDN));
			Assert.assertTrue(b.model.getOutputType().equals(OUTPUT_TYPE.MONO));
		}catch (Exception e){
			logger.error("JUNIT TEST ERROR");
		
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
		String in="-i "+pwd+"medatlas/diap";
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
		String in="-i "+pwd+"medatlas/diap";
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
		String in="-i "+pwd+"medatlas/diap";
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

	private void logArgs(String[] args){
		String line = "";
		for (String s : args){
			line+= " "+ s;
		}
		logger.info("");
		logger.info(">>> " +line);
	}
}
