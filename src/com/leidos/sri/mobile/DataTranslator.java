package com.leidos.sri.mobile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;
import org.json.JSONObject;

import sri.data.TruckFeed;
import srimobile.aspen.leidos.com.sri.data.DDateTime;
import srimobile.aspen.leidos.com.sri.data.DriverVehicleInformationData;
import srimobile.aspen.leidos.com.sri.data.WeightResultData;


public class DataTranslator {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
	public DriverVehicleInformationData decodeByteArray(byte[] bytes){
		DriverVehicleInformationData data = null;
		try {
			IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			data = decoder.decode(bais, DriverVehicleInformationData.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return data;
	}
	
	public byte[] encodeWeightResult(WeightResultData data) throws Exception{
		IEncoder<WeightResultData> encoder = CoderFactory.getInstance().newEncoder("BER");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		encoder.encode(data, baos);
		return baos.toByteArray();
	}
	

	public byte[] encodeDriverInfo(DriverVehicleInformationData data) throws Exception{
		IEncoder<DriverVehicleInformationData> encoder = CoderFactory.getInstance().newEncoder("BER");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		encoder.encode(data, baos);
		return baos.toByteArray();
	}	
	
	
	public String convertToJSON(DriverVehicleInformationData data){
		//going to co-opt the site id to add which message is being sent.  Will need to scrub the
		// extra site ID data out and send just the site id.
		TruckFeed tf = new TruckFeed();
		double currentLat = data.getLat().getValue();
		double currentLon = data.getLon().getValue();
		
		double lat = currentLat / 10000000;
		double lon = currentLon / 10000000;		
		tf.setCommercialDriversLicense(data.getCdlNumber());
		tf.setDriversLicense(data.getDriversLicenseNumber());
		tf.setUsdotNumber(data.getUsdotNumber());
		tf.setVin(data.getVin());
		tf.setLicensePlate(data.getPlateNumber());
		tf.setLatitude(lat);
		tf.setLongitude(lon);
		tf.setSiteId(Integer.parseInt(data.getSiteId().substring(0, data.getSiteId().indexOf("-"))));
		tf.setTimestamp(decodeDDateTime(data.getFullDate()));
		
		if(data.getId() != null && Integer.parseInt(data.getId()) > 0){
			tf.setId(Integer.parseInt(data.getId()));
		}
		
		
		StringWriter sw = new StringWriter();
		
		
		try {
			JAXBContext jc = JAXBContext.newInstance(TruckFeed.class);
			Marshaller m = jc.createMarshaller();
	        Marshaller marshaller = jc.createMarshaller();
//	        marshaller.setProperty("eclipselink.media-type", "application/json");
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        m.marshal(tf, sw);
			
			return sw.toString();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
//		JSONObject jsonObject = new JSONObject(sw.toString());
		
//		jsonObject.put("timestamp", new Timestamp(Calendar.getInstance().getTime().getTime()));
//		if(!data.getCdlNumber().equalsIgnoreCase("NoValue")){
//			jsonObject.put("commercialDriversLicense", data.getCdlNumber());
//		}
//		if(!data.getDriversLicenseNumber().equalsIgnoreCase("NoValue")){
//			jsonObject.put("driversLicense", data.getDriversLicenseNumber());
//		}
//		jsonObject.put("vin", data.getVin());
//		jsonObject.put("usdotNumber", data.getUsdotNumber());
//		jsonObject.put("licensePlate", data.getPlateNumber());
//		double lat = data.getLat().getValue() / 10000000;
//		double lon = data.getLon().getValue() / 10000000;
//		jsonObject.put("latitude", lat);
//		jsonObject.put("longitude", lon);
//		jsonObject.put("siteId", data.getSiteId().substring(0, data.getSiteId().indexOf("-")));
		
		return null;
	}
	
	
	
	private static String decodeDDateTime(DDateTime ddt){
//		yyyy-MM-dd HH:mm:ss
		int year = ddt.getYear().getValue();
		int month = ddt.getMonth().getValue();
		int day = ddt.getDay().getValue();
		int hour = ddt.getHour().getValue();
		int minute = ddt.getMinute().getValue();
		int second = ddt.getSecond().getValue();
		
		String dateString = year+"-";
		
		if(month < 10){
			dateString += "0"+month;
		}else{
			dateString += ""+month;
		}
		
		dateString += "-";
		
		if(day < 10){
			dateString += "0"+day;	
		}else{
			dateString += ""+day;
		}
		
		dateString += " ";
		
		if(hour < 10){
			dateString += "0"+hour;
		}else{
			dateString += ""+hour;
		}
		
		dateString += ":";
		
		if(minute < 10){
			dateString += "0"+minute;
		}else{
			dateString += ""+minute;
		}
		
		dateString += ":";
		
		if(second < 10){
			dateString += "0"+second;
		}else{
			dateString += ""+second;
		}
		
		
		
		return dateString;
	}
	
}
