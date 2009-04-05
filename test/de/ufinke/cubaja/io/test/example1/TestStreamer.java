package de.ufinke.cubaja.io.test.example1;

import de.ufinke.cubaja.io.*;

public class TestStreamer extends Streamer<Data> {

  public TestStreamer() {
    
  }

  public Data read() throws Exception {

    testState(in);
    
    Data data = new Data();
    
    data.setInteger(in.readInt());
    data.setString(in.readString());
    SubData subData = new SubData();
    subData.setNumber(in.readInt());
    subData.setName(in.readString());
    data.setSubData(subData);
    data.setDate(in.readDate());
    data.setStreamable(in.readStreamable(TestStreamable.class));
    
    return data;
  }

  public void write(Data data) throws Exception {

    testState(out);
    
    out.writeInt(data.getInteger());
    out.writeString(data.getString());
    SubData subData = data.getSubData();
    out.writeInt(subData.getNumber());
    out.writeString(subData.getName());
    out.writeDate(data.getDate());
    out.writeStreamable(data.getStreamable());
  }
  
}
