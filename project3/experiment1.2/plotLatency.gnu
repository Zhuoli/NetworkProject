set term jpeg 
reno = "reno/data/latencyInColumn.dat" 
newReno = "newReno/data/latencyInColumn.dat" 
tahoe = "tahoe/data/latencyInColumn.dat" 
vegas = "vegas/data/latencyInColumn.dat" 
set output 'latency.jpg'
set title "latency_At_CBRrate_9Mb"
set xlabel "Time(s)"
set ylabel "Latency(s)"
plot tahoe using 1:10 with lines title 'tahoe', reno using 1:10 with lines title 'reno', newReno using 1:10 with lines title 'newReno', vegas using 1:10 with lines title 'vegas'

