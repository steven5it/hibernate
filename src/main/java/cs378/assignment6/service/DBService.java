package cs378.assignment6.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cs378.assignment6.domain.Meeting;
import cs378.assignment6.domain.Project;

//import com.mysql.jdbc.DatabaseMetaData;

public class DBService {
	protected static Log logger = LogFactory.getLog(DBService.class
			.getName());
	
	String dbURL = "";
	DataSource ds;

	public DBService() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		//name of database
		dbURL = "jdbc:mysql://localhost:3306/openstack_projects";
		ds = setupDataSource(dbURL);
		
	}
	
	public static DataSource setupDataSource(String connectURI) {
        BasicDataSource ds = new BasicDataSource();
//        ds.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        ds.setUsername("user");
        ds.setPassword("userpw");
        ds.setUrl(connectURI);
        return ds;
    }
	
	public void findScrollType() {
		Connection conn = null;		
		try {
			conn = DriverManager.getConnection(dbURL,"user", "userpw");			
			DatabaseMetaData meta = (DatabaseMetaData) conn.getMetaData();			
			if (meta.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY)) {
			      System.out.println("type name=TYPE_FORWARD_ONLY");
			    }
			    if (meta.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)) {
			      System.out.println("type name=TYPE_SCROLL_INSENSITIVE");
			    }
			    if (meta.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)) {
			      System.out.println("type name=TYPE_SCROLL_SENSITIVE");
			    }			    
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void findTransactionIsolationLevel() {
		Connection conn = null;		
		try {
			conn = DriverManager.getConnection(dbURL,"user", "userpw");
			int isolationLevel = conn.getTransactionIsolation();
			if (isolationLevel == conn.TRANSACTION_NONE) {
				System.out.println("Transaction isolation level is NONE");
			}
			if (isolationLevel == conn.TRANSACTION_READ_COMMITTED) {
				System.out.println("Transaction isolation level is READ_COMMITTED");
			}
			if (isolationLevel == conn.TRANSACTION_READ_UNCOMMITTED) {
				System.out.println("Transaction isolation level is READ_UNCOMMITTED");
			}
			if (isolationLevel == conn.TRANSACTION_REPEATABLE_READ) {
				System.out.println("Transaction isolation level is REPEATABLE_READ");
			}
			if (isolationLevel == conn.TRANSACTION_SERIALIZABLE) {
				System.out.println("Transaction isolation level is SERIALIZABLE");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void findConcurrencyType() {
		Connection conn = null;		
		try {
			conn = DriverManager.getConnection(dbURL,"user", "userpw");			
			DatabaseMetaData meta = (DatabaseMetaData) conn.getMetaData();			
			if (meta.supportsResultSetType(ResultSet.CONCUR_READ_ONLY)) {
			      System.out.println("type name=CONCUR_READ_ONLY");
			    }
			else if (meta.supportsResultSetType(ResultSet.CONCUR_UPDATABLE)) {
			      System.out.println("type name=CONCUR_UPDATABLE");
			    }
			else {
				System.out.println("Cannot determine concurrency type.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private Connection getConnection() {
		Connection conn = null;		
		try {
			conn = DriverManager.getConnection(dbURL,"user", "userpw");
			logger.info("Just got the connection");
			
			DatabaseMetaData meta = (DatabaseMetaData) conn.getMetaData();
			
			if (meta.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY)) {
			      System.out.println("type name=TYPE_FORWARD_ONLY");
			    }
			    if (meta.supportsResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE)) {
			      System.out.println("type name=TYPE_SCROLL_INSENSITIVE");
			    }
			    if (meta.supportsResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE)) {
			      System.out.println("type name=TYPE_SCROLL_SENSITIVE");
			    }
			    
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return conn;
	}
	
	public void addProject(String name, String description) {
		logger.info("inserting solum into table");
		Project project = new Project();
		project.setName(name);
		project.setDescription(description);
		String query = "insert ignore into projects(description, name) values(?,?) ";
		Map<String, Object> projectInfo = new HashMap<String, Object>();
		projectInfo.put("name", name);
		projectInfo.put("description", description);
		PreparedStatement ps = prepareQuery(query, projectInfo);
		executeUpdate(ps);
	}
	
	private void executeUpdate(PreparedStatement ps) {
		try {
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private ResultSet executeQuery(PreparedStatement ps) {
		ResultSet results = null;
		try {
			results = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return results;
	}
	
	private PreparedStatement prepareQuery(String query, String value) {
		PreparedStatement statement = null;
		try {
			 Connection conn = ds.getConnection();
			 statement = conn.prepareStatement(query);
			 statement.setString(1, value);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return statement;
	}
	
	private PreparedStatement prepareQuery(String query, Map<String,Object>projectInfo) {
		PreparedStatement statement = null;
		try {
			 Connection conn = ds.getConnection();
			 statement = conn.prepareStatement(query);
			 statement.setString(1, (String)projectInfo.get("description"));			 
			 statement.setString(2, (String)projectInfo.get("name"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return statement;
	}
}