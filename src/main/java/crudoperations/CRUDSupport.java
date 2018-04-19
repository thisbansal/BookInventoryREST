package crudoperations;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
	public static void returnBook(Connection conn, Book book) {
		try {
			String stringReturnQuery = "update BookInventory.Book set status = ? where idBook = ?";
			String stringJoinTableQuery = "delete from BookInventory.BorrowBook where idBook = ?";
			PreparedStatement preparedStatement = conn.prepareStatement(stringReturnQuery);
			preparedStatement.setString(1, "available");
			preparedStatement.setInt(2, book.getIdBook());
			preparedStatement.executeUpdate();
			preparedStatement = conn.prepareStatement(stringJoinTableQuery);
			preparedStatement.setInt(1, book.getIdBook());
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
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
	public static void makeBorrowedPermanent(Connection conn,Book book, String studentID, String order) {
		try {
			Integer studentIDInteger = Integer.parseInt(studentID);
			String stringBorrowQuery = "update BookInventory.Book set status = ? where idBook = ?";
			String stringJoinQueryForBorrow = "insert into BookInventory.BorrowBook (idBook, idStudent) values (?,?)";
			String stringJoinQueryForPurchase = "insert into BookInventory.PurchasedBook (idBook, idStudent) values (?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(stringBorrowQuery);
			preparedStatement.setString(1, order);
			preparedStatement.setInt(2, book.getIdBook());
			preparedStatement.executeUpdate();
			if (order.equalsIgnoreCase("purchased")){
				preparedStatement = conn.prepareStatement(stringJoinQueryForPurchase);
			}
			else {

				preparedStatement = conn.prepareStatement(stringJoinQueryForBorrow);
			}
			preparedStatement.setInt(1, book.getIdBook());
			preparedStatement.setInt(2, studentIDInteger);
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) {
			e.printStackTrace();
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
		String queryISBNTable = "select idBook from BookInventory.ISBN where (isbn10 =?  or isbn13 = ?)";
		Integer bookId = 0;
		String titleString = "";
		String statusString = "";
		String averageRatingString = "";
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(queryISBNTable);
			preparedStatement.setString(1, iSBNumberInteger);
			preparedStatement.setString(2, iSBNumberInteger);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			while (resultSet.next()) {
				bookId = resultSet.getInt(1);
			}
			if (bookId != 0){
				queryISBNTable = "select title, status, averageRating from BookInventory.Book where idBook = ?";
				preparedStatement = conn.prepareStatement(queryISBNTable);
				preparedStatement.setInt(1, bookId);
				resultSet = preparedStatement.executeQuery();
				
				while (resultSet.next()) {
					titleString = resultSet.getString("title");
					statusString = resultSet.getString("status");
					averageRatingString = resultSet.getString("averageRating");
				}

				book = new Book(iSBNumberInteger, titleString, statusString, bookId, averageRatingString);
			}
			preparedStatement.close();
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
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
	public static void addBookToDatabse(Connection conn, GoogleBook googleBook, Integer idStudent) {
		insertToBookTable(conn, googleBook);
		insertAuthorsToDatabase(conn, googleBook);
		insertISBNToDatabase(conn, googleBook);
		insertToRequestedTable(conn, googleBook, idStudent);
	}
	
	/**
	 * Inserts Requested book info into the Requested table.
	 * 
	 * @param conn
	 *            connection to the database;
	 * @param googleBook
	 *            Object of GoogleBook
	 */
	public static void insertToRequestedTable(Connection conn, GoogleBook googleBook, Integer idStudent){
		Integer bookID = CRUDSupport.getBookID(conn, googleBook);
		String insertAuthorsString = "insert into BookInventory.Requested (idbook, idStudent) values (?, ?)";
		try {
			PreparedStatement preparedStmt = conn.prepareStatement(insertAuthorsString);
		      preparedStmt.setInt    (1, bookID);
		      preparedStmt.setInt (2, idStudent);
		      preparedStmt.executeUpdate();
		      preparedStmt.close();
		      preparedStmt.close();
		} catch (SQLException e) {
			System.out.println("Something wrong with insert RequestedTable update query.");
		}
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
		String insertAuthorsString = "insert into BookInventory.Authors (idbook, nameAuthor) values (?, ?)";
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
		      preparedStmt.setString (4, "back order");
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
			System.out.println("Something wrong with Select statement. Get Book ID.");
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

	public static ArrayList<String> getAllBooks(Connection conn) {
		ArrayList<String> allDataStrings = new ArrayList<String>();
		ArrayList<String> title = new ArrayList<String>();
		ArrayList<String> publisher= new ArrayList<String>();
		ArrayList<String> status = new ArrayList<String>();
		ArrayList<String> averageRating = new ArrayList<String>();
		ArrayList<String> isbn = new ArrayList<String>();
		String queryString = "select title, publisher, status, averageRating from BookInventory.Book";
		String queryISBN = "select isbn10 from BookInventory.ISBN";
		try{
			PreparedStatement preparedStatement = conn.prepareStatement(queryString);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()){
				title.add(resultSet.getString(1));
				int index = title.size()-1;
				if (title.get(index).length()>40) title.set(index, title.get(index).substring(0, 40)+"..."); ;
				publisher.add(resultSet.getString(2));
				status.add(resultSet.getString(3));
				averageRating.add(resultSet.getString(4));
			}
			preparedStatement = conn.prepareStatement(queryISBN);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()){
				isbn.add(resultSet.getString(1));
			}
			preparedStatement.close();
			resultSet.close();
			for (int i =0; i< title.size(); i++) {
				String dataString = title.get(i) +" published by "+publisher.get(i)+"."+" It's currently under \""+status.get(i)+"\" status. "+"It's rating and ISBN is "+averageRating.get(i)+" and "+isbn.get(i)+" resp.";
				allDataStrings.add(dataString);
			}
		}
		
		catch (SQLException e) {
			System.out.println("Something wrong with view all books prepared statement.");
		}
		return allDataStrings;
	}

	public static ArrayList<String> getBorrowedBooks(Connection conn, Integer idStudent) {
		ArrayList<String> booksBorrowed = new ArrayList<String>();
		ArrayList<Integer> idBook = new ArrayList<Integer>();
		ArrayList<String> isbn = new ArrayList<String>();
		ArrayList<String> title = new ArrayList<String>();
		ArrayList<String> status = new ArrayList<String>();
		String queryString = "select idBook from BookInventory.BorrowBook where idStudent = ?";
		String queryTitle = "select title,status from BookInventory.Book where idBook = ?";
		String queryISBN = "select isbn10 from BookInventory.ISBN where idBook = ?";
		try{
			PreparedStatement preparedStatement = conn.prepareStatement(queryString);
			preparedStatement.setInt(1, idStudent);
			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()){
				idBook.add(resultSet.getInt(1));
				System.out.println(resultSet.getInt(1));
			}
			for (Integer idString : idBook){
				preparedStatement = conn.prepareStatement(queryTitle);
				preparedStatement.setInt(1, idString);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()){
					title.add(resultSet.getString(1));
					int index = title.size()-1;
					if (title.get(index).length()>40) title.set(index, title.get(index).substring(0, 40)+"..."); ;
					status.add(resultSet.getString(2));
				}
			}
			for (Integer idInteger : idBook){
				preparedStatement = conn.prepareStatement(queryISBN);
				preparedStatement.setInt(1, idInteger);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()){
					isbn.add(resultSet.getString(1));
				}
			}
			System.out.println(isbn.toString());
			preparedStatement.close();
			resultSet.close();
			for (int i =0; i< idBook.size(); i++) {
				String dataString = "Student "+idStudent+", has "+status.get(i)+" "+title.get(i)+" book. Book's ISBN is "+isbn.get(i);
				booksBorrowed.add(dataString);
			}
		}
		
		catch (SQLException e) {
			System.out.println("Something wrong with view all books prepared statement.");
			e.printStackTrace();
		}
		return booksBorrowed;
	}
}
