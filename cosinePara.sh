#!/usr/bin/env bash

for (( a=0; a<=500; a+=10 ))
do
    for (( b=0; b<=500; b+=10 ))
    do
        for (( c=0; c<=500; c+=10 ))
        do
            for (( d=0; d<=500; d+=10 ))
            do
                echo "****************"
                java -Xmx1024m -cp classes edu.stanford.cs276.Rank ./pa3-data/pa3.signal.train baseline ./pa1-data true $a $b $c $d > flowResult.txt
                java -Xmx1024m -cp classes edu.stanford.cs276.NdcgMain flowResult.txt pa3-data/pa3.rel.train 1
                echo "urlweight" $a
                echo "titleweight" $b
                echo "headerweight" $c
                echo "anchorweight" $d
                echo "****************"
            done
        done
    done
done
