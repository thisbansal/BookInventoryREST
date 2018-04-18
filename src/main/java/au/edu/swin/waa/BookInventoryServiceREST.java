package au.edu.swin.waa;

import java.sql.Connection;


import crudoperations.CRUDSupport;
import book.Book;
import book.GoogleBook;

public class BookInventoryServiceREST {
	
	/**
	 * Adds a book to the database if it is not previously availbale at the library
	 * @param bookDetails JSON info about the book
	 * @return String with different key string
	 */
	public static String addBook(String bookDetails) {
//		String resultString = "No book found";
		GoogleBook googleBook = new GenerateBookClasses().getMeBookClass(bookDetails);
		
		Connection conn = CRUDSupport.connectToDatabase();
		
//		String iSBNumberInteger = CRUDSupport.getISBN(googleBook);
//		System.out.println(CRUDSupport.getBookID(conn, googleBook));
//		Book book = CRUDSupport.returnACopyOfBook(conn, iSBNumberInteger);
		
//		if (book == null){
			CRUDSupport.addBookToDatabse(conn, googleBook);
		String	resultString = "Book is successfully added to the library";
//		}
//		else {
//			resultString = "Book is already available at library";
//		}
		CRUDSupport.closeConnection(conn);
		return resultString;
	}

	/**
	 * If a book is present in the system log and currently under borrowed state
	 * if will set it to available Also tells whether the given book belongs to
	 * the library or not Also removes the entry from borrow table of a
	 * particular student for borrowed book
	 * 
	 * @param iSBNumberInteger
	 *            ISBN Number of the the book of type Integer
	 * @return Returns different strings referring whether book has been
	 *         successfully returned or not.
	 */
	public static String returnABook(Integer iSBNumberInteger) {
		String resultString = "norecord";
		Book book = null;
		Connection conn = CRUDSupport.connectToDatabase();
		book = CRUDSupport.returnACopyOfBook(conn, iSBNumberInteger.toString());
		if (book != null) {
			if (book.getAvailability().equalsIgnoreCase("borrowed")) {
				CRUDSupport
						.returnBook(conn, iSBNumberInteger, book.getIdBook());
				resultString = "yes";
			} else {
				resultString = "no";
			}
		}
		CRUDSupport.closeConnection(conn);
		return resultString;
	}

	/**
	 * Makes a book available for the student if found in the system. If not,
	 * then advises the student to try requesting through google books Also
	 * tells if student is present on the system. or If a valid student has
	 * entered a wrong secret pin
	 * 
	 * @param iSBNumberInteger
	 *            Integer type for ISBN Number of requested book
	 * @param studentIDInteger
	 *            Integer type for StudentID
	 * @param studentPin
	 *            Integer type student's secret pin key
	 * @return Returns a string stating whether a book has been borrowed
	 *         successfully or if there's anything wrong with given input
	 *         parameters
	 */
	public static String borrowABookRequest(Integer iSBNumberInteger,
			Integer studentIDInteger) {
		String resultString = "No Book found with given ISBN number. Try requesting from Google Book.";
		Book book = null;
		Connection conn = CRUDSupport.connectToDatabase();
		book = CRUDSupport.returnACopyOfBook(conn, iSBNumberInteger.toString());
		if (book != null) {
			if (book.getAvailability().equalsIgnoreCase("available")) {
				CRUDSupport.makeBorrowedPermanent(conn, book.getIsbnNumber(),
						book.getIdBook(), studentIDInteger);
				CRUDSupport.closeConnection(conn);
				resultString = studentIDInteger.toString()
						+ " has successfully borrowed book associated with this "
						+ iSBNumberInteger + " ISBN number";
			} else {
				resultString = "Book is currently not availbale to be borrowed.";
			}
		}
		CRUDSupport.closeConnection(conn);
		return resultString;
	}

}