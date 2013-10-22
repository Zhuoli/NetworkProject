set term jpeg 
reno = "reno/data/win9Mb" 
newReno = "newReno/data/win9Mb" 
tahoe = "tahoe/data/win9Mb" 
vegas = "vegas/data/win9Mb" 
set output 'throughput.jpg'
set title "Throughput_At_CBRrate_9Mb"
set xlabel "Time(s)"
set ylabel "cwnd size"
plot tahoe using 2:4 with lines title 'tahoe', reno using 2:4 with lines title 'reno', newReno using 2:4 with lines title 'newReno', vegas using 2:4 with lines title 'vegas'

