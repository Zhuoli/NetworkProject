/* this program is used to list 11 columns represents 10 deferent throughput */
import java.util.Scanner;
import java.io.FileWriter;

public class Throughput{
  java.io.File[] inFile = null;
  Scanner[] input = null;
  java.io.File outFile = null;
  java.io.PrintWriter output = null;
  final int SimulateTime = 20;
  final int PacketSize = 1040;
  
  public Throughput() throws Exception{
  	inFile = new java.io.File[4];
	input = new Scanner[4];
	  inFile[0] = new java.io.File("reno/data/throughputInColumn.dat");
	  input[0] = new Scanner(inFile[0]); 
	  inFile[1] = new java.io.File("newReno/data/throughputInColumn.dat");
	  input[1] = new Scanner(inFile[1]); 
	  inFile[2] = new java.io.File("tahoe/data/throughputInColumn.dat");
	  input[2] = new Scanner(inFile[2]); 
	  inFile[3] = new java.io.File("vegas/data/throughputInColumn.dat");
	  input[3] = new Scanner(inFile[3]); 
	outFile = new java.io.File("/throughput.dat");
	output = new java.io.PrintWriter(outFile);

   }
   public void start() throws Exception {

    String inStr = "";
    double ftime = 0.1;
    while(ftime < SimulateTime) {
     String[] strArr = null;
     // calculate ave throughput for each input at ftime
     for(int i = 0; i < 10; i++){
        while(input[i].hasNext()){
         inStr = input[i].nextLine();
	 strArr = inStr.split(" ");
         if(dtime > ftime) {
	    double thruRate = (8.0 * count[i] * PacketSize) / (ftime * 1024 * 1024);
            if(i == 0) {
              output.printf("\n%.2f %2.4f",ftime, thruRate);
	    }
	    else if(i == 9) {
              output.printf(" %2.4f", thruRate);
	    }
	    else
	      output.printf(" %2.4f", thruRate);
	    break;
	 } 
      }
     }
      ftime += 0.10;
    }
    output.close();
 }

   public static void main(String[] argv) throws Exception{
       new Throughput().start();
   }

 }
