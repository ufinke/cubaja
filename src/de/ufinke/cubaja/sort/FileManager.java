package de.ufinke.cubaja.sort;

import java.io.*;
import java.util.*;
import de.ufinke.cubaja.io.*;

class FileManager {

  private SortConfig config;
  private List<HandlerDefinition> handlerList;
  private File file;
  private BinaryOutputStream outputStream;
  private BinaryInputStream inputStream;
  
  FileManager(SortConfig config, List<HandlerDefinition> handlerList) {
  
    this.config = config;
    this.handlerList = handlerList;
  }
  
  private File getFile() throws Exception {
    
    if (file == null) {
      File dir = null;
      if (config.getWorkDirectory() != null) {
        dir = new File(config.getWorkDirectory());
        dir.mkdirs();
      }
      file = File.createTempFile(config.getFilePrefix(), null, dir);
      file.deleteOnExit();
    }
    
    return file;
  }
  
  BinaryOutputStream createOutput() throws Exception {
    
    outputStream = new BinaryOutputStream(new BufferedOutputStream(new FileOutputStream(getFile())));
    
    for (HandlerDefinition handler : handlerList) {
      outputStream.addObjectHandler(handler.getClass(), handler.getOutputHandler());
    }
    
    return outputStream;
  }
  
  void closeOutput() throws Exception {
    
    if (outputStream != null) {
      outputStream.close();
      outputStream = null;
    }
  }
  
  BinaryInputStream createInput() throws Exception {
    
    inputStream = new BinaryInputStream(new BufferedInputStream(new FileInputStream(getFile())));
    
    for (HandlerDefinition handler : handlerList) {
      inputStream.addObjectHandler(handler.getClass(), handler.getInputHandler());
    }
    
    return inputStream;
  }
  
  void closeInput() throws Exception {
    
    if (inputStream != null) {
      inputStream.close();
      inputStream = null;
    }
  }
  
}
