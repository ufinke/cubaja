/**
 * <p>
 * Easy access to XML configuration data.
 * </p>
 * <b>Introduction</b>
 * <p>
 * The idea is to unburden the application from interpreting typeless configuration data. 
 * Instead, the application relies on typesafe data which can
 * be retrieved from objects giving a really object oriented view on the configuration.
 * This framework makes configuration issues as easy as coding simple data access objects.
 * </p><p>
 * The central class of this package is {@link Configurator}.
 * </p>
 * <b>Configuration objects</b>
 * <p>
 * An arbitrary configuration object represents
 * an XML element. Like XML elements, the element node objects may be nested.
 * For every type of XML element, there is a corresponding class.
 * These classes have 'setter' and 'adder' methods; their names begin
 * with <code>set</code> or <code>add</code>, followed by the name of an XML attribute
 * or the tag name of an XML element. The difference between setter and adder methods is 
 * that setters may be invoked only once (for an attribute value or a unique subelement),
 * whereas adders may be invoked any number of times. 
 * Within an adder method, the passed parameter is typically added to a collection.
 * </p><p>
 * Setter and adder methods must have a <code>void</code> return type and exactly one
 * parameter. Built-in supported parameter types are
 * </p>
 * <ul>
 *   <li>all primitive types and their corresponding object classes</li>
 *   <li><code>String</code></li> 
 *   <li><code>java.util.Date</code></li>
 *   <li><code>java.math.BigInteger</code></li>
 *   <li><code>java.math.BigDecimal</code></li>
 *   <li><code>Enum</code> types</li>
 *   <li><code>Class</code> (the parameter is a class name)</li>
 *   <li>any interfaces (the parameter is the name of an implementing class)</li>
 * </ul>
 * <p>
 * Other types with a public parameterless constructor are considered to be element nodes.
 * Those types do not need to extend or implement supertypes or interfaces.
 * But if such a type implements 
 * {@link StartElementHandler},
 * {@link EndElementHandler} 
 * or {@link ParameterFactoryProvider}, 
 * the implemented methods will
 * be called during the configuration process to gain more control for special purposes.
 * </p><p>
 * An attribute value will be passed to the actual element node object
 * as the appropriate parameter type when processing the starting XML element tag.
 * Element content (subelements) 
 * is also passed with the appropriate type to the adder / setter method when
 * processing the XML element's end tag. 
 * In case the parameter type is an array of the types listed above,
 * the attribute value or element content may be a comma separated list which is split 
 * into separate trimmed strings. The strings are processed in the same way as single values
 * and collected in an array.
 * </p><p>
 * The application passes its root configuration object to the
 * {@link de.ufinke.cubaja.config.Configurator#configure configure}
 * method of a <code>Configurator</code> instance.
 * Before processing, the <code>Configurator</code> may be customized,
 * e.g. by setting the name of the XML source,
 * applying properties, setting patterns, or providing 
 * {@link ParameterFactoryFinder}s
 * for your own parameter types.  
 * </p>
 * <b>Properties</b>
 * <p>
 * The XML attribute values or element content may contain properties of the form 
 * <code>${<i>propertyName</i>}</code>.
 * Those properties are replaced by their actual values when the
 * parameter types of the setter / adder methods are processed.
 * There are several possible sources for property values, all provided by implementations
 * of {@link PropertyProvider}. 
 * Basic property providers are defined by enum
 * {@link PropertyProviderType}. 
 * Additionally, you can write your own providers or pass an instance of <code>java.util.Properties</code>.
 * </p><p>
 * The properties' search order is defined by the order of  
 * {@link de.ufinke.cubaja.config.Configurator#addPropertyProvider addPropertyProvider} method calls.
 * Basic property providers are automatically appended to the search order 
 * if they were not defined explicitly and there is no <code>NULL</code> property provider.
 * The default order is as follows:
 * </p>
 * <ol>
 *   <li>
 *     <code>SYSTEM</code>
 *     <br>
 *     System properties.
 *   </li>
 *   <li>
 *     <code>CONFIG</code>
 *     <br>
 *     Properties in an optional resource <code>config.properties</code>
 *     which is loaded by the resource loader.
 *   </li>
 *   <li>
 *     <code>XML</code>
 *     <br>
 *     Properties defined in the XML document with the special element
 *     <br>
 *     <code>&lt;configProperty name="<i>name</i>" value="<i>value</i>/"&gt;</code>.
 *   </li>
 *   <li>
 *     <code>ENVIRONMENT</code>
 *     <br>
 *     Environment variables.
 *   </li>
 * </ol>
 * <p>
 * The {@link de.ufinke.cubaja.config.Configurator#configure configure} method may be called 
 * more than once on an <code>Configurator</code> instance.
 * This is useful when a big configuration should be split into several independent files
 * (e.g. technical and end-user responsibility) and the same basic settings should be used.
 * The provider for property type 
 * <code>XML</code> is stored in a stack.
 * The properties are searched from the top of the stack downward.
 * On every call to {@link de.ufinke.cubaja.config.Configurator#configure configure},
 * the actual XML provider is initialized
 * and pushed onto the stack. When 
 * {@link de.ufinke.cubaja.config.Configurator#configure configure}
 * finishes, it is popped off the stack.
 * A call to {@link de.ufinke.cubaja.config.Configurator#pushXMLProperties pushXMLProperties} 
 * pushes the actual provider
 * onto the stack before the next call to
 * {@link de.ufinke.cubaja.config.Configurator#configure configure} parses the next XML document. 
 * There is a corresponding method
 * {@link de.ufinke.cubaja.config.Configurator#popXMLProperties popXMLProperties} 
 * to pop a provider off the stack.
 * </p><p>
 * Implementations of {@link NamedPropertyProvider} 
 * are not part of the search sequence.
 * The provider is called directly when a <code>configProperty</code> element
 * with an attribute '<code>provider</code>' is encountered. Such <code>configProperty</code>
 * elements may have sub-elements with the tag name '<code>parm</code>', containing attributes
 * '<code>name</code>' and '<code>value</code>'.
 * </p><p>
 * Named property providers are defined by the application, or within the XML. For the latter,
 * code an element '<code>configPropertyProvider</code>' with the attributes
 * '<code>name</code>' (the name of the provider) and '<code>class</code>' (the implementing class name).
 * The class has to be in the classpath.
 * </p><p>
 * <b>Includes</b>
 * </p><p>
 * The special element <code>configInclude</code> allows to include XML from other resources.
 * The element requires one of the following attributes:
 * </p>
 * <ol>
 * 	 <li>
 * 		 <code>include</code>
 * 	   <br>
 *     Name of a resource. The root element of the included resource
 *     is discarded but its children are processed as if they had been defined in
 *     the root document.
 * 	 </li>
 * 	 <li>
 * 		 <code>includeOptional</code>
 * 	   <br>
 *     Name of an optional resource.
 *     Same as <code>include</code>, but no exception is thrown when the resource could not be found. 
 * 	 </li>
 * 	 <li>
 * 		 <code>define</code>
 * 	   <br>
 *     Name of an inline XML block.
 *     The block may be included later like a normal classpath or file resource. 
 * 	 </li>
 * </ol>
 * <p>
 * <b>Settings</b>
 * <p>
 * There is another special element named <code>configSettings</code> to set the parser's behaviour.
 * Possible attributes are
 * </p>
 * <ol>
 *   <li>
 *     <code>datePattern</code>
 *     <br>
 *     The date pattern for parsing date values.
 *     For a description how to code the pattern see {@link java.text.SimpleDateFormat}.
 *     Default is <code>yyyy-MM-dd</code>.
 *   </li>
 *   <li>
 *     <code>trueValues</code>
 *     <br>
 *     A comma separated list of constants representing the boolean value <code>true</code>.
 *     Default is <code>true,yes,on</code>.
 *   </li>
 *   <li>
 *     <code>falseValues</code>
 *     <br>
 *     A comma separated list of constants representing the boolean value <code>false</code>.
 *     Default is <code>false,no,off</code>.
 *   </li>
 *   <li>
 *     <code>decimalPoint</code>
 *     <br>
 *     The decimal point character, which may be a point or a comma.
 *     By default, both characters are processed as decimal point.
 *   </li>
 *   <li>
 *     <code>processEscape</code>
 *     <br>
 *     Enables or disables processing of escape characters 
 *     (introduced by backslash, i.e. <code>\n</code> for newline).
 *     The values <code>true</code>, <code>yes</code> or <code>on</code> enable
 *     processing, other values disable processing.
 *     By default, processing is enabled.
 *   </li>
 *   <li>
 *     <code>processProperties</code>
 *     <br>
 *     Enables or disables processing of properties
 *     (that is, replacement of <code>${...}</code> sequences).
 *     The values <code>true</code>, <code>yes</code> or <code>on</code> enable
 *     processing, other values disable processing.
 *     By default, processing is enabled.
 *   </li>
 * </ol>
 * <p>
 * Copyright (c) 2006 - 2021, Uwe Finke. All rights reserved.
 * <br>
 * Subject to 
 * <a href="http://www.opensource.org/licenses/bsd-license.php">BSD License</a>. 
 * See <code>license.txt</code> distributed with this library.
 * </p>
 */
package de.ufinke.cubaja.config;