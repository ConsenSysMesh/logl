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
package org.logl.logl;

import java.io.PrintWriter;
import java.time.ZonedDateTime;

final class DateFormatter {
  private DateFormatter() {}

  private static final String COMMON_YEARS[] = new String[200];
  static {
    for (int i = 0; i < 200; ++i) {
      COMMON_YEARS[i] = Integer.toString(i + 1900);
    }
  }

  private static final String TWO_DIGIT_INTS[] = new String[61];
  static {
    for (int i = 0; i <= 60; ++i) {
      TWO_DIGIT_INTS[i] = String.format("%02d", i);
    }
  }

  private static final String THREE_DIGIT_INTS[] = new String[1000];
  static {
    for (int i = 0; i < 1000; ++i) {
      THREE_DIGIT_INTS[i] = String.format("%03d", i);
    }
  }

  static void formatTo(ZonedDateTime dateTime, PrintWriter out) {
    int year = dateTime.getYear();
    if (year >= 1900 && year < 2100) {
      out.write(COMMON_YEARS[year - 1900]);
    } else {
      String ys = Integer.toString(year % 10000);
      out.write("   ", 0, 4 - ys.length());
      out.write(ys);
    }
    out.write('-');
    out.write(TWO_DIGIT_INTS[dateTime.getMonth().getValue()]);
    out.write('-');
    out.write(TWO_DIGIT_INTS[dateTime.getDayOfMonth()]);
    out.write(' ');
    out.write(TWO_DIGIT_INTS[dateTime.getHour()]);
    out.write(':');
    out.write(TWO_DIGIT_INTS[dateTime.getMinute()]);
    out.write(':');
    out.write(TWO_DIGIT_INTS[dateTime.getSecond()]);
    out.write('.');
    int millis = dateTime.getNano() / 1000000;
    out.write(THREE_DIGIT_INTS[millis]);
    int offsetSeconds = dateTime.getOffset().getTotalSeconds();
    if (offsetSeconds < 0) {
      out.write('-');
      offsetSeconds = -offsetSeconds;
    } else {
      out.write('+');
    }
    int offsetMinutes = offsetSeconds / 60;
    int offsetHours = offsetMinutes / 60;
    out.write(TWO_DIGIT_INTS[offsetHours]);
    out.write(TWO_DIGIT_INTS[offsetMinutes % 60]);
  }
}
