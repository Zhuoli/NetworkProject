/* this program is used to listdcolumns represents 10 deferent throughput */
import java.util.Scanner;
import java.io.FileWriter;

public class ListDropRate{
  java.io.File[] inFile = null;
  Scanner[] input = null;
  java.io.File outFile = null;
  java.io.PrintWriter output = null;
  final int SimulateTime = 20;
  
  public ListDropRate() throws Exception{
  	inFile = new java.io.File[10];
	input = new Scanner[10];
	for(int i = 0; i < 10; i++){
	  inFile[i] = new java.io.File("data/TCPatCBRrate" + (i+1) +"Mb.tr");
	  input[i] = new Scanner(inFile[i]); 
	  }
	outFile = new java.io.File("data/dropInColumn.dat");
	output = new java.io.PrintWriter(outFile);

   }
   public void start() throws Exception {

    String inStr = "";
    double ftime = 0.1;
    int[] count = new int[10];
    while(ftime < SimulateTime) {
     String[] strArr = null;
     // calculate ave throughput for each input at ftime
     for(int i = 0; i < 10; i++){
        while(input[i].hasNext()){
         inStr = input[i].nextLine();
	 strArr = inStr.split(" ");
	 double dtime = Double.parseDouble(strArr[1]);
         if(strArr[0].equals("d"))
	     count[i] ++;
         if(dtime > ftime) {
	    int dropRate = count[i];
            if(i == 0) {
              output.printf("\n%4.1f %3d",ftime, dropRate);
	    }
	    else if(i == 9) {
              output.printf(" %3d", dropRate);
	    }
	    else
	      output.printf(" %3d", dropRate);
	    break;
	 } 
      }
     }
      ftime += 0.10;
    }
    output.close();
 }

   public static void main(String[] argv) throws Exception{
       new ListDropRate().start();
   }

 }
