#include "includes.h"
char* index_of_substr(char* buf, char* substr)
{
	char* pchr = buf;
	char first_char = substr[0];
//	printf("goes index_of_substr\nLooking for\t%s\n\n",substr);
	while(pchr != NULL){
		pchr = strchr(pchr,first_char);
		if(pchr == NULL)
			return NULL;
		if(startswith(substr,pchr)){
//			printf("find sub str:%s\n\ngonna return\n",pchr);
			return pchr;
		}
		pchr++;
	}
	return NULL;
}
int contain_substr(char* buf, char* substr)
{
  char* pchar = index_of_substr(buf,substr);
  if(pchar != NULL)
	return 1;
  return 0;


}
int look_for_local_ip(char* ipaddr)
{
  char buffer[4086];
  FILE *fp;
  char* pchr = buffer;
  if((fp=fopen("localIP.dat","r")) == NULL){
	
	perror("Cannot open file, localIP.dat");
	exit(0);
  };
  fread(buffer,1,4086,fp);
 // get wlan ip addr
 pchr = index_of_substr(pchr,"wlan");
 if(pchr != NULL) {
   pchr = index_of_substr(pchr,"inet addr");	
   pchr = strchr(pchr,':');
   pchr++;
   memcpy(ipaddr,pchr,INET_ADDRSTRLEN);
   if((pchr = strchr(ipaddr,' ')) != NULL)
     *pchr = '\0';
   return 0;
  }else{
	printf("not found wlan");
  }
 // get echo ip addr 
 pchr = buffer;
 pchr = index_of_substr(buffer,"eth");
 if(pchr != NULL) {
   pchr = index_of_substr(pchr,"inet addr");	
   pchr = strchr(pchr,':');
   pchr++;
   memcpy(ipaddr,pchr,INET_ADDRSTRLEN);
   if((pchr = strchr(ipaddr,' ')) != NULL)
     *pchr = '\0';
   return 0;
  }else{
	printf("not found eth");
  }
 perror("Failed find local ip address, gonna quit!");
 exit(0);
 
  return 0;
}



/*
int main(void){
  char* buf = "nihao this is a client test from most of our manangement, northeastern university, aa bbb dd cc dd ee ff gg hhhhh\n";
  char* index = index_of_substr(buf,"aha~");
  char buffer[16];
  if(index != NULL)
	  printf("index str is :\t%s\n",index);
  look_for_local_ip(buffer);
 printf("local ip is:\t%s\n",buffer);
 return 0;
}

*/
