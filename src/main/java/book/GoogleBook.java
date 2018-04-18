package book;

import java.util.Arrays;

public class GoogleBook {
	private Items[] items;

	public Items[] getItems() {
		return items;
	}

	public void setItems(Items[] items) {
		this.items = items;
	}

	@Override
	public String toString() {
		return "[items = " + Arrays.toString(items) + "]";
	}
}