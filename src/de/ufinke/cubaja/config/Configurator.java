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
 * <p/>
 * When we invoke the <code>configure</code> method,
 * unless the <code>NULL</code> property provider
 * had been added, 
 * all not already specified base propery providers are
 * added automatically in the order 
 * <code>SYSTEM</code>, <code>BASE_PROPERTIES</code>,
 * <code>BASE_XML</code>, and <code>ENVIRONMENT</code>.
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
    
    baseName = System.getProperty("net.sf.batchelor.config.baseName", "config");
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
   * <p/>
   * The map is shared between the
   * application (can be accessed with this method)
   * and all implementors of the <code>ManagedElement</code> interface.
   * We can optionally put general information into the 
   * map. 
   * There is one map per <code>Configurator</code> instance.
   * @return the shared map
   */  
  public Map<Object, Object> infoMap() {
    
    return infoMap;
  }
  
  /**
   * Sets the base name of XML and property documents.
   * <p/>
   * The base name is the name of the XML document to be parsed,
   * without the '<code>.xml</code>' extension.
   * There may be an optional properties file with the same 
   * name (and extension '<code>.properties</code>').
   * <p/>
   * If we don't supply a base name, the default <code>config</code>
   * will be used; with '<code>config.xml</code>' as XML document name
   * and '<code>config.properties</code>' as name of the 
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
   * Sets an individual <code>ResourceLoader</code>.
   * <p/>
   * If we don't specify a resource loader,
   * <code>Configurator</code> uses a default resource loader,
   * which tries to get the XML parser's input from a resource
   * (see <code>java.lang.Classloader</code>) 
   * or from a file in the file system.
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
   * <p/>
   * With this method, we can control the sequence of the basic property providers.
   * @param type the provider's type
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
   * Pushes last <code>configure</code> properties onto the stack.
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
   * <p/>
   * We need an individual <code>ParameterFactoryFinder</code>
   * if we want have support for our own parameter types.
   * The instances will be invoked in the descending order of
   * their definition. 
   * The last finder in the stack is a default finder which 
   * includes the <code>ParameterFactory</code>s 
   * for the basic parameter types.
   * <p/>
   * Additionally to the global finders
   * an element node object may implement the <code>DynamicElement</code> interface
   * to provide an individual
   * finder which is used only in the scope of that node.
   * @param finder a parameter factory finder
   */
  public void addParameterFactoryFinder(ParameterFactoryFinder finder) {
    
    parameterManager.pushParameterFactoryFinder(finder);
  }
  
  /**
   * Sets the date pattern.
   * <p/>
   * The default pattern is <code>yyyy-MM-dd</code>.
   * <p/>
   * We can set another pattern in the XML document
   * with the attribute <code>datePattern</code>
   * in a <code>configSettings</code> element.
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
   * Sets constants values for boolean <code>true</code>.
   * <p/>
   * Default values are <code>true</code>, <code>yes</code> and <code>on</code>.
   * @param trueValues The constants representing <code>true</code>
   */
  public void setTrueValues(String[] trueValues) {
    
    parameterManager.setTrueValues(trueValues);
  }
  
  /**
   * Sets constants for boolean <code>false</code>.
   * <p/>
   * Default values are <code>false</code>, <code>no</code> and <code>off</code>.
   * @param falseValues The constants representing <code>false</code>.
   */
  public void setFalseValues(String[] falseValues) {
    
    parameterManager.setFalseValues(falseValues);
  }

  /**
   * Specifies the decimal point character.
   * <p/>
   * By default, or if the passed Character is <code>null</code>,
   * both point and comma are treated as 
   * decimal point characters.
   * <p/>
   * If we set the decimal point character (point or comma) explicitly,
   * the other character (comma or point) is 
   * considered to be a grouping character and is ignored
   * when parsing numeric values.
   * <p/>
   * We can set another decimal point character in the XML document
   * with the attribute <code>decimalPoint</code>
   * in a <code>configSettings</code> element.
   * @param decimalPoint the decimal point character
   */
  public void setDecimalPoint(Character decimalPoint) {
    
    parameterManager.setDecimalPoint(decimalPoint);
  }
  
  /**
   * Enables or disables processing of escape sequences.
   * <p/>
   * By default, escape sequence processing is enabled.
   * <p/>
   * We can switch the setting in the XML document
   * with the attribute <code>processEscape</code>
   * in a <code>configSettings</code> element.
   * @param processEscape <code>true</code> or <code>false</code>
   */
  public void setProcessEscape(boolean processEscape) {
    
    this.processEscape = processEscape;
  }
  
  /**
   * Enables or disables properties resolving.
   * <p/>
   * By default, properties resolving is enabled.
   * <p/>
   * We can switch the setting in the XML document
   * with the attribute <code>processProperties</code>
   * in a <code>configSettings</code> element.
   * @param processProperties <code>true</code> or <code>false</code>
   */
  public void setProcessProperties(boolean processProperties) {
    
    this.processProperties = processProperties;
  }
  
  /**
   * Parses the XML document and sets the root element values.
   * @param <T> the type of our root element
   * @param rootNode our application's root element
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
