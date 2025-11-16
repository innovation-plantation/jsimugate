#!/bin/bash
if [ "$0" == "/bin/bash" ] 
then 
  DIR=`dirname $BASH_SOURCE`
else 
  DIR=`dirname $0`
fi
cd $DIR
cd classes/artifacts/jsimugate_jar
./add-splash
echo Added splash screen to jar from `pwd` 
echo To run the jar, use the command:     
echo java -jar jsimugate.jar
