/* this program is used to list 11 columns represents 10 deferent throughput */
import java.util.Scanner;
import java.io.FileWriter;

public class ListThroughput{
  java.io.File inFile = null;
  Scanner input = null;
  java.io.File outFile = null;
  java.io.PrintWriter output = null;
  final int SimulateTime = 20;
  final int PacketSize = 1040;
  
  public ListThroughput(String queue_type) throws Exception{
	inFile = new java.io.File("data/TCPat" + queue_type +".tr");
	input = new Scanner(inFile); 
	outFile = new java.io.File("data/throughputInColumn.dat");
	output = new java.io.PrintWriter(outFile);

   }
   public void start() throws Exception {

    String inStr = "";
    double ftime = 0.1;
    int[] count = new int[2];
    while(ftime < SimulateTime) {
     String[] strArr = null;
     // calculate ave throughput for each input at ftime
        while(input.hasNext()){
         inStr = input.nextLine();
	 strArr = inStr.split(" ");
	 double dtime = Double.parseDouble(strArr[1]);
         if(strArr[0].equals("r") && strArr[2].equals("2") &&strArr[3].equals("3"))
	     count[0] ++;
         if(strArr[0].equals("r") && strArr[2].equals("2") &&strArr[3].equals("5"))
	     count[1] ++;
         if(dtime > ftime) {
	    double[] thruRate = new double[2];
	    thruRate[0] = (8.0 * count[0] * PacketSize) / (ftime * 1024 * 1024);
	    thruRate[1] = (8.0 * count[1] * PacketSize) / (ftime * 1024 * 1024);
            output.printf("\n%.2f %2.4f %2.4f" ,ftime, thruRate[0], thruRate[1]);
	    ftime +=0.1;
	 } 
      }
    }
    output.close();
 }

   public static void main(String[] argv) throws Exception{
       new ListThroughput(argv[0]).start();
   }

 }
