package au.edu.swin.waa;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.sql.Connection;


public class CRUDSupport {

	/**
	 * Establishes a connection between the application and the database
	 * 
	 * @return A copy of the Connection object referring to the database
	 */
	public static Connection connectToDatabase() {
		Connection connectionObject = null;
		Properties connectionProperties = new Properties();
		connectionProperties.put("user", "waaWebService1");
		connectionProperties.put("password", "Assignm3nt1");
		String databseUrl = "jdbc:mysql://swinmysql-instance1.csfetredihe2.us-east-1.rds.amazonaws.com/";
		try {
			connectionObject = DriverManager.getConnection(databseUrl,
					connectionProperties);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connectionObject;
	}

	/**
	 * Check whether a student is valid or not Sends different Strings based on
	 * outcome of the query
	 * 
	 * @param conn
	 *            connection to the database.
	 * @param studentID
	 *            student ID of the student.
	 * @param studentPinCode
	 *            pin code of the student.
	 * @return Returns a string for invalid student id or pin code
	 */
	public static String isStudentValid(Connection conn, Integer studentID,
			Integer studentPinCode) {
		String result = "Invalid Student ID";
		Statement stmt = null;
		String queryString = "SELECT `idStudent`,`pinCodeStudent` FROM `waaBookInventoryDB`.`Student` WHERE `idStudent` = "
				+ studentID.toString();
		try {
			stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(queryString);
			while (resultSet.next()) {
				int id = resultSet.getInt(1);
				int pinCode = resultSet.getInt(2);
				if (id == studentID && pinCode == studentPinCode) {
					result = "Succesfully Logged In";
				} else if (id == studentID) {
					result = "Pin code doesn't matches";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException closingStmtGetStudentDetailsException) {
					closingStmtGetStudentDetailsException.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * Checks whether a book is valid or not
	 * 
	 * @param conn
	 *            Connection to the database
	 * @param iSBNumberInteger
	 *            ISBN Number of the requested book
	 * @param studentIdString
	 *            StudentID of the the student
	 * @return Returns different sets of strings whether it is a valid, invalid
	 *         or borrowed
	 */
	public static String isBookValid(Connection conn, Integer iSBNumberInteger) {
		String result = "Invalid Book ID";
		Statement stmt = null;
		String queryString = "SELECT `ISBN`,`AVAILABILITY`,`idBook` FROM `waaBookInventoryDB`.`Book` WHERE `ISBN` = "
				+ iSBNumberInteger.toString();
		try {
			stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(queryString);
			while (resultSet.next()) {
				int isbn = resultSet.getInt(1);
				String availabilityString = resultSet.getString(2);
				int idBook = resultSet.getInt(3);
				if (isbn == iSBNumberInteger
						&& availabilityString.equalsIgnoreCase("available")) {
					result = "Book found";
					BookInventoryServiceREST.bookId = idBook;
				} else if (isbn == iSBNumberInteger
						&& availabilityString.equalsIgnoreCase("Borrowed")) {
					result = "Borrowed";
					BookInventoryServiceREST.bookId = idBook;
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException closingStmtGetStudentDetailsException) {
					closingStmtGetStudentDetailsException.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * Returns the said book
	 * @param conn Connection to the database
	 * @param iSBNNumberInteger ISBN Number of the provided book
	 * @return String with information about the returning book
	 */
	public static String returnBook(Connection conn, Integer iSBNNumberInteger){
		String resultString = "Some error occured. Item couldn't be returned.";
		String queryStringForChangingFieldValue = "UPDATE `waaBookInventoryDB`.`Book` SET `Availability`='Available' WHERE `ISBN`='"
				+ iSBNNumberInteger + "'";
		String deleteColumnFromBorrowTableString = "DELETE FROM `waaBookInventoryDB`.`BooksBorrowed`WHERE idBook = "+BookInventoryServiceREST.bookId;
		Statement stmtStatement = null;
		try {
			stmtStatement = conn.createStatement();
			stmtStatement.addBatch(queryStringForChangingFieldValue);
			stmtStatement.addBatch(deleteColumnFromBorrowTableString);
			stmtStatement.executeBatch();
			resultString = "Successfuly Returned.";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			try {
				if (stmtStatement!=null)
					stmtStatement.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
		return resultString;
	}
	
	/**
	 * Updates the required tables to reflect the changes on the back head. Uses
	 * SQLBatch execution to update multiple tables
	 * 
	 * @param conn
	 *            Connection to the database
	 * @param iSBNumberInteger
	 *            ISBN Number of the requested Book
	 * @param idBook
	 *            ID of the book on the table
	 * @param studentId
	 *            StudentID given by the student.
	 */
	public static void makeBorrowedPermanent(Connection conn,
			Integer iSBNumberInteger, int idBook, int studentId) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String stringBorrowQuery = "UPDATE `waaBookInventoryDB`.`Book` SET `Availability`='Borrowed' WHERE `ISBN`='"
					+ iSBNumberInteger + "'";
			String stringJoinQuery = "INSERT INTO `waaBookInventoryDB`.`BooksBorrowed`(`idBook`,`idStudent`)VALUES("
					+ idBook + "," + studentId + ")";
			stmt.addBatch(stringBorrowQuery);
			stmt.addBatch(stringJoinQuery);
			int[] recordsAffected = stmt.executeBatch();
			for (int i : recordsAffected) {
				System.out.println("records affected: " + i);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * Closes the connection to the database securely
	 * @param conn object which needs to be closed
	 */
	public static void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
