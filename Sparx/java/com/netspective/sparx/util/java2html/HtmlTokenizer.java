package com.netspective.sparx.util.java2html;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.Character;

/**
 * Abstract base class to an HTML parser.  Leaves data access and data
 * storage methods to be implemented by subclasses.
 * @author Len Norton <len.norton@acm.org>
 */

public abstract class HtmlTokenizer {

    /** End of File */
    public static final int TT_EOF       = 0xffffffff;

    /**
     * A note about the tag constants: the upper byte is where they are
     * differentiated.  This is to allow specialization for the types in some
     * future application, yet still allow compatibilty with some current
     * implementations.  For example, say someone wanted to elaborate the
     * <var>TT_TAG</var> constant because they wanted &lt;pre&gt; to be a
     * special case, identifiable before the call to <code>getToken()</code>.
     * They could create a token type called <var>TT_TAG_PRE</var> in their
     * subclass with the value 0x0101.  As long as they used the
     * <code>isTag(int)</code> static member to identify the token,
     * compatibility is not broken.
     */

    /** HTML tag */
    public static final int TT_TAG       = 0x0100;

    /** Plain text */
    public static final int TT_TEXT      = 0x0200;

    /** SGML/XML definition: &lt;!DOCTYPE blah blah&gt; */
    public static final int TT_SGML      = 0x0300;

    /** Comment: &lt;!-- fnord! --&gt; */
    public static final int TT_COMMENT   = 0x0400;

    /** Syntax error */
    public static final int TT_ERROR     = 0x0500;

    // intermediate states
    private static final int STATE01     = 0x0001;
    private static final int STATE02     = 0x0002;
    private static final int STATE03     = 0x0003;
    private static final int STATE04     = 0x0004;
    private static final int STATE05     = 0x0005;
    private static final int STATE06     = 0x0006;
    private static final int STATE07     = 0x0007;
    private static final int STATE08     = 0x0008;
    private static final int STATE09     = 0x0009;
    private static final int STATE10     = 0x0010;
    private static final int STATE11     = 0x0011;
    private static final int STATE12     = 0x0012;
    private static final int STATE13     = 0x0013;
    private static final int STATE14     = 0x0014;
    private static final int STATE15     = 0x0015;
    private static final int STATE16     = 0x0016;
    private static final int STATE17     = 0x0017;
    private static final int STATE18     = 0x0018;
    private static final int STATE19     = 0x0019;
    private static final int STATE20     = 0x0020;
    private static final int STATE21     = 0x0021;
    private static final int STATE22     = 0x0022;
    private static final int STATE23     = 0x0023;
    private static final int STATE24     = 0x0024;
    private static final int STATE25     = 0x0025;
    private static final int STATE26     = 0x0026;
    private static final int STATE27     = 0x0027;
    private static final int STATE28     = 0x0028;
    private static final int STATE29     = 0x0029;
    private static final int STATE30     = 0x0030;

    /**
     * When true, whitespace between tags, and between tags and text, is
     * not remembered.  It has no effect when <var>in_pre</var> is true.
     */
    private boolean ignore_whitespace = false;

    /**
     * When true, text and whitespace itself is tokenized.  All whitespace
     * is ignored when <var>ignore_whitespace</var> is true, which is
     * overriden when <var>in_pre</var> is true.
     */
    private boolean tokenize_words = false; 

    // True when nextToken() should return TT_EOF.
    private boolean eof = false;

    // Holds state within and between calls to nextToken().
    private int current_state = STATE01;

    // True when parsing the insides of a <pre>...</pre> block.
    // Overrides ignore_whitespace.
    private boolean in_pre = false;

    /** Holds last recognized token type. */
    protected int token_type = TT_EOF;

    /** Holds last recognized token.  */
    protected Object token = null;

    /** Holds characters of current token. */
    protected StringBuffer buffer = new StringBuffer();

    public HtmlTokenizer() {
    }

    public HtmlTokenizer( boolean ignore_whitespace,
                              boolean tokenize_words ) {
        this.ignore_whitespace = ignore_whitespace;
        this.tokenize_words = tokenize_words;
    }

    /**
     * Returns the next character from the datasource.  This method
     * is abstract because the data source must be defined by subclasses.
     * Returns a character as an <code>int</code>, or a negative
     * integer as a special notifier like end of file.
     */
    protected abstract int nextChar();

    /**
     * Remembers the character as part of a potential token.
     */
    protected void saveChar( char ch ) {
        buffer.append( ch );
    }

    /**
     * Recognizes all remembered characters as part of a new token,
     * leaving <var>token</var> referencing a representation of it, and
     * <var>token_type</var> containing the type passed in as <var>t</var>.
     * Subsequent calls to <code>saveChar(char)</code> only remember
     * characters for the next potential token.
     */
    protected int saveToken( int t ) {
        // Leave buffer and token alone on EOF for subclasses that allow
        // the data sources to be reassigned.  It is possible that a token
        // may be continued in the next buffer/file/string/etc.
        if( t != TT_EOF ) {
            buffer.setLength( 0 );
            token = buffer.toString();
        }
        return ( token_type = t );
    }

    /**
     * Forgets all characters previously remembered as part of a potential
     * token.  Has no effect on the last token.
     */
    protected void clear() {
        buffer.setLength( 0 );
    }

    protected void beginMarkup() {
    }
    protected void endMarkup() {
    }

    protected void beginElementName( boolean end_tag ) {
    }
    protected void endElementName() {
    }

    protected void beginAttributeName() {
    }
    protected void endAttributeName() {
    }

    protected void beginAttributeValue() {
    }
    protected void endAttributeValue() {
    }

    /** Returns the last recognized token. */
    public Object getToken() {
        return token;
    }

    /** Returns the last recognized token type. */
    public int getTokenType() {
        return token_type;
    }

    /** Parses out the next token, and returns its type. */
    public final int nextToken() {

        if( eof ) return saveToken( TT_EOF );

        int c;
        char ch;
        boolean in_squote = false;
        boolean in_dquote = false;

        while( true ) {
            c = nextChar();
            if( c == -1 ) {
                eof = true;
                switch( current_state ) {
                case STATE01:
                    return saveToken( TT_EOF );
                case STATE02:
                    if( in_pre || ! ignore_whitespace ) {
                        return saveToken( TT_TEXT );
                    } else {
                        return saveToken( TT_EOF );
                    }
                case STATE03:
                    return saveToken( TT_TEXT );
                default:
                    return saveToken( TT_ERROR );
                }
            }

            ch = (char) c;

            switch( current_state ) {
            case STATE01:
                if( Character.isWhitespace( ch ) ) {
                    if( in_pre || ! ignore_whitespace ) {
                        current_state = STATE02;
                        saveChar( ch );
                    }
                    // else { } // if ignoring whitespace, do nothing and stay
                } else if( ch == '<' ) {
                    beginMarkup();
                    current_state = STATE04;
                    saveChar( ch );
                } else {
                    current_state = STATE03;
                    saveChar( ch );
                }
                break;

            case STATE02:
                if( Character.isWhitespace( ch ) ) {
                    // stay
                    saveChar( ch );
                } else if( ch == '<' ) {
                    beginMarkup();
                    if( in_pre || ! ignore_whitespace ) {
                        saveToken( TT_TEXT );
                        current_state = STATE04;
                        saveChar( ch );
                        return getTokenType();
                    } else {
                        clear();
                        current_state = STATE04;
                        saveChar( ch );
                    }
                } else {
                    if( tokenize_words ) {
                        saveToken( TT_TEXT );
                        current_state = STATE03;
                        saveChar( ch );
                        return getTokenType();
                    } else {
                        current_state = STATE03;
                        saveChar( ch );
                    }
                }
                break;

            case STATE03:
                if( Character.isWhitespace( ch ) ) {
                    if( tokenize_words ) {
                        if( in_pre || ! ignore_whitespace ) {
                            saveToken( TT_TEXT );
                            current_state = STATE02;
                            saveChar( ch );
                            return getTokenType();
                        } else {
                            current_state = STATE01;
                           return saveToken( TT_TEXT );
                        }
                    } else {
                        current_state = STATE02;
                        saveChar( ch );
                    }
                } else if( ch == '<' ) {
                    saveToken( TT_TEXT );
                    beginMarkup();
                    current_state = STATE04;
                    saveChar( ch );
                    return getTokenType();
                } else {
                    // stay
                    saveChar( ch );
                }
                break;

            case STATE04:
                if( Character.isWhitespace( ch ) ) {
                    // stay (technically should be an error, but people
                    // can be sloppy with their HTML)
                    saveChar( ch );
                } else if( Character.isLetter( ch )) {
                    beginElementName( false );
                    current_state = STATE06;
                    saveChar( ch );
                } else if( ch == '>' ) {
                    current_state = STATE01;
                    saveChar( ch );
                    endMarkup();
                    return saveToken( TT_ERROR );
                } else if( ch == '!' ) {
                    current_state = STATE20; // go to SGML/comment state
                    saveChar( ch );
                } else if( ch == '/' ) {
                    current_state = STATE05;
                    saveChar( ch );
                    beginElementName( true );
                } else {
                    current_state = STATE01;
                    saveChar( ch );
                    return saveToken( TT_ERROR );
                }
                break;

            case STATE05:
                if( Character.isLetter( ch ) ) {
                    current_state = STATE06;
                    saveChar( ch );
                } else {
                    current_state = STATE01;
                    saveChar( ch );
                    return saveToken( TT_ERROR );
                }
                break;

            case STATE06:
                if( Character.isLetterOrDigit( ch )
                        || ch == '-' || ch == '.' || ch == ':' ) {
                    // stay
                    saveChar( ch );
                } else if( Character.isWhitespace( ch ) ) {
                    endElementName();
                    current_state = STATE07;
                    saveChar( ch );
                } else if( ch == '>' ) {
                    endElementName();
                    current_state = STATE01;
                    saveChar( ch );
                    endMarkup();
                    return saveToken( TT_TAG );
                } else {
                    current_state = STATE01;
                    saveChar( ch );
                    return saveToken( TT_ERROR );
                }
                break;

            case STATE07:
                if( Character.isLetter( ch ) ) {
                    beginAttributeName();
                    current_state = STATE08;
                    saveChar( ch );
                } else if( Character.isWhitespace( ch ) ) {
                    // stay
                    saveChar( ch );
                } else if( ch == '>' ) {
                    current_state = STATE01;
                    saveChar( ch );
                    endMarkup();
                    return saveToken( TT_TAG );
                } else {
                    current_state = STATE01;
                    saveChar( ch );
                    return saveToken( TT_ERROR );
                }
                break;

            case STATE08:
                if( Character.isLetterOrDigit( ch )
                        || ch == '-' || ch == '.' || ch == '_' ) {
                    // stay
                    saveChar( ch );
                } else if( Character.isWhitespace( ch ) ) {
                    endAttributeName();
                    current_state = STATE09;
                    saveChar( ch );
                } else if( ch == '>' ) {
                    endAttributeName();
                    current_state = STATE01;
                    saveChar( ch );
                    endMarkup();
                    return saveToken( TT_TAG );
                } else if( ch == '=' ) {
                    endAttributeName();
                    current_state = STATE10;
                    saveChar( ch );
                } else {
                    current_state = STATE01;
                    saveChar( ch );
                    return saveToken( TT_ERROR );
                }
                break;

            case STATE09:
                if( Character.isLetter( ch ) ) {
                    beginAttributeName();
                    current_state = STATE08;
                    saveChar( ch );
                } else if( Character.isWhitespace( ch ) ) {
                    // stay
                    saveChar( ch );
                } else if( ch == '>' ) {
                    current_state = STATE01;
                    saveChar( ch );
                    endMarkup();
                    return saveToken( TT_TAG );
                } else if( ch == '=' ) {
                    current_state = STATE10;
                    saveChar( ch );
                } else {
                    current_state = STATE01;
                    saveChar( ch );
                    return saveToken( TT_ERROR );
                }
                break;

            case STATE10:
                if( Character.isWhitespace( ch ) ) {
                    // stay
                    saveChar( ch );
                } else if( ch == '>' ) {
                    // "<... attribute=>", assume value exists, but is null
                    beginAttributeValue();
                    endAttributeValue();
                    current_state = STATE01;
                    saveChar( ch );
                    return saveToken( TT_TAG );
                } else if( ch == '\'' ) {
                    in_squote = true;
                    current_state = STATE11;
                    saveChar( ch );
                    beginAttributeValue();
                } else if( ch == '\"' ) {
                    in_dquote = true;
                    current_state = STATE11;
                    saveChar( ch );
                    beginAttributeValue();
                } else {
                    // accept any text
                    beginAttributeValue();
                    current_state = STATE11;
                    saveChar( ch );
                }
                break;

            case STATE11:
                if( in_squote ) {
                    if( ch == '\'' ) {
                        endAttributeValue();
                        in_squote = false;
                        current_state = STATE07;
                        saveChar( ch );
                    } else {
                        // stay
                        saveChar( ch );
                    }
                } else if( in_dquote ) {
                    if( ch == '\"' ) {
                        endAttributeValue();
                        in_dquote = false;
                        current_state = STATE07;
                        saveChar( ch );
                    } else {
                        // stay
                        saveChar( ch );
                    }
                } else {
                    if( Character.isWhitespace( ch ) ) {
                        endAttributeValue();
                        current_state = STATE07;
                        saveChar( ch );
                    } else if( ch == '>' ) {
                        endAttributeValue();
                        current_state = STATE01;
                        saveChar( ch );
                        endMarkup();
                        return saveToken( TT_TAG );
                    } else {
                        // stay
                        saveChar( ch );
                    }
                }
                break;

     /// end HTML states, begin comment/SGML states ////

            case STATE20:
                if( ch == '-' ) {
                    current_state = STATE21;
                    saveChar( ch );
                } else if( ch == '>' ) {
                    current_state = STATE01;
                    saveChar( ch );
                    endMarkup();
                    return saveToken( TT_SGML );
                } else {
                    current_state = STATE30; // SGML state
                    saveChar( ch );
                }
                break;

            case STATE21:
                if( ch == '-' ) {
                    current_state = STATE22;
                    saveChar( ch );
                } else if( ch == '>' ) {
                    current_state = STATE01;
                    saveChar( ch );
                    endMarkup();
                    return saveToken( TT_SGML );
                } else {
                    current_state = STATE30; // SGML state
                    saveChar( ch );
                }
                break;

            case STATE22:
                if( ch == '-' ) {
                    current_state = STATE23;
                    saveChar( ch );
                } else {
                    // stay, save as part of comment text
                    saveChar( ch );
                }
                break;

            case STATE23:
                if( ch == '-' ) {
                    current_state = STATE24;
                    saveChar( ch );
                } else {
                    current_state = STATE22;
                    saveChar( ch );
                }
                break;

            case STATE24:
                if( ch == '>' ) {
                    current_state = STATE01;
                    saveChar( ch );
                    endMarkup();
                    return saveToken( TT_COMMENT );
                } else {
                    // Stay, because technically the comment is over when
                    //  "--" is encountered, but we should wait until
                    // '>' because people still write bad HTML.
                    saveChar( ch );
                }
                break;

            case STATE30:
                if( ch == '>' ) {
                    current_state = STATE01;
                    saveChar( ch );
                    endMarkup();
                    return saveToken( TT_SGML );
                } else {
                    // stay
                    saveChar( ch );
                }
                break;

            default:
            }
        }
    }

    public static final boolean IsEOF( int t ) {
        return ( t == TT_EOF );
    }

    public static final boolean IsTag( int t ) {
        return ( ( t & 0x0000ff00 ) == TT_TAG );
    }

    public static final boolean IsText( int t ) {
        return ( ( t & 0x0000ff00 ) == TT_TEXT );
    }

    public static final boolean IsSGML( int t ) {
        return ( ( t & 0x0000ff00 ) == TT_SGML );
    }

    public static final boolean IsComment( int t ) {
        return ( ( t & 0x0000ff00 ) == TT_COMMENT );
    }

    public static final boolean IsError( int t ) {
        return ( ( t & 0x0000ff00 ) == TT_ERROR );
    }
}

