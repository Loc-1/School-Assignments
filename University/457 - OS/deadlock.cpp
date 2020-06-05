/**********************************************
 * Last Name:   Moore
 * First Name:  Lachlan
 * Student ID:  30030228
 * Course:      CPSC 457
 * Tutorial:    01
 * Assignment:  4
 * Question:    Q6
 *
 * File name: count.cpp
 *********************************************/


#include <stdio.h>
#include <ostream>
#include <string.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>
#include <iomanip>
#include <iostream>
#include<bits/stdc++.h> 
  
using namespace std; 

// global vars
int deadlockProcesses[10000];
int deadlockCount = 0;

// the below code is taken from https://www.geeksforgeeks.org/detect-cycle-in-a-graph/ and has been changed slightly to fit the requirements of the assignment
class Graph 
{ 
    int V;    // No. of vertices 
    list<int> *adj;    // Pointer to an array containing adjacency lists 
    bool isCyclicUtil(int v, bool visited[], bool *rs);  // used by isCyclic() 
public: 
    Graph(int V);   // Constructor 
    void addVertex(int x);
    void addEdge(int v, int w);   // to add an edge to graph 
    int getVertex();
    bool isCyclic();    // returns true if there is a cycle in this graph 
}; 
  
Graph::Graph(int V) 
{ 
    this->V = V; 
    // we set this to a large size based on how many max inputs we can be given based on the assignment
    adj = new list<int>[10000000];
    // setting up the deadlockProcesses array
    for (int i = 0; i < 10000; i++) {
        deadlockProcesses[i] = 1000000;
    }
} 
  
void Graph::addEdge(int v, int w) 
{ 
    adj[v].push_back(w); // Add w to v’s list. 
} 
// added for help
void Graph::addVertex(int x) 
{
    this -> V = x;
}
// added for help
int Graph::getVertex()
{
    return this -> V;
}
  
// this has been slightly changed to help find the deadlock processes
bool Graph::isCyclicUtil(int v, bool visited[], bool *recStack) 
{ 
    if(visited[v] == false) 
    { 
        // Mark the current node as visited and part of recursion stack 
        visited[v] = true; 
        recStack[v] = true; 
  
        // Recur for all the vertices adjacent to this vertex 
        list<int>::iterator i; 
        for(i = adj[v].begin(); i != adj[v].end(); ++i) 
        { 
            if ( !visited[*i] && isCyclicUtil(*i, visited, recStack) ) {
                deadlockProcesses[deadlockCount] = *i;
                deadlockCount++;
                return true;
            } 
            else if (recStack[*i]) {
                deadlockProcesses[deadlockCount] = *i;
                deadlockCount++;
                return true;
            } 
        } 
  
    } 
    recStack[v] = false;  // remove the vertex from recursion stack 
    return false; 
} 
  
// Returns true if the graph contains a cycle, else false.
bool Graph::isCyclic() 
{ 
    // Mark all the vertices as not visited and not part of recursion 
    // stack 
    bool *visited = new bool[V]; 
    bool *recStack = new bool[V]; 
    for(int i = 0; i < V; i++) 
    { 
        visited[i] = false; 
        recStack[i] = false; 
    } 
  
    // Call the recursive helper function to detect cycle in different 
    // DFS trees 
    for(int i = 0; i < V; i++) 
        if (isCyclicUtil(i, visited, recStack)) 
            return true; 
  
    return false; 
} 
// this marks the begining of my code

// main function
int main(int argc, char *argv[]) 
{
    bool eof = false;
    // while we are not at the end of file
    while(!eof) {
        string input;
        bool hashFlag = false;
        Graph g(0);
        // check until we get a hash
        while (!hashFlag) {
            getline(cin, input);

            // if the line starts with a # we know we are at the end of a graph
            if (input.find("#") == 0) {
                hashFlag = true;
                if (g.isCyclic()) {
                    cout << "Deadlocked processes: ";
                    // finding the correct processes that are deadlocked
                    size_t n = sizeof(deadlockProcesses)/sizeof(deadlockProcesses[0]);
                    sort(deadlockProcesses, deadlockProcesses+n);
                    for (size_t i = 0; i < n; i++) {
                        if (deadlockProcesses[i] < 1000000) {
                            // print out process
                            cout << deadlockProcesses[i] << " ";
                        }
                    }
                    cout << endl;
                    // reset the deadlockedProcesses
                    for (int i = 0; i < deadlockCount; i++) {
                        deadlockProcesses[i] = 1000000;
                    }

                } else {
                    cout << "Deadlocked processes: none \n";
                }
                // in this case we end the file in that case we dont need a hash but also need to flag we have reached end of graph
            } else if (input.compare("") == 0) {
                eof = true;
                hashFlag = true;
                if (g.isCyclic()) {
                    cout << "Deadlocked processes: ";
                    // finding the correct processes that are deadlocked
                    size_t n = sizeof(deadlockProcesses)/sizeof(deadlockProcesses[0]);
                    sort(deadlockProcesses, deadlockProcesses+n);
                    for (size_t i = 0; i < n; i++) {
                        if (deadlockProcesses[i] < 1000000) {
                            // print process
                            cout << deadlockProcesses[i] << " ";
                        }
                    }
                    cout << endl;
                    // reset the deadlockedProcesses
                    for (int i = 0; i < deadlockCount; i++) {
                        deadlockProcesses[i] = 1000000;
                    }
                } else {
                    cout << "Deadlocked processes: none \n";
                }
                // else case we have not found a # or end of file so we need to add the edge between the resources and porcesses
            } else {
                // set up string stream to find the process and resources and what way their connections are
                istringstream iss(input);
                int v1, v2;
                string arrow;

                // v1 is always process, v2 is always a resource
                iss >> v1 >> arrow >> v2;
                
                // used to differenrate between processes and resources
                v2 = v2 + (1000000);

                if ((v2 > v1) && (v2 > g.getVertex())) {
                    g.addVertex(v2);
                } else if ((v1 > v2) && (v1 > g.getVertex())) {
                    g.addVertex(v1);
                }

                // two edge direction cases
                if (arrow == "->") {
                    g.addEdge(v1, v2);
                } else if (arrow == "<-") {
                    g.addEdge(v2, v1);
                }
                // clear iss stream when done
                iss.clear();
            }
        }
        // found end of file
        if (input.compare("") == 0) {
            // cout << "end of file" << endl;
            // set end of file flag
            eof = true;
        }
    }
    return 0; 
} 