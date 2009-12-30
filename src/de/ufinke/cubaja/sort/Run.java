// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.ArrayList;
import java.util.List;

class Run {

  private List<Block> blockList;
  
  public Run() {
    
    blockList = new ArrayList<Block>();
  }
  
  public void addBlock(Block block) {
    
    blockList.add(block);
  }
  
  public List<Block> getBlockList() {
    
    return blockList;
  }
}
