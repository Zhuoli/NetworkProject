package project2;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *  this class stores information to be passed from
 *  a class to the other class
 * @author Administrator
 *
 */
public class StateStruct {

	public static boolean threadStop = false;
    StringBuffer csrftoken = null;
    StringBuffer sessionid = null;
    String Referer = null;
    String  location = null;
    private static int MaxThreadNum = 300;
	// URLs to be searched
	private static Queue<String> newURLs = null;
	// secret flags to be stored
	private static Queue<String> flags = null;
	 // Known URLs
    private static Hashtable seenURLs = null;
	 
	 static int threadCounts = 0;
	 /************* threadNum method **********/
	 public static int MaxThreadNumGet(){
		 return MaxThreadNum;
	 }
	 public static void MaxThreadNumSub1(){
		 final ReadWriteLock lock = new ReentrantReadWriteLock();//read write lock
			lock.writeLock().lock();
			 --MaxThreadNum;
			lock.writeLock().unlock();
	 }
	 public static void MaxThreadNumAdd1(){
		 final ReadWriteLock lock = new ReentrantReadWriteLock();//read write lock
			lock.writeLock().lock();
			 ++MaxThreadNum;
			lock.writeLock().unlock();
	 }
	 /************ flags method ***************/
	 // point to a new address
	 public static void flagsPoint(Queue<String> q){
		 final ReadWriteLock lock = new ReentrantReadWriteLock();//read write lock
			lock.writeLock().lock();
			  flags = q;
			lock.writeLock().unlock();
	 }
	 // add a new elements 
	 public static boolean flagsPut(String str) throws FileNotFoundException{
		final ReadWriteLock lock = new ReentrantReadWriteLock();//read write lock
		boolean state = false;
		lock.writeLock().lock();
		   state = flags.offer(str);
		lock.writeLock().unlock();
		return state;
	 }
	 // returns the size of flags
	 public static int flagsSize(){
	  final ReadWriteLock lock = new ReentrantReadWriteLock();//read write lock
	  int size = 0;
	  lock.writeLock().lock();
	    size = flags.size();
	  lock.writeLock().unlock();
	  return size;
	 }
	 /************ seenURLs method ************/
	 // point to a new address
	public static void seenURLsPoint(Hashtable q){
		final ReadWriteLock lock = new ReentrantReadWriteLock();//read write lock
		lock.writeLock().lock();
		  seenURLs = q;
		lock.writeLock().unlock();
	}
	// add a element 
	 public static void seenURLsPut(String url){
		 final ReadWriteLock lock = new ReentrantReadWriteLock();//read write lock
		 // lock newURLs
		 lock.writeLock().lock();
		 seenURLs.put(url, true);
		 lock.writeLock().unlock();
	}
	 // return true if this url is in seenURLs
	public static boolean seenURLsContainsKey(String url){
		final ReadWriteLock lock = new ReentrantReadWriteLock();//read write lock
		boolean state = false;
		lock.readLock().lock();
		  state = seenURLs.containsKey(url);
		lock.readLock().unlock();
		return state;
	}
	// return the seenURLs' size
	public static int seenURLsSize(){
		final ReadWriteLock lock = new ReentrantReadWriteLock();//read write lock
		int size = 0;
		lock.readLock().lock();
		  size = seenURLs.size();
		lock.readLock().unlock();
		return size;
	}
	 
	 /*********** newURLS Method **************/
	 // add a element 
	 public static boolean newURLsPut(String url){
		 final ReadWriteLock lock = new ReentrantReadWriteLock();//read write lock
		 boolean state = false;
		 try{
		 // lock newURLs
		 lock.writeLock().lock();
		   state = newURLs.offer(url);
		 lock.writeLock().unlock();
		 } catch(Exception e){
			   System.err.println("newURLsPut Error\n" +e.getMessage() );
			   System.exit(0);
		 }
		 return state;
	 }
	 // point to a new address
	 public static void newURLsPoint(Queue<String> q){
		 final ReadWriteLock lock = new ReentrantReadWriteLock();//read write lock
		 // lock newURLs
		 lock.writeLock().lock();
		 newURLs = q;
		 lock.writeLock().unlock();
		 
	 }
	 // Peek a url from newURLs
	 public static String newURLsPeek(){
		 final ReadWriteLock lock = new ReentrantReadWriteLock();//read write lock
		 String str = "";
		 try{
		 // lock newURLs
		 lock.readLock().lock();
		  str = newURLs.peek();
		 lock.readLock().unlock();
		 } catch(Exception e){
			   System.err.println("newURLsPeek Error\n" +e.getMessage() );
			   System.exit(0);
		   }
		 return str;
	 }
	 // pop a url from newURLs
	 public static String newURLsPoll(){
		 final ReadWriteLock lock = new ReentrantReadWriteLock();//read write lock
		 String str = "";
	   try{
		 lock.writeLock().lock();
		  str = newURLs.poll();
		 lock.writeLock().unlock();
	   } catch(Exception e){
		   while(str == null && newURLs.size() > 0)
				  str = newURLs.poll();
		   if (str != null) return str;
		   System.err.println("newURLsPoll Error\n" +"the result of pop is: " + str );
		   System.err.println("the rest size of newURLs is: " + newURLs.size() );
		   System.exit(0);
	   }
		 return str;
	 }
	 // return true if newURLs has this url
	 public static boolean newURLsContains(String url){
		 final ReadWriteLock lock = new ReentrantReadWriteLock();//read write lock
		 boolean flg = false;
	  try{
		 lock.readLock().lock();
		   flg = newURLs.contains(url);
		 lock.readLock().unlock();
	  } catch(Exception e){
		   System.err.println("newURLsContains Error\n" +e.getMessage() );
		   return true;
	   }
		 return flg;
	 }
	 // return the newURLs' size
	 public static int newURLsSize(){
		 final ReadWriteLock lock = new ReentrantReadWriteLock(); 
		 int size = 0;
		 try{
		 lock.readLock().lock();
		   size = newURLs.size();
		 lock.readLock().unlock();
		 } catch(Exception e){
			   System.err.println("newURLsSize Error\n" +e.getMessage() );
			   System.exit(0);
		   }
		 return size;
	 }
	 
	 public static void writeToFile(String fileName,String content){
		 FileWriter writer = null;  
		 final ReadWriteLock lock = new ReentrantReadWriteLock();//read write lock
	        try {     
	        	lock.writeLock().lock();
	            // open a file  true means append content to the file     
	            writer = new FileWriter(fileName, true);     
	            writer.write(content);       
	        } catch (IOException e) {     
	            e.printStackTrace();     
	        } finally {     
	            try {     
	                if(writer != null){  
	                    writer.close();     
	                    lock.writeLock().unlock();
	                }  
	            } catch (IOException e) {     
	                e.printStackTrace();     
	                lock.writeLock().unlock();
	            }     
	        } 
	 }
	
}
