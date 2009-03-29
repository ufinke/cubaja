package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ExceptionAttribute implements Generatable {

  private GenClass genClass;
  private int nameIndex;
  private List<Integer> exceptionList;
  
  ExceptionAttribute(GenClass genClass) {
  
    this.genClass = genClass;
    nameIndex = genClass.getConstantPool().addName("Exceptions");
    exceptionList = new ArrayList<Integer>();
  }
  
  void addException(Type exception) {
  
    exceptionList.add(genClass.getConstantPool().addClass(exception));
  }
  
  public void generate(DataOutputStream out) throws IOException {
    
    out.writeShort(nameIndex);
    out.writeInt(2 + exceptionList.size() * 2);
    out.writeShort(exceptionList.size());
    for (Integer exceptionIndex : exceptionList) {
      out.writeShort(exceptionIndex);
    }
  }
  
}
