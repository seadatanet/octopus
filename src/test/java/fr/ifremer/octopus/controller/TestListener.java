package fr.ifremer.octopus.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	public static String RESU_SEP=";";
	private static int nbTest=0;
	private static int nbTestIgnore=0;
	private static int nbTestFail=0;
	private static List<String> failedTests;




	/**
	 * Main method: launch all listed jUnit classes
	 * @param args
	 */
	public static void main(String[] args) {
		failedTests= new ArrayList<String>();
		System.out.println("START TESTS");
		initResultWriter();

		JUnitCore core= new JUnitCore();
		core.addListener(new TestListener());



		core.run(Batch_File_Med_2_Med_Test.class);
		core.run(Batch_File_Med_2_Odv_Test.class);
		core.run(Batch_File_Med_2_Cf_Test.class);
		core.run(Batch_File_Odv_2_Odv_Test.class);
		core.run(Batch_File_Odv_2_Cf_Test.class);
		core.run(Batch_File_Cf_2_Cf_Test.class);


		core.run(Batch_Dir_Med_2_Med_Test.class);
		core.run(Batch_Dir_Med_2_Odv_Test.class);
		core.run(Batch_Dir_Med_2_Cf_Test.class);
		core.run(Batch_Dir_Odv_2_Odv_Test.class);
		core.run(Batch_Dir_Odv_2_Cf_Test.class);
		core.run(Batch_Dir_Cf_2_Cf_Test.class);


		closeResultWriter();
		System.out.println("TESTS ENDED");
		logger.info("****************************************");
		logger.info(nbTestFail + " fail / " +nbTest);
		logger.info("nb tests ignored "+ +nbTestIgnore);
		if (nbTestFail>0){
			logger.info("tests failed: ");
			for (String t: failedTests){
				logger.info("\t"+t);
			}
		}
		logger.info("****************************************");



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
		failedTests.add(failure.getDescription().getClassName() +" - "+ failure.getDescription().getMethodName());
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
		logger.info("****************************************");
		logger.info("runtime "+ result.getRunTime());
//		logger.info(nbTestFail + " fail / " +nbTest);
//		logger.info("nb tests ignored "+ +nbTestIgnore);
//		if (nbTestFail>0){
//			logger.info("tests failed: ");
//			for (String t: failedTests){
//				logger.info("\t"+t);
//			}
//		}
		logger.info("****************************************");

	}



	private static void initResultWriter() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH-mm"); //$NON-NLS-1$
		resFileName = "results_" + df.format(new Date()) + ".csv";


		//		File _resFile = new File(resFileName);
		try {
			_writer = new FileWriter(resFileName, false);
			_writer.append("success"+RESU_SEP);
			_writer.append("input type"+RESU_SEP);
			_writer.append("type mono/multi"+RESU_SEP);
			_writer.append("input Format"+RESU_SEP);
			_writer.append("output Format"+RESU_SEP);
			_writer.append("cdiList"+RESU_SEP);


			_writer.append("input"+RESU_SEP);
			_writer.append("input sub"+RESU_SEP);

			_writer.append("output type"+RESU_SEP);
			_writer.append("output"+RESU_SEP);
			_writer.append("output sub"+RESU_SEP);

			_writer.append('\n');

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	private static void closeResultWriter() {
		try {
			_writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
