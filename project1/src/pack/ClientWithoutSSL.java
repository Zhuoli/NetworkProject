package pack;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class ClientWithoutSSL {
	  Socket clientSocket = null;
	  PrintWriter outToServer = null;
	  BufferedReader inFromServer = null;
	  String secretFlag = null;
	  
	// Construction Method
	public ClientWithoutSSL(String hostName,int PortNum,String id){
		try {
			initialize(hostName,PortNum);
			start(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	    	try{
	    		outToServer.close();
	    		inFromServer.close();
	    		clientSocket.close();
			} catch(Exception e){
				System.out.println(e.getMessage());
			}
	    }
	}
	// initialize method
	public void initialize(String hostName,int PortNum) throws IOException{
	      try{
		clientSocket = new Socket(hostName,PortNum);
		} catch (Exception e){
		  System.out.println("Can not connect to target.");
		  System.exit(0);
		}
		OutputStream socketOut = clientSocket.getOutputStream();
		outToServer = new PrintWriter(socketOut,true);
		inFromServer = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		System.out.println("Initialize succeed!");
	}
	// Start to communicate with server
	public void start(String id) throws IOException, InterruptedException {
		  String message = null;
		  String solution = null;
		  PrintWriter output = null; 
		  // Send HelloMessage
		  System.out.println("Sending...");
		  outToServer.println("cs5700spring2013 HELLO "+id);
		  System.out.println("Reading...");
		  message = inFromServer.readLine();
		  try{
		   assert  varify(message) : "\n\nmessage received from the server can not match the MESSAGE\n\n";
		  }catch(AssertionError err){
		    System.out.println(err.getMessage());
		    System.exit(0);
		  }
		  while(!(message.endsWith("BYE"))){
		     solution = String.valueOf(Calculate.calculate(message));
		     System.out.println("The solution is: "+ solution);
	             System.out.println("Sending answer......");
		     outToServer.println("cs5700spring2013 " + solution);
		     System.out.println("Reading MESSAGE......");
    	             message = inFromServer.readLine();
		     try{
		         assert  varify(message) : "\n\nmessage received from the server can not match the MESSAGE\n\n";
		      }catch(AssertionError err){
		         System.out.println(err.getMessage());
		         System.exit(0);
		      }
		  }
		  output = new PrintWriter("secret_flags");
		  secretFlag = message.substring(17,((message.length() - 4)));
		  try{
		   assert (!secretFlag.equals("Unknown_Husky_ID")) : "Server can not identify your Husky ID";
		  System.out.println("Your Secret Flag is:" +secretFlag);
		  output.println(secretFlag);
		  } catch(AssertionError err){
		   System.out.println(err.getMessage());
		  } finally{
		  output.close();
		  clientSocket.close(); 
		 }
	}
        // Varify the validity of message
	private boolean varify(String message){
 	  boolean flag = true;
	  if(!message.startsWith("cs5700spring2013 "))
	     flag = false;
	  return flag;
	}
}
