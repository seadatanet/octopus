package fr.ifremer.octopus.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Launch all junit Octopus tests and write results in results_yyyy_MM_dd-HH-mm.csv file
 * @author altran
 *
 */
public class TestListener  extends RunListener{
	
	private static final Logger logger = LogManager.getLogger(TestListener.class);
	public static String resFileName;
	
	// resume results file
	private static FileWriter _writer;
	private int nbTest=0;
	private int nbTestIgnore=0;
	private int nbTestFail=0;
	
	

	
	/**
	 * Main method: launch all listed jUnit classes
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("START TESTS");
		initResultWriter();
		
		JUnitCore core= new JUnitCore();
		core.addListener(new TestListener());
		
		
		
//		core.run(Batch_File_Med_2_Med_Test.class);
//		core.run(Batch_File_Med_2_Odv_Test.class);
//		core.run(Batch_File_Med_2_Cf_Test.class);
//		core.run(Batch_File_Odv_2_Odv_Test.class);
//		core.run(Batch_File_Odv_2_Cf_Test.class);
//		core.run(Batch_File_Cf_2_Cf_Test.class);
//		
//		
//		core.run(Batch_Dir_Med_2_Med_Test.class);
//		core.run(Batch_Dir_Med_2_Odv_Test.class);
		core.run(Batch_Dir_Med_2_Cf_Test.class);
//		core.run(Batch_Dir_Odv_2_Odv_Test.class);
//		core.run(Batch_Dir_Odv_2_Cf_Test.class);
//		core.run(Batch_Dir_Cf_2_Cf_Test.class);
		
		
		closeResultWriter();
		System.out.println("TESTS ENDED");
		
		
		
	}
	public static FileWriter getWriter(){
		return _writer;
	}
	
	
	@Override
	public void testStarted(Description description) throws Exception {
		super.testStarted(description);
		nbTest+=1;
	}
	@Override
	public void testFailure(Failure failure) throws Exception {
		super.testFailure(failure);
		logger.error("!!!FAIL!!! " + failure.getDescription().getClassName() +" - "+ failure.getDescription().getMethodName());
		nbTestFail+=1;
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		super.testIgnored(description);
		nbTestIgnore+=1;
	}

	@Override
	public void testRunStarted(Description description)  throws java.lang.Exception{
		
	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		super.testRunFinished(result);
		logger.info(result.getRunTime());
		logger.info(nbTestFail + " fail / " +nbTest);
		logger.info("nb tests ignored "+ +nbTestIgnore);
		

	}

	

	private static void initResultWriter() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH-mm"); //$NON-NLS-1$
		resFileName = "results_" + df.format(new Date()) + ".csv";
		
		
//		File _resFile = new File(resFileName);
		try {
			_writer = new FileWriter(resFileName, false);
			_writer.append("success"+"\t");
			_writer.append("expected result"+"\t");
			
			_writer.append("type mono/multi"+"\t");
			_writer.append("input Format"+"\t");
			_writer.append("output Format"+"\t");
			_writer.append("cdiList\t");
			
			_writer.append("input type"+"\t");
			_writer.append("input"+"\t");
			_writer.append("inputFiles"+"\t");
			
			
			
			
			_writer.append("output type"+"\t");
			_writer.append("output"+"\t");
			_writer.append("output files"+"\t");
			
			_writer.append('\n');

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private static void closeResultWriter() {
		try {
			_writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
