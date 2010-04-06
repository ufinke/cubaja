// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

/**
 * Configuration main class.
 * <p>
 * When the <tt>configure</tt> method is invoked,
 * and no <tt>NULL</tt> property provider
 * had been added, 
 * all default property providers not already specified are
 * added automatically in the order 
 * <tt>SYSTEM</tt>, <tt>BASE_PROPERTIES</tt>,
 * <tt>BASE_XML</tt>, and <tt>ENVIRONMENT</tt>.
 * @author Uwe Finke
 */
public class Configurator {

  private String baseName;
  private ResourceLoader loader;
  
  private Stack<XMLPropertyProvider> baseXMLStack;
  private Stack<PropertyProvider> baseResourceStack;
  private XMLPropertyProvider lastBaseXMLProvider;
  private PropertyProvider lastBaseResourceProvider;
  private PropertyProvider dummyProvider;
  
  private PropertyProvider baseXMLProvider;
  private PropertyProvider baseResourceProvider;  
  private EnvironmentPropertyProvider environmentProvider;
  private SystemPropertyProvider systemProvider;
  private boolean nullProvider;  
  
  private Map<String, NamedPropertyProvider> namedProviderMap;
  private List<PropertyProvider> providerSequenceList;
  private PropertyProvider masterProvider;
  
  private ParameterManager parameterManager;
  private Map<Object, Object> infoMap;
  
  private boolean processEscape;
  private boolean processProperties;
  
  /**
   * Constructor.
   */
  public Configurator() {
    
    processEscape = true;
    processProperties = true;
    
    baseName = System.getProperty("de.ufinke.cubaja.config.baseName", "config");
    loader = new DefaultResourceLoader();
    
    parameterManager = new ParameterManager();
    infoMap = new HashMap<Object, Object>();
    
    namedProviderMap = new HashMap<String, NamedPropertyProvider>();
    providerSequenceList = new ArrayList<PropertyProvider>();
    
    masterProvider = new PropertyProvider() {
      
      public String getProperty(String key) throws ConfigException {
        
        String result = null;
        int i = 0;
        List<PropertyProvider> seqList = getProviderSequenceList();
        while (result == null && i < seqList.size()) {
          result = seqList.get(i).getProperty(key);
          i++;
        }
        return result;
      }
    };
    
    dummyProvider = new PropertyProvider() {
      
      public String getProperty(String key) {
        
        return null;
      }
    };
  }
  
  List<PropertyProvider> getProviderSequenceList() {
    
    return providerSequenceList;
  }
  
  /**
   * Returns a shared map.
   * <p>
   * The map is shared between the
   * application (can be accessed with this method)
   * and all implementors of the {@link StartElementHandler} interface.
   * With the map, arbitrary information can be passed between element node instances.
   * There is one map per <tt>Configurator</tt> instance.
   * @return the shared map
   */  
  public Map<Object, Object> infoMap() {
    
    return infoMap;
  }
  
  /**
   * Sets the base name of XML and property documents.
   * <p>
   * The base name is the name of the XML document to be parsed,
   * without the '<tt>.xml</tt>' extension.
   * There may be an optional properties file with the same 
   * name (and extension '<tt>.properties</tt>').
   * <p>
   * If there is no explicit base name, the name '<tt>config</tt>'
   * will be used by default; with '<tt>config.xml</tt>' as XML document name
   * and '<tt>config.properties</tt>' as name of the 
   * optional base properties file.
   * @param baseName the XML document name without file name extension
   */
  public void setBaseName(String baseName) {
    
    if (baseName.endsWith(".xml")) {
      baseName = baseName.substring(0, baseName.length() - 4);
    }
    this.baseName = baseName;
  }
  
  /**
   * Returns the XML document base name.
   * @return the base name
   */
  public String getBaseName() {
    
    return baseName;
  }
  
  /**
   * Sets an individual <tt>ResourceLoader</tt>.
   * <p>
   * If no resource loader is explicitly specified,
   * <tt>Configurator</tt> uses a default resource loader
   * which tries to get the XML parsers input from a resource
   * (see {@link java.lang.ClassLoader ClassLoader}) 
   * or from a file in the file system.
   * <p>
   * If you want to load from file system and include XML files with resource names relative to
   * a base directory, use {@link FileResourceLoader}.
   * @param loader an individual resource loader
   */
  public void setResourceLoader(ResourceLoader loader) {
    
    this.loader = loader;
  }
  
  /**
   * Adds a named property provider.
   * @param provider the provider instance
   * @param providerName the providers name
   */
  public void addPropertyProvider(NamedPropertyProvider provider, String providerName) {
    
    namedProviderMap.put(providerName, provider);
  }

  /**
   * Adds an individual property provider.
   * @param provider a property provider
   */
  public void addPropertyProvider(PropertyProvider provider) {
  
    if (nullProvider) {
      return;
    }
    providerSequenceList.add(provider);
  }
  
  /**
   * Adds properties which are wrapped into a property provider.
   * @param properties properties
   */
  public void addPropertyProvider(Properties properties) {
    
    addPropertyProvider(new PropertiesWrapper(properties));
  }
  
  /**
   * Adds a standard property provider.
   * <p>
   * With this method, the sequence of the basic property providers can be set explicitly.
   * @param type the type of the provider
   */
  public void addPropertyProvider(PropertyProviderType type) {
    
    switch (type) {
      
      case SYSTEM:
        
        systemProvider = new SystemPropertyProvider();
        addPropertyProvider(systemProvider);
        
        break;
        
      case BASE_PROPERTIES:
        
        baseResourceStack = new Stack<PropertyProvider>();
        
        baseResourceProvider = new PropertyProvider() {
          
          public String getProperty(String key) throws ConfigException {
            
            String result = null;
            Stack<PropertyProvider> stack = getBaseResourceStack();
            int i = stack.size() - 1;
            while (result == null && i >= 0) {
              result = stack.get(i).getProperty(key);
              i--;
            }
            return result;
          }
        };
        addPropertyProvider(baseResourceProvider);
        
        break;
        
      case BASE_XML:
        
        baseXMLStack = new Stack<XMLPropertyProvider>();
        
        baseXMLProvider = new PropertyProvider() {
          
          public String getProperty(String key) throws ConfigException {
            
            String result = null;
            Stack<XMLPropertyProvider> stack = getBaseXMLStack();
            int i = stack.size() - 1;
            while (result == null && i >= 0) {
              result = stack.get(i).getProperty(key);
              i--;
            }
            return result;
          }
        };
        addPropertyProvider(baseXMLProvider);
        
        break;
        
      case ENVIRONMENT:
        
        environmentProvider = new EnvironmentPropertyProvider();
        addPropertyProvider(environmentProvider);
        
        break;
        
      case NULL:
        
        nullProvider = true;
        
        break;
    }
  }
  
  Stack<XMLPropertyProvider> getBaseXMLStack() {
    
    return baseXMLStack;
  }
  
  Stack<PropertyProvider> getBaseResourceStack() {
    
    return baseResourceStack;
  }
  
  private void finishProviders() throws ConfigException {
    
    if (nullProvider) {
      return;
    }
    
    if (systemProvider == null) {
      addPropertyProvider(PropertyProviderType.SYSTEM);
    }
    
    if (baseResourceProvider == null) {
      addPropertyProvider(PropertyProviderType.BASE_PROPERTIES);
    }
    
    if (baseXMLProvider == null) {
      addPropertyProvider(PropertyProviderType.BASE_XML);
    }
    
    if (environmentProvider == null) {
      addPropertyProvider(PropertyProviderType.ENVIRONMENT);
    }
  }

  /**
   * Pushes last <tt>configure</tt> properties onto the stack.
   */
  public void pushBaseProperties() {
    
    baseXMLStack.push(lastBaseXMLProvider);
    baseResourceStack.push(lastBaseResourceProvider);
  }
  
  /**
   * Pops properties off the stack.
   */
  public void popBaseProperties() {
    
    lastBaseXMLProvider = baseXMLStack.pop();
    lastBaseResourceProvider = baseResourceStack.pop();
  }
  
  /**
   * Adds a parameter factory finder.
   * <p>
   * An individual <tt>ParameterFactoryFinder</tt>
   * may be set to support special parameter types.
   * The instances will be invoked in the descending order of
   * their definition. 
   * The last finder in the stack is a default finder which 
   * includes the <tt>ParameterFactory</tt>s 
   * for the basic parameter types.
   * @param finder a parameter factory finder
   */
  public void addParameterFactoryFinder(ParameterFactoryFinder finder) {
    
    parameterManager.pushParameterFactoryFinder(finder);
  }
  
  /**
   * Sets the date pattern.
   * <p>
   * The default pattern is <tt>yyyy-MM-dd</tt>.
   * <p>
   * The pattern may also be set in the XML document
   * with the attribute <tt>datePattern</tt>
   * in a <tt>configSettings</tt> element.
   * @param pattern The date pattern
   * @param hint a user-friendly pattern used in exception messages
   */
  public void setDatePattern(String pattern, String hint) {
    
    parameterManager.setDatePattern(pattern, hint);
  }
  
  /**
   * Sets the date pattern with the pattern itself as hint.
   * @param pattern the date pattern
   */
  public void setDatePattern(String pattern) {
    
    setDatePattern(pattern, pattern);
  }
  
  /**
   * Sets constant values for boolean <tt>true</tt>.
   * <p>
   * Default values are <tt>true</tt>, <tt>yes</tt> and <tt>on</tt>.
   * @param trueValues The constants representing <tt>true</tt>
   */
  public void setTrueValues(String[] trueValues) {
    
    parameterManager.setTrueValues(trueValues);
  }
  
  /**
   * Sets constants for boolean <tt>false</tt>.
   * <p>
   * Default values are <tt>false</tt>, <tt>no</tt> and <tt>off</tt>.
   * @param falseValues The constants representing <tt>false</tt>.
   */
  public void setFalseValues(String[] falseValues) {
    
    parameterManager.setFalseValues(falseValues);
  }

  /**
   * Specifies the decimal point character.
   * <p>
   * By default, or if the passed Character is <tt>null</tt>,
   * both point and comma are treated as 
   * decimal point characters.
   * <p>
   * If the decimal point character (point or comma) is set explicitly,
   * the other character (comma or point) is 
   * considered to be a grouping character and is ignored
   * when parsing numeric values.
   * <p>
   * The decimal point character may also be set in the XML document
   * with the attribute <tt>decimalPoint</tt>
   * in a <tt>configSettings</tt> element.
   * @param decimalPoint the decimal point character
   */
  public void setDecimalPoint(Character decimalPoint) {
    
    parameterManager.setDecimalPoint(decimalPoint);
  }
  
  /**
   * Enables or disables processing of escape sequences.
   * <p>
   * By default, escape sequence processing is enabled.
   * <p>
   * The switch may also be set in the XML document
   * with the attribute <tt>processEscape</tt>
   * in a <tt>configSettings</tt> element.
   * @param processEscape <tt>true</tt> or <tt>false</tt>
   */
  public void setProcessEscape(boolean processEscape) {
    
    this.processEscape = processEscape;
  }
  
  /**
   * Enables or disables properties resolving.
   * <p>
   * By default, properties resolving is enabled.
   * <p>
   * The switch may also be set in the XML document
   * with the attribute <tt>processProperties</tt>
   * in a <tt>configSettings</tt> element.
   * @param processProperties <tt>true</tt> or <tt>false</tt>
   */
  public void setProcessProperties(boolean processProperties) {
    
    this.processProperties = processProperties;
  }
  
  /**
   * Parses the XML document and sets the root element values.
   * @param <T> the type of our root element
   * @param rootNode the object representing the root element
   * @return the root element
   * @throws ConfigException
   */
  public <T> T configure(T rootNode) throws ConfigException {
    
    finishProviders();
    lastBaseXMLProvider = new XMLPropertyProvider();
    lastBaseResourceProvider = (baseResourceProvider == null) ? 
        dummyProvider : new ResourcePropertyProvider(loader, baseName + ".properties", false);
    pushBaseProperties();
    
    SAXHandler saxHandler = new SAXHandler();
    
    saxHandler.setParameterManager(parameterManager);
    saxHandler.setInfoMap(infoMap);
    saxHandler.setRootNode(rootNode);
    saxHandler.setLoader(loader);
    saxHandler.setXMLProperties(lastBaseXMLProvider);
    saxHandler.setPropertyProvider(masterProvider);
    saxHandler.setNamedProviderMap(namedProviderMap);
    saxHandler.setProcessEscape(processEscape);
    saxHandler.setProcessProperties(processProperties);
    
    saxHandler.parse(getBaseName() + ".xml");
    
    popBaseProperties();
    
    return rootNode;
  }
  
}
