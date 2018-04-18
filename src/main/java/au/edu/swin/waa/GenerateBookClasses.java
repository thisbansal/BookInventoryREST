package au.edu.swin.waa;

import com.google.gson.Gson;

import book.GoogleBook;

public class GenerateBookClasses {

	public GoogleBook getMeBookClass(String bookDetails) {
		GoogleBook book = null;
		Gson gson = new Gson();
		book = gson.fromJson(bookDetails, GoogleBook.class);
		return book;
	}
}
