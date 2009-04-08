package de.ufinke.cubaja.io;

class PropertyDescription implements Comparable<PropertyDescription> {

  private String name;
  private Class<?> type;
  private boolean noSetterPresent;

  PropertyDescription(String name, Class<?> type) {

    this.name = name;
    this.type = type;
  }
  
  public int compareTo(PropertyDescription other) {
    
    return name.compareTo(other.name);
  }

  String getName() {

    return name;
  }

  Class<?> getType() {

    return type;
  }
  
  void markNoSetterPresent() {
    
    noSetterPresent = true;
  }
  
  boolean isNoSetterPresent() {
    
    return noSetterPresent;
  }
}
