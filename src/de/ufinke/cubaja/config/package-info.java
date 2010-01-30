/**
 * <p>
 * Classes for easy access to XML configuration data.
 * </p>
 * Examples see <a href="./doc-files/cubaja_config_en.pdf" hreflang="en" target="_blank">HowTo</a>
 * (<a href="./doc-files/cubaja_config_de.pdf" hreflang="de" target="_blank">German</a>).
 * <p>
 * The idea is to eliminate code for interpreting typeless configuration data
 * from the application. The application relies on typesafe data which can
 * be retrieved from instances representing an object oriented view of the configuration.
 * This framework makes configuration issues nearly as easy as coding simple data access objects.
 * </p>
 * <p>
 * The central class of this package is <code>Configurator</code>.
 * </p>
 * <p>
 * An arbitrary object represents
 * an XML element. Like XML elements, the element node objects may be nested.
 * For every type of XML element, we code a separate class.
 * These classes have 'setter' and 'adder' methods; that is, their names begin
 * with <code>set</code> or <code>add</code>, followed by the name of an XML attribute
 * or the tag name of an XML element. The difference between setter and adder methods is, 
 * that setters may be invoked only once (for an attribute value or a unique subelement),
 * whereas adders may be invoked any number of times 
 * (within an adder method, the passed parameter is typically added to a collection).
 * </p>
 * <p>
 * Setter and adder methods must have a <code>void</code> return type and exactly one
 * parameter. Builtin supported parameter types are
 * <code>Enum</code>s, all primitive types and their corresponding object classes, 
 * <code>String</code>, <code>java.util.Date</code>, <code>java.math.BigInteger</code>,
 * <code>java.math.BigDecimal</code>, <code>Class</code> and interfaces.
 * Other types with a public parameterless constructor are considered to be element nodes.
 * Those types need not to extend or implement supertypes or interfaces.
 * But if such a type implements <code>StartElementHandler</code>,
 * <code>EndElementHandler</code>, <code>ParameterFactoryFinder</code> or
 * <code>ParameterFactoryProvider</code>, the implemented methods will
 * be called during the configuration process and we gain more control for special purposes.
 * </p>
 * <p>
 * An attribute value will be passed to the actual element node object
 * as the appropriate parameter type when processing the starting XML element tag.
 * Element content (subelements) 
 * are also passed with the appropriate type to the adder / setter method when
 * processing the XML elements end tag. 
 * In case the parameter type is an array of the types listed above,
 * the attribute value or element content may be a comma separated list which is split 
 * into separate trimmed strings. The strings then are processed in the same way as single values
 * and collected in an array.
 * </p>
 * <p>
 * The application passes its root configuration object to
 * the method <code>configure</code> of a <code>Configurator</code> instance.
 * Before doing so, we can customize the <code>Configurator</code>,
 * e.g. by setting the base name of the XML source,
 * applying properties, setting patterns, or providing <code>ParameterFactoryFinder</code>s
 * for our own parameter types.  
 * </p>
 * <p>
 * The XML attribute values or element content may contain properties in the form 
 * <code>${<i>propertyName</i>}</code>.
 * Those properties are replaced by their actual values when the
 * parameter types of the setter / adder methods are processed.
 * There are several possible sources for property values, all provided by implementations
 * of <code>PropertyProvider</code>. Basic property providers are defined by enum
 * <code>PropertyProviderType</code>. 
 * Additionally, we can write our own providers or pass an instance of <code>java.util.Properties</code>.
 * </p>
 * <p>
 * The properties search order is defined by the order of  
 * <code>addPropertyProvider</code> method calls.
 * Basic property providers are automatically appended to the search order 
 * if we did not define them explicitly and we did not add the <code>NULL</code> property provider.
 * The default order is as follows:
 * <ol>
 *   <li>
 *     <code>SYSTEM</code>
 *     <br/>
 *     System properties.
 *   </li>
 *   <li>
 *     <code>BASE_PROPERTIES</code>
 *     <br/>
 *     Properties in an optional file <code><i>baseName</i>.properties</code>
 *     corresponding to the XML source <code><i>baseName</i>.xml</code>.
 *   </li>
 *   <li>
 *     <code>BASE_XML</code>
 *     <br/>
 *     Properties defined in the XML document with the special element tag
 *     <br/>
 *     <code>&lt;configProperty name="<i>name</i>" value="<i>value</i>/"&gt;</code>.
 *   </li>
 *   <li>
 *     <code>ENVIRONMENT</code>
 *     <br/>
 *     Environment variables.
 *   </li>
 * </ol>
 * </p>
 * <p>
 * We can invoke <code>configure</code> more than once on a <code>Configurator</code> instance.
 * This is useful when we split a big configuration into several independent files
 * (e.g. technical and end-user responsibility) and we want to use the same basic settings.
 * The providers for the property types 
 * <code>BASE_PROPERTIES</code> and <code>BASE_XML</code> are stored in stacks.
 * The properties are searched from the top of the stack downward.
 * On every call to <code>configure</code> the actual base providers are initialized
 * and pushed to the stack. When <code>configure</code> finishes, they are popped off the stack.
 * A call to <code>pushBaseProperties</code> pushes the base providers
 * (with properties file and XML defined properties) created by the previous call to
 * <code>configure</code> onto the stack before we let
 * <code>configure</code> parse the next XML document. There is a corresponding method
 * <code>popBaseProperties</code> to pop the providers off the stack.
 * </p>
 * <p>
 * Implementations of <code>NamedPropertyProvider</code> are not part of the search sequence.
 * The provider is called directly when a <code>configProperty</code> element
 * with an attribute '<code>provider</code>' is encountered. Such <code>configProperty</code>
 * elements may have sub-elements with the tag name '<code>parm</code>', containing attributes
 * '<code>name</code>' and '<code>value</code>'.
 * </p>
 * <p>
 * The special element <code>configInclude</code> with an attribute named <code>include</code>
 * includes the named resource (or file) while parsing. The root element of the included resource
 * is discarded but its children are processed as if they had been defined in
 * the root document.
 * </p>
 * There is another special element named <code>configSettings</code> to set the parsers behaviour.
 * Possible attributes are
 * <ol>
 *   <li>
 *     <code>datePattern</code>
 *     <br/>
 *     The date pattern for parsing date values.
 *     For a description how to code the pattern see <code>java.text.SimpleDateFormat</code>.
 *     Default is <code>yyyy-MM-dd</code>.
 *   </li>
 *   <li>
 *     <code>trueValues</code>
 *     <br/>
 *     A comma separated list of constants representing the boolean value <code>true</code>.
 *     Default is <code>true,yes,on</code>.
 *   </li>
 *   <li>
 *     <code>falseValues</code>
 *     <br/>
 *     A comma separated list of constants representing the boolean value <code>false</code>.
 *     Default is <code>false,no,off</code>.
 *   </li>
 *   <li>
 *     <code>decimalPoint</code>
 *     <br/>
 *     The decimal point character, which may be a point or a comma.
 *     By default, both characters are processed as decimal point.
 *   </li>
 *   <li>
 *     <code>processEscape</code>
 *     <br/>
 *     Enables or disables processing of escape characters 
 *     (introduced by backslash, i.e. <code>\n</code> for newline).
 *     The values <code>true</code>, <code>yes</code> or <code>on</code> enable
 *     processing, other values disable processing.
 *     By default, processing is enabled.
 *   </li>
 *   <li>
 *     <code>processProperties</code>
 *     <br/>
 *     Enables or disables processing of properties
 *     (that is, replacement of <code>${...}</code> sequences).
 *     The values <code>true</code>, <code>yes</code> or <code>on</code> enable
 *     processing, other values disable processing.
 *     By default, processing is enabled.
 *   </li>
 * </ol>
 * <p>
 * Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
 * <br/>
 * Subject to 
 * {@link <a href="http://www.opensource.org/licenses/bsd-license.php">BSD License</a>}. 
 * See <code>license.txt</code> distributed with this package.
 * </p>
 */
package de.ufinke.cubaja.config;