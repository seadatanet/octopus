package fr.ifremer.octopus.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractBatch_X_2_Y_Test {
	protected static  String pwd ;
	protected static  String outDir="out";


	protected BatchController b = null ;
	protected String type;
	protected static String inFormat;// for resume only
	protected String in;
	protected String out;
	protected static String outFormat;
	protected String cdiList;
	protected boolean success = false;
	protected boolean expectOutputExist = true;


	public AbstractBatch_X_2_Y_Test() {
	}


	@Before
	public void before(){
		pwd = new File("##").getAbsolutePath().replace("#", "") + "src/test/resources/";
//		deleteDir(getInputDir(), getTmpDir());
		cdiList="";
		success = false;
		expectOutputExist= true;
	}

	@After
	public void after(){
//		deleteDir(getInputDir(), getTmpDir());
	}
	@BeforeClass
	public static void beforeClass(){
	}
	@AfterClass
	public static void afterClass(){

	}


	protected abstract  String getTmpDir();
	protected abstract  String getInputDir();


	protected void launchTest(Logger logger) {
		logArgs(getArgs(), logger);
		try{
			b = new BatchController(getArgs(), true);

		}catch (Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			logger.error("JUNIT TEST ERROR");
		}

	}

	protected void checkResult(boolean fileMustExist){
		if (fileMustExist){
			success = new File(getOutputPath(out)).exists();
		}else{
			success = !new File(getOutputPath(out)).exists();
		}
		org.junit.Assert.assertTrue(success);

	}

	protected void resume(String in, String out,  Logger logger) {
		File inFile = new File(pwd+in);
		logger.info("input = " + in);
		String inFiles="";
		if (inFile.isDirectory()){
			for (String f : inFile.list()){
				logger.info("   - "+f);
				inFiles += f+",";
			}
		}
		String outFiles="";
		File outFile = new File(getOutputPath(out));
		logger.info("output = " + getOutputPath(out).substring(pwd.length()));
		if (outFile.isDirectory()){
			for (String f : outFile.list()){
				if (new File(f).isDirectory()){
					for (String ff : new File(f).list()){
						outFiles+=f+File.separator+ff;
					}
				}
				//				else{
				//					logger.info("   - "+f);
				//					outFiles += f+",";
				//				}
			}
		}
		else{
			logger.info("   - "+outFile.getName());
			outFiles += outFile.getName();
		}
		try {
			FileWriter _writer=TestListener.getWriter();
			if (_writer!=null){
				_writer.append(success? "OK"+TestListener.RESU_SEP: "!!!KO!!!"+TestListener.RESU_SEP);
				_writer.append((inFile.isDirectory() ) ? "DIR"+TestListener.RESU_SEP : "FILE"+TestListener.RESU_SEP);
				_writer.append(type+TestListener.RESU_SEP);
				_writer.append(inFormat+TestListener.RESU_SEP);
				_writer.append(outFormat+TestListener.RESU_SEP);
				_writer.append(cdiList.isEmpty()? "empty"+TestListener.RESU_SEP:"filled"+TestListener.RESU_SEP);


				_writer.append( inFile.getAbsolutePath().substring(pwd.length())+TestListener.RESU_SEP);
				_writer.append(inFiles+TestListener.RESU_SEP);


				if (expectOutputExist){
					_writer.append(outFile.isDirectory()  ? "DIR"+TestListener.RESU_SEP : "FILE"+TestListener.RESU_SEP);
					_writer.append(out+TestListener.RESU_SEP);
					_writer.append(outFiles+TestListener.RESU_SEP);
				}else{
					_writer.append("NA"+TestListener.RESU_SEP);
					_writer.append("NA"+TestListener.RESU_SEP);
					_writer.append("NA"+TestListener.RESU_SEP);
				}

				_writer.append('\n');
				_writer.flush();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected static void deleteDir(String inputDir, String tmpDir){
		try {
			File d = new File(pwd+inputDir+File.separatorChar+outDir+File.separatorChar+tmpDir);
			for (File sub: d.listFiles()){
				for (File ff : sub.listFiles()){
					if (ff.isFile()){
						ff.delete();
					}else{
						FileUtils.deleteDirectory(ff);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected String[] getArgs(){
		String[] args;

		if (cdiList.isEmpty()){
			args= new String[]{"-i "+ pwd+in,
					"-o " + getOutputPath(out),
					"-f "+outFormat,
					"-t "+type};
		}else{
			args= new String[]{"-i "+ pwd+in,
					"-o " +getOutputPath(out),
					"-f "+outFormat,
					"-t "+type,
					"-c "+ cdiList};
		}
		return args;
	}
	protected void logArgs(String[] args, Logger logger){
		String line = "";
		for (String s : args){
			line+= " "+ s;
		}
		logger.info("--------------------------------");
		logger.info(">>> " +line);
	}

	protected String getOutputPath(String endDir){
		return pwd+getInputDir()+File.separatorChar+outDir+File.separatorChar+getTmpDir()+File.separatorChar+endDir;
	}
}
