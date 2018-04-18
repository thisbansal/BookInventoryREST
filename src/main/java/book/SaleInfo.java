package book;

public class SaleInfo {
	private String saleability;

	private String country;

	public String getSaleability() {
		return saleability;
	}

	public void setSaleability(String saleability) {
		this.saleability = saleability;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "[saleability = " + saleability + ", country = " + country + "]";
	}
}