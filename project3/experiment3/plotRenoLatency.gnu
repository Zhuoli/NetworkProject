set term jpeg 
droptail = "renoDroptail/data/win5Mb" 
Red = "renoRed/data/win5Mb" 
set output 'Renolatency.jpg'
set title "latency_of_two_queue_type"
set xlabel "Time(s)"
set ylabel "Latency(S)"
plot droptail using 2:4 with lines title 'RenoDropTail', Red using 2:4 with lines title 'RenoRED'

