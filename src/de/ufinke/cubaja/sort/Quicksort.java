//Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
//Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;
import java.util.Random;

/**
* Quicksort algorithm.
* @author Uwe Finke
*/
public class Quicksort<T> implements SortAlgorithm<T> {

  static private final int INSERTION_THRESHOLD = 7;
  
  private Random random;
  private T[] entries;
  private T temp;
  private Comparator<T> comparator;
  
  private int insertionThreshold;
  
  /**
  * Constructor.
  */
  public Quicksort() {
   
   random = new Random();
   insertionThreshold = INSERTION_THRESHOLD;
  }
  
  public void sort(T[] entries, int size, Comparator<T> comparator) {
  
   this.entries = entries;
   this.comparator = comparator;
   
   sort(0, size - 1);
  }
  
  private void sort(int left, int right) {
  
   while (right > left) {
     
     if ((right - left) <= insertionThreshold) {
       
       insertionSort(left, right);
       left = right;
       
     } else {
       
       findBestPivot(left, right);
       
       T pivot = entries[right];
       int leftIndex = left - 1;
       int rightIndex = right;
       boolean loop = true;
       
       while (loop) {
         while (comparator.compare(entries[++leftIndex], pivot) < 0) {
         }
         while (comparator.compare(entries[--rightIndex], pivot) > 0 && rightIndex > leftIndex) {
         }
         if (leftIndex >= rightIndex) {
           loop = false;
         } else {
           swap(leftIndex, rightIndex);
         }
       }
       
       entries[right] = entries[leftIndex];
       entries[leftIndex] = pivot;
       
       if ((leftIndex - left) < (right - leftIndex)) {
         sort(left, leftIndex - 1);
         left = leftIndex + 1;
       } else {
         sort(leftIndex + 1, right);
         right = leftIndex - 1;
       }
     }
   }
   
  }
  
  private void insertionSort(int left, int right) {
   
   int j;
   int i = left + 1;
   
   while (i <= right) {
     temp = entries[i];
     j = i - 1;
     while (j >= left && comparator.compare(temp, entries[j]) < 0) {
       entries[j + 1] = entries[j];
       j--;
     }
     entries[j + 1] = temp;
     i++;
   }
  }
  
  private void findBestPivot(int left, int right) {
  
   int median = left + random.nextInt(right - left + 1);
   swap(right, median);
  }
  
  private void swap(int a, int b) {
   
   temp = entries[a];
   entries[a] = entries[b];
   entries[b] = temp;
  }
  
}
