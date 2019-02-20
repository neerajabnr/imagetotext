package it.sella.f24.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class HsqlDbService {
	
	
	    public static void init() throws SQLException, ClassNotFoundException, IOException {
	        Class.forName("org.hsqldb.jdbc.JDBCDriver");
	 
	        // initialize database
	        initDatabase();
	    }
	     
	 
	
	    public static void destroy() throws SQLException, ClassNotFoundException, IOException {
	        try (Connection connection = getConnection(); 
	        		Statement statement = connection.createStatement();) {
	            statement.executeUpdate("DROP TABLE employee");
	            connection.commit();
	        }
	    }
	 
	    /**
	     * Database initialization for testing i.e.
	     * <ul>
	     * <li>Creating Table</li>
	     * <li>Inserting record</li>
	     * </ul>
	     * 
	     * @throws SQLException
	     */
	    private static void initDatabase() throws SQLException {
	        try (Connection connection = getConnection();){
	            connection.commit();
	        
	            	        }
	    }
	    
	    
	    public void insert() {
	    	
	    }
	    
	 
	    /**
	     * Create a connection
	     * 
	     * @return connection object
	     * @throws SQLException
	     */
	    private static Connection getConnection() throws SQLException {
	        return DriverManager.getConnection("jdbc:hsqldb:mem:newdb", "nij", "nij");
	    }
	    
	    public static void createDB() {
	    
			try {
				Connection connection = getConnection();
		    	 Statement statement;
				statement = connection.createStatement();
	            statement.execute("CREATE TABLE positions ( description VARCHAR(500) NOT NULL,xstart DECIMAL(11,8),ystart DECIMAL(11,8),xend DECIMAL(11,8),yend DECIMAL(11,8),font DECIMAL(11,8) )");

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    public static void insertData(String data, double xstart,double ystart,double xend,double yend,double font) {
	    	try {
				Connection connection = getConnection();
				 Statement statement = connection.createStatement();
				
		    	 statement.executeUpdate("INSERT INTO positions values ("+"'"+data+"'"+","+xstart+","+ystart+","+xend+","+yend+","+font+")");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    }
	 
	    /**
	     * Get total records in table
	     * 
	     * @return total number of records. In case of exception 0 is returned
	     */
	    private static int getTotalRecords() {
	        try (Connection connection = getConnection(); Statement statement = connection.createStatement();) {
	            ResultSet result = statement.executeQuery("SELECT count(*) as total FROM positions");
	            if (result.next()) {
	                return result.getInt("total");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return 0;
	    }
	 
	    
	   public static void main(String args[]) {
		  
		   try {
			init();
			createDB();
			insertData("dec",1.2,1.3,1.5,1.6,1.3);
			System.out.println(getTotalRecords());
		} catch (SQLException | ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
	 

	   public void createTable() {
		   try {
			init();
			createDB();
		} catch (ClassNotFoundException | SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	   }
	   
	   public void select() {
		   try (Connection connection = getConnection(); Statement statement = connection.createStatement();) {
	            ResultSet result = statement.executeQuery("SELECT * FROM positions order by  abs(ystart) desc , abs(xstart) asc   ");
	            while (result.next()) {
	            	System.out.println(result.getString("description")+" | "+
	                        result.getDouble("xstart")+" | "+
	                        result.getDouble("ystart")+" |"+
	                        result.getDouble("xend")+" | "+
	                        result.getDouble("yend")+" |");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
		   
	   }
	    
	   
	   public void selectByLabelColumwise(String str) {
		   try (Connection connection = getConnection(); Statement statement = connection.createStatement();) {
	            ResultSet result = null;
	            result = statement.executeQuery("select xstart,xend,abs(ystart) as ystart from positions where description like '%"+str+"%' order by abs(ystart) asc ");
	          
	            while(result.next()) {
	            	System.out.println();
	            	double xstart = 0;
	           double xend = 0;
	           double ystart =0;
	         
	           
//	            	System.out.println(result.getString("description")+" | "+
//	                        result.getDouble("xstart")+" | "+
//	                        result.getDouble("ystart")+" |"+
//	                        result.getDouble("xend")+" | "+
//	                        result.getDouble("yend")+" |");
	            	xstart = result.getDouble("xstart");
	            	xend = result.getDouble("xend");
	            	ystart = result.getDouble("ystart");
	            	
	            
	            	ResultSet result2 = statement.executeQuery("select * from positions where ((xstart >="+xstart + " and" + " xstart <="+xend+") or (abs(abs(xstart)-"+xstart+")<=3))" +" and abs(abs(ystart)-"+ystart+")"+"<=16");
	             while (result2.next()) {
		            	System.out.print(result2.getString("description")+" "
		            			);
		            }
	             
	            // System.out.println();
	            }
		   }catch (SQLException e) {
	            e.printStackTrace();
	        }
		   
	   }
	   
	   public void selectByLabelRow(String str) {
		   try (Connection connection = getConnection(); Statement statement = connection.createStatement();) {
	            ResultSet result = null;
	            result = statement.executeQuery("select xstart,xend,abs(ystart) as ystart from positions where description like '%"+str+"%' order by abs(ystart) asc ");
	          
	            while(result.next()) {
	            	System.out.println();
	            	double xstart = 0;
	           double xend = 0;
	           double ystart =0;
	         
	           
//	            	System.out.println(result.getString("description")+" | "+
//	                        result.getDouble("xstart")+" | "+
//	                        result.getDouble("ystart")+" |"+
//	                        result.getDouble("xend")+" | "+
//	                        result.getDouble("yend")+" |");
	            	xstart = result.getDouble("xstart");
	            	xend = result.getDouble("xend");
	            	ystart = result.getDouble("ystart");
	            	
	            
	            	ResultSet result2 = statement.executeQuery("select * from positions where xstart >"+xstart  +" and abs(abs(ystart)-"+ystart+")"+"<=5");
	             while (result2.next()) {
		            	System.out.print(result2.getString("description")+" "
		            			);
		            }
	             
	            // System.out.println();
	            }
		   }catch (SQLException e) {
	            e.printStackTrace();
	        }
		   
	   }
}
