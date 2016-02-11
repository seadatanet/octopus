package fr.ifremer.octopus.controller.couplingTable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.controller.OctopusException;
import fr.ifremer.octopus.utils.PreferencesManager;
import fr.ifremer.sismer_tools.coupling.CouplingRecord;
import fr.ifremer.sismer_tools.seadatanet.Format;

public class CouplingTableManager {
	private static final Logger LOGGER = LogManager.getLogger(CouplingTableManager.class);

	private Connection c ;
	private PreparedStatement stmtListRecord;
	private PreparedStatement stmtSelectRecord;
	private PreparedStatement stmtUpdateExistingRecord;
	private PreparedStatement stmtSaveNewRecord;


	private static CouplingTableManager instance;

	public static CouplingTableManager getInstance() throws ClassNotFoundException, SQLException{
		if (instance == null){
			instance = new CouplingTableManager();
		}
		return instance;
	}
	private CouplingTableManager() throws SQLException, ClassNotFoundException {
		init();

	}
	public void init()throws SQLException, ClassNotFoundException {
		createDatabaseConnection();

		/**
		 * use delete table if modifications have been made on the table schema
		 * RE-COMMENT BEFORE COMMIT!!!!!
		 * deleteTable();
		 */

		createTable();
		getPreparedStatementList();
		getPreparedStatementSelect();
		getPreparedStatementUpdate();
		getPreparedStatementNew();
	}

	public List<CouplingRecord> list() throws SQLException {
		List<CouplingRecord> list = new ArrayList<CouplingRecord>();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		ResultSet rs = stmtListRecord.executeQuery();
		while (rs.next()) {
			String cdi = rs.getString("LOCAL_CDI_ID");
			int modus= rs.getInt("MODUS");	
			String format = rs.getString("FORMAT");	
			String path = rs.getString("PATH");
			Timestamp date = rs.getTimestamp("DATEUPDATE");
			LOGGER.debug(cdi +" "+ modus+ " "+format + " "+path+ " "+sdf.format(date));
			CouplingRecord cr = new CouplingRecord(cdi, modus, Format.valueOfFromCouplingString(format), path, date);
			list.add(cr);
		}
		rs.close();
		return list;
	}

	public void createTable() throws SQLException{

		ResultSet tables = c.getMetaData().getTables(null, null, "COUPLING", null);
		if (tables.next()) {
			LOGGER.debug("OctopusDB: table COUPLING already exist");
		}
		else {
			LOGGER.debug("OctopusDB: create table COUPLING");
			String create = "CREATE TABLE COUPLING   (LOCAL_CDI_ID VARCHAR(255),MODUS INT ,"
					+"FORMAT VARCHAR(12), PATH VARCHAR(255), DATEUPDATE TIMESTAMP)";
			PreparedStatement stmt = c.prepareStatement(create);
			stmt.execute();
		}


	}
	
	public void cleanCoupling() throws SQLException, ClassNotFoundException{
		deleteTable();
		init();		
	}
	public void deleteTable() throws SQLException{
		String create = "DROP TABLE COUPLING";
		PreparedStatement stmt = c.prepareStatement(create);
		stmt.execute();
	}
	public Connection createDatabaseConnection()
			throws SQLException, ClassNotFoundException {
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		Class.forName(driver);
		String url = "jdbc:derby:octopusDB;create=true";
		try{
			c = DriverManager.getConnection(url);
		}catch(SQLException e){
			LOGGER.error("unable to connect to database");
			throw e;
		}
		return c;
	}

	private void getPreparedStatementList() throws SQLException {
		stmtListRecord = c.prepareStatement(	"SELECT * FROM COUPLING "); 

	}
	private void  getPreparedStatementSelect() throws SQLException {
		stmtSelectRecord = c.prepareStatement(	"SELECT * FROM COUPLING WHERE LOCAL_CDI_ID=? AND FORMAT=?"); 
	}
	private void getPreparedStatementUpdate() throws SQLException {
		stmtUpdateExistingRecord = c.prepareStatement(
				"UPDATE COUPLING " +
						"SET LOCAL_CDI_ID = ?, " +
						" MODUS = ?, "+
						" FORMAT = ? ," +
						" PATH = ? ," +
						" DATEUPDATE = ? " +
				"WHERE LOCAL_CDI_ID = ? AND MODUS=? AND FORMAT=?");

	}

	private void getPreparedStatementNew() throws SQLException {
		stmtSaveNewRecord = c.prepareStatement(
				"INSERT INTO COUPLING " +
						"   (LOCAL_CDI_ID, MODUS, FORMAT, PATH , DATEUPDATE)" +
						"VALUES (?, ?, ?,?, ?)",
						Statement.RETURN_GENERATED_KEYS);
	}


	public void saveRecord(CouplingRecord record, String couplingPrefix) throws OctopusException {
		try {
			stmtSaveNewRecord.clearParameters();
			stmtSaveNewRecord.setString(1, record.getLocal_cdi_id());
			stmtSaveNewRecord.setInt(2, record.getModus());
			stmtSaveNewRecord.setString(3, record.getFormat().toCouplingFormat());



			stmtSaveNewRecord.setString(4, record.getPath());
			stmtSaveNewRecord.setTimestamp(5, record.getDate());

			stmtSaveNewRecord.executeUpdate();

		} catch(SQLException e) {
			LOGGER.error("error creating new coupling table entry");
			LOGGER.error("local_cdi_id "+ record.getLocal_cdi_id());
			LOGGER.error("modus "+ record.getModus());
			throw new OctopusException(e.getMessage());
		}
	} 
	private void updateRecord(CouplingRecord record, String couplingPrefix) throws OctopusException {
		try {
			stmtUpdateExistingRecord.clearParameters();
			stmtUpdateExistingRecord.setString(1, record.getLocal_cdi_id());
			stmtUpdateExistingRecord.setInt(2, record.getModus());
			stmtUpdateExistingRecord.setString(3, record.getFormat().toCouplingFormat());
			stmtUpdateExistingRecord.setString(4, record.getPath());
			stmtUpdateExistingRecord.setTimestamp(5, record.getDate());

			stmtUpdateExistingRecord.setString(6, record.getLocal_cdi_id());
			stmtUpdateExistingRecord.setInt(7, record.getModus());
			stmtUpdateExistingRecord.setString(8, record.getFormat().toCouplingFormat());

			stmtUpdateExistingRecord.executeUpdate();
		} catch (SQLException e) {
			LOGGER.error("error updating coupling table entry");
			LOGGER.error("local_cdi_id "+ record.getLocal_cdi_id());
			LOGGER.error("modus "+ record.getModus());
			throw new OctopusException(e.getMessage());
		}
	}



	public void add(List<CouplingRecord> records) throws OctopusException {
		if (PreferencesManager.getInstance().isCouplingEnabled()){
			// first, get the current coupling prefix (can be changed in settings)
			String couplingPrefix = PreferencesManager.getInstance().getCouplingPrefix();
			Path prefix = Paths.get(couplingPrefix);
			Path absolute, relative;

			for (CouplingRecord cr : records){
				absolute = Paths.get(cr.getPath());

				relative = prefix.relativize(absolute);
				cr.setPath(relative.toString());


				if (isAlreadyInTable(cr)){
					updateRecord(cr, couplingPrefix);
				}else{
					saveRecord(cr, couplingPrefix);
				}
			}
		}
	}

	public boolean checkPrefixCompliance(String outputAbsolutePath)
	{
		// first, get the current coupling prefix (can be changed in settings)
		if (PreferencesManager.getInstance().isCouplingEnabled()){
			String couplingPrefix = PreferencesManager.getInstance().getCouplingPrefix();
			Path prefix = Paths.get(couplingPrefix);
			Path absolute = Paths.get(outputAbsolutePath);
			if (!absolute.startsWith(prefix)){
				return false;
			}
		}
		return true;
	}
	private boolean isAlreadyInTable(CouplingRecord cr) throws OctopusException {

		try {
			stmtSelectRecord.setString(1, cr.getLocal_cdi_id());
			stmtSelectRecord.setString(2, cr.getFormat().toCouplingFormat());
			ResultSet results;
			results = stmtSelectRecord.executeQuery();
			if (results.next()) {
				return true;
			}
			return false;
		} catch (SQLException e) {
			LOGGER.error("error reading coupling table");
			throw new OctopusException(e.getMessage());
		}

	}
	/**
	 * export HSQLDB coupling table in a csv file
	 * @param couplingPath absolute path
	 * @throws OctopusException
	 */
	public void export(String couplingPath) throws OctopusException{
		String COUPLING_SEP = ";";
		FileWriter _writer = null;

		try {
			_writer = new FileWriter(couplingPath, false);
			_writer.append("LOCAL_CDI_ID"+COUPLING_SEP+"MODUS"+COUPLING_SEP+"FORMAT"+COUPLING_SEP+"FILENAME"+System.getProperty("line.separator"));
			for (CouplingRecord cr : list()){
				_writer.append(cr.getLocal_cdi_id()+COUPLING_SEP+cr.getModus()+COUPLING_SEP+cr.getFormat().toCouplingFormat()+COUPLING_SEP+cr.getPath()+System.getProperty("line.separator"));
			}

		} catch (IOException e1) {
			LOGGER.error(e1.getMessage());
			throw new OctopusException("error exporting coupling table");
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
			throw new OctopusException("error exporting coupling table");
		}finally{

			try {
				_writer.close();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
				throw new OctopusException("error exporting coupling table");
			}
		}
	}

	public static void main(String[] args) {
		try {
			CouplingTableManager.getInstance().export("coupling.txt");

		} catch ( OctopusException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

	}

}
