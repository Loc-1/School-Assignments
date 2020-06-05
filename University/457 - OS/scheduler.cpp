/**********************************************
 * Last Name:   Moore
 * First Name:  Lachlan
 * Student ID:  30030228
 * Course:      CPSC 457
 * Tutorial:    01
 * Assignment:  4
 * Question:    Q4
 *
 * File name: count.cpp
 *********************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <iostream>
#include <sys/types.h>
#include <sys/stat.h>
#include <string>
#include <sys/stat.h>
#include <map>
#include <cstring>
#include <cstdio>
#include <fstream>
#include <queue>

using namespace std;

// setting up global vars
#define MAX_PROCESSES 30
queue<int> readyQ;
int numOfProcess = 0;
// total CPU running time
int totalBurst = 0;
int statusCount;
int timeQuant;
// the process is running
int currProcess = -1;
int countWait[MAX_PROCESSES];
// 2D-array for the process table
int processTbl[MAX_PROCESSES][2];
// shows the status for the global var
char status[MAX_PROCESSES] = {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};

// function that reads in the config file and sets to the assigned global vars
int readFile(int *processT, char *ar, int *coun)
{
    ifstream read; // declare an input file stream
    read.open(ar); // open the file stream
    // Check that the file was opened
    if (!read)
    { // error checking
        cout << "File not found " << ar << endl;
        exit(-1);
    }
    int i = 0;
    int next = 0; // integer value that represents the result in the file
    while (read >> next)
    {                           // Read from the stream in the same way as cin
        *(processT + i) = next; // insert the read result into the array
        i++;
        if (i % 2 == 0)
        {                         // if the pointer has reached the end of the line,...
            *coun = *coun + next; // calculate the total CPU running tiime by adding the last result in each line
            numOfProcess++;
        }
    }
    read.close(); // close the stream
    return numOfProcess;
}

// function that prints the required header
void printHeader(int processNum)
{
    printf("Time");
    for (int i = 0; i < processNum; i++)
    {
        cout << "\tP" << i;
    }
    printf("\n");
    printf("------------------------------------------------------------\n");
}

// function that prints the required graph body
void printBody(int t)
{
    printf("%d", t);
    for (int i = 0; i < numOfProcess; i++)
    {
        cout << "\t" << status[i];
        if (status[i] == '.')
        { // Denotes the READY state
            countWait[i]++;
        }
        else if (status[i] == '#')
        { // Denotes the RUNNING state
            totalBurst--;
        }
    }
    printf("\n");
}

// function that prints the required footer
void printFooter()
{
    printf("------------------------------------------------------------\n\n");
    int sum = 0;
    for (int i = 0; i < numOfProcess; i++)
    {
        sum = sum + countWait[i]; // sum up the for calculating the average wait time
        cout << "P" << i << "\twaited " << countWait[i] << " sec.\n";
    }
    printf("Average waiting time = ");
    float average = ((float)sum / numOfProcess); // calculate the average waiting time
    printf("%.3f sec.\n", average);
}

// Main Body
int main(int argc, char *argv[])
{
    // handles command line arguments and error checking
    if (argc != 4 && argc != 3)
    {
        fprintf(stderr, "**Only 3 arguments are used**\nUsage: ./<file_name> <configuration_file> <'RR' or 'SJF'> <time_quantum>\n");
        exit(-1);
    }
    else if (strcmp(argv[2], "SJF") != 0 && strcmp(argv[2], "RR") != 0)
    {
        fprintf(stderr, "**Please enter 'RR' or 'SJF'**\n");
        exit(-1);
    }
    else if (strcmp(argv[2], "SJF") == 0 && argc == 4)
    {
        fprintf(stderr, "**SJF needs no quantum input**\n");
        exit(-1);
    }
    else if (strcmp(argv[2], "RR") == 0 && argc == 3)
    {
        fprintf(stderr, "**RR needs a quantum input**\n");
        exit(-1);
    }
    // Round-Robin case
    else if (strcmp(argv[2], "RR") == 0)
    {
        timeQuant = stoi(argv[3]);
        // assign the time quantum to a temp variable
        int timeCounter = timeQuant;
        // read the file
        numOfProcess = readFile((int *)processTbl, argv[1], &totalBurst);
        //print out the graph's header
        printHeader(numOfProcess);
        for (int i = 0; totalBurst > 0; i++)
        { // for loop for analying the processes
            if (currProcess == -1)
            {
                while (statusCount < numOfProcess)
                {
                    if (processTbl[statusCount][0] == i)
                    { // if found a process arrived, record it and push the process to the ready queue
                        readyQ.push(statusCount);
                        status[statusCount] = '.';
                    }
                    if (processTbl[statusCount][0] > i)
                    {
                        break;
                    }
                    statusCount++;
                }
                if (readyQ.size() == 0)
                { // when where is no processes in the running state, print
                    printBody(i);
                }
                else
                { // if the there is process ready, pop from the queue and put it in running state
                    currProcess = readyQ.front();
                    readyQ.pop();
                    timeCounter = timeQuant;
                    processTbl[currProcess][1]--;
                    status[currProcess] = '#';
                    timeCounter--;
                }
            }
            else
            {
                // when running process
                if (timeCounter == 0)
                { // when time slice finishes
                    if (processTbl[currProcess][1] == 0)
                    {
                        status[currProcess] = ' ';
                        currProcess = -1;
                        if (readyQ.size() == 0)
                        { // is no processes in the running state, print
                            printBody(i);
                        }
                        else
                        { // there is process ready, pop from the queue and put it in running state
                            currProcess = readyQ.front();
                            readyQ.pop();
                            timeCounter = timeQuant;
                            processTbl[currProcess][1]--;
                            status[currProcess] = '#';
                            timeCounter--;
                        }
                    }
                    else
                    {
                        status[currProcess] = '.';
                        readyQ.push(currProcess);

                        while (statusCount < numOfProcess)
                        {
                            if (processTbl[statusCount][0] == i)
                            { // if found a process arrived, record it and push the process to the ready queue
                                readyQ.push(statusCount);
                                status[statusCount] = '.';
                            }
                            if (processTbl[statusCount][0] > i)
                            {
                                break;
                            }
                            statusCount++;
                        }
                        // same as previous operation except since there is an existing process
                        currProcess = readyQ.front();
                        readyQ.pop();
                        timeCounter = timeQuant;
                        processTbl[currProcess][1]--;
                        status[currProcess] = '#';
                        timeCounter--;
                    }
                }
                else
                { // the time slice is not finished
                    if (processTbl[currProcess][1] == 0)
                    {
                        status[currProcess] = ' ';
                        currProcess = -1;
                        if (readyQ.size() == 0)
                        { // is no processes in the running state, print
                            printBody(i);
                        }
                        else
                        {
                            // there is process ready, pop from the queue and put it in running state
                            currProcess = readyQ.front();
                            readyQ.pop();
                            timeCounter = timeQuant;
                            processTbl[currProcess][1]--;
                            status[currProcess] = '#';
                            timeCounter--;
                        }
                    }
                    else
                    {
                        processTbl[currProcess][1]--;
                        timeCounter--;
                    }
                }
            }
            // after the time i, prints result in the graph body
            printBody(i);
        }
        // print the footer when finished
        printFooter();

        // Shortest-job-first case
    }
    else if (strcmp(argv[2], "SJF") == 0)
    {
        numOfProcess = readFile((int *)processTbl, argv[1], &totalBurst); // read the file
        printf("Process#: %d, Total burst: %d\n", numOfProcess, totalBurst);
        printHeader(numOfProcess); //print out the graph's header
        for (int i = 0; totalBurst > 0; i++)
        {
            while (statusCount < numOfProcess)
            {
                if (processTbl[statusCount][0] == i)
                { // if found a process arrived, record it
                    status[statusCount] = '.';
                }
                else if (processTbl[statusCount][0] > i)
                {
                    break;
                }
                statusCount++;
            }
            if (currProcess == -1)
            {
                // Finding the the ready queue that arrived first
                int min = -1;
                for (int i = 0; i < statusCount; i++)
                {
                    if (min == -1 && status[i] == '.')
                    {
                        min = i; // if no process is found, then set the readyed process to be the first process
                    }
                    else if (processTbl[i][1] < processTbl[min][1] && status[i] == '.')
                    {
                        min = i; // if the there is processes arrived earlier then the recorded process and it is readyed,the nset that process to be the first process
                    }
                }
                if (min != -1)
                {                      // case in which the first process has been found
                    currProcess = min; // assign the currect process
                    status[currProcess] = '#';
                    processTbl[currProcess][1]--;
                }
            }
            else if (processTbl[currProcess][1] == 0)
            { // the case when the process is finished
                status[currProcess] = ' ';
                currProcess = -1;
                int min = -1;
                for (int i = 0; i < statusCount; i++)
                {
                    if (min == -1 && status[i] == '#')
                    {
                        min = i;
                    }
                    else if (processTbl[i][1] < processTbl[min][1] && status[i] == '.')
                    {
                        min = i;
                    }
                }
                if (min != -1)
                {
                    currProcess = min;
                    status[currProcess] = '#';
                    processTbl[currProcess][1]--;
                }
            }
            else
            {
                processTbl[currProcess][1]--;
            }
            // after the time i, prints te result in the graph body
            printBody(i);
        }
        // print the footer when finished
        printFooter();
    }
    return 0;
}