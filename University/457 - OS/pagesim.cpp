/**********************************************
 * Last Name:   Moore
 * First Name:  Lachlan
 * Student ID:  30030228
 * Course:      CPSC 457
 * Tutorial:    01
 * Assignment:  5
 * Question:    Q1
 *
 * File name: pagesim.cpp
 *********************************************/

#include <string>
#include <iostream>
#include <fstream>
#include <sstream>
#include <stdio.h>
#include <vector>
#include <unordered_map>

using namespace std;

// global vars
vector<int> reference;
int numOfFrames;

// Otimal algorithm
void Optimal() {
    // initilization
    int numOfFaults = 0;
    int frames[numOfFrames];
    bool repeat = false;
    unordered_map<int, int> frameUsed;

    for (int i = 0; i < numOfFrames; i++) {
        frames[i] = -1;
    }
    // start of algorithm
    for (int i = 0; i < reference.size(); i++) {
        // hit check
        for (int j = 0; j < numOfFrames; j++) {
            if (frames[j] == reference[i]) {
                repeat = true;
            }
        }

        if (repeat == false) {
            for (int j = 0; j < numOfFrames; j++) {
                if (frames[j] == -1) {
                    frames[j] = reference[i];
                    repeat = true;
                    j = numOfFrames;
                    numOfFaults++;
                }
            }
        }
        // no hits all frames are taken
        if (repeat == false) {
            // find when frames will be used in the future
            for (int j = 0; j < numOfFrames; j++) {
                for (int k = i; k < reference.size(); k++) {
                    if (frames[j] != reference[k]) {
                        frameUsed[j] += 1;
                    } else {
                        k = reference.size();
                    }
                }
            }
            int distance = -1;
            int index;

            // check which frame is not being used for the longest time
            for (auto itr = frameUsed.begin(); itr != frameUsed.end(); ++itr) {
                if (distance == -1) {
                    distance = itr -> second;
                    index = itr -> first;
                } else if (distance <= itr -> second) {
                    distance = itr -> second;
                    index = itr -> first;
                }
            }
            frameUsed.clear();
            frames[index] = reference[i];
            numOfFaults++;
        }
        repeat = false;
    }
    // print values
    cout << "- frames: ";
    for (int j = 0; j < numOfFrames; j++) {
        cout << frames[j] << " ";
    }
    cout << endl;
    cout << "- page faults: " << numOfFaults << endl;

}
// LRU algorithm
void LRU() {
    // initilization 
    int numOfFaults = 0;
    int frames[numOfFrames];
    bool repeat = false;
    unordered_map<int, int> frameUsed;

    for (int i = 0; i < numOfFrames; i++) {
        frames[i] = -1;
        frameUsed[i] = 0;
    }

    // start of algorithm
    for (int i = 0; i < reference.size(); i++) {
        // check if we have a hit
        for (int j = 0; j < numOfFrames; j++) {
            if (frames[j] == reference[i]) {
                repeat = true;
                frameUsed[j] = 0;
            }
        }

        if (repeat == false) {
            for (int j = 0; j < numOfFrames; j++) {
                if (frames[j] == -1) {
                    frames[j] = reference[i];
                    repeat = true;
                    j = numOfFrames;
                    numOfFaults++;
                }
            }
        }
        // miss and all frames are taken
        if (repeat == false) {
            int leastUsed = -1;
            int index;
            // find last used frame
            for (auto itr = frameUsed.begin(); itr != frameUsed.end(); ++itr) {
                if (leastUsed == -1) {
                    leastUsed = itr -> second;
                    index = itr -> first;
                } else if (leastUsed <= itr -> second) {
                    leastUsed = itr -> second;
                    index = itr -> first;
                }
            }
            frames[index] = reference[i];
            frameUsed[index] = 0;
            numOfFaults++;
        }
        repeat = false;
        for (auto itr = frameUsed.begin(); itr != frameUsed.end(); ++itr) {
            if (frames[itr -> first] != -1) {
                itr -> second++;
            }
        }
    }
    // print values
    cout << "- frames: ";
    for (int j = 0; j < numOfFrames; j++) {
        cout << frames[j] << " ";
    }
    cout << endl;
    cout << "- page faults: " << numOfFaults << endl;
}
// Clock algorithm
void Clock() {
    int numOfFaults = 0;
    int frames[numOfFrames];
    bool repeat = false;
    int clock[numOfFrames];
    int timer = 0;

    for (int i = 0; i < numOfFrames; i++) {
        frames[i] = -1;
        clock[i] = 0;
    }
    // start of algorithm
    for (int i = 0; i < reference.size(); i++) {
        for (int j = 0; j < numOfFrames; j++) {
            if (frames[j] == reference[i]) {
                repeat = true;
                clock[j] = 1;
            }
        }
        // do clock cycle to find which value to remove
        while (repeat == false) {
            if (clock[timer] == 0) {
                frames[timer] = reference[i];
                clock[timer] = 1;
                numOfFaults++;
                if (timer < numOfFrames-1) {
                    timer++;
                } else {
                    timer = 0;
                }
                repeat = true;
            } else {
                if (clock[timer] == 1) {
                    clock[timer] = 0;
                }
                if (timer < numOfFrames-1) {
                        timer++;
                } else {
                    timer = 0;
                }
            }
        }
        repeat = false;
    }
    // print values
    cout << "- frames: ";
    for (int j = 0; j < numOfFrames; j++) {
        cout << frames[j] << " ";
    }
    cout << endl;
    cout << "- page faults: " << numOfFaults << endl;
}

// main
int main (int argc, char *argv[]) {
    string input = "";
    numOfFrames = atoi(argv[1]);

    while(getline(cin, input)) {
        stringstream stream(input);
        int pageNum;
        while (!stream.eof()) {
            stream >> pageNum;
            reference.push_back(pageNum);
            stream.get();
        }
    }
    // run each algorithm 
    cout << "Optimal:" << endl;
    Optimal();
    cout << "LRU:" << endl;
    LRU();
    cout << "Clock:" << endl;
    Clock();
}
