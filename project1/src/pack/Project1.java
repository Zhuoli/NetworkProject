package pack;
import java.io.*;
public class Project1 {

	public static void main(String[] args) {
	  int  port = 27993; 
	  String host = null;
	  String id = null;
	  BufferedReader inFromUser = new BufferedReader(
					new InputStreamReader(System.in));
	 if (args.length >= 5 ) {
           try{
	     assert ((args[1].equals("-p"))&&(args[3].equals("-s"))) : "Input can not match!";
	     host = args[0];
	     port = Integer.parseInt(args[2]);
	     id = args[4];
	    ClientWithSSL client = new ClientWithSSL(host,port,id);
	     } catch(Exception err){
		System.out.println("Inputs can not match!\nPlease input: ./client [hostname] <-p port> <-s> [NEU ID]");
                System.exit(0);
	     }
	 }
  	 else if(args.length == 4 ) {
	   try{
	     assert args[1].equals("-p") : "Input can not match!";
	     host = args[0];
	     port = Integer.parseInt(args[2]);
	     id = args[3];
	     ClientWithoutSSL client = new ClientWithoutSSL(host,port,id);
	     } catch(Exception err){
		System.out.println("Inputs can not match!\nPlease input: ./client [hostname] <-p port> <-s> [NEU ID]");
                System.exit(0);
	    }
	 }
	// Default SSL port
         else if(args.length == 3) {
	   try{
	     assert args[1].equals("-s") : "Input can not match!";
	     host = args[0];
	     port = 27994;
	     id = args[2];
	     ClientWithSSL client = new ClientWithSSL(host,port,id);
	   } catch(Exception err){
		System.out.println("Inputs can not match!\nPlease input: ./client [hostname] <-p port> <-s> [NEU ID]");
                System.exit(0);
	   }
	 } 
	// Defaykt Without SSL port
	 else if(args.length == 2) {
	   host = args[0];
	   port = 27993;
	   id = args[1];
	   ClientWithoutSSL project1 = new ClientWithoutSSL(host,port,id);
         } 
	 else {
		System.out.println("Inputs can not match!\nPlease input: ./client [hostname] <-p port> <-s> [NEU ID]");
                System.exit(0);
	 }
	}

}
