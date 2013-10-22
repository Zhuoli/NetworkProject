#include <stdio.h>
#include <stdlib.h>
#include <string.h>
int startswith(char* head, char* src){
	
	if( !memcmp(head,src,strlen(head))){
		
		return 1;
	} else {
	
		return 0;
	}
}
