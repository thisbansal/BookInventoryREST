package au.edu.swin.waa;

import java.util.Properties;

public class BookInventoryServiceREST{
	
	//Code for global variables
	
	//All the of the functions goes here
	
	/**
	 * Checks whether a book is available by the university.
	 * If a book is not originally available by the university
	 * Student can opt to order the book from Google Books.
	 * Which later will be made available by the university if
	 * Google Books holds a copy of the requested book.
	 * @param bookISBNumberInteger An integer number of the requested Book
	 * @return Returns True if the book is available at the library
	 */
	public boolean checkIfLibraryHasTheBook(Integer bookISBNumberInteger){
		boolean isBookAvailable = false;
		
		
		
		return isBookAvailable;
	}
	
	/**
	 * Request a copy of the requested book from Google Books
	 * If a books is available, BookInvertory will also add it to
	 * their database for the students to make use of it.
	 * @param bookISBNumberInteger An integer number of the requested Book
	 * @return Returns True if the book is available at the library if found at Google Books
	 */
	public boolean requestTheBookFromGoogleBooks(Integer bookISBNumberInteger){
		boolean isBookAvailableByTheUniversity = false;
		
		return isBookAvailableByTheUniversity;
	}
	
	
}