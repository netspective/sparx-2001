package com.netspective.sparx.util.java2html;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class HtmlHighlighter extends HtmlTokenizer
{
    private InputStream source;
    private DataOutputStream out;

    public HtmlHighlighter(boolean ignore_whitespace, boolean tokenize_words, InputStream source, OutputStream out)
    {
        super(ignore_whitespace, tokenize_words);
        this.source = source;
        this.out = new DataOutputStream(out);
    }

    public final void generateHtml()
    {
        try
        {
            out.writeBytes("<pre>  ");
            while(nextToken() != TT_EOF);
            out.writeBytes("</pre>");
        }
        catch(Exception e)
        {
        }
    }

    protected int nextChar()
    {
        try
        {
            return source.read();
        }
        catch(IOException e)
        {
            return -1;
        }
    }

    protected final void saveChar(char ch)
    {
        super.saveChar(ch);
        try
        {
            switch(ch)
            {
                case '<' :
                    out.writeBytes("&lt;");
                    break;

                case '\t':
                    out.writeBytes("    ");
                    break;

                case '\n':
                    out.writeBytes("\n  ");
                    break;

                default:
                    out.write(ch);
            }
        }
        catch(Exception e)
        {
        }
    }

    protected final void beginElementName(boolean end_tag)
    {
        try { out.writeBytes("<font color='#000099'><strong>"); } catch (Exception e)  { }
    }

    protected final void endElementName()
    {
        try { out.writeBytes("</strong></font>"); } catch (Exception e)  { }
    }

    protected final void beginAttributeName()
    {
        try { out.writeBytes("<font color='#009999'>"); } catch (Exception e)  { }
    }

    protected final void endAttributeName()
    {
        try { out.writeBytes("</font>"); } catch (Exception e)  { }
    }

    protected final void beginAttributeValue()
    {
        try { out.writeBytes("<font color='#009900'>"); } catch (Exception e)  { }
    }

    protected final void endAttributeValue()
    {
        try { out.writeBytes("</font>"); } catch (Exception e)  { }
    }
}

