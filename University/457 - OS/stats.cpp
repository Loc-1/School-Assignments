/**********************************************
 * Last Name:   Moore
 * First Name:  Lachlan
 * Student ID:  30030228
 * Course:      CPSC 457
 * Tutorial:    01
 * Assignment:  2
 * Question:    Q5
 *
 * File name: stats.cpp
 *********************************************/

#include <stdio.h>
#include <ostream>
#include <string.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>
#include <iomanip>
#include <iostream>

//assumed from question 3 max size of file name
#define MAX_SIZE 512
//max of N
#define MAX_NUM 1000000

using namespace std;

//global vars
char filename[MAX_SIZE];
int T;
int countN;
int file_Nums[MAX_NUM];
int SUM;
double MEAN;
double thread_MEAN[MAX_NUM];
double SD;
pthread_mutex_t mutex_sum;
pthread_mutex_t mutex_sd;
int thread_Counter = 0;
int ind_pos = 0;

//threads for calculating the mean and sum
void *mean_threads(void *t)
{

    int div = countN / T;
    int thrSum = 0;
    long int num_Of_T = (long int)t;

    int partition = (countN % T);

    pthread_mutex_lock(&mutex_sum);

    if (thread_Counter < partition)
    {

        for (int i = 0; i < div + 1; i++)
        {
            thrSum += file_Nums[ind_pos];
            ind_pos++;
        }
        thread_Counter++;
    }
    else
    {

        for (int i = 0; i < div; i++)
        {
            thrSum += file_Nums[ind_pos];
            ind_pos++;
        }
        thread_Counter++;
    }

    thread_MEAN[num_Of_T] = double(thrSum/div);
    printf("\t Thread %ld: %.2f \n", (num_Of_T + 1), thread_MEAN[num_Of_T]);

    SUM += thrSum;
    pthread_mutex_unlock(&mutex_sum);

    pthread_exit(0);
}

//threads for calculating the SD sum (n - mean)^2
void *sd_threads(void *t)
{

    int div = countN / T;
    double thrSum = 0;
    long int num_Of_T = (long int)t;

    int partition = (countN % T);

    pthread_mutex_lock(&mutex_sd);

    if (thread_Counter < partition)
    {

        for (int i = 0; i < div + 1; i++)
        {
            thrSum = thrSum + pow((file_Nums[ind_pos] - thread_MEAN[num_Of_T]), 2);
            SUM = SUM + pow((file_Nums[ind_pos] - MEAN), 2);
            ind_pos++;
        }
        thread_Counter++;
    }
    else
    {

        for (int i = 0; i < div; i++)
        {
            thrSum = thrSum + pow((file_Nums[ind_pos] - thread_MEAN[num_Of_T]), 2);
            SUM = SUM + pow((file_Nums[ind_pos] - MEAN), 2);
            ind_pos++;
        }
        thread_Counter++;
    }


    double thread_sd = sqrt(thrSum/div);
    printf("\t Thread %ld: %.2f \n", (num_Of_T + 1), thread_sd);
    pthread_mutex_unlock(&mutex_sd);

    pthread_exit(0);
}

//main
int main(int argc, char **argv)
{
    //error checking the # of args
    if (argc != 3)
    {
        fprintf(stderr, "\n You need to provide two arguments: <filename> <T> \n\n");
        exit(-1);
    }
    else
    {
        strcpy(filename, argv[1]);
        T = atoi(argv[2]);
    }

    //open file
    FILE *fp = fopen(filename, "r");
    if (fp == NULL)
    {
        perror("Failed to open file, please try again");
        exit(-1);
    }

    //read all int in the file and store them in an array
    char buff[MAX_SIZE];
    countN = 0;

    while (fgets(buff, MAX_SIZE, fp))
    {
        int len = strlen(buff);
        int k = atoi(strndup(buff, len));
        file_Nums[countN] = k;
        countN++;
    }
    //close file
    fclose(fp);

    pthread_t threads[T];
    long i, status;
    SUM = 0;


    //Mean calculations...
    for (i = 0; i < T; i++)
    {
        status = pthread_create(&threads[i], NULL, mean_threads, (void *)i);
        if (status != 0)
        {
            printf("pthread_create error, please try again \n");
            exit(-1);
        }
    }

    for (i = 0; i < T; i++)
    {
        pthread_join(threads[i], NULL);
    }

    //ending mean threading print total mean
    pthread_mutex_destroy(&mutex_sum);
    MEAN = (double) SUM/ (double) countN;
    printf("\t Mean = %.2f \n", MEAN);

    //reset global vars 
    thread_Counter = 0;
    ind_pos = 0;
    SUM = 0;

    //Standard Deviation calculations...
    for (i = 0; i < T; i++)
    {
        status = pthread_create(&threads[i], NULL, sd_threads, (void *)i);
        if (status != 0)
        {
            printf("pthread_create error, please try again \n");
            exit(-1);
        }
    }

    for (i = 0; i < T; i++)
    {
        pthread_join(threads[i], NULL);
    }
    
    //ending SD threading print total SD
    pthread_mutex_destroy(&mutex_sd);
    SD = sqrt(SUM/countN);
    printf("\t StDev = %.2f \n", SD);

    return 0;
}