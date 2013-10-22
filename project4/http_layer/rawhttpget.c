
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <string.h>
#define SERVPORT 80 
#define MAXDATASIZE 1024

static char* hostname = NULL;
static char* getstr = NULL;
static char* filename = NULL;
int main(int argc, char*argv[]){
	int sockfd,recvbytes,sendbyte;
	char buf_head[MAXDATASIZE];
	char* buf_body = NULL;
	struct hostent *host;
	struct sockaddr_in serv_addr;
	char message[1024] = "GET ";
	char *pmessage = message;
	char *msg = " HTTP/1.1\nAccept: text/html, application/xhtml + xml, */*\nAccept-Language: en-US\nHost: ";
	char *msg2 = "\nConnection: Keep-Alive\n\n";
	char *pbuf = buf_head;
	char **pptr = NULL;
	char str[INET_ADDRSTRLEN];
	int body_length;
	if(argc < 2){
		fprintf(stderr,"please enter the server's hostname!\n");
		exit(1);
	}
	if(startswith("http://",argv[1])){
		printf("input url start with http\n");
		argv[1] += strlen("http://");
	}
	split_url(argv[1],hostname,getstr,filename);
	strcat(message,getstr);
	strcat(message,msg);
	strcat(message,hostname);
	strcat(message,msg2);
	printf("http head is\n%s\n",message);
	printf("host is %s\nparsing ...",hostname);
	// get host's ip address
	if((host = gethostbyname(hostname)) == NULL){
		herror("gethostbyname wrong!");
		exit(1);
	}
	printf("official host name :%s \n",host->h_name);
	for(pptr = host->h_aliases; *pptr != NULL; pptr++)
		printf("\talias: %s\n",*pptr);
	pptr = host->h_addr_list;
	inet_ntop(host->h_addrtype,*pptr,str,sizeof(str));
	printf("\taddress: %s \n",str);

	Connect(str,80);
//	Connect("192.168.1.136",8080);
	Write(message,strlen(message));
	Read(filename);
	sleep(6);
	Close();
	return 0;
}

int split_url(char* url)
{
        char* host = malloc(strlen(url) + 1);
        char* getbuf = malloc(strlen(url) + 1);
        char* substr = url;
        memcpy(host,url,strlen(url) + 1);
        getstr = strchr(host,'/');
        if(getstr != NULL){
                memcpy(getbuf,getstr,strlen(getstr) + 1);
                *getstr = '\0';
                getstr = getbuf;
                hostname = host;
                // file name
                while(strlen(substr) > 1){
                        filename = substr;
//                      printf("Before substr is %s\n",substr);
                        substr = strchr(substr,'/');
//                      printf("After substr is %s\n",substr);
                        if(substr == NULL)
                                break;
                        substr++;
                        if(strlen(substr) == 0)
                                filename = "index.html";
                }
        }else{
         hostname = url;
	 getstr = "/";
	 filename = "index.html";
        } 
                printf("host name is: %s\ngetstr is :%s\nfilename is :%s\n",hostname,getstr,filename);
        
        
}

