package pack;

import java.io.BufferedReader;  
import java.io.IOException;
import java.io.InputStreamReader;  
import java.io.PrintWriter;  
import java.net.Socket;  
import java.net.UnknownHostException;
  
import javax.net.SocketFactory;  
import javax.net.ssl.SSLSocketFactory;  

public class ClientWithSSL {
    private static String CLIENT_KEY_STORE = "res/jssecacerts";  
    PrintWriter writer = null;
    BufferedReader reader = null;
    BufferedReader inFromUser = null;
    Socket s = null;
    String solution = null;
	String secretFlag = null;
	String message = null;
	PrintWriter output = null; 
  	
  	public ClientWithSSL(String host,int port,String id) {
  		try {
			initiazler(host,port,id);
			start();
		} catch (Exception e) {
		   e.printStackTrace();
		} finally {
			secretFlag = message.substring(17,((message.length() - 4)));
		         try{
	                   assert (!secretFlag.equals("Unknown_Husky_ID")) : "\nServer can not identify your Husky_ID\n"; 
	                   output.println(secretFlag);
	                   System.out.println("\nYour Secret Flag is:" +secretFlag);
			  } catch(AssertionError err){
		              System.out.println(err.getMessage());
	               	  } finally{
	                  output.close();
	                  writer.close();
	                  try {
		           reader.close();
		 	   s.close(); 
		 	  } catch (IOException e) {
		 	   e.printStackTrace();
		 	  }
		         }
		}
  	}
    private void initiazler(String host,int port,String id) throws Exception{
    	inFromUser = new BufferedReader(
    			new InputStreamReader(System.in));
            // Set the key store to use for validating the server cert.  
    	System.setProperty("javax.net.ssl.trustStore", CLIENT_KEY_STORE);  
    	System.setProperty("javax.net.debug", "ssl,handshake");  
    	ClientWithSSL client = this;  
    	try{
    		s = client.clientWithCert(host,port);
    	}catch (Exception e){
    		System.out.println("\n\n\nCan not connect to the target!\n\n\n");
    		System.exit(0);
    	}
	writer = new PrintWriter(s.getOutputStream());  
    	reader = new BufferedReader(new InputStreamReader(s  
    			.getInputStream()));  
       try{    
	writer.println("cs5700spring2013 HELLO " + id);  
    	writer.flush();  
    	}catch(Exception e){
	 System.out.println("\n\nError happend in initiazler\n\n");
	 reader.readLine();
        }
        System.out.println("Initiation Succeed!");
        }
    
    public void start() throws Exception{
    	output = new PrintWriter("ssl_secret_flags");
        message = reader.readLine();
        try{
	   assert varify(message) : "\n\nmessage received from the server can not match the MESSAGE, program exit\n\n";
	  } catch(AssertionError err){ 
              System.out.println(err.getMessage());
	      System.exit(0);
	  }   
	    
        while(!(message.endsWith("BYE"))) {
          solution = String.valueOf(Calculate.calculate(message));
          System.out.println("The solution is: "+ solution);
          System.out.println("Sending answer......");
          writer.println("cs5700spring2013 " + solution);
          writer.flush();
          System.out.println("Reading MESSAGE......");
          message = reader.readLine();
	  try{
	   assert varify(message) : "\n\nmessage received from the server can not match the MESSAGE, program exit\n\n";
	  } catch(AssertionError err) {
              System.out.println(err.getMessage());
	      System.exit(0);
	  }
        }
        
    }
    // varify the validity of message
    private boolean varify(String message){
         boolean flag = true;
	 if (! message.startsWith("cs5700spring2013 "))
	 	flag = false;
         return flag;
    }
   
    private Socket clientWithCert(String host,int port) throws UnknownHostException, IOException {  
    	SocketFactory sf = SSLSocketFactory.getDefault();  
    	Socket s = sf.createSocket(host,port);
        return s;
    }  

}  
