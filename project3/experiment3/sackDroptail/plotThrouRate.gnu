set term jpeg 
datafile = "data/throughputInColumn.dat" 
set output 'data/throughput.jpg'
set title "DropTailThroughput"
set xlabel "Time(s)"
set ylabel "Mbps"
set yrange [0:10]
set ytics format "%.0f"

plot datafile using 1:2 with lines title 'sack', datafile using 1:3 with lines title 'CBR'


