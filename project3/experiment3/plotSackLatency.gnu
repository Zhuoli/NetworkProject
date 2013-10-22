set term jpeg 
droptail = "sackDroptail/data/win5Mb" 
Red = "sackRed/data/win5Mb" 
set output 'Sacklatency.jpg'
set title "latency_of_two_queue_type"
set xlabel "Time(s)"
set ylabel "Latency(S)"
plot droptail using 2:4 with lines title 'SackDropTail', Red using 2:4 with lines title 'SackRED'

