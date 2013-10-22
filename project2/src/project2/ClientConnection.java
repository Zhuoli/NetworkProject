package project2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class ClientConnection {
	  final static boolean DEBUG = false;
	  Socket clientSocket = null;
	  DataOutputStream outToServer = null;
	  BufferedReader inFromServer = null;
	  
	  StringBuffer csrftoken = new StringBuffer();
	  StringBuffer sessionid = new StringBuffer();
	  String  location = "";
	  int contentLength = 65535;
	
	  /** Construction Method */
	  public ClientConnection(String hostName,int PortNum){
		try {
			initialize(hostName,PortNum);
		} catch (Exception e) {
			System.out.println("Can not connected to host");
			System.exit(0);
		} 
	}
	  /** initialize method  */
	  public void initialize(String hostName,int PortNum) throws UnknownHostException, IOException{
		clientSocket = new Socket(hostName,PortNum);
		outToServer = new DataOutputStream(
				clientSocket.getOutputStream());
	    inFromServer = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
	    if(DEBUG) System.out.println("Initialize succeed!");
	}
	
		/** login method to login the fakebook  */
	  public StateStruct login(String username, String password) throws IOException {
	    StateStruct st = new StateStruct();
	    // Get the initial Cookie				
	    getCookie();
		// use the Cookie , username and password to login
		if(!postUsernamePasswd(username,password))
			return null;
		st.csrftoken = this.csrftoken;
		st.location =  this.location;
		st.sessionid = this.sessionid;
		st.Referer = "Referer: http://cs5700f12.ccs.neu.edu/accounts/login/";
		return st;
		
	}
		// Connect to /accounts/login/ and get the initial Cookie
		private void getCookie() throws IOException{
		String messageFromServer = "";
		String response = null;
		File getLoginFile = new File("res/getLogInCookie");
		Scanner getLoginScanner = null;
		String getLoginString = "";
	    getLoginScanner = new Scanner(getLoginFile);
		// get the Http get head
		while(getLoginScanner.hasNext())
			getLoginString += "\n" + getLoginScanner.nextLine();
		// sending get http
		if(DEBUG) System.out.println("Sending Get Message...");
		outToServer.writeBytes(getLoginString + '\n'+'\n');
		if(DEBUG) System.out.println("Sending finished");
		// Reading response
		messageFromServer = inFromServer.readLine();
		response = messageFromServer;
		while((messageFromServer != null) && !messageFromServer.matches("</html>.*")){
			messageFromServer = inFromServer.readLine();
			response += '\n' + messageFromServer;
		}
				
		if(DEBUG) System.out.println("Reading response finished");
		ContentParse.CookieParse(response, csrftoken, sessionid);
		if(DEBUG)  System.out.println("\ncsrftoken and sessionid are: \n" + csrftoken + '\n' + sessionid +'\n');
	}
		// post Username and Passwd
	private boolean postUsernamePasswd(String username, String password) throws IOException{
		String messageFromServer = "";
		// the head of response string
		String responseHead = null;
		// the message body of response http
		String responseBody = null;
		// Open the http head file
		File postLoginFile = new File("res/postLogIn");
		// set Scanner
		Scanner postLoginScanner = null;
		// set string to store the http head
		String postLoginString = "";
		// set string to store the response from server

	    postLoginScanner = new Scanner(postLoginFile);

		// get the Http post head
		while(postLoginScanner.hasNext())
			postLoginString += "\n" + postLoginScanner.nextLine();
		postLoginString += '\n' + "Cookie: csrftoken=" +csrftoken + "; sessionid=" + sessionid + '\n' + '\n' +
				"csrfmiddlewaretoken=" + csrftoken + "&username=" + username + "&password=" + password + "&next=%2Ffakebook%2F" +'\n';
		// sending get http
		if(DEBUG) System.out.println("Sending Post Message...");
		outToServer.writeBytes(postLoginString + '\n');
		if(DEBUG) System.out.println("Post Done\n\n\n");
		// Reading response
		messageFromServer = inFromServer.readLine();
		responseHead = messageFromServer;
		while(!messageFromServer.startsWith("HTTP")){
		        messageFromServer = inFromServer.readLine();
			responseHead = messageFromServer;
		}
		while((messageFromServer != null) && !messageFromServer.matches("")){
			if(DEBUG) System.out.println(messageFromServer + '\n');
			messageFromServer = inFromServer.readLine();
			responseHead += '\n' + messageFromServer;
			loginHeadParse(messageFromServer);
		}
		// return false if it failed to login with the username and password
		if(!responseHead.contains("Location"))
			return false;
		if(DEBUG) System.out.println("HeadResponse finished");
		
		while(contentLength != 0 && (messageFromServer != null) && !messageFromServer.matches("</html>.*")){
			messageFromServer = inFromServer.readLine();
			if(DEBUG) System.out.println(messageFromServer);
			responseBody += '\n' + messageFromServer;
		}
		if(DEBUG) System.out.println("Head Is:\n" + responseHead + '\n' + "Body Is: \n" + responseBody);		
		if(DEBUG) System.out.println("Login Succeed!\n");
		// Close all connections and exit this function
		inFromServer.close();
		outToServer.close();
		clientSocket.close();
		return true;
	}

	private void loginHeadParse(String headStr){
		if(headStr.startsWith("Content-Length")) {
			headStr = headStr.substring(headStr.lastIndexOf(":") + 2);
			try{
				this.contentLength = Integer.parseInt(headStr);
			} catch(Exception e){
				System.err.println("Integer.parseInt ERR");
				System.exit(0);
			}
		}
		if(headStr.startsWith("Location:")) {
			String url = "";
			try{
				url = headStr.substring(headStr.indexOf(":") + 2);
				// extract the url to corresponding address
				if(! url.startsWith("http://cs5700f12.ccs.neu.edu/")){
					System.err.println("Wrong url: " + url);
		    		return;
				}
				url = url.substring(url.indexOf("edu/") + 3);
				this.location = url;
			}catch(Exception e){
				System.err.println("LocationSubstring Error");
				System.exit(0);
			}
		}
		if(headStr.startsWith("Set-Cookie:")){
			
			String str = "";
			if(headStr.startsWith("Set-Cookie: sessionid=")){
				if(DEBUG) System.err.println("sessionid changed\n");
				str = headStr.substring(headStr.indexOf("=") + 1, headStr.indexOf(";"));
				sessionid.replace(0, sessionid.length(), str);
			}
			if(headStr.startsWith("Set-Cookie: csrftoken=")){
				if(DEBUG) System.err.println("csrftoken changed\n");
				str = headStr.substring(headStr.indexOf("=") + 1, headStr.indexOf(";"));
				csrftoken.replace(0, csrftoken.length(), str);
			}
		}
			
	}
}
