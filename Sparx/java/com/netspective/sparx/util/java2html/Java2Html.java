package com.netspective.sparx.util.java2html;

/*
 * Java2Html.java
 *
 * Created on May 27, 2000, 10:48 PM
 */


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
<p>
<center>
<table border=1 cellspacing=0 cellpadding=10><tr align="center"><td><i>
&copy; 2000, Think Tank Ltd, Douglas, Isle Of Man.
All rights reserved.<br />
See <a href="../../../overview-summary.html#license">overview</a>
for full copyright notice, license and disclaimer.<br />
For more information please contact
<a href="mailto:info@t-tank.com?subject=Java2Html">info@t-tank.com</a>
</i></td></tr></table>
</center>
</p>
<p>
<a name="intro"><code>Java2Html</code></a> is a Java Bean generating syntax-highlighted HTML ouput
for Java source code. Check out this <a href="../../../Java2Html.html">example</a>
to see what the generated HTML looks like.
The component has a number of properties and can be customized by
setting these accordingly:
<dl>
 <p>
 <dt>
 <code><strong><font color="maroon">String[] color</font></strong></code>
 </dt>
 <dd>
 When parsing the Java source code <code>Java2Html</code> recognizes a
 number of token classes. The <code>color</code> table
 holds one entry per token class specifying the (foreground)
 color that is to be used for rendering tokens of a particular
 token class on the generated web page.
 <p>
 <center>
 <a name="colorTable"><table border="1" cellspacing="2" cellpadding="4" bordercolor="#9C9A9C"></a>
 <tr bgcolor="#94CBFF">
 <th>index</th>
 <th>token class</th>
 <th>default color</th>
 </tr>
 <tr bgcolor="#fffbea">
 <td align="center"><code>0</code></td>
 <td>plain/normal</td>
 <td><font color="#000000"><code>#000000</code></font></td>
 </tr>
 <tr bgcolor="#fffbea">
 <td align="center"><code>1</code></td>
 <td>Java keyword</td>
 <td><font color="#800000"><code>#800000</code></font></td>
 </tr>
 <tr bgcolor="#fffbea">
 <td align="center"><code>2</code></td>
 <td>Java API class</td>
 <td><font color="#000080"><code>#000080</code></font></td>
 </tr>
 <tr bgcolor="#fffbea">
 <td align="center"><code>3</code></td>
 <td>constant/literal</td>
 <td><font color="#008200"><code>#008200</code></font></td>
 </tr>
 <tr bgcolor="#fffbea">
 <td align="center"><code>4</code></td>
 <td>comment</td>
 <td><font color="#636163"><code>#636163</code></font></td>
 </tr>
 </table>
 </center>
 </p>
 </dd>
 </p>
 <p>
 <dt>
 <code><strong><font color="maroon">boolean lineNumberFlag</font></strong></code>
 </dt>
 <dd>
 This flag determines whether the source code lines in the
 HTML output will be prefixed with line numbers
 (default value: <code>false</code>)
 </dd>
 </p>
 <p>
 <dt>
 <code><strong><font color="maroon">String tabString</font></strong></code>
 </dt>
 <dd>
 The <code>TAB</code> chars in the Java source will be replaced by this
 string (default value: 2 <code>space</code> characters)
 </dd>
 </p>
 <p>
 <dt>
 <code><strong><font color="maroon">int wrapColumn</font></strong></code>
 </dt>
 <dd>
 Long code lines will be wrapped after this column if
 <code>Java2Html</code> was configured to perform line
 wrapping (default value: <code>80</code>)
 </dd>
 </p>
 <p>
 <dt>
 <code><strong><font color="maroon">String wrapMode</font></strong></code>
 </dt>
 <dd>
 Set this property to one of the following values to specify
 what <code>Java2Html</code> is supposed to do with long lines:
 <p>
 <ul>
 <li>
 <code><strong><font color="navy">none</font></strong></code>
 -- do nothing, leave long lines as they are
 </li>
 <li>
 <code><strong><font color="navy">sharp</font></strong></code>
 -- wrap long lines after exactly <code>'wrapColumn'</code>
 characters
 </li>
 <li>
 <code><strong><font color="navy">word</font></strong></code>
 -- wrap long lines on first <em>delimiter</em> seen after
 <code>'wrapColumn'</code> characters. Please note: this wrap
 mode needs some brushing up.
 </li>
 </ul>
 </p>
 </dd>
 </p>
</dl>
</p>
 */

public class Java2Html extends Object implements Serializable
{

    private String[] color =
            {
                "#000000", // plain
                "#800000", // keyword
                "#000080", // api class
                "#008200", // constant
                "#636163" // comment
            };

    private boolean lineNumberFlag;
    private String tabString = "  ";
    private int wrapColumn = 80;
    private String wrapMode = "none";

    private WrapMode wmode;
    private int columnCounter = 1;
    /** this string will contain a number of blanks if we're
     generating line numbers in order to indent the wrapped lines
     appropriately */
    private String wrapPrefix = "";
    private String toolName = "Java2Html";
    private boolean writeSerialFiles;
    private boolean initialized;
    private static TreeSet keywordSet = new TreeSet();
    private static TreeSet apiclassSet = new TreeSet();
    private static final String kwordsFile = "kwords.ser";
    private static final String aclassesFile = "aclasses.ser";

    /** Reads Java source code from the input stream (supplied by the
     first parameter) and writes the generated HTML to the output stream
     (supplied by the second parameter).<br />
     The <a href="#intro">introduction</a> provides more information
     on the properties that control the appearance
     of the Java source on the generated web page */

    public int generateHtml(InputStream javaSource, OutputStream htmlOutput) throws IOException
    {
        initialize();

        String contents = readJavaSource(javaSource);
        DataOutputStream out = new DataOutputStream(htmlOutput);

        ParseCtxt context = new ParseCtxt(ParseCtxt.none);
        String delimiters = " \r\t\n()[]{},\"'/\\*=;+-<>|&%^~?:";
        StringTokenizer st = new StringTokenizer(contents, delimiters, true);
        int lineCounter = 1;
        char lastDelimiter = '\0', firstChar;
        String colorPrefix = null, colorSuffix = null, notPrintedYet = null;
        ColorManager cmgr = new ColorManager(this);
        boolean dontPrintDelimiter = false;

        out.writeBytes(htmlPrefix());
        out.writeBytes("<pre>\n");

        if (lineNumberFlag)
            out.writeBytes(formatLineNumber(lineCounter));

        while (st.hasMoreTokens())
        {
            String token = st.nextToken();

            /*
			System.out.println("[" + lineCounter + "] context = '" + context + "', token = '" + token + "'");
			*/

            boolean lastTokenWasADelimiter = false;

            firstChar = token.charAt(0);

            if ((token.length() == 1) && (delimiters.indexOf(firstChar) > -1))
            {
                /* it's a delimiter */
                switch (firstChar)
                {
                    case '\t':
                        token = tabString;
                        break;
                    case '\n':
                        columnCounter = 1;
                        lineCounter += 1;
                        if (context.value() == ParseCtxt.slComment)
                        {
                            context.set(ParseCtxt.none);
                            colorPrefix = cmgr.closeColor();
                        }

                        if (lineNumberFlag)
                        {
                            String colorString = cmgr.switchColor(TokenClass.plain);

                            if (colorPrefix != null)
                                colorPrefix += colorString;
                            else
                                colorPrefix = colorString;

                            token += formatLineNumber(lineCounter);

                            colorString = cmgr.restoreColor();

                            if (colorSuffix != null)
                                colorSuffix = colorString + colorSuffix;
                            else
                                colorSuffix = colorString;
                        }

                        break;
                    case '"':
                        if (context.value() == ParseCtxt.string)
                        {
                            if (lastDelimiter != '\\')
                            {
                                context.set(ParseCtxt.none);
                                colorSuffix = cmgr.closeColor();
                            }
                        }
                        else if (context.value() == ParseCtxt.none)
                        {
                            context.set(ParseCtxt.string);
                            colorPrefix = cmgr.openColor(TokenClass.constant);
                        }
                        break;
                    case '/':
                        if (context.value() == ParseCtxt.mlComment)
                        {
                            /* end of multi-line comment */
                            if (lastDelimiter == '*')
                            {
                                context.set(ParseCtxt.none);
                                colorSuffix = cmgr.closeColor();
                            }
                        }
                        else if (context.value() == ParseCtxt.none)
                        {
                            if (lastDelimiter == '/')
                            {
                                context.set(ParseCtxt.slComment);
                                colorPrefix = cmgr.openColor(TokenClass.comment);
                                token += "/";
                                notPrintedYet = null;
                                dontPrintDelimiter = false;
                            }
                            else
                            {
                                // possibly the start of a '/*' or '//' token
                                notPrintedYet = "/";
                                dontPrintDelimiter = true;
                            }
                        }
                        break;
                    case '*':
                        if (context.value() == ParseCtxt.none)
                        {
                            if (lastDelimiter == '/')
                            {
                                context.set(ParseCtxt.mlComment);
                                colorPrefix = cmgr.openColor(TokenClass.comment);
                                token = "/*";
                                notPrintedYet = null;
                                dontPrintDelimiter = false;
                            }
                        }
                        break;
                    case '\'':
                        if (context.value() == ParseCtxt.charLiteral)
                        {
                            if (lastDelimiter != '\\')
                            {
                                context.set(ParseCtxt.none);
                                colorSuffix = cmgr.closeColor();
                            }
                        }
                        else if (context.value() == ParseCtxt.none)
                        {
                            context.set(ParseCtxt.charLiteral);
                            colorPrefix = cmgr.openColor(TokenClass.constant);
                        }
                        break;
                }

                if ((firstChar == '\\') && (lastDelimiter == '\\'))
                    lastDelimiter = '\0';
                else
                    lastDelimiter = firstChar;

                lastTokenWasADelimiter = true;
            }
            else
                lastDelimiter = '\0';

            if (!lastTokenWasADelimiter && (context.value() == ParseCtxt.none))
            {
                /* we'll only check the token if it's not a delimiter
					 and if we're not in some sort of parse context */

                boolean keyWord = false, apiClass = false;

                if ((keyWord = keywordSet.contains(token)))
                {
                    colorPrefix = cmgr.openColor(TokenClass.keyword);
                    colorSuffix = cmgr.closeColor();
                }
                else if ((apiClass = apiclassSet.contains(token)))
                {
                    colorPrefix = cmgr.openColor(TokenClass.apiClass);
                    colorSuffix = cmgr.closeColor();
                }
            }

            if (!lastTokenWasADelimiter || (lastTokenWasADelimiter && !dontPrintDelimiter))
            {
                if (colorPrefix != null)
                {
                    out.writeBytes(colorPrefix);
                    colorPrefix = null;
                }

                if (!lastTokenWasADelimiter && (notPrintedYet != null))
                {
                    if (notPrintedYet.compareTo("\r") != 0)
                    {
                        notPrintedYet = checkForWordWrap(notPrintedYet);
                        out.writeBytes(notPrintedYet);
                    }

                    notPrintedYet = null;
                    dontPrintDelimiter = false;
                }

                if (token.compareTo("\r") != 0)
                {
                    token = checkForWordWrap(token);
                    out.writeBytes(token);
                }

                if (colorSuffix != null)
                {
                    out.writeBytes(colorSuffix);
                    colorSuffix = null;
                }
            }
        }

        out.writeBytes("</pre>\n");
        out.writeBytes(htmlSuffix());

        return (0);
    }

    /** getter method for the <code>lineNumberFlag</code> property,
     see <a href="#intro">the introduction</a> for more information
     */
    public boolean getLineNumberFlag()
    {
        return (lineNumberFlag);
    }

    /** setter method for the <code>lineNumberFlag</code> property,
     see <a href="#intro">introduction</a> for more information
     */
    public void setLineNumberFlag(boolean value)
    {
        lineNumberFlag = value;

        if (lineNumberFlag == true)
            wrapPrefix = "      ";
        else
            wrapPrefix = "";
    }

    public Java2Html()
    {
        wmode = new WrapMode(WrapMode.none);
    }

    /** Indexed getter method for the <code>color</code> property,
     see <a href="#intro">introduction</a> for more information
     */
    public String getColor(int index) throws ArrayIndexOutOfBoundsException
    {
        if ((index > TokenClass.lowerb) && (index < TokenClass.upperb))
            return color[index];
        else
            throw(new ArrayIndexOutOfBoundsException("Index: '" + index + "' out of range!"));
    }

    /** Indexed setter method for the <code>color</code> property,
     see <a href="#intro">introduction</a> for more information
     */
    public void setColor(int index, String color) throws ArrayIndexOutOfBoundsException
    {
        if ((index > TokenClass.lowerb) && (index < TokenClass.upperb))
            this.color[index] = color;
        else
            throw(new ArrayIndexOutOfBoundsException("Index: '" + index + "' out of range!"));
    }

    /** getter method for the <code>tabString</code> property,
     see <a href="#intro">introduction</a> for more information
     */
    public String getTabString()
    {
        return tabString;
    }

    /** setter method for the <code>tabString</code> property,
     see <a href="#intro">introduction</a> for more information
     */
    public void setTabString(String tabString)
    {
        this.tabString = tabString;
    }

    /** getter method for the <code>wrapColumn</code> property,
     see <a href="#intro">introduction</a> for more information
     */
    public int getWrapColumn()
    {
        return wrapColumn;
    }

    /** setter method for the <code>wrapColumn</code> property,
     see <a href="#intro">introduction</a> for more information
     */
    public void setWrapColumn(int value)
    {
        this.wrapColumn = value;
    }

    /** getter method for the <code>wrapMode</code> property,
     see <a href="#intro">introduction</a> for more information
     */
    public String getWrapMode()
    {
        return wrapMode;
    }

    /** setter method for the <code>wrapMode</code> property,
     see <a href="#intro">introduction</a> for more information
     */
    public void setWrapMode(String value)
    {

        if (value.equals("none"))
            wmode.set(WrapMode.none);
        else if (value.equals("sharp"))
            wmode.set(WrapMode.sharp);
        else if (value.equals("word"))
            wmode.set(WrapMode.word);
        else
            Eiffel.Error("unknown wrap mode '" + value + "'");

        this.wrapMode = value;
    }

    /** main method for the HTML code generator, see the
     <a href="../../../overview-summary.html#cmdline">
     command line interface</a> description for more
     information.
     */

    public static void main(String[] args)
    {
        try
        {
            Java2Html pp = new Java2Html();
            ArrayList sourceFiles = new ArrayList();

            pp.handleArgs(args, sourceFiles);

            Iterator fileIter = sourceFiles.iterator();

            while (fileIter.hasNext())
            {
                String javaName = (String) fileIter.next();
                String htmlName = javaName.substring(0, javaName.lastIndexOf('.')) + ".html";

                try
                {
                    FileInputStream java = new FileInputStream(javaName);
                    FileOutputStream html = new FileOutputStream(htmlName);

                    pp.generateHtml(java, html);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    private String checkForWordWrap(String token)
    {
        final String eol = "<font color=\"#800000\"><strong>&para;</strong></font>";
        String result = null;

        if ((wmode.value() != WrapMode.none) && ((columnCounter + token.length()) > wrapColumn))
        {
            if (wmode.value() == WrapMode.word)
                result = convertMetachars(token) + eol + "\n" + wrapPrefix;
            else if (wmode.value() == WrapMode.sharp)
            {
                int columnsLeft = wrapColumn - columnCounter;

                result = convertMetachars(token.substring(0, columnsLeft)) + eol + "\n" + wrapPrefix + convertMetachars(token.substring(columnsLeft));
            }

            // reset column counter since we've inserted a newline char
            columnCounter = 1;
        }
        else
        {
            result = convertMetachars(token);
            columnCounter += token.length();
        }

        return (result);
    }

    private String readJavaSource(InputStream javaSource) throws IOException
    {
        DataInputStream dis = new DataInputStream(javaSource);
        byte[] buffer = new byte[65536];
        StringBuffer result = new StringBuffer();
        int bytesRead = 0;

        while ((bytesRead = dis.read(buffer)) > 0)
            result.append(new String(buffer, 0, bytesRead));

        return (result.toString());
    }

    private String convertMetachars(String token)
    {
        String result = token;
        /** meta char index, location */
        int mci = 0, mcLocation = 0;
        /* meta char array */
        char[] metaChar =
                {
                    '&',
                    '<',
                    '>',
                    '"'
                };
        /** the equivalent html escape codes */
        String[] htmlCode =
                {
                    "&amp;",
                    "&lt;",
                    "&gt;",
                    "&quot;"
                };

        for (; mci < metaChar.length; mci += 1)
        {
            mcLocation = 0;

            while ((mcLocation = result.indexOf(metaChar[mci], mcLocation)) > -1)
            {
                result = result.substring(0, mcLocation) + htmlCode[mci] + result.substring(mcLocation + 1);
                mcLocation += 1;
            }
        }
        return (result);
    }

    private String formatLineNumber(int lineNumber)
    {
        String result = null;

        if (lineNumber < 10)
            result = "    " + lineNumber + " ";
        else if (lineNumber < 100)
            result = "   " + lineNumber + " ";
        else if (lineNumber < 1000)
            result = "  " + lineNumber + " ";
        else if (lineNumber < 10000)
            result = " " + lineNumber + " ";
        else
            result = lineNumber + " ";

        return (result);
    }

    private String htmlPrefix()
    {
        String result =
                "<style type=\"text/css\">\n"
                + "<!--\n"
                + "body\n"
                + "{ background-color: #ffffff; font-size: -1 }\n"
                + "-->\n"
                + "</style>\n"
                + "<body>\n\n";

        return (result);
    }

    private String htmlSuffix()
    {
        return "";
    }

    private void initialize() throws IOException
    {
        if (!initialized)
        {
            synchronized (Java2Html.class)
            {
                if (!initialized)
                {
                    try
                    {
                        InputStream kwordsIS = getClass().getResourceAsStream(kwordsFile);

                        if (kwordsIS != null)
                        {
                            ObjectInputStream kois = new ObjectInputStream(kwordsIS);
                            keywordSet = (TreeSet) kois.readObject();

                            /*
							System.out.println("Found serialized keyword set");
							*/
                        }
                        else
                            initializeKeywords();
                    }
                    catch (IOException e)
                    {
                        initializeKeywords();
                    }
                    catch (ClassNotFoundException e)
                    {
                        initializeKeywords();
                    }
                    finally
                    {
                        if (keywordSet == null)
                            initializeKeywords();
                    }

                    try
                    {
                        InputStream aclassesIS = getClass().getResourceAsStream(aclassesFile);

                        if (aclassesIS != null)
                        {
                            ObjectInputStream aois = new ObjectInputStream(aclassesIS);
                            apiclassSet = (TreeSet) aois.readObject();

                            /*
							System.out.println("Found serialized API classes set");
							*/
                        }
                        else
                            initializeAPIClasses();
                    }
                    catch (IOException e)
                    {
                        initializeAPIClasses();
                    }
                    catch (ClassNotFoundException e)
                    {
                        initializeAPIClasses();
                    }
                    finally
                    {
                        if (apiclassSet == null)
                            initializeAPIClasses();
                    }

                    initialized = true;
                }
            }
        }
    }

    private void initializeKeywords() throws IOException
    {
        /*
		System.out.println("Initializing keyword set");
		*/

        for (int kwi = 0; kwi < keyWords.length; kwi += 1)
            keywordSet.add(keyWords[kwi]);

        if (writeSerialFiles == true)
        {
            OutputStream os = new FileOutputStream(kwordsFile);
            ObjectOutputStream oos = new ObjectOutputStream(os);

            oos.writeObject(keywordSet);
        }
    }

    private void initializeAPIClasses() throws IOException
    {
        /*
		System.out.println("Initializing API classes set");
		*/

        for (int aci = 0; aci < apiClasses.length; aci += 1)
            apiclassSet.add(apiClasses[aci]);

        if (writeSerialFiles == true)
        {
            OutputStream os = new FileOutputStream(aclassesFile);
            ObjectOutputStream oos = new ObjectOutputStream(os);

            oos.writeObject(apiclassSet);
        }
    }

    private static final String WRITESERIAL_ARG = "-ws";
    private static final String WRAP_COLUMN = "-wc";
    private static final String WRAP_MODE = "-wm";
    private static final String FILES_ARG = "-f";
    private static final String HELP_ARG = "-h";
    private static final String WRITELINENUMBERS_ARG = "-ln";
    private static final String COLOR_ARG = "-c";
    private static final String TABULATOR_ARG = "-t";

    private void handleArgs(String[] args, ArrayList sourceFiles)
    {
        if (args.length > 0)
        {
            for (int ai = 0; ai < args.length; ai += 1)
            {
                if (args[ai].equals(WRITESERIAL_ARG))
                    writeSerialFiles = true;
                else if (args[ai].equals(WRITELINENUMBERS_ARG))
                    setLineNumberFlag(true);
                else if (args[ai].equals(FILES_ARG))
                {
                    Eiffel.Assert(((ai + 1) < args.length), "no file names supplied");

                    for (ai += 1; ai < args.length; ai += 1)
                        sourceFiles.add(args[ai]);
                }
                else if (args[ai].equals(TABULATOR_ARG))
                {
                    Eiffel.Assert(((ai + 1) < args.length), "no tabulator substitution string supplied");

                    ai += 1;
                    setTabString(args[ai]);
                }
                else if (args[ai].equals(COLOR_ARG))
                {
                    Eiffel.Assert(((ai + 2) < args.length), "no color data supplied");

                    ai += 1;
                    int colorIndex = Integer.parseInt(args[ai]);

                    Eiffel.Assert((colorIndex > TokenClass.lowerb) && (colorIndex < TokenClass.upperb), "color index '" + colorIndex + "' out of range!");

                    ai += 1;

                    setColor(colorIndex, args[ai]);
                }
                else if (args[ai].equals(WRAP_COLUMN))
                {
                    Eiffel.Assert(((ai + 1) < args.length), "no wrap column supplied");

                    ai += 1;
                    setWrapColumn(Integer.parseInt(args[ai]));
                }
                else if (args[ai].equals(WRAP_MODE))
                {
                    Eiffel.Assert(((ai + 1) < args.length), "no wrap mode supplied");

                    ai += 1;
                    String wm = args[ai];

                    if (wm.equals("none") || wm.equals("sharp") || wm.equals("word"))
                        setWrapMode(wm);
                    else
                        Eiffel.Error("unknown wrap mode '" + wm + "'");
                }
                else if (args[ai].equals(HELP_ARG))
                {
                    help();
                    System.exit(0);
                }
                else
                    Eiffel.Error("unknown arg '" + args[ai] + "'");
            }
        }
        else
        {
            help();
            System.exit(0);
        }
    }

    private void help()
    {
        System.out.println("###############################################################################");
        System.out.println("#                                                                             #");
        System.out.println("# This tool generates syntax highlighted html files based on                  #");
        System.out.println("# the java files it reads:                                                    #");
        System.out.println("#                                                                             #");
        System.out.println("# SYNOPSIS: j2h [-ln] [-c 0 red] [-t \"  \"] [-wc 80] [-wm sharp] -f f1 .. fn   #");
        System.out.println("#                                                                             #");
        System.out.println("#   -f     : list of java files for which to generate html output,            #");
        System.out.println("#            must be the last parameter, whatever follows is taken to be      #");
        System.out.println("#            a Java source input file name.                                   #");
        System.out.println("#   -ln    : switches on the generating of line numbers                       #");
        System.out.println("#   -c     : specifies a color to use for a token class, the token class      #");
        System.out.println("#            number can be a value from 0 to 4. The numeric values map to     #");
        System.out.println("#            the following token classes: 0:plain, 1:keyword, 2:API class,    #");
        System.out.println("#            3:constant, 4:comment                                            #");
        System.out.println("#   -t     : specifies the substitution string for TAB characters in the      #");
        System.out.println("#            Java source code (default: 2 space characters)                   #");
        System.out.println("#   -wc    : specifies the wrap column for long lines (default: 80)           #");
        System.out.println("#   -wm    : specifies the wrap mode, can be one of [none, sharp, word]       #");
        System.out.println("#            (default: sharp)                                                 #");
        System.out.println("#                                                                             #");
        System.out.println("###############################################################################");
    }

    private String[] keyWords =
            {
                "abstract",
                "boolean",
                "break",
                "byte",
                "case",
                "catch",
                "char",
                "class",
                "const",
                "continue",
                "do",
                "double",
                "else",
                "extends",
                "final",
                "finally",
                "float",
                "for",
                "default",
                "implements",
                "import",
                "instanceof",
                "int",
                "interface",
                "long",
                "native",
                "new",
                "goto",
                "if",
                "public",
                "short",
                "super",
                "switch",
                "synchronized",
                "package",
                "private",
                "protected",
                "transient",
                "return",
                "void",
                "static",
                "while",
                "this",
                "throw",
                "throws",
                "try",
                "volatile",
                "strictfp",
                "true",
                "false",
                "null"
            };
    private String[] apiClasses =
            {
                "AbstractAction",
                "AbstractBorder",
                "AbstractButton",
                "AbstractCellEditor",
                "AbstractCollection",
                "AbstractColorChooserPanel",
                "AbstractDocument",
                "AbstractDocument.AttributeContext",
                "AbstractDocument.Content",
                "AbstractDocument.ElementEdit",
                "AbstractLayoutCache",
                "AbstractLayoutCache.NodeDimensions",
                "AbstractList",
                "AbstractListModel",
                "AbstractMap",
                "AbstractMethodError",
                "AbstractSequentialList",
                "AbstractSet",
                "AbstractTableModel",
                "AbstractUndoableEdit",
                "AbstractWriter",
                "AccessControlContext",
                "AccessControlException",
                "AccessController",
                "AccessException",
                "Accessible",
                "AccessibleAction",
                "AccessibleBundle",
                "AccessibleComponent",
                "AccessibleContext",
                "AccessibleHyperlink",
                "AccessibleHypertext",
                "AccessibleIcon",
                "AccessibleObject",
                "AccessibleRelation",
                "AccessibleRelationSet",
                "AccessibleResourceBundle",
                "AccessibleRole",
                "AccessibleSelection",
                "AccessibleState",
                "AccessibleStateSet",
                "AccessibleTable",
                "AccessibleTableModelChange",
                "AccessibleText",
                "AccessibleValue",
                "Acl",
                "AclEntry",
                "AclNotFoundException",
                "Action",
                "ActionEvent",
                "ActionListener",
                "ActionMap",
                "ActionMapUIResource",
                "Activatable",
                "ActivateFailedException",
                "ActivationDesc",
                "ActivationException",
                "ActivationGroup",
                "ActivationGroupDesc",
                "ActivationGroupDesc.CommandEnvironment",
                "ActivationGroupID",
                "ActivationID",
                "ActivationInstantiator",
                "ActivationMonitor",
                "ActivationSystem",
                "Activator",
                "ActiveEvent",
                "Adjustable",
                "AdjustmentEvent",
                "AdjustmentListener",
                "Adler32",
                "AffineTransform",
                "AffineTransformOp",
                "AlgorithmParameterGenerator",
                "AlgorithmParameterGeneratorSpi",
                "AlgorithmParameters",
                "AlgorithmParameterSpec",
                "AlgorithmParametersSpi",
                "AllPermission",
                "AlphaComposite",
                "AlreadyBound",
                "AlreadyBoundException",
                "AlreadyBoundHelper",
                "AlreadyBoundHolder",
                "AncestorEvent",
                "AncestorListener",
                "Annotation",
                "Any",
                "AnyHolder",
                "AnySeqHelper",
                "AnySeqHolder",
                "Applet",
                "AppletContext",
                "AppletInitializer",
                "AppletStub",
                "ApplicationException",
                "Arc2D",
                "Arc2D.Double",
                "Arc2D.Float",
                "Area",
                "AreaAveragingScaleFilter",
                "ARG_IN",
                "ARG_INOUT",
                "ARG_OUT",
                "ArithmeticException",
                "Array",
                "ArrayIndexOutOfBoundsException",
                "ArrayList",
                "Arrays",
                "ArrayStoreException",
                "AsyncBoxView",
                "Attribute",
                "AttributedCharacterIterator",
                "AttributedCharacterIterator.Attribute",
                "AttributedString",
                "AttributeInUseException",
                "AttributeList",
                "AttributeModificationException",
                "Attributes",
                "Attributes",
                "Attributes.Name",
                "AttributeSet",
                "AttributeSet.CharacterAttribute",
                "AttributeSet.ColorAttribute",
                "AttributeSet.FontAttribute",
                "AttributeSet.ParagraphAttribute",
                "AudioClip",
                "AudioFileFormat",
                "AudioFileFormat.Type",
                "AudioFileReader",
                "AudioFileWriter",
                "AudioFormat",
                "AudioFormat.Encoding",
                "AudioInputStream",
                "AudioPermission",
                "AudioSystem",
                "AuthenticationException",
                "AuthenticationNotSupportedException",
                "Authenticator",
                "Autoscroll",
                "AWTError",
                "AWTEvent",
                "AWTEventListener",
                "AWTEventMulticaster",
                "AWTException",
                "AWTPermission",
                "BAD_CONTEXT",
                "BAD_INV_ORDER",
                "BAD_OPERATION",
                "BAD_PARAM",
                "BAD_POLICY",
                "BAD_POLICY_TYPE",
                "BAD_POLICY_VALUE",
                "BAD_TYPECODE",
                "BadKind",
                "BadLocationException",
                "BandCombineOp",
                "BandedSampleModel",
                "BasicArrowButton",
                "BasicAttribute",
                "BasicAttributes",
                "BasicBorders",
                "BasicBorders.ButtonBorder",
                "BasicBorders.FieldBorder",
                "BasicBorders.MarginBorder",
                "BasicBorders.MenuBarBorder",
                "BasicBorders.RadioButtonBorder",
                "BasicBorders.SplitPaneBorder",
                "BasicBorders.ToggleButtonBorder",
                "BasicButtonListener",
                "BasicButtonUI",
                "BasicCheckBoxMenuItemUI",
                "BasicCheckBoxUI",
                "BasicColorChooserUI",
                "BasicComboBoxEditor",
                "BasicComboBoxEditor.UIResource",
                "BasicComboBoxRenderer",
                "BasicComboBoxRenderer.UIResource",
                "BasicComboBoxUI",
                "BasicComboPopup",
                "BasicDesktopIconUI",
                "BasicDesktopPaneUI",
                "BasicDirectoryModel",
                "BasicEditorPaneUI",
                "BasicFileChooserUI",
                "BasicGraphicsUtils",
                "BasicHTML",
                "BasicIconFactory",
                "BasicInternalFrameTitlePane",
                "BasicInternalFrameUI",
                "BasicLabelUI",
                "BasicListUI",
                "BasicLookAndFeel",
                "BasicMenuBarUI",
                "BasicMenuItemUI",
                "BasicMenuUI",
                "BasicOptionPaneUI",
                "BasicOptionPaneUI.ButtonAreaLayout",
                "BasicPanelUI",
                "BasicPasswordFieldUI",
                "BasicPermission",
                "BasicPopupMenuSeparatorUI",
                "BasicPopupMenuUI",
                "BasicProgressBarUI",
                "BasicRadioButtonMenuItemUI",
                "BasicRadioButtonUI",
                "BasicRootPaneUI",
                "BasicScrollBarUI",
                "BasicScrollPaneUI",
                "BasicSeparatorUI",
                "BasicSliderUI",
                "BasicSplitPaneDivider",
                "BasicSplitPaneUI",
                "BasicStroke",
                "BasicTabbedPaneUI",
                "BasicTableHeaderUI",
                "BasicTableUI",
                "BasicTextAreaUI",
                "BasicTextFieldUI",
                "BasicTextPaneUI",
                "BasicTextUI",
                "BasicTextUI.BasicCaret",
                "BasicTextUI.BasicHighlighter",
                "BasicToggleButtonUI",
                "BasicToolBarSeparatorUI",
                "BasicToolBarUI",
                "BasicToolTipUI",
                "BasicTreeUI",
                "BasicViewportUI",
                "BatchUpdateException",
                "BeanContext",
                "BeanContextChild",
                "BeanContextChildComponentProxy",
                "BeanContextChildSupport",
                "BeanContextContainerProxy",
                "BeanContextEvent",
                "BeanContextMembershipEvent",
                "BeanContextMembershipListener",
                "BeanContextProxy",
                "BeanContextServiceAvailableEvent",
                "BeanContextServiceProvider",
                "BeanContextServiceProviderBeanInfo",
                "BeanContextServiceRevokedEvent",
                "BeanContextServiceRevokedListener",
                "BeanContextServices",
                "BeanContextServicesListener",
                "BeanContextServicesSupport",
                "BeanContextServicesSupport.BCSSServiceProvider",
                "BeanContextSupport",
                "BeanContextSupport.BCSIterator",
                "BeanDescriptor",
                "BeanInfo",
                "Beans",
                "BevelBorder",
                "BigDecimal",
                "BigInteger",
                "BinaryRefAddr",
                "BindException",
                "Binding",
                "Binding",
                "BindingHelper",
                "BindingHolder",
                "BindingIterator",
                "BindingIteratorHelper",
                "BindingIteratorHolder",
                "BindingIteratorOperations",
                "BindingListHelper",
                "BindingListHolder",
                "BindingType",
                "BindingTypeHelper",
                "BindingTypeHolder",
                "BitSet",
                "Blob",
                "BlockView",
                "Book",
                "Boolean",
                "BooleanControl",
                "BooleanControl.Type",
                "BooleanHolder",
                "BooleanSeqHelper",
                "BooleanSeqHolder",
                "Border",
                "BorderFactory",
                "BorderLayout",
                "BorderUIResource",
                "BorderUIResource.BevelBorderUIResource",
                "BorderUIResource.CompoundBorderUIResource",
                "BorderUIResource.EmptyBorderUIResource",
                "BorderUIResource.EtchedBorderUIResource",
                "BorderUIResource.LineBorderUIResource",
                "BorderUIResource.MatteBorderUIResource",
                "BorderUIResource.TitledBorderUIResource",
                "BoundedRangeModel",
                "Bounds",
                "Box",
                "Box.Filler",
                "BoxedValueHelper",
                "BoxLayout",
                "BoxView",
                "BreakIterator",
                "BufferedImage",
                "BufferedImageFilter",
                "BufferedImageOp",
                "BufferedInputStream",
                "BufferedOutputStream",
                "BufferedReader",
                "BufferedWriter",
                "Button",
                "ButtonGroup",
                "ButtonModel",
                "ButtonUI",
                "Byte",
                "ByteArrayInputStream",
                "ByteArrayOutputStream",
                "ByteHolder",
                "ByteLookupTable",
                "Calendar",
                "CallableStatement",
                "CannotProceed",
                "CannotProceedException",
                "CannotProceedHelper",
                "CannotProceedHolder",
                "CannotRedoException",
                "CannotUndoException",
                "Canvas",
                "CardLayout",
                "Caret",
                "CaretEvent",
                "CaretListener",
                "CellEditor",
                "CellEditorListener",
                "CellRendererPane",
                "Certificate",
                "Certificate",
                "Certificate.CertificateRep",
                "CertificateEncodingException",
                "CertificateException",
                "CertificateExpiredException",
                "CertificateFactory",
                "CertificateFactorySpi",
                "CertificateNotYetValidException",
                "CertificateParsingException",
                "ChangedCharSetException",
                "ChangeEvent",
                "ChangeListener",
                "Character",
                "Character.Subset",
                "Character.UnicodeBlock",
                "CharacterIterator",
                "CharArrayReader",
                "CharArrayWriter",
                "CharConversionException",
                "CharHolder",
                "CharSeqHelper",
                "CharSeqHolder",
                "Checkbox",
                "CheckboxGroup",
                "CheckboxMenuItem",
                "CheckedInputStream",
                "CheckedOutputStream",
                "Checksum",
                "Choice",
                "ChoiceFormat",
                "Class",
                "ClassCastException",
                "ClassCircularityError",
                "ClassDesc",
                "ClassFormatError",
                "ClassLoader",
                "ClassNotFoundException",
                "Clip",
                "Clipboard",
                "ClipboardOwner",
                "Clob",
                "Cloneable",
                "CloneNotSupportedException",
                "CMMException",
                "CodeSource",
                "CollationElementIterator",
                "CollationKey",
                "Collator",
                "Collection",
                "Collections",
                "Color",
                "ColorChooserComponentFactory",
                "ColorChooserUI",
                "ColorConvertOp",
                "ColorModel",
                "ColorSelectionModel",
                "ColorSpace",
                "ColorUIResource",
                "ComboBoxEditor",
                "ComboBoxModel",
                "ComboBoxUI",
                "ComboPopup",
                "COMM_FAILURE",
                "CommunicationException",
                "Comparable",
                "Comparator",
                "Compiler",
                "CompletionStatus",
                "CompletionStatusHelper",
                "Component",
                "ComponentAdapter",
                "ComponentColorModel",
                "ComponentEvent",
                "ComponentInputMap",
                "ComponentInputMapUIResource",
                "ComponentListener",
                "ComponentOrientation",
                "ComponentSampleModel",
                "ComponentUI",
                "ComponentView",
                "Composite",
                "CompositeContext",
                "CompositeName",
                "CompositeView",
                "CompoundBorder",
                "CompoundControl",
                "CompoundControl.Type",
                "CompoundEdit",
                "CompoundName",
                "ConcurrentModificationException",
                "ConfigurationException",
                "ConnectException",
                "ConnectException",
                "ConnectIOException",
                "Connection",
                "Constructor",
                "Container",
                "ContainerAdapter",
                "ContainerEvent",
                "ContainerListener",
                "ContentHandler",
                "ContentHandlerFactory",
                "ContentModel",
                "Context",
                "Context",
                "ContextList",
                "ContextNotEmptyException",
                "ContextualRenderedImageFactory",
                "Control",
                "Control",
                "Control.Type",
                "ControlFactory",
                "ControllerEventListener",
                "ConvolveOp",
                "CRC32",
                "CRL",
                "CRLException",
                "CropImageFilter",
                "CSS",
                "CSS.Attribute",
                "CTX_RESTRICT_SCOPE",
                "CubicCurve2D",
                "CubicCurve2D.Double",
                "CubicCurve2D.Float",
                "Current",
                "CurrentHelper",
                "CurrentHolder",
                "CurrentOperations",
                "Cursor",
                "Customizer",
                "CustomMarshal",
                "CustomValue",
                "DATA_CONVERSION",
                "DatabaseMetaData",
                "DataBuffer",
                "DataBufferByte",
                "DataBufferInt",
                "DataBufferShort",
                "DataBufferUShort",
                "DataFlavor",
                "DataFormatException",
                "DatagramPacket",
                "DatagramSocket",
                "DatagramSocketImpl",
                "DatagramSocketImplFactory",
                "DataInput",
                "DataInputStream",
                "DataInputStream",
                "DataLine",
                "DataLine.Info",
                "DataOutput",
                "DataOutputStream",
                "DataOutputStream",
                "DataTruncation",
                "Date",
                "Date",
                "DateFormat",
                "DateFormatSymbols",
                "DebugGraphics",
                "DecimalFormat",
                "DecimalFormatSymbols",
                "DefaultBoundedRangeModel",
                "DefaultButtonModel",
                "DefaultCaret",
                "DefaultCellEditor",
                "DefaultColorSelectionModel",
                "DefaultComboBoxModel",
                "DefaultDesktopManager",
                "DefaultEditorKit",
                "DefaultEditorKit.BeepAction",
                "DefaultEditorKit.CopyAction",
                "DefaultEditorKit.CutAction",
                "DefaultEditorKit.DefaultKeyTypedAction",
                "DefaultEditorKit.InsertBreakAction",
                "DefaultEditorKit.InsertContentAction",
                "DefaultEditorKit.InsertTabAction",
                "DefaultEditorKit.PasteAction",
                "DefaultFocusManager",
                "DefaultHighlighter",
                "DefaultHighlighter.DefaultHighlightPainter",
                "DefaultListCellRenderer",
                "DefaultListCellRenderer.UIResource",
                "DefaultListModel",
                "DefaultListSelectionModel",
                "DefaultMenuLayout",
                "DefaultMetalTheme",
                "DefaultMutableTreeNode",
                "DefaultSingleSelectionModel",
                "DefaultStyledDocument",
                "DefaultStyledDocument.AttributeUndoableEdit",
                "DefaultStyledDocument.ElementSpec",
                "DefaultTableCellRenderer",
                "DefaultTableCellRenderer.UIResource",
                "DefaultTableColumnModel",
                "DefaultTableModel",
                "DefaultTextUI",
                "DefaultTreeCellEditor",
                "DefaultTreeCellRenderer",
                "DefaultTreeModel",
                "DefaultTreeSelectionModel",
                "DefinitionKind",
                "DefinitionKindHelper",
                "Deflater",
                "DeflaterOutputStream",
                "Delegate",
                "Delegate",
                "DesignMode",
                "DesktopIconUI",
                "DesktopManager",
                "DesktopPaneUI",
                "DGC",
                "Dialog",
                "Dictionary",
                "DigestException",
                "DigestInputStream",
                "DigestOutputStream",
                "Dimension",
                "Dimension2D",
                "DimensionUIResource",
                "DirContext",
                "DirectColorModel",
                "DirectoryManager",
                "DirObjectFactory",
                "DirStateFactory",
                "DirStateFactory.Result",
                "DnDConstants",
                "Document",
                "DocumentEvent",
                "DocumentEvent.ElementChange",
                "DocumentEvent.EventType",
                "DocumentListener",
                "DocumentParser",
                "DomainCombiner",
                "DomainManager",
                "DomainManagerOperations",
                "Double",
                "DoubleHolder",
                "DoubleSeqHelper",
                "DoubleSeqHolder",
                "DragGestureEvent",
                "DragGestureListener",
                "DragGestureRecognizer",
                "DragSource",
                "DragSourceContext",
                "DragSourceDragEvent",
                "DragSourceDropEvent",
                "DragSourceEvent",
                "DragSourceListener",
                "Driver",
                "DriverManager",
                "DriverPropertyInfo",
                "DropTarget",
                "DropTarget.DropTargetAutoScroller",
                "DropTargetContext",
                "DropTargetDragEvent",
                "DropTargetDropEvent",
                "DropTargetEvent",
                "DropTargetListener",
                "DSAKey",
                "DSAKeyPairGenerator",
                "DSAParameterSpec",
                "DSAParams",
                "DSAPrivateKey",
                "DSAPrivateKeySpec",
                "DSAPublicKey",
                "DSAPublicKeySpec",
                "DTD",
                "DTDConstants",
                "DynamicImplementation",
                "DynAny",
                "DynArray",
                "DynEnum",
                "DynFixed",
                "DynSequence",
                "DynStruct",
                "DynUnion",
                "DynValue",
                "EditorKit",
                "Element",
                "Element",
                "ElementIterator",
                "Ellipse2D",
                "Ellipse2D.Double",
                "Ellipse2D.Float",
                "EmptyBorder",
                "EmptyStackException",
                "EncodedKeySpec",
                "Entity",
                "EnumControl",
                "EnumControl.Type",
                "Enumeration",
                "Environment",
                "EOFException",
                "Error",
                "EtchedBorder",
                "Event",
                "EventContext",
                "EventDirContext",
                "EventListener",
                "EventListenerList",
                "EventObject",
                "EventQueue",
                "EventSetDescriptor",
                "Exception",
                "ExceptionInInitializerError",
                "ExceptionList",
                "ExpandVetoException",
                "ExportException",
                "ExtendedRequest",
                "ExtendedResponse",
                "Externalizable",
                "FeatureDescriptor",
                "Field",
                "FieldNameHelper",
                "FieldPosition",
                "FieldView",
                "File",
                "FileChooserUI",
                "FileDescriptor",
                "FileDialog",
                "FileFilter",
                "FileFilter",
                "FileInputStream",
                "FilenameFilter",
                "FileNameMap",
                "FileNotFoundException",
                "FileOutputStream",
                "FilePermission",
                "FileReader",
                "FileSystemView",
                "FileView",
                "FileWriter",
                "FilteredImageSource",
                "FilterInputStream",
                "FilterOutputStream",
                "FilterReader",
                "FilterWriter",
                "FixedHeightLayoutCache",
                "FixedHolder",
                "FlatteningPathIterator",
                "FlavorMap",
                "Float",
                "FloatControl",
                "FloatControl.Type",
                "FloatHolder",
                "FloatSeqHelper",
                "FloatSeqHolder",
                "FlowLayout",
                "FlowView",
                "FlowView.FlowStrategy",
                "FocusAdapter",
                "FocusEvent",
                "FocusListener",
                "FocusManager",
                "Font",
                "FontFormatException",
                "FontMetrics",
                "FontRenderContext",
                "FontUIResource",
                "Format",
                "FormatConversionProvider",
                "FormView",
                "Frame",
                "FREE_MEM",
                "GapContent",
                "GeneralPath",
                "GeneralSecurityException",
                "GlyphJustificationInfo",
                "GlyphMetrics",
                "GlyphVector",
                "GlyphView",
                "GlyphView.GlyphPainter",
                "GradientPaint",
                "GraphicAttribute",
                "Graphics",
                "Graphics2D",
                "GraphicsConfigTemplate",
                "GraphicsConfiguration",
                "GraphicsDevice",
                "GraphicsEnvironment",
                "GrayFilter",
                "GregorianCalendar",
                "GridBagConstraints",
                "GridBagLayout",
                "GridLayout",
                "Group",
                "Guard",
                "GuardedObject",
                "GZIPInputStream",
                "GZIPOutputStream",
                "HasControls",
                "HashMap",
                "HashSet",
                "Hashtable",
                "HierarchyBoundsAdapter",
                "HierarchyBoundsListener",
                "HierarchyEvent",
                "HierarchyListener",
                "Highlighter",
                "Highlighter.Highlight",
                "Highlighter.HighlightPainter",
                "HTML",
                "HTML.Attribute",
                "HTML.Tag",
                "HTML.UnknownTag",
                "HTMLDocument",
                "HTMLDocument.Iterator",
                "HTMLEditorKit",
                "HTMLEditorKit.HTMLFactory",
                "HTMLEditorKit.HTMLTextAction",
                "HTMLEditorKit.InsertHTMLTextAction",
                "HTMLEditorKit.LinkController",
                "HTMLEditorKit.Parser",
                "HTMLEditorKit.ParserCallback",
                "HTMLFrameHyperlinkEvent",
                "HTMLWriter",
                "HttpURLConnection",
                "HyperlinkEvent",
                "HyperlinkEvent.EventType",
                "HyperlinkListener",
                "ICC_ColorSpace",
                "ICC_Profile",
                "ICC_ProfileGray",
                "ICC_ProfileRGB",
                "Icon",
                "IconUIResource",
                "IconView",
                "IdentifierHelper",
                "Identity",
                "IdentityScope",
                "IDLEntity",
                "IDLType",
                "IDLTypeHelper",
                "IDLTypeOperations",
                "IllegalAccessError",
                "IllegalAccessException",
                "IllegalArgumentException",
                "IllegalComponentStateException",
                "IllegalMonitorStateException",
                "IllegalPathStateException",
                "IllegalStateException",
                "IllegalThreadStateException",
                "Image",
                "ImageConsumer",
                "ImageFilter",
                "ImageGraphicAttribute",
                "ImageIcon",
                "ImageObserver",
                "ImageProducer",
                "ImagingOpException",
                "IMP_LIMIT",
                "IncompatibleClassChangeError",
                "InconsistentTypeCode",
                "IndexColorModel",
                "IndexedPropertyDescriptor",
                "IndexOutOfBoundsException",
                "IndirectionException",
                "InetAddress",
                "Inflater",
                "InflaterInputStream",
                "InheritableThreadLocal",
                "InitialContext",
                "InitialContextFactory",
                "InitialContextFactoryBuilder",
                "InitialDirContext",
                "INITIALIZE",
                "Initializer",
                "InitialLdapContext",
                "InlineView",
                "InputContext",
                "InputEvent",
                "InputMap",
                "InputMapUIResource",
                "InputMethod",
                "InputMethodContext",
                "InputMethodDescriptor",
                "InputMethodEvent",
                "InputMethodHighlight",
                "InputMethodListener",
                "InputMethodRequests",
                "InputStream",
                "InputStream",
                "InputStream",
                "InputStreamReader",
                "InputSubset",
                "InputVerifier",
                "Insets",
                "InsetsUIResource",
                "InstantiationError",
                "InstantiationException",
                "Instrument",
                "InsufficientResourcesException",
                "Integer",
                "INTERNAL",
                "InternalError",
                "InternalFrameAdapter",
                "InternalFrameEvent",
                "InternalFrameListener",
                "InternalFrameUI",
                "InterruptedException",
                "InterruptedIOException",
                "InterruptedNamingException",
                "INTF_REPOS",
                "IntHolder",
                "IntrospectionException",
                "Introspector",
                "INV_FLAG",
                "INV_IDENT",
                "INV_OBJREF",
                "INV_POLICY",
                "Invalid",
                "INVALID_TRANSACTION",
                "InvalidAlgorithmParameterException",
                "InvalidAttributeIdentifierException",
                "InvalidAttributesException",
                "InvalidAttributeValueException",
                "InvalidClassException",
                "InvalidDnDOperationException",
                "InvalidKeyException",
                "InvalidKeySpecException",
                "InvalidMidiDataException",
                "InvalidName",
                "InvalidName",
                "InvalidNameException",
                "InvalidNameHelper",
                "InvalidNameHolder",
                "InvalidObjectException",
                "InvalidParameterException",
                "InvalidParameterSpecException",
                "InvalidSearchControlsException",
                "InvalidSearchFilterException",
                "InvalidSeq",
                "InvalidTransactionException",
                "InvalidValue",
                "InvocationEvent",
                "InvocationHandler",
                "InvocationTargetException",
                "InvokeHandler",
                "IOException",
                "IRObject",
                "IRObjectOperations",
                "IstringHelper",
                "ItemEvent",
                "ItemListener",
                "ItemSelectable",
                "Iterator",
                "JApplet",
                "JarEntry",
                "JarException",
                "JarFile",
                "JarInputStream",
                "JarOutputStream",
                "JarURLConnection",
                "JButton",
                "JCheckBox",
                "JCheckBoxMenuItem",
                "JColorChooser",
                "JComboBox",
                "JComboBox.KeySelectionManager",
                "JComponent",
                "JDesktopPane",
                "JDialog",
                "JEditorPane",
                "JFileChooser",
                "JFrame",
                "JInternalFrame",
                "JInternalFrame.JDesktopIcon",
                "JLabel",
                "JLayeredPane",
                "JList",
                "JMenu",
                "JMenuBar",
                "JMenuItem",
                "JobAttributes",
                "JobAttributes.DefaultSelectionType",
                "JobAttributes.DestinationType",
                "JobAttributes.DialogType",
                "JobAttributes.MultipleDocumentHandlingType",
                "JobAttributes.SidesType",
                "JOptionPane",
                "JPanel",
                "JPasswordField",
                "JPopupMenu",
                "JPopupMenu.Separator",
                "JProgressBar",
                "JRadioButton",
                "JRadioButtonMenuItem",
                "JRootPane",
                "JScrollBar",
                "JScrollPane",
                "JSeparator",
                "JSlider",
                "JSplitPane",
                "JTabbedPane",
                "JTable",
                "JTableHeader",
                "JTextArea",
                "JTextComponent",
                "JTextComponent.KeyBinding",
                "JTextField",
                "JTextPane",
                "JToggleButton",
                "JToggleButton.ToggleButtonModel",
                "JToolBar",
                "JToolBar.Separator",
                "JToolTip",
                "JTree",
                "JTree.DynamicUtilTreeNode",
                "JTree.EmptySelectionModel",
                "JViewport",
                "JWindow",
                "Kernel",
                "Key",
                "KeyAdapter",
                "KeyEvent",
                "KeyException",
                "KeyFactory",
                "KeyFactorySpi",
                "KeyListener",
                "KeyManagementException",
                "Keymap",
                "KeyPair",
                "KeyPairGenerator",
                "KeyPairGeneratorSpi",
                "KeySpec",
                "KeyStore",
                "KeyStoreException",
                "KeyStoreSpi",
                "KeyStroke",
                "Label",
                "LabelUI",
                "LabelView",
                "LastOwnerException",
                "LayeredHighlighter",
                "LayeredHighlighter.LayerPainter",
                "LayoutManager",
                "LayoutManager2",
                "LayoutQueue",
                "LdapContext",
                "LdapReferralException",
                "Lease",
                "LimitExceededException",
                "Line",
                "Line.Info",
                "Line2D",
                "Line2D.Double",
                "Line2D.Float",
                "LineBorder",
                "LineBreakMeasurer",
                "LineEvent",
                "LineEvent.Type",
                "LineListener",
                "LineMetrics",
                "LineNumberInputStream",
                "LineNumberReader",
                "LineUnavailableException",
                "LinkageError",
                "LinkedList",
                "LinkException",
                "LinkLoopException",
                "LinkRef",
                "List",
                "List",
                "ListCellRenderer",
                "ListDataEvent",
                "ListDataListener",
                "ListIterator",
                "ListModel",
                "ListResourceBundle",
                "ListSelectionEvent",
                "ListSelectionListener",
                "ListSelectionModel",
                "ListUI",
                "ListView",
                "LoaderHandler",
                "Locale",
                "LocateRegistry",
                "LogStream",
                "Long",
                "LongHolder",
                "LongLongSeqHelper",
                "LongLongSeqHolder",
                "LongSeqHelper",
                "LongSeqHolder",
                "LookAndFeel",
                "LookupOp",
                "LookupTable",
                "MalformedLinkException",
                "MalformedURLException",
                "Manifest",
                "Map",
                "Map.Entry",
                "MARSHAL",
                "MarshalException",
                "MarshalledObject",
                "Math",
                "MatteBorder",
                "MediaTracker",
                "Member",
                "MemoryImageSource",
                "Menu",
                "MenuBar",
                "MenuBarUI",
                "MenuComponent",
                "MenuContainer",
                "MenuDragMouseEvent",
                "MenuDragMouseListener",
                "MenuElement",
                "MenuEvent",
                "MenuItem",
                "MenuItemUI",
                "MenuKeyEvent",
                "MenuKeyListener",
                "MenuListener",
                "MenuSelectionManager",
                "MenuShortcut",
                "MessageDigest",
                "MessageDigestSpi",
                "MessageFormat",
                "MetaEventListener",
                "MetalBorders",
                "MetalBorders.ButtonBorder",
                "MetalBorders.Flush3DBorder",
                "MetalBorders.InternalFrameBorder",
                "MetalBorders.MenuBarBorder",
                "MetalBorders.MenuItemBorder",
                "MetalBorders.OptionDialogBorder",
                "MetalBorders.PaletteBorder",
                "MetalBorders.PopupMenuBorder",
                "MetalBorders.RolloverButtonBorder",
                "MetalBorders.ScrollPaneBorder",
                "MetalBorders.TableHeaderBorder",
                "MetalBorders.TextFieldBorder",
                "MetalBorders.ToggleButtonBorder",
                "MetalBorders.ToolBarBorder",
                "MetalButtonUI",
                "MetalCheckBoxIcon",
                "MetalCheckBoxUI",
                "MetalComboBoxButton",
                "MetalComboBoxEditor",
                "MetalComboBoxEditor.UIResource",
                "MetalComboBoxIcon",
                "MetalComboBoxUI",
                "MetalDesktopIconUI",
                "MetalFileChooserUI",
                "MetalIconFactory",
                "MetalIconFactory.FileIcon16",
                "MetalIconFactory.FolderIcon16",
                "MetalIconFactory.PaletteCloseIcon",
                "MetalIconFactory.TreeControlIcon",
                "MetalIconFactory.TreeFolderIcon",
                "MetalIconFactory.TreeLeafIcon",
                "MetalInternalFrameTitlePane",
                "MetalInternalFrameUI",
                "MetalLabelUI",
                "MetalLookAndFeel",
                "MetalPopupMenuSeparatorUI",
                "MetalProgressBarUI",
                "MetalRadioButtonUI",
                "MetalScrollBarUI",
                "MetalScrollButton",
                "MetalScrollPaneUI",
                "MetalSeparatorUI",
                "MetalSliderUI",
                "MetalSplitPaneUI",
                "MetalTabbedPaneUI",
                "MetalTextFieldUI",
                "MetalTheme",
                "MetalToggleButtonUI",
                "MetalToolBarUI",
                "MetalToolTipUI",
                "MetalTreeUI",
                "MetaMessage",
                "Method",
                "MethodDescriptor",
                "MidiChannel",
                "MidiDevice",
                "MidiDevice.Info",
                "MidiDeviceProvider",
                "MidiEvent",
                "MidiFileFormat",
                "MidiFileReader",
                "MidiFileWriter",
                "MidiMessage",
                "MidiSystem",
                "MidiUnavailableException",
                "MimeTypeParseException",
                "MinimalHTMLWriter",
                "MissingResourceException",
                "Mixer",
                "Mixer.Info",
                "MixerProvider",
                "ModificationItem",
                "Modifier",
                "MouseAdapter",
                "MouseDragGestureRecognizer",
                "MouseEvent",
                "MouseInputAdapter",
                "MouseInputListener",
                "MouseListener",
                "MouseMotionAdapter",
                "MouseMotionListener",
                "MultiButtonUI",
                "MulticastSocket",
                "MultiColorChooserUI",
                "MultiComboBoxUI",
                "MultiDesktopIconUI",
                "MultiDesktopPaneUI",
                "MultiFileChooserUI",
                "MultiInternalFrameUI",
                "MultiLabelUI",
                "MultiListUI",
                "MultiLookAndFeel",
                "MultiMenuBarUI",
                "MultiMenuItemUI",
                "MultiOptionPaneUI",
                "MultiPanelUI",
                "MultiPixelPackedSampleModel",
                "MultipleMaster",
                "MultiPopupMenuUI",
                "MultiProgressBarUI",
                "MultiScrollBarUI",
                "MultiScrollPaneUI",
                "MultiSeparatorUI",
                "MultiSliderUI",
                "MultiSplitPaneUI",
                "MultiTabbedPaneUI",
                "MultiTableHeaderUI",
                "MultiTableUI",
                "MultiTextUI",
                "MultiToolBarUI",
                "MultiToolTipUI",
                "MultiTreeUI",
                "MultiViewportUI",
                "MutableAttributeSet",
                "MutableComboBoxModel",
                "MutableTreeNode",
                "Name",
                "NameAlreadyBoundException",
                "NameClassPair",
                "NameComponent",
                "NameComponentHelper",
                "NameComponentHolder",
                "NamedValue",
                "NameHelper",
                "NameHolder",
                "NameNotFoundException",
                "NameParser",
                "NamespaceChangeListener",
                "NameValuePair",
                "NameValuePairHelper",
                "Naming",
                "NamingContext",
                "NamingContextHelper",
                "NamingContextHolder",
                "NamingContextOperations",
                "NamingEnumeration",
                "NamingEvent",
                "NamingException",
                "NamingExceptionEvent",
                "NamingListener",
                "NamingManager",
                "NamingSecurityException",
                "NegativeArraySizeException",
                "NetPermission",
                "NO_IMPLEMENT",
                "NO_MEMORY",
                "NO_PERMISSION",
                "NO_RESOURCES",
                "NO_RESPONSE",
                "NoClassDefFoundError",
                "NoInitialContextException",
                "NoninvertibleTransformException",
                "NoPermissionException",
                "NoRouteToHostException",
                "NoSuchAlgorithmException",
                "NoSuchAttributeException",
                "NoSuchElementException",
                "NoSuchFieldError",
                "NoSuchFieldException",
                "NoSuchMethodError",
                "NoSuchMethodException",
                "NoSuchObjectException",
                "NoSuchProviderException",
                "NotActiveException",
                "NotBoundException",
                "NotContextException",
                "NotEmpty",
                "NotEmptyHelper",
                "NotEmptyHolder",
                "NotFound",
                "NotFoundHelper",
                "NotFoundHolder",
                "NotFoundReason",
                "NotFoundReasonHelper",
                "NotFoundReasonHolder",
                "NotOwnerException",
                "NotSerializableException",
                "NullPointerException",
                "Number",
                "NumberFormat",
                "NumberFormatException",
                "NVList",
                "OBJ_ADAPTER",
                "Object",
                "Object",
                "OBJECT_NOT_EXIST",
                "ObjectChangeListener",
                "ObjectFactory",
                "ObjectFactoryBuilder",
                "ObjectHelper",
                "ObjectHolder",
                "ObjectImpl",
                "ObjectImpl",
                "ObjectInput",
                "ObjectInputStream",
                "ObjectInputStream.GetField",
                "ObjectInputValidation",
                "ObjectOutput",
                "ObjectOutputStream",
                "ObjectOutputStream.PutField",
                "ObjectStreamClass",
                "ObjectStreamConstants",
                "ObjectStreamException",
                "ObjectStreamField",
                "ObjectView",
                "ObjID",
                "Observable",
                "Observer",
                "OctetSeqHelper",
                "OctetSeqHolder",
                "OMGVMCID",
                "OpenType",
                "Operation",
                "OperationNotSupportedException",
                "Option",
                "OptionalDataException",
                "OptionPaneUI",
                "ORB",
                "ORB",
                "OutOfMemoryError",
                "OutputStream",
                "OutputStream",
                "OutputStream",
                "OutputStreamWriter",
                "OverlayLayout",
                "Owner",
                "Package",
                "PackedColorModel",
                "Pageable",
                "PageAttributes",
                "PageAttributes.ColorType",
                "PageAttributes.MediaType",
                "PageAttributes.OrientationRequestedType",
                "PageAttributes.OriginType",
                "PageAttributes.PrintQualityType",
                "PageFormat",
                "Paint",
                "PaintContext",
                "PaintEvent",
                "Panel",
                "PanelUI",
                "Paper",
                "ParagraphView",
                "ParagraphView",
                "ParameterBlock",
                "ParameterDescriptor",
                "ParseException",
                "ParsePosition",
                "Parser",
                "ParserDelegator",
                "PartialResultException",
                "PasswordAuthentication",
                "PasswordView",
                "Patch",
                "PathIterator",
                "Permission",
                "PermissionCollection",
                "Permissions",
                "PERSIST_STORE",
                "PhantomReference",
                "PipedInputStream",
                "PipedOutputStream",
                "PipedReader",
                "PipedWriter",
                "PixelGrabber",
                "PixelInterleavedSampleModel",
                "PKCS8EncodedKeySpec",
                "PlainDocument",
                "PlainView",
                "Point",
                "Point2D",
                "Point2D.Double",
                "Point2D.Float",
                "Policy",
                "PolicyError",
                "PolicyHelper",
                "PolicyHolder",
                "PolicyListHelper",
                "PolicyListHolder",
                "PolicyOperations",
                "PolicyTypeHelper",
                "Polygon",
                "PopupMenu",
                "PopupMenuEvent",
                "PopupMenuListener",
                "PopupMenuUI",
                "Port",
                "Port.Info",
                "PortableRemoteObject",
                "PortableRemoteObjectDelegate",
                "Position",
                "Position.Bias",
                "PreparedStatement",
                "Principal",
                "PrincipalHolder",
                "Printable",
                "PrinterAbortException",
                "PrinterException",
                "PrinterGraphics",
                "PrinterIOException",
                "PrinterJob",
                "PrintGraphics",
                "PrintJob",
                "PrintStream",
                "PrintWriter",
                "PRIVATE_MEMBER",
                "PrivateKey",
                "PrivilegedAction",
                "PrivilegedActionException",
                "PrivilegedExceptionAction",
                "Process",
                "ProfileDataException",
                "ProgressBarUI",
                "ProgressMonitor",
                "ProgressMonitorInputStream",
                "Properties",
                "PropertyChangeEvent",
                "PropertyChangeListener",
                "PropertyChangeSupport",
                "PropertyDescriptor",
                "PropertyEditor",
                "PropertyEditorManager",
                "PropertyEditorSupport",
                "PropertyPermission",
                "PropertyResourceBundle",
                "PropertyVetoException",
                "ProtectionDomain",
                "ProtocolException",
                "Provider",
                "ProviderException",
                "Proxy",
                "PUBLIC_MEMBER",
                "PublicKey",
                "PushbackInputStream",
                "PushbackReader",
                "QuadCurve2D",
                "QuadCurve2D.Double",
                "QuadCurve2D.Float",
                "Random",
                "RandomAccessFile",
                "Raster",
                "RasterFormatException",
                "RasterOp",
                "Reader",
                "Receiver",
                "Rectangle",
                "Rectangle2D",
                "Rectangle2D.Double",
                "Rectangle2D.Float",
                "RectangularShape",
                "Ref",
                "RefAddr",
                "Reference",
                "Reference",
                "Referenceable",
                "ReferenceQueue",
                "ReferralException",
                "ReflectPermission",
                "Registry",
                "RegistryHandler",
                "RemarshalException",
                "Remote",
                "RemoteCall",
                "RemoteException",
                "RemoteObject",
                "RemoteRef",
                "RemoteServer",
                "RemoteStub",
                "RenderableImage",
                "RenderableImageOp",
                "RenderableImageProducer",
                "RenderContext",
                "RenderedImage",
                "RenderedImageFactory",
                "Renderer",
                "RenderingHints",
                "RenderingHints.Key",
                "RepaintManager",
                "ReplicateScaleFilter",
                "Repository",
                "RepositoryIdHelper",
                "Request",
                "RescaleOp",
                "Resolver",
                "ResolveResult",
                "ResourceBundle",
                "ResponseHandler",
                "ResultSet",
                "ResultSetMetaData",
                "ReverbType",
                "RGBImageFilter",
                "RMIClassLoader",
                "RMIClientSocketFactory",
                "RMIFailureHandler",
                "RMISecurityException",
                "RMISecurityManager",
                "RMIServerSocketFactory",
                "RMISocketFactory",
                "Robot",
                "RootPaneContainer",
                "RootPaneUI",
                "RoundRectangle2D",
                "RoundRectangle2D.Double",
                "RoundRectangle2D.Float",
                "RowMapper",
                "RSAKey",
                "RSAKeyGenParameterSpec",
                "RSAPrivateCrtKey",
                "RSAPrivateCrtKeySpec",
                "RSAPrivateKey",
                "RSAPrivateKeySpec",
                "RSAPublicKey",
                "RSAPublicKeySpec",
                "RTFEditorKit",
                "RuleBasedCollator",
                "Runnable",
                "Runtime",
                "RunTime",
                "RuntimeException",
                "RunTimeOperations",
                "RuntimePermission",
                "SampleModel",
                "SchemaViolationException",
                "Scrollable",
                "Scrollbar",
                "ScrollBarUI",
                "ScrollPane",
                "ScrollPaneConstants",
                "ScrollPaneLayout",
                "ScrollPaneLayout.UIResource",
                "ScrollPaneUI",
                "SearchControls",
                "SearchResult",
                "SecureClassLoader",
                "SecureRandom",
                "SecureRandomSpi",
                "Security",
                "SecurityException",
                "SecurityManager",
                "SecurityPermission",
                "Segment",
                "SeparatorUI",
                "Sequence",
                "SequenceInputStream",
                "Sequencer",
                "Sequencer.SyncMode",
                "Serializable",
                "SerializablePermission",
                "ServantObject",
                "ServerCloneException",
                "ServerError",
                "ServerException",
                "ServerNotActiveException",
                "ServerRef",
                "ServerRequest",
                "ServerRuntimeException",
                "ServerSocket",
                "ServiceDetail",
                "ServiceDetailHelper",
                "ServiceInformation",
                "ServiceInformationHelper",
                "ServiceInformationHolder",
                "ServiceUnavailableException",
                "Set",
                "SetOverrideType",
                "SetOverrideTypeHelper",
                "Shape",
                "ShapeGraphicAttribute",
                "Short",
                "ShortHolder",
                "ShortLookupTable",
                "ShortMessage",
                "ShortSeqHelper",
                "ShortSeqHolder",
                "Signature",
                "SignatureException",
                "SignatureSpi",
                "SignedObject",
                "Signer",
                "SimpleAttributeSet",
                "SimpleBeanInfo",
                "SimpleDateFormat",
                "SimpleTimeZone",
                "SinglePixelPackedSampleModel",
                "SingleSelectionModel",
                "SizeLimitExceededException",
                "SizeRequirements",
                "SizeSequence",
                "Skeleton",
                "SkeletonMismatchException",
                "SkeletonNotFoundException",
                "SliderUI",
                "Socket",
                "SocketException",
                "SocketImpl",
                "SocketImplFactory",
                "SocketOptions",
                "SocketPermission",
                "SocketSecurityException",
                "SoftBevelBorder",
                "SoftReference",
                "SortedMap",
                "SortedSet",
                "Soundbank",
                "SoundbankReader",
                "SoundbankResource",
                "SourceDataLine",
                "SplitPaneUI",
                "SQLData",
                "SQLException",
                "SQLInput",
                "SQLOutput",
                "SQLPermission",
                "SQLWarning",
                "Stack",
                "StackOverflowError",
                "StateEdit",
                "StateEditable",
                "StateFactory",
                "Statement",
                "Streamable",
                "StreamableValue",
                "StreamCorruptedException",
                "StreamTokenizer",
                "StrictMath",
                "String",
                "StringBuffer",
                "StringBufferInputStream",
                "StringCharacterIterator",
                "StringContent",
                "StringHolder",
                "StringIndexOutOfBoundsException",
                "StringReader",
                "StringRefAddr",
                "StringSelection",
                "StringTokenizer",
                "StringValueHelper",
                "StringWriter",
                "Stroke",
                "Struct",
                "StructMember",
                "StructMemberHelper",
                "Stub",
                "StubDelegate",
                "StubNotFoundException",
                "Style",
                "StyleConstants",
                "StyleConstants.CharacterConstants",
                "StyleConstants.ColorConstants",
                "StyleConstants.FontConstants",
                "StyleConstants.ParagraphConstants",
                "StyleContext",
                "StyledDocument",
                "StyledEditorKit",
                "StyledEditorKit.AlignmentAction",
                "StyledEditorKit.BoldAction",
                "StyledEditorKit.FontFamilyAction",
                "StyledEditorKit.FontSizeAction",
                "StyledEditorKit.ForegroundAction",
                "StyledEditorKit.ItalicAction",
                "StyledEditorKit.StyledTextAction",
                "StyledEditorKit.UnderlineAction",
                "StyleSheet",
                "StyleSheet.BoxPainter",
                "StyleSheet.ListPainter",
                "SwingConstants",
                "SwingPropertyChangeSupport",
                "SwingUtilities",
                "SyncFailedException",
                "Synthesizer",
                "SysexMessage",
                "System",
                "SystemColor",
                "SystemException",
                "SystemFlavorMap",
                "TabableView",
                "TabbedPaneUI",
                "TabExpander",
                "TableCellEditor",
                "TableCellRenderer",
                "TableColumn",
                "TableColumnModel",
                "TableColumnModelEvent",
                "TableColumnModelListener",
                "TableHeaderUI",
                "TableModel",
                "TableModelEvent",
                "TableModelListener",
                "TableUI",
                "TableView",
                "TabSet",
                "TabStop",
                "TagElement",
                "TargetDataLine",
                "TCKind",
                "TextAction",
                "TextArea",
                "TextAttribute",
                "TextComponent",
                "TextEvent",
                "TextField",
                "TextHitInfo",
                "TextLayout",
                "TextLayout.CaretPolicy",
                "TextListener",
                "TextMeasurer",
                "TextUI",
                "TexturePaint",
                "Thread",
                "ThreadDeath",
                "ThreadGroup",
                "ThreadLocal",
                "Throwable",
                "Tie",
                "TileObserver",
                "Time",
                "TimeLimitExceededException",
                "Timer",
                "Timer",
                "TimerTask",
                "Timestamp",
                "TimeZone",
                "TitledBorder",
                "ToolBarUI",
                "Toolkit",
                "ToolTipManager",
                "ToolTipUI",
                "TooManyListenersException",
                "Track",
                "TRANSACTION_REQUIRED",
                "TRANSACTION_ROLLEDBACK",
                "TransactionRequiredException",
                "TransactionRolledbackException",
                "Transferable",
                "TransformAttribute",
                "TRANSIENT",
                "Transmitter",
                "Transparency",
                "TreeCellEditor",
                "TreeCellRenderer",
                "TreeExpansionEvent",
                "TreeExpansionListener",
                "TreeMap",
                "TreeModel",
                "TreeModelEvent",
                "TreeModelListener",
                "TreeNode",
                "TreePath",
                "TreeSelectionEvent",
                "TreeSelectionListener",
                "TreeSelectionModel",
                "TreeSet",
                "TreeUI",
                "TreeWillExpandListener",
                "TypeCode",
                "TypeCodeHolder",
                "TypeMismatch",
                "Types",
                "UID",
                "UIDefaults",
                "UIDefaults.ActiveValue",
                "UIDefaults.LazyInputMap",
                "UIDefaults.LazyValue",
                "UIDefaults.ProxyLazyValue",
                "UIManager",
                "UIManager.LookAndFeelInfo",
                "UIResource",
                "ULongLongSeqHelper",
                "ULongLongSeqHolder",
                "ULongSeqHelper",
                "ULongSeqHolder",
                "UndeclaredThrowableException",
                "UndoableEdit",
                "UndoableEditEvent",
                "UndoableEditListener",
                "UndoableEditSupport",
                "UndoManager",
                "UnexpectedException",
                "UnicastRemoteObject",
                "UnionMember",
                "UnionMemberHelper",
                "UNKNOWN",
                "UnknownError",
                "UnknownException",
                "UnknownGroupException",
                "UnknownHostException",
                "UnknownHostException",
                "UnknownObjectException",
                "UnknownServiceException",
                "UnknownUserException",
                "UnmarshalException",
                "UnrecoverableKeyException",
                "Unreferenced",
                "UnresolvedPermission",
                "UnsatisfiedLinkError",
                "UnsolicitedNotification",
                "UnsolicitedNotificationEvent",
                "UnsolicitedNotificationListener",
                "UNSUPPORTED_POLICY",
                "UNSUPPORTED_POLICY_VALUE",
                "UnsupportedAudioFileException",
                "UnsupportedClassVersionError",
                "UnsupportedEncodingException",
                "UnsupportedFlavorException",
                "UnsupportedLookAndFeelException",
                "UnsupportedOperationException",
                "URL",
                "URLClassLoader",
                "URLConnection",
                "URLDecoder",
                "URLEncoder",
                "URLStreamHandler",
                "URLStreamHandlerFactory",
                "UserException",
                "UShortSeqHelper",
                "UShortSeqHolder",
                "UTFDataFormatException",
                "Util",
                "UtilDelegate",
                "Utilities",
                "ValueBase",
                "ValueBaseHelper",
                "ValueBaseHolder",
                "ValueFactory",
                "ValueHandler",
                "ValueMember",
                "ValueMemberHelper",
                "VariableHeightLayoutCache",
                "Vector",
                "VerifyError",
                "VersionSpecHelper",
                "VetoableChangeListener",
                "VetoableChangeSupport",
                "View",
                "ViewFactory",
                "ViewportLayout",
                "ViewportUI",
                "VirtualMachineError",
                "Visibility",
                "VisibilityHelper",
                "VM_ABSTRACT",
                "VM_CUSTOM",
                "VM_NONE",
                "VM_TRUNCATABLE",
                "VMID",
                "VoiceStatus",
                "Void",
                "WCharSeqHelper",
                "WCharSeqHolder",
                "WeakHashMap",
                "WeakReference",
                "Window",
                "WindowAdapter",
                "WindowConstants",
                "WindowEvent",
                "WindowListener",
                "WrappedPlainView",
                "WritableRaster",
                "WritableRenderedImage",
                "WriteAbortedException",
                "Writer",
                "WrongTransaction",
                "WStringValueHelper",
                "X509Certificate",
                "X509CRL",
                "X509CRLEntry",
                "X509EncodedKeySpec",
                "X509Extension",
                "ZipEntry",
                "ZipException",
                "ZipFile",
                "ZipInputStream",
                "ZipOutputStream",
                "ZoneView",
                "_BindingIteratorImplBase",
                "_BindingIteratorStub",
                "_IDLTypeStub",
                "_NamingContextImplBase",
                "_NamingContextStub",
                "_PolicyStub",
                "_Remote_Stub"
            };
}

class ColorManager
{
    private int oldColor = TokenClass.lowerb;
    private int currentColor = TokenClass.lowerb;
    private Java2Html theClient;

    public ColorManager(Java2Html client)
    {
        Eiffel.Assert(client != null, "null client reference!");
        theClient = client;
    }

    public String openColor(int tc)
    {
        String htmlString = "";

        if (currentColor != TokenClass.lowerb)
        {
            if (currentColor == TokenClass.keyword)
                htmlString = "</b></font>";
            else
                htmlString = "</font>";
        }

        htmlString += "<font color=\"" + theClient.getColor(tc) + "\">";

        currentColor = tc;

        if (currentColor == TokenClass.keyword)
            htmlString += "<b>";

        return (htmlString);
    }

    public String closeColor()
    {
        String htmlString = "";

        if (currentColor != TokenClass.lowerb)
        {
            if (currentColor == TokenClass.keyword)
                htmlString = "</strong></font>";
            else
                htmlString = "</font>";

            currentColor = TokenClass.lowerb;
        }

        return (htmlString);
    }

    public String switchColor(int tc)
    {
        String htmlString = "";

        if (currentColor != TokenClass.lowerb)
        {
            oldColor = currentColor;

            if (oldColor == TokenClass.keyword)
                htmlString = "</strong></font>";
            else
                htmlString = "</font>";
        }
        else
            oldColor = TokenClass.lowerb;

        htmlString += "<font color=\"" + theClient.getColor(tc) + "\">";

        currentColor = tc;

        if (currentColor == TokenClass.keyword)
            htmlString += "<strong>";

        return (htmlString);
    }

    public String restoreColor()
    {
        String htmlString = "";

        if (currentColor != TokenClass.lowerb)
        {
            if (currentColor == TokenClass.keyword)
                htmlString = "</strong></font>";
            else
                htmlString = "</font>";
        }

        if (oldColor != TokenClass.lowerb)
            htmlString += "<font color=\"" + theClient.getColor(oldColor) + "\">";

        currentColor = oldColor;

        if (currentColor == TokenClass.keyword)
            htmlString += "<strong>";

        return (htmlString);
    }
}
