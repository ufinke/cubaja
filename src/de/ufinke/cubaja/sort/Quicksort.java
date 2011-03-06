// Copyright (c) 2006 - 2011, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;
import java.util.Random;

/**
* Quicksort algorithm.
* @author Uwe Finke
*/
public class Quicksort implements SortAlgorithm {

  static private final int INSERTION_THRESHOLD = 7;
  
  /**
  * Constructor.
  */
  public Quicksort() {
   
  }
  
  @SuppressWarnings("rawtypes")
  public void sort(final Object[] array, final int size, final Comparator comparator) {

    sort(array, 0, size - 1, comparator, new Random());
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void sort(final Object[] array, int left, int right, final Comparator comparator, final Random random) {
  
   while (right > left) {
     
     if ((right - left) <= INSERTION_THRESHOLD) {
       
       insertionSort(array, left, right, comparator);
       left = right;
       
     } else {
       
       findBestPivot(array, left, right, random);
       
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
         sort(array, left, leftIndex - 1, comparator, random);
         left = leftIndex + 1;
       } else {
         sort(array, leftIndex + 1, right, comparator, random);
         right = leftIndex - 1;
       }
     }
   }
   
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void insertionSort(final Object[] array, final int left, final int right, final Comparator comparator) {
   
   int j;
   int i = left + 1;
   
   while (i <= right) {
     final Object temp = array[i];
     j = i - 1;
     while (j >= left && comparator.compare(temp, array[j]) < 0) {
       array[j + 1] = array[j];
       j--;
     }
     array[j + 1] = temp;
     i++;
   }
  }
  
  private void findBestPivot(final Object[] array, final int left, final int right, final Random random) {
  
   final int median = left + random.nextInt(right - left + 1);
   swap(array, right, median);
  }
  
  private void swap(final Object[] array, final int a, final int b) {
   
   final Object temp = array[a];
   array[a] = array[b];
   array[b] = temp;
  }
  
}
