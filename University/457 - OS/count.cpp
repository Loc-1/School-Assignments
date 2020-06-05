/**********************************************
 * Last Name:   Moore
 * First Name:  Lachlan
 * Student ID:  30030228
 * Course:      CPSC 457
 * Tutorial:    01
 * Assignment:  3
 * Question:    Q2
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

using namespace std;

// global vars 
bool prime = true;
int nThreads = 1;
// setting up mutex and condition var
pthread_mutex_t pmutex = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t cond = PTHREAD_COND_INITIALIZER;
// struct to pass into thread
struct thread_args {
    int64_t number;
    int64_t start;
    int64_t end;
};


// while loop in isPrime threading 
void *whileLoopHelper (void *ptr) {
    // lock to set vars
    pthread_mutex_lock(&pmutex);
    struct thread_args *args = (struct thread_args*)ptr;
    int64_t i = args->start;
    int64_t max = args->end;
    int64_t inputNum = args->number;
    // unlock when done
    pthread_cond_signal(&cond);
    pthread_mutex_unlock(&pmutex);

    // same function as isPrime while loop
    while (i <= max) {
        if (prime == false) {
            // cout << "not prime" << endl;
            pthread_exit(0);
        } else if (inputNum % i == 0 || inputNum % (i + 2) == 0) {
            prime = false;
            // cout << "not prime while" << endl;
            pthread_exit(0);
        }
        i += 6;
    }
    pthread_exit(0);
}


/// primality test, if n is prime, return 1, else return 0
int isPrime(int64_t n)
{
    // set vars 
    // prime is set true incase it passes all checks (then is prime)
    prime = true;
    int64_t currentI = 5;
    int64_t totalEle = 0;
    // lock threads
    pthread_mutex_lock(&pmutex);
    // more var setting
    pthread_t id[nThreads];
    struct thread_args threadVars;
    int64_t max = ceil(sqrt(n));
    // prime checking
     if( n <= 1) {
         prime = false; // small numbers are not primes
         pthread_mutex_unlock(&pmutex);
     } else if( n <= 3 || n == 5 || n == 7) { 
         // added quick checks for 5 and 7 as they are primes
         prime = true; // 2, 3, 5 and 7 are prime
         pthread_mutex_unlock(&pmutex);
     } else if( n % 2 == 0 || n % 3 == 0) {
         prime = false; // multiples of 2 and 3 are not primes
         pthread_mutex_unlock(&pmutex);
     } else {
         // loop for creating n threads
         for (int j = 0; j < nThreads; j++) {
             // dividing up the range of each thread
            if (j != nThreads - 1 || nThreads == 1) {
                totalEle = ceil(double(max)/double(nThreads));
                threadVars.number = n;
            } else {
                totalEle = floor(double(max)/double(nThreads));
                threadVars.number = n;
            }
            while ((currentI - 5) % 6 != 0) {
                currentI++;
            }

            // set threads values for range
            threadVars.start = currentI;
            threadVars.end = (currentI + totalEle);
            // creating threads
            long status = pthread_create(&id[j], NULL, whileLoopHelper, (void*)&threadVars);
            // set to next pos
            currentI += totalEle;
            // error checking
            if (status) {
                printf("Error when creating thread\n");
                exit(0);
            }
            // unlock when done
            pthread_cond_wait(&cond, &pmutex);
            pthread_mutex_unlock(&pmutex);
         }
         // rejoin threads
         for (int j = 0; j < nThreads; j++) {
             pthread_join(id[j], NULL);
         }
         // cout << "Threads joined " << prime << endl;
     }
    // if prime passes all check return 1 else 0
     if (prime == true) {
         return 1;
     } else {
         return 0;
     }
}


// main method
int main( int argc, char ** argv)
{
    /// parse command line arguments
    if( argc != 1 && argc != 2) {
        printf("Uasge: countPrimes [nThreads]\n");
        exit(-1);
    }
    if( argc == 2) nThreads = atoi( argv[1]);

    /// handle invalid arguments
    if( nThreads < 1 || nThreads > 256) {
        printf("Bad arguments. 1 <= nThreads <= 256!\n");
    }

    /// count the primes
    printf("Counting primes using %d thread(s).\n",
           nThreads);
    int64_t count = 0;
    while( 1) {
        int64_t num;
        if( 1 != scanf("%ld", & num)) break;
        if(isPrime(num)) count ++;
    }

    /// report results
    printf("Found %ld primes.\n", count);

    return 0;
}
