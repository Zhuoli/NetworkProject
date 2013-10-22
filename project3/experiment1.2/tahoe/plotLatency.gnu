set term jpeg 
datafile = "data/latencyInColumn.dat" 
set output 'data/latency.jpg'
set title "latency"
set xlabel "Time"
set ylabel "ms"
set yrange [0:200]
set ytics format "%.0f"

plot datafile using 1:2 with lines title '1Mb', datafile using 1:3 with lines title '2Mb', datafile using 1:4 with lines title '3Mb', datafile using 1:5 with lines title '4Mb', datafile using 1:6 with lines title '5Mb', datafile using 1:7 with lines title '6Mb', datafile using 1:8 with lines title '7Mb', datafile using 1:9 with lines title '8Mb', datafile using 1:10 with lines title '9Mb'

