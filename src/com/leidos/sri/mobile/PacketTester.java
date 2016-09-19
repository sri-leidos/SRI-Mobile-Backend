package com.leidos.sri.mobile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Calendar;

import org.apache.commons.io.HexDump;
import org.bn.CoderFactory;
import org.bn.IEncoder;

import srimobile.aspen.leidos.com.sri.data.DDateTime;
import srimobile.aspen.leidos.com.sri.data.DDay;
import srimobile.aspen.leidos.com.sri.data.DHour;
import srimobile.aspen.leidos.com.sri.data.DMinute;
import srimobile.aspen.leidos.com.sri.data.DMonth;
import srimobile.aspen.leidos.com.sri.data.DSecond;
import srimobile.aspen.leidos.com.sri.data.DYear;
import srimobile.aspen.leidos.com.sri.data.DriverVehicleInformationData;
import srimobile.aspen.leidos.com.sri.data.Latitude;
import srimobile.aspen.leidos.com.sri.data.Longitude;


public class PacketTester {

	private DataTranslator translator = new DataTranslator();
	
	
	public static void main(String[] args){
		try {
			new PacketTester().run();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	public void run() throws IOException{
		DatagramSocket dgSocket = new DatagramSocket(16555);
		
		DriverVehicleInformationData info = createGenericMessage();
		
		byte[] encoded = encodeData(info);
		DatagramPacket packet = new DatagramPacket(encoded, encoded.length);
		packet.setAddress(InetAddress.getByName("2001:1890:110e:a777::231"));
		packet.setPort(55245);
		System.out.println("Sending");
		dgSocket.send(packet);

		System.out.println("Waiting for response");
		receive(dgSocket);
		
	}
	
	private void receive(DatagramSocket dgSocket) throws IOException{
		byte[] receiveData = new byte[5120];
		DatagramPacket receivedPacket = new DatagramPacket(receiveData,receiveData.length);
		dgSocket.receive(receivedPacket);
		System.out.println("Received Response ["+receivedPacket.getLength()+"]");
		byte[] resized = Arrays.copyOf(receivedPacket.getData(), receivedPacket.getLength());
		DriverVehicleInformationData retData = translator.decodeByteArray(resized);
		System.out.println("ID: "+retData.getId());
		HexDump.dump(resized, 0, System.out, 0);
		dgSocket.close();
	}
	
	
    private byte[] encodeData(DriverVehicleInformationData data){
        try {
            IEncoder<DriverVehicleInformationData> encoder = CoderFactory.getInstance().newEncoder("BER");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            encoder.encode(data, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
	
    private DriverVehicleInformationData createGenericMessage(){
        String truckV_driversLicense = "11111";
//        String driversLicense =  "11111";
        String truckV_vin = "2222";
        String truckV_usdot = "33333";
        String truckV_lp = "444555";

        
        double lat = 38.58513;
        double lon = -89.92773;

        DriverVehicleInformationData data = new DriverVehicleInformationData();
        if(true) {
            data.setCdlNumber(truckV_driversLicense);
//            data.setDriversLicenseNumber("NoValue"); //value set as required in ASN - maybe change
        }
       
        data.setPlateNumber(truckV_lp);
        data.setUsdotNumber(truckV_usdot);
        data.setVin(truckV_vin);

        Latitude latitude = new Latitude();
        latitude.setValue((int)(lat * 10000000));
        Longitude longitude = new Longitude();
        longitude.setValue((int)(lon * 10000000));
        data.setLat(latitude);
        data.setLon(longitude);
        data.setFullDate(getDateTimeForTimeStamp());
        data.setSiteId("1337-1");
        return data;
    }
	
	
	
    private DDateTime getDateTimeForTimeStamp() {
        Calendar cal = Calendar.getInstance();
        DDateTime dateTime = new DDateTime();
        DHour hour = new DHour(cal.get(Calendar.HOUR_OF_DAY));
        dateTime.setHour(hour);
        DMinute min = new DMinute(cal.get(Calendar.MINUTE));
        dateTime.setMinute(min);
        DSecond sec = new DSecond(cal.get(Calendar.SECOND));
        dateTime.setSecond(sec);
        DMonth month = new DMonth(cal.get(Calendar.MONTH)+1);
        dateTime.setMonth(month);
        DDay day = new DDay(cal.get(Calendar.DAY_OF_MONTH));
        dateTime.setDay(day);
        DYear year = new DYear(cal.get(Calendar.YEAR));
        dateTime.setYear(year);
        return dateTime;
    }
	
	
}
