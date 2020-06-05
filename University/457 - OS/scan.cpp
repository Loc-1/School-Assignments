/**********************************************
 * Last Name:   Moore
 * First Name:  Lachlan
 * Student ID:  30030228
 * Course:      CPSC 457
 * Tutorial:    01
 * Assignment:  2
 * Question:    Q3
 *
 * File name: scan.cpp
 *********************************************/

#include<iostream>
#include<stdio.h>
#include<stdlib.h>
#include<fcntl.h>
#include<unistd.h>
#include<sys/types.h>
#include<sys/stat.h>
#include<string>
#include<cstring>
#include<sys/wait.h>
#include<signal.h>
#include<algorithm>
 
using namespace std;

struct iAmSorry {
    char * fp;
    int size;
};

bool compareGomen(iAmSorry a, iAmSorry b) {
    return (a.size > b.size);
}

bool compareGomenR(iAmSorry a, iAmSorry b) {
    return (a.size < b.size);
}

// this is taken from someones code from https://stackoverflow.com/questions/874134/find-out-if-string-ends-with-another-string-in-c
bool hasEnding (std::string const &fullString, std::string const &ending) {
    if (fullString.length() >= ending.length()) {
        return (0 == fullString.compare (fullString.length() - ending.length(), ending.length(), ending));
    } else {
        return false;
    }
}

int main(int argc, char **argv){
    struct stat x;

    char buf[512];
    int n = 0;
    char * files[1000];

    string suffix = argv[1];
    int display = atoi(argv[2]);
    int type = atoi(argv[3]);

    FILE * fp = popen("./myFind", "r");
    if(!fp) {
        cout<< "Cannot be piped"<< endl;
    } else { 
        while(fgets(buf, 512, fp)) {
            int helper = strlen(buf) -1;
            files[n] = strndup(buf, helper);
            n++;
        }

        int sizes[n];

        for (int i = 0; i < n; i++) {
        stat(files[i], &x);
        size_t y = x.st_size;
        int q = static_cast<int>(y);
        sizes[i] = q;
        }

        iAmSorry storage[n-1];
        int totalSize = 0;
        int total = 0;

        for (int i = 0; i < n; i++) {
            string testing(files[i]);
            if (hasEnding(testing, suffix)) {
                storage[total].fp = files[i];
                storage[total].size = sizes[i];
                totalSize = totalSize + sizes[i];
                total++;
            }
            
        }

        if (type == 1) {
            sort(storage, storage + total, compareGomen);
        }

        if (type == 0) {
            sort(storage, storage + total, compareGomenR);
        }
        
        int partSize = 0;

        for (int i = 0; i < min(display, total); i++) {
            cout << storage[i].fp << " " << storage[i].size << endl;
            partSize = partSize + storage[i].size;
        }

        cout << "Total Size: " << totalSize << endl;
        cout << "Part Size: " << partSize << endl;
        cout << "Percent: " << partSize*100/totalSize << endl;
    }
    pclose(fp);
}
