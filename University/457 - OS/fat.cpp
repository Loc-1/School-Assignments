/**********************************************
 * Last Name:   Moore
 * First Name:  Lachlan
 * Student ID:  30030228
 * Course:      CPSC 457
 * Tutorial:    01
 * Assignment:  5
 * Question:    Q2
 *
 * File name: fat.cpp
 *********************************************/
// this is updated from my last submission to run test 2 faster
// and not as poopy, using maps instead of for loops
#include <stdio.h>
#include <string>
#include <vector>
#include <set>
#include <map>

typedef std::string SS;
typedef std::vector<SS> VS;

using namespace std;

// set all check values to false intitalily, as we need to check each file
struct DEntry
{
    SS fname = SS(4096, 0);
    int size = 0;
    int ind = 0;
    bool tooManyBlocks = false;
    bool tooFewBlocks = false;
    bool hasCycle = false;
    bool sharesBlocks = false;
};


static SS join(const VS &toks, const SS &sep)
{
    SS res;
    bool first = true;
    for (auto &t : toks)
    {
        res += (first ? "" : sep) + t;
        first = false;
    }
    return res;
}

// modified from the skeleton provided 
int checkConsistency(int blockSize, std::vector<DEntry> &files, std::vector<int> &fat)
{
    // the value we will return at the end
    int returnVal = fat.size();
    set<int> indexs; // all used indexs 
    map<int,int> totalUsed; // all used values

    // loop over every file
    for (int i = 0; i < files.size(); i++) {
        int size = files[i].size;
        int curr = files[i].ind;
        map<int, int> visited;
        // loop until we have end of this speficic chain 
        while (curr != -1) {
            // testing to see if a cycle is found
            if (visited.find(curr) != visited.end()) {
                files[i].hasCycle = true;
                break;
            }
            // testing to see if blocks are shared
            if (totalUsed.find(curr) != totalUsed.end()) {
                files[i].sharesBlocks = true;
                files[totalUsed[curr]].sharesBlocks = true;
            } else {
                totalUsed[curr] = i;
            }
            visited[curr] = curr;
            size -= blockSize;
            indexs.insert(curr);
            // update curr to the next valu
            curr = fat[curr];
        }
        
        // sizing checking
        if (size <= 0) {
            // too many block case
            if (size+blockSize <= 0) {
                files[i].tooManyBlocks = true;
            }
        // too few block case
        } else {
            files[i].tooFewBlocks = true;
        }
    }
    // return
    returnVal -= indexs.size();
    return returnVal;
}

// main method
int main()
{
    try
    {
        // read in blockSize, nFiles, fatSize
        int blockSize, nFiles, fatSize;
        if (3 != scanf("%d %d %d", &blockSize, &nFiles, &fatSize))
            throw "cannot read blockSize, nFiles and fatSize";
        if (blockSize < 1 || blockSize > 1024)
            throw "bad block size";
        if (nFiles < 0 || nFiles > 50)
            throw "bad number of files";
        if (fatSize < 1 || fatSize > 200000)
            throw "bad FAT size";
        
        // read in the entries
        std::vector<DEntry> entries;

        for (int i = 0; i < nFiles; i++)
        {
            DEntry e;
            if (3 != scanf("%s %d %d", (char *)e.fname.c_str(), &e.ind, &e.size))
                throw "bad file entry";
            e.fname = e.fname.c_str();
            if (e.fname.size() < 1 || e.fname.size() > 16)
                throw "bad filename in file entry";
            if (e.ind < -1 || e.ind >= fatSize)
                throw "bad first block in fille entry";
            if (e.size < 0 || e.size > 1073741824)
                throw "bad file size in file entry";
            entries.push_back(e);
        }

        // read in the FAT
        std::vector<int> fat(fatSize);
        for (int i = 0; i < fatSize; i++)
        {
            if (1 != scanf("%d", &fat[i]))
                throw "could not read FAT entry";
            if (fat[i] < -1 || fat[i] >= fatSize)
                throw "bad FAT entry";
        }

        // run the consistency check
        int nFreeBlocks = checkConsistency(blockSize, entries, fat);

        // format the output
        size_t maxflen = 0;
        for (auto &e : entries)
            maxflen = std::max(maxflen, e.fname.size());
        SS fmt = " %" + std::to_string(maxflen) + "s: %s\n";
        printf("Issues with files:\n");
        for (auto &e : entries)
        {
            VS issues;
            if (e.tooFewBlocks)
                issues.push_back("not enough blocks");
            if (e.tooManyBlocks)
                issues.push_back("too many blocks");
            if (e.hasCycle)
                issues.push_back("contains cycle");
            if (e.sharesBlocks)
                issues.push_back("shares blocks");
            printf(fmt.c_str(), e.fname.c_str(), join(issues, ", ").c_str());
        }
        printf("Number of free blocks: %d\n", nFreeBlocks);
    }
    catch (const char *err)
    {
        fprintf(stderr, "Error: %s\n", err);
    }
    catch (...)
    {
        fprintf(stderr, "Errro: unknown.\n");
    }
    return 0;
}