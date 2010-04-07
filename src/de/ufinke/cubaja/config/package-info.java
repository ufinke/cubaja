/**
 * Easy access to XML configuration data.
 * <p>
 * <b>Introduction</b>
 * <p>
 * The idea is to unburden the application from interpreting typeless configuration data. 
 * Instead, the application relies on typesafe data which can
 * be retrieved from objects giving a really object oriented view on the configuration.
 * This framework makes configuration issues as easy as coding simple data access objects.
 * <p>
 * The central class of this package is {@link de.ufinke.cubaja.config.Configurator Configurator}.
 * <p>
 * <b>Configuration objects</b>
 * <p>
 * An arbitrary configuration object represents
 * an XML element. Like XML elements, the element node objects may be nested.
 * For every type of XML element, there is a corresponding class.
 * These classes have 'setter' and 'adder' methods; their names begin
 * with <tt>set</tt> or <tt>add</tt>, followed by the name of an XML attribute
 * or the tag name of an XML element. The difference between setter and adder methods is 
 * that setters may be invoked only once (for an attribute value or a unique subelement),
 * whereas adders may be invoked any number of times. 
 * Within an adder method, the passed parameter is typically added to a collection.
 * <p>
 * Setter and adder methods must have a <tt>void</tt> return type and exactly one
 * parameter. Built-in supported parameter types are
 * <ul>
 *   <li>all primitive types and their corresponding object classes</li>
 *   <li><tt>String</tt></li> 
 *   <li><tt>java.util.Date</tt></li>
 *   <li><tt>java.math.BigInteger</tt></li>
 *   <li><tt>java.math.BigDecimal</tt></li>
 *   <li><tt>Enum</tt> types</li>
 *   <li><tt>Class</tt> (the parameter is a class name)</li>
 *   <li>any interfaces (the parameter is the name of an implementing class)</li>
 * </ul>
 * Other types with a public parameterless constructor are considered to be element nodes.
 * Those types do not need to extend or implement supertypes or interfaces.
 * But if such a type implements 
 * {@link de.ufinke.cubaja.config.StartElementHandler StartElementHandler},
 * {@link de.ufinke.cubaja.config.EndElementHandler} 
 * or {@link de.ufinke.cubaja.config.ParameterFactoryProvider}, 
 * the implemented methods will
 * be called during the configuration process to gain more control for special purposes.
 * <p>
 * An attribute value will be passed to the actual element node object
 * as the appropriate parameter type when processing the starting XML element tag.
 * Element content (subelements) 
 * are also passed with the appropriate type to the adder / setter method when
 * processing the XML elements end tag. 
 * In case the parameter type is an array of the types listed above,
 * the attribute value or element content may be a comma separated list which is split 
 * into separate trimmed strings. The strings are processed in the same way as single values
 * and collected in an array.
 * <p>
 * The application passes its root configuration object to the
 * {@link de.ufinke.cubaja.config.Configurator#configure configure}
 * method of a {@link de.ufinke.cubaja.config.Configurator Configurator} instance.
 * Before processing, the 
 * {@link de.ufinke.cubaja.config.Configurator Configurator} may be customized,
 * e.g. by setting the base name of the XML source,
 * applying properties, setting patterns, or providing 
 * {@link de.ufinke.cubaja.config.ParameterFactoryFinder ParameterFactoryFinder}s
 * for our own parameter types.  
 * <p>
 * <b>Properties</b>
 * <p>
 * The XML attribute values or element content may contain properties of the form 
 * <tt>${<i>propertyName</i>}</tt>.
 * Those properties are replaced by their actual values when the
 * parameter types of the setter / adder methods are processed.
 * There are several possible sources for property values, all provided by implementations
 * of {@link de.ufinke.cubaja.config.PropertyProvider PropertyProvider}. 
 * Basic property providers are defined by enum
 * {@link de.ufinke.cubaja.config.PropertyProviderType PropertyProviderType}. 
 * Additionally, you can write your own providers or pass an instance of <tt>java.util.Properties</tt>.
 * <p>
 * The properties search order is defined by the order of  
 * {@link de.ufinke.cubaja.config.Configurator#addPropertyProvider addPropertyProvider} method calls.
 * Basic property providers are automatically appended to the search order 
 * if they were not defined explicitly and there is no <tt>NULL</tt> property provider.
 * The default order is as follows:
 * <ol>
 *   <li>
 *     <tt>SYSTEM</tt>
 *     <br>
 *     System properties.
 *   </li>
 *   <li>
 *     <tt>CONFIG</tt>
 *     <br>
 *     Properties in an optional resource <tt>config.properties</tt>
 *     which is loaded by the resource loader.
 *   </li>
 *   <li>
 *     <tt>XML</tt>
 *     <br>
 *     Properties defined in the XML document with the special element
 *     <br>
 *     <tt>&lt;configProperty name="<i>name</i>" value="<i>value</i>/"&gt;</tt>.
 *   </li>
 *   <li>
 *     <tt>ENVIRONMENT</tt>
 *     <br>
 *     Environment variables.
 *   </li>
 * </ol>
 * <p>
 * The {@link de.ufinke.cubaja.config.Configurator#configure configure} method may be called 
 * more than once on a {@link de.ufinke.cubaja.config.Configurator Configurator} instance.
 * This is useful when a big configuration should be split into several independent files
 * (e.g. technical and end-user responsibility) and the same basic settings should be used.
 * The provider for property type 
 * <tt>XML</tt> is stored in a stack.
 * The properties are searched from the top of the stack downward.
 * On every call to {@link de.ufinke.cubaja.config.Configurator#configure configure} 
 * the actual XML provider is initialized
 * and pushed onto the stack. When 
 * {@link de.ufinke.cubaja.config.Configurator#configure configure}
 * finishes, it is popped off the stack.
 * A call to {@link de.ufinke.cubaja.config.Configurator#pushBaseProperties pushXMLProperties} 
 * pushes the actual provider
 * onto the stack before the next call to
 * {@link de.ufinke.cubaja.config.Configurator#configure configure} parses the next XML document. 
 * There is a corresponding method
 * {@link de.ufinke.cubaja.config.Configurator#popBaseProperties popXMLProperties} 
 * to pop a provider off the stack.
 * <p>
 * Implementations of {@link de.ufinke.cubaja.config.NamedPropertyProvider NamedPropertyProvider} 
 * are not part of the search sequence.
 * The provider is called directly when a <tt>configProperty</tt> element
 * with an attribute '<tt>provider</tt>' is encountered. Such <tt>configProperty</tt>
 * elements may have sub-elements with the tag name '<tt>parm</tt>', containing attributes
 * '<tt>name</tt>' and '<tt>value</tt>'.
 * <p>
 * Named property providers are defined by the application, or within the XML. For the latter,
 * code an element '<tt>configPropertyProvider</tt>' with the attributes
 * '<tt>name</tt>' (the name of the provider) and '<tt>class</tt>' (the implementing class name).
 * The class has to be in the classpath.
 * <p>
 * <b>Includes</b>
 * <p>
 * The special element <tt>configInclude</tt> with an attribute named <tt>include</tt>
 * includes the named resource (or file) while parsing. The root element of the included resource
 * is discarded but its children are processed as if they had been defined in
 * the root document.
 * </p>
 * <b>Settings</b>
 * <p>
 * There is another special element named <tt>configSettings</tt> to set the parsers behaviour.
 * Possible attributes are
 * <ol>
 *   <li>
 *     <tt>datePattern</tt>
 *     <br>
 *     The date pattern for parsing date values.
 *     For a description how to code the pattern see <tt>java.text.SimpleDateFormat</tt>.
 *     Default is <tt>yyyy-MM-dd</tt>.
 *   </li>
 *   <li>
 *     <tt>trueValues</tt>
 *     <br>
 *     A comma separated list of constants representing the boolean value <tt>true</tt>.
 *     Default is <tt>true,yes,on</tt>.
 *   </li>
 *   <li>
 *     <tt>falseValues</tt>
 *     <br>
 *     A comma separated list of constants representing the boolean value <tt>false</tt>.
 *     Default is <tt>false,no,off</tt>.
 *   </li>
 *   <li>
 *     <tt>decimalPoint</tt>
 *     <br>
 *     The decimal point character, which may be a point or a comma.
 *     By default, both characters are processed as decimal point.
 *   </li>
 *   <li>
 *     <tt>processEscape</tt>
 *     <br>
 *     Enables or disables processing of escape characters 
 *     (introduced by backslash, i.e. <tt>\n</tt> for newline).
 *     The values <tt>true</tt>, <tt>yes</tt> or <tt>on</tt> enable
 *     processing, other values disable processing.
 *     By default, processing is enabled.
 *   </li>
 *   <li>
 *     <tt>processProperties</tt>
 *     <br>
 *     Enables or disables processing of properties
 *     (that is, replacement of <tt>${...}</tt> sequences).
 *     The values <tt>true</tt>, <tt>yes</tt> or <tt>on</tt> enable
 *     processing, other values disable processing.
 *     By default, processing is enabled.
 *   </li>
 * </ol>
 * <p>
 * Copyright (c) 2006 - 2010, Uwe Finke. All rights reserved.
 * <br>
 * Subject to 
 * {@link <a href="http://www.opensource.org/licenses/bsd-license.php">BSD License</a>}. 
 * See <tt>license.txt</tt> distributed with this library.
 */
package de.ufinke.cubaja.config;