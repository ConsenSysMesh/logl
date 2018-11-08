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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NameAbbreviatorTest {

  @ParameterizedTest
  @MethodSource("abbreviationsProvider")
  void testAbbreviatorPatterns(String pattern, String name, String expected) throws Exception {
    NameAbbreviator abbreviator = NameAbbreviator.forPattern(pattern);
    String prefix = "class: ";
    StringBuilder destination = new StringBuilder(prefix);
    abbreviator.writeTo(name, destination);
    assertThat(destination.toString()).isEqualTo(prefix + expected);
    assertThat(abbreviator.abbreviate(name)).isEqualTo(expected);
  }

  private static Stream<Arguments> abbreviationsProvider() {
    return Stream.of(
        Arguments.of("", "org.logl.NameAbbreviator", "org.logl.NameAbbreviator"),
        Arguments.of("1", "org.logl.NameAbbreviator", "NameAbbreviator"),
        Arguments.of("2", "org.logl.NameAbbreviator", "logl.NameAbbreviator"),
        Arguments.of("3", "org.logl.NameAbbreviator", "org.logl.NameAbbreviator"),
        Arguments.of("10", "org.logl.NameAbbreviator", "org.logl.NameAbbreviator"),
        Arguments.of("2147483647", "org.logl.NameAbbreviator", "org.logl.NameAbbreviator"),
        Arguments.of("1", "org.logl.NameAbbreviator.", ""),
        Arguments.of("5", "", ""),
        Arguments.of("-1", "org.logl.NameAbbreviator", "logl.NameAbbreviator"),
        Arguments.of("-2", "org.logl.NameAbbreviator", "NameAbbreviator"),
        Arguments.of("-3", "org.logl.NameAbbreviator", "NameAbbreviator"),
        Arguments.of("-3", "", ""),
        Arguments.of("1.", "org.logl.NameAbbreviator", "o.l.NameAbbreviator"),
        Arguments.of("2.", "org.logl.NameAbbreviator", "or.lo.NameAbbreviator"),
        Arguments.of("3.", "org.logl.NameAbbreviator", "org.log.NameAbbreviator"),
        Arguments.of("4.", "org.logl.NameAbbreviator", "org.logl.NameAbbreviator"),
        Arguments.of("*.", "org.logl.NameAbbreviator", "org.logl.NameAbbreviator"),
        Arguments.of("-.", "org.logl.NameAbbreviator", "-.-.NameAbbreviator"),
        Arguments.of("--.", "org.logl.NameAbbreviator", "--.--.NameAbbreviator"),
        Arguments.of("-", "org.logl.NameAbbreviator", "-.-.NameAbbreviator"),
        Arguments.of("1~.", "org.logl.NameAbbreviator", "o~.l~.NameAbbreviator"),
        Arguments.of("2~.", "org.logl.NameAbbreviator", "or~.lo~.NameAbbreviator"),
        Arguments.of("3~.", "org.logl.NameAbbreviator", "org.log~.NameAbbreviator"),
        Arguments.of("4~.", "org.logl.NameAbbreviator", "org.logl.NameAbbreviator"),
        Arguments.of("2147483647~.", "org.logl.NameAbbreviator", "org.logl.NameAbbreviator"),
        Arguments.of("*~.", "org.logl.NameAbbreviator", "org.logl.NameAbbreviator"),
        Arguments.of("1~.2-", "org.logl.NameAbbreviator", "o~.lo-.NameAbbreviator"),
        Arguments.of("1~~.2-", "org.logl.test.NameAbbreviator", "o~~.lo-.te-.NameAbbreviator"),
        Arguments.of("1~~.2", "org.logl.test.NameAbbreviator", "o~~.lo.te.NameAbbreviator"),
        Arguments.of("*.1.2-", "org.logl.test.NameAbbreviator", "org.l.te-.NameAbbreviator"),
        Arguments.of("1~.2-.3+.4&", "org.logl.NameAbbreviator", "o~.lo-.NameAbbreviator"));
  }

  @Test
  void zeroAbbreviatorShouldBeInvalid() {
    Throwable exception = assertThrows(IllegalArgumentException.class, () -> NameAbbreviator.forPattern("0"));
    assertThat(exception.getMessage()).isEqualTo("Abbreviation of 0 would output nothing");
  }

  @Test
  void overflowingSingleCountShouldBeInvalid() {
    Throwable exception = assertThrows(IllegalArgumentException.class, () -> NameAbbreviator.forPattern("2147483648"));
    assertThat(exception.getMessage()).isEqualTo("Abbreviation count is larger than an integer");
  }

  @Test
  void overflowingFragmentCountShouldBeInvalid() {
    Throwable exception =
        assertThrows(IllegalArgumentException.class, () -> NameAbbreviator.forPattern("-.2147483648.-"));
    assertThat(exception.getMessage()).isEqualTo("Abbreviation count is larger than an integer");
  }

  @Test
  void nonSymbolFragmentReplacementShouldBeInvalid() {
    Throwable exception = assertThrows(IllegalArgumentException.class, () -> NameAbbreviator.forPattern("1a."));
    assertThat(exception.getMessage()).isEqualTo("Abbreviation fragment contains non-symbol replacement 'a'");
    exception = assertThrows(IllegalArgumentException.class, () -> NameAbbreviator.forPattern("1 ."));
    assertThat(exception.getMessage()).isEqualTo("Abbreviation fragment contains non-symbol replacement ' '");
  }
}
