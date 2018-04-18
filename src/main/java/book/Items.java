package book;

public class Items {
	private SaleInfo saleInfo;

	private VolumeInfo volumeInfo;

	public SaleInfo getSaleInfo() {
		return saleInfo;
	}

	public void setSaleInfo(SaleInfo saleInfo) {
		this.saleInfo = saleInfo;
	}

	public VolumeInfo getVolumeInfo() {
		return volumeInfo;
	}

	public void setVolumeInfo(VolumeInfo volumeInfo) {
		this.volumeInfo = volumeInfo;
	}

	@Override
	public String toString() {
		return "[saleInfo = " + saleInfo + ", volumeInfo = " + volumeInfo + "]";
	}
}
