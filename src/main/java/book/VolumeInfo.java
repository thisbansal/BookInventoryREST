package book;

import java.util.Arrays;

public class VolumeInfo {
	private String[] authors;

	private String title;

	private String averageRating;

	private IndustryIdentifiers[] industryIdentifiers;

	private String publishedDate;

	private String publisher;

	public String[] getAuthors() {
		return authors;
	}

	public void setAuthors(String[] authors) {
		this.authors = authors;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(String averageRating) {
		this.averageRating = averageRating;
	}

	public IndustryIdentifiers[] getIndustryIdentifiers() {
		return industryIdentifiers;
	}

	public void setIndustryIdentifiers(IndustryIdentifiers[] industryIdentifiers) {
		this.industryIdentifiers = industryIdentifiers;
	}

	public String getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	@Override
	public String toString() {
		return "[authors = " + Arrays.toString(authors) + ", title = " + title
				+ ", averageRating = " + averageRating
				+ ", industryIdentifiers = " + Arrays.toString(industryIdentifiers)
				+ ", publishedDate = " + publishedDate + ", publisher = "
				+ publisher + "]";
	}
}
