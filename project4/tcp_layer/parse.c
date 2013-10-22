#include "includes.h"
#include "tcp.h"

struct TCP_header *get_tcph_from_packet(char* buf){

	struct iphdr *iph = NULL;
	struct TCP_header *tcph = NULL;
	
	iph = (struct iphdr *) buf;
	tcph = (struct TCP_header *)(buf + sizeof(struct * iphdr));

	return tcph;
}
