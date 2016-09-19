package sri.data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TruckFeed {

	private Integer id;
	private Integer siteId;
	private String timestamp;
	private String licensePlate;
	private String imageUrl;
	private String driversLicense;
	private String commercialDriversLicense;
	private String vin;
	private String usdotNumber;
	private Double latitude;
	private Double longitude;
	private String sequenceNumber;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSiteId() {
		return siteId;
	}
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}
	public String getLicensePlate() {
		return licensePlate;
	}
	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getDriversLicense() {
		return driversLicense;
	}
	public void setDriversLicense(String driversLicense) {
		this.driversLicense = driversLicense;
	}
	public String getCommercialDriversLicense() {
		return commercialDriversLicense;
	}
	public void setCommercialDriversLicense(String commercialDriversLicense) {
		this.commercialDriversLicense = commercialDriversLicense;
	}
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public String getUsdotNumber() {
		return usdotNumber;
	}
	public void setUsdotNumber(String usdotNumber) {
		this.usdotNumber = usdotNumber;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public String getTimestamp() {
		if (timestamp != null && timestamp.indexOf('.') != -1) {
			return timestamp.substring(0, timestamp.indexOf('.'));
		}
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
}
