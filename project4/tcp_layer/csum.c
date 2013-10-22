#include "includes.h"
#include "tcp.h"
unsigned short csum(unsigned short *ptr, int nbytes){
        register long sum;
        unsigned short oddbyte;
        register short answer;

        sum = 0;
        while(nbytes > 1){
                sum += *ptr++;
                nbytes -= 2;
        }
        if(nbytes == 1) {
                oddbyte = 0;
                *((u_char*)&oddbyte) = *(u_char*)ptr;
                sum += oddbyte;
        }

        sum = (sum >> 16) + (sum & 0xffff);
        sum = sum + (sum >> 16);
        answer = (short)~sum;
        return (answer);
}
unsigned short tcp_csum(struct sockaddr_in send_addr,struct TCP_header* tcph,int data_size, const char* srcaddr){

	char* pseudogram = NULL;
	int psize = sizeof(struct pseudo_header) + sizeof(struct TCP_header) + data_size;
	pseudogram = malloc(psize);
	struct pseudo_header psh;
	//psh.source_address = inet_addr("192.168.30.128");
	psh.source_address = inet_addr(srcaddr);
	psh.dest_address = send_addr.sin_addr.s_addr;
	psh.placeholder = 0;
	psh.protocol = IPPROTO_TCP;
	psh.tcp_length = htons(sizeof(struct TCP_header) + data_size);
	memcpy(pseudogram,(char*)&psh,sizeof(struct pseudo_header));

	memcpy(pseudogram + sizeof(struct pseudo_header), tcph, sizeof(struct TCP_header) + data_size);
	return (csum((unsigned short*) pseudogram,psize));
};
