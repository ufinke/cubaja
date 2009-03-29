// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.util.Map;

/**
 * Represents a node within the XML element tree.
 * <p/>
 * We instantiate the root node (a subclass of <code>ConfigNode</code>)
 * in our application and pass it to the 
 * <code>configure</code> method of our <code>Configurator</code>.
 * <p/>
 * For every XML element or attribute, the <code>Configurator</code>
 * invokes an appropriate setter or adder method of a <code>ConfigNode</code>'s
 * subclass.
 * If the method argument's type is a subclass of <code>ConfigNode</code>,
 * an instance of this class will be created automatically.
 * <p/>
 * Before any setter or adder methods will be invoked, the
 * <code>Configurator</code> calls the <code>ConfigNode</code>'s 
 * <code>init</code> method; 
 * and before the completed instance is passed to the parent node's
 * setter / adder method, <code>Configurator</code> calls
 * the <code>finish</code> method.
 * <p/>
 * We can annotate our setter and adder methods with <code>Pattern</code>,
 * <code>NoConfig</code>, or <code>Mandatory</code>.
 * @author Uwe Finke
 */
public class ConfigNode {
  
  {
    defaultParameterFactoryFinder = new ParameterFactoryFinder() {

      public ParameterFactory findFactory(Class<?> type) throws Exception {

        return null;
      } 
    };
  }
  
  static private ParameterFactoryFinder defaultParameterFactoryFinder;

  private Map<Object, Object> infoMap;
  private String charData;

  /**
   * Standard constructor.
   * <p/>
   * We have to write our own subclasses
   * with setter and adder methods.
   * <p/>
   * A subclass of <code>ConfigNode</code> must have a parameterless constructor.
   */
  protected ConfigNode() {
    
  }
  
  /**
   * Initialization hook before start of XML element processing.
   * <p/>
   * By default, this method does nothing.
   * We can overwrite it and place our initialization code
   * here. Here, in contrast to the constructor, we
   * have access to the info map. 
   * @throws ConfigException
   */
  protected void init() throws ConfigException {
    
  }
  
  /**
   * Finalization hook after end of XML element processing.
   * By default, this method does nothing.
   * @throws ConfigException
   */
  protected void finish() throws ConfigException {
    
  }
  
  /**
   * Returns a shared map.
   * <p/>
   * The map is shared between the
   * application (can be accessed from the 
   * <code>Configurator</code> instance)
   * and all <code>ConfigNode</code> instances.
   * We can optionally put information into the 
   * map which is needed by other 
   * <code>ConfigNode</code>s. 
   * @return the shared map
   */
  protected Map<Object, Object> infoMap() {

    return infoMap;
  }
  
  void info(Map<Object, Object> map) {
    
    this.infoMap = map;
  }
  
  /**
   * Returns the elements character data.
   * <p/>
   * All character data is presented in a
   * normalized form: Newline characters
   * are converted to space characters
   * (except newlines resulted from escape sequence <code>\n</code>).
   * Leading and trailing space characters are trimmed,
   * and consecutive space characters result in a
   * single space. This single space is suppressed if 
   * another whitespace character (e.g. newline, tab)
   * preceeds or succeeds the space.
   * This behaviour is analogue to text presentation
   * in HTML browsers.
   * Normalization isn't applied to <code>CDATA</code>
   * content.
   * <p/>
   * If an element has no character data, this
   * method returns the empty string.
   * @return the character data
   */
  protected String charData() {
    
    return charData;
  }
  
  void charData(String data) {
    
    this.charData = data;
  }
  
  /**
   * Returns an alternate method name when no matching setter or adder is present.
   * We can overwrite this method to allow dynamic tag or attribute names.
   * By default, this method returns <code>null</code>; this means there is no alternate name.
   * @param originalTagOrAttributeName name of element ar attribute name in XML
   * @return the name of an appropriate setter or adder method within this class, or <code>null</code>
   */
  protected String assignAlternateName(String originalTagOrAttributeName) {
    
    return null;
  }
  
  /**
   * Returns an individual <code>ParameterFactoryFinder</code>.
   * We can overwrite this method to define a <code>ParameterFactoryFinder</code>
   * for our individual parameter types.
   * It is recommended to store an instance in a static variable rather than
   * letting the configurator create a new instance every time when a new node is created.
   * By default, this method returns a finder which finds nothing (returns <code>null</code>).
   * @return a <code>ParameterFactoryFinder</code>.
   */
  protected ParameterFactoryFinder parameterFactoryFinder() {
    
    return defaultParameterFactoryFinder;
  }
}
