# ChandyMisraHaas Algorithm

This folder contains the implementation of ChandyMisraHaas algorithm. The algorithm can be 
found here: https://en.wikipedia.org/wiki/Chandy-Misra-Haas_algorithm_resource_model.

## Prerequisites
This java code should work on any OS that supports jdk 1.8. This was built and tested on macOS 10.14.

## Building the program
In the below instructions terminal means terminal for Linux or macOS and CMD for Windows.

1. Make sure JDK is installed in your system and the standard javac and java commands are working from terminal.
2. In terminal cd to ChandyMisraHaas directory.
3. run "javac ChandyMisraHaas.java"
4. run "java ChandyMisraHaas <Args>" <br>
    Where the format of <Args> is as follows

    NumProcesses WaitForGraph InitiatorProcess 

    NumProcesses: number of processes in the system <br>
    WaitForGraph: space separated wait for graph. This should be a square matrix entered with ROW MAJOR format 
                  where 1 denotes a process in row depends on a process in column. 0 otherwise. <br>
    InitiatorProcess: The id of the process that initiates the deadlock detection 

## Sample Input/Output
$ java ChandyMisraHaas 2 0 1 0 0 0 <br>
In ChandyMisraHaas Algorithm <br>
Number of processes is 2 <br>
WFG is: <br>
0 1 <br>
0 0 <br>
Initiator process is 0 <br>
Initiating probe... <br>
[(0,0,1)] <br>
No Deadlock has been detected... <br>


# Notes
If we dissect the input given, the input is <br>
2 0 1 0 0 0

- The first argument says that there are 2 processes in the system.
- The 4 arguments that follow denote the wait-for-graph in ROW-MAJOR format.
- The last argument indicates the process id of the process that initiates the deadlock detection.


Note that the process ids start from 0.