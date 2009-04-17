package de.ufinke.cubaja.sort;

import de.ufinke.cubaja.io.*;

class HandlerDefinition {

  private Class<?> clazz;
  private OutputObjectHandler outputHandler;
  private InputObjectHandler inputHandler;

  HandlerDefinition(Class<?> clazz, OutputObjectHandler outputHandler, InputObjectHandler inputHandler) {

    this.clazz = clazz;
    this.outputHandler = outputHandler;
    this.inputHandler = inputHandler;
  }

  Class<?> getClazz() {

    return clazz;
  }

  OutputObjectHandler getOutputHandler() {

    return outputHandler;
  }

  InputObjectHandler getInputHandler() {

    return inputHandler;
  }
}
