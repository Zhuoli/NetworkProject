# myfirst_ns.tcl
# Create a Simulator

 set ns [new Simulator]

 set mytrace [open out.tr w]
# recording cwnd size
 set windowVsTime [open data/win[lindex $argv 0] w]
 $ns trace-all $mytrace
# Define a procedure finish
 proc finish {} {
 global ns mytrace 
 $ns flush-trace
 close $mytrace
 exit 0 
 }
proc plotWindow {} {
	global ns tcp_1 windowVsTime
	set time 0.03
	set now [$ns now]
	set cwnd [$tcp_1 set cwnd_]

	puts -nonewline $windowVsTime "now: $now \t cwnd: $cwnd \n"
	$ns at [expr $now + $time] "plotWindow"

}

# Create Nodes
 set n1 [$ns node]
 set n2 [$ns node]
 set n3 [$ns node]
 set n4 [$ns node]
 set n5 [$ns node]
 set n6 [$ns node]

# Connect Nodes with Links
 $ns duplex-link $n1 $n2 10Mb 10ms DropTail
 $ns duplex-link $n5 $n2 10Mb 10ms DropTail
 $ns duplex-link $n2 $n3 10Mb 10ms DropTail
 $ns duplex-link $n3 $n4 10Mb 10ms DropTail
 $ns duplex-link $n3 $n6 10Mb 10ms DropTail


set tcp_1 [new Agent/TCP]
set tcp_2 [new Agent/TCP]
set tcp_s1 [new Agent/TCPSink]
set tcp_s2 [new Agent/TCPSink]

$tcp_1 set  window_ 1024
$tcp_1 set maxcwnd_ 8000
$tcp_1 set ssthresh_ 600
$tcp_1 set MWS 80010
$ns attach-agent $n1 $tcp_1
$ns attach-agent $n4 $tcp_s1
$ns connect $tcp_1 $tcp_s1
$tcp_1 set fid_ 1
set ftp_1 [$tcp_1 attach-source FTP]

$tcp_2 set  window_ 1024
$tcp_2 set maxcwnd_ 8000
$tcp_2 set ssthresh_ 600
$tcp_2 set MWS 80010
$ns attach-agent $n5 $tcp_2
$ns attach-agent $n6 $tcp_s2
$ns connect $tcp_2 $tcp_s2
$tcp_2 set fid_ 2
set ftp_2 [$tcp_2 attach-source FTP]

# Create a UDP agent
 set udp [new Agent/UDP]
 $ns attach-agent $n2 $udp
 set null [new Agent/Null]
 $ns attach-agent $n3 $null
 $ns connect $udp $null
 $udp set fid_ 2
 
# Create a CBR traffic source
 set cbr [new Application/Traffic/CBR]
 $cbr attach-agent $udp
 $cbr set type_ CBR
 $cbr set packet_size_ 1000
 $cbr set rate_ [lindex $argv 0]
 $cbr set random_ false



# Schedule events
 $ns at 0.02 "$ftp_1 start"
 $ns at 0.02 "$ftp_2 start"
 $ns at 0.02 "plotWindow"
 $ns at 0.03 "$cbr start"
 $ns at 20.03 "$ftp_1 stop"
 $ns at 20.03 "$ftp_2 stop"
 $ns at 20.03 "$cbr stop"
 
 $ns at 22 "finish"
# Start the simulation
 $ns run

