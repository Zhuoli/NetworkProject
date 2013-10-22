package project2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class ContentParse extends Thread{
	final static boolean DEBUG = false;
    int contentLength = 65535;
    private static int threadCount = 0;
    StateStruct state = null;
    
    
	  public ContentParse(StateStruct st) {
		st.threadCounts++;
		state = st;
	}

	public void run(){
		try{
		  getAllSecretFlags(state);
		}catch(Exception e){
			StateStruct.MaxThreadNumSub1();
			--StateStruct.threadCounts;
		  try{
			FileWriter writer = new FileWriter("crawler.log", true);     
	            writer.write('\n' + e.getMessage() + '\n');   
	        writer.close();
		  }catch(Exception es){
			  
		  }
		}
	  }
	/** extract the cookie  */
	public static void CookieParse(String response, StringBuffer csrftoken, StringBuffer sessionid){
		int csrftokenIndex = response.lastIndexOf("csrftoken=");
		int sessionidIndex = response.lastIndexOf("sessionid=");
		
		String csrftokenString = response.substring(csrftokenIndex);
		String sessionidString = response.substring(sessionidIndex);
		
		csrftokenString = csrftokenString.substring(csrftokenString.indexOf('=') + 1 , csrftokenString.indexOf(';'));
		sessionidString = sessionidString.substring(sessionidString.indexOf('=') + 1 , sessionidString.indexOf(';'));
		
		csrftoken.append(csrftokenString);
		sessionid.append(sessionidString);
	}
	/** pops a url from newURLS, then donload this page and parse it. */
	public void getAllSecretFlags (StateStruct st) throws Exception {
	    // put this url to seenUrls
	    String url = null;
	    try{
	        url = st.newURLsPoll();
	    	if (url == null || url == "") {
	    		if(DEBUG) System.err.println("There has " + st.newURLsSize() + "'s url, the first url is " + st.newURLsPeek() + ", but now"+ "Got a null url, thread terminated");
	    		--st.threadCounts;
	    		return;
	    	}
	    } catch(Exception e){
	    	if(DEBUG) System.err.println("newURLspeek() result is " + url);
	    	if(DEBUG) System.err.println("Failed to pop the newURLs.  Error");
	    	--st.threadCounts;
	    	return;
	    }
	    st.seenURLsPut(url);
		Socket clientSocket  = new Socket("cs5700f12.ccs.neu.edu",80);
	    DataOutputStream outToServer = null;
	    BufferedReader inFromServer = null;
	    
		outToServer = new DataOutputStream(
			clientSocket.getOutputStream());
	    inFromServer = new BufferedReader(new InputStreamReader(
			clientSocket.getInputStream()));
		
	    
	    if(DEBUG) if (outToServer == null) System.err.println("st.outToServer is null");
	    if(DEBUG) if (outToServer == null) System.err.println("outToServer is null");
	    
	    StringBuffer csrftoken = st.csrftoken;
	    StringBuffer sessionid = st.sessionid;
	    String getString = "";
	    String messageFromServer = "";
	    String responseHead = "";
	    String responseBody = "";
	    
	    if(DEBUG) System.out.println("Start searching : " + url);
	    
	    getString = "GET" + " " + url +" " + "HTTP/1.1" + '\n' + 
	    			"Accept: text/html, application/xhtml+xml, */*\n" +
	    			st.Referer + '\n' +
	    			"Accept-Language: en-us\n" +
	    			"User-Agent: Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)\n" +
	    			"Accept-Encoding:\n" +
	    			"Host: cs5700f12.ccs.neu.edu\n" +
	    			"Connection: Keep-Alive\n" +
	    			"Cookie: csrftoken=" + csrftoken + "; sessionid=" + sessionid + '\n' + '\n';
	    st.Referer = "Referer: http://cs5700f12.ccs.neu.edu" + url;
	    if(false) System.out.println('\n' + "Get String is: \n" + getString + '\n');
	    // sending GET 
	    if(false) System.out.println("Start sending get url");
	    try {
			outToServer.writeBytes(getString);
		} catch (IOException e) {
			System.err.println("GET Sending ERROR");
			--st.threadCounts;
			return;
		}
	    if(false) System.out.println("Sending Done");
	    try {
	    	messageFromServer = inFromServer.readLine();
		} catch (IOException e) {
			if(DEBUG) System.err.println("GET Reading ERROR");
			--st.threadCounts;
			return;
		}
		while((messageFromServer != null) && !messageFromServer.matches("")){
			responseHead += '\n' + messageFromServer;
			HeadParse(st,messageFromServer);
			try {
				messageFromServer = inFromServer.readLine();
			} catch (IOException e) {
				if(DEBUG) System.err.println("GET Reading ERROR");
				--st.threadCounts;
				return;
			}
		}
		

		while(contentLength != 0 && (messageFromServer != null) && !messageFromServer.matches("</html>.*")){
			findFlags(st,messageFromServer);
			responseBody += messageFromServer +'\n';
			try {
				messageFromServer = inFromServer.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				--st.threadCounts;
				return;
			}
		}
		BodyParse(st,responseBody);
		if(false) System.out.println("Head Is:\n" + responseHead + '\n' + "Body Is: \n" + responseBody);
		--st.threadCounts;
		if(DEBUG) System.out.println("ThreadNum changed to: " + st.threadCounts);
		outToServer.close();
		inFromServer.close();
		clientSocket.close();
	}
	private  void HeadParse(StateStruct st,String headStr){
		if(headStr.startsWith("Content-Length")) {
			headStr = headStr.substring(headStr.lastIndexOf(":") + 2);
			try{
			   contentLength = Integer.parseInt(headStr);
			} catch(Exception e){
				System.err.println("Integer.parseInt ERR");
				System.exit(0);
			}
		}
		if(headStr.startsWith("Set-Cookie:")){
			
			String str = "";
			if(headStr.startsWith("Set-Cookie: sessionid=")){
				if(DEBUG) System.err.println("sessionid changed\n");
				str = headStr.substring(headStr.indexOf("=") + 1, headStr.indexOf(";"));
				st.sessionid.replace(0, st.sessionid.length(), str);
			}
			if(headStr.startsWith("Set-Cookie: csrftoken=")){
				if(DEBUG) System.err.println("csrftoken changed\n");
				str = headStr.substring(headStr.indexOf("=") + 1, headStr.indexOf(";"));
				st.csrftoken.replace(0, st.csrftoken.length(), str);
			}
		}
	}
	
	/** find new url and find secret flags */
	private  void BodyParse(StateStruct st,String bodyStr){
		//findFlags(st,bodyStr);
		findNewUrls(st,bodyStr);
	}
	/* find the Urls that is 1: not in seenURLs  2: only point to arbitrary domains   */
	private  void findNewUrls(StateStruct st,String bodyStr){
		String url = null;
		String str = bodyStr;
		while(str.contains("href=")){
			str = str.substring(str.indexOf("href="));
			url = str.substring(str.indexOf("href=") + 6,str.indexOf("\">"));
			if(false) System.out.print('\n' + url);
			// if this url is not in seenURLs and not in newURLs and in the domain url, then add it in newURLs
			if(url != null && !st.seenURLsContainsKey(url) && ! st.newURLsContains(url) && url.startsWith("/")) {
				if(DEBUG) System.out.println(url + "	Stored");
				if(!st.newURLsPut(url))
					System.err.println("Failed to store this url: " + url);
			}
			str = str.substring(str.indexOf(">"));
		}
		if(DEBUG) System.err.println('\n' + st.newURLsSize() + "'s urls to be searched");
		if(DEBUG) System.err.println(StateStruct.seenURLsSize() + "'s urls have been searched");
		if(DEBUG) System.out.println("Done!");
	}
	private  void findFlags(StateStruct st,String bodyStr) throws Exception{
		if(bodyStr.contains("FLAG") ){
			if(DEBUG) System.err.println("HAHA find flag!");
			String flagStr =
					bodyStr.substring(bodyStr.indexOf("FLAG:") + 6);
			flagStr = flagStr.substring(0, flagStr.indexOf("<"));
			System.out.println(flagStr);
			FileWriter writer = new FileWriter("secret_flags", true);     
			 writer.write(flagStr + '\n');  
			 writer.close();
			 st.flagsPut(flagStr);
			 if(st.flagsSize() > 4) st.threadStop = true;
			
		}
	}

}


