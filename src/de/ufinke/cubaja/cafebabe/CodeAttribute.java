// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.ufinke.cubaja.util.Text;

/**
 * Code portion of a method.
 * An instance is supplied by {@link GenMethod#getCode() getCode} in <tt>GenMethod</tt>.
 * <p>
 * Java instructions are added to the code by calling 
 * methods which generate the opcodes and their parameters.
 * Just as in assembler languages, labels may be defined to mark branch target points.
 * Local variables may be identified by their index number, or by name.
 * Constant pool entries are added automatically when needed.
 * <p>
 * The Java VM opcodes are implemented by the following methods:<blockquote>
 * <table border="0" cellspacing="3" cellpadding="2" summary="Attributes and subelements.">
 * <tr bgcolor="#ccccff">
 * <th align="left">opcode</th>
 * <th align="left">mnemonic</th>
 * <th align="center">implementing method(s)</th>
 * </tr>
 * <tr align="left" valign="top"><td><tt>00</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc10.html#nop">nop</a>}</tt></td><td>{@link #nop()}</td></tr>
 * <tr align="left" valign="top"><td><tt>01</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#aconst_null">aconst_null</a>}</tt></td><td>{@link #loadNull()}</td></tr>
 * <tr align="left" valign="top"><td><tt>02</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iconst_i">iconst_m1</a>}</tt></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr align="left" valign="top"><td><tt>03</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iconst_i">iconst_0</a>}</tt></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr align="left" valign="top"><td><tt>04</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iconst_i">iconst_1</a>}</tt></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr align="left" valign="top"><td><tt>05</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iconst_i">iconst_2</a>}</tt></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr align="left" valign="top"><td><tt>06</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iconst_i">iconst_3</a>}</tt></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr align="left" valign="top"><td><tt>07</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iconst_i">iconst_4</a>}</tt></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr align="left" valign="top"><td><tt>08</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iconst_i">iconst_5</a>}</tt></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr align="left" valign="top"><td><tt>09</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lconst_l">lconst_0</a>}</tt></td><td>{@link #loadConstant(long)}</td></tr>
 * <tr align="left" valign="top"><td><tt>0A</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lconst_l">lconst_1</a>}</tt></td><td>{@link #loadConstant(long)}</td></tr>
 * <tr align="left" valign="top"><td><tt>0B</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fconst_f">fconst_0</a>}</tt></td><td>{@link #loadConstant(float)}</td></tr>
 * <tr align="left" valign="top"><td><tt>0C</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fconst_f">fconst_1</a>}</tt></td><td>{@link #loadConstant(float)}</td></tr>
 * <tr align="left" valign="top"><td><tt>0D</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fconst_f">fconst_2</a>}</tt></td><td>{@link #loadConstant(float)}</td></tr>
 * <tr align="left" valign="top"><td><tt>0E</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dconst_d">dconst_0</a>}</tt></td><td>{@link #loadConstant(double)}</td></tr>
 * <tr align="left" valign="top"><td><tt>0F</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dconst_d">dconst_1</a>}</tt></td><td>{@link #loadConstant(double)}</td></tr>
 * <tr align="left" valign="top"><td><tt>10</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc1.html#bipush">bipush</a>}</tt></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr align="left" valign="top"><td><tt>11</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc13.html#sipush">sipush</a>}</tt></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr align="left" valign="top"><td><tt>12</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#ldc">ldc</a>}</tt></td><td>{@link #loadConstant(int)}, {@link #loadConstant(float)}, {@link #loadConstant(Type)}}</td></tr>
 * <tr align="left" valign="top"><td><tt>13</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#ldc_w">ldc_w</a>}</tt></td><td>{@link #loadConstant(int)}, {@link #loadConstant(float)}, {@link #loadConstant(Type)}}</td></tr>
 * <tr align="left" valign="top"><td><tt>14</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#ldc2_w">ldc2_w</a>}</tt></td><td>{@link #loadConstant(long)}, {@link #loadConstant(double)}</td></tr>
 * <tr align="left" valign="top"><td><tt>15</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iload">iload</a>}</tt></td><td>{@link #loadLocalInt(int)}, {@link #loadLocalInt(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>16</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lload">lload</a>}</tt></td><td>{@link #loadLocalLong(int)}, {@link #loadLocalLong(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>17</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fload">fload</a>}</tt></td><td>{@link #loadLocalFloat(int)}, {@link #loadLocalFloat(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>18</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dload">dload</a>}</tt></td><td>{@link #loadLocalDouble(int)}, {@link #loadLocalDouble(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>19</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#aload">aload</a>}</tt></td><td>{@link #loadLocalReference(int)}, {@link #loadLocalReference(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>1A</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iload_n">iload_0</a>}</tt></td><td>{@link #loadLocalInt(int)}, {@link #loadLocalInt(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>1B</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iload_n">iload_1</a>}</tt></td><td>{@link #loadLocalInt(int)}, {@link #loadLocalInt(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>1C</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iload_n">iload_2</a>}</tt></td><td>{@link #loadLocalInt(int)}, {@link #loadLocalInt(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>1D</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iload_n">iload_3</a>}</tt></td><td>{@link #loadLocalInt(int)}, {@link #loadLocalInt(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>1E</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lload_n">lload_0</a>}</tt></td><td>{@link #loadLocalLong(int)}, {@link #loadLocalLong(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>1F</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lload_n">lload_1</a>}</tt></td><td>{@link #loadLocalLong(int)}, {@link #loadLocalLong(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>20</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lload_n">lload_2</a>}</tt></td><td>{@link #loadLocalLong(int)}, {@link #loadLocalLong(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>21</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lload_n">lload_3</a>}</tt></td><td>{@link #loadLocalLong(int)}, {@link #loadLocalLong(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>22</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fload_n">fload_0</a>}</tt></td><td>{@link #loadLocalFloat(int)}, {@link #loadLocalFloat(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>23</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fload_n">fload_1</a>}</tt></td><td>{@link #loadLocalFloat(int)}, {@link #loadLocalFloat(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>24</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fload_n">fload_2</a>}</tt></td><td>{@link #loadLocalFloat(int)}, {@link #loadLocalFloat(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>25</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fload_n">fload_3</a>}</tt></td><td>{@link #loadLocalFloat(int)}, {@link #loadLocalFloat(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>26</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dload_n">dload_0</a>}</tt></td><td>{@link #loadLocalDouble(int)}, {@link #loadLocalDouble(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>27</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dload_n">dload_1</a>}</tt></td><td>{@link #loadLocalDouble(int)}, {@link #loadLocalDouble(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>28</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dload_n">dload_2</a>}</tt></td><td>{@link #loadLocalDouble(int)}, {@link #loadLocalDouble(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>29</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dload_n">dload_3</a>}</tt></td><td>{@link #loadLocalDouble(int)}, {@link #loadLocalDouble(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>2A</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#aload_n">aload_0</a>}</tt></td><td>{@link #loadLocalReference(int)}, {@link #loadLocalReference(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>2B</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#aload_n">aload_1</a>}</tt></td><td>{@link #loadLocalReference(int)}, {@link #loadLocalReference(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>2C</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#aload_n">aload_2</a>}</tt></td><td>{@link #loadLocalReference(int)}, {@link #loadLocalReference(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>2D</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#aload_n">aload_3</a>}</tt></td><td>{@link #loadLocalReference(int)}, {@link #loadLocalReference(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>2E</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iaload">iaload</a>}</tt></td><td>{@link #loadIntArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>2F</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#laload">laload</a>}</tt></td><td>{@link #loadLongArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>30</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#faload">faload</a>}</tt></td><td>{@link #loadFloatArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>31</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#daload">daload</a>}</tt></td><td>{@link #loadDoubleArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>32</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#aaload">aaload</a>}</tt></td><td>{@link #loadReferenceArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>33</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc1.html#baload">baload</a>}</tt></td><td>{@link #loadBooleanArrayElement()}, {@link #loadByteArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>34</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc2.html#caload">caload</a>}</tt></td><td>{@link #loadCharArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>35</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc13.html#saload">saload</a>}</tt></td><td>{@link #loadShortArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>36</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#istore">istore</a>}</tt></td><td>{@link #storeLocalInt(int)}, {@link #storeLocalInt(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>37</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lstore">lstore</a>}</tt></td><td>{@link #storeLocalLong(int)}, {@link #storeLocalLong(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>38</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fstore">fstore</a>}</tt></td><td>{@link #storeLocalFloat(int)}, {@link #storeLocalFloat(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>39</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dstore">dstore</a>}</tt></td><td>{@link #storeLocalDouble(int)}, {@link #storeLocalDouble(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>3A</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#astore">astore</a>}</tt></td><td>{@link #storeLocalReference(int)}, {@link #storeLocalReference(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>3B</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#istore_n">istore_0</a>}</tt></td><td>{@link #storeLocalInt(int)}, {@link #storeLocalInt(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>3C</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#istore_n">istore_1</a>}</tt></td><td>{@link #storeLocalInt(int)}, {@link #storeLocalInt(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>3D</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#istore_n">istore_2</a>}</tt></td><td>{@link #storeLocalInt(int)}, {@link #storeLocalInt(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>3E</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#istore_n">istore_3</a>}</tt></td><td>{@link #storeLocalInt(int)}, {@link #storeLocalInt(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>3F</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lstore_n">lstore_0</a>}</tt></td><td>{@link #storeLocalLong(int)}, {@link #storeLocalLong(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>40</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lstore_n">lstore_1</a>}</tt></td><td>{@link #storeLocalLong(int)}, {@link #storeLocalLong(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>41</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lstore_n">lstore_2</a>}</tt></td><td>{@link #storeLocalLong(int)}, {@link #storeLocalLong(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>42</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lstore_n">lstore_3</a>}</tt></td><td>{@link #storeLocalLong(int)}, {@link #storeLocalLong(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>43</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fstore_n">fstore_0</a>}</tt></td><td>{@link #storeLocalFloat(int)}, {@link #storeLocalFloat(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>44</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fstore_n">fstore_1</a>}</tt></td><td>{@link #storeLocalFloat(int)}, {@link #storeLocalFloat(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>45</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fstore_n">fstore_2</a>}</tt></td><td>{@link #storeLocalFloat(int)}, {@link #storeLocalFloat(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>46</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fstore_n">fstore_3</a>}</tt></td><td>{@link #storeLocalFloat(int)}, {@link #storeLocalFloat(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>47</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dstore_n">dstore_0</a>}</tt></td><td>{@link #storeLocalDouble(int)}, {@link #storeLocalDouble(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>48</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dstore_n">dstore_1</a>}</tt></td><td>{@link #storeLocalDouble(int)}, {@link #storeLocalDouble(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>49</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dstore_n">dstore_2</a>}</tt></td><td>{@link #storeLocalDouble(int)}, {@link #storeLocalDouble(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>4A</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dstore_n">dstore_3</a>}</tt></td><td>{@link #storeLocalDouble(int)}, {@link #storeLocalDouble(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>4B</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#astore_n">astore_0</a>}</tt></td><td>{@link #storeLocalReference(int)}, {@link #storeLocalReference(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>4C</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#astore_n">astore_1</a>}</tt></td><td>{@link #storeLocalReference(int)}, {@link #storeLocalReference(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>4D</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#astore_n">astore_2</a>}</tt></td><td>{@link #storeLocalReference(int)}, {@link #storeLocalReference(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>4E</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#astore_n">astore_3</a>}</tt></td><td>{@link #storeLocalReference(int)}, {@link #storeLocalReference(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>4F</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iastore">iastore</a>}</tt></td><td>{@link #storeIntArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>50</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lastore">lastore</a>}</tt></td><td>{@link #storeLongArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>51</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fastore">fastore</a>}</tt></td><td>{@link #storeFloatArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>52</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dastore">dastore</a>}</tt></td><td>{@link #storeDoubleArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>53</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#aastore">aastore</a>}</tt></td><td>{@link #storeReferenceArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>54</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc1.html#bastore">bastore</a>}</tt></td><td>{@link #storeBooleanArrayElement()}, {@link #storeByteArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>55</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc2.html#castore">castore</a>}</tt></td><td>{@link #storeCharArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>56</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc13.html#sastore">sastore</a>}</tt></td><td>{@link #storeShortArrayElement()}</td></tr>
 * <tr align="left" valign="top"><td><tt>57</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc11.html#pop">pop</a>}</tt></td><td>{@link #pop()}, {@link #pop(int)}</td></tr>
 * <tr align="left" valign="top"><td><tt>58</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc11.html#pop2">pop2</a>}</tt></td><td>{@link #popDouble()}, {@link #pop(int)}</td></tr>
 * <tr align="left" valign="top"><td><tt>59</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dup">dup</a>}</tt></td><td>{@link #duplicate()}</td></tr>
 * <tr align="left" valign="top"><td><tt>5A</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dup_x1">dup_x1</a>}</tt></td><td>{@link #duplicateSkip()}</td></tr>
 * <tr align="left" valign="top"><td><tt>5B</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dup_x2">dup_x2</a>}</tt></td><td>{@link #duplicateSkipDouble()}</td></tr>
 * <tr align="left" valign="top"><td><tt>5C</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dup2">dup2</a>}</tt></td><td>{@link #duplicateDouble()}</td></tr>
 * <tr align="left" valign="top"><td><tt>5D</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dup2_x1">dup2_x1</a>}</tt></td><td>{@link #duplicateDoubleSkip()}</td></tr>
 * <tr align="left" valign="top"><td><tt>5E</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dup2_x2">dup2_x2</a>}</tt></td><td>{@link #duplicateDoubleSkipDouble()}</td></tr>
 * <tr align="left" valign="top"><td><tt>5F</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc13.html#swap">swap</a>}</tt></td><td>{@link #swap()}</td></tr>
 * <tr align="left" valign="top"><td><tt>60</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iadd">iadd</a>}</tt></td><td>{@link #addInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>61</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#ladd">ladd</a>}</tt></td><td>{@link #addLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>62</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fadd">fadd</a>}</tt></td><td>{@link #addFloat()}</td></tr>
 * <tr align="left" valign="top"><td><tt>63</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dadd">dadd</a>}</tt></td><td>{@link #addDouble()}</td></tr>
 * <tr align="left" valign="top"><td><tt>64</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#isub">isub</a>}</tt></td><td>{@link #subtractInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>65</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lsub">lsub</a>}</tt></td><td>{@link #subtractLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>66</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fsub">fsub</a>}</tt></td><td>{@link #subtractFloat()}</td></tr>
 * <tr align="left" valign="top"><td><tt>67</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dsub">dsub</a>}</tt></td><td>{@link #subtractDouble()}</td></tr>
 * <tr align="left" valign="top"><td><tt>68</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#imul">imul</a>}</tt></td><td>{@link #multiplyInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>69</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lmul">lmul</a>}</tt></td><td>{@link #multiplyLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>6A</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fmul">fmul</a>}</tt></td><td>{@link #multiplyFloat()}</td></tr>
 * <tr align="left" valign="top"><td><tt>6B</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dmul">dmul</a>}</tt></td><td>{@link #multiplyDouble()}</td></tr>
 * <tr align="left" valign="top"><td><tt>6C</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#idiv">idiv</a>}</tt></td><td>{@link #divideInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>6D</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#ldiv">ldiv</a>}</tt></td><td>{@link #divideLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>6E</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fdiv">fdiv</a>}</tt></td><td>{@link #divideFloat()}</td></tr>
 * <tr align="left" valign="top"><td><tt>6F</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#ddiv">ddiv</a>}</tt></td><td>{@link #divideDouble()}</td></tr>
 * <tr align="left" valign="top"><td><tt>70</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#irem">irem</a>}</tt></td><td>{@link #remainderInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>71</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lrem">lrem</a>}</tt></td><td>{@link #remainderLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>72</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#frem">frem</a>}</tt></td><td>{@link #remainderFloat()}</td></tr>
 * <tr align="left" valign="top"><td><tt>73</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#drem">drem</a>}</tt></td><td>{@link #remainderDouble()}</td></tr>
 * <tr align="left" valign="top"><td><tt>74</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#ineg">ineg</a>}</tt></td><td>{@link #negateInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>75</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lneg">lneg</a>}</tt></td><td>{@link #negateLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>76</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fneg">fneg</a>}</tt></td><td>{@link #negateFloat()}</td></tr>
 * <tr align="left" valign="top"><td><tt>77</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dneg">dneg</a>}</tt></td><td>{@link #negateDouble()}</td></tr>
 * <tr align="left" valign="top"><td><tt>78</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#ishl">ishl</a>}</tt></td><td>{@link #shiftLeftInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>79</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lshl">lshl</a>}</tt></td><td>{@link #shiftLeftLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>7A</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#ishr">ishr</a>}</tt></td><td>{@link #arithmeticShiftRightInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>7B</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lshr">lshr</a>}</tt></td><td>{@link #arithmeticShiftRightLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>7C</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iushr">iushr</a>}</tt></td><td>{@link #logicalShiftRightInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>7D</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lushr">lushr</a>}</tt></td><td>{@link #logicalShiftRightLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>7E</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iand">iand</a>}</tt></td><td>{@link #booleanAndInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>7F</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#land">land</a>}</tt></td><td>{@link #booleanAndLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>80</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#ior">ior</a>}</tt></td><td>{@link #booleanOrInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>81</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lor">lor</a>}</tt></td><td>{@link #booleanOrLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>82</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#ixor">ixor</a>}</tt></td><td>{@link #booleanXorInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>83</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lxor">lxor</a>}</tt></td><td>{@link #booleanXorLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>84</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#iinc">iinc</a>}</tt></td><td>{@link #incrementLocalInt(int, int)}, {@link #incrementLocalInt(String, int)}</td></tr>
 * <tr align="left" valign="top"><td><tt>85</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#i2l">i2l</a>}</tt></td><td>{@link #convertIntToLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>86</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#i2f">i2f</a>}</tt></td><td>{@link #convertIntToFloat()}</td></tr>
 * <tr align="left" valign="top"><td><tt>87</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#i2d">i2d</a>}</tt></td><td>{@link #convertIntToDouble()}</td></tr>
 * <tr align="left" valign="top"><td><tt>88</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#l2i">l2i</a>}</tt></td><td>{@link #convertLongToInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>89</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#l2f">l2f</a>}</tt></td><td>{@link #convertLongToFloat()}</td></tr>
 * <tr align="left" valign="top"><td><tt>8A</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#l2d">l2d</a>}</tt></td><td>{@link #convertLongToDouble()}</td></tr>
 * <tr align="left" valign="top"><td><tt>8B</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#f2i">f2i</a>}</tt></td><td>{@link #convertFloatToInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>8C</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#f2l">f2l</a>}</tt></td><td>{@link #convertFloatToLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>8D</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#f2d">f2d</a>}</tt></td><td>{@link #convertFloatToDouble()}</td></tr>
 * <tr align="left" valign="top"><td><tt>8E</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#d2i">d2i</a>}</tt></td><td>{@link #convertDoubleToInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>8F</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#d2l">d2l</a>}</tt></td><td>{@link #convertDoubleToLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>90</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#d2f">d2f</a>}</tt></td><td>{@link #convertDoubleToFloat()}</td></tr>
 * <tr align="left" valign="top"><td><tt>91</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#i2b">i2b</a>}</tt></td><td>{@link #convertIntToByte()}</td></tr>
 * <tr align="left" valign="top"><td><tt>92</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#i2c">i2c</a>}</tt></td><td>{@link #convertIntToChar()}</td></tr>
 * <tr align="left" valign="top"><td><tt>93</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#i2s">i2s</a>}</tt></td><td>{@link #convertIntToShort()}</td></tr>
 * <tr align="left" valign="top"><td><tt>94</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lcmp">lcmp</a>}</tt></td><td>{@link #compareLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>95</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fcmpop">fcmpl</a>}</tt></td><td>{@link #compareFloat(boolean)}</td></tr>
 * <tr align="left" valign="top"><td><tt>96</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#fcmpop">fcmpg</a>}</tt></td><td>{@link #compareFloat(boolean)}</td></tr>
 * <tr align="left" valign="top"><td><tt>97</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dcmpop">dcmpl</a>}</tt></td><td>{@link #compareDouble(boolean)}</td></tr>
 * <tr align="left" valign="top"><td><tt>98</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dcmpop">dcmpg</a>}</tt></td><td>{@link #compareDouble(boolean)}</td></tr>
 * <tr align="left" valign="top"><td><tt>99</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#ifcond">ifeq</a>}</tt></td><td>{@link #branchIfEqual(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>9A</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#ifcond">ifne</a>}</tt></td><td>{@link #branchIfNotEqual(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>9B</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#ifcond">iflt</a>}</tt></td><td>{@link #branchIfLess(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>9C</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#ifcond">ifge</a>}</tt></td><td>{@link #branchIfGreaterEqual(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>9D</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#ifcond">ifgt</a>}</tt></td><td>{@link #branchIfGreater(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>9E</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#ifcond">ifle</a>}</tt></td><td>{@link #branchIfLessEqual(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>9F</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#if_icmpcond">if_icmpeq</a>}</tt></td><td>{@link #compareIntBranchIfEqual(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>A0</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#if_icmpcond">if_icmpne</a>}</tt></td><td>{@link #compareIntBranchIfNotEqual(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>A1</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#if_icmpcond">if_icmplt</a>}</tt></td><td>{@link #compareIntBranchIfLess(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>A2</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#if_icmpcond">if_icmpge</a>}</tt></td><td>{@link #compareIntBranchIfGreaterEqual(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>A3</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#if_icmpcond">if_icmpgt</a>}</tt></td><td>{@link #compareIntBranchIfGreater(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>A4</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#if_icmpcond">if_icmple</a>}</tt></td><td>{@link #compareIntBranchIfLessEqual(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>A5</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#if_acmpcond">if_acmpeq</a>}</tt></td><td>{@link #compareReferenceBranchIfEqual(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>A6</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#if_acmpcond">if_acmpne</a>}</tt></td><td>{@link #compareReferenceBranchIfNotEqual(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>A7</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc5.html#goto">goto</a>}</tt></td><td>{@link #branch(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>A8</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc7.html#jsr">jsr</a>}</tt></td><td>{@link #jumpSubroutine(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>A9</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc12.html#ret">ret</a>}</tt></td><td>{@link #returnFromSubroutine(int)}, {@link #returnFromSubroutine(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>AA</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc14.html#tableswitch">tableswitch</a>}</tt></td><td>{@link #tableswitch(BranchTable)}</td></tr>
 * <tr align="left" valign="top"><td><tt>AB</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lookupswitch">lookupswitch</a>}</tt></td><td>{@link #lookupswitch(BranchTable)}</td></tr>
 * <tr align="left" valign="top"><td><tt>AC</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#ireturn">ireturn</a>}</tt></td><td>{@link #returnInt()}</td></tr>
 * <tr align="left" valign="top"><td><tt>AD</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc8.html#lreturn">lreturn</a>}</tt></td><td>{@link #returnLong()}</td></tr>
 * <tr align="left" valign="top"><td><tt>AE</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc4.html#freturn">freturn</a>}</tt></td><td>{@link #returnFloat()}</td></tr>
 * <tr align="left" valign="top"><td><tt>AF</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc3.html#dreturn">dreturn</a>}</tt></td><td>{@link #returnDouble()}</td></tr>
 * <tr align="left" valign="top"><td><tt>B0</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#areturn">areturn</a>}</tt></td><td>{@link #returnReference()}</td></tr>
 * <tr align="left" valign="top"><td><tt>B1</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc12.html#return">return</a>}</tt></td><td>{@link #returnVoid()}</td></tr>
 * <tr align="left" valign="top"><td><tt>B2</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc5.html#getstatic">getstatic</a>}</tt></td><td>{@link #getStatic(Type, Type, String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>B3</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc11.html#putstatic">putstatic</a>}</tt></td><td>{@link #putStatic(Type, Type, String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>B4</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc5.html#getfield">getfield</a>}</tt></td><td>{@link #getField(Type, Type, String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>B5</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc11.html#putfield">putfield</a>}</tt></td><td>{@link #putField(Type, Type, String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>B6</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#invokevirtual">invokevirtual</a>}</tt></td><td>{@link #invokeVirtual(Type, Type, String, Type...)}</td></tr>
 * <tr align="left" valign="top"><td><tt>B7</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#invokespecial">invokespecial</a>}</tt></td><td>{@link #invokeSpecial(Type, Type, String, Type...)}</td></tr>
 * <tr align="left" valign="top"><td><tt>B8</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#invokestatic">invokestatic</a>}</tt></td><td>{@link #invokeStatic(Type, Type, String, Type...)}</td></tr>
 * <tr align="left" valign="top"><td><tt>B9</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#invokeinterface">invokeinterface</a>}</tt></td><td>{@link #invokeInterface(Type, Type, String, Type...)}</td></tr>
 * <tr align="left" valign="top"><td><tt>BA</tt></td><td><tt><i>xxxunusedxxx</i></tt></td><td></td></tr>
 * <tr align="left" valign="top"><td><tt>BB</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc10.html#new">new</a>}</tt></td><td>{@link #newObject(Type)}</td></tr>
 * <tr align="left" valign="top"><td><tt>BC</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc10.html#newarray">newarray</a>}</tt></td><td>{@link #newArray(Type)}</td></tr>
 * <tr align="left" valign="top"><td><tt>BD</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#anewarray">anewarray</a>}</tt></td><td>{@link #newArray(Type)}</td></tr>
 * <tr align="left" valign="top"><td><tt>BE</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#arraylength">arraylength</a>}</tt></td><td>{@link #arraylength()}</td></tr>
 * <tr align="left" valign="top"><td><tt>BF</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html#athrow">athrow</a>}</tt></td><td>{@link #throwException()}</td></tr>
 * <tr align="left" valign="top"><td><tt>C0</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc2.html#checkcast">checkcast</a>}</tt></td><td>{@link #cast(Type)}</td></tr>
 * <tr align="left" valign="top"><td><tt>C1</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#instanceof">instanceof</a>}</tt></td><td>{@link #checkInstance(Type)}</td></tr>
 * <tr align="left" valign="top"><td><tt>C2</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc9.html#monitorenter">monitorenter</a>}</tt></td><td>{@link #monitorEnter()}</td></tr>
 * <tr align="left" valign="top"><td><tt>C3</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc9.html#monitorexit">monitorexit</a>}</tt></td><td>{@link #monitorExit()}</td></tr>
 * <tr align="left" valign="top"><td><tt>C4</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc15.html#wide">wide</a>}</tt></td><td>{@link #loadLocalInt(int)}, {@link #loadLocalInt(String)}, {@link #loadLocalLong(int)}, {@link #loadLocalLong(String)}, {@link #loadLocalFloat(int)}, {@link #loadLocalFloat(String)}, {@link #loadLocalDouble(int)}, {@link #loadLocalDouble(String)}, {@link #loadLocalReference(int)}, {@link #loadLocalReference(String)}, {@link #storeLocalInt(int)}, {@link #storeLocalInt(String)}, {@link #storeLocalLong(int)}, {@link #storeLocalLong(String)}, {@link #storeLocalFloat(int)}, {@link #storeLocalFloat(String)}, {@link #storeLocalDouble(int)}, {@link #storeLocalDouble(String)}, {@link #storeLocalReference(int)}, {@link #storeLocalReference(String)}, {@link #incrementLocalInt(int, int)}, {@link #incrementLocalInt(String, int)}, {@link #returnFromSubroutine(int)}, {@link #returnFromSubroutine(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>C5</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc9.html#multianewarray">multianewarray</a>}</tt></td><td>{@link #newMultiReferenceArray(Type, int)}</td></tr>
 * <tr align="left" valign="top"><td><tt>C6</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#ifnull">ifnull</a>}</tt></td><td>{@link #branchIfNull(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>C7</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc6.html#ifnonnull">ifnonnull</a>}</tt></td><td>{@link #branchIfNonNull(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>C8</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc5.html#goto_w">goto_w</a>}</tt></td><td>{@link #branchFar(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>C9</tt></td><td><tt>{@link <a href="http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc7.html#jsr_w">jsr_w</a>}</tt></td><td>{@link #jumpFarSubroutine(String)}</td></tr>
 * <tr align="left" valign="top"><td><tt>CA</tt></td><td><tt><i>breakpoint</i></tt></td><td></td></tr>
 * <tr align="left" valign="top"><td><tt>FE</tt></td><td><tt><i>impdep1</i></tt></td><td></td></tr>
 * <tr align="left" valign="top"><td><tt>FF</tt></td><td><tt><i>impdep2</i></tt></td><td></td></tr>
 * </table></blockquote>
 * 
 * @author Uwe Finke
 */
public class CodeAttribute implements Generatable {

  static private Text text = new Text(CodeAttribute.class);
  
  private ConstantPool constantPool;
  private int nameIndex;
  private int currentStack;
  private int maxStackSize;
  private int maxLocals;
  private Map<String, Integer> localVariableMap;
  private ByteArrayOutputStream buffer;
  private Map<String, Label> labelMap;
  private List<Jump> jumpList;
  private List<ExceptionHandlerDefinition> exceptionHandlerList;
  private int opCodeOffset;
  
  CodeAttribute(GenClass genClass, boolean isStatic, Type[] args) {
  
    constantPool = genClass.getConstantPool();
    nameIndex = constantPool.addName("Code");
    
    localVariableMap = new HashMap<String, Integer>();
    if (! isStatic) {
      getLocalVariable("this");
      maxLocals++;
    }
    for (int i = 0; i < args.length; i++) {
      String parmName = args[i].getParameterName();
      if (parmName == null) {
        parmName = "parm_" + (i + 1);
      }
      getLocalVariable(parmName);
      maxLocals += args[i].getSize();
    }
    
    labelMap = new HashMap<String, Label>();
    jumpList = new ArrayList<Jump>();
    exceptionHandlerList = new ArrayList<ExceptionHandlerDefinition>();
    buffer = new ByteArrayOutputStream();
  }
  
  private int getLocalVariable(String name) {
    
    Integer index = localVariableMap.get(name);
    if (index == null) {
      index = maxLocals;
      localVariableMap.put(name, index);
    }
    return index;
  }
  
  private void checkMaxLocals(int index) {
    
    maxLocals = Math.max(maxLocals, index + 1);
  }
  
  private void incrementStack(int increment) {
    
    currentStack += increment;
    maxStackSize = Math.max(maxStackSize, currentStack);
  }
  
  private void writeOpCode(int opCode) {

    opCodeOffset = buffer.size();
    buffer.write(opCode);
  }
  
  private void write1(int value) {
    
    buffer.write(value);
  }
  
  private void write2(int value) {
    
    buffer.write(value >>> 8);
    buffer.write(value);
  }
  
  private void write4(int value) {
    
    buffer.write(value >>> 24);
    buffer.write(value >>> 16);
    buffer.write(value >>> 8);
    buffer.write(value);
  }
  
  private Label getLabel(String labelName) {
    
    Label label = labelMap.get(labelName);
    if (label == null) {
      label = new Label(labelName);
      labelMap.put(labelName, label);
    }
    return label;
  }
  
  private void checkLabelStack(Label label) {
    
    currentStack = label.stackSize(currentStack);
  }
  
  private void createJump(int size, String labelName) {
        
    Label label = getLabel(labelName);
    
    jumpList.add(new Jump(size, opCodeOffset, buffer.size(), label));

    for (int i = 0; i < size; i++) {
      write1(0);
    }
    
    checkLabelStack(label);
  }
  
  /**
   * Defines a label.
   * This label may be used as branch target in jump instructions.
   * @param labelName
   */
  public void defineLabel(String labelName) {
    
    Label label = getLabel(labelName);
    label.define(buffer.size());
    checkLabelStack(label);
  }
  
  /**
   * Defines a <tt>try-/catch</tt>-block.
   * The <tt>try</tt>-block must be enclosed with a start and an end label.
   * The entry point of the <tt>catch</tt>-block (handler) must also be labelled.
   * @param startLabelName 
   * @param endLabelName
   * @param exceptionType
   * @param handlerLabelName
   */
  public void defineExceptionHandler(String startLabelName, String endLabelName, Type exceptionType, String handlerLabelName) {
    
    exceptionHandlerList.add(new ExceptionHandlerDefinition(getLabel(startLabelName), getLabel(endLabelName), exceptionType, getLabel(handlerLabelName)));
  }
  
  /**
   * Returns the index of a named local variable.
   * @param variableName
   * @param type
   * @return index
   */
  public int getLocalVariable(String variableName, Type type) {
    
    int index = getLocalVariable(variableName);
    checkMaxLocals(index + type.getSize() - 1);
    return index;
  }

  /**
   * Opcode <tt>nop</tt>.
   */
  public void nop() {
    
    writeOpCode(0x00); // nop
  }
  
  /**
   * Opcode <tt>aconst_null</tt>.
   */
  public void loadNull() {
  
    writeOpCode(0x01); // aconst_null
    incrementStack(1);
  }
  
  /**
   * Opcode to load an integer constant 
   * (<tt>iconst_&lt;n&gt;</tt>, <tt>bipush</tt>, <tt>sipush</tt>, <tt>ldc</tt> or <tt>ldc_w</tt>).
   * @param value
   */
  public void loadConstant(int value) {
    
    switch (value) {
      case -1:
        writeOpCode(0x02); // iconst_m1
        break;
      case 0:
        writeOpCode(0x03); // iconst_0
        break;
      case 1:
        writeOpCode(0x04); // iconst_1
        break;
      case 2:
        writeOpCode(0x05); // iconst_2
        break;
      case 3:
        writeOpCode(0x06); // iconst_3
        break;
      case 4:
        writeOpCode(0x07); // iconst_4
        break;
      case 5:
        writeOpCode(0x08); // iconst_5
        break;
      default:
        if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
          writeOpCode(0x10); // bipush
          write1(value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
          writeOpCode(0x11); // sipush
          write2(value);
        } else {
          int index = constantPool.addInteger(value);
          if (index <= Byte.MAX_VALUE) {
            writeOpCode(0x12); // ldc
            write1(index);
          } else {
            writeOpCode(0x13); // ldc_w
            write2(index);
          }
        }
    }
    
    incrementStack(1);
  }
  
  /**
   * Opcode to load a long constant
   * (<tt>lconst_&lt;n&gt;</tt> or <tt>ldc2_w</tt>).
   * @param value
   */
  public void loadConstant(long value) {

    if (value == 0L) {
      writeOpCode(0x09); // lconst_0
    } else if (value == 1L) {
      writeOpCode(0x0A); // lconst_1
    } else {
      writeOpCode(0x14); // ldc2_w
      write2(constantPool.addLong(value));
    }
    
    incrementStack(2);
  }
  
  /**
   * Opcode to load a float constant
   * (<tt>fconst_&lt;n&gt;</tt>, <tt>ldc</tt> or <tt>ldc_w</tt>).
   * @param value
   */
  public void loadConstant(float value) {

    if (value == 0.0F) {
      writeOpCode(0x0B); // fconst_0
    } else if (value == 1.0F) {
      writeOpCode(0x0C); // fconst_1
    } else if (value == 2.0F) {
      writeOpCode(0x0D); // fconst_2
    } else {
      int index = constantPool.addFloat(value);
      if (index <= Byte.MAX_VALUE) {
        writeOpCode(0x12); // ldc
        write1(index);
      } else {
        writeOpCode(0x13); // ldc_w
        write2(index);
      }
    }
    
    incrementStack(1);
  }
  
  /**
   * Opcode to load a double constant
   * (<tt>dconst_&lt;n&gt;</tt> or <tt>ldc2_w</tt>).
   * @param value
   */
  public void loadConstant(double value) {

    if (value == 0.0D) {
      writeOpCode(0x0E); // dconst_0
    } else if (value == 1.0D) {
      writeOpCode(0x0F); // dconst_1
    } else {
      writeOpCode(0x14); // ldc2_w
      write2(constantPool.addDouble(value));
    }
    
    incrementStack(2);
  }
  
  /**
   * Opcode to load a string constant
   * (<tt>ldc</tt> or <tt>lcd_w</tt>).
   * @param value
   */
  public void loadConstant(String value) {
    
    int index = constantPool.addString(value);
    if (index <= Byte.MAX_VALUE) {
      writeOpCode(0x12); // ldc
      write1(index);
    } else {
      writeOpCode(0x13); // ldc_w
      write2(index);
    }
    
    incrementStack(1);
  }
  
  /**
   * Opcode to load a class constant
   * (<tt>ldc</tt> or <tt>lcd_w</tt>).
   * @param value
   */
  public void loadConstant(Type value) {
    
    int index = constantPool.addClass(value);
    if (index <= Byte.MAX_VALUE) {
      writeOpCode(0x12); // ldc
      write1(index);
    } else {
      writeOpCode(0x13); // ldc_w
      write2(index);
    }
    
    incrementStack(1);
  }
  
  /**
   * Opcode to load a local int variable by name
   * (<tt>iload_&lt;n&gt;</tt> or <tt>iload</tt>).
   * @param variableName
   */
  public void loadLocalInt(String variableName) {
    
    loadLocalInt(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to load a local int variable
   * (<tt>iload_&lt;n&gt;</tt> or <tt>iload</tt>).
   * @param index
   */
  public void loadLocalInt(int index) {
    
    checkMaxLocals(index);
    
    switch (index) {
      case 0:
        writeOpCode(0x1A); // iload_0
        break;
      case 1:
        writeOpCode(0x1B); // iload_1
        break;
      case 2:
        writeOpCode(0x1C); // iload_2
        break;
      case 3:
        writeOpCode(0x1D); // iload_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x15); // iload
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x15); // iload
          write2(index);
        }
    }
    
    incrementStack(1);
  }
  
  /**
   * Opcode to load a local long variable by name
   * (<tt>lload_&lt;n&gt;</tt> or <tt>lload</tt>).
   * @param variableName
   */
  public void loadLocalLong(String variableName) {
    
    loadLocalLong(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to load a local long variable
   * (<tt>lload_&lt;n&gt;</tt> or <tt>lload</tt>).
   * @param index
   */
  public void loadLocalLong(int index) {
   
    checkMaxLocals(index + 1);
    
    switch (index) {
      case 0:
        writeOpCode(0x1E); // lload_0
        break;
      case 1:
        writeOpCode(0x1F); // lload_1
        break;
      case 2:
        writeOpCode(0x20); // lload_2
        break;
      case 3:
        writeOpCode(0x21); // lload_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x16); // lload
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x16); // lload
          write2(index);
        }
    }
    
    incrementStack(2);
  }
  
  /**
   * Opcode to load a local float variable by name
   * (<tt>fload_&lt;n&gt;</tt> or <tt>fload</tt>).
   * @param variableName
   */
  public void loadLocalFloat(String variableName) {
    
    loadLocalFloat(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to load a local float variable
   * (<tt>fload_&lt;n&gt;</tt> or <tt>fload</tt>).
   * @param index
   */
  public void loadLocalFloat(int index) {
    
    checkMaxLocals(index);
    
    switch (index) {
      case 0:
        writeOpCode(0x22); // fload_0
        break;
      case 1:
        writeOpCode(0x23); // fload_1
        break;
      case 2:
        writeOpCode(0x24); // fload_2
        break;
      case 3:
        writeOpCode(0x25); // fload_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x17); // fload
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x17); // fload
          write2(index);
        }
    }
    
    incrementStack(1);
  }
  
  /**
   * Opcode to load a local double variable by name
   * (<tt>dload_&lt;n&gt;</tt> or <tt>dload</tt>).
   * @param variableName
   */
  public void loadLocalDouble(String variableName) {
    
    loadLocalDouble(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to load a local double variable
   * (<tt>dload_&lt;n&gt;</tt> or <tt>dload</tt>).
   * @param index
   */
  public void loadLocalDouble(int index) {
   
    checkMaxLocals(index + 1);
    
    switch (index) {
      case 0:
        writeOpCode(0x26); // dload_0
        break;
      case 1:
        writeOpCode(0x27); // dload_1
        break;
      case 2:
        writeOpCode(0x28); // dload_2
        break;
      case 3:
        writeOpCode(0x29); // dload_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x18); // dload
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x18); // dload
          write2(index);
        }
    }
    
    incrementStack(2);
  }
  
  /**
   * Opcode to load a local reference variable by name
   * (<tt>aload_&lt;n&gt;</tt> or <tt>aload</tt>).
   * @param variableName
   */
  public void loadLocalReference(String variableName) {
    
    loadLocalReference(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to load a local reference variable
   * (<tt>aload_&lt;n&gt;</tt> or <tt>aload</tt>).
   * @param index
   */
  public void loadLocalReference(int index) {
    
    checkMaxLocals(index);
    
    switch (index) {
      case 0:
        writeOpCode(0x2A); // aload_0
        break;
      case 1:
        writeOpCode(0x2B); // aload_1
        break;
      case 2:
        writeOpCode(0x2C); // aload_2
        break;
      case 3:
        writeOpCode(0x2D); // aload_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x19); // aload
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x19); // aload
          write2(index);
        }
    }
    
    incrementStack(1);
  }
  
  /**
   * Opcode <tt>iaload</tt>.
   */
  public void loadIntArrayElement() {
    
    writeOpCode(0x2E); // iaload
    currentStack--;
  }
  
  /**
   * Opcode <tt>laload</tt>.
   */
  public void loadLongArrayElement() {
    
    writeOpCode(0x2F); // laload
  }
  
  /**
   * Opcode <tt>faload</tt>.
   */
  public void loadFloatArrayElement() {
    
    writeOpCode(0x30); // faload
    currentStack--;
  }
  
  /**
   * Opcode <tt>daload</tt>.
   */
  public void loadDoubleArrayElement() {
    
    writeOpCode(0x31); // daload
  }
  
  /**
   * Opcode <tt>aaload</tt>.
   */
  public void loadReferenceArrayElement() {
    
    writeOpCode(0x32); // aaload
    currentStack--;
  }
  
  /**
   * Opcode <tt>baload</tt> (boolean).
   */
  public void loadBooleanArrayElement() {
    
    writeOpCode(0x33); // baload
    currentStack--;
  }
  
  /**
   * Opcode <tt>baload</tt> (byte).
   */
  public void loadByteArrayElement() {
    
    writeOpCode(0x33); // baload
    currentStack--;
  }
  
  /**
   * Opcode <tt>caload</tt>.
   */
  public void loadCharArrayElement() {
    
    writeOpCode(0x34); // caload
    currentStack--;
  }
  
  /**
   * Opcode <tt>saload</tt>.
   */
  public void loadShortArrayElement() {
    
    writeOpCode(0x35); // saload
    currentStack--;
  }
  
  /**
   * Opcode to store a local int variable by name
   * (<tt>istore_&lt;n&gt;</tt> or <tt>istore</tt>).
   * @param variableName
   */
  public void storeLocalInt(String variableName) {
    
    storeLocalInt(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to store a local int variable
   * (<tt>istore_&lt;n&gt;</tt> or <tt>istore</tt>).
   * @param index
   */
  public void storeLocalInt(int index) {
   
    checkMaxLocals(index);
    
    switch (index) {
      case 0:
        writeOpCode(0x3B); // istore_0
        break;
      case 1:
        writeOpCode(0x3C); // istore_1
        break;
      case 2:
        writeOpCode(0x3D); // istore_2
        break;
      case 3:
        writeOpCode(0x3E); // istore_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x36); // istore
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x36); // istore
          write2(index);
        }
    }
    
    currentStack--;
  }
  
  /**
   * Opcode to store a local long variable by name
   * (<tt>lstore_&lt;n&gt;</tt> or <tt>lstore</tt>).
   * @param variableName
   */
  public void storeLocalLong(String variableName) {
    
    storeLocalLong(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to store a local long variable
   * (<tt>lstore_&lt;n&gt;</tt> or <tt>lstore</tt>).
   * @param index
   */
  public void storeLocalLong(int index) {
    
    checkMaxLocals(index + 1);
    
    switch (index) {
      case 0:
        writeOpCode(0x3F); // lstore_0
        break;
      case 1:
        writeOpCode(0x40); // lstore_1
        break;
      case 2:
        writeOpCode(0x41); // lstore_2
        break;
      case 3:
        writeOpCode(0x42); // lstore_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x37); // lstore
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x37); // lstore
          write2(index);
        }
    }
    
    currentStack -= 2;
  }
  
  /**
   * Opcode to store a local float variable by name
   * (<tt>fstore_&lt;n&gt;</tt> or <tt>fstore</tt>).
   * @param variableName
   */
  public void storeLocalFloat(String variableName) {
    
    storeLocalFloat(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to store a local float variable
   * (<tt>fstore_&lt;n&gt;</tt> or <tt>fstore</tt>).
   * @param index
   */
  public void storeLocalFloat(int index) {
    
    checkMaxLocals(index);
    
    switch (index) {
      case 0:
        writeOpCode(0x43); // fstore_0
        break;
      case 1:
        writeOpCode(0x44); // fstore_1
        break;
      case 2:
        writeOpCode(0x45); // fstore_2
        break;
      case 3:
        writeOpCode(0x46); // fstore_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x38); // fstore
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x38); // fstore
          write2(index);
        }
    }
    
    currentStack--;
  }
  
  /**
   * Opcode to store a local double variable by name
   * (<tt>dstore_&lt;n&gt;</tt> or <tt>dstore</tt>).
   * @param variableName
   */
  public void storeLocalDouble(String variableName) {
    
    storeLocalDouble(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to store a local double variable
   * (<tt>dstore_&lt;n&gt;</tt> or <tt>dstore</tt>).
   * @param index
   */
  public void storeLocalDouble(int index) {
    
    checkMaxLocals(index + 1);
    
    switch (index) {
      case 0:
        writeOpCode(0x47); // dstore_0
        break;
      case 1:
        writeOpCode(0x48); // dstore_1
        break;
      case 2:
        writeOpCode(0x49); // dstore_2
        break;
      case 3:
        writeOpCode(0x4A); // dstore_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x39); // dstore
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x39); // dstore
          write2(index);
        }
    }
    
    currentStack -= 2;
  }
  
  /**
   * Opcode to store a local reference variable by name
   * (<tt>astore_&lt;n&gt;</tt> or <tt>astore</tt>).
   * @param variableName
   */
  public void storeLocalReference(String variableName) {
    
    storeLocalReference(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to store a local reference variable
   * (<tt>astore_&lt;n&gt;</tt> or <tt>astore</tt>).
   * @param index
   */
  public void storeLocalReference(int index) {
    
    checkMaxLocals(index);
    
    switch (index) {
      case 0:
        writeOpCode(0x4B); // astore_0
        break;
      case 1:
        writeOpCode(0x4C); // astore_1
        break;
      case 2:
        writeOpCode(0x4D); // astore_2
        break;
      case 3:
        writeOpCode(0x4E); // astore_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x3A); // astore
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x3A); // astore
          write2(index);
        }
    }
    
    currentStack--;
  }
  
  /**
   * Opcode <tt>iastore</tt>.
   */
  public void storeIntArrayElement() {
    
    writeOpCode(0x4F); // iastore
    currentStack -= 3;
  }
  
  /**
   * Opcode <tt>lastore</tt>.
   */
  public void storeLongArrayElement() {
    
    writeOpCode(0x50); // lastore
    currentStack -= 4;
  }
  
  /**
   * Opcode <tt>fastore</tt>.
   */
  public void storeFloatArrayElement() {
    
    writeOpCode(0x51); // fastore
    currentStack -= 3;
  }
  
  /**
   * Opcode <tt>dastore</tt>.
   */
  public void storeDoubleArrayElement() {
    
    writeOpCode(0x52); // dastore
    currentStack -= 4;
  }
  
  /**
   * Opcode <tt>aastore</tt>.
   */
  public void storeReferenceArrayElement() {
    
    writeOpCode(0x53); // aastore
    currentStack -= 3;
  }
  
  /**
   * Opcode <tt>bastore</tt> (boolean).
   */
  public void storeBooleanArrayElement() {
    
    writeOpCode(0x54); // bastore
    currentStack -= 3;
  }
  
  /**
   * Opcode <tt>bastore</tt> (byte).
   */
  public void storeByteArrayElement() {
    
    writeOpCode(0x54); // bastore
    currentStack -= 3;
  }
  
  /**
   * Opcode <tt>castore</tt>.
   */
  public void storeCharArrayElement() {
    
    writeOpCode(0x55); // castore
    currentStack -= 3;
  }
  
  /**
   * Opcode <tt>sastore</tt>.
   */
  public void storeShortArrayElement() {
    
    writeOpCode(0x56); // sastore
    currentStack -= 3;
  }
  
  /**
   * Opcode <tt>pop</tt>.
   */
  public void pop() {
    
    writeOpCode(0x57); // pop
    currentStack--;
  }
  
  /**
   * Opcode <tt>pop2</tt>.
   */
  public void popDouble() {
    
    writeOpCode(0x58); // pop2
    currentStack -= 2;
  }
  
  /**
   * Opcode to pop <tt>popSize</tt> bytes
   * (<tt>pop</tt> or <tt>pop2</tt> repeatedly).
   */
  public void pop(int popSize) {

    int remainingPops = popSize;
    while (remainingPops >= 2) {
      popDouble();
      remainingPops -= 2;
    }
    if (remainingPops == 1) {
      pop();
    }
  }
  
  /**
   * Opcode <tt>dup</tt>.
   */
  public void duplicate() {
    
    writeOpCode(0x59); // dup
    incrementStack(1);
  }
  
  /**
   * Opcode <tt>dup_x1</tt>.
   */
  public void duplicateSkip() {
    
    writeOpCode(0x5A); // dup_x1
    incrementStack(1);
  }
  
  /**
   * Opcode <tt>dup_x2</tt>.
   */
  public void duplicateSkipDouble() {
    
    writeOpCode(0x5B); // dup_x2
    incrementStack(1);
  }
  
  /**
   * Opcode <tt>dup2</tt>.
   */
  public void duplicateDouble() {
    
    writeOpCode(0x5C); // dup2
    incrementStack(2);
  }
  
  /**
   * Opcode <tt>dup2_x1</tt>.
   */
  public void duplicateDoubleSkip() {
    
    writeOpCode(0x5D); // dup2_x1
    incrementStack(2);
  }
  
  /**
   * Opcode <tt>dup2_x2</tt>.
   */
  public void duplicateDoubleSkipDouble() {
    
    writeOpCode(0x5E); // dup2_x2
    incrementStack(2);
  }
  
  /**
   * Opcode <tt>swap</tt>.
   */
  public void swap() {
    
    writeOpCode(0x5F); // swap
  }
  
  /**
   * Opcode <tt>iadd</tt>.
   */
  public void addInt() {
    
    writeOpCode(0x60); // iadd
    currentStack--;
  }
  
  /**
   * Opcode <tt>ladd</tt>.
   */
  public void addLong() {
    
    writeOpCode(0x61); // ladd
    currentStack -= 2;
  }
  
  /**
   * Opcode <tt>fadd</tt>.
   */
  public void addFloat() {
    
    writeOpCode(0x62); // fadd
    currentStack--;
  }
  
  /**
   * Opcode <tt>dadd</tt>.
   */
  public void addDouble() {
    
    writeOpCode(0x63); // dadd
    currentStack -= 2;
  }
  
  /**
   * Opcode <tt>isub</tt>.
   */
  public void subtractInt() {
    
    writeOpCode(0x64); // isub
    currentStack--;
  }
  
  /**
   * Opcode <tt>lsub</tt>.
   */
  public void subtractLong() {
    
    writeOpCode(0x65); // lsub
    currentStack -= 2;
  }
  
  /**
   * Opcode <tt>fsub</tt>.
   */
  public void subtractFloat() {
    
    writeOpCode(0x66); // fsub
    currentStack--;
  }
  
  /**
   * Opcode <tt>dsub</tt>.
   */
  public void subtractDouble() {
    
    writeOpCode(0x67); // dsub
    currentStack -= 2;
  }
  
  /**
   * Opcode <tt>imul</tt>.
   */
  public void multiplyInt() {
    
    writeOpCode(0x68); // imul
    currentStack--;
  }
  
  /**
   * Opcode <tt>lmul</tt>.
   */
  public void multiplyLong() {
    
    writeOpCode(0x69); // lmul
    currentStack -= 2;
  }
  
  /**
   * Opcode <tt>fmul</tt>.
   */
  public void multiplyFloat() {
    
    writeOpCode(0x6A); // fmul
    currentStack--;
  }
  
  /**
   * Opcode <tt>dmul</tt>.
   */
  public void multiplyDouble() {
    
    writeOpCode(0x6B); // dmul
    currentStack -= 2;
  }
  
  /**
   * Opcode <tt>idiv</tt>.
   */
  public void divideInt() {
    
    writeOpCode(0x6C); // idiv
    currentStack--;
  }
  
  /**
   * Opcode <tt>ldiv</tt>.
   */
  public void divideLong() {
    
    writeOpCode(0x6D); // ldiv
    currentStack -= 2;
  }
  
  /**
   * Opcode <tt>fdiv</tt>.
   */
  public void divideFloat() {
    
    writeOpCode(0x6E); // fdiv
    currentStack--;
  }
  
  /**
   * Opcode <tt>ddiv</tt>.
   */
  public void divideDouble() {
    
    writeOpCode(0x6F); // ddiv
    currentStack -= 2;
  }
  
  /**
   * Opcode <tt>irem</tt>.
   */
  public void remainderInt() {
    
    writeOpCode(0x70); // irem
    currentStack--;
  }
  
  /**
   * Opcode <tt>lrem</tt>.
   */
  public void remainderLong() {
    
    writeOpCode(0x71); // lrem
    currentStack -= 2;
  }
  
  /**
   * Opcode <tt>frem</tt>.
   */
  public void remainderFloat() {
    
    writeOpCode(0x72); // frem
    currentStack--;
  }
  
  /**
   * Opcode <tt>drem</tt>.
   */
  public void remainderDouble() {
    
    writeOpCode(0x73); // drem
    currentStack -= 2;
  }
  
  /**
   * Opcode <tt>ineg</tt>.
   */
  public void negateInt() {
    
    writeOpCode(0x74); // ineg
  }
  
  /**
   * Opcode <tt>lneg</tt>.
   */
  public void negateLong() {
    
    writeOpCode(0x75); // lneg
  }
  
  /**
   * Opcode <tt>fneg</tt>.
   */
  public void negateFloat() {
    
    writeOpCode(0x76); // fneg
  }
  
  /**
   * Opcode <tt>dneg</tt>.
   */
  public void negateDouble() {
    
    writeOpCode(0x77); // dneg
  }
  
  /**
   * Opcode <tt>ishl</tt>.
   */
  public void shiftLeftInt() {
    
    writeOpCode(0x78); // ishl
    currentStack--;
  }
  
  /**
   * Opcode <tt>lshl</tt>.
   */
  public void shiftLeftLong() {
    
    writeOpCode(0x79); // lshl
    currentStack--;
  }
  
  /**
   * Opcode <tt>ishr</tt>.
   */
  public void arithmeticShiftRightInt() {
    
    writeOpCode(0x7A); // ishr
    currentStack--;
  }
  
  /**
   * Opcode <tt>lshr</tt>.
   */
  public void arithmeticShiftRightLong() {
    
    writeOpCode(0x7B); // lshr
    currentStack--;
  }
  
  /**
   * Opcode <tt>iushr</tt>.
   */
  public void logicalShiftRightInt() {
    
    writeOpCode(0x7C); // iushr
    currentStack--;
  }
  
  /**
   * Opcode <tt>lushr</tt>.
   */
  public void logicalShiftRightLong() {
    
    writeOpCode(0x7D); // lushr
    currentStack--;
  }
  
  /**
   * Opcode <tt>iand</tt>.
   */
  public void booleanAndInt() {
    
    writeOpCode(0x7E); // iand
    currentStack--;
  }
  
  /**
   * Opcode <tt>land</tt>.
   */
  public void booleanAndLong() {
    
    writeOpCode(0x7F); // land
    currentStack -= 2;
  }
  
  /**
   * Opcode <tt>ior</tt>.
   */
  public void booleanOrInt() {
    
    writeOpCode(0x80); // ior
    currentStack--;
  }
  
  /**
   * Opcode <tt>lor</tt>.
   */
  public void booleanOrLong() {
    
    writeOpCode(0x81); // lor
    currentStack -= 2;
  }
  
  /**
   * Opcode <tt>ixor</tt>.
   */
  public void booleanXorInt() {
    
    writeOpCode(0x82); // ixor
    currentStack--;
  }
  
  /**
   * Opcode <tt>lxor</tt>.
   */
  public void booleanXorLong() {
    
    writeOpCode(0x83); // lxor
    currentStack -= 2;
  }
  
  /**
   * Opcode to increment a local int variable by name
   * (<tt>iinc</tt> or a combination of load, add and store</tt>).
   * @param variableName
   * @param increment
   */
  public void incrementLocalInt(String variableName, int increment) {
    
    incrementLocalInt(getLocalVariable(variableName), increment);
  }
  
  /**
   * Opcode to increment a local int variable
   * (<tt>iinc</tt> or a combination of load, add and store</tt>).
   * @param index
   * @param increment
   */
  public void incrementLocalInt(int index, int increment) {
    
    checkMaxLocals(index);
    
    if (index <= Byte.MAX_VALUE && increment <= Byte.MAX_VALUE && increment >= Byte.MIN_VALUE) {      
      writeOpCode(0x84); // iinc
      write1(index);
      write1(increment);
    } else if (increment <= Short.MAX_VALUE && increment >= Short.MIN_VALUE) {
      writeOpCode(0xC4); // wide
      write1(0x84); // iinc
      write2(index);
      write2(increment);
    } else {
      loadLocalInt(index);
      loadConstant(increment);
      addInt();
      storeLocalInt(index);
    }
  }

  /**
   * Opcode <tt>i2l</tt>.
   */
  public void convertIntToLong() {
    
    writeOpCode(0x85); // i2l
    incrementStack(1);
  }
  
  /**
   * Opcode <tt>i2f</tt>.
   */
  public void convertIntToFloat() {
    
    writeOpCode(0x86); // i2f
  }
  
  /**
   * Opcode <tt>i2d</tt>.
   */
  public void convertIntToDouble() {
    
    writeOpCode(0x87); // i2d
    incrementStack(1);
  }
  
  /**
   * Opcode <tt>l2i</tt>.
   */
  public void convertLongToInt() {
    
    writeOpCode(0x88); // l2i
    currentStack--;
  }
  
  /**
   * Opcode <tt>l2f</tt>.
   */
  public void convertLongToFloat() {
    
    writeOpCode(0x89); // l2f
    currentStack--;
  }
  
  /**
   * Opcode <tt>l2d</tt>.
   */
  public void convertLongToDouble() {
    
    writeOpCode(0x8A); // l2d
  }
  
  /**
   * Opcode <tt>f2i</tt>.
   */
  public void convertFloatToInt() {
    
    writeOpCode(0x8B); // f2i
  }
  
  /**
   * Opcode <tt>f2l</tt>.
   */
  public void convertFloatToLong() {
    
    writeOpCode(0x8C); // f2l
    incrementStack(1);
  }
  
  /**
   * Opcode <tt>f2d</tt>.
   */
  public void convertFloatToDouble() {
    
    writeOpCode(0x8D); // f2d
    incrementStack(1);
  }
  
  /**
   * Opcode <tt>d2i</tt>.
   */
  public void convertDoubleToInt() {
    
    writeOpCode(0x8E); // d2i
    currentStack--;
  }
  
  /**
   * Opcode <tt>d2l</tt>.
   */
  public void convertDoubleToLong() {
    
    writeOpCode(0x8F); // d2l
  }
  
  /**
   * Opcode <tt>d2f</tt>.
   */
  public void convertDoubleToFloat() {
    
    writeOpCode(0x90); // d2f
    currentStack--;
  }
  
  /**
   * Opcode <tt>i2b</tt>.
   */
  public void convertIntToByte() {
    
    writeOpCode(0x91); // i2b
  }
  
  /**
   * Opcode <tt>i2c</tt>.
   */
  public void convertIntToChar() {
    
    writeOpCode(0x92); // i2c
  }
  
  /**
   * Opcode <tt>i2s</tt>.
   */
  public void convertIntToShort() {
    
    writeOpCode(0x93); // i2s
  }
  
  /**
   * Opcode <tt>lcmp</tt>.
   */
  public void compareLong() {
    
    writeOpCode(0x94); // lcmp
    currentStack -= 3;
  }

  /**
   * Opcode <tt>fcmpg</tt> or <tt>fcmpl</tt>.
   * @param nanIsMinus
   */
  public void compareFloat(boolean nanIsMinus) {
    
    writeOpCode(nanIsMinus ? 0x95 : 0x96); // fcmpg : fcmpl
    currentStack--;
  }
  
  /**
   * Opcode <tt>dcmpg</tt> or <tt>dcmpl</tt>.
   * @param nanIsMinus
   */
  public void compareDouble(boolean nanIsMinus) {
    
    writeOpCode(nanIsMinus ? 0x97 : 0x98); // dcmpg : dcmpl
    currentStack -= 3;
  }
  
  /**
   * Opcode <tt>ifeq</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void branchIfEqual(String labelName) {
    
    writeOpCode(0x99); // ifeq
    currentStack--;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <tt>ifne</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void branchIfNotEqual(String labelName) {
    
    writeOpCode(0x9A); // ifne
    currentStack--;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <tt>iflt</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void branchIfLess(String labelName) {
    
    writeOpCode(0x9B); // iflt
    currentStack--;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <tt>ifge</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void branchIfGreaterEqual(String labelName) {
    
    writeOpCode(0x9C); // ifge
    currentStack--;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <tt>ifgt</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void branchIfGreater(String labelName) {
    
    writeOpCode(0x9D); // ifgt
    currentStack--;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <tt>ifle</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void branchIfLessEqual(String labelName) {
    
    writeOpCode(0x9E); // ifle
    currentStack--;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <tt>if_icmpeq</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void compareIntBranchIfEqual(String labelName) {
    
    writeOpCode(0x9F); // if_icmpeq
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <tt>if_icmpne</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void compareIntBranchIfNotEqual(String labelName) {
    
    writeOpCode(0xA0); // if_icmpne
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <tt>if_icmplt</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void compareIntBranchIfLess(String labelName) {
    
    writeOpCode(0xA1); // if_icmplt
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <tt>if_icmpge</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void compareIntBranchIfGreaterEqual(String labelName) {
    
    writeOpCode(0xA2); // if_icmpge
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <tt>if_icmpgt</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void compareIntBranchIfGreater(String labelName) {
    
    writeOpCode(0xA3); // if_icmpgt
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <tt>if_icmple</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void compareIntBranchIfLessEqual(String labelName) {
    
    writeOpCode(0xA4); // if_icmple
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <tt>if_acmpeq</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void compareReferenceBranchIfEqual(String labelName) {
    
    writeOpCode(0xA5); // if_acmpeq
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <tt>if_acmpne</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void compareReferenceBranchIfNotEqual(String labelName) {
    
    writeOpCode(0xA6); // if_acmpne
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <tt>goto</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void branch(String labelName) {
    
    writeOpCode(0xA7); // goto
    createJump(2, labelName);
    currentStack = 0;
  }
  
  /**
   * Opcode <tt>jsr</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void jumpSubroutine(String labelName) {
    
    writeOpCode(0xA8); // jsr
    incrementStack(1);
    createJump(2, labelName);
  }
  
  /**
   * Opcode <tt>ret</tt> (address variable by name).
   * @param variableName
   */
  public void returnFromSubroutine(String variableName) {
    
    returnFromSubroutine(getLocalVariable(variableName));
  }
  
  /**
   * Opcode <tt>ret</tt>.
   * @param index
   */
  public void returnFromSubroutine(int index) {
    
    checkMaxLocals(index);
    
    if (index <= Byte.MAX_VALUE) {
      writeOpCode(0xA9); // ret
      write1(index);
    } else {
      writeOpCode(0xC4); // wide
      write1(0xA9); // ret
      write2(index);
    }
  }
  
  /**
   * Opcode <tt>tableswitch</tt>.
   * @param table
   */
  public void tableswitch(BranchTable table) {
    
    codeSwitch(0xAA, table); // tableswitch
  }
  
  /**
   * Opcode <tt>lookupswitch</tt>.
   * @param table
   */
  public void lookupswitch(BranchTable table) {
    
    codeSwitch(0xAB, table); // lookupswitch
  }
  
  private void codeSwitch(int opCode, BranchTable table) {
    
    if (table.getPairList().size() == 0) {
      if (table.getDefaultLabelName() != null) {
        branch(table.getDefaultLabelName()); // goto
      }
      return;
    }
        
    currentStack--;
    
    writeOpCode(opCode); // tableswitch, lookupswitch
    
    while ((buffer.size() & 0x3) != 0) {
      write1(0);
    }
    
    createJump(4, table.getDefaultLabelName());
    
    switch (opCode) {
      case 0xAA:
        codeTableswitch(table);
        break;
      case 0xAB:
        codeLookupswitch(table);
        break;
    }
    
    currentStack = 0;
  }
  
  private void codeTableswitch(BranchTable table) {

    List<BranchTable.Pair> pairList = table.getPairList();
    
    int minKey = pairList.get(0).getKey();
    int maxKey = pairList.get(pairList.size() - 1).getKey();
    
    write4(minKey);
    write4(maxKey);

    int expectedKey = minKey; 
    int tableIndex = 0;
    
    while (expectedKey <= maxKey) {
      BranchTable.Pair pair = pairList.get(tableIndex++);
      while (expectedKey < pair.getKey()) {
        createJump(4, table.getDefaultLabelName());
        expectedKey++;
      }
      createJump(4, pair.getLabelName());
      expectedKey++;
    }
  }
  
  private void codeLookupswitch(BranchTable table) {
    
    List<BranchTable.Pair> pairList = table.getPairList();
    
    write4(pairList.size());
    
    for (BranchTable.Pair pair : pairList) {
      write4(pair.getKey());
      createJump(4, pair.getLabelName());
    }
  }
  
  /**
   * Opcode <tt>ireturn</tt>.
   */
  public void returnInt() {
    
    writeOpCode(0xAC); // ireturn
    currentStack = 0;
  }
  
  /**
   * Opcode <tt>lreturn</tt>.
   */
  public void returnLong() {
    
    writeOpCode(0xAD); // lreturn
    currentStack = 0;
  }
  
  /**
   * Opcode <tt>freturn</tt>.
   */
  public void returnFloat() {
    
    writeOpCode(0xAE); // freturn
    currentStack = 0;
  }
  
  /**
   * Opcode <tt>dreturn</tt>.
   */
  public void returnDouble() {
    
    writeOpCode(0xAF); // dreturn
    currentStack = 0;
  }
  
  /**
   * Opcode <tt>areturn</tt>.
   */
  public void returnReference() {
    
    writeOpCode(0xB0); // areturn
    currentStack = 0;
  }
  
  /**
   * Opcode <tt>return</tt>.
   */
  public void returnVoid() {
    
    writeOpCode(0xB1); // return
    currentStack = 0;
  }
  
  /**
   * Opcode <tt>getstatic</tt>.
   * @param fieldClass
   * @param fieldType
   * @param fieldName
   */
  public void getStatic(Type fieldClass, Type fieldType, String fieldName) {
    
    writeOpCode(0xB2); // getstatic
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
    incrementStack(1);
  }
  
  /**
   * Opcode <tt>putstatic</tt>.
   * @param fieldClass
   * @param fieldType
   * @param fieldName
   */
  public void putStatic(Type fieldClass, Type fieldType, String fieldName) {

    writeOpCode(0xB3); // putstatic
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
    currentStack--;
  }
  
  /**
   * Opcode <tt>getfield</tt>.
   * @param fieldClass
   * @param fieldType
   * @param fieldName
   */
  public void getField(Type fieldClass, Type fieldType, String fieldName) {
    
    writeOpCode(0xB4); // getfield
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
  }
  
  /**
   * Opcode <tt>putfield</tt>.
   * @param fieldClass
   * @param fieldType
   * @param fieldName
   */
  public void putField(Type fieldClass, Type fieldType, String fieldName) {

    writeOpCode(0xB5); // putfield
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
    currentStack -= 2;
  }
  
  /**
   * Opcode <tt>invokevirtual</tt>.
   * @param methodClass
   * @param returnType
   * @param methodName
   * @param argTypes
   */
  public void invokeVirtual(Type methodClass, Type returnType, String methodName, Type... argTypes) {
    
    invoke(0xB6, 1, methodClass, returnType, methodName, argTypes); // invokevirtual
  }
  
  /**
   * Opcode <tt>invokespecial</tt>.
   * @param methodClass
   * @param returnType
   * @param methodName
   * @param argTypes
   */
  public void invokeSpecial(Type methodClass, Type returnType, String methodName, Type... argTypes) {
    
    invoke(0xB7, 1, methodClass, returnType, methodName, argTypes); // invokespecial
  }
  
  /**
   * Opcode <tt>invokestatic</tt>.
   * @param methodClass
   * @param returnType
   * @param methodName
   * @param argTypes
   */
  public void invokeStatic(Type methodClass, Type returnType, String methodName, Type... argTypes) {
    
    invoke(0xB8, 0, methodClass, returnType, methodName, argTypes); // invokestatic
  }
  
  /**
   * Opcode <tt>invokeinterface</tt>.
   * @param methodClass
   * @param returnType
   * @param methodName
   * @param argTypes
   */
  public void invokeInterface(Type methodClass, Type returnType, String methodName, Type... argTypes) {
    
    invoke(0xB9, 1, methodClass, returnType, methodName, argTypes); // invokeinterface
  }
  
  private void invoke(int opCode, int referenceCount, Type methodClass, Type returnType, String methodName, Type... argTypes) {
    
    int popCount = referenceCount;
    for (int i = 0; i < argTypes.length; i++) {
      popCount += argTypes[i].getSize();
    }
    currentStack -= popCount;
    
    writeOpCode(opCode);
    write2(constantPool.addMethodref(methodClass, methodName, returnType, argTypes));
    
    if (opCode == 0xB9) { // invokeinterface (historical)     
      write1(popCount);
      write1(0);
    }
    
    incrementStack(returnType.getSize());
  }

  /**
   * Opcode <tt>new</tt>.
   * @param clazz
   */
  public void newObject(Type clazz) {
    
    writeOpCode(0xBB); // new
    write2(constantPool.addClass(clazz));
    incrementStack(1);
  }
  
  /**
   * Opcode <tt>newarray</tt> or <tt>anewarray</tt>.
   * @param elementType
   */
  public void newArray(Type elementType) {
    
    int arrayType = 0;
    switch (elementType.getDescriptor().charAt(0)) {
      case 'Z':
        arrayType = 4;
        break;
      case 'C':
        arrayType = 5;
        break;
      case 'F':
        arrayType = 6;
        break;
      case 'D':
        arrayType = 7;
        break;
      case 'B':
        arrayType = 8;
        break;
      case 'S':
        arrayType = 9;
        break;
      case 'I':
        arrayType = 10;
        break;
      case 'J':
        arrayType = 11;
        break;
    }

    if (arrayType != 0) {      
      writeOpCode(0xBC); // newarray
      write1(arrayType);
    } else {
      writeOpCode(0xBD); // anewarray
      write2(constantPool.addClass(elementType));
    }
  }
  
  /**
   * Opcode <tt>arraylength</tt>.
   */
  public void arraylength() {
    
    writeOpCode(0xBE); // arraylength
  }
  
  /**
   * Opcode <tt>athrow</tt>.
   */
  public void throwException() {

    writeOpCode(0xBF); // athrow
    currentStack = 0;
    incrementStack(1);
  }
  
  /**
   * Opcode <tt>checkcast</tt>.
   * @param checkedType
   */
  public void cast(Type checkedType) {
    
    writeOpCode(0xC0); // checkcast
    write2(constantPool.addClass(checkedType));
  }
  
  /**
   * Opcode <tt>instanceof</tt>.
   * @param checkedType
   */
  public void checkInstance(Type checkedType) {
    
    writeOpCode(0xC1); // instanceof
    write2(constantPool.addClass(checkedType));
  }
  
  /**
   * Opcode <tt>monitorenter</tt>.
   */
  public void monitorEnter() {
    
    writeOpCode(0xC2); // monitorenter
    currentStack--;
  }
  
  /**
   * Opcode <tt>monitorexit</tt>.
   */
  public void monitorExit() {
    
    writeOpCode(0xC3); // monitorexit
    currentStack--;
  }
  
  /**
   * Opcode <tt>multianewarray</tt>.
   * @param clazz
   * @param dimensions
   */
  public void newMultiReferenceArray(Type clazz, int dimensions) {
    
    writeOpCode(0xC5); // multianewarray
    write2(constantPool.addClass(clazz));
    write1(dimensions);
    currentStack -= (dimensions - 1);
  }
  
  /**
   * Opcode <tt>ifnull</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void branchIfNull(String labelName) {
    
    writeOpCode(0xC6); // ifnull
    currentStack--;
    createJump(2, labelName);
  }
    
  /**
   * Opcode <tt>ifnonnull</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void branchIfNonNull(String labelName) {
    
    writeOpCode(0xC7); // ifnonnull
    currentStack--;
    createJump(2, labelName);
  }

  /**
   * Opcode <tt>goto_w</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void branchFar(String labelName) {
    
    writeOpCode(0xC8); // goto_w
    createJump(4, labelName);
    currentStack = 0;
  }
  
  /**
   * Opcode <tt>jsr_w</tt>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName
   */
  public void jumpFarSubroutine(String labelName) {
    
    writeOpCode(0xC9); // jsr_w
    incrementStack(1);
    createJump(4, labelName);
  }
  
  public void generate(DataOutputStream out) throws Exception {
    
    buffer.close();
    byte[] code = buffer.toByteArray();
    resolveJumpList(code);
    
    out.writeShort(nameIndex);
    out.writeInt(12 + code.length + exceptionHandlerList.size() * 8);
    out.writeShort(maxStackSize);
    out.writeShort(maxLocals);
    out.writeInt(code.length);
    out.write(code);
    generateExceptionHandlers(out);
    out.writeShort(0); // attributes
  }
  
  private void resolveJumpList(byte[] code) throws Exception {
    
    for (Jump jump : jumpList) {
      resolveJump(jump, code);
    }
  }
  
  private void resolveJump(Jump jump, byte[] code) throws Exception {
    
    Label label = jump.getLabel();
    if (! label.isDefined()) {
      throw new CafebabeException(text.get("undefinedLabel", label.getName()));
    }
    
    int distance = label.getOffset() - jump.getOpCodeOffset();
    int index = jump.getJumpOffset();
    
    switch (jump.getSize()) {
      case 4:
        code[index++] = (byte) (0xff & (distance >> 24));
        code[index++] = (byte) (0xff & (distance >> 16));
      case 2:
        code[index++] = (byte) (0xff & (distance >> 8));
        code[index]   = (byte) (0xff & distance);
    }
  }
  
  private void generateExceptionHandlers(DataOutputStream out) throws Exception {

    out.writeShort(exceptionHandlerList.size());
    
    for (ExceptionHandlerDefinition handler : exceptionHandlerList) {
      generateProgramCounter(out, handler.getStartLabel());
      generateProgramCounter(out, handler.getEndLabel());
      generateProgramCounter(out, handler.getHandlerLabel());
      if (handler.getExceptionType() == null) {
        out.writeShort(0);
      } else {
        out.writeShort(constantPool.addClass(handler.getExceptionType()));
      }
    }
  }
  
  private void generateProgramCounter(DataOutputStream out, Label label) throws Exception {
    
    if (! label.isDefined()) {
      throw new CafebabeException(text.get("undefinedLabel", label.getName()));
    }
    
    out.writeShort(label.getOffset());
  }
}
