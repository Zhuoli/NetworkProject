set term jpeg 
datafile = "data/throughputInColumn.dat" 
set output 'data/throughput.jpg'
set title "Throughput"
set xlabel "Time"
set ylabel "Mbps"

plot datafile using 1:2 with lines title 'Vegas1', datafile using 1:3 with lines title 'Vegas2'

