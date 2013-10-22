#include "includes.h"
#include "string.h"
#include "tcp.h"
#define MSS 1024
static char datagram[4096], *pseudogram;
static int sock2send;
static int sock2recv;
struct TCP_header* ptcph;
static char* buf = NULL;
static struct sockaddr_in send_addr,recv_addr;
struct iphdr *iph = NULL;
struct TCP_header *tcph = NULL;
static int seqNum = 0;
static int ackNum = 0;
static destPort = 7070;
static srcPort = 6060;
static adv_window = 1024;
static u_int32_t relative_first_seq = 0;
static char srcaddr[INET_ADDRSTRLEN];
char* indexOfString(char* buf, char* substr)
{
        char* pchr = buf;
        char first_char = substr[0];
        while(pchr != NULL){
                pchr = strchr(pchr,first_char);
                if(pchr == NULL)
                        return NULL;
                if(startswith(substr,pchr)){
                        return pchr;
                }
                pchr++;
        }
        return NULL;
}

int BindSocket(int socket,int portNum){
	struct sockaddr_in *sin = malloc(sizeof(struct sockaddr_in));
	bzero((char *)&sin,sizeof(sin));
	sin->sin_port = htons(portNum);	
	int err = bind(socket, (struct sockaddr * )sin, sizeof(*sin));
	if(err < 0){
		perror("ERROR binding socket.\n");
		exit(1);
	} 
	return 0;
}
int createRaw(int protocol){
	
	int raw_fd = socket(AF_INET,SOCK_RAW,protocol);
	if(raw_fd < 0) {
		perror("Creating socket failled");
		exit(1);
	}
	return raw_fd;
}
//int Connect(e){
// return sockfd
int Connect(char* tgt_ip_addr, int portNum){
	destPort = portNum;	
	srand((unsigned)time(NULL));
	srcPort = 1000 + rand()%63535;
	sock2send = createRaw(IPPROTO_RAW);
	sock2recv = createRaw(IPPROTO_TCP);
	char **pptr = NULL;
	unsigned int OPTION_SIZE = 20;
	memset(datagram,0,4096);
	iph = (struct iphdr *) datagram;
	tcph = (struct TCP_header *) (datagram + sizeof(struct ip));
	struct hostent *host = NULL;

	buf = malloc(1024);
	send_addr.sin_family = AF_INET;
	send_addr.sin_port = htons(destPort);
	send_addr.sin_addr.s_addr = inet_addr(tgt_ip_addr);
	look_for_local_ip(srcaddr);
	printf("Local IP Address:\t%s\n",srcaddr); 
	// ip headG
	iph->ihl = 5;
	iph->version = 4;
	iph->tos = 0;
	iph->tot_len = sizeof(struct iphdr) + sizeof(struct TCP_header);
	iph->id = htonl(54321);
	iph->frag_off = 0;
	iph->ttl = 60;
	iph->protocol = IPPROTO_TCP;
	iph->check = 0;
	iph->saddr = inet_addr(srcaddr);
	iph->daddr = send_addr.sin_addr.s_addr;
	iph->check = csum((unsigned short*)datagram, iph->tot_len);
	three_hand_shake(sock2send,sock2recv,datagram,send_addr);

 return sock2recv;
}

int three_hand_shake(int sock2send,int sock2recv,char* datagram,struct sockaddr_in send_addr)
{
	int one = 1,i = 0;
	const int *val = &one;
	int rcvlen = 0;
	struct TCP_header * tcph = (struct TCP_header *) (datagram + sizeof(struct ip));
	// tcp head
	tcph->source = htons(srcPort);
	tcph->dest = htons(destPort);
	tcph->seq = random();;
	tcph->ack_seq = 0;
	seqNum = tcph->seq;
	tcph->doff = 5;
	tcph->fin = 0;
	tcph->syn = 1;
	tcph->rst = 0;
	tcph->psh = 0;
	tcph->ack = 0;
	tcph->urg = 0;
	tcph->window = htons(adv_window);
	tcph->urg_ptr = 0;
	tcph->offset = 0;
	tcph->check = 0;
	tcph->check = tcp_csum(send_addr,tcph,0,srcaddr);
	if(setsockopt(sock2send, IPPROTO_IP, IP_HDRINCL, val, sizeof(one)) < 0){
		perror("Error setting IP_HDRINCL");
		exit(0);
	}
	do{
	static int count = 0;
	count++;
	if(count == 10){
		perror("Can not connect to host\n");
		exit(0);
	}
	// first hand shake	
	if(sendto(sock2send,datagram,iph->tot_len, 0, (struct sockaddr *)&send_addr, sizeof(send_addr)) < 0) 
	{
		perror("sendto failed");
	}
	printf("first hand shake done\n"); 
	// second hand shake
	if((rcvlen = recv(sock2recv,buf,iph->tot_len,0)) < 0){
		perror("recv failed");
	} else 
	{
		printf("second hand shake done\n"); 
	}
	if(!recv_flag_contains(SYN,buf))
		 handle_response();
	} while(!(recv_flag_contains(SYN,buf) && recv_flag_contains(ACK,buf)));

	// third hand shake

	{
	ptcph = (struct TCP_header *)(buf + sizeof(struct iphdr));

	tcph->source = htons(srcPort);
	tcph->dest = htons(destPort);
	tcph->seq = htonl(ntohl(tcph->seq) + 1);
	tcph->ack_seq = htonl(ntohl(ptcph->seq) + 1); 
	tcph->ack = 1;
	tcph->syn = 0;
	tcph->fin = 0;
	tcph->rst = 0;
	tcph->psh = 0;
	tcph->urg = 0;
	tcph->window = htons(adv_window);
	tcph->urg_ptr = 0;
	tcph->offset = 0;
	/* Check sum */
	tcph->check = 0;
	tcph->check = tcp_csum(send_addr,tcph,0,srcaddr);
	if(sendto(sock2send,datagram,iph->tot_len, 0, (struct sockaddr *)&send_addr, sizeof(send_addr)) < 0) 
	{
		perror("sendto failed");
	} 
	}
	relative_first_seq = ntohl(ptcph->seq); 
	printf("third hand shake done\n"); 
	seqNum = tcph->seq;
	ackNum = tcph->ack_seq;
	return 0;
}
int Close() {
	int rcvlen = 0;	
	struct TCP_header * tcph = (struct TCP_header *) (datagram + sizeof(struct ip));
	iph->tot_len = sizeof(struct iphdr) + sizeof(struct TCP_header) ;
	printf("Goes in Close()\n");
	tcph->seq = seqNum;
	tcph->ack_seq = ackNum; 
	tcph->psh = 0;
	tcph->fin = 1;
	tcph->ack = 1;
	tcph->syn = 0;
	/* Check sum */
	tcph->check = 0;
	tcph->check = tcp_csum(send_addr,tcph,0,srcaddr);
	if(sendto(sock2send,datagram,iph->tot_len, 0, (struct sockaddr *)&send_addr, sizeof(send_addr)) < 0) 
	{
		perror("sendto failed");
	} 
	if((rcvlen = recv(sock2recv,buf,iph->tot_len,0)) < 0){
		perror("recv failed");
	}
	if(ptcph->fin){
		tcph->seq = htonl(ntohl(ptcph->ack));
		tcph->ack_seq = htonl(ntohl(ptcph->seq) + 1); 
		tcph->ack = 1;
		tcph->psh = 0;
		tcph->fin = 0;
		tcph->check = 0;		
		tcph->check = tcp_csum(send_addr,tcph,0,srcaddr);
		if(sendto(sock2send,datagram,iph->tot_len, 0, (struct sockaddr *)&send_addr, sizeof(send_addr)) < 0) 
		{
			perror("sendto failed");
		} 
	
	}	 
	printf("Close succeed!");
}
// retransmit a ACK to re psh packet
int retransmit(char* buf)
{
	u_int16_t datasize;
	struct TCP_header* ptcph = (struct TCP_header *)(buf + sizeof(struct iphdr));
	struct iphdr* iph = (struct iphdr*) buf;
	datasize = ntohs(iph->tot_len) - (sizeof(struct iphdr) + 4 * ptcph->doff);
        iph->tot_len = sizeof(struct iphdr) + sizeof(struct TCP_header) ;
	tcph->seq = ptcph->ack_seq;
 	tcph->ack_seq = htonl(ntohl(ptcph->seq) + datasize); 
	tcph->psh = 0;
	tcph->doff = 5;
	tcph->ack = 1;
	tcph->fin = 0;
 	tcph->rst = 0;
	/* Check sum */
	tcph->check = 0;
	tcph->check = tcp_csum(send_addr,tcph,0,srcaddr);
	if(sendto(sock2send,datagram,iph->tot_len, 0, (struct sockaddr *)&send_addr, sizeof(send_addr)) < 0) 
	{
		perror("sendto failed");
	} 
	printf("Send succss\n");
}
int Write(char* msg,int size){
//	printf("Goes in write function\n");
	struct TCP_header * tcph = (struct TCP_header *) (datagram + sizeof(struct ip));
	char* data = datagram + sizeof(struct iphdr) + sizeof(struct TCP_header);	
	int sendbytes = 0;
	int rcvlen,count = 0;
	tcph->offset = 0;
	tcph->doff = 5;
	tcph->fin = 0;
	tcph->syn = 0;
	tcph->rst = 0;
	tcph->psh = 1;
	tcph->ack = 1;
	tcph->urg = 0;
	tcph->ece = 0;
	tcph->cwr = 0;
	tcph->urg_ptr = 0;
	memcpy(data,msg,size);
	sendbytes = strlen(data);
//	printf("data is \n%s\n,data size is %d\n",data,sendbytes);
	sendbytes = sizeof(struct TCP_header);
//	strcpy(data,buf);
	iph->tot_len = sizeof(struct iphdr) + sizeof(struct TCP_header)  + size;
	tcph->seq = seqNum;
	tcph->ack_seq = ackNum; 
	tcph->check = 0;
	tcph->check = tcp_csum(send_addr,tcph,size,srcaddr);
	do{
	count++;
	if(count > 5){
		perror("duplicate ACKs, program gonna quit\n");
		exit(0);
	}	
	if(sendto(sock2send,datagram,iph->tot_len, 0, (struct sockaddr *)&send_addr, sizeof(send_addr)) < 0) 
	{
		perror("sendto failed");
	}
	if((rcvlen = recv(sock2recv,buf,iph->tot_len,0)) < 0){
		perror("recv failed");
	} else 
	{
		handle_response();
//		printf("Write successfully\n"); 
	}
	}while(not_correct_seq(buf,ackNum) || !recv_flag_contains(ACK,buf));
	seqNum = htonl(ntohl(seqNum) + size);

}
char* Read(char* filename){
	FILE* fp = fopen(filename,"w");
	printf("goes in read function\n**************************************************************************************\n\n\n");
	int data_len = 0;
	int count = 0;
	u_int32_t previousSeq = 1;
	struct TCP_header * tcph = (struct TCP_header *) (datagram + sizeof(struct ip));
	int rcvlen = 0;	
	char* msg = NULL;
	u_int16_t datasize = 0;
	int contentlength = 50000;
	int first_http_pack = 1;
	char* http_buf = NULL;
	char buf[1024];
	struct TCP_header* ptcph = (struct TCP_header *)(buf + sizeof(struct iphdr));
	struct iphdr* iph = (struct iphdr*) buf;
	do{
	  printf("\n\n");
	  memset(buf,0,1024);
	  // Receive packet
   	  if((rcvlen = recv(sock2recv,buf,1024,0)) < 0){
		perror("recv failed");
		exit(0);
	  } 
          if(recv_flag_contains(FIN,buf))
	   {
	     printf("Packet signal SYN, gonna quit\n");
	     break;
	    }

 	  msg = buf + sizeof(struct iphdr) + 4 * ptcph->doff;
//	  printf("\n****************************************\n********************** msg is\n%s\n**********************************\n***************************************\n",msg);
	  printf("recvlen is %d\tseq num is: %d\n",rcvlen,(ntohl(ptcph->seq) - relative_first_seq));
	  if(rcvlen == 0){
		continue;
	  };
	  datasize = ntohs(iph->tot_len) - (sizeof(struct iphdr) + 4 * ptcph->doff);
 	  // if data size greater than zero
	  if(datasize > 0){
		printf("datasize is %d\n",datasize);
		if(previousSeq >= ntohl(ptcph->seq)|| not_correct_seq(buf,ackNum)){
			printf("Got duplicate packet previousSeq is\t%u now seq is \t%u\tdatasize is\t%d\n",(previousSeq - relative_first_seq),(ntohl(ptcph->seq) - relative_first_seq),datasize);
			retransmit(buf);
			continue;
		}
		msg = buf + sizeof(struct iphdr) + 4 * ptcph->doff;
		count = strlen(msg);
		// handle http header
		if(first_http_pack){
			data_len = datasize;
			char* slen = NULL;
//			printf("\nFirst time\n");
//			printf("msg is :\n%s\n",msg);
			first_http_pack = 0;
			if(!(contain_substr(msg,"HTTP/") && contain_substr(msg, "200") && contain_substr(msg,"OK")) )
			{
				printf("HTTP header code is not \"200 OK\", gonna quit!\n");
//				printf("Msg is\n%s\n",msg);
				exit(0);
			}
			slen = indexOfString(msg,"Content-Length");
			if(slen != NULL){
				slen = strchr(slen,':');
				slen++;
				contentlength = atoi(slen);
				printf("Content-Length is :\t %d\n",contentlength);
			} else {
				printf("\ndid not find Content-Length\n\n");
				exit(0);
			}
			printf("First time\n");
			http_buf = malloc(contentlength + 1000);
			memset(http_buf,0,contentlength + 1000);
		}
		strcat(http_buf,msg);
//		printf("BODY\n%s\n",msg);
		previousSeq = ntohl(ptcph->seq);
		printf("previousSeq is %d\n",(previousSeq - relative_first_seq));
	  } else {
		if(ntohl(ackNum) > contentlength){
			printf("got all data from packets: %u bytes, gonna quit\n",(ntohl(ackNum) - relative_first_seq));
			break;
		}
		printf("Received rubbish, go on\n");
		continue;
	  }
	  // Respond ACK
	  iph->tot_len = sizeof(struct iphdr) + sizeof(struct TCP_header) ;
	  tcph->seq = ptcph->ack_seq;
 	  tcph->ack_seq = htonl(ntohl(ptcph->seq) + datasize); 
	  tcph->psh = 0;
	  tcph->doff = 5;
	  tcph->ack = 1;
	  tcph->fin = 0;
 	  tcph->rst = 0;
	  /* Check sum */
	  tcph->check = 0;
	  tcph->check = tcp_csum(send_addr,tcph,0,srcaddr);
	  if(sendto(sock2send,datagram,iph->tot_len, 0, (struct sockaddr *)&send_addr, sizeof(send_addr)) < 0) 
	  {
		perror("sendto failed");
	 } 
	 printf("Send succss\n");
 	ackNum = htonl((ntohl(ackNum) + datasize));	
      }while((!recv_flag_contains(FIN,buf)));
//	printf("receive size is %d\n",count);
//	printf("%s\n",http_buf);
	printf("Read done!\n");
	fwrite(http_buf,1,(contentlength + 1000),fp);
	return http_buf; 
}
int handle_response(){
		struct TCP_header* ptcph = (struct TCP_header *)(buf + sizeof(struct iphdr));
		int rcvlen = 0;	
		struct TCP_header * tcph = (struct TCP_header *) (datagram + sizeof(struct ip));
	int expected_ack = (htonl(ntohl(seqNum) + 1));
	// not correct ack number
	if(not_correct_seq(buf,tcph->ack_seq)){
		perror("packet not in order, gonna quit!");
		iph->tot_len = sizeof(struct iphdr) + sizeof(struct TCP_header) ;
		tcph->seq = ptcph->ack_seq;
		tcph->ack_seq = htonl(ntohl(ptcph->seq) + 1); 
		tcph->psh = 0;
		tcph->doff = 5;
		tcph->ack = 1;
		tcph->fin = 0;
		tcph->rst = 1;
		/* Check sum */
		tcph->check = 0;
		tcph->check = tcp_csum(send_addr,tcph,0,srcaddr);
		if(sendto(sock2send,datagram,iph->tot_len, 0, (struct sockaddr *)&send_addr, sizeof(send_addr)) < 0) 
		{
			perror("sendto failed");
		} 
		exit(0);
	}
	// got fin signal
	else if(recv_flag_contains(FIN,buf)) {
		perror("got fin flag, goes to close\n");
		tcph->seq = htonl(ntohl(ptcph->ack));
		tcph->ack_seq = htonl(ntohl(ptcph->seq) + 1); 
		tcph->rst = 0;
		tcph->syn = 0;
		tcph->fin = 0;
		tcph->ack = 1;
		tcph->psh = 0;
		/* Check sum */
		tcph->check = 0;
		tcph->check = tcp_csum(send_addr,tcph,0,srcaddr);
		if(sendto(sock2send,datagram,iph->tot_len, 0, (struct sockaddr *)&send_addr, sizeof(send_addr)) < 0) 
		{
			perror("sendto failed");
		} 
		if((rcvlen = recv(sock2recv,buf,iph->tot_len,0)) < 0){
			perror("recv failed");
		}
		exit(0);
	
	}
	// got rst signal
	else if(recv_flag_contains(RST,buf)){
		perror("got rst flag, goes to close\n");
		exit(0);
	};
}
