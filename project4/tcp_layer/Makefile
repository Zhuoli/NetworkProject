test : main.o tcp.o csum.o recv_parse.o
	cc -o test  main.o tcp.o csum.o recv_parse.o
main.o : main.c includes.h
	cc -c main.c
tcp.o : tcp.c  includes.h
	cc -c  tcp.c
csum.o : csum.c includes.h
	cc -c csum.c
recv_parse.o : recv_parse.c
	cc -c recv_parse.c
