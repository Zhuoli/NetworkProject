/* this program is used to list 11 columns represents 10 deferent throughput */
import java.util.Scanner;
import java.io.FileWriter;

public class ListThroughput{
  java.io.File[] inFile = null;
  Scanner[] input = null;
  java.io.File outFile = null;
  java.io.PrintWriter output = null;
  final int SimulateTime = 5;
  final int PacketSize = 1040;
  
  public ListThroughput() throws Exception{
  	inFile = new java.io.File[10];
	input = new Scanner[10];
	for(int i = 0; i < 10; i++){
	  inFile[i] = new java.io.File("data/TCPatCBRrate" + (i+1) +"Mb.tr");
	  input[i] = new Scanner(inFile[i]); 
	  }
	outFile = new java.io.File("data/throughputInColumn.dat");
	output = new java.io.PrintWriter(outFile);

   }
   public void start() throws Exception {

    String inStr = "";
    double ftime = 0.1;
    while(ftime < SimulateTime) {
     int[] count = new int[10];
     String[] strArr = null;
     // calculate ave throughput for each input at ftime
     for(int i = 0; i < 10; i++){
        while(input[i].hasNext()){
         inStr = input[i].nextLine();
	 strArr = inStr.split(" ");
	 double dtime = Double.parseDouble(strArr[1]);
         if(strArr[0].equals("r") && strArr[2].equals("2") &&strArr[3].equals("3"))
	     count[i] ++;
         if(dtime > ftime) {
	    double thruRate = (8.0 * count[i] * PacketSize) / (0.1 * 1024 * 1024);
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
       new ListThroughput().start();
   }

 }
