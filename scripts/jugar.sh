#!/bin/bash
if [ $# == 2 ]; then
  java -jar $1 --episodes $2
else
  echo "usage:"
  echo " > jugar.sh jar-file episodes"
  echo ""
  echo "formats:"
  echo " - episode: Integer"
fi
