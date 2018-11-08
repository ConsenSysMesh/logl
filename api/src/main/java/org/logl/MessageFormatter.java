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

// Based on SLF4J implementation, licensed under MIT license and available at
// https://github.com/qos-ch/slf4j/blob/343e0a8ef48793a42685fc0c69e75a18a2c01e91/slf4j-api/src/main/java/org/slf4j/helpers/MessageFormatter.java

/*
 * Copyright (c) 2004-2011 QOS.ch All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS  IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

final class MessageFormatter {
  private static final char DELIM_START = '{';
  private static final String DELIM_STR = "{}";
  private static final char ESCAPE_CHAR = '\\';

  static void formatTo(String pattern, Object[] args, Appendable appendable) throws IOException {
    if (args == null || args.length == 0) {
      appendable.append(pattern);
      return;
    }

    int patternLength = pattern.length();

    int n = 0;
    int i = 0;
    while (n < args.length && i < patternLength) {
      int j = pattern.indexOf(DELIM_STR, i);
      if (j < 0) {
        break;
      }

      if (j > 0 && pattern.charAt(j - 1) == ESCAPE_CHAR) {
        appendable.append(pattern, i, j - 1);
        if (j <= 1 || pattern.charAt(j - 2) != ESCAPE_CHAR) {
          // escaped delimiter
          appendable.append(pattern, i, j - 1);
          appendable.append(DELIM_START);
          i = j + 1;
          continue;
        }
      } else {
        appendable.append(pattern, i, j);
      }

      appendParameter(appendable, args[n++], null);
      i = j + 2;
    }
    if (i < patternLength) {
      appendable.append(pattern, i, patternLength);
    }
  }

  private static void appendParameter(Appendable appendable, Object parameter, Set<Object[]> seen) throws IOException {
    if (parameter == null) {
      appendable.append("null");
      return;
    }

    if (!parameter.getClass().isArray()) {
      String paramString;
      try {
        paramString = parameter.toString();
      } catch (Exception e) {
        paramString = "[FAILED toString()]";
      }
      appendable.append(paramString);
      return;
    }

    // check for primitive array types because they
    // unfortunately cannot be cast to Object[]
    if (parameter instanceof boolean[]) {
      appendBooleanArray(appendable, (boolean[]) parameter);
    } else if (parameter instanceof byte[]) {
      appendByteArray(appendable, (byte[]) parameter);
    } else if (parameter instanceof char[]) {
      appendCharArray(appendable, (char[]) parameter);
    } else if (parameter instanceof short[]) {
      appendShortArray(appendable, (short[]) parameter);
    } else if (parameter instanceof int[]) {
      appendIntArray(appendable, (int[]) parameter);
    } else if (parameter instanceof long[]) {
      appendLongArray(appendable, (long[]) parameter);
    } else if (parameter instanceof float[]) {
      appendFloatArray(appendable, (float[]) parameter);
    } else if (parameter instanceof double[]) {
      appendDoubleArray(appendable, (double[]) parameter);
    } else {
      appendObjectArray(appendable, (Object[]) parameter, seen);
    }
  }

  private static void appendBooleanArray(Appendable appendable, boolean[] a) throws IOException {
    if (a.length == 0) {
      appendable.append("[]");
      return;
    }
    appendable.append('[');
    int l = a.length - 1;
    for (int i = 0; i < l; ++i) {
      appendable.append(a[i] ? "true, " : "false, ");
    }
    appendable.append(a[l] ? "true]" : "false]");
  }

  private static void appendByteArray(Appendable appendable, byte[] a) throws IOException {
    if (a.length == 0) {
      appendable.append("[]");
      return;
    }
    appendable.append('[');
    int l = a.length - 1;
    if (appendable instanceof StringBuilder) {
      StringBuilder builder = (StringBuilder) appendable;
      for (int i = 0; i < l; ++i) {
        builder.append(a[i]);
        builder.append(", ");
      }
      builder.append(a[l]);
    } else {
      for (int i = 0; i < l; ++i) {
        appendable.append(String.valueOf(a[i]));
        appendable.append(", ");
      }
      appendable.append(String.valueOf(a[l]));
    }
    appendable.append(']');
  }

  private static void appendCharArray(Appendable appendable, char[] a) throws IOException {
    if (a.length == 0) {
      appendable.append("[]");
      return;
    }
    appendable.append('[');
    int l = a.length - 1;
    for (int i = 0; i < l; i++) {
      appendable.append(a[i]);
      appendable.append(", ");
    }
    appendable.append(a[l]);
    appendable.append(']');
  }

  private static void appendShortArray(Appendable appendable, short[] a) throws IOException {
    if (a.length == 0) {
      appendable.append("[]");
      return;
    }
    appendable.append('[');
    int l = a.length - 1;
    if (appendable instanceof StringBuilder) {
      StringBuilder builder = (StringBuilder) appendable;
      for (int i = 0; i < l; ++i) {
        builder.append(a[i]);
        builder.append(", ");
      }
      builder.append(a[l]);
    } else {
      for (int i = 0; i < l; ++i) {
        appendable.append(String.valueOf(a[i]));
        appendable.append(", ");
      }
      appendable.append(String.valueOf(a[l]));
    }
    appendable.append(']');
  }

  private static void appendIntArray(Appendable appendable, int[] a) throws IOException {
    if (a.length == 0) {
      appendable.append("[]");
      return;
    }
    appendable.append('[');
    int l = a.length - 1;
    if (appendable instanceof StringBuilder) {
      StringBuilder builder = (StringBuilder) appendable;
      for (int i = 0; i < l; ++i) {
        builder.append(a[i]);
        builder.append(", ");
      }
      builder.append(a[l]);
    } else {
      for (int i = 0; i < l; ++i) {
        appendable.append(String.valueOf(a[i]));
        appendable.append(", ");
      }
      appendable.append(String.valueOf(a[l]));
    }
    appendable.append(']');
  }

  private static void appendLongArray(Appendable appendable, long[] a) throws IOException {
    if (a.length == 0) {
      appendable.append("[]");
      return;
    }
    appendable.append('[');
    int l = a.length - 1;
    if (appendable instanceof StringBuilder) {
      StringBuilder builder = (StringBuilder) appendable;
      for (int i = 0; i < l; ++i) {
        builder.append(a[i]);
        builder.append(", ");
      }
      builder.append(a[l]);
    } else {
      for (int i = 0; i < l; ++i) {
        appendable.append(String.valueOf(a[i]));
        appendable.append(", ");
      }
      appendable.append(String.valueOf(a[l]));
    }
    appendable.append(']');
  }

  private static void appendFloatArray(Appendable appendable, float[] a) throws IOException {
    if (a.length == 0) {
      appendable.append("[]");
      return;
    }
    appendable.append('[');
    int l = a.length - 1;
    if (appendable instanceof StringBuilder) {
      StringBuilder builder = (StringBuilder) appendable;
      for (int i = 0; i < l; ++i) {
        builder.append(a[i]);
        builder.append(", ");
      }
      builder.append(a[l]);
    } else {
      for (int i = 0; i < l; ++i) {
        appendable.append(String.valueOf(a[i]));
        appendable.append(", ");
      }
      appendable.append(String.valueOf(a[l]));
    }
    appendable.append(']');
  }

  private static void appendDoubleArray(Appendable appendable, double[] a) throws IOException {
    if (a.length == 0) {
      appendable.append("[]");
      return;
    }
    appendable.append('[');
    int l = a.length - 1;
    if (appendable instanceof StringBuilder) {
      StringBuilder builder = (StringBuilder) appendable;
      for (int i = 0; i < l; ++i) {
        builder.append(a[i]);
        builder.append(", ");
      }
      builder.append(a[l]);
    } else {
      for (int i = 0; i < l; ++i) {
        appendable.append(String.valueOf(a[i]));
        appendable.append(", ");
      }
      appendable.append(String.valueOf(a[l]));
    }
    appendable.append(']');
  }

  @SuppressWarnings("ArrayAsKeyOfSetOrMap")
  private static void appendObjectArray(Appendable appendable, Object[] a, Set<Object[]> seen) throws IOException {
    if (a.length == 0) {
      appendable.append("[]");
      return;
    }
    appendable.append('[');
    if (seen != null && seen.contains(a)) {
      appendable.append("...]");
      return;
    }

    if (seen == null) {
      seen = new HashSet<>();
      seen.add(a);
    } else if (seen.contains(a)) {
      appendable.append("...]");
      return;
    }

    int l = a.length - 1;
    for (int i = 0; i < l; i++) {
      appendParameter(appendable, a[i], seen);
      appendable.append(", ");
    }
    appendParameter(appendable, a[l], seen);
    appendable.append(']');
  }
}
