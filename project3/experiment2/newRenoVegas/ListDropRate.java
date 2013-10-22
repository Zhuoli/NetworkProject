/* this program is used to listdcolumns represents 10 deferent throughput */
import java.util.Scanner;
import java.io.FileWriter;

public class ListDropRate{
  java.io.File inFile = null;
  Scanner input = null;
  java.io.File outFile = null;
  java.io.PrintWriter output = null;
  final int SimulateTime = 20;
  
  public ListDropRate(int rate) throws Exception{
	inFile = new java.io.File("data/TCPatCBRrate" + rate +"Mb.tr");
	input = new Scanner(inFile); 
	outFile = new java.io.File("data/dropInColumn.dat");
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
         if(strArr[0].equals("d") && strArr[8].equals("0.0") && strArr[9].equals("3.0"))
	     count[0] ++;
         if(strArr[0].equals("d") && strArr[8].equals("4.0") && strArr[9].equals("5.0"))
	     count[1] ++;
         if(dtime > ftime) {
              output.printf("\n%4.1f %3d %3d",ftime, count[0], count[1]);
	   ftime += 0.1;
	 } 
      }
     }
    output.close();
 }

   public static void main(String[] argv) throws Exception{
       new ListDropRate(Integer.parseInt(argv[0])).start();
   }

 }
