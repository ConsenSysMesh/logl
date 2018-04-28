package org.logl.logl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

abstract class NameAbbreviator {
  private NameAbbreviator() {}

  static NameAbbreviator forPattern(String pattern) {
    if (pattern.isEmpty()) {
      return new NoOp();
    }

    // try parsing as a single count
    OptionalInt maybeCount = parseCount(pattern);
    if (maybeCount.isPresent()) {
      int count = maybeCount.getAsInt();
      if (count == 0) {
        throw new IllegalArgumentException("Abbreviation of 0 would output nothing");
      } else if (count < 0) {
        return new DropElements(-count);
      } else {
        return new RetainElements(count);
      }
    }

    // try parsing as a list of fragments
    List<Abbreviation> fragments = parseFragments(pattern);
    return new AbbreviateElements(fragments);
  }

  private static OptionalInt parseCount(String pattern) {
    try {
      return OptionalInt.of(Integer.parseInt(pattern));
    } catch (NumberFormatException ex) {
      // check if it was all digits
      int i = 0;
      char c = pattern.charAt(i);
      if (c == '-' || c == '+') {
        ++i;
      }
      int patternLength = pattern.length();
      if (i == patternLength) {
        // no digit characters in the string, so not a count
        return OptionalInt.empty();
      }
      while (i < patternLength && Character.digit(pattern.charAt(i), 10) >= 0) {
        ++i;
      }
      if (i == patternLength) {
        // all digits, so it is a count but it overflowed
        throw new IllegalArgumentException("Abbreviation count is larger than an integer");
      }
      // non-digit characters were found, so not a count
      return OptionalInt.empty();
    }
  }

  private static List<Abbreviation> parseFragments(String pattern) {
    ArrayList<Abbreviation> abbreviations = new ArrayList<>();

    int patternLength = pattern.length();

    for (int pos = 0; pos < patternLength; ++pos) {
      int leadingChars = 0;
      StringBuilder ellipsis = new StringBuilder();
      char c = pattern.charAt(pos);

      if (c == '*') {
        leadingChars = Integer.MAX_VALUE;
        ++pos;
      } else {
        int digit;
        while ((digit = Character.digit(c, 10)) >= 0) {
          leadingChars *= 10;
          if ((Integer.MAX_VALUE - leadingChars) < digit) {
            throw new IllegalArgumentException("Abbreviation count is larger than an integer");
          }
          leadingChars += digit;
          if (!(++pos < patternLength)) {
            break;
          }
          c = pattern.charAt(pos);
        }
      }

      while (pos < patternLength && (c = pattern.charAt(pos)) != '.') {
        if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {
          throw new IllegalArgumentException("Abbreviation fragment contains non-symbol replacement '" + c + "'");
        }
        ellipsis.append(c);
        ++pos;
      }

      abbreviations.add(new Abbreviation(leadingChars, ellipsis.toString()));
    }

    return abbreviations;
  }

  abstract void writeTo(String name, Appendable appendable) throws IOException;

  String abbreviate(String name) {
    StringBuilder builder = new StringBuilder();
    try {
      writeTo(name, builder);
    } catch (IOException e) {
      // not thrown by StringBuilder
      throw new RuntimeException(e);
    }
    return builder.toString();
  }

  private static final class NoOp extends NameAbbreviator {
    @Override
    void writeTo(String name, Appendable destination) throws IOException {
      destination.append(name);
    }
  }

  private static final class DropElements extends NameAbbreviator {
    private final int count;

    DropElements(int count) {
      this.count = count;
    }

    @Override
    void writeTo(String name, Appendable destination) throws IOException {
      int start = 0;
      for (int i = 0; i < count; i++) {
        int next = name.indexOf('.', start);
        if (next == -1) {
          // always output the last element
          destination.append(name, start, name.length());
          return;
        }
        start = next + 1;
      }
      destination.append(name, start, name.length());
    }
  }

  private static final class RetainElements extends NameAbbreviator {
    private final int count;

    RetainElements(int count) {
      this.count = count;
    }

    @Override
    void writeTo(String name, Appendable destination) throws IOException {
      int length = name.length();
      if (length == 0) {
        return;
      }
      int end = length;
      assert (count > 0);
      for (int i = count; i > 0; --i) {
        end = name.lastIndexOf('.', end - 1);
        if (end == -1) {
          destination.append(name);
          return;
        }
      }
      destination.append(name, end + 1, length);
    }
  }

  private static final class Abbreviation {
    private final int leadingChars;
    private final String ellipsis;

    Abbreviation(int leadingChars, String ellipsis) {
      this.leadingChars = leadingChars;
      this.ellipsis = ellipsis;
    }

    public int abbreviateNext(String name, int start, Appendable destination) throws IOException {
      int nameLength = name.length();
      for (int i = start; i < nameLength; ++i) {
        if (name.charAt(i) == '.') {
          if ((i - start) <= leadingChars) {
            destination.append(name, start, i + 1);
          } else {
            destination.append(name, start, start + leadingChars);
            destination.append(ellipsis);
            destination.append('.');
          }
          return i + 1;
        }
      }

      destination.append(name, start, nameLength);
      return nameLength;
    }
  }

  private static final class AbbreviateElements extends NameAbbreviator {
    private final Abbreviation[] fragments;

    AbbreviateElements(List<Abbreviation> fragments) {
      this.fragments = fragments.toArray(new Abbreviation[fragments.size()]);
    }

    @Override
    void writeTo(String name, Appendable destination) throws IOException {
      int pos = 0;
      int nameLength = name.length();

      // all non-terminal abbreviations are evaluated once
      for (Abbreviation fragment : fragments) {
        pos = fragment.abbreviateNext(name, pos, destination);
        if (pos >= nameLength) {
          return;
        }
      }

      // last abbreviations is evaluated repeatedly
      Abbreviation lastFragment = fragments[fragments.length - 1];
      while (pos < nameLength) {
        pos = lastFragment.abbreviateNext(name, pos, destination);
      }
    }
  }
}
