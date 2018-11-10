# SuzukiKasami Algorithm

This folder contains the implementation of SuzukiKasami algorithm. The algorithm can be 
found here: https://en.wikipedia.org/wiki/Suzuki%E2%80%93Kasami_algorithm

## Prerequisites
This java code should work on any OS that supports jdk 1.8. This was built and tested on macOS 10.14.

## Building the program
In the below instructions terminal means terminal for Linux or macOS and CMD for Windows.

1. Make sure JDK is installed in your system and the standard javac and java commands are working from terminal.
2. In terminal cd to SuzukiKasami directory.
3. run "javac SuzukiKasami.java"
4. run "java SuzukiKasami <Args>" <br>
    Where the format of <Args> is as follows

    

## Sample Input/Output
$ java SuzukiKasami 5 5 0 0 1 2 2 7 3 7 4 8 <br>
Node: 0 <br>
Node: 1 <br>
Node: 2 <br>
Node: 3 <br>
Node: 4 <br>
numProcesses: 5 <br>
csRequest: (0, 0) <br>
csRequest: (1, 2) <br>
csRequest: (2, 7) <br>
csRequest: (3, 7) <br>
csRequest: (4, 8) <br>
Requesting token from Node: 0 <br>
Node: 0has token. Entering CS <br>
Node: 0 is entering CS <br>
Current time: 1 <br>
Current time: 2 <br>
Requesting token from Node: 1 <br>
Broadcasting message from Node: 1 <br>
Current time: 3 <br>
Node: 0 is exiting CS <br>
Sending token to Node: 1 <br>
Node: 1 is entering CS <br>
Current time: 4 <br>
Current time: 5 <br>
Node: 1 is exiting CS <br>
Current time: 6 <br>
Current time: 7 <br>
Requesting token from Node: 2 <br>
Broadcasting message from Node: 2 <br>
Requesting token from Node: 3 <br>
Broadcasting message from Node: 3 <br>
Current time: 8 <br>
Sending token to Node: 2 <br>
Node: 2 is entering CS <br>
Requesting token from Node: 4 <br>
Broadcasting message from Node: 4 <br>
Current time: 9 <br>
Current time: 10 <br>
Node: 2 is exiting CS <br>
Sending token to Node: 3 <br>
Node: 3 is entering CS <br>
Current time: 11 <br>
Current time: 12 <br>
Node: 3 is exiting CS <br>
Sending token to Node: 4 <br>
Node: 4 is entering CS <br>
Current time: 13 <br>
Current time: 14 <br>
Node: 4 is exiting CS <br>
Current time: 15 <br>
Current time: 16 <br>
Current time: 17 <br>


## Notes

Note that the program runs endlessly. You will have to quit the program with an interrupt like Control+C on macOS. You can use a similar mechanism if you are running on any other OS.

If we dissect the sample input given to the program, the input is <br> 
5 5 0 0 1 2 2 7 3 7 4 8

- The first number denotes that there are 5 processes in the system. 
- The second number denotes that there are 5 CS requests that need to be placed. <br>
- After this, there are 5 pairs of integers where the first number in the pair is the process id and the second number in the pair is the time at which the CS request is to be posted. 


Note that the process ids start from 0.

The program assumes that each process will hold the CS for 3 seconds.