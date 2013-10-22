/* this program is used to list 11 columns represents 10 deferent throughput */
import java.util.Scanner;
import java.io.FileWriter;
import java.util.Hashtable;

public class ListLatency{
  java.io.File inFile = null;
  Scanner input = null;
  java.io.File outFile = null;
  java.io.PrintWriter output = null;
  Hashtable<Integer,Double> latencyTable = null;
  final int SimulateTime = 20;
  
  public ListLatency(String queue_type) throws Exception{
   	latencyTable = new Hashtable<Integer,Double>();
	inFile = new java.io.File("data/TCPat" + queue_type +".tr");
	input = new Scanner(inFile); 
	outFile = new java.io.File("data/latencyInColumn.dat");
	output = new java.io.PrintWriter(outFile);

   }
   public void start() throws Exception {

    String inStr = "";
    double ftime = 0.1;
    double latency = 0;
    while(ftime < SimulateTime) {
     String[] strArr = null;
     // calculate ave throughput for each input at ftime
     while(input.hasNext()){
         inStr = input.nextLine();
	 strArr = inStr.split(" ");
	 double dtime = Double.parseDouble(strArr[1]);
         if(strArr[0].equals("r") && strArr[2].equals("0") &&strArr[3].equals("1"))
	     latencyTable.put(Integer.parseInt(strArr[10]),Double.parseDouble(strArr[1]));
         else if(strArr[0].equals("r") && strArr[2].equals("1") && strArr[3].equals("0")){
             int seqNum = Integer.parseInt(strArr[10]);
	     double time = 0;
	     if(latencyTable.containsKey(seqNum)){
               time = latencyTable.get(seqNum);
	       latencyTable.remove(seqNum);
	       latency = 0.9 * latency + 0.1 * (Double.parseDouble(strArr[1]) - time);
	     }
	}
        if(dtime > ftime) {
            output.printf("\n%5.2f %5.1f",ftime, (latency * 1000));
	   ftime += 0.1;
	 } 
      }
   }
    output.close();
 }

   public static void main(String[] argv) throws Exception{
       new ListLatency(argv[0]).start();
   }

 }
