// CPSC 331
//Lachlan Moore 30030228
//Junehyuk Kim  30020861
//Nathanael Huh 10128366

package cpsc331.assignment3;

import cpsc331.collections.BoundedMaxHeap;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import cpsc331.collections.HeapFullException;

/**
*
* Provides an ArrayList-based implementation of a BoundedMaxHeap<br><br>
*
* ArrayMaxHeapInvariant: A finite multiset of non-null values of ordered type T is
* stored in a bounded binary MaxHeap, using an array-based representation
* <br><br>
*
*/

public class ArrayMaxHeap<T extends Comparable<T>> implements BoundedMaxHeap<T> {

  // Data fields
  
  private final int CAPACITY;
  private int size;
  private ArrayList<T> A;

  // Returns the index of a parent of a node that has one
  //
  // Precondition: A integer i such that 1 <= i <= this.size - 1 is given as input.
  // Postcondition: If A is viewed as a representation of a binary tree with
  //   size "this.size" then the index of the parent of the node with index i is
  //   returned as output.
  
  private int parent (int i) {
  
    if (i >= 1 && i <= this.size -1) {
        return (i-1)/2;
    }
    return 0;
  
  }
  
  // Reports whether a node with a given index has a left child in the binary
  // tree represented by A
  //
  // Precondition: An integer i such that 0 <= i <= size - 1 is given as input.
  // Postcondition: If A is viewed as a representation of a binary tree with size
  //   "this.size" then true is returned if the node with index i has a left
  //   child in this binary tree, and false is returned otherwise
  
  private boolean hasLeft (int i) {
  
    if (i >= 0 && 2*i+1 <= this.size -1) {
        return true;
    }
    return false
  }
  
  // Returns the index of a node that is the left child of a node with a given index
  //
  // Precondition:
  // a) An integer i such that 0 <= i <= this.size - 1 is given as input.
  // b) If A is viewed as a representation of a binary tree with size "this.size"
  //    then the node x with index i has a left child in this binary tree.
  // Postcondition: The index of the left child of x is returned as output.
  //
  
  private int leftChild (int i) {
  
    if (hasLeft(i)) {
        return 2*i+1;
    }
    return 0;
  
  }
  
  // Reports whether a node with a given index has a right child in the binary
  // tree represented by A
  //
  // Precondition: An integer i such that 0 <= i <= this.size - 1 is given as input.
  // Postcondition: If A is viewed as a representation of a binary tree with size
  //   "this.size" then true is returned if the node with index i has a right
  //   child in this binary tree, and false is returned otherwise
  
  private boolean hasRight (int i) {
  
    if (i >= 0 && 2*i+2 <= this.size-1) {
        return true;
    }
    return false;
  }
  
  // Returns the index of a node that is the right child of a node with a given index
  //
  // Precondition:
  // a) An integer i such that 0 <= i <= this.size - 1 is given as input.
  // b) If A is viewed as a representation of a binary tree with size "this.size"
  //    then the node x with index i has a right child in this binary tree.
  // Postcondition: The index of the right child of x is returned as output.
  //
  
  private int rightChild (int i) {
  
    if (hasRight(i)) {
        return 2*i +2;
    }
    return 0;
    
  }
  
  
  //
  // Implements the "bubbleUp" method needed to complete an insertion
  //
  // Precondition:
  // a) If this.A is viewed as representing a binary tree with the shape of a binary
  //    heap then all entries are non-null, so that the size of this tree is the
  //    same as the value of the data field "this.size", and this tree has  non-null
  //    values at its nodes.
  // b) The value of this.CAPACITY is an integer that is greater than or equal to the
  //    value of the data field "this.size" - and equal to the capacity of the
  //    ArrayList this.A.
  // c) An integer i such that 0 <= i <= this.size - 1 has been given as input. If x
  //    is the node with index i in the above binary tree then, for every node y
  //    in this tree except for x, both the parent and grandparent of y store values
  //    that are greater than is stored at y - if these nodes exist.
  // Postcondition:
  // a) The values stored in the first "this.size" locations of A have been reordered
  //    but not otherwise changed.
  // b) this.A now represents a BoundedMaxHeap with size "this.size"
  //
  // Note: The value of i can change - by decreasing - as this problem is being solved.
  
  private void bubbleUp (int i) {
  
    int index = i;
    
    while (parent(index) >= 0 && A.get(parent(index)).compareTo(A.get(index)) < 0) {
        T temp = A.get(index);
        A.set(index, A.get(parent(index)));
        A.set(parent(index), temp);
        index = parent(index);
    }
    
  }
  
  //
  // Implements the "bubbleDown" method needed to complete a deleteMax
  //
  // Precondition:
  // a) If A is viewed as representing a binary tree with the shape of a binary
  //    heap then all entries are non-null, so that the size of this tree is the
  //    same as the value of the data field "this.size", and this tree has non-null
  //    values at its nodes.
  // b) The value of this.CAPACITY is an integer that is greater than or equal to the
  //    value of the data field "this.size" - and equal to the capacity of the
  //    ArrayList A.
  // c) An integer i such that 0 <= i <= this.size - 1 has been given as input. If x
  //    is the node with index x in the above binary tree then, for every node y
  //    in this tree except for x, both the children and grandchildren of y store
  //    values that are less than the value stored at y - if these nodes exist.
  
  // Postcondition:
  // a) The values stored in the first "this.size" locations of A have been reordered
  //    but not changed.
  // b) A now represents a BoundedMaxHeap with size "this.size".
  //
  // Note: The value of i can change - by increasing - as this problem is being solved.
  
  private void bubbleDown (int i) {
  
    int index = i;
    
    while (hasLeft(index)) {
        int max = leftChild(index);
        if (hasRight(index) && A.get(leftChild(index)).compareTo(A.get(rightChild(index))) < 0) {
            max = rightChild(index);
        }
        if (A.get(index).compareTo(A.get(max)) < 0) {
            T temp = A.get(index);
            A.set(index, A.get(max));
            A.set(max, temp);
        } else {
            break;
        }
        index = max;
    }
  
  }
  
  //
  // Converts an array into a representation of a BoundedMaxHeap
  //
  // Precondition:
  // a) A is an ArrayList with positive integer capacity this.CAPACITY, and whose
  //    size is this this.CAPACITY, storing non-null values from some ordered type T
  // Postcondition:
  // a) The entries of A have been ordered but otherwise not changed.
  // b) A is now a representation of a BoundedMaXHeap whose size is the size (and
  //    capacity) of this ArrayList.
  
  private void heapify(){
  
    int i = getSize()/2;
    
    while(i > 0) {
        bubbleDown(i-1);
        i--;
    }
  
  }
  
  
  /**
  *
  * @param capacity the capacity of the BondedMaxHeap to be created
  * @throws IllegalArgumentException if the capacity is not positive
  * <br><br>
  *
  * Constructs an empty BoundedHeapHeap with a given positive integer capacity<br><br>
  *
  * Precondition:<br>
  * <ol style="list-style-type: lower-alpha">
  * <li> An integer capacity is given as input </li>
  * </ol>
  * Postcondition:<br>
  * <ol style="list-style-type: lower-alpha">
  * <li> If the input capacity is positive then an empty (array-based) BondedMaxHeap
  *      with this capacity has been created. An IllegalArgumentException has been
  *      thrown, and a BoundedMaxHeap has not been created, otherwise. </li>
  * </ol>
  *
  */
  
  public ArrayMaxHeap(int capacity) throws IllegalArgumentException {
  
    if (capacity > 0) {
    
      CAPACITY = capacity;
      size = 0;
      A = new ArrayList<T>(capacity);
      int i = 0;
      
      // Loop Invariant:
      // 1. A positive integer capacity is given as input.
      // 2. A is an ArrayList storing values with type T.
      // 3. i is an integer such that 0 <= i <= capacity
      // 4. The current size of A is i and, for every integer j such that
      //    0 <= j < i, A[j] = null
      // Bound Function: capacity - i
      
      while (i < capacity) {
        A.add(null);
        i = i+1;
      }
    
    } else {
    
      throw new IllegalArgumentException("The input capacity must be positive.");
    
    }
  
  }
  
  /**
  *
  * @param givenA the ArrayList to be used to create this.A
  * <br><br>
  *
  * Constructs a BoundedMaxHeap from a given ArrayList<br><br>
  *
  * Precondition:<br>
  * <ol style="list-style-type: lower-alpha">
  * <li> An ArrayList givenA, whose size is the same as its positive integer capacity,
  *      storing non-null values from the ordered type T, is given as input </li>
  * </ol>
  * Postcondition:<br>
  * <ol style="list-style-type: lower-alpha">
  * <li> Both this.CAPaCITY and this.size are equal to the capacity of A
  * <li> this.A is the array givenA - whose entries have been reordered, but
  *      not changed, so that this represents a BoundedMaxHeap </li>
  * </ol>
  *
  */
  
  public ArrayMaxHeap(ArrayList<T> givenA) {
  
    CAPACITY = givenA.size();
    size =  CAPACITY;
    A = givenA;
    heapify();
  
  }
  
  
  // Implementation of insert
  
  public void insert(T v) throws HeapFullException {
  
    if (this.size == CAPACITY) {
        throw new HeapFullException("the heap is full");
    }
    this.size++;
    int index = this.size - 1;
    A.set(index, v);
    bubbleUp(index);
  
  }
  
  // Implementation of deleteMax
  
  public T deleteMax() throws NoSuchElementException {
  
    if (this.size == 0) {
        throw new NoSuchElementException("the heap is empty");
    }
    T res = A.get(0);
    A.set(0, A.get(this.size-1));
    A.set(this.size-1, null);
    this.size--;
    bubbleDown(0);
    return res;
  }
  
  // Implementation of getSize; supplied by instructor
  
  public int getSize() {
    return size;
  }
  
  // Implementation of getCapacity; supplied by instructor
  
  public int getCapacity() {
    return CAPACITY;
  }
  
  // Used for testing; supplied by instructor
  
  T valueByIndex (int indx) throws NoSuchElementException {
  
    if ((indx >= 0) && (indx < size)) {
    
      return A.get(indx);
    
    } else {
    
      throw new NoSuchElementException("There is no node with this index.");
    
    }
  
  }

}