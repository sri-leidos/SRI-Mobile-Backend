package com.leidos.sri.mobile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class StopListener {

	
	public static void main(String[] args) throws IOException{
		if(args.length < 1){
			System.out.println("Args Missing");
			System.out.println("Start Up Command:");
			System.out.println("java -cp SRIMobile.jar com.leidos.sri.mobile.StopListener <adminPort>");		
		}else{
			Socket s = new Socket(InetAddress.getLocalHost(),Integer.parseInt(args[0]));
			OutputStream os = s.getOutputStream();
			os.write(new String("shutdown").getBytes());
			os.close();	
			s.close();
		}
	}
	
}
