#include "includes.h"
#include <linux/if_ether.h>
#include <netpacket/packet.h>
#include <net/ethernet.h>
#define FIN 0
#define SYN 1
#define RST 2
#define PSH 3
#define ACK 4
// ip header
struct pseudo_header
{
	u_int32_t source_address;
	u_int32_t dest_address;
	u_int8_t placeholder;
	u_int8_t protocol;
	u_int16_t tcp_length;
};
// ether_header
/*struct ETHER_header 
{
//	unsigned u_int8_t destMac[6];
//	unsigned u_int8_t srcMac[6];
	unsigned u_int16_t ether_type;
};*/
// tcp header
struct TCP_header
{
	u_int16_t source;
	u_int16_t dest;
	u_int32_t seq;
	u_int32_t ack_seq;
	u_int16_t offset:4,
		  doff:4,
	      	  fin :1,
		  syn :1,
		  rst :1,
		  psh :1,
		  ack :1,
		  urg :1,
		  ece :1,
		  cwr :1;
	u_int16_t window;
	u_int16_t check;
	u_int16_t urg_ptr;

};
//struct Max_seg_size
struct Option
{
	u_int8_t MSS;
	u_int8_t Length;
	u_int16_t MSS_Value;
};
struct SACK_Permitted
{
	u_int8_t Kind;
	u_int8_t Length;
};
struct Timestamps
{
	u_int8_t Kind;
	u_int8_t Length;
	u_int32_t value;
	u_int32_t reply;
};
	
struct window_scale
{
	u_int8_t windowScale;
	u_int8_t Length;
	u_int8_t shift_count;
};
/*
struct Option
{
	struct Max_seg_size mss;
	struct SACK_Permitted sack;
	struct Timestamps timestamps;
	u_int8_t nop;
	struct window_scale window;
};
*/		
