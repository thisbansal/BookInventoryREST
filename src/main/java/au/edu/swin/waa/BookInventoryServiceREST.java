package au.edu.swin.waa;

import java.sql.Connection;
import java.sql.SQLException;

public class BookInventoryServiceREST {
	static int bookId ;

	public static void main(String[] args) {
		BookInventoryServiceREST.borrowABookRequest(1234123, 101, 9867);
	}
	// All the of the functions goes here

	/**
	 * Makes a book available for the student if found in the system.
	 * If not, then advises the student to try requesting through google books
	 * Also tells if student is present on the system. or If a valid student has entered a wrong secret pin
	 * @param iSBNumberInteger int type for ISBN Number of requested book
	 * @param studentIDInteger int type studentid
	 * @param studentPin int type student's secret pin key
	 * @return Returns a string stating whether a book has been borrowed successfully or if there's anything wrong with given input parameters
	 */
	public static String borrowABookRequest(Integer iSBNumberInteger,
			Integer studentIDInteger, Integer studentPin) {
		String resultString = "";
		Connection conn = CRUDSupport.connectToDatabase();
		resultString = CRUDSupport.isStudentValid(conn, studentIDInteger,
				studentPin);
		System.out.println("isValidStudent status: " + resultString);
		//switches over the results retrieved by CRUDSupport class for definitive result string for student validation
		switch (resultString) {
		case "Invalid Student ID":
			return resultString;
		case "Pin code doesn't matches":
			return resultString;
		}
		resultString = CRUDSupport.isBookValid(conn, iSBNumberInteger,studentIDInteger);
		System.out.println("isBookAvailStatus: " + resultString);
		//switches over the results retrieved by CRUDSupport class for definitive result string for book borrow request
		switch (resultString) {
			case "Book found":
			{
				System.out.println("Book has been successfully borrowed by the student ("
								+ studentIDInteger.toString() + ")");
				try {
					CRUDSupport.makeBorrowedPermanent(conn, iSBNumberInteger,
							bookId, studentIDInteger);
				} catch (SQLException e) {
					e.printStackTrace();
				}
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