package org.dnal.compiler.parser;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.pattern.Patterns;

/**
 * Lexer specific for the DNAL language rules.
 * 
 */
public final class DNALLexer {
  
  static final Parser<String> IDENTIFIER = Patterns.isChar(Character::isJavaIdentifierStart)
      .next(Patterns.isChar(Character::isJavaIdentifierPart).many())
      .toScanner("identifier")
      .source();
  
}
