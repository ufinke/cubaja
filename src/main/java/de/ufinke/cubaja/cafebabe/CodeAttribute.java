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
 * <p>
 * Code portion of a method.
 * An instance is supplied by {@link GenMethod#getCode()}.
 * </p><p>
 * Use methods of this class in a way similar to assembler code.
 * Java instructions are added to the generated code by calling the corresponding methods. 
 * Just as in assembler, labels may be defined to mark branch target points.
 * Local variables may be identified by their index number, or by name.
 * Constant pool entries are added automatically when needed.
 * </p>
 * <table class="striped">
 * <caption style="text-align:left">Java VM opcodes and their corresponding methods within this class</caption>
 * <thead>
 * <tr>
 * <th scope="col" style="text-align:left">opcode</th>
 * <th scope="col" style="text-align:left">mnemonic</th>
 * <th scope="col" style="text-align:left">implementing method(s)</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr style="vertical-align:top"><td><code>00</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.nop"><code>nop</code></a></td><td>{@link #nop()}</td></tr>
 * <tr style="vertical-align:top"><td><code>01</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.aconst_null"><code>aconst_null</code></a></td><td>{@link #loadNull()}</td></tr>
 * <tr style="vertical-align:top"><td><code>02</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iconst_i"><code>iconst_m1</code></a></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr style="vertical-align:top"><td><code>03</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iconst_i"><code>iconst_0</code></a></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr style="vertical-align:top"><td><code>04</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iconst_i"><code>iconst_1</code></a></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr style="vertical-align:top"><td><code>05</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iconst_i"><code>iconst_2</code></a></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr style="vertical-align:top"><td><code>06</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iconst_i"><code>iconst_3</code></a></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr style="vertical-align:top"><td><code>07</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iconst_i"><code>iconst_4</code></a></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr style="vertical-align:top"><td><code>08</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iconst_i"><code>iconst_5</code></a></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr style="vertical-align:top"><td><code>09</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lconst_l"><code>lconst_0</code></a></td><td>{@link #loadConstant(long)}</td></tr>
 * <tr style="vertical-align:top"><td><code>0A</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lconst_l"><code>lconst_1</code></a></td><td>{@link #loadConstant(long)}</td></tr>
 * <tr style="vertical-align:top"><td><code>0B</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fconst_f"><code>fconst_0</code></a></td><td>{@link #loadConstant(float)}</td></tr>
 * <tr style="vertical-align:top"><td><code>0C</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fconst_f"><code>fconst_1</code></a></td><td>{@link #loadConstant(float)}</td></tr>
 * <tr style="vertical-align:top"><td><code>0D</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fconst_f"><code>fconst_2</code></a></td><td>{@link #loadConstant(float)}</td></tr>
 * <tr style="vertical-align:top"><td><code>0E</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dconst_d"><code>dconst_0</code></a></td><td>{@link #loadConstant(double)}</td></tr>
 * <tr style="vertical-align:top"><td><code>0F</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dconst_d"><code>dconst_1</code></a></td><td>{@link #loadConstant(double)}</td></tr>
 * <tr style="vertical-align:top"><td><code>10</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.bipush"><code>bipush</code></a></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr style="vertical-align:top"><td><code>11</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.sipush"><code>sipush</code></a></td><td>{@link #loadConstant(int)}</td></tr>
 * <tr style="vertical-align:top"><td><code>12</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ldc"><code>ldc</code></a></td><td>{@link #loadConstant(int)}, {@link #loadConstant(float)}, {@link #loadConstant(Type)}}</td></tr>
 * <tr style="vertical-align:top"><td><code>13</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ldc_w"><code>ldc_w</code></a></td><td>{@link #loadConstant(int)}, {@link #loadConstant(float)}, {@link #loadConstant(Type)}}</td></tr>
 * <tr style="vertical-align:top"><td><code>14</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ldc2_w"><code>ldc2_w</code></a></td><td>{@link #loadConstant(long)}, {@link #loadConstant(double)}</td></tr>
 * <tr style="vertical-align:top"><td><code>15</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iload"><code>iload</code></a></td><td>{@link #loadLocalInt(int)}, {@link #loadLocalInt(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>16</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lload"><code>lload</code></a></td><td>{@link #loadLocalLong(int)}, {@link #loadLocalLong(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>17</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fload"><code>fload</code></a></td><td>{@link #loadLocalFloat(int)}, {@link #loadLocalFloat(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>18</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dload"><code>dload</code></a></td><td>{@link #loadLocalDouble(int)}, {@link #loadLocalDouble(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>19</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.aload"><code>aload</code></a></td><td>{@link #loadLocalReference(int)}, {@link #loadLocalReference(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>1A</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iload_n"><code>iload_0</code></a></td><td>{@link #loadLocalInt(int)}, {@link #loadLocalInt(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>1B</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iload_n"><code>iload_1</code></a></td><td>{@link #loadLocalInt(int)}, {@link #loadLocalInt(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>1C</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iload_n"><code>iload_2</code></a></td><td>{@link #loadLocalInt(int)}, {@link #loadLocalInt(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>1D</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iload_n"><code>iload_3</code></a></td><td>{@link #loadLocalInt(int)}, {@link #loadLocalInt(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>1E</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lload_n"><code>lload_0</code></a></td><td>{@link #loadLocalLong(int)}, {@link #loadLocalLong(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>1F</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lload_n"><code>lload_1</code></a></td><td>{@link #loadLocalLong(int)}, {@link #loadLocalLong(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>20</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lload_n"><code>lload_2</code></a></td><td>{@link #loadLocalLong(int)}, {@link #loadLocalLong(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>21</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lload_n"><code>lload_3</code></a></td><td>{@link #loadLocalLong(int)}, {@link #loadLocalLong(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>22</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fload_n"><code>fload_0</code></a></td><td>{@link #loadLocalFloat(int)}, {@link #loadLocalFloat(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>23</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fload_n"><code>fload_1</code></a></td><td>{@link #loadLocalFloat(int)}, {@link #loadLocalFloat(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>24</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fload_n"><code>fload_2</code></a></td><td>{@link #loadLocalFloat(int)}, {@link #loadLocalFloat(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>25</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fload_n"><code>fload_3</code></a></td><td>{@link #loadLocalFloat(int)}, {@link #loadLocalFloat(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>26</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dload_n"><code>dload_0</code></a></td><td>{@link #loadLocalDouble(int)}, {@link #loadLocalDouble(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>27</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dload_n"><code>dload_1</code></a></td><td>{@link #loadLocalDouble(int)}, {@link #loadLocalDouble(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>28</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dload_n"><code>dload_2</code></a></td><td>{@link #loadLocalDouble(int)}, {@link #loadLocalDouble(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>29</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dload_n"><code>dload_3</code></a></td><td>{@link #loadLocalDouble(int)}, {@link #loadLocalDouble(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>2A</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.aload_n"><code>aload_0</code></a></td><td>{@link #loadLocalReference(int)}, {@link #loadLocalReference(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>2B</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.aload_n"><code>aload_1</code></a></td><td>{@link #loadLocalReference(int)}, {@link #loadLocalReference(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>2C</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.aload_n"><code>aload_2</code></a></td><td>{@link #loadLocalReference(int)}, {@link #loadLocalReference(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>2D</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.aload_n"><code>aload_3</code></a></td><td>{@link #loadLocalReference(int)}, {@link #loadLocalReference(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>2E</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iaload"><code>iaload</code></a></td><td>{@link #loadIntArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>2F</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.laload"><code>laload</code></a></td><td>{@link #loadLongArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>30</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.faload"><code>faload</code></a></td><td>{@link #loadFloatArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>31</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.daload"><code>daload</code></a></td><td>{@link #loadDoubleArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>32</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.aaload"><code>aaload</code></a></td><td>{@link #loadReferenceArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>33</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.baload"><code>baload</code></a></td><td>{@link #loadBooleanArrayElement()}, {@link #loadByteArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>34</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.caload"><code>caload</code></a></td><td>{@link #loadCharArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>35</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.saload"><code>saload</code></a></td><td>{@link #loadShortArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>36</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.istore"><code>istore</code></a></td><td>{@link #storeLocalInt(int)}, {@link #storeLocalInt(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>37</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lstore"><code>lstore</code></a></td><td>{@link #storeLocalLong(int)}, {@link #storeLocalLong(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>38</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fstore"><code>fstore</code></a></td><td>{@link #storeLocalFloat(int)}, {@link #storeLocalFloat(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>39</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dstore"><code>dstore</code></a></td><td>{@link #storeLocalDouble(int)}, {@link #storeLocalDouble(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>3A</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.astore"><code>astore</code></a></td><td>{@link #storeLocalReference(int)}, {@link #storeLocalReference(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>3B</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.istore_n"><code>istore_0</code></a></td><td>{@link #storeLocalInt(int)}, {@link #storeLocalInt(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>3C</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.istore_n"><code>istore_1</code></a></td><td>{@link #storeLocalInt(int)}, {@link #storeLocalInt(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>3D</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.istore_n"><code>istore_2</code></a></td><td>{@link #storeLocalInt(int)}, {@link #storeLocalInt(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>3E</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.istore_n"><code>istore_3</code></a></td><td>{@link #storeLocalInt(int)}, {@link #storeLocalInt(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>3F</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lstore_n"><code>lstore_0</code></a></td><td>{@link #storeLocalLong(int)}, {@link #storeLocalLong(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>40</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lstore_n"><code>lstore_1</code></a></td><td>{@link #storeLocalLong(int)}, {@link #storeLocalLong(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>41</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lstore_n"><code>lstore_2</code></a></td><td>{@link #storeLocalLong(int)}, {@link #storeLocalLong(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>42</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lstore_n"><code>lstore_3</code></a></td><td>{@link #storeLocalLong(int)}, {@link #storeLocalLong(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>43</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fstore_n"><code>fstore_0</code></a></td><td>{@link #storeLocalFloat(int)}, {@link #storeLocalFloat(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>44</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fstore_n"><code>fstore_1</code></a></td><td>{@link #storeLocalFloat(int)}, {@link #storeLocalFloat(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>45</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fstore_n"><code>fstore_2</code></a></td><td>{@link #storeLocalFloat(int)}, {@link #storeLocalFloat(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>46</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fstore_n"><code>fstore_3</code></a></td><td>{@link #storeLocalFloat(int)}, {@link #storeLocalFloat(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>47</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dstore_n"><code>dstore_0</code></a></td><td>{@link #storeLocalDouble(int)}, {@link #storeLocalDouble(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>48</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dstore_n"><code>dstore_1</code></a></td><td>{@link #storeLocalDouble(int)}, {@link #storeLocalDouble(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>49</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dstore_n"><code>dstore_2</code></a></td><td>{@link #storeLocalDouble(int)}, {@link #storeLocalDouble(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>4A</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dstore_n"><code>dstore_3</code></a></td><td>{@link #storeLocalDouble(int)}, {@link #storeLocalDouble(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>4B</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.astore_n"><code>astore_0</code></a></td><td>{@link #storeLocalReference(int)}, {@link #storeLocalReference(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>4C</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.astore_n"><code>astore_1</code></a></td><td>{@link #storeLocalReference(int)}, {@link #storeLocalReference(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>4D</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.astore_n"><code>astore_2</code></a></td><td>{@link #storeLocalReference(int)}, {@link #storeLocalReference(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>4E</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.astore_n"><code>astore_3</code></a></td><td>{@link #storeLocalReference(int)}, {@link #storeLocalReference(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>4F</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iastore"><code>iastore</code></a></td><td>{@link #storeIntArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>50</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lastore"><code>lastore</code></a></td><td>{@link #storeLongArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>51</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fastore"><code>fastore</code></a></td><td>{@link #storeFloatArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>52</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dastore"><code>dastore</code></a></td><td>{@link #storeDoubleArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>53</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.aastore"><code>aastore</code></a></td><td>{@link #storeReferenceArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>54</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.bastore"><code>bastore</code></a></td><td>{@link #storeBooleanArrayElement()}, {@link #storeByteArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>55</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.castore"><code>castore</code></a></td><td>{@link #storeCharArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>56</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.sastore"><code>sastore</code></a></td><td>{@link #storeShortArrayElement()}</td></tr>
 * <tr style="vertical-align:top"><td><code>57</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.pop"><code>pop</code></a></td><td>{@link #pop()}, {@link #pop(int)}</td></tr>
 * <tr style="vertical-align:top"><td><code>58</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.pop2"><code>pop2</code></a></td><td>{@link #popDouble()}, {@link #pop(int)}</td></tr>
 * <tr style="vertical-align:top"><td><code>59</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dup"><code>dup</code></a></td><td>{@link #duplicate()}</td></tr>
 * <tr style="vertical-align:top"><td><code>5A</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dup_x1"><code>dup_x1</code></a></td><td>{@link #duplicateSkip()}</td></tr>
 * <tr style="vertical-align:top"><td><code>5B</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dup_x2"><code>dup_x2</code></a></td><td>{@link #duplicateSkipDouble()}</td></tr>
 * <tr style="vertical-align:top"><td><code>5C</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dup2"><code>dup2</code></a></td><td>{@link #duplicateDouble()}</td></tr>
 * <tr style="vertical-align:top"><td><code>5D</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dup2_x1"><code>dup2_x1</code></a></td><td>{@link #duplicateDoubleSkip()}</td></tr>
 * <tr style="vertical-align:top"><td><code>5E</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dup2_x2"><code>dup2_x2</code></a></td><td>{@link #duplicateDoubleSkipDouble()}</td></tr>
 * <tr style="vertical-align:top"><td><code>5F</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.swap"><code>swap</code></a></td><td>{@link #swap()}</td></tr>
 * <tr style="vertical-align:top"><td><code>60</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iadd"><code>iadd</code></a></td><td>{@link #addInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>61</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ladd"><code>ladd</code></a></td><td>{@link #addLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>62</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fadd"><code>fadd</code></a></td><td>{@link #addFloat()}</td></tr>
 * <tr style="vertical-align:top"><td><code>63</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dadd"><code>dadd</code></a></td><td>{@link #addDouble()}</td></tr>
 * <tr style="vertical-align:top"><td><code>64</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.isub"><code>isub</code></a></td><td>{@link #subtractInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>65</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lsub"><code>lsub</code></a></td><td>{@link #subtractLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>66</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fsub"><code>fsub</code></a></td><td>{@link #subtractFloat()}</td></tr>
 * <tr style="vertical-align:top"><td><code>67</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dsub"><code>dsub</code></a></td><td>{@link #subtractDouble()}</td></tr>
 * <tr style="vertical-align:top"><td><code>68</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.imul"><code>imul</code></a></td><td>{@link #multiplyInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>69</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lmul"><code>lmul</code></a></td><td>{@link #multiplyLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>6A</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fmul"><code>fmul</code></a></td><td>{@link #multiplyFloat()}</td></tr>
 * <tr style="vertical-align:top"><td><code>6B</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dmul"><code>dmul</code></a></td><td>{@link #multiplyDouble()}</td></tr>
 * <tr style="vertical-align:top"><td><code>6C</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.idiv"><code>idiv</code></a></td><td>{@link #divideInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>6D</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ldiv"><code>ldiv</code></a></td><td>{@link #divideLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>6E</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fdiv"><code>fdiv</code></a></td><td>{@link #divideFloat()}</td></tr>
 * <tr style="vertical-align:top"><td><code>6F</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ddiv"><code>ddiv</code></a></td><td>{@link #divideDouble()}</td></tr>
 * <tr style="vertical-align:top"><td><code>70</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.irem"><code>irem</code></a></td><td>{@link #remainderInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>71</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lrem"><code>lrem</code></a></td><td>{@link #remainderLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>72</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.frem"><code>frem</code></a></td><td>{@link #remainderFloat()}</td></tr>
 * <tr style="vertical-align:top"><td><code>73</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.drem"><code>drem</code></a></td><td>{@link #remainderDouble()}</td></tr>
 * <tr style="vertical-align:top"><td><code>74</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ineg"><code>ineg</code></a></td><td>{@link #negateInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>75</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lneg"><code>lneg</code></a></td><td>{@link #negateLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>76</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fneg"><code>fneg</code></a></td><td>{@link #negateFloat()}</td></tr>
 * <tr style="vertical-align:top"><td><code>77</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dneg"><code>dneg</code></a></td><td>{@link #negateDouble()}</td></tr>
 * <tr style="vertical-align:top"><td><code>78</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ishl"><code>ishl</code></a></td><td>{@link #shiftLeftInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>79</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lshl"><code>lshl</code></a></td><td>{@link #shiftLeftLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>7A</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ishr"><code>ishr</code></a></td><td>{@link #arithmeticShiftRightInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>7B</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lshr"><code>lshr</code></a></td><td>{@link #arithmeticShiftRightLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>7C</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iushr"><code>iushr</code></a></td><td>{@link #logicalShiftRightInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>7D</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lushr"><code>lushr</code></a></td><td>{@link #logicalShiftRightLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>7E</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iand"><code>iand</code></a></td><td>{@link #booleanAndInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>7F</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.land"><code>land</code></a></td><td>{@link #booleanAndLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>80</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ior"><code>ior</code></a></td><td>{@link #booleanOrInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>81</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lor"><code>lor</code></a></td><td>{@link #booleanOrLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>82</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ixor"><code>ixor</code></a></td><td>{@link #booleanXorInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>83</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lxor"><code>lxor</code></a></td><td>{@link #booleanXorLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>84</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.iinc"><code>iinc</code></a></td><td>{@link #incrementLocalInt(int, int)}, {@link #incrementLocalInt(String, int)}</td></tr>
 * <tr style="vertical-align:top"><td><code>85</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.i2l"><code>i2l</code></a></td><td>{@link #convertIntToLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>86</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.i2f"><code>i2f</code></a></td><td>{@link #convertIntToFloat()}</td></tr>
 * <tr style="vertical-align:top"><td><code>87</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.i2d"><code>i2d</code></a></td><td>{@link #convertIntToDouble()}</td></tr>
 * <tr style="vertical-align:top"><td><code>88</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.l2i"><code>l2i</code></a></td><td>{@link #convertLongToInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>89</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.l2f"><code>l2f</code></a></td><td>{@link #convertLongToFloat()}</td></tr>
 * <tr style="vertical-align:top"><td><code>8A</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.l2d"><code>l2d</code></a></td><td>{@link #convertLongToDouble()}</td></tr>
 * <tr style="vertical-align:top"><td><code>8B</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.f2i"><code>f2i</code></a></td><td>{@link #convertFloatToInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>8C</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.f2l"><code>f2l</code></a></td><td>{@link #convertFloatToLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>8D</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.f2d"><code>f2d</code></a></td><td>{@link #convertFloatToDouble()}</td></tr>
 * <tr style="vertical-align:top"><td><code>8E</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.d2i"><code>d2i</code></a></td><td>{@link #convertDoubleToInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>8F</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.d2l"><code>d2l</code></a></td><td>{@link #convertDoubleToLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>90</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.d2f"><code>d2f</code></a></td><td>{@link #convertDoubleToFloat()}</td></tr>
 * <tr style="vertical-align:top"><td><code>91</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.i2b"><code>i2b</code></a></td><td>{@link #convertIntToByte()}</td></tr>
 * <tr style="vertical-align:top"><td><code>92</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.i2c"><code>i2c</code></a></td><td>{@link #convertIntToChar()}</td></tr>
 * <tr style="vertical-align:top"><td><code>93</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.i2s"><code>i2s</code></a></td><td>{@link #convertIntToShort()}</td></tr>
 * <tr style="vertical-align:top"><td><code>94</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lcmp"><code>lcmp</code></a></td><td>{@link #compareLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>95</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fcmpop"><code>fcmpl</code></a></td><td>{@link #compareFloat(boolean)}</td></tr>
 * <tr style="vertical-align:top"><td><code>96</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.fcmpop"><code>fcmpg</code></a></td><td>{@link #compareFloat(boolean)}</td></tr>
 * <tr style="vertical-align:top"><td><code>97</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dcmpop"><code>dcmpl</code></a></td><td>{@link #compareDouble(boolean)}</td></tr>
 * <tr style="vertical-align:top"><td><code>98</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dcmpop"><code>dcmpg</code></a></td><td>{@link #compareDouble(boolean)}</td></tr>
 * <tr style="vertical-align:top"><td><code>99</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ifcond"><code>ifeq</code></a></td><td>{@link #branchIfEqual(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>9A</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ifcond"><code>ifne</code></a></td><td>{@link #branchIfNotEqual(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>9B</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ifcond"><code>iflt</code></a></td><td>{@link #branchIfLess(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>9C</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ifcond"><code>ifge</code></a></td><td>{@link #branchIfGreaterEqual(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>9D</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ifcond"><code>ifgt</code></a></td><td>{@link #branchIfGreater(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>9E</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ifcond"><code>ifle</code></a></td><td>{@link #branchIfLessEqual(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>9F</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.if_icmpcond"><code>if_icmpeq</code></a></td><td>{@link #compareIntBranchIfEqual(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>A0</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.if_icmpcond"><code>if_icmpne</code></a></td><td>{@link #compareIntBranchIfNotEqual(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>A1</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.if_icmpcond"><code>if_icmplt</code></a></td><td>{@link #compareIntBranchIfLess(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>A2</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.if_icmpcond"><code>if_icmpge</code></a></td><td>{@link #compareIntBranchIfGreaterEqual(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>A3</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.if_icmpcond"><code>if_icmpgt</code></a></td><td>{@link #compareIntBranchIfGreater(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>A4</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.if_icmpcond"><code>if_icmple</code></a></td><td>{@link #compareIntBranchIfLessEqual(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>A5</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.if_acmpcond"><code>if_acmpeq</code></a></td><td>{@link #compareReferenceBranchIfEqual(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>A6</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.if_acmpcond"><code>if_acmpne</code></a></td><td>{@link #compareReferenceBranchIfNotEqual(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>A7</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.goto"><code>goto</code></a></td><td>{@link #branch(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>A8</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.jsr"><code>jsr</code></a></td><td>{@link #jumpSubroutine(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>A9</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ret"><code>ret</code></a></td><td>{@link #returnFromSubroutine(int)}, {@link #returnFromSubroutine(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>AA</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.tableswitch"><code>tableswitch</code></a></td><td>{@link #tableswitch(BranchTable)}</td></tr>
 * <tr style="vertical-align:top"><td><code>AB</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lookupswitch"><code>lookupswitch</code></a></td><td>{@link #lookupswitch(BranchTable)}</td></tr>
 * <tr style="vertical-align:top"><td><code>AC</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ireturn"><code>ireturn</code></a></td><td>{@link #returnInt()}</td></tr>
 * <tr style="vertical-align:top"><td><code>AD</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.lreturn"><code>lreturn</code></a></td><td>{@link #returnLong()}</td></tr>
 * <tr style="vertical-align:top"><td><code>AE</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.freturn"><code>freturn</code></a></td><td>{@link #returnFloat()}</td></tr>
 * <tr style="vertical-align:top"><td><code>AF</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.dreturn"><code>dreturn</code></a></td><td>{@link #returnDouble()}</td></tr>
 * <tr style="vertical-align:top"><td><code>B0</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.areturn"><code>areturn</code></a></td><td>{@link #returnReference()}</td></tr>
 * <tr style="vertical-align:top"><td><code>B1</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.return"><code>return</code></a></td><td>{@link #returnVoid()}</td></tr>
 * <tr style="vertical-align:top"><td><code>B2</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.getstatic"><code>getstatic</code></a></td><td>{@link #getStatic(Type, Type, String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>B3</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.putstatic"><code>putstatic</code></a></td><td>{@link #putStatic(Type, Type, String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>B4</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.getfield"><code>getfield</code></a></td><td>{@link #getField(Type, Type, String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>B5</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.putfield"><code>putfield</code></a></td><td>{@link #putField(Type, Type, String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>B6</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.invokevirtual"><code>invokevirtual</code></a></td><td>{@link #invokeVirtual(Type, Type, String, Type...)}</td></tr>
 * <tr style="vertical-align:top"><td><code>B7</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.invokespecial"><code>invokespecial</code></a></td><td>{@link #invokeSpecial(Type, Type, String, Type...)}</td></tr>
 * <tr style="vertical-align:top"><td><code>B8</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.invokestatic"><code>invokestatic</code></a></td><td>{@link #invokeStatic(Type, Type, String, Type...)}</td></tr>
 * <tr style="vertical-align:top"><td><code>B9</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.invokeinterface"><code>invokeinterface</code></a></td><td>{@link #invokeInterface(Type, Type, String, Type...)}</td></tr>
 * <tr style="vertical-align:top"><td><code>BA</code></td><td><code><i>invokedynamic</i></code></td><td></td></tr>
 * <tr style="vertical-align:top"><td><code>BB</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.new"><code>new</code></a></td><td>{@link #newObject(Type)}</td></tr>
 * <tr style="vertical-align:top"><td><code>BC</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.newarray"><code>newarray</code></a></td><td>{@link #newArray(Type)}</td></tr>
 * <tr style="vertical-align:top"><td><code>BD</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.anewarray"><code>anewarray</code></a></td><td>{@link #newArray(Type)}</td></tr>
 * <tr style="vertical-align:top"><td><code>BE</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.arraylength"><code>arraylength</code></a></td><td>{@link #arraylength()}</td></tr>
 * <tr style="vertical-align:top"><td><code>BF</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.athrow"><code>athrow</code></a></td><td>{@link #throwException()}</td></tr>
 * <tr style="vertical-align:top"><td><code>C0</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.checkcast"><code>checkcast</code></a></td><td>{@link #cast(Type)}</td></tr>
 * <tr style="vertical-align:top"><td><code>C1</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.instanceof"><code>instanceof</code></a></td><td>{@link #checkInstance(Type)}</td></tr>
 * <tr style="vertical-align:top"><td><code>C2</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.monitorenter"><code>monitorenter</code></a></td><td>{@link #monitorEnter()}</td></tr>
 * <tr style="vertical-align:top"><td><code>C3</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.monitorexit"><code>monitorexit</code></a></td><td>{@link #monitorExit()}</td></tr>
 * <tr style="vertical-align:top"><td><code>C4</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.wide"><code>wide</code></a></td><td>{@link #loadLocalInt(int)}, {@link #loadLocalInt(String)}, {@link #loadLocalLong(int)}, {@link #loadLocalLong(String)}, {@link #loadLocalFloat(int)}, {@link #loadLocalFloat(String)}, {@link #loadLocalDouble(int)}, {@link #loadLocalDouble(String)}, {@link #loadLocalReference(int)}, {@link #loadLocalReference(String)}, {@link #storeLocalInt(int)}, {@link #storeLocalInt(String)}, {@link #storeLocalLong(int)}, {@link #storeLocalLong(String)}, {@link #storeLocalFloat(int)}, {@link #storeLocalFloat(String)}, {@link #storeLocalDouble(int)}, {@link #storeLocalDouble(String)}, {@link #storeLocalReference(int)}, {@link #storeLocalReference(String)}, {@link #incrementLocalInt(int, int)}, {@link #incrementLocalInt(String, int)}, {@link #returnFromSubroutine(int)}, {@link #returnFromSubroutine(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>C5</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.multianewarray"><code>multianewarray</code></a></td><td>{@link #newMultiReferenceArray(Type, int)}</td></tr>
 * <tr style="vertical-align:top"><td><code>C6</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ifnull"><code>ifnull</code></a></td><td>{@link #branchIfNull(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>C7</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.ifnonnull"><code>ifnonnull</code></a></td><td>{@link #branchIfNonNull(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>C8</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.goto_w"><code>goto_w</code></a></td><td>{@link #branchFar(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>C9</code></td><td><a href="http://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5.jsr_w"><code>jsr_w</code></a></td><td>{@link #jumpFarSubroutine(String)}</td></tr>
 * <tr style="vertical-align:top"><td><code>CA</code></td><td><code><i>breakpoint</i></code></td><td></td></tr>
 * <tr style="vertical-align:top"><td><code>FE</code></td><td><code><i>impdep1</i></code></td><td></td></tr>
 * <tr style="vertical-align:top"><td><code>FF</code></td><td><code><i>impdep2</i></code></td><td></td></tr>
 * </tbody>
 * </table>
 * 
 * @author Uwe Finke
 */
public class CodeAttribute implements Generatable {

  static private Text text = Text.getPackageInstance(CodeAttribute.class);
  
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
   * @param labelName a named label at this place of code
   */
  public void defineLabel(String labelName) {
    
    Label label = getLabel(labelName);
    label.define(buffer.size());
    checkLabelStack(label);
  }
  
  /**
   * Defines a <code>try-/catch</code>-block.
   * The <code>try</code>-block must be enclosed with a start and an end label.
   * The entry point of the <code>catch</code>-block (handler) must also be labelled.
   * @param startLabelName start label name of try block
   * @param endLabelName end label name of try block
   * @param exceptionType exception class or type
   * @param handlerLabelName start label name of catch block
   */
  public void defineExceptionHandler(String startLabelName, String endLabelName, Type exceptionType, String handlerLabelName) {
    
    exceptionHandlerList.add(new ExceptionHandlerDefinition(getLabel(startLabelName), getLabel(endLabelName), exceptionType, getLabel(handlerLabelName)));
  }
  
  /**
   * Returns the index of a named local variable.
   * @param variableName name of variable
   * @param type type of variable
   * @return index index position
   */
  public int getLocalVariable(String variableName, Type type) {
    
    int index = getLocalVariable(variableName);
    checkMaxLocals(index + type.getSize() - 1);
    return index;
  }

  /**
   * Opcode <code>nop</code>.
   */
  public void nop() {
    
    writeOpCode(0x00); // nop
  }
  
  /**
   * Opcode <code>aconst_null</code>.
   */
  public void loadNull() {
  
    writeOpCode(0x01); // aconst_null
    incrementStack(1);
  }
  
  /**
   * Opcode to load an integer constant 
   * (<code>iconst_&lt;n&gt;</code>, <code>bipush</code>, <code>sipush</code>, <code>ldc</code> or <code>ldc_w</code>).
   * @param value constant value
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
   * (<code>lconst_&lt;n&gt;</code> or <code>ldc2_w</code>).
   * @param value constant value
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
   * (<code>fconst_&lt;n&gt;</code>, <code>ldc</code> or <code>ldc_w</code>).
   * @param value constant value
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
   * (<code>dconst_&lt;n&gt;</code> or <code>ldc2_w</code>).
   * @param value constant value
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
   * (<code>ldc</code> or <code>lcd_w</code>).
   * @param value constant value
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
   * (<code>ldc</code> or <code>lcd_w</code>).
   * @param value class type as constant value
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
   * (<code>iload_&lt;n&gt;</code> or <code>iload</code>).
   * @param variableName name of variable
   */
  public void loadLocalInt(String variableName) {
    
    loadLocalInt(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to load a local int variable
   * (<code>iload_&lt;n&gt;</code> or <code>iload</code>).
   * @param index index position of variable
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
   * (<code>lload_&lt;n&gt;</code> or <code>lload</code>).
   * @param variableName name of variable
   */
  public void loadLocalLong(String variableName) {
    
    loadLocalLong(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to load a local long variable
   * (<code>lload_&lt;n&gt;</code> or <code>lload</code>).
   * @param index index position of variable
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
   * (<code>fload_&lt;n&gt;</code> or <code>fload</code>).
   * @param variableName name of variable
   */
  public void loadLocalFloat(String variableName) {
    
    loadLocalFloat(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to load a local float variable
   * (<code>fload_&lt;n&gt;</code> or <code>fload</code>).
   * @param index index position of variable
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
   * (<code>dload_&lt;n&gt;</code> or <code>dload</code>).
   * @param variableName name of variable
   */
  public void loadLocalDouble(String variableName) {
    
    loadLocalDouble(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to load a local double variable
   * (<code>dload_&lt;n&gt;</code> or <code>dload</code>).
   * @param index index position of variable
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
   * (<code>aload_&lt;n&gt;</code> or <code>aload</code>).
   * @param variableName name of variable
   */
  public void loadLocalReference(String variableName) {
    
    loadLocalReference(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to load a local reference variable
   * (<code>aload_&lt;n&gt;</code> or <code>aload</code>).
   * @param index index position of variable
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
   * Opcode <code>iaload</code>.
   */
  public void loadIntArrayElement() {
    
    writeOpCode(0x2E); // iaload
    currentStack--;
  }
  
  /**
   * Opcode <code>laload</code>.
   */
  public void loadLongArrayElement() {
    
    writeOpCode(0x2F); // laload
  }
  
  /**
   * Opcode <code>faload</code>.
   */
  public void loadFloatArrayElement() {
    
    writeOpCode(0x30); // faload
    currentStack--;
  }
  
  /**
   * Opcode <code>daload</code>.
   */
  public void loadDoubleArrayElement() {
    
    writeOpCode(0x31); // daload
  }
  
  /**
   * Opcode <code>aaload</code>.
   */
  public void loadReferenceArrayElement() {
    
    writeOpCode(0x32); // aaload
    currentStack--;
  }
  
  /**
   * Opcode <code>baload</code> (boolean).
   */
  public void loadBooleanArrayElement() {
    
    writeOpCode(0x33); // baload
    currentStack--;
  }
  
  /**
   * Opcode <code>baload</code> (byte).
   */
  public void loadByteArrayElement() {
    
    writeOpCode(0x33); // baload
    currentStack--;
  }
  
  /**
   * Opcode <code>caload</code>.
   */
  public void loadCharArrayElement() {
    
    writeOpCode(0x34); // caload
    currentStack--;
  }
  
  /**
   * Opcode <code>saload</code>.
   */
  public void loadShortArrayElement() {
    
    writeOpCode(0x35); // saload
    currentStack--;
  }
  
  /**
   * Opcode to store a local int variable by name
   * (<code>istore_&lt;n&gt;</code> or <code>istore</code>).
   * @param variableName name of variable
   */
  public void storeLocalInt(String variableName) {
    
    storeLocalInt(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to store a local int variable
   * (<code>istore_&lt;n&gt;</code> or <code>istore</code>).
   * @param index index position of variable
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
   * (<code>lstore_&lt;n&gt;</code> or <code>lstore</code>).
   * @param variableName name of variable
   */
  public void storeLocalLong(String variableName) {
    
    storeLocalLong(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to store a local long variable
   * (<code>lstore_&lt;n&gt;</code> or <code>lstore</code>).
   * @param index index position of variable
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
   * (<code>fstore_&lt;n&gt;</code> or <code>fstore</code>).
   * @param variableName name of variable
   */
  public void storeLocalFloat(String variableName) {
    
    storeLocalFloat(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to store a local float variable
   * (<code>fstore_&lt;n&gt;</code> or <code>fstore</code>).
   * @param index index position of variable
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
   * (<code>dstore_&lt;n&gt;</code> or <code>dstore</code>).
   * @param variableName name of variable
   */
  public void storeLocalDouble(String variableName) {
    
    storeLocalDouble(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to store a local double variable
   * (<code>dstore_&lt;n&gt;</code> or <code>dstore</code>).
   * @param index index position of variable
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
   * (<code>astore_&lt;n&gt;</code> or <code>astore</code>).
   * @param variableName name of variable
   */
  public void storeLocalReference(String variableName) {
    
    storeLocalReference(getLocalVariable(variableName));
  }
  
  /**
   * Opcode to store a local reference variable
   * (<code>astore_&lt;n&gt;</code> or <code>astore</code>).
   * @param index index position of variable
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
   * Opcode <code>iastore</code>.
   */
  public void storeIntArrayElement() {
    
    writeOpCode(0x4F); // iastore
    currentStack -= 3;
  }
  
  /**
   * Opcode <code>lastore</code>.
   */
  public void storeLongArrayElement() {
    
    writeOpCode(0x50); // lastore
    currentStack -= 4;
  }
  
  /**
   * Opcode <code>fastore</code>.
   */
  public void storeFloatArrayElement() {
    
    writeOpCode(0x51); // fastore
    currentStack -= 3;
  }
  
  /**
   * Opcode <code>dastore</code>.
   */
  public void storeDoubleArrayElement() {
    
    writeOpCode(0x52); // dastore
    currentStack -= 4;
  }
  
  /**
   * Opcode <code>aastore</code>.
   */
  public void storeReferenceArrayElement() {
    
    writeOpCode(0x53); // aastore
    currentStack -= 3;
  }
  
  /**
   * Opcode <code>bastore</code> (boolean).
   */
  public void storeBooleanArrayElement() {
    
    writeOpCode(0x54); // bastore
    currentStack -= 3;
  }
  
  /**
   * Opcode <code>bastore</code> (byte).
   */
  public void storeByteArrayElement() {
    
    writeOpCode(0x54); // bastore
    currentStack -= 3;
  }
  
  /**
   * Opcode <code>castore</code>.
   */
  public void storeCharArrayElement() {
    
    writeOpCode(0x55); // castore
    currentStack -= 3;
  }
  
  /**
   * Opcode <code>sastore</code>.
   */
  public void storeShortArrayElement() {
    
    writeOpCode(0x56); // sastore
    currentStack -= 3;
  }
  
  /**
   * Opcode <code>pop</code>.
   */
  public void pop() {
    
    writeOpCode(0x57); // pop
    currentStack--;
  }
  
  /**
   * Opcode <code>pop2</code>.
   */
  public void popDouble() {
    
    writeOpCode(0x58); // pop2
    currentStack -= 2;
  }
  
  /**
   * Opcode to pop <code>popSize</code> bytes
   * (<code>pop</code> or <code>pop2</code> repeatedly).
   * @param popSize number of bytes to pop
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
   * Opcode <code>dup</code>.
   */
  public void duplicate() {
    
    writeOpCode(0x59); // dup
    incrementStack(1);
  }
  
  /**
   * Opcode <code>dup_x1</code>.
   */
  public void duplicateSkip() {
    
    writeOpCode(0x5A); // dup_x1
    incrementStack(1);
  }
  
  /**
   * Opcode <code>dup_x2</code>.
   */
  public void duplicateSkipDouble() {
    
    writeOpCode(0x5B); // dup_x2
    incrementStack(1);
  }
  
  /**
   * Opcode <code>dup2</code>.
   */
  public void duplicateDouble() {
    
    writeOpCode(0x5C); // dup2
    incrementStack(2);
  }
  
  /**
   * Opcode <code>dup2_x1</code>.
   */
  public void duplicateDoubleSkip() {
    
    writeOpCode(0x5D); // dup2_x1
    incrementStack(2);
  }
  
  /**
   * Opcode <code>dup2_x2</code>.
   */
  public void duplicateDoubleSkipDouble() {
    
    writeOpCode(0x5E); // dup2_x2
    incrementStack(2);
  }
  
  /**
   * Opcode <code>swap</code>.
   */
  public void swap() {
    
    writeOpCode(0x5F); // swap
  }
  
  /**
   * Opcode <code>iadd</code>.
   */
  public void addInt() {
    
    writeOpCode(0x60); // iadd
    currentStack--;
  }
  
  /**
   * Opcode <code>ladd</code>.
   */
  public void addLong() {
    
    writeOpCode(0x61); // ladd
    currentStack -= 2;
  }
  
  /**
   * Opcode <code>fadd</code>.
   */
  public void addFloat() {
    
    writeOpCode(0x62); // fadd
    currentStack--;
  }
  
  /**
   * Opcode <code>dadd</code>.
   */
  public void addDouble() {
    
    writeOpCode(0x63); // dadd
    currentStack -= 2;
  }
  
  /**
   * Opcode <code>isub</code>.
   */
  public void subtractInt() {
    
    writeOpCode(0x64); // isub
    currentStack--;
  }
  
  /**
   * Opcode <code>lsub</code>.
   */
  public void subtractLong() {
    
    writeOpCode(0x65); // lsub
    currentStack -= 2;
  }
  
  /**
   * Opcode <code>fsub</code>.
   */
  public void subtractFloat() {
    
    writeOpCode(0x66); // fsub
    currentStack--;
  }
  
  /**
   * Opcode <code>dsub</code>.
   */
  public void subtractDouble() {
    
    writeOpCode(0x67); // dsub
    currentStack -= 2;
  }
  
  /**
   * Opcode <code>imul</code>.
   */
  public void multiplyInt() {
    
    writeOpCode(0x68); // imul
    currentStack--;
  }
  
  /**
   * Opcode <code>lmul</code>.
   */
  public void multiplyLong() {
    
    writeOpCode(0x69); // lmul
    currentStack -= 2;
  }
  
  /**
   * Opcode <code>fmul</code>.
   */
  public void multiplyFloat() {
    
    writeOpCode(0x6A); // fmul
    currentStack--;
  }
  
  /**
   * Opcode <code>dmul</code>.
   */
  public void multiplyDouble() {
    
    writeOpCode(0x6B); // dmul
    currentStack -= 2;
  }
  
  /**
   * Opcode <code>idiv</code>.
   */
  public void divideInt() {
    
    writeOpCode(0x6C); // idiv
    currentStack--;
  }
  
  /**
   * Opcode <code>ldiv</code>.
   */
  public void divideLong() {
    
    writeOpCode(0x6D); // ldiv
    currentStack -= 2;
  }
  
  /**
   * Opcode <code>fdiv</code>.
   */
  public void divideFloat() {
    
    writeOpCode(0x6E); // fdiv
    currentStack--;
  }
  
  /**
   * Opcode <code>ddiv</code>.
   */
  public void divideDouble() {
    
    writeOpCode(0x6F); // ddiv
    currentStack -= 2;
  }
  
  /**
   * Opcode <code>irem</code>.
   */
  public void remainderInt() {
    
    writeOpCode(0x70); // irem
    currentStack--;
  }
  
  /**
   * Opcode <code>lrem</code>.
   */
  public void remainderLong() {
    
    writeOpCode(0x71); // lrem
    currentStack -= 2;
  }
  
  /**
   * Opcode <code>frem</code>.
   */
  public void remainderFloat() {
    
    writeOpCode(0x72); // frem
    currentStack--;
  }
  
  /**
   * Opcode <code>drem</code>.
   */
  public void remainderDouble() {
    
    writeOpCode(0x73); // drem
    currentStack -= 2;
  }
  
  /**
   * Opcode <code>ineg</code>.
   */
  public void negateInt() {
    
    writeOpCode(0x74); // ineg
  }
  
  /**
   * Opcode <code>lneg</code>.
   */
  public void negateLong() {
    
    writeOpCode(0x75); // lneg
  }
  
  /**
   * Opcode <code>fneg</code>.
   */
  public void negateFloat() {
    
    writeOpCode(0x76); // fneg
  }
  
  /**
   * Opcode <code>dneg</code>.
   */
  public void negateDouble() {
    
    writeOpCode(0x77); // dneg
  }
  
  /**
   * Opcode <code>ishl</code>.
   */
  public void shiftLeftInt() {
    
    writeOpCode(0x78); // ishl
    currentStack--;
  }
  
  /**
   * Opcode <code>lshl</code>.
   */
  public void shiftLeftLong() {
    
    writeOpCode(0x79); // lshl
    currentStack--;
  }
  
  /**
   * Opcode <code>ishr</code>.
   */
  public void arithmeticShiftRightInt() {
    
    writeOpCode(0x7A); // ishr
    currentStack--;
  }
  
  /**
   * Opcode <code>lshr</code>.
   */
  public void arithmeticShiftRightLong() {
    
    writeOpCode(0x7B); // lshr
    currentStack--;
  }
  
  /**
   * Opcode <code>iushr</code>.
   */
  public void logicalShiftRightInt() {
    
    writeOpCode(0x7C); // iushr
    currentStack--;
  }
  
  /**
   * Opcode <code>lushr</code>.
   */
  public void logicalShiftRightLong() {
    
    writeOpCode(0x7D); // lushr
    currentStack--;
  }
  
  /**
   * Opcode <code>iand</code>.
   */
  public void booleanAndInt() {
    
    writeOpCode(0x7E); // iand
    currentStack--;
  }
  
  /**
   * Opcode <code>land</code>.
   */
  public void booleanAndLong() {
    
    writeOpCode(0x7F); // land
    currentStack -= 2;
  }
  
  /**
   * Opcode <code>ior</code>.
   */
  public void booleanOrInt() {
    
    writeOpCode(0x80); // ior
    currentStack--;
  }
  
  /**
   * Opcode <code>lor</code>.
   */
  public void booleanOrLong() {
    
    writeOpCode(0x81); // lor
    currentStack -= 2;
  }
  
  /**
   * Opcode <code>ixor</code>.
   */
  public void booleanXorInt() {
    
    writeOpCode(0x82); // ixor
    currentStack--;
  }
  
  /**
   * Opcode <code>lxor</code>.
   */
  public void booleanXorLong() {
    
    writeOpCode(0x83); // lxor
    currentStack -= 2;
  }
  
  /**
   * Opcode to increment a local int variable by name
   * (<code>iinc</code> or a combination of <code>load</code>, <code>add</code> and <code>store</code>).
   * @param variableName name of variable
   * @param increment increment value
   */
  public void incrementLocalInt(String variableName, int increment) {
    
    incrementLocalInt(getLocalVariable(variableName), increment);
  }
  
  /**
   * Opcode to increment a local int variable
   * (<code>iinc</code> or a combination of <code>load</code>, <code>add</code> and <code>store</code>).
   * @param index index position of variable
   * @param increment increment value
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
   * Opcode <code>i2l</code>.
   */
  public void convertIntToLong() {
    
    writeOpCode(0x85); // i2l
    incrementStack(1);
  }
  
  /**
   * Opcode <code>i2f</code>.
   */
  public void convertIntToFloat() {
    
    writeOpCode(0x86); // i2f
  }
  
  /**
   * Opcode <code>i2d</code>.
   */
  public void convertIntToDouble() {
    
    writeOpCode(0x87); // i2d
    incrementStack(1);
  }
  
  /**
   * Opcode <code>l2i</code>.
   */
  public void convertLongToInt() {
    
    writeOpCode(0x88); // l2i
    currentStack--;
  }
  
  /**
   * Opcode <code>l2f</code>.
   */
  public void convertLongToFloat() {
    
    writeOpCode(0x89); // l2f
    currentStack--;
  }
  
  /**
   * Opcode <code>l2d</code>.
   */
  public void convertLongToDouble() {
    
    writeOpCode(0x8A); // l2d
  }
  
  /**
   * Opcode <code>f2i</code>.
   */
  public void convertFloatToInt() {
    
    writeOpCode(0x8B); // f2i
  }
  
  /**
   * Opcode <code>f2l</code>.
   */
  public void convertFloatToLong() {
    
    writeOpCode(0x8C); // f2l
    incrementStack(1);
  }
  
  /**
   * Opcode <code>f2d</code>.
   */
  public void convertFloatToDouble() {
    
    writeOpCode(0x8D); // f2d
    incrementStack(1);
  }
  
  /**
   * Opcode <code>d2i</code>.
   */
  public void convertDoubleToInt() {
    
    writeOpCode(0x8E); // d2i
    currentStack--;
  }
  
  /**
   * Opcode <code>d2l</code>.
   */
  public void convertDoubleToLong() {
    
    writeOpCode(0x8F); // d2l
  }
  
  /**
   * Opcode <code>d2f</code>.
   */
  public void convertDoubleToFloat() {
    
    writeOpCode(0x90); // d2f
    currentStack--;
  }
  
  /**
   * Opcode <code>i2b</code>.
   */
  public void convertIntToByte() {
    
    writeOpCode(0x91); // i2b
  }
  
  /**
   * Opcode <code>i2c</code>.
   */
  public void convertIntToChar() {
    
    writeOpCode(0x92); // i2c
  }
  
  /**
   * Opcode <code>i2s</code>.
   */
  public void convertIntToShort() {
    
    writeOpCode(0x93); // i2s
  }
  
  /**
   * Opcode <code>lcmp</code>.
   */
  public void compareLong() {
    
    writeOpCode(0x94); // lcmp
    currentStack -= 3;
  }

  /**
   * Opcode <code>fcmpg</code> or <code>fcmpl</code>.
   * @param nanIsMinus flag wether NaN is negative
   */
  public void compareFloat(boolean nanIsMinus) {
    
    writeOpCode(nanIsMinus ? 0x95 : 0x96); // fcmpg : fcmpl
    currentStack--;
  }
  
  /**
   * Opcode <code>dcmpg</code> or <code>dcmpl</code>.
   * @param nanIsMinus flag wether NaN is negative
   */
  public void compareDouble(boolean nanIsMinus) {
    
    writeOpCode(nanIsMinus ? 0x97 : 0x98); // dcmpg : dcmpl
    currentStack -= 3;
  }
  
  /**
   * Opcode <code>ifeq</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void branchIfEqual(String labelName) {
    
    writeOpCode(0x99); // ifeq
    currentStack--;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <code>ifne</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void branchIfNotEqual(String labelName) {
    
    writeOpCode(0x9A); // ifne
    currentStack--;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <code>iflt</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void branchIfLess(String labelName) {
    
    writeOpCode(0x9B); // iflt
    currentStack--;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <code>ifge</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void branchIfGreaterEqual(String labelName) {
    
    writeOpCode(0x9C); // ifge
    currentStack--;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <code>ifgt</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void branchIfGreater(String labelName) {
    
    writeOpCode(0x9D); // ifgt
    currentStack--;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <code>ifle</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void branchIfLessEqual(String labelName) {
    
    writeOpCode(0x9E); // ifle
    currentStack--;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <code>if_icmpeq</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void compareIntBranchIfEqual(String labelName) {
    
    writeOpCode(0x9F); // if_icmpeq
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <code>if_icmpne</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void compareIntBranchIfNotEqual(String labelName) {
    
    writeOpCode(0xA0); // if_icmpne
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <code>if_icmplt</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void compareIntBranchIfLess(String labelName) {
    
    writeOpCode(0xA1); // if_icmplt
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <code>if_icmpge</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void compareIntBranchIfGreaterEqual(String labelName) {
    
    writeOpCode(0xA2); // if_icmpge
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <code>if_icmpgt</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void compareIntBranchIfGreater(String labelName) {
    
    writeOpCode(0xA3); // if_icmpgt
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <code>if_icmple</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void compareIntBranchIfLessEqual(String labelName) {
    
    writeOpCode(0xA4); // if_icmple
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <code>if_acmpeq</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void compareReferenceBranchIfEqual(String labelName) {
    
    writeOpCode(0xA5); // if_acmpeq
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <code>if_acmpne</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void compareReferenceBranchIfNotEqual(String labelName) {
    
    writeOpCode(0xA6); // if_acmpne
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  /**
   * Opcode <code>goto</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void branch(String labelName) {
    
    writeOpCode(0xA7); // goto
    createJump(2, labelName);
    currentStack = 0;
  }
  
  /**
   * Opcode <code>jsr</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName name of label to branch to
   */
  public void jumpSubroutine(String labelName) {
    
    writeOpCode(0xA8); // jsr
    incrementStack(1);
    createJump(2, labelName);
  }
  
  /**
   * Opcode <code>ret</code> (address variable by name).
   * @param variableName name of variable
   */
  public void returnFromSubroutine(String variableName) {
    
    returnFromSubroutine(getLocalVariable(variableName));
  }
  
  /**
   * Opcode <code>ret</code>.
   * @param index index position of variable
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
   * Opcode <code>tableswitch</code>.
   * @param table branch table with case entries
   */
  public void tableswitch(BranchTable table) {
    
    codeSwitch(0xAA, table); // tableswitch
  }
  
  /**
   * Opcode <code>lookupswitch</code>.
   * @param table branch table with case entries
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
   * Opcode <code>ireturn</code>.
   */
  public void returnInt() {
    
    writeOpCode(0xAC); // ireturn
    currentStack = 0;
  }
  
  /**
   * Opcode <code>lreturn</code>.
   */
  public void returnLong() {
    
    writeOpCode(0xAD); // lreturn
    currentStack = 0;
  }
  
  /**
   * Opcode <code>freturn</code>.
   */
  public void returnFloat() {
    
    writeOpCode(0xAE); // freturn
    currentStack = 0;
  }
  
  /**
   * Opcode <code>dreturn</code>.
   */
  public void returnDouble() {
    
    writeOpCode(0xAF); // dreturn
    currentStack = 0;
  }
  
  /**
   * Opcode <code>areturn</code>.
   */
  public void returnReference() {
    
    writeOpCode(0xB0); // areturn
    currentStack = 0;
  }
  
  /**
   * Opcode <code>return</code>.
   */
  public void returnVoid() {
    
    writeOpCode(0xB1); // return
    currentStack = 0;
  }
  
  /**
   * Opcode <code>getstatic</code>.
   * @param fieldClass member class of field
   * @param fieldType type of field
   * @param fieldName name of field
   */
  public void getStatic(Type fieldClass, Type fieldType, String fieldName) {
    
    writeOpCode(0xB2); // getstatic
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
    incrementStack(1);
  }
  
  /**
   * Opcode <code>putstatic</code>.
   * @param fieldClass member class of field
   * @param fieldType type of field
   * @param fieldName name of field
   */
  public void putStatic(Type fieldClass, Type fieldType, String fieldName) {

    writeOpCode(0xB3); // putstatic
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
    currentStack--;
  }
  
  /**
   * Opcode <code>getfield</code>.
   * @param fieldClass member class of field
   * @param fieldType type of field
   * @param fieldName name of field
   */
  public void getField(Type fieldClass, Type fieldType, String fieldName) {
    
    writeOpCode(0xB4); // getfield
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
  }
  
  /**
   * Opcode <code>putfield</code>.
   * @param fieldClass member class of field
   * @param fieldType type of field
   * @param fieldName name of field
   */
  public void putField(Type fieldClass, Type fieldType, String fieldName) {

    writeOpCode(0xB5); // putfield
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
    currentStack -= 2;
  }
  
  /**
   * Opcode <code>invokevirtual</code>.
   * @param methodClass member class of method
   * @param returnType type of return value
   * @param methodName method name
   * @param argTypes types of arguments
   */
  public void invokeVirtual(Type methodClass, Type returnType, String methodName, Type... argTypes) {
    
    invoke(0xB6, 1, methodClass, returnType, methodName, argTypes); // invokevirtual
  }
  
  /**
   * Opcode <code>invokespecial</code>.
   * @param methodClass member class of method
   * @param returnType type of return value
   * @param methodName method name
   * @param argTypes types of arguments
   */
  public void invokeSpecial(Type methodClass, Type returnType, String methodName, Type... argTypes) {
    
    invoke(0xB7, 1, methodClass, returnType, methodName, argTypes); // invokespecial
  }
  
  /**
   * Opcode <code>invokestatic</code>.
   * @param methodClass member class of method
   * @param returnType type of return value
   * @param methodName method name
   * @param argTypes types of arguments
   */
  public void invokeStatic(Type methodClass, Type returnType, String methodName, Type... argTypes) {
    
    invoke(0xB8, 0, methodClass, returnType, methodName, argTypes); // invokestatic
  }
  
  /**
   * Opcode <code>invokeinterface</code>.
   * @param methodClass interface class of method
   * @param returnType type of return value
   * @param methodName method name
   * @param argTypes types of arguments
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
   * Opcode <code>new</code>.
   * @param clazz class type
   */
  public void newObject(Type clazz) {
    
    writeOpCode(0xBB); // new
    write2(constantPool.addClass(clazz));
    incrementStack(1);
  }
  
  /**
   * Opcode <code>newarray</code> or <code>anewarray</code>.
   * @param elementType array element type
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
   * Opcode <code>arraylength</code>.
   */
  public void arraylength() {
    
    writeOpCode(0xBE); // arraylength
  }
  
  /**
   * Opcode <code>athrow</code>.
   */
  public void throwException() {

    writeOpCode(0xBF); // athrow
    currentStack = 0;
    incrementStack(1);
  }
  
  /**
   * Opcode <code>checkcast</code>.
   * @param checkedType type to cast
   */
  public void cast(Type checkedType) {
    
    writeOpCode(0xC0); // checkcast
    write2(constantPool.addClass(checkedType));
  }
  
  /**
   * Opcode <code>instanceof</code>.
   * @param checkedType type of instance
   */
  public void checkInstance(Type checkedType) {
    
    writeOpCode(0xC1); // instanceof
    write2(constantPool.addClass(checkedType));
  }
  
  /**
   * Opcode <code>monitorenter</code>.
   */
  public void monitorEnter() {
    
    writeOpCode(0xC2); // monitorenter
    currentStack--;
  }
  
  /**
   * Opcode <code>monitorexit</code>.
   */
  public void monitorExit() {
    
    writeOpCode(0xC3); // monitorexit
    currentStack--;
  }
  
  /**
   * Opcode <code>multianewarray</code>.
   * @param clazz element class type
   * @param dimensions number of dimensions
   */
  public void newMultiReferenceArray(Type clazz, int dimensions) {
    
    writeOpCode(0xC5); // multianewarray
    write2(constantPool.addClass(clazz));
    write1(dimensions);
    currentStack -= (dimensions - 1);
  }
  
  /**
   * Opcode <code>ifnull</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName label name to branch to
   */
  public void branchIfNull(String labelName) {
    
    writeOpCode(0xC6); // ifnull
    currentStack--;
    createJump(2, labelName);
  }
    
  /**
   * Opcode <code>ifnonnull</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName label name to branch to
   */
  public void branchIfNonNull(String labelName) {
    
    writeOpCode(0xC7); // ifnonnull
    currentStack--;
    createJump(2, labelName);
  }

  /**
   * Opcode <code>goto_w</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName label name to branch to
   */
  public void branchFar(String labelName) {
    
    writeOpCode(0xC8); // goto_w
    createJump(4, labelName);
    currentStack = 0;
  }
  
  /**
   * Opcode <code>jsr_w</code>.
   * The label must be defined by {@link #defineLabel}.
   * @param labelName label name to branch to
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
