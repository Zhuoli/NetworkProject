#include <stdio.h>
#include <stdlib.h>
#include <string.h>
int split_url(char* url, char* hostname,char* getstr,char* filename)
{
	char* host = hostname;
	char* getbuf = getstr;
	char* substr = url;
	memcpy(host,url,strlen(url));
	getstr = strchr(host,'/');
	if(getstr != NULL){
		memcpy(getbuf,getstr,strlen(getstr) + 1);
		*getstr = '\0';
		getstr = getbuf;
		hostname = host;
		// file name
		while(strlen(substr) > 1){
			filename = substr;
//			printf("Before substr is %s\n",substr);
			substr = strchr(substr,'/');
//			printf("After substr is %s\n",substr);
			if(substr == NULL)
				break;
			substr++;
			if(strlen(substr) == 0)
				filename = "index.html";
		}		
	}else{
	 memcpy(hostname,url,strlen(url) + 1);
	 memcpy(getstr,url,strlen(url) + 1);
//	 memcpy(getstr,"/",2);
	 memcpy(filename,"index.html",strlen("index.html")+1);
	} 
		printf("host name is: %s\ngetstr is :%s\nfilename is :%s\n",hostname,getstr,filename);
	
	
}

/*
int main(void){
 char* str1 = malloc(512);
 char* cp = "Hello world";
 memcpy(str1,cp,strlen(cp)+1); 
 char* str2 = "str2";
 printf("%s\n",strcat(str1,str2));
return 0;
}*/	
