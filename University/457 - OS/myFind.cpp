/**********************************************
 * Last Name:   Moore
 * First Name:  Lachlan
 * Student ID:  30030228
 * Course:      CPSC 457
 * Tutorial:    01
 * Assignment:  2
 * Question:    Q2
 *
 * File name: myFind.cpp
 *********************************************/
// Based off of code from tutorials
#include<iostream>
#include<dirent.h>
#include<cstring>
 
using namespace std;
 
void traverse(string path){
   //Get the dir to search given argument
   DIR *dir = opendir(path.c_str());
   struct dirent *info = NULL;
   //Error checking
   if(dir == NULL){
       printf("Directory cannot be opened %s \n", path);
   }else{
       while(info = readdir(dir)){
           if(info->d_type == DT_DIR){
               if(strcmp(info->d_name,".") == 0 || strcmp(info -> d_name, "..") == 0){
                   continue;
               }else{
                   //Print out file found and their path
                   string dir_name = path + "/" +info -> d_name;
                   //Recursive call
                   traverse(dir_name);
               }
           }else{
               //Print out file found and their path
               string file_path = path + "/";
               cout<<file_path<<info -> d_name<< "\n";
           }
       }
   }
   //Close dir after done searching
   closedir(dir);
}

//Main takes no arguments and just runs traverse from current dir
//signified by "."
int main(){
   traverse(".");
}
