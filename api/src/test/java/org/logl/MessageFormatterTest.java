/*
 * Copyright 2018 ConsenSys AG.
 *
 * This code is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files(the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and / or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.logl;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

abstract class MessageFormatterTest {

  private Supplier<Appendable> appendableSupplier;
  private Appendable appendable;

  MessageFormatterTest(Supplier<Appendable> appendableSupplier) {
    this.appendableSupplier = appendableSupplier;
  }

  @BeforeEach
  void setUp() {
    appendable = appendableSupplier.get();
  }

  @Test
  void formatMessageWithNoArgs() throws IOException {
    MessageFormatter.formatTo("Hello world", null, appendable);
    assertEquals("Hello world", appendable.toString());
  }

  @Test
  void formatMessageWithMissingArg() throws IOException {
    MessageFormatter.formatTo("Hello {}", null, appendable);
    assertEquals("Hello {}", appendable.toString());
  }

  @Test
  void formatMessageWithOneArg() throws IOException {
    MessageFormatter.formatTo("Hello {}", new Object[] {"world"}, appendable);
    assertEquals("Hello world", appendable.toString());
  }

  @Test
  void formatMessageWithByteArrayArg() throws IOException {
    MessageFormatter.formatTo("Hello {}", new Object[] {"world".getBytes(UTF_8)}, appendable);
    assertEquals("Hello [119, 111, 114, 108, 100]", appendable.toString());
  }

  @Test
  void formatMessageWithEmptyByteArrayArg() throws IOException {
    MessageFormatter.formatTo("Hello {}", new Object[] {new byte[0]}, appendable);
    assertEquals("Hello []", appendable.toString());
  }

  @Test
  void formatMessageWithOneNumericArg() throws IOException {
    MessageFormatter.formatTo("Hello {}", new Object[] {123}, appendable);
    assertEquals("Hello 123", appendable.toString());
  }

  @Test
  void formatMessageWithBooleanArrayArg() throws IOException {
    MessageFormatter.formatTo("Hello {}", new Object[] {new boolean[] {false, true}}, appendable);
    assertEquals("Hello [false, true]", appendable.toString());
  }

  @Test
  void formatMessageWithFloatArrayArg() throws IOException {
    MessageFormatter.formatTo("Hello {}", new Object[] {new double[] {12.1, 34.5}}, appendable);
    assertEquals("Hello [12.1, 34.5]", appendable.toString());
  }

  @Test
  void formatMessageWithIntArrayArg() throws IOException {
    MessageFormatter.formatTo("Hello {}", new Object[] {new int[] {12, 34}}, appendable);
    assertEquals("Hello [12, 34]", appendable.toString());
  }

  @Test
  void formatMessageWithCharArrayArg() throws IOException {
    MessageFormatter.formatTo("Hello {}", new Object[] {new char[] {'a', 'b'}}, appendable);
    assertEquals("Hello [a, b]", appendable.toString());
  }

  @Test
  void formatMessageWithShortArg() throws IOException {
    MessageFormatter.formatTo("Hello {}", new Object[] {new short[] {1, 2, 3, 4}}, appendable);
    assertEquals("Hello [1, 2, 3, 4]", appendable.toString());
  }

  @Test
  void formatMessageWithLongArg() throws IOException {
    MessageFormatter.formatTo("Hello {}", new Object[] {new long[] {1, 2, 3, 4}}, appendable);
    assertEquals("Hello [1, 2, 3, 4]", appendable.toString());
  }

  @Test
  void formatMessageWithFloatArg() throws IOException {
    MessageFormatter.formatTo("Hello {}", new Object[] {new float[] {1, 2, 3.3f, 4}}, appendable);
    assertEquals("Hello [1.0, 2.0, 3.3, 4.0]", appendable.toString());
  }

  @Test
  void formatMessageWithObjectArrayArg() throws IOException {
    MessageFormatter.formatTo("Hello {}", new Object[] {new Object[] {"world", 34, new byte[] {123}}}, appendable);
    assertEquals("Hello [world, 34, [123]]", appendable.toString());
  }
}


class StringWriterMessageFormatterTest extends MessageFormatterTest {

  StringWriterMessageFormatterTest() {
    super(StringWriter::new);
  }
}


class StringBuilderMessageFormatterTest extends MessageFormatterTest {

  StringBuilderMessageFormatterTest() {
    super(StringBuilder::new);
  }
}
