#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "includes.h"
#include <unistd.h>
int main(void){
	char *buf = "hello server, this is client\n";
	int size = strlen(buf);
	Connect("192.168.1.136",7070);
	sleep(2);
	printf("in main,buf is \"%s\", buf size is %d\n",buf,size);
	Write(buf,size);
/*	buf = Read();
	if(buf != NULL){
		printf("Received: %s\n",buf);
	} else {
		printf("Received Null\n");
	};
*/	sleep(2);
	Close();
	return 0;
}
