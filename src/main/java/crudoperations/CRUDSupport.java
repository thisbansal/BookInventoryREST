package crudoperations;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
		// String queryBookTable =
		// "SELECT `status`,`idBook`,`title`,`publisher` FROM `BookInventory`.`Book` WHERE `idBook` = "
		// + "idBook" + ";";

		String queryISBNTable = "SELECT `idBook`,`isbn13` FROM `BookInventory`.`ISBN` WHERE `idBook` = "
				+ "idBook" + ";";
		try {
			stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(queryISBNTable);
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

	/**
	 * Updates database for book, authors, isbn numbers etc.
	 * 
	 * @param conn
	 *            connection to the database;
	 * @param googleBook
	 *            Object of GoogleBook
	 */
	public static void addBookToDatabse(Connection conn, GoogleBook googleBook) {
		insertToBookTable(conn, googleBook);
		insertAuthorsToDatabase(conn, googleBook);
		insertISBNToDatabase(conn, googleBook);
	}

	/**
	 * Adds authors to the database
	 * 
	 * @param connection
	 *            to the database;
	 * @param googleBook
	 *            Object of GoogleBook
	 */
	public static void insertAuthorsToDatabase(Connection conn,
			GoogleBook googleBook) {
		Integer bookID = CRUDSupport.getBookID(conn, googleBook);
		String[] authorsString = googleBook.getItems()[0].getVolumeInfo()
				.getAuthors();
		String insertAuthorsString = "insert into BookInventory.Authors (idbook, nameAuthor)"
		        + " values (?, ?)";
		for (String name : authorsString) {
			try {
				PreparedStatement preparedStmt = conn.prepareStatement(insertAuthorsString);
			      preparedStmt.setInt    (1, bookID);
			      preparedStmt.setString (2, name);
			      preparedStmt.executeUpdate();
				 preparedStmt.close();
				preparedStmt.close();
			} catch (SQLException e) {
				System.out.println("Something wrong with insert Authour.");
			}

		}
	}

	/**
	 * Adds ISBN to the database
	 * 
	 * @param conn
	 *            connection to the database
	 * @param googleBook
	 *            Object of Google Book
	 */
	public static void insertISBNToDatabase(Connection conn,
			GoogleBook googleBook) {
		IndustryIdentifiers[] identifiers = googleBook.getItems()[0]
				.getVolumeInfo().getIndustryIdentifiers();
		String isbn_10 = "";
		String isbn_13 = "";
		for (IndustryIdentifiers identifier : identifiers) {
			if (identifier.getType().equals("ISBN_10")) {
				isbn_10 = identifier.getIdentifier();
			} else {
				isbn_13 = identifier.getIdentifier();
			}
		}
		String insertIntoisbnTable = "insert into BookInventory.ISBN (isbn10, isbn13, idBook)"
		        + " values (?, ?, ?)";
		 try {
			  PreparedStatement preparedStmt = conn.prepareStatement(insertIntoisbnTable);
		      preparedStmt.setString (1, isbn_10);
		      preparedStmt.setString (2, isbn_13);
		      preparedStmt.setInt    (3, CRUDSupport.getBookID(conn, googleBook));
		      preparedStmt.executeUpdate();
			 preparedStmt.close();
		 } catch (SQLException e) {
			System.out.println("Something wrong with insert ISBN.");
		}

	}

	/**
	 * Adds Book info to the database
	 * 
	 * @param conn
	 *            connection to the database
	 * @param googleBook
	 *            Object of Google Book
	 */
	public static void insertToBookTable(Connection conn, GoogleBook googleBook) {
		String insertIntoBookTable = "insert into BookInventory.Book (title, publisher, publishedDate, status, averageRating)"
		        + " values (?, ?, ?, ?, ?)";
		 try {
			  PreparedStatement preparedStmt = conn.prepareStatement(insertIntoBookTable);
		      preparedStmt.setString (1, googleBook.getItems()[0].getVolumeInfo().getTitle());
		      preparedStmt.setString (2, googleBook.getItems()[0].getVolumeInfo().getPublisher());
		      preparedStmt.setString (3, googleBook.getItems()[0].getVolumeInfo().getPublishedDate());
		      preparedStmt.setString (4, "availbale");
		      preparedStmt.setString (5, googleBook.getItems()[0].getVolumeInfo().getAverageRating());
		      preparedStmt.executeUpdate();
			 preparedStmt.close();
		 } catch (SQLException e) {
			 System.out.println("Something wrong with Insert into book table.");
			 e.printStackTrace();
		 }
	}

	/**
	 * Gets the BookID of the current Book in question
	 * 
	 * @param conn
	 *            connection to the database
	 * @param googleBook
	 *            object of the GoogleBook class
	 * @return
	 */
	public static Integer getBookID(Connection conn, GoogleBook googleBook) {
		Integer bookID = null;
		try {
			String getBookIDFromBookTable = "SELECT `idBook` FROM `BookInventory`.`Book` WHERE `title` = \""
					+ googleBook.getItems()[0].getVolumeInfo().getTitle()
					+ "\";";
			Statement stmt = conn.createStatement();
			ResultSet resultSet = stmt.executeQuery(getBookIDFromBookTable);
			while (resultSet.next()) {
				bookID = resultSet.getInt(1);
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Something wrong with Select statemen. Get Book ID.");
		}
		return bookID;
	}

	/**
	 * Returns the ISBN number of the Book from Json
	 * 
	 * @param googleBook
	 *            Object of the class GoogleBook
	 * @return Returns a copy of the ISBN number in string format
	 */
	public static String getISBN(GoogleBook googleBook) {
		IndustryIdentifiers[] identifiers = googleBook.getItems()[0]
				.getVolumeInfo().getIndustryIdentifiers();
		String isbn_13 = "";

		for (IndustryIdentifiers identifier : identifiers) {
			if (identifier.getType().equals("ISBN_13")) {
				isbn_13 = identifier.getIdentifier();
			}
		}
		return isbn_13;
	}
}
