package de.ufinke.cubaja.cafebabe;

public interface Generator {

  public String getClassName() throws Exception;
  
  public GenClass generate() throws Exception;
}
