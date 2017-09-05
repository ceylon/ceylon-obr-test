package com.redhat.ceylon.compiler.typechecker.parser;

import java.io.IOException;
import java.io.StringReader;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;

/**
 * Responsible for re-lexing the stream of tokens produced
 * by the ANTLR grammar, scanning STRING_LITERAL tokens
 * for \(expression) style interpolated strings.
 */
public final class CeylonInterpolatingLexer implements TokenSource {
    
    private final CeylonLexer lexer;
    private int tokenIndex;
    private Token interpolatedStringToken;
    private int currentIndexInStringToken;
    private CeylonLexer interpolatedExpressionLexer;
    private StringBuilder consumedText = new StringBuilder();
    
    public CeylonInterpolatingLexer(CeylonLexer lexer) {
        this.lexer = lexer;
    }

    private int line() {
        int line = 1;
        for (int i=0, len=consumedText.length(); 
                i<len; i++) {
            char ch = consumedText.charAt(i);
            if (ch=='\n' 
                    || ch=='\r' 
                    && i+1<len 
                    && consumedText.charAt(i+1)=='\f') {
                line++;
            }
        }
        return line;
    }
    
    private int charPos() {
        return consumedText.length() 
                - Math.max(consumedText.lastIndexOf("\n"), 
                        consumedText.lastIndexOf("\r\f"));
    }
    
    private int token() {
        return tokenIndex++;
    }
    
    private void initToken(CommonToken token) {
        token.setTokenIndex(token());
        token.setLine(line());
        token.setCharPositionInLine(charPos());
        token.setStartIndex(consumedText.length());
        consumedText.append(token.getText());
        token.setStopIndex(consumedText.length()-1);
    }

    int findMatchingCloseParen(String text, int from) {
        int depth = 0;
        for (int i=from+2, s=text.length(); i<s; i++) {
            char ch = text.charAt(i);
            if (ch=='(') depth++;
            if (ch==')') depth--;
            if (depth<0) return i;
        }
        return text.length()-1;
    }

    @Override
    public Token nextToken() {
        if (interpolatedExpressionLexer != null) {
            Token token = interpolatedExpressionLexer.nextToken();
            if (token.getType()==CeylonLexer.EOF) {
                interpolatedExpressionLexer = null;
            }
            else {
                CommonToken t = new CommonToken(token);
                initToken(t);
                return t;
            }
        }
        
        if (interpolatedStringToken!=null) {
            String text = interpolatedStringToken.getText();
            int start = text.indexOf("\\(", currentIndexInStringToken);
            if (start<0) {
                CommonToken endToken = 
                        new CommonToken(CeylonLexer.STRING_END, 
                                text.substring(currentIndexInStringToken));
                initToken(endToken);
                interpolatedStringToken = null;
                return endToken;
            }
            else {
                CommonToken midToken = 
                        new CommonToken(CeylonLexer.STRING_MID, 
                                text.substring(currentIndexInStringToken, start+2));
                initToken(midToken);
                createInterpolatedLexer(text, start);
                return midToken;
            }
        }
        
        CommonToken token = (CommonToken) lexer.nextToken();
        token.setTokenIndex(token());
        String text = token.getText();
        
        if (token.getType()!=CeylonLexer.STRING_LITERAL) {
            consumedText.append(text);
            return token;
        }
        int start = text.indexOf("\\(");
        if (start<0) {
            consumedText.append(text);
            return token;
        }
        
        CommonToken startToken = 
                new CommonToken(CeylonLexer.STRING_START, 
                        text.substring(0, start+2));
        initToken(startToken);
        interpolatedStringToken = token;
        createInterpolatedLexer(text, start);
        return startToken;
    }

    private void createInterpolatedLexer(String text, int start) {
        int end = findMatchingCloseParen(text, start);
        String substring = text.substring(start+2, end);
        try {
            interpolatedExpressionLexer = 
                    new CeylonLexer(new ANTLRReaderStream(
                            new StringReader(substring)));
        } catch (IOException e) {
            interpolatedStringToken = null;
            interpolatedExpressionLexer = null;
        }
        currentIndexInStringToken = end;
    }

    @Override
    public String getSourceName() {
        return lexer.getSourceName();
    }
}