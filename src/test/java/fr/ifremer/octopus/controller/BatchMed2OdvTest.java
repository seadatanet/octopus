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
public class BatchMed2OdvTest extends AbstractBatchX2YTest{
	private static final Logger logger = LogManager.getLogger(BatchMed2OdvTest.class);
	protected static  String dir ="medatlas";
	
	/**
	 * 1 med -> n cf
	 * cdi list : empty
	 */
	@Test
	public void med2odvMono_emptyCDI() {
		BatchController b = null ;
		String in="-i "+pwd+"medatlas/diap";
		String out = "-o "+getOutputPath("med2odvMono_emptyCDI");
		String[] args = new String[]{in, out, "-f odv", "-t mono"};
		logArgs(args, logger);
		try{
			b = new BatchController(args, true);
		}catch (Exception e){
			logger.error("JUNIT TEST ERROR");
		
		}
	}
	/**
	 * 1 med -> 1 cf
	 * cdi list : empty
	 */
	@Test
	public void med2odvMulti_emptyCDI() {
		BatchController b = null ;
		String in="-i "+pwd+"medatlas/diap";
		String out = "-o "+getOutputPath("med2odvMulti_emptyCDI");
		String[] args = new String[]{in, out, "-f odv", "-t multi"};
		logArgs(args, logger);
		try{
			b = new BatchController(args, true);
		}catch (Exception e){
			logger.error("JUNIT TEST ERROR");
		
		}
	}
	/**
	 * 1 med -> n cf
	 * cdi list : 2
	 */
	@Test
	public void med2odvMono_2CDI() {
		BatchController b = null ;
		String in="-i "+pwd+"medatlas/diap";
		String out = "-o "+getOutputPath("med2odvMono_2CDI");
		String[] args = new String[]{in, out, 
				"-f odv",
				"-t mono",
				"-c FI35200110014_00020_H09,FI35200110014_00022_H09"};
		logArgs(args, logger);
		try{
			b = new BatchController(args, true);
		}catch (Exception e){
			logger.error("JUNIT TEST ERROR");
		
		}
	}
	@Test
	public void med2odvMulti_2CDI() {
		BatchController b = null ;
		String in="-i "+pwd+"medatlas/diap";
		String out = "-o "+getOutputPath("med2odvMulti_2CDI");
		String[] args = new String[]{in, out, 
				"-f odv",
				"-t multi",
				"-c FI35200110014_00020_H09,FI35200110014_00022_H09"};
		logArgs(args, logger);
		try{
			b = new BatchController(args, true);
		}catch (Exception e){
			logger.error(e.getMessage());
			logger.error("JUNIT TEST ERROR");
		
		}
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
