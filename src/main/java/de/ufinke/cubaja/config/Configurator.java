// Copyright (c) 2008 - 2011, Uwe Finke. All rights reserved.
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
 * When the <code>configure</code> method is invoked,
 * and no <code>NULL</code> property provider
 * had been added, 
 * all default property providers not already specified are
 * added automatically in the order 
 * <code>SYSTEM</code>,
 * <code>CONFIG</code>,
 * <code>XML</code>,
 * <code>ENVIRONMENT</code>.
 * @author Uwe Finke
 */
public class Configurator {

  private String name;
  private ResourceLoader loader;
  
  private Stack<XMLPropertyProvider> baseXMLStack;
  private XMLPropertyProvider lastXmlProvider;
  
  private PropertyProvider xmlProvider;
  private ConfigPropertyProvider configProvider;  
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
    
    name = System.getProperty("de.ufinke.cubaja.config.name", "config.xml");
    loader = new DefaultResourceLoader();
    
    parameterManager = new ParameterManager();
    infoMap = new HashMap<Object, Object>();
    
    baseXMLStack = new Stack<XMLPropertyProvider>();
    
    namedProviderMap = new HashMap<String, NamedPropertyProvider>();
    providerSequenceList = new ArrayList<PropertyProvider>();
    
    masterProvider = new PropertyProvider() {
      
      public String getProperty(String key) throws Exception {
        
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
   * With the map, arbitrary information may be passed between element node instances.
   * There is one map per <code>Configurator</code> instance.
   * @return the shared map
   */  
  public Map<Object, Object> infoMap() {
    
    return infoMap;
  }
  
  /**
   * Sets the name of the XML document.
   * <p>
   * If the name does not end with '<code>.xml</code>', the extension will be
   * added automatically.
   * <p>
   * If there is no explicit name, the name '<code>config.xml</code>'
   * will be used by default.
   * @param name the XML document name
   */
  public void setName(String name) {
    
    if (name == null) {
      name = "config.xml";
    } else if (! name.toLowerCase().endsWith(".xml")) {
      name = name + ".xml";
    }
    this.name = name;
  }
  
  /**
   * Returns the XML document name.
   * @return the XML name
   */
  public String getName() {
    
    return name;
  }
  
  /**
   * Sets an individual <code>ResourceLoader</code>.
   * <p>
   * If no resource loader is explicitly specified,
   * <code>Configurator</code> uses a default resource loader
   * which tries to get the XML parser's input from a resource
   * (see {@link java.lang.ClassLoader ClassLoader}) 
   * or from a file in the file system.
   * If the default resource loader finds a system property <code>de.ufinke.cubaja.config.base</code>
   * then all resource names will be prefixed with the value of that system property.
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
        
      case CONFIG:
        
        configProvider = new ConfigPropertyProvider();
        addPropertyProvider(configProvider);
        
        break;
        
      case XML:
        
        xmlProvider = new PropertyProvider() {
          
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
        addPropertyProvider(xmlProvider);
        
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
  
  private void finishProviders() {
    
    if (nullProvider) {
      return;
    }
    
    if (systemProvider == null) {
      addPropertyProvider(PropertyProviderType.SYSTEM);
    }
    
    if (configProvider == null) {
      addPropertyProvider(PropertyProviderType.CONFIG);
    }
    
    if (xmlProvider == null) {
      addPropertyProvider(PropertyProviderType.XML);
    }
    
    if (environmentProvider == null) {
      addPropertyProvider(PropertyProviderType.ENVIRONMENT);
    }
  }

  /**
   * Pushes XML properties of last <code>configure</code> onto the stack.
   */
  public void pushXMLProperties() {
    
    if (lastXmlProvider == null) {
      lastXmlProvider = new XMLPropertyProvider();
    }
    baseXMLStack.push(lastXmlProvider);
  }
  
  /**
   * Pops XML properties off the stack.
   */
  public void popXMLProperties() {
    
    if (baseXMLStack.size() == 0) {
      return;
    }
    lastXmlProvider = baseXMLStack.pop();
  }
  
  /**
   * Adds a parameter factory finder.
   * <p>
   * An individual <code>ParameterFactoryFinder</code>
   * may be set to support special parameter types.
   * The instances will be invoked in the descending order of
   * their definition. 
   * The last finder in the stack is a default finder which 
   * includes the <code>ParameterFactory</code>s 
   * for the basic parameter types.
   * @param finder a parameter factory finder
   */
  public void addParameterFactoryFinder(ParameterFactoryFinder finder) {
    
    parameterManager.pushParameterFactoryFinder(finder);
  }
  
  /**
   * Sets the date pattern.
   * <p>
   * The default pattern is <code>yyyy-MM-dd</code>.
   * <p>
   * The pattern may also be set in the XML document
   * with the attribute <code>datePattern</code>
   * in a <code>configSettings</code> element.
   * @param pattern The date pattern
   * @param hint a user-friendly pattern used in exception messages
   */
  public void setDatePattern(String pattern, String hint) {
    
    parameterManager.setDatePattern(pattern, hint, false);
  }
  
  /**
   * Sets the date pattern with the pattern itself as hint.
   * @param pattern the date pattern
   */
  public void setDatePattern(String pattern) {
    
    setDatePattern(pattern, pattern);
  }
  
  /**
   * Sets constant values for boolean <code>true</code>.
   * <p>
   * Default values are <code>true</code>, <code>yes</code> and <code>on</code>.
   * @param trueValues The constants representing <code>true</code>
   */
  public void setTrueValues(String[] trueValues) {
    
    parameterManager.setTrueValues(trueValues);
  }
  
  /**
   * Sets constants for boolean <code>false</code>.
   * <p>
   * Default values are <code>false</code>, <code>no</code> and <code>off</code>.
   * @param falseValues The constants representing <code>false</code>.
   */
  public void setFalseValues(String[] falseValues) {
    
    parameterManager.setFalseValues(falseValues);
  }

  /**
   * Specifies the decimal point character.
   * <p>
   * By default, or if the passed Character is <code>null</code>,
   * both point and comma are treated as 
   * decimal point characters.
   * <p>
   * If the decimal point character (point or comma) is set explicitly,
   * the other character (comma or point) is 
   * considered to be a grouping character and is ignored
   * when parsing numeric values.
   * <p>
   * The decimal point character may also be set in the XML document
   * with the attribute <code>decimalPoint</code>
   * in a <code>configSettings</code> element.
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
   * with the attribute <code>processEscape</code>
   * in a <code>configSettings</code> element.
   * @param processEscape <code>true</code> or <code>false</code>
   */
  public void setProcessEscape(boolean processEscape) {
    
    this.processEscape = processEscape;
  }
  
  /**
   * Enables or disables property resolving.
   * <p>
   * By default, property resolving is enabled.
   * <p>
   * The switch may also be set in the XML document
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
   * @param rootNode the object representing the root element
   * @return the root element
   * @throws ConfigException when configuration failed
   */
  public <T> T configure(T rootNode) throws ConfigException {
    
    finishProviders();
    lastXmlProvider = new XMLPropertyProvider();
    pushXMLProperties();
    if (configProvider != null) {
      try {
        configProvider.load(loader);
      } catch (ConfigException e) {
        throw e;
      } catch (Throwable t) {
        throw new ConfigException(t);
      }
    }
    
    SAXHandler saxHandler = new SAXHandler();
    
    saxHandler.setParameterManager(parameterManager);
    saxHandler.setInfoMap(infoMap);
    saxHandler.setRootNode(rootNode);
    saxHandler.setLoader(loader);
    saxHandler.setXMLProperties(lastXmlProvider);
    saxHandler.setPropertyProvider(masterProvider);
    saxHandler.setNamedProviderMap(namedProviderMap);
    saxHandler.setProcessEscape(processEscape);
    saxHandler.setProcessProperties(processProperties);
    
    saxHandler.parse(name);
    
    popXMLProperties();
    
    return rootNode;
  }
  
}
