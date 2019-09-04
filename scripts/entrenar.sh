#!/bin/bash

if [ $# == 5 ]; then
  java -jar $1 --training $3 $4 --stats --episodes $2 --qfunction $5
elif [ $# == 3 ]; then
  java -jar $1 --training --stats --episodes $2 --qfunction $3
elif [ $# == 0 ]; then
  echo "usage:"
  echo "  > entrenar.sh jar-file episodes alpha gamma qfunction-filename"
  echo "  > entrenar.sh jar-file episodes qfunction-filename"
  echo "" 
  echo "formats:"
  echo "  - episodes: Int"
  echo "  - alpha: Double"
  echo "  - gamma: Double"
fi
