set term jpeg 
datafile = "data/latencyInColumn.dat" 
set output 'data/latency.jpg'
set title "latency"
set xlabel "Time"
set ylabel "ms"
set yrange [0:200]
set ytics format "%.0f"

plot datafile using 1:2 with lines title 'NewReno', datafile using 1:3 with lines title 'Reno'

