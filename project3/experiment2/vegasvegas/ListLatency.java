/* this program is used to list 11 columns represents 10 deferent throughput */
import java.util.Scanner;
import java.io.FileWriter;
import java.util.Hashtable;

public class ListLatency{
  java.io.File inFile = null;
  Scanner input = null;
  java.io.File outFile = null;
  java.io.PrintWriter output = null;
  Hashtable<Integer,Double>[] latencyTable =new Hashtable[2];
  final int SimulateTime = 20;
  
  public ListLatency(int rate) throws Exception{
   	latencyTable[0] = new Hashtable<Integer,Double>();
   	latencyTable[1] = new Hashtable<Integer,Double>();
	inFile = new java.io.File("data/TCPatCBRrate" + rate +"Mb.tr");
	input = new Scanner(inFile); 
	outFile = new java.io.File("data/latencyInColumn.dat");
	output = new java.io.PrintWriter(outFile);

   }
   public void start() throws Exception {

    String inStr = "";
    double ftime = 0.1;
    double[] latency = new double[2];
    while(ftime < SimulateTime) {
     String[] strArr = null;
     // calculate ave throughput for each input at ftime
     while(input.hasNext()){
         inStr = input.nextLine();
	 strArr = inStr.split(" ");
	 double dtime = Double.parseDouble(strArr[1]);
         if(strArr[0].equals("r") && strArr[2].equals("0") &&strArr[3].equals("1"))
	     latencyTable[0].put(Integer.parseInt(strArr[10]),Double.parseDouble(strArr[1]));
         else if(strArr[0].equals("r") && strArr[2].equals("4") &&strArr[3].equals("1"))
	     latencyTable[1].put(Integer.parseInt(strArr[10]),Double.parseDouble(strArr[1]));
         else if(strArr[0].equals("r") && strArr[2].equals("1") && strArr[3].equals("0")){
             int seqNum = Integer.parseInt(strArr[10]);
	     double time = 0;
	     if(latencyTable[0].containsKey(seqNum)){
               time = latencyTable[0].get(seqNum);
	       latencyTable[0].remove(seqNum);
	       latency[0] = 0.9 * latency[0] + 0.1 * (Double.parseDouble(strArr[1]) - time);
	     }
	}
        else if(strArr[0].equals("r") && strArr[2].equals("1") && strArr[3].equals("4")){
             int seqNum = Integer.parseInt(strArr[10]);
	     double time = 0;
	     if(latencyTable[1].containsKey(seqNum)){
               time = latencyTable[1].get(seqNum);
	       latencyTable[1].remove(seqNum);
	       latency[1] = 0.9 * latency[1] + 0.1 * (Double.parseDouble(strArr[1]) - time);
	     }
	}
         if(dtime > ftime) {
            output.printf("\n%5.2f %5.1f %5.1f",ftime, (latency[0] * 1000), (latency[1] * 1000));
	   ftime += 0.1;
	 } 
      }
   }
    output.close();
 }

   public static void main(String[] argv) throws Exception{
       new ListLatency(Integer.parseInt(argv[0])).start();
   }

 }
