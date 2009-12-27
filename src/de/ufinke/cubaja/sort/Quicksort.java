//Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
//Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;
import java.util.Random;

/**
* Quicksort algorithm.
* @author Uwe Finke
*/
public class Quicksort implements SortAlgorithm {

  static private final int INSERTION_THRESHOLD = 7;
  
  private Random random;
  @SuppressWarnings("rawtypes")
  private Comparator comparator;
  
  /**
  * Constructor.
  */
  public Quicksort() {
   
   random = new Random();
  }
  
  @SuppressWarnings("rawtypes")
  public void setComparator(Comparator comparator) {
    
    this.comparator = comparator;
  }
  
  public void sort(SortArray array) {

   sort(array.getArray(), 0, array.getSize() - 1);
  }
  
  @SuppressWarnings("unchecked")
  private void sort(Object[] array, int left, int right) {
  
   while (right > left) {
     
     if ((right - left) <= INSERTION_THRESHOLD) {
       
       insertionSort(array, left, right);
       left = right;
       
     } else {
       
       findBestPivot(array, left, right);
       
       Object pivot = array[right];
       int leftIndex = left - 1;
       int rightIndex = right;
       boolean loop = true;
       
       while (loop) {
         while (comparator.compare(array[++leftIndex], pivot) < 0) {
         }
         while (comparator.compare(array[--rightIndex], pivot) > 0 && rightIndex > leftIndex) {
         }
         if (leftIndex >= rightIndex) {
           loop = false;
         } else {
           swap(array, leftIndex, rightIndex);
         }
       }
       
       array[right] = array[leftIndex];
       array[leftIndex] = pivot;
       
       if ((leftIndex - left) < (right - leftIndex)) {
         sort(array, left, leftIndex - 1);
         left = leftIndex + 1;
       } else {
         sort(array, leftIndex + 1, right);
         right = leftIndex - 1;
       }
     }
   }
   
  }
  
  @SuppressWarnings("unchecked")
  private void insertionSort(Object[] array, int left, int right) {
   
   int j;
   int i = left + 1;
   
   while (i <= right) {
     Object temp = array[i];
     j = i - 1;
     while (j >= left && comparator.compare(temp, array[j]) < 0) {
       array[j + 1] = array[j];
       j--;
     }
     array[j + 1] = temp;
     i++;
   }
  }
  
  private void findBestPivot(Object[] array, int left, int right) {
  
   int median = left + random.nextInt(right - left + 1);
   swap(array, right, median);
  }
  
  private void swap(Object[] array, int a, int b) {
   
   Object temp = array[a];
   array[a] = array[b];
   array[b] = temp;
  }
  
}
