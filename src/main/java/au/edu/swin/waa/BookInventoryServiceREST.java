package au.edu.swin.waa;

import java.sql.Connection;

public class BookInventoryServiceREST {
	/**
	 * used to keep track of the unique book id of the a particular book when borrowing or returning it.
	 */
	static int bookId;	
	
	// All the of the functions goes here

	/**
	 * If a book is present in the system log and currently under borrowed state
	 * if will set it to available Also tells whether the given book belongs to
	 * the library or not Also removes the entry from borrow table of a
	 * particular student for borrowed book
	 * 
	 * @param iSBNumberInteger
	 *            ISBN Number of the the book of type Integer
	 * @return Returns different strings referring whether book has been
	 *         returned or not.
	 */
	public static String returnABook(Integer iSBNumberInteger) {
		String resultString = "";
		Connection conn = CRUDSupport.connectToDatabase();
		String isValidBookString = CRUDSupport.isBookValidToReturn(conn,
				iSBNumberInteger);
		switch (isValidBookString) {
		case "yes":
			CRUDSupport.returnBook(conn, iSBNumberInteger, BookInventoryServiceREST.bookId);
			resultString = "yes";
			break;
		case "no":
			resultString = "no";
			break;
		case "norecord":
			resultString = "norecord";
			break;
		default:
			break;
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
		String resultString = "";
		Connection conn = CRUDSupport.connectToDatabase();
		resultString = CRUDSupport.isBookValid(conn, iSBNumberInteger);
		switch (resultString) {
		case "successfully borrowed": {
			
			CRUDSupport.makeBorrowedPermanent(conn, iSBNumberInteger, bookId,
					studentIDInteger);
			CRUDSupport.closeConnection(conn);
			resultString = studentIDInteger.toString()+" has successfully borrowed book associated with this "+iSBNumberInteger+" ISBN number";
			return resultString;

		}
		case "nobookfound":
			resultString = "No Book found with given ISBN number. Try requesting from Google Book.";
			break;
		case "alreadyborrowed":
			resultString = "Book is currently not availbale to be borrowed.";
			break;
		}
		CRUDSupport.closeConnection(conn);
		return resultString;
	}

}