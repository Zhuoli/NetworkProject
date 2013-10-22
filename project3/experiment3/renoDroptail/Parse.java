/* this program is used to parse ns-2 out result */
import java.util.Scanner;
import java.io.FileWriter;
public class Parse{
   java.io.File inFile = null;
   java.io.File  outFile = null;
   java.io.File throughput = null;
   java.io.PrintWriter output = null;
   FileWriter throughputWrite = null;
   final int PacketSize = 1040;
   final int SimulateTime = 20;
   Scanner input = null;
   public Parse(String fileName) throws Exception{
	inFile = new java.io.File("out.tr");
	outFile = new java.io.File("data/" + fileName);
	output = new java.io.PrintWriter(outFile);
	input = new Scanner(inFile);
	throughput = new java.io.File("data/throughput.txt");
	throughputWrite = new FileWriter(throughput,true);
   }
  
   public void start(String CBRrate) throws Exception{
	String inStr = "";
	String[] strArr = null;
	int lastSeqNum = 0;
	double throughputRate = 0;
	// Read data from a file
	while(input.hasNext()){
	  inStr = input.nextLine();
	  strArr = inStr.split(" ");
         if(strArr[0].equals("r") || strArr[0].equals("d")){
             lastSeqNum = Integer.parseInt(strArr[10]);
	     output.println(inStr);
	    }
	}
	throughputRate = 8.0 * ((lastSeqNum * PacketSize) / (double)(SimulateTime * 1024.0 * 1024.0));
	throughputWrite.write(CBRrate + " lastSeqNum: " + lastSeqNum + " TCP_throughput: " + throughputRate + '\n');
	throughputWrite.close();
	input.close();
	output.close();
	inFile.delete();

  }
   public static void main(String[] argv) throws Exception{
	new Parse(argv[0]).start(argv[0]);
   }	

}
