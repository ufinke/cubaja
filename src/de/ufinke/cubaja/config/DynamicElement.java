package de.ufinke.cubaja.config;

public interface DynamicElement {

  public String alternateName(String originalName) throws ConfigException;
  
  public ParameterFactoryFinder parameterFactoryFinder() throws ConfigException;
}
