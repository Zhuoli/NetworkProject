/* this program is used to list 11 columns represents 10 deferent throughput */
import java.util.Scanner;
import java.io.FileWriter;
import java.util.Hashtable;

public class ListLatency{
  java.io.File[] inFile = null;
  Scanner[] input = null;
  java.io.File outFile = null;
  java.io.PrintWriter output = null;
  Hashtable<Integer,Double> latencyTable = null;
  final int SimulateTime = 20;
  
  public ListLatency() throws Exception{
  	inFile = new java.io.File[10];
	input = new Scanner[10];
   	latencyTable = new Hashtable<Integer,Double>();
	for(int i = 0; i < 10; i++){
	  inFile[i] = new java.io.File("data/TCPatCBRrate" + (i+1) +"Mb.tr");
	  input[i] = new Scanner(inFile[i]); 
	  }
	outFile = new java.io.File("data/latencyInColumn.dat");
	output = new java.io.PrintWriter(outFile);

   }
   public void start() throws Exception {

    String inStr = "";
    double ftime = 0.1;
    double[] latency = new double[10];
    while(ftime < SimulateTime) {
     String[] strArr = null;
     // calculate ave throughput for each input at ftime
     for(int i = 0; i < 10; i++){
        while(input[i].hasNext()){
         inStr = input[i].nextLine();
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
	       latency[i] = 0.9 * latency[i] + 0.1 * (Double.parseDouble(strArr[1]) - time);
	     }
	}
         if(dtime > ftime) {
            if(i == 0) {
              output.printf("\n%5.2f %5.1f",ftime, (latency[i] * 1000));
	    }
	    else if(i == 9) {
              output.printf(" %5.1f", (latency[i] * 1000));
	    }
	    else
	      output.printf(" %5.1f", (latency[i] * 1000));
	    break;
	 } 
      }
     }
      ftime += 0.10;
    }
    output.close();
 }

   public static void main(String[] argv) throws Exception{
       new ListLatency().start();
   }

 }
