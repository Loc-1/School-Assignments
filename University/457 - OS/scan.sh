#! /bin/bash

if [[ $3 -eq 0 ]]
then
    find . -name "*$1" -printf "%p %s \n" | sort -n -k 2 | head -n $2
    totalSize=$(find . -name "*$1" -printf "%s \n" | awk '{total+=$1}END{print total}')
    partSize=$(find . -name "*$1" -printf "%p %s \n" | sort -n -k 2 | head -n $2 | awk '{total+=$2}END{print total}')
elif [[ $3 -eq 1 ]]
then
    find . -name "*$1" -printf "%p %s \n" | sort -n -k 2 -r | head -n $2
    totalSize=$(find . -name "*$1" -printf "%s \n" | awk '{total+=$1}END{print total}')
    partSize=$(find . -name "*$1" -printf "%p %s \n" | sort -n -k 2 -r | head -n $2 | awk '{total+=$2}END{print total}')
fi

if [[ $2 -eq 0 ]]
then
    partSize=0
fi

if [[ $totalSize -eq 0 ]]
then
    percent="Undefined"
else
    percent=$(echo "scale=0; $partSize*100/$totalSize" | bc)
fi


echo "Total Size: " $totalSize
echo "Part Size: " $partSize
echo "Percent: " $percent

