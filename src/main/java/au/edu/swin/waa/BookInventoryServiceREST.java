package au.edu.swin.waa;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;

import crudoperations.CRUDSupport;
import book.Book;
import book.GoogleBook;

public class BookInventoryServiceREST {
	
	/**
	 * Adds a book to the database if it is not previously available at the library
	 * @param bookDetails JSON info about the book
	 * @return String with different key string
	 */
	public static String addBook(String bookDetails, Integer idStudent) {
//		System.out.println("inside function");
		GoogleBook googleBook = new GenerateBookClasses().getMeBookClass(bookDetails);
		String resultString ="No Book Found with given ISBN Number";
		String iSBNumberInteger = CRUDSupport.getISBN(googleBook);
		Connection conn = CRUDSupport.connectToDatabase();
		String sellableString = googleBook.getItems()[0].getSaleInfo().getSaleability();
		String countryString = googleBook.getItems()[0].getSaleInfo().getCountry();
		if (countryString.equalsIgnoreCase("au")) {
//			System.out.println("inside countryString");
			if (sellableString.equalsIgnoreCase("FOR_SALE")|| sellableString.equalsIgnoreCase("FOR_SALE_AND_RENTAL")) {
//				System.out.println("inside sellable");
				Double rating = Double.parseDouble(googleBook.getItems()[0].getVolumeInfo().getAverageRating());
				if (rating != null && rating >=3.5){
//					System.out.println("inside rating: " + rating);
					Book book = CRUDSupport.returnACopyOfBook(conn, iSBNumberInteger);
					if (book == null && googleBook.getItems().length == 1){
//						System.out.println("inside book if");
						CRUDSupport.addBookToDatabse(conn, googleBook, idStudent);
						resultString = "Book is successfully added to the library";
					}
					else if (book != null){
//						System.out.println("inside else if for book");
						resultString = "Book is already available at library";
					}
				}
				else {
//					System.out.println("inside rating else");
					resultString = "The rating standard of the requested book doesn't complies well with library standards."+
									" Try requesting another book.";
				}
			}
			else {
//				System.out.println("Inside outside country else");
				resultString = "Book cannot be request for students from "+countryString;
			}
		}
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
	public static String returnABook(String iSBNumberInteger) {
		String resultString = "norecord";
		Book book = null;
		Connection conn = CRUDSupport.connectToDatabase();
		book = CRUDSupport.returnACopyOfBook(conn, iSBNumberInteger);
		if (book != null && book.getStatus().equalsIgnoreCase("borrowed")) {
			CRUDSupport.returnBook(conn, book);
			resultString = "yes";
		}
		else if(book.getStatus().equalsIgnoreCase("back order")){
			resultString = "back order";
		}
		else if(book.getStatus().equalsIgnoreCase("purchased")){
			resultString = "purchased";
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
	public static String borrowABookRequest(String iSBNumberString,
			String studentIDInteger, String order) {
		String resultString = "No Book found with given ISBN number. Try requesting from Google Book.";
		Book book = null;
		Connection conn = CRUDSupport.connectToDatabase();
		book = CRUDSupport.returnACopyOfBook(conn, iSBNumberString);
		if (book != null && book.getStatus().equalsIgnoreCase("available")) {
			CRUDSupport.makeBorrowedPermanent(conn, book, studentIDInteger, order);
			CRUDSupport.closeConnection(conn);
			resultString = studentIDInteger.toString()+ " has successfully "+order+" book associated with this "+ iSBNumberString + " ISBN number";
		}else{ 
			resultString = "Book is currently not available to be borrowed or purchased."; 
		}
		
		CRUDSupport.closeConnection(conn);
		return resultString;
	}
	
	public static String viewAllBooks(){
		ArrayList<String> dataString = new ArrayList<String>();
		String resultString = "";
		Connection conn = CRUDSupport.connectToDatabase();
		dataString = CRUDSupport.getAllBooks(conn);
		CRUDSupport.closeConnection(conn);
		for (String data : dataString){
			resultString += "\n"+data+"\n";
		}
		return resultString;
	}
	
	public static String viewBorrowedBooks(Integer idStudent){
		String resultString = "";
		ArrayList<String> dataStrings = new ArrayList<String>();
		Connection conn =CRUDSupport.connectToDatabase();
		dataStrings = CRUDSupport.getBorrowedBooks(conn, idStudent);
		CRUDSupport.closeConnection(conn);
		for (String string : dataStrings) {
			resultString += "\n"+string+"\n";
		}
		System.out.println(resultString);
		return resultString;
	}

	public static String addABookToLibrary(String title, String publisher, String publishedDate, 
				Double rating, String authors, String ISBN){
		String resultString = "Some info is missing";
		Connection conn = CRUDSupport.connectToDatabase();
		if (rating != null && rating >=3.5 && rating <=5.0){
			System.out.println("inside rating: " + rating);
			
		}
		CRUDSupport.closeConnection(conn);
		return resultString;
	}
}