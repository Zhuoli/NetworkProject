package project2;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.io.FileWriter;
public class WebCrawler {
	/** Data Definition **************/
	 public static final boolean DEBUG = false;
	// URLs to be searched
	 Queue<String> newURLs;
	// secret flags to be stored
	 Queue<String> flags;
	 // Known URLs
	 Hashtable seenURLs;
	 static String loginUrl = null;
	 String username = "";
	 String password = "";
	 StateStruct st = null;
	 
	/******  WebCrawler Method*************************/
	public WebCrawler(String[] argv){
		// initial global var, get username and password
		WebCrawlerinitialize(argv);
		// set socket connection and call login method
		this.st = WebCrawlerLogin(loginUrl);
		this.st.newURLsPoint(this.newURLs);
		this.st.seenURLsPoint(this.seenURLs);
		this.st.flagsPoint(this.flags);
		this.st.threadCounts = 0;
	}
	/******  WebCrawlerinitialize Method*****************/
	 private void WebCrawlerinitialize(String[] argv) {
	  String url;
	  seenURLs = new Hashtable();
	  newURLs = new LinkedList<String>();
	  flags = new LinkedList<String>();
	  url = loginUrl; 
	  // put the first url in knownURLs
	  seenURLs.put(url,new Integer(1));
	  //System.out.println("Starting search: Initial URL " + url);
	  if(argv.length != 2){
		  System.out.println("Please input correct arguments");
		  System.exit(0);
	  }
	  username = argv[0];
	  password = argv[1];
	  
	 // username = "001198017";
	 // password = "U23QNRWG";
	 }
	 /******  WebCrawlerLogin  Method*****************/
	 private StateStruct WebCrawlerLogin(String loginurl){
		 StateStruct st = null;
		 ClientConnection loginConnect = new ClientConnection(loginurl,80);
		 if(DEBUG) System.out.println("Initial Connection completed"); 
		 try {
           st = loginConnect.login(username, password);
           if(st == null){
        	   System.err.println("Failed to login with the username and password you offered!" + "\nUsername: " + username + "\npassword: " + password);
        	   System.err.println("Program will quit now!");
        	   System.exit(0);
           }
		} catch (Exception e) {
			System.out.println("ClinetConnection.login  ERROR\n\n");
			System.err.println(e.getMessage());
			System.exit(0);
		}
		 return st;
	 }
	 /******  startSearch Method  *****************/
	 // Start search the url
	 private void startSearch(StateStruct conSt){
		 newURLs.offer(conSt.location);
		 
		 while(!st.threadStop && ( st.threadCounts != 0 || newURLs.size() !=0)){
		  // System.out.println("try to add new thread");
		   if(newURLs.size() != 0 && (this.st.threadCounts < st.MaxThreadNumGet())){
		     for(int i = Math.min(newURLs.size(),(st.MaxThreadNumGet() - st.threadCounts)); i > 0; i--)
			 new ContentParse(this.st).start();
		    try{
		     Thread.sleep(100);
		     } catch(Exception e){
		        System.out.println("Can not fall asleep'");
			}
		   } 
		 }	
	        try{	
		 FileWriter writer = new FileWriter("crawler.log",true);
		 writer.write("flagSize is : " + flags.size());
		 writer.write("\nSearching finished\n");
		 writer.close();
		} catch(Exception e){
		}
	//	 System.out.println("Searching finished, please wait for all threads terminated");
	 }
	 
	 public static void main(String[] argv)
		{   
		    loginUrl = "cs5700f12.ccs.neu.edu";
			WebCrawler wc = new WebCrawler(argv);
			wc.startSearch(wc.st);
		}

}
