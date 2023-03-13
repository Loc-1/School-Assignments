package cpsc331.collections;

import java.util.NoSuchElementException;

/**
*
* Privates an interface for a mapping when there is a total order on the set of
* keys.<br><br>
*
* Dictionary Invariant: A finite collection S of pairs (k,&nbsp;v) of values where
* k has type&nbsp;K, such that there is a total order on&nbsp;K, and values v
* of type&nbsp;K is maintained. This includes at most one ordered pair with first
* element&nbsp;k for every key k&nbsp;&isin;&nbsp;K, so that a partial function
* from&nbsp;K  to&nbsp;V is being represented.
* <br><br>
*
* @author Wayne Eberly
*/

public interface Dictionary<K extends Comparable<K>, V>
  extends Mapping<K, V> {
  
}