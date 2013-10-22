
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

int main(int argc, char*argv[]){
	int sockfd,recvbytes,sendbyte;
	char buf_head[MAXDATASIZE];
	char* buf_body = NULL;
	struct hostent *host;
	struct sockaddr_in serv_addr;
	char message[1024];
	char *pmessage = message;
	char *msg = "GET / HTTP/1.1\nAccept:text/html,application/xhtml + xml, */*\nHost:";
	char *pbuf = buf_head;
	int body_length;
	if(argc < 2){
		fprintf(stderr,"please enter the server's hostname!\n");
		exit(1);
	}
	if(startswith("http://",argv[1])){
		printf("input url start with http\n");
		argv[1] += strlen("http://");
	}

	memset(message,0,1024);
	memcpy(message,msg,strlen(msg));
	memcpy((message + strlen(msg)),argv[1],strlen(argv[1]));
	memcpy((message + strlen(message)),"\nConnection: Keep-Alive",strlen("\nConnection: Keep-Alive"));
	memcpy((message + strlen(message)),"\n\n",strlen("\n\n"));
	if((host = gethostbyname(argv[1])) == NULL){
		herror("gethostbyname wrong!");
		exit(1);
	}
	if((sockfd = socket(AF_INET,SOCK_STREAM,0)) == -1){
		perror("socket init wrong!");
		exit(1);
	}
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_port = htons(SERVPORT);
	serv_addr.sin_addr = *((struct in_addr *)host->h_addr);
//	serv_addr.sin_addr.s_addr = inet_addr("192.168.1.136");
	bzero(&(serv_addr.sin_zero),8);
//	printf("we are going to connect:\n");	
	// Here three times hand shaking 
	if(connect(sockfd,(struct sockaddr *)&serv_addr,sizeof(struct sockaddr)) == -1){
		perror("connect wrong!");
		exit(1);
	}
//	printf("connect succeed!\n");
	printf("we are going to send:\n%s\n",pmessage);
	while(strlen(pmessage)>0){
	//	printf("next to send: %s\n", pmessage);
		if((sendbyte = send(sockfd,pmessage,strlen(pmessage)+1,0)) < 0){
			perror("send wrong!");
			exit(1);
		}
		pmessage += sendbyte + 1;
	}
	memset(buf_head,0,MAXDATASIZE);
	if(recv(sockfd,buf_head,MAXDATASIZE,0) < 0){
		perror("recv wrong!");
		exit(1);
	} else {
		char* pbuf = strstr(buf_head,"Content-Length:");
		char* clength = NULL;
		printf("http head :\n %s\n",buf_head);
		if(pbuf == NULL){
			perror("Did not find Content-Length:");
			exit(1);
		}
		pbuf += strlen("Content-Length: ");
		clength = strsep(&pbuf,"\n");
		printf("\n\nhttp body length is : %s\n\n",clength);
		body_length = atoi(clength);
		buf_body = malloc(body_length + 1);
	}
		
	printf("Start reading http body:\n");
	pbuf = buf_body;
	while(body_length > 0){
		recvbytes = recv(sockfd,pbuf,MAXDATASIZE,0);
		if(recvbytes <0){
			perror("recv wrong!");
			exit(1);
		}
//		printf("%s\n",buf_body);
//		printf("Done\n\n");
		pbuf += recvbytes;
		body_length -= recvbytes;
	}
//	buf_body[recvbytes] = '\0';
	printf("%s\n",buf_body);
	close(sockfd);
}
