/**********************************************
 * Last Name:   Moore
 * First Name:  Lachlan
 * Student ID:  30030228
 * Course:      CPSC 457
 * Tutorial:    01
 * Assignment:  1
 * Question:    Q4
 *
 * File name: myWc.cpp
 *********************************************/

#include <unistd.h>
#include <string>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

using namespace std;
 
int main(int argc, char * const argv[]){

 // get the file name from command line
  string filename;
  if (argc != 2) {
    cerr << "Usage: readFile <input file> " << endl;
    return -1;
  } else {
    filename = argv[1];
  }

   int filePointer = open(filename.c_str(), O_RDONLY);
   /*  O_RDONLY - open the file only to read
       O_WRONLY - open the file only to write
       O_RDWR - open the file to read and write
   */

  if(filePointer == -1){
      printf("\nFile cannot be opened.");
      exit(-1);
  }

  char buffer[1000];
  int counter = 0;
  while(read(filePointer, buffer,  1000)){
      for(int i = 0; i<1000; i++){
          if (buffer[i] == '\n') {
              counter++;
          }
      }
  }

  cout << counter << " " << filename << "\n";
 
  //closing file 
  if(close(filePointer) == 0){
      printf("File has been closed successfully\n");
  }
  else{
      printf("Error!File cannot be closed\n");
  }
}
