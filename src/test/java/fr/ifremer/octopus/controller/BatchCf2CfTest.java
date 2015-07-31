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
public class BatchCf2CfTest extends AbstractBatchX2YTest{
	private static final Logger logger = LogManager.getLogger(BatchCf2CfTest.class);
	protected static  String dir ="cfpoint";
	/**
	 * split all CDIs in n mono station cf files
	 */
	@Test
	public void cf2cfMono_emptyCDI() {
		BatchController b = null ;
		String in="-i "+pwd+"cfpoint/diap.nc";
		String out = "-o "+getOutputPath("cf2cfMono_emptyCDI");
		String[] args = new String[]{in, out, "-f cfpoint", "-t mono"};
		logArgs(args, logger);
		try{
			b = new BatchController(args, true);
		}catch (Exception e){
			logger.error("JUNIT TEST ERROR");
		
		}
	}
	
	/**
	 * split 2 cdis in 2 mono station cf files
	 */
	@Test
	public void cf2cfMono_2CDI() {
		BatchController b = null ;
		String in="-i "+pwd+"cfpoint/diap.nc";
		String out = "-o "+getOutputPath("cf2cfMono_2CDI");
		String[] args = new String[]{in, out, 
				"-f cfpoint",
				"-t mono",
				"-c FI35200110014_00020_H09,FI35200110014_00022_H09"};
		logArgs(args, logger);
		try{
			b = new BatchController(args, true);
		}catch (Exception e){
			logger.error("JUNIT TEST ERROR");
		
		}
	}
	
	/**
	 * split 2 cdis in 1 multi station cf file
	 */
	@Test
	public void cf2cfMulti_2CDI() {
		BatchController b = null ;
		String in="-i "+pwd+"cfpoint/diap.nc";
		String out = "-o "+getOutputPath("cf2cfMulti_2CDI");
		String[] args = new String[]{in, out, 
				"-f cfpoint",
				"-t multi",
				"-c FI35200110014_00020_H09,FI35200110014_00022_H09"};
		logArgs(args, logger);
		try{
			b = new BatchController(args, true);
		}catch (Exception e){
			logger.error("JUNIT TEST ERROR");
		
		}
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
