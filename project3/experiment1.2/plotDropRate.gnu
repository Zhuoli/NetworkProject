set term jpeg 
reno = "reno/data/dropInColumn.dat" 
newReno = "newReno/data/dropInColumn.dat" 
tahoe = "tahoe/data/dropInColumn.dat" 
vegas = "vegas/data/dropInColumn.dat" 
set output 'dropRate.jpg'
set title "dropRate_At_CBRrate_9Mb"
set xlabel "Time(s)"
set ylabel "dropped packets"
plot tahoe using 1:10 with lines title 'tahoe', reno using 1:10 with lines title 'reno', newReno using 1:10 with lines title 'newReno', vegas using 1:10 with lines title 'vegas'

