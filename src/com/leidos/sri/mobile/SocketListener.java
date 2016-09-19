package com.leidos.sri.mobile;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.HexDump;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import srimobile.aspen.leidos.com.sri.data.DriverVehicleInformationData;
import srimobile.aspen.leidos.com.sri.data.WeightResultData;


public class SocketListener implements DataProcessorCallback {
	
	private Logger logger = Logger.getLogger(SocketListener.class);
	
	private DatagramSocket ss = null;
	private ServerSocket adminSS = null;
	
	private ServerSocketListener ssListener = null;
	
	private boolean stopped = false;
	private boolean interrupted = false;

    public static final String APPROACH_ADDITION = "1";
    public static final String WIM_ENTER_ADDITION = "2";
    public static final String WIM_EXIT_ADDITION = "3";
	
	
//	private DataTranslator translator = new DataTranslator();
//	private WSCommunicator wsCommunicator = new WSCommunicator();
	
	private long currentId = 0;
	
	private Map<Long, DataProcessor> processors = new HashMap<Long, SocketListener.DataProcessor>();
	
	
	
	private class DataProcessor implements Runnable{
		private DataTranslator translator = new DataTranslator();
		private WSCommunicator wsCommunicator = new WSCommunicator();
		
		private long threadId;
		private DataProcessorCallback callback;
		private DatagramPacket packet;
		
		public DataProcessor(long threadId, DataProcessorCallback callback, DatagramPacket packet){
			
			this.threadId = threadId;
			this.callback = callback;
			this.packet = packet;
		}
		
		
		public void run(){
			try{
				logger.debug("Received message from: ["+packet.getAddress().getHostName()+"]");
				
				
				byte[] resized = Arrays.copyOf(packet.getData(), packet.getLength());
				
				
				logger.debug("Decoding byte array");
				
				DriverVehicleInformationData data = translator.decodeByteArray(resized);
				
				logger.debug("Have message object");
				
				String xml = translator.convertToJSON(data);
				
				logger.debug("Message translated to XML");
				
				if(data.getSiteId().endsWith(APPROACH_ADDITION)){
					logger.debug("Calling approach web service");
					int id = wsCommunicator.callApproachWebService(xml);
					data.setId(id+"");
					
					byte[] retBytes = translator.encodeDriverInfo(data);
					HexDump.dump(retBytes, 0, System.out, 0);
					packet.setData(retBytes);
					logger.debug("Sending result datagram: ["+retBytes.length+"] bytes");
					
					DatagramPacket retpacket = new DatagramPacket(retBytes, retBytes.length, packet.getAddress(), packet.getPort());
					ss.send(retpacket);
					
	//				ss.send(packet);
				}else if(data.getSiteId().endsWith(WIM_ENTER_ADDITION)){
					logger.debug("Calling wim enter web service");
					wsCommunicator.callWimEnterWebService(xml);
					logger.debug("Sending result datagram");
					ss.send(packet);
				}else if(data.getSiteId().endsWith(WIM_EXIT_ADDITION)){
					logger.debug("Calling win exit web service");
					boolean result = wsCommunicator.callWimExitWebService(xml);
					logger.debug("Constructing Weight Result Response");
					WeightResultData resultData = new WeightResultData();
					resultData.setFullDate(data.getFullDate());
					resultData.setId(data.getId());
					resultData.setSiteId(data.getSiteId());
					if(result){
						resultData.setWeightResult("PASS");
					}else{
						resultData.setWeightResult("FAIL");
					}
					logger.debug("Encoding Weight Result response");
					byte[] resultBytes = translator.encodeWeightResult(resultData);
					packet.setData(resultBytes);
					logger.debug("Sending result datagram: ["+resultBytes.length+"] bytes");
					ss.send(packet);
				}
			
			}catch(Exception e){
				logger.error("Error processing data",e);
			}finally{
				callback.processorFinished(threadId);
			}
		}
		
	}
	
	
	private class ServerSocketListener implements Runnable{

		@Override
		public void run() {
			logger.info("Waiting for Connection...");
			try{
				while(!interrupted){
					try {
						byte[] receiveData = new byte[5120];
						DatagramPacket packet = new DatagramPacket(receiveData,receiveData.length);
						ss.receive(packet);
						if(packet.getLength() > 0){
							logger.debug(packet.getLength());
							DataProcessor processor = new DataProcessor(currentId, SocketListener.this, packet);
							processors.put(currentId, processor);
							new Thread(processor).start();
							currentId++;
						}
					} catch (IOException e) {
						if(! (e instanceof SocketTimeoutException)){
							logger.warn("Error receiving Packet from socket",e);
						}
					}
					
				}
			}catch(Exception e){
				logger.fatal("Error with UDP Listener", e);
				e.printStackTrace();
			}finally{
				stopped = true;
			}
		}
	}
	
	
	private class PortListenerAdmin implements Runnable{

		@Override
		public void run() {
			
			logger.info("ADMIN: Starting Admin Thread");
			logger.info("ADMIN: Waiting for admin command");
			while(!stopped){
				try {
					Socket socket = adminSS.accept();
					logger.info("ADMIN: Received connection");
					InputStream is = socket.getInputStream();
					byte[] bytes = new byte[512];
					int read = is.read(bytes);
					is.close();
					byte[] resized = Arrays.copyOf(bytes,read);
					String command = new String(resized);
					logger.info("ADMIN: Command is: "+command);
					if(command.equalsIgnoreCase("shutdown")){
						stop();
					}
				} catch (IOException e) {
					if(! (e instanceof SocketTimeoutException)){
						e.printStackTrace();
					}
				}

			}
		}
		
	}
	
		
	
	
	public static void main(String[] args) throws NumberFormatException, IOException{
		if(args.length < 3){
			System.out.println("Args Missing");
			System.out.println("Start Up Command:");
			System.out.println("java -jar SRIMobile.jar <ipv6 Address> <listenerPort> <adminPort>");
		}else{
			new SocketListener().startListener(args[0], args[1], args[2]);
		}
		
	}
	
	
	public void startListener(String datagramSocketAddress, String listenerPort, String adminPort) throws NumberFormatException, IOException{
//		InetAddress address = Inet6Address.get;

		
		InetAddress tmpAddress = InetAddress.getByName(datagramSocketAddress);
		logger.info("TMP Host Address: "+tmpAddress.getHostAddress());
		byte[] addressBytes = tmpAddress.getAddress();
		InetAddress address = Inet6Address.getByAddress(datagramSocketAddress, addressBytes);
		logger.info("Host Address: "+address.getHostAddress());
		logger.info("Host Address: "+address.getCanonicalHostName());

		
		logger.info("Connecting datagram socket on port: "+listenerPort);
		ss = new DatagramSocket(Integer.parseInt(listenerPort),address);
		
		
		ss.setSoTimeout(10000);
		ssListener = new ServerSocketListener();
		new Thread(ssListener).start();
		
		
		adminSS = new ServerSocket(Integer.parseInt(adminPort));
		adminSS.setSoTimeout(10000);

		PortListenerAdmin adminThread = new PortListenerAdmin();
		new Thread(adminThread).start();		
		
	}

	
	public void stop(){
		interrupted = true;
	}


	@Override
	public void processorFinished(long threadId) {
		processors.remove(threadId);
		
	}

	public synchronized void sendMessage(DatagramPacket packet){
		try {
			ss.send(packet);
		} catch (IOException e) {
			logger.error("Error sending message",e);
		}
	}
	
}
