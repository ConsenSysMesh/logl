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

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Formatter;
import java.util.Locale;

/**
 * A log message.
 */
@FunctionalInterface
public interface LogMessage {

  /**
   * Construct a {@link LogMessage} for a formatted {@link String}.
   *
   * <p>
   * The formatting of the message will be deferred until the message is appended to a log, so all arguments should
   * remain constant while the log is being written.
   *
   * @param format A format string, as per {@link String#format(String, Object...)}.
   * @param args The arguments for the format.
   * @return A {@link LogMessage} instance.
   */
  static LogMessage stringFormat(String format, Object... args) {
    requireNonNull(format);
    return (l, a) -> {
      if (a instanceof PrintWriter) {
        ((PrintWriter) a).format(l, format, args);
        return;
      }
      new Formatter(a).format(l, format, args);
    };
  }

  /**
   * Construct a {@link LogMessage} using a {@link MessageFormat}.
   *
   * <p>
   * The formatting of the message will be deferred until the message is appended to a log, so all arguments should
   * remain constant while the log is being written.
   *
   * @param pattern A pattern string, as per {@link MessageFormat#format(String, Object...)}.
   * @param args The arguments for the pattern.
   * @return A {@link LogMessage} instance.
   */
  static LogMessage messageFormat(String pattern, Object... args) {
    requireNonNull(pattern);
    return (l, a) -> {
      MessageFormat messageFormat = new MessageFormat(pattern, l);
      if (a instanceof StringBuffer) {
        messageFormat.format(args, (StringBuffer) a, null);
      } else {
        a.append(messageFormat.format(args));
      }
    };
  }

  /**
   * Construct a {@link LogMessage} using a pattern format.
   *
   * <p>
   * The formatting of the message will be deferred until the message is appended to a log, so all arguments should
   * remain constant while the log is being written.
   *
   * <p>
   * Formats messages according to very simple substitution rules, and is typically much faster than the other
   * formatting methods. For example,
   *
   * <pre>
   * LogMessage.patternFormat(&quot;Hi {}.&quot;, &quot;there&quot;)
   * </pre>
   *
   * will log the string "Hi there.".
   *
   * <p>
   * The {} pair is serves to designate the location where an argument need to be substituted within the message
   * pattern.
   *
   * <p>
   * In case your message contains the '{' or the '}' character, you do not have to do anything special unless the '}'
   * character immediately follows '{'. For example,
   *
   * <pre>
   * LogMessage.patternFormat(&quot;Set {1,2,3} is not equal to {}.&quot;, &quot;1,2&quot;);
   * </pre>
   *
   * will return the string "Set {1,2,3} is not equal to 1,2.".
   *
   * <p>
   * If you need to place a literal "{}" in the message, then you need to escape the '{' character with a backslash
   * character, '\'. Only the '{' character should be escaped. There is no need to escape the '}' character. For
   * example,
   *
   * <pre>
   * LogMessage.patternFormat(&quot;Set \\{} is not equal to {}.&quot;, &quot;1,2&quot;);
   * </pre>
   *
   * will return the string "Set {} is not equal to 1,2.".
   *
   * <p>
   * The escape character can itself be escaped. For example,
   *
   * <pre>
   * LogMessage.patternFormat(&quot;File name is C:\\\\{}.&quot;, &quot;file.zip&quot;);
   * </pre>
   *
   * will return the string "File name is C:\file.zip".
   *
   * @param pattern A pattern string.
   * @param args The arguments for the pattern.
   * @return A {@link LogMessage} instance.
   */
  static LogMessage patternFormat(String pattern, Object... args) {
    requireNonNull(pattern);
    return (l, a) -> MessageFormatter.formatTo(pattern, args, a);
  }

  /**
   * Append the log message to the given {@link Appendable}.
   *
   * @param locale The {@link Locale} that should be used if the message requires localization.
   * @param appendable The {@link Appendable} the message should be appended to.
   * @throws IOException If any of the appendable methods throw an {@code IOException} then it will be propagated.
   */
  void appendTo(Locale locale, Appendable appendable) throws IOException;
}
