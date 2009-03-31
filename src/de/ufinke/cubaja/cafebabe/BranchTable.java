// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import de.ufinke.cubaja.util.Util;

public class BranchTable {

  static class Pair {
  
    private int key;
    private String labelName;
    
    Pair(int key, String labelName) {
      
      this.key = key;
      this.labelName = labelName;
    }
    
    int getKey() {
      
      return key;
    }
    
    String getLabelName() {
      
      return labelName;
    }
  }
  
  private String defaultLabelName;
  private List<Pair> pairList;
  private boolean sorted;
  
  public BranchTable(String defaultLabelName) {
    
    pairList = new ArrayList<Pair>();
    this.defaultLabelName = defaultLabelName;
  }
  
  public void addBranch(int key, String labelName) {
    
    pairList.add(new Pair(key, labelName));
    sorted = false;
  }
  
  String getDefaultLabelName() {
    
    return defaultLabelName;
  }
  
  List<Pair> getPairList() {
    
    if (! sorted) {
      sortList();
    }
    
    return pairList;
  }
  
  private void sortList() {
    
    Comparator<Pair> comparator = new Comparator<Pair>() {
      
      public int compare(Pair a, Pair b) {
        
        return Util.compare(a.getKey(), b.getKey());
      }
    };
    
    Collections.sort(pairList, comparator);
    sorted = true;
  }
}
