package fr.ifremer.octopus.controller;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractBatchX2YTest {

	protected static  String pwd ;
	protected static  String outDir="out";
	
	public AbstractBatchX2YTest() {
		// TODO Auto-generated constructor stub
	}
	
	
	@Before
	public void before(){
		pwd = new File("##").getAbsolutePath().replace("#", "") + "src/test/resources/";
//		deleteDir(getInputDir(), getTmpDir());
		
	}
	
	@After
	public void after(){
//		deleteDir(getInputDir(), getTmpDir());
	}
	
	protected abstract  String getTmpDir();
	protected abstract  String getInputDir();
	

	protected static void deleteDir(String inputDir, String tmpDir){
		try {
			File d = new File(pwd+inputDir+File.separatorChar+outDir+File.separatorChar+tmpDir);
			for (File ff : d.listFiles()){
				if (ff.isFile()){
					ff.delete();
				}else{
					FileUtils.deleteDirectory(ff);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected void logArgs(String[] args, Logger logger){
		String line = "";
		for (String s : args){
			line+= " "+ s;
		}
		logger.info("");
		logger.info(">>> " +line);
	}
	
	protected String getOutputPath(String endDir){
		return pwd+getInputDir()+File.separatorChar+outDir+File.separatorChar+getTmpDir()+File.separatorChar+endDir;
	}
}
