package au.edu.swin.waa;

import java.sql.Connection;

public class BookInventoryServiceREST {
	static int bookId;

	/**
	 * for testing the web service locally
	 * this method will be deleted once pushed to ws02
	 * @param args
	 */
	public static void main(String[] args) {
		BookInventoryServiceREST.returnABook(1212);
	}

	// All the of the functions goes here

	/**
	 * If a book is present in the system log and currently under borrowed state
	 * if will set it to available
	 * Also tells whether the given book belongs to the library or not
	 * Also removes the entry from borrow table of a particular student for borrowed book
	 * @param iSBNumberInteger ISBN Number of the the book of type Integer
	 * @return Returns different strings referring whether book has been returned or not.
	 */
	public static String returnABook(Integer iSBNumberInteger) {
		String resultString = "";
		Connection conn = CRUDSupport.connectToDatabase();
		String isValidBookString = CRUDSupport.isBookValid(conn,
				iSBNumberInteger);
		switch (isValidBookString) {
		case "Borrowed": {
			resultString = CRUDSupport.returnBook(conn, iSBNumberInteger);
			CRUDSupport.closeConnection(conn);
			switch (resultString) {
			case "Successfuly Returned.":
				return resultString;
			default:
				resultString = "Some error occured. Item couldn't be returned";
				return resultString;
			}
		}
		case "Book found": {
			resultString = "Book is already present in the system.";
			return resultString;
		}
		default:
			resultString = "This Book doesn't belongs to this library.";
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
			Integer studentIDInteger, Integer studentPin) {
		String resultString = "";
		Connection conn = CRUDSupport.connectToDatabase();
		resultString = CRUDSupport.isStudentValid(conn, studentIDInteger,
				studentPin);
		// switches over the results retrieved by CRUDSupport class for
		// definitive result string for student validation
		switch (resultString) {
		case "Invalid Student ID":
			return resultString;
		case "Pin code doesn't matches":
			return resultString;
		}
		resultString = CRUDSupport.isBookValid(conn, iSBNumberInteger);
		// switches over the results retrieved by CRUDSupport class for
		// definitive result string for borrow book request
		switch (resultString) {
		case "Book found": {
			System.out
					.println("Book has been successfully borrowed by the student ("
							+ studentIDInteger.toString() + ")");
			CRUDSupport.makeBorrowedPermanent(conn, iSBNumberInteger, bookId,
					studentIDInteger);
			CRUDSupport.closeConnection(conn);
			resultString += " " + studentIDInteger.toString();
			return resultString;

		}
		case "Invalid Book ID":
			resultString = "No Book found with given ISBN number. Try requesting from Google Book.";
			break;
		case "borrowed":
			resultString = "Book is currently not availbale to be borrowed.";
			break;
		}
		CRUDSupport.closeConnection(conn);
		return resultString;
	}
	
	/**
	 * Request a copy of the requested book from Google Books If a books is
	 * available, BookInvertory will also add it to their database for the
	 * students to make use of it.
	 * 
	 * @param bookISBNumberInteger
	 *            An integer number of the requested Book
	 * @return True if the book is added succesfully to the library from Google
	 *         Books
	 */
	public static boolean requestTheBookFromGoogleBooks(
			Integer bookISBNumberInteger) {
		boolean isBookAvailableByTheUniversity = false;

		return isBookAvailableByTheUniversity;
	}

	
}