package cpsc331.assignment2;

import java.util.NoSuchElementException;
import cpsc331.collections.Dictionary;
import cpsc331.assignment2.AVLDictionary;
import java.util.ArrayList;

/**
*
* Provides utilities that can be used to test implementations
* of AVLDictionary.
*
*/

public class AVLUtilities<K extends Comparable<K>, V> {

  /**
  *
  * Checks whether an input AVLDictionary is a valid AVL tree.<br>
  *
  * A second Boolean input is used to indicate whether
  * diagnostic messages should be printed if it is determined
  * that this is not, in fact, an AVL tree
  *
  */
  
  public boolean isAVLTree (AVLDictionary<K, V> D, boolean verbose) {
  
    boolean isBST = isBST(D, verbose);
    boolean isBalanced = isBalanced(D, verbose);
    return (isBST && isBalanced);
  
  }

  /**
  *
  * Checks whether an input AVLDictionary is a valid
  * binary search tree.<br>
  *
  * A second Boolean input is used to indicate whether
  * diagonstic messages should be printed if it is determined
  * that input tree is not, in fact, a binary search tree
  *
  */
  
  public boolean isBST (AVLDictionary<K, V> D, boolean verbose) {
  
    ArrayList<K> A = makeArray(D, verbose);
    int length = A.size();
    int i = 0;
    while (i < length - 1) {
      int result = (A.get(i)).compareTo(A.get(i+1));
      if (result >= 0) {
        return false;
      };
      i = i+1;
    };
    return true;
  
  }
  
  // Produces an ArrayList storing the keys in an input AVLDictionary.
  //
  // A second Boolean input is used as for isBST
  //
  // Based on the algorithm described in Tutorial Exercise #11
  
  private ArrayList<K> makeArray
     (AVLDictionary<K, V> D, boolean verbose) {
  
    ArrayList<K> A = new ArrayList<K>();
    if (D.root() != null) {
      addToArray(D.root(), A, verbose);
    };
    return A;
  
  }
  
  
  // Adds the entries of a subtree with an input node to an
  // input ArrayList.
  //
  // A second Boolean input is used as for isBST
  //
  // Based on the algorithm described in Tutorial Exercise #11 -
  // but also includes checks to ensure that the ArrayList
  // being produced is sorted
  
  private void addToArray (AVLDictionary<K, V>.AVLNode x,
                           ArrayList<K> A, boolean verbose) {
                           
    if (x.left() != null) {
      addToArray(x.left(), A, verbose);
      int length = A.size();
      int leftResult = (x.key()).compareTo(A.get(length - 1));
      if ((leftResult <= 0) && verbose) {
        System.out.print((A.get(length-1)).toString());
        System.out.print(" is in the left subtree of ");
        System.out.println(x.key().toString());
      };
    };
    
    A.add(x.key());
    int length = A.size();
    
    if (x.right() != null) {
      addToArray(x.right(), A, verbose);
      int rightResult = (x.key()).compareTo(A.get(length));
      if ((rightResult >= 0) && verbose) {
        System.out.print(A.get(length).toString());
        System.out.print(" is in the right subtree of ");
        System.out.println(x.key().toString());
      };
    };
                         
  }
  
  /**
  *
  * Checks whether all nodes in a (supposed) AVL tree are,
  * indeed, balanced.<br>
  *
  * A second Boolean input is used to indicated whether
  * diagonstic messages should be printed if it is determined
  * that input tree is not, in fact, a binary search tree
  *
  */
  
  public boolean
    isBalanced(AVLDictionary<K, V> D, boolean verbose) {
  
    if (D.root() == null) {
      return true;
    } else {
      return isSubtreeBalanced(D.root(), verbose);
    }
    
  }
  
  // Slow method to correctly check the height of a non-null node
  
  private int correctHeight(AVLDictionary<K, V>.AVLNode x) {
  
    int leftHeight = -1;
    if (x.left() != null) {
      leftHeight = correctHeight(x.left());
    };
    
    int rightHeight = -1;
    if (x.right() != null) {
      rightHeight = correctHeight(x.right());
    };
    
    return Math.max(leftHeight, rightHeight) + 1;
  
  }
  
  private int correctBalance(AVLDictionary<K, V>.AVLNode x) {
  
    int leftHeight = -1;
    if (x.left() != null) {
      leftHeight = correctHeight(x.left());
    };
    
    int rightHeight = -1;
    if (x.right() != null) {
      rightHeight = correctHeight(x.right());
    };
    
    return leftHeight - rightHeight;
  
  }
  
  // Slow method to correctly report the balance factor of a
  // non-null node
  
  //
  // Checks whether all nodes in the subtree of a given node
  // are, indeed, balanced - and that heights of nodes are
  // correctly defined
  //
  // A second Boolean input, verbose, is as for isBalanced
  //
  
  private boolean isSubtreeBalanced
     (AVLDictionary<K, V>.AVLNode x, boolean verbose) {
                                                  
    boolean leftBalanced = true;
    if (x.left() != null) {
      leftBalanced = isSubtreeBalanced(x.left(), verbose);
    };
    
    boolean rightBalanced = true;
    if (x.right() != null) {
      rightBalanced = isSubtreeBalanced(x.right(), verbose);
    };
    
    boolean correctHeight = true;
    if (correctHeight(x) != x.height()) {
      correctHeight = false;
    };
    if ((!correctHeight) && verbose) {
      System.out.print("Height of node with key ");
      System.out.print(x.key().toString());
      System.out.println(" is incorrect.");
    };
    
    int balanceFactor = correctBalance(x);
    boolean nodeBalanced = ((balanceFactor == 1)
                        || (balanceFactor == 0)
                        || (balanceFactor == -1)
                        || correctHeight);
                        
    if ((!nodeBalanced) && verbose) {
      System.out.print("Node with key ");
      System.out.print(x.key().toString());
      System.out.print(" has balance factor ");
      System.out.println(balanceFactor);
    };
    
    return (leftBalanced && rightBalanced && nodeBalanced);
                                                  
  }

}