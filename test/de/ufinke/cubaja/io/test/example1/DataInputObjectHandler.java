package de.ufinke.cubaja.io.test.example1;

import de.ufinke.cubaja.io.BinaryInputStream;
import de.ufinke.cubaja.io.InputObjectHandler;

public class DataInputObjectHandler implements InputObjectHandler {

  public DataInputObjectHandler() {
    
  }

  public Object read(BinaryInputStream stream, Class<?> clazz) throws Exception {

    Data data = new Data();
    
    data.setDate(stream.readDate());
    data.setInteger(stream.readInt());
    data.setString(stream.readString());
    data.setSubData(stream.readObject(SubData.class));
    
    return data;
  }
}
