package fr.ifremer.octopus.controller;

import java.io.File;

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
public class BatchOdv2CfTest extends AbstractBatchX2YTest {
	private static final Logger logger = LogManager.getLogger(BatchOdv2CfTest.class);
	protected static  String dir ="odv";
	/**
	 * 1 med -> n cf
	 * cdi list : empty
	 */
	@Test
	public void odv2cfMono_emptyCDI() {
		boolean success = false;
		BatchController b = null ;
		String in="-i "+pwd+"odv/diap.txt";
		String out = "-o "+getOutputPath("odv2cfMono_emptyCDI");
		String[] args = new String[]{in, out, "-f cfpoint", "-t mono"};
		logArgs(args, logger);
		try{
			b = new BatchController(args, true);
			success = new File(getOutputPath("odv2cfMono_emptyCDI")).exists();
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
	public void odv2cfMulti_emptyCDI() {
		boolean success = false;
		BatchController b = null ;
		String in="-i "+pwd+"odv/diap.txt";
		String out = "-o "+getOutputPath("odv2cfMulti_emptyCDI.cf");
		String[] args = new String[]{in, out, "-f cfpoint", "-t multi"};
		logArgs(args, logger);
		try{
			b = new BatchController(args, true);
			success = new File(getOutputPath("odv2cfMulti_emptyCDI.cf")).exists();
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
	public void odv2cfMono_2CDI() {
		boolean success = false;
		BatchController b = null ;
		String in="-i "+pwd+"odv/diap.txt";
		String out = "-o "+getOutputPath("odv2cfMono_2CDI");
		String[] args = new String[]{in, out, 
				"-f cfpoint",
				"-t mono",
				"-c FI35200110014_00020_H09,FI35200110014_00022_H09"};
		logArgs(args, logger);
		try{
			b = new BatchController(args, true);
			success = new File(getOutputPath("odv2cfMono_2CDI")).exists();
		}catch (Exception e){
			logger.error("JUNIT TEST ERROR");
		
		}
		org.junit.Assert.assertTrue(success);
	}
	@Test
	public void odv2cfMulti_2CDI() {
		boolean success = false;
		BatchController b = null ;
		String in="-i "+pwd+"odv/diap.txt";
		String out = "-o "+getOutputPath("odv2cfMulti_2CDI.cf");
		String[] args = new String[]{in, out, 
				"-f cfpoint",
				"-t multi",
				"-c FI35200110014_00020_H09,FI35200110014_00022_H09"};
		logArgs(args, logger);
		try{
			b = new BatchController(args, true);
			success = new File(getOutputPath("odv2cfMulti_2CDI.cf")).exists();
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
		return "cfpoint";
	}
}
