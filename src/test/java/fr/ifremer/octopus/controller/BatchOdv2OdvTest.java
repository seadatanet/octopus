package fr.ifremer.octopus.controller;

import java.io.File;
import java.io.IOException;

import opendap.util.Getopts;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.ifremer.octopus.controller.BatchController;


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
public class BatchOdv2OdvTest extends AbstractBatchX2YTest {
	private static final Logger logger = LogManager.getLogger(BatchOdv2OdvTest.class);
	protected static  String dir ="odv";
	
	
	/**
	 * 1 med -> n cf
	 * cdi list : empty
	 */
	@Test
	public void odv2odvMono_emptyCDI() {
		boolean success = false;
		BatchController b = null ;
		String in="-i "+pwd+"odv/diap.txt";
		String out = "-o "+getOutputPath("odv2odvMono_emptyCDI");
		String[] args = new String[]{in, out, "-f odv", "-t mono"};
		logArgs(args, logger);
		try{
			b = new BatchController(args, true);
			success = new File(getOutputPath("odv2odvMono_emptyCDI")).exists();
		}catch (Exception e){
			logger.error("JUNIT TEST ERROR");
		
		}
		org.junit.Assert.assertTrue(success);
	}
	/**
	 * 1 med -> 1 cf
	 * cdi list : empty
	 */
	@Test
	public void odv2odvMulti_emptyCDI() {
		boolean success = false;
		BatchController b = null ;
		String in="-i "+pwd+"odv/diap.txt";
		String out = "-o "+getOutputPath("odv2odvMulti_emptyCDI.txt");
		String[] args = new String[]{in, out, "-f odv", "-t multi"};
		logArgs(args, logger);
		try{
			b = new BatchController(args, true);
			success = new File(getOutputPath("odv2odvMulti_emptyCDI.txt")).exists();
		}catch (Exception e){
			logger.error("JUNIT TEST ERROR");
		
		}
		org.junit.Assert.assertTrue(success);
	}
	/**
	 * 1 med -> n cf
	 * cdi list : 2
	 */
	@Test
	public void odv2odvMono_2CDI() {
		boolean success = false;
		BatchController b = null ;
		String in="-i "+pwd+"odv/diap.txt";
		String out = "-o "+getOutputPath("odv2odvMono_2CDI");
		String[] args = new String[]{in, out, 
				"-f odv",
				"-t mono",
				"-c FI35200110014_00020_H09,FI35200110014_00022_H09"};
		logArgs(args, logger);
		try{
			b = new BatchController(args, true);
			success = new File(getOutputPath("odv2odvMono_2CDI")).exists();
		}catch (Exception e){
			logger.error("JUNIT TEST ERROR");
		
		}
		org.junit.Assert.assertTrue(success);
	}
	@Test
	public void odv2odvMulti_2CDI() {
		boolean success = false;
		BatchController b = null ;
		String in="-i "+pwd+"odv/diap.txt";
		String out = "-o "+getOutputPath("odv2odvMulti_2CDI.txt");
		String[] args = new String[]{in, out, 
				"-f odv",
				"-t multi",
				"-c FI35200110014_00020_H09,FI35200110014_00022_H09"};
		logArgs(args, logger);
		try{
			b = new BatchController(args, true);
			success = new File(getOutputPath("odv2odvMulti_2CDI.txt")).exists();
		}catch (Exception e){
			logger.error(e.getMessage());
			logger.error("JUNIT TEST ERROR");
		
		}
		org.junit.Assert.assertTrue(success);
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
