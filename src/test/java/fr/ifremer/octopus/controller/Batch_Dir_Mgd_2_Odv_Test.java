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
public class Batch_Dir_Mgd_2_Odv_Test extends AbstractBatch_X_2_Y_Test {
	private static final Logger logger = LogManager.getLogger(Batch_Dir_Mgd_2_Odv_Test.class);
	protected String local_cdi_id;

	/**
	 * 1 mgd81 -> n odv
	 */
	@Test
	public void dir_mgd_2_odv_Multi_emptyCDI() {
		inFormat="MGD81";
		in="MGD81/input";
		out = "81";
		type= "multi";
		outFormat = "odv";
		local_cdi_id=pwd+"MGD81/mapping.txt";
		expectOutputExist=true;

		launchTest(logger);
		checkResult(expectOutputExist);
		resume(in, out, logger);

	}
	protected String[] getArgs(){
		String[] args;


		args= new String[]{"-i "+ pwd+in,
				"-o " +getOutputPath(out),
				"-f "+outFormat,
				"-l "+ local_cdi_id};

		return args;
	}
	@Override
	protected String getInputDir() {
		return "MGD81";
	}
	@Override
	protected String getTmpDir() {
		return "odv";
	}
}