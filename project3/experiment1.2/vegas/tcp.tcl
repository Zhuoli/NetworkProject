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
	global ns tcp windowVsTime
	set time 0.03
	set now [$ns now]
	set cwnd [$tcp set cwnd_]

	puts -nonewline $windowVsTime "now: $now \t cwnd: $cwnd \n"
	$ns at [expr $now + $time] "plotWindow"

}

# Create Nodes
 set n1 [$ns node]
 set n2 [$ns node]
 set n3 [$ns node]
 set n4 [$ns node]

# Connect Nodes with Links
 $ns duplex-link $n1 $n2 10Mb 10ms DropTail
 $ns duplex-link $n2 $n3 10Mb 10ms DropTail
 $ns duplex-link $n3 $n4 10Mb 10ms DropTail


set tcp [new Agent/TCP/Vegas]
set tcpsink [new Agent/TCPSink]
$tcp set  window_ 1024
$tcp set maxcwnd_ 8000
$tcp set ssthresh_ 600
$tcp set MWS 80010

$ns attach-agent $n1 $tcp
$ns attach-agent $n4 $tcpsink
$ns connect $tcp $tcpsink
$tcp set fod_ 1

set ftp [$tcp attach-source FTP]

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
 $ns at 0.2 "$ftp start"
 $ns at 0.2 "plotWindow"
 $ns at 0.01 "$cbr start"
 $ns at 5.03 "$ftp stop"
 $ns at 5.03 "$cbr stop"
 
 $ns at 6 "finish"
# Start the simulation
 $ns run

