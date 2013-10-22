vpath %.c tcp_layer 
vpath % http_layer
vpath %.h lib
objects=rawhttpget.o startswith.o  recv_parse.o  csum.o tcp.o look_for_local_ip.o

main : $(objects)
	cc -o main $(objects)

$(objects) : includes.h
tcp.o csum.o tcp.o recv_parse.o : tcp.h 

clean :
	rm *.o main


