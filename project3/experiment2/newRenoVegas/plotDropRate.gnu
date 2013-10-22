set term jpeg 
datafile = "data/dropInColumn.dat" 
set output 'data/dropRate.jpg'
set title "dropRate"
set xlabel "Time"
set ylabel "Drop Numbers"
set yrange [0:200]

plot datafile using 1:2 with lines title 'NewReno', datafile using 1:3 with lines title 'Vegas'

