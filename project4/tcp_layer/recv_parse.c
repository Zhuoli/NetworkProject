#include "includes.h"
#include "tcp.h"

int recv_flag_contains(int flg,char *buf) {
	struct TCP_header* ptcph = (struct TCP_header *)(buf + sizeof(struct iphdr));
/*
	if(ptcph->fin) 
		printf("fin\n");
	if (ptcph->syn) 
		printf("syn\n");
	if(ptcph->rst)
		printf("rst\n");
	if(ptcph->psh)
		printf("psh\n");
	if(ptcph->ack)
		printf("ack\n");	
*/	switch(flg)
	{

	case FIN :  return ptcph->fin; break;
	case SYN :  return ptcph->syn; break;
	case RST :  return ptcph->rst; break;
	case PSH :  return ptcph->psh; break;
	case ACK :  return ptcph->ack; break;
	default: return 0;
	}
}
int  not_correct_ack(char* buf, int ackNum){
        struct TCP_header* ptcph = (struct TCP_header *)(buf + sizeof(struct iphdr));
        int recv_ack = ptcph->ack_seq;
        if(recv_ack != ackNum){
                printf("Receive buf not in order, recv ack is %d, expected ack is %d\n", recv_ack,ackNum);
                return 1;
        } else {
//              printf("Receive buf in order, recv ack is %d, expected ack is %d\n", recv_ack,ackNum);
        return 0;
        }
}
int  not_correct_seq(char* buf, int seqNum){

        struct TCP_header* ptcph = (struct TCP_header *)(buf + sizeof(struct iphdr));
        int seq = ptcph->seq;
        if(seq != seqNum){
                printf("Receive buf not in order, recv seq is %d, expected seq is %d\n", seq,seqNum);
                return 1;
        } else {
//              printf("Receive buf in order, recv seq is %d, expected seq is %d\n", seq,seqNum);
        return 0;
        }
}

