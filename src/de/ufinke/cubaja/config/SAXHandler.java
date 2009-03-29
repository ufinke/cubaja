// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.helpers.XMLReaderFactory;
import de.ufinke.cubaja.util.Text;

class SAXHandler extends DefaultHandler2 {

  static private Text text = new Text(SAXHandler.class);
  
  private ResourceLoader loader;
  private XMLPropertyProvider xmlProperties;
  private PropertyProvider propertyProvider;
  private Map<String, NamedPropertyProvider> namedProviderMap;
  private Map<Object, Object> infoMap;
  private ParameterManager parameterManager;
  
  private Map<String, IncludeDefinition> includeMap;
  private IncludeDefinition includeDefinition;
    
  private Stack<Locator> locatorStack;
  private Stack<ElementProxy> elementStack;
  private Stack<NamedPropertyValue> propertyStack;
  private Stack<String> includeStack;
  
  private Object rootNode;
  private Locator locator;
  private boolean includedRoot;
  private boolean includedContent;
  
  private boolean processEscape;
  private boolean processProperties;
  
  SAXHandler() {
  
    locatorStack = new Stack<Locator>();
    elementStack = new Stack<ElementProxy>();
    propertyStack = new Stack<NamedPropertyValue>();
    includeStack = new Stack<String>();
    includeMap = new HashMap<String, IncludeDefinition>();
  }
  
  void setRootNode(Object rootNode) {

    this.rootNode = rootNode;
  }
  
  void setLoader(ResourceLoader loader) {
    
    this.loader = loader;
  }
  
  void setXMLProperties(XMLPropertyProvider xmlProperties) {
    
    this.xmlProperties = xmlProperties;
  }
  
  void setPropertyProvider(PropertyProvider propertyProvider) {
    
    this.propertyProvider = propertyProvider;
  }
  
  void setNamedProviderMap(Map<String, NamedPropertyProvider> map) {
    
    namedProviderMap = map;
  }
  
  void setInfoMap(Map<Object, Object> map) {
    
    infoMap = map;
  }
  
  void setParameterManager(ParameterManager parameterManager) {
    
    this.parameterManager = parameterManager;
  }
  
  void setProcessEscape(boolean processEscape) {
    
    this.processEscape = processEscape;
  }
  
  void setProcessProperties(boolean processProperties) {
    
    this.processProperties = processProperties;
  }
  
  void parse(String resourceName) throws ConfigException {
    
    try {
      runXMLReader(resourceName);
    } catch (Exception e) {
      throw createException(e);
    }
  }
  
  private ConfigException createException(Throwable cause) {

    StringBuilder sb = new StringBuilder(500);

    while (cause.getCause() != null) {
      cause = cause.getCause();
    }

    if (cause instanceof ConfigException && cause.getMessage() != null) {
      sb.append(cause.getMessage());
    } else {      
      sb.append(cause.toString());
    }
    
    appendLocation(sb, locatorStack.size());
    
    ConfigException exception = new ConfigException(sb.toString());
    exception.setStackTrace(cause.getStackTrace());
    return exception;
  }
  
  private void appendLocation(StringBuilder sb, int index) {
    
    if (index == 0) {
      return;
    }
    
    index--;
    Locator locatorEntry = locatorStack.get(index);
    
    if (locatorEntry == null) {
      return;
    }
    
    sb.append(" [");
    sb.append(text.get("location", locatorEntry.getPublicId(), Integer.valueOf(locatorEntry.getLineNumber())));
    
    appendLocation(sb, index);
    
    sb.append(']');
  }
  
  private void runXMLReader(String resourceName) throws SAXException {
      
    InputSource source = null;
    
    IncludeDefinition definition = includeMap.get(resourceName);
    if (definition == null) {      
      source = loader.loadResource(resourceName);
      source.setPublicId(resourceName);
    } else {
      source = new InputSource(new StringReader(definition.toString()));
      source.setPublicId(definition.getPublicId());
    }
    
    XMLReader reader = XMLReaderFactory.createXMLReader();
    
    reader.setContentHandler(this);
    reader.setFeature("http://xml.org/sax/features/namespaces", true);
    reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
    reader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
    
    try {      
      reader.parse(source);
    } catch (IOException e) {
      throw new ConfigException(e);
    }
  }
  
  private ElementProxy peekElement() {
    
    ElementProxy element = elementStack.peek();
    if (element.getKind() == ElementKind.INCLUDED_ROOT) {
      element = elementStack.get(elementStack.size() - 2);
    }
    return element;
  }
  
  public void setDocumentLocator(Locator locator) {
    
    this.locator = locator;
  }
  
  public void startDocument() {

    locatorStack.push(locator);
    includedRoot = (locatorStack.size() > 1);
  }
  
  public void endDocument() {

    locatorStack.pop();
  }
  
  public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

    try {
      startElement(localName, atts);
    } catch (NullPointerException e) {
      throw new ConfigException(e);
    }
  }
  
  private void startElement(String localName, Attributes atts) throws SAXException {
    
    ElementKind kind = ElementKind.UNKNOWN;
    if (elementStack.size() == 0) {
      kind = ElementKind.ROOT_NODE;
    } else if (includedRoot) {
      includedRoot = false;
      kind = ElementKind.INCLUDED_ROOT;
    } else {
      ElementKind parentKind = peekElement().getKind();
      switch (parentKind) {        
        case INCLUDE_DEFINITION:
        case INCLUDED_CONTENT:
          kind = ElementKind.INCLUDED_CONTENT;
          break;
        default:
          if (localName.equals("configInclude")) {
            kind = ElementKind.INCLUDE;
          } else if (localName.equals("configProperty")) {
            kind = ElementKind.PROPERTY;
          } else if (localName.equals("configSettings")) {
            kind = ElementKind.SETTINGS;
          } else {        
            switch (parentKind) {
              case ATTRIBUTE:
              case PROPERTY:
              case PROPERTY_PARM:
              case SETTINGS:
                throw new ConfigException(text.get("leaf", peekElement().getName()));
              case PROPERTY_PROVIDER:
                if (! localName.equals("parm")) {
                  throw new ConfigException(text.get("propertyParmName"));
                }
                kind = ElementKind.PROPERTY_PARM;
                break;
            }
          }
      }
    }
    
    ElementProxy element = new ElementProxy(localName, kind);
    
    switch (kind) {
      case UNKNOWN:
      case ROOT_NODE:
        startElement(element, atts);
        break;
      case PROPERTY:
        startProperty(element, atts);
        break;
      case PROPERTY_PARM:
        startPropertyParm(element, atts);
        break;
      case INCLUDE:
        startInclude(element, atts);
        break;
      case INCLUDED_CONTENT:
        startIncludedContent(element, atts);
        break;
      case SETTINGS:
        startSettings(element, atts);
        break;
    }
    
    //System.out.println("push " + localName + ": " + element.getKind());
    elementStack.push(element);
  }
  
  public void endElement(String uri, String localName, String qName) throws SAXException {
    
    try {
      endElement();
    } catch (Exception e) {
      throw new ConfigException(e);
    }
  }
  
  private void endElement() throws SAXException {
    
    ElementProxy element = elementStack.pop();
    //System.out.println("pop  " + element.getName() + ": " + element.getKind());
    
    switch (element.getKind()) {
      case ROOT_NODE:
      case NODE:
      case ATTRIBUTE:
        endElement(element);
        break;
      case PROPERTY_PROVIDER:
        endPropertyProvider(element);
        break;
      case INCLUDED_CONTENT:
        endIncludedContent(element);
        break;
      case INCLUDE:
        endInclude(element);
        break;
      case INCLUDE_DEFINITION:
        endIncludeDefinition(element);
        break;
    }
  }
  
  public void startCDATA() {
    
    if (includedContent) {
      includeDefinition.addText("<![CDATA[");
    } else {      
      peekElement().toggleCData();
    }
  }
  
  public void endCDATA() {
    
    if (includedContent) {
      includeDefinition.addText("]]>");
    } else {      
      peekElement().toggleCData();
    }
  }
  
  public void characters(char[] ch, int start, int length) {
    
    handleCharacters(ch, start, length);
  }
  
  public void ignorableWhitespace(char[] ch, int start, int length) {
    
    handleCharacters(ch, start, length);
  }
  
  private void handleCharacters(char[] ch, int start, int length) {
    
    if (includedContent) {
      includeDefinition.addText(String.valueOf(ch, start, length));
    } else {      
      peekElement().addCharData(ch, start, length);
    }
  }
  
  private void startElement(ElementProxy element, Attributes atts) throws SAXException {
    
    Object node = null;
    
    if (element.getKind() == ElementKind.ROOT_NODE) {
      node = rootNode;
    } else {      
      ElementProxy parentElement = peekElement();
      MethodProxy parentMethod = parentElement.findMethod(element.getName());
      element.setParentMethod(parentMethod);
      ParameterFactory factory = parameterManager.getFactory(parentMethod.getType());
      element.setFactory(factory);
      if (factory.isNode()) {
        try {          
          node = parameterManager.createParameter(factory, element.getName(), parentMethod);
        } catch (Exception e) {
          String clazz = parentMethod.getMethod().getParameterTypes()[0].getName();
          throw new ConfigException(text.get("createNode", clazz, element.getName(), e.toString()));
        }
        element.setKind(ElementKind.NODE);
      } else {
        element.setKind(ElementKind.ATTRIBUTE);
      }
    }
    
    if (node != null) {
      element.setNode(node);
    }
    
    if (element.isManagedNode()) {
      ManagedElement managed = (ManagedElement) node;
      managed.init(infoMap);
    }
    
    if (element.isDynamicNode()) {
      DynamicElement dynamic = (DynamicElement) node;
      parameterManager.pushParameterFactoryFinder(dynamic.parameterFactoryFinder());
    }
    
    if (atts.getLength() > 0) {      
      if (element.getKind() == ElementKind.ATTRIBUTE) {
        throw new ConfigException(text.get("leaf", element.getName()));
      } else {
        for (int i = 0; i < atts.getLength(); i++) {
          setAttribute(element, atts.getLocalName(i), atts.getValue(i));
        }
      }
    }
  }
  
  private void setAttribute(ElementProxy element, String name, String value) throws SAXException {
    
    MethodProxy method = element.findMethod(name);
    if (method == null) {
      throw new ConfigException(text.get("unexpectedAttribute", name));
    }
    
    Object parm = null;
    value = resolve(value, false);
    try {
      ParameterFactory factory = parameterManager.getFactory(method.getType());
      parm = parameterManager.createParameter(factory, value, method);
    } catch (Exception e) {
      throw new ConfigException(text.get("createParameter", value, name, e.getLocalizedMessage()));
    }
    method.invoke(name, element.getNode(), parm);
  }
  
  private void endElement(ElementProxy element) throws SAXException {
    
    Object parm = null;
    
    switch (element.getKind()) {
      
      case ROOT_NODE:
      case NODE:
        
        parm = element.getNode();
        
        element.checkMandatory();
        
        MethodProxy charDataMethod = element.getCharDataMethod();
        if (charDataMethod != null) {
          charDataMethod.invoke(element.getName(), parm, resolve(element.getCharData(), true));
        }

        if (element.isManagedNode()) {
          ManagedElement managed = (ManagedElement) parm;
          managed.finish();
        }
        
        if (element.isDynamicNode()) {
          parameterManager.popParameterFactoryFinder();
        }
        
        break;
        
      case ATTRIBUTE:
        
        String value = resolve(element.getCharData().toString(), true);
        try {
          parm = parameterManager.createParameter(element.getFactory(), value, element.getParentMethod());
        } catch (Exception e) {
          throw new ConfigException(text.get("createParameter", value, element.getName(), e.getLocalizedMessage()));
        }
        break;
    }
    
    if (! (element.getKind() == ElementKind.ROOT_NODE)) {      
      Object parentNode = peekElement().getNode();
      element.getParentMethod().invoke(element.getName(), parentNode, parm);
    }
  }
  
  private void startInclude(ElementProxy element, Attributes atts) throws SAXException {
    
    int includeIndex = atts.getIndex("", "include");
    int defineIndex = atts.getIndex("", "define");
    
    if ((includeIndex == -1 && defineIndex == -1) || (includeIndex > -1 && defineIndex > -1)) {
      throw new ConfigException(text.get("includeAttr"));
    }
    
    if (includeIndex > -1) {
      includeStack.push(resolve(atts.getValue(includeIndex), false));
    }
    
    if (defineIndex > -1) {
      element.setKind(ElementKind.INCLUDE_DEFINITION);
      includeDefinition = new IncludeDefinition(atts.getValue(defineIndex), locatorStack.peek());
      includedContent = true;
    }
  }

  private void endInclude(ElementProxy element) throws SAXException {
    
    runXMLReader(includeStack.pop()); 
  }
  
  private void endIncludeDefinition(ElementProxy element) {
    
    includeMap.put(includeDefinition.getName(), includeDefinition);
    includedContent = false;
  }
  
  private void startIncludedContent(ElementProxy element, Attributes atts) {
    
    includeDefinition.startElement(element.getName(), atts);
  }
  
  private void endIncludedContent(ElementProxy element) {
    
    includeDefinition.endElement(element.getName());
  }
  
  private void startProperty(ElementProxy element, Attributes atts) throws ConfigException {
    
    int nameIndex = atts.getIndex("", "name");
    if (nameIndex == -1) {
      throw new ConfigException(text.get("propertyName"));
    }
    String name = atts.getValue(nameIndex);
    
    int valueIndex = atts.getIndex("", "value");
    int providerIndex = atts.getIndex("", "provider");
    if ((providerIndex == -1 && valueIndex == -1) || (providerIndex > -1 && valueIndex > -1)) {
      throw new ConfigException(text.get("propertyValue"));
    }
    
    if (valueIndex > -1) {
      String value = resolve(atts.getValue(valueIndex), false); 
      setXMLProperty(name, value);
    }
    
    if (providerIndex > -1) {
      element.setKind(ElementKind.PROPERTY_PROVIDER);
      String provider = resolve(atts.getValue(providerIndex), false);
      NamedPropertyValue entry = new NamedPropertyValue(name, provider);
      propertyStack.push(entry);
    }
  }
  
  private void endPropertyProvider(ElementProxy element) throws SAXException {

    NamedPropertyValue entry = propertyStack.pop();
    setXMLProperty(entry.getName(), entry.getProvider(), entry.getParms());
  }
  
  private void startPropertyParm(ElementProxy element, Attributes atts) throws SAXException {
    
    int nameIndex = atts.getIndex("", "name");
    int valueIndex = atts.getIndex("", "value");
    if (nameIndex == -1 || valueIndex == -1) {
      throw new ConfigException(text.get("propertyParmAtts"));
    }
    
    String name = atts.getValue(nameIndex);
    String value = resolve(atts.getValue(valueIndex), false);
    NamedPropertyValue entry = propertyStack.peek();
    entry.addParm(name, value);
  }
  
  private void startSettings(ElementProxy element, Attributes atts) throws SAXException {

    for (int i = 0; i < atts.getLength(); i++) {
      String name = atts.getLocalName(i);
      String value = atts.getValue(i);
      if (name.equals("datePattern")) {
        parameterManager.setDatePattern(value, null);
      } else if (name.equals("decimalPoint")) {
        if (value.length() != 1 || (! ".,".contains(value))) {
          throw new ConfigException(text.get("setDecimalPoint", value));
        }
        parameterManager.setDecimalPoint(value.charAt(0));
      } else if (name.equals("processEscape")) {
        processEscape = ("true,yes,on".contains(value));
      } else if (name.equals("processProperties")) {
        processProperties = ("true,yes,on".contains(value));
      } else {
        throw new ConfigException(text.get("setUnknown", name));
      }
    }
  }
  
  private String resolve(String value, boolean normalize) throws ConfigException {

    if (processProperties) {      
      value = resolveProperties(value);
    }
    if (processEscape) {      
      value = resolveEscape(value);
    }
    if (normalize) {
      value = normalize(value);
    }
    return value;
  }
  
  private String resolveProperties(String value) throws ConfigException {
    
    if (value == null) {
      return "";
    }
    
    int start = value.indexOf("${");
    if (start == -1) {
      return value;
    }

    int end = value.indexOf('}', start);
    if (end == -1) {
      return value;
    }

    // backward, because properties may be nested and initially "${" doesn't match to ending "}"
    start = end - 1;
    while (start > 0 && ! (value.charAt(start) == '{' && value.charAt(start - 1) == '$')) {
      start--;
    }

    if (start > 0) {
      start--;
      String propertyName = value.substring(start + 2, end);
      String propertyValue = propertyProvider.getProperty(propertyName);
      if (propertyValue == null) {
        throw new ConfigException(text.get("undefinedProperty", propertyName));
      }
      end++;
      StringBuilder sb = new StringBuilder(value.length() - propertyName.length() + propertyValue.length());
      sb.append(value.substring(0, start));
      sb.append(propertyValue);
      sb.append(value.substring(end));
      return resolveProperties(sb.toString()); // more properties?
    }

    return value;
  }  
  
  private String resolveEscape(String value) throws ConfigException {
    
    if (value.indexOf('\\') == -1) {
      return value;
    }
    
    StringBuilder sb = new StringBuilder(value.length());
    StringBuilder unicode = null;
    boolean cdata = false;
    boolean escapePending = false;
    
    for (int i = 0; i < value.length(); i++) {
      
      char c = value.charAt(i);
      
      if (c == '\uFFFF') {
        
        cdata = ! cdata;
        sb.append(c);
        
      } else if (unicode != null) {
        
        unicode.append(c);
        if (unicode.length() == 4) {
          String hex = unicode.toString();    
          unicode = null;
          try {
            char ch = (char) (Integer.parseInt(hex, 16));
            if (! Character.isDefined(c)) {
              throw new NumberFormatException();
            }
            sb.append(ch);
          } catch (NumberFormatException e) {
            throw new ConfigException(text.get("invalidUnicode", "\\u" + hex));
          }
        }
        
      } else if (escapePending) {
        
        switch (c) {
          case 'f':
            sb.append('\f');
            break;
          case 'n':
            sb.append('\n');
            break;
          case 'r':
            sb.append('\r');
            break;
          case 't':
            sb.append('\t');
            break;
          case '\'':
            sb.append('\'');
            break;
          case '\"':
            sb.append('\"');
            break;
          case '\\':
            sb.append('\\');
            break;
          case 'u':
            unicode = new StringBuilder();
            break;
          default:
            throw new ConfigException(text.get("invalidEscape", "\\" + c));
        }
        
        escapePending = false;
        
      } else if (c == '\\') {
        
        escapePending = true;
                
      } else {
        
        sb.append(c);
        
      }
    }
    
    return sb.toString();
  }
  
  private String normalize(String value) {
    
    StringBuilder sb = new StringBuilder(value.length());

    boolean whitespaceInserted = true;
    boolean spacePending = false;
    boolean cdata = false;
    
    for (int i = 0; i < value.length(); i++) {
      
      char c = value.charAt(i);

      if (c == '\uFFFF') {
        cdata = ! cdata;
      } else if (cdata) {
        if (spacePending) {
          sb.append(' ');
          spacePending = false;
        }
        sb.append(c);
        whitespaceInserted = false;
      } else if (c == ' ') {
        spacePending = ! whitespaceInserted;
      } else if (Character.isWhitespace(c)) {
        sb.append(c);
        whitespaceInserted = true;
        spacePending = false;
      } else {
        if (spacePending) {
          sb.append(' ');
          spacePending = false;
        }
        sb.append(c);
        whitespaceInserted = false;
      }
    }
    
    return sb.toString();
  }
  
  private void setXMLProperty(String key, String value) {
    
    xmlProperties.setProperty(key, value);
  }
  
  private void setXMLProperty(String key, String providerName, Map<String, String> parms) throws ConfigException {
    
    NamedPropertyProvider provider = namedProviderMap.get(providerName);
    if (provider == null) {
      throw new ConfigException(text.get("namedProvider", providerName));
    }
    
    String value = provider.getProperty(key, parms);
    if (value == null) {
      throw new ConfigException(text.get("namedProperty", key, providerName));
    }
    
    setXMLProperty(key, value);
  }
  
}
