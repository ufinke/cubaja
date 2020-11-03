// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

class ExceptionHandlerDefinition {

  private Label startLabel;
  private Label endLabel;
  private Type exceptionType;
  private Label handlerLabel;
  
  ExceptionHandlerDefinition(Label startLabel, Label endLabel, Type exceptionType, Label handlerLabel) {
    
    this.startLabel = startLabel;
    this.endLabel = endLabel;
    this.exceptionType = exceptionType;
    this.handlerLabel = handlerLabel;
  }

  Label getStartLabel() {
  
    return startLabel;
  }

  Label getEndLabel() {
  
    return endLabel;
  }

  Type getExceptionType() {
  
    return exceptionType;
  }

  Label getHandlerLabel() {
  
    return handlerLabel;
  }
}
