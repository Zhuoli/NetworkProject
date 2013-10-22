package pack;

import java.math.*;
public class Calculate {
  
   public static int calculate(String message) {
	   int pre = 0;
	   int post = 0;
	   int result = 0;
	   String operator = null;
	   String expression = null;
	   if(message.matches("cs5700spring2013 STATUS.*")){
		   expression = message.substring(24);
		   System.out.println("The expression is: " + expression );
		   String[] arry = expression.split(" ");
		   
		   for(int i = 0; i < arry.length; i++ ){
			 //  System.out.println("Expression : "+ arry[i]);
		   }
		   if(arry.length != 3) {
			   System.out.println("Split Error, Sys quit!");
			   System.exit(0);
		   }
		  try{
		   pre = Integer.parseInt(arry[0]);
		   post = Integer.parseInt(arry[2]);

		   } catch(Exception e) {
			System.out.println("\n\nExpression Error\n\n");
			System.exit(0);
		   }
		   operator = arry[1].trim();	 
		   System.out.println("Integer pre is:" + pre);
		   System.out.println("Integer post is:" + post);
		   
		   if(operator.endsWith("+")) {
			   result = (pre + post);
			   return result;
		   } else if(operator.endsWith("-")){
			   result  = (pre - post);
			   return result;
		   } else if(operator.endsWith("*")){
			   result = (pre * post);
			   return result;
		   } else if(operator.endsWith("/")){
			   result = (pre / post);
			   return result;
		   } else {
			   System.out.println("Operator can not match!");
			   System.exit(0);
		   }
	   } else {
		   System.out.print("Expression can not match MESSAGES"+"Sys will exit");
		   System.exit(0);
	   }   
	   return 0;
   }
}
