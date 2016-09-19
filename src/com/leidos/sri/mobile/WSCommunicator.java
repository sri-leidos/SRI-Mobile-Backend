package com.leidos.sri.mobile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import sri.data.TruckFeed;
import sri.data.WeightReport;

public class WSCommunicator {
	private Logger logger = Logger.getLogger(WSCommunicator.class);	
	private static final String APPROACH_URL = "http://sri.leidosweb.com/DashCon/resources/truck/approachEnter";
	private static final String WIM_ENTER_URL = "http://sri.leidosweb.com/DashCon/resources/truck/wimEnter";
	private static final String WIM_EXIT_URL = "http://sri.leidosweb.com/DashCon/resources/truck/wimLeave";

//	private static final String APPROACH_URL = "http://cassadyja2:8080/DashCon/resources/truck/approachEnter";
//	private static final String WIM_ENTER_URL = "http://cassadyja2:8080/DashCon/resources/truck/wimEnter";
//	private static final String WIM_EXIT_URL = "http://cassadyja2:8080/DashCon/resources/truck/wimLeave";
	
	
	private HttpClient client = null;
	
	public WSCommunicator(){
		client = HttpClientBuilder.create().build();
	}
	
	
	private HttpResponse callWebService(String url, String xml) throws UnsupportedEncodingException{
		
		logger.debug("Starting call");
		client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);

        httpPost.setHeader("Content-Type", "application/xml");
        httpPost.setHeader("Accept", "application/xml");

        
        ByteArrayEntity baEntity = new ByteArrayEntity(xml.getBytes("UTF8"));
        baEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/xml"));
        httpPost.setEntity(baEntity);
        
        logger.debug("Executing post");
        try {
			HttpResponse httpResponse = client.execute(httpPost);
			
			logger.debug("Have response");
			
            
            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {
                entity.getContentLength();

                Header[] headers = httpResponse.getAllHeaders();
                for (Header header : headers) {
                    logger.debug(
                        "headers -> Key : " + header.getName() +
                        " ,Value : " + header.getValue());
                }
            }
			
			return httpResponse;
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
	}
	
	
	public int callApproachWebService(String xml){
		try {
			HttpResponse httpResponse = callWebService(APPROACH_URL, xml);
			if(httpResponse != null){
				int code = httpResponse.getStatusLine().getStatusCode();
				long length = httpResponse.getEntity().getContentLength();
				
				if(code == 200){
					InputStream is = httpResponse.getEntity().getContent();

					
					
					JAXBContext jc = JAXBContext.newInstance(TruckFeed.class);
			        // Create the Marshaller Object using the JaxB Context
					Unmarshaller um = jc.createUnmarshaller();
					TruckFeed tf = (TruckFeed)um.unmarshal(is);
					return tf.getId();
					
//					byte[] bytes = new byte[(int)length]; 
//					is.read(bytes, 0, (int)length);
//					String s = new String(bytes);
//					JSONObject o = new JSONObject(s);
//					String result = o.getString("id");
//					logger.debug("Decoding id: "+result);
//					
//					if(result != null && !result.equals("")){
//						return Integer.parseInt(result);
//					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public boolean callWimEnterWebService(String xml){
		try {
			HttpResponse httpResponse = callWebService(WIM_ENTER_URL, xml);
			if(httpResponse != null){
				int code = httpResponse.getStatusLine().getStatusCode();
				if(code == 200){
					return true;
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("Error with WIM Enter call",e);
		}
		return false;
	}
	
	public boolean callWimExitWebService(String xml){
		try {
			HttpResponse httpResponse = callWebService(WIM_EXIT_URL, xml);
			if(httpResponse != null){

				int code = httpResponse.getStatusLine().getStatusCode();
				if(code == 200){
					InputStream is = httpResponse.getEntity().getContent();
					long length = httpResponse.getEntity().getContentLength();
					
					JAXBContext jc = JAXBContext.newInstance(WeightReport.class);
			        // Create the Marshaller Object using the JaxB Context
					Unmarshaller um = jc.createUnmarshaller();
					WeightReport wr = (WeightReport)um.unmarshal(is);
					
					
					if(wr != null && wr.getStatus() != null){
					
//					byte[] bytes = new byte[(int)length]; 
//					is.read(bytes, 0, (int)length);
//					String s = new String(bytes);
//					JSONObject o = new JSONObject(s);
//					String result = o.getString("status");
//					logger.debug("Decoding weight result: "+result);
					
						if(wr.getStatus().equalsIgnoreCase("P")){
							logger.debug("Sending weight passed");
							return true;
						}else{
							logger.debug("Sending weight failed");
							return false;
						}
//					}else{
//						logger.info("Weight Report not received");
//					}
					}else{
						logger.warn("Web Service returned non 200 result.");
					}
				}		
			}
		} catch (UnsupportedEncodingException e) {
			logger.error("Error with WIM Exit call",e);
		} catch (UnsupportedOperationException e) {
			logger.error("Error with WIM Exit call",e);
		} catch (IOException e) {
			logger.error("Error with WIM Exit call",e);
		} catch (JAXBException e) {
			logger.error("Error with WIM Exit call",e);
		} catch (Exception e){
			logger.error("Error with WIM Exit call",e);
		}
		return false;
	}
	
	
	
	
	
	
	
	

}
