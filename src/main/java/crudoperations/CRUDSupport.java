package crudoperations;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.sql.Connection;

import book.Book;
import book.GoogleBook;
import book.IndustryIdentifiers;

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
			Class.forName("com.mysql.jdbc.Driver");
			connectionObject = DriverManager.getConnection(databseUrl,
					connectionProperties);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return connectionObject;
	}

	/**
	 * Returns the said book
	 * 
	 * @param conn
	 *            Connection to the database
	 * @param iSBNNumberInteger
	 *            ISBN Number of the provided book
	 * @param bookIdInteger
	 * @return String with information about the returning book
	 */
	public static void returnBook(Connection conn, Integer iSBNNumberInteger,
			Integer bookIdInteger) {
		String queryStringForChangingFieldValue = "UPDATE `waaBookInventoryDB`.`Book` SET `Availability`='Available' WHERE `ISBN`='"
				+ iSBNNumberInteger + "'";
		String deleteColumnFromBorrowTableString = "DELETE FROM `waaBookInventoryDB`.`borrowedbooks` WHERE `idBook`="
				+ bookIdInteger;
		Statement stmtStatement = null;
		try {
			stmtStatement = conn.createStatement();
			stmtStatement.addBatch(queryStringForChangingFieldValue);
			stmtStatement.addBatch(deleteColumnFromBorrowTableString);
			stmtStatement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmtStatement != null)
					stmtStatement.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
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
			Integer iSBNumberInteger, int idBook, Integer studentId) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String stringBorrowQuery = "UPDATE `waaBookInventoryDB`.`Book` SET `Availability`='Borrowed' WHERE `ISBN`='"
					+ iSBNumberInteger + "'";
			String stringJoinQuery = "INSERT INTO `waaBookInventoryDB`.`borrowedbooks` (`idBook`, `idStudent`) VALUES("
					+ idBook + "," + studentId + ")";

			stmt.addBatch(stringBorrowQuery);
			stmt.addBatch(stringJoinQuery);
			stmt.executeBatch();
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
	 * 
	 * @param conn
	 *            object which needs to be closed
	 */
	public static void closeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a book if, it belongs to the library.
	 * 
	 * @param conn
	 *            connection to the database
	 * @param iSBNumberInteger
	 *            ISBNNumberof the book
	 * @return Returns a copy of the book if found. Otherwise returns null.
	 */
	public static Book returnACopyOfBook(Connection conn,
			String iSBNumberInteger) {
		Book book = null;
		Statement stmt = null;
		String queryString = "SELECT `ISBN`,`AVAILABILITY`,`idBook`,`nameBook`,`publisher` FROM `waaBookInventoryDB`.`Book` WHERE `ISBN` = "
				+ iSBNumberInteger + ";";

		try {
			stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(queryString);
			while (resultSet.next()) {
				book = new Book(resultSet.getInt(1), resultSet.getString(4),
						resultSet.getString(2), resultSet.getInt(3));
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
		return book;
	}

	public static void addBookToDatabse(Connection conn, GoogleBook googleBook) {
		insertToBookTable(conn, googleBook);
		insertAuthorsToDatabase(conn, googleBook);
		insertISBNToDatabase(conn, googleBook);
	}

	public static void insertAuthorsToDatabase(Connection conn,
			GoogleBook googleBook) {
		Integer bookID = CRUDSupport.getBookID(conn, googleBook);
		String[] authorsString = googleBook.getItems()[0].getVolumeInfo()
				.getAuthors();
		for (String name : authorsString) {
			String insertAuthorsString = "INSERT INTO `BookInventory`.`Authors` (`idBook`, `nameAuthor`)"
					+ " VALUES ('" + bookID + "', '" + name + "');";
			System.out.println("------------------------------------------------\n"+insertAuthorsString);
			/*
			 * Statement stmt = null; try { stmt = conn.createStatement();
			 * stmt.execute(insertAuthorsString); stmt.close();
			 * System.out.println("Inside Authors Table"); } catch (SQLException e)
			 * { e.printStackTrace(); }
			 */
		}
	}

	public static void insertISBNToDatabase(Connection conn,
			GoogleBook googleBook) {
		
		IndustryIdentifiers[] identifiers = googleBook.getItems()[0].getVolumeInfo().getIndustryIdentifiers();
		String isbn_10="";
		String isbn_13="";
		
		for(IndustryIdentifiers identifier : identifiers){
			if (identifier.getType().equals("ISBN_10")){
				isbn_10 = identifier.getIdentifier();
			}
			else {
				isbn_13 = identifier.getIdentifier();
			}
		}
		String insertIntoISBNTableString = "INSERT INTO `BookInventory`.`ISBN` "
				+ "(`isbn10`, `isbn13`, `idBook`) VALUES"
				+ "('"
				+ isbn_10
				+ "', '"
				+ isbn_13
				+ "', '"
				+ CRUDSupport.getBookID(conn, googleBook) + "');";
		
		System.out.println("-----------------------------------------------------\n"+insertIntoISBNTableString);
		// Statement stmt = null;
		// try {
		// stmt = conn.createStatement();
		// stmt.execute(insertIntoISBNTableString);
		// stmt.close();
		// System.out.println("Inside ISBN Table");
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }

	}

	public static void insertToBookTable(Connection conn, GoogleBook googleBook) {
		String averageRatingString = googleBook.getItems()[0].getVolumeInfo()
				.getAverageRating();
		String insertIntoBookTable = "";
		if (!averageRatingString.isEmpty()) {
			insertIntoBookTable = "INSERT INTO `BookInventory`.`Book` "
					+ "(`title`, `publisher`, `publishedDate`, `status`, `averageRating`) "
					+ "VALUES ('"
					+ googleBook.getItems()[0].getVolumeInfo().getTitle()
					+ "', '"
					+ googleBook.getItems()[0].getVolumeInfo().getPublisher()
					+ "', '"
					+ googleBook.getItems()[0].getVolumeInfo()
							.getPublishedDate() + "', 'availbale', '"
					+ averageRatingString + "');";
		} else {
			insertIntoBookTable = "INSERT INTO `BookInventory`.`Book` "
					+ "(`title`, `publisher`, `publishedDate`, `status`) "
					+ "VALUES ('"
					+ googleBook.getItems()[0].getVolumeInfo().getTitle()
					+ "', '"
					+ googleBook.getItems()[0].getVolumeInfo().getPublisher()
					+ "', '"
					+ googleBook.getItems()[0].getVolumeInfo()
							.getPublishedDate() + "', 'availbale');";
		}

		System.out.println("-------------------------------------------\n"+insertIntoBookTable + "\n");
		// Statement stmt = null;
		// try {
		// stmt = conn.createStatement();
		// stmt.execute(insertIntoBookTable);
		// stmt.close();
		// System.out.println("Inside Book Table");
		// } catch (SQLException e) {
		// e.printStackTrace();
		// }
	}

	public static Integer getBookID(Connection conn, GoogleBook googleBook) {
		Integer bookID = null;
		try {
			String getBookIDFromBookTable = "SELECT `idBook` FROM `BookInventory`.`Book` WHERE `title` = \""
					+ googleBook.getItems()[0].getVolumeInfo().getTitle()
					+ "\";";
			System.out.println("--------------------------------------------------------\n"
					+ getBookIDFromBookTable);
			Statement stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(getBookIDFromBookTable);
			while (resultSet.next()) {
				bookID = resultSet.getInt(1);
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bookID;
	}

	public static String getISBN(Connection conn, GoogleBook googleBook) {
		IndustryIdentifiers[] identifiers = googleBook.getItems()[0].getVolumeInfo().getIndustryIdentifiers();
		String isbn_13="";
		
		for(IndustryIdentifiers identifier : identifiers){
			if (identifier.getType().equals("ISBN_13")){
				isbn_13 = identifier.getIdentifier();
			}
		}
		return isbn_13;
	}
}
