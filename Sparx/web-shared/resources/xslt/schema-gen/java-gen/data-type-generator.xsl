<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text"/>

<xsl:param name="package-name"/>
<xsl:param name="data-type-name"/>
<xsl:param name="java-type-init-cap"/>

<xsl:template match="datatype"><xsl:variable name="java-class-spec"><xsl:value-of select="java-class/@package"/>.<xsl:value-of select="java-class"/></xsl:variable>
package <xsl:value-of select="$package-name"/>;

import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.ParseException;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

import com.netspective.sparx.xif.dal.*;
import com.netspective.sparx.xif.dal.validation.UnknownValidationResultException;
import com.netspective.sparx.xif.dal.validation.result.*;
import com.netspective.sparx.xaf.form.DialogContext;

<xsl:if test="java-class and java-class/@package != 'java.lang' and java-class/@package != 'java.util'">import <xsl:value-of select="java-class/@package"/>;</xsl:if>

public class <xsl:value-of select="$data-type-name"/> extends AbstractColumn
{
<!--
	// Constants for each Validation Rule
<xsl:for-each select="validation"><xsl:for-each select="rule">
	public static final int RULE_<xsl:value-of select="@_gen-java-constant-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if> = <xsl:value-of select="position() - 1"/>;
</xsl:for-each></xsl:for-each>
-->
<xsl:for-each select="validation"><xsl:for-each select="rule">
	public static final String ruleName<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if> = &quot;<xsl:value-of select="@_gen-rule-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>&quot;;
</xsl:for-each></xsl:for-each>
<xsl:if test="java-date-format-instance">	private static DateFormat defaultDateFormat = <xsl:value-of select="java-date-format-instance"/>;
	private DateFormat dateFormat = defaultDateFormat;
</xsl:if>
	private <xsl:value-of select="$java-class-spec"/> defaultValue;

	// Instantiate the PatternCompiler here so there is less cost incurred at runtime
	private static PatternCompiler patternCompiler = new Perl5Compiler();

	// Instantiate the PatternMatchers - One for each pattern to ensure Thread-safety<xsl:for-each select="validation"><xsl:for-each select="rule"><xsl:if test="@type = 'regex' or @type = 'identifier'">
	private static PatternMatcher matcher<xsl:value-of select="@_gen-java-identifier-name"/> = new Perl5Matcher();
</xsl:if></xsl:for-each></xsl:for-each>
	// Declare all patterns as Patterns - For one time compiles to improve performance at runtime<xsl:for-each select="validation"><xsl:for-each select="rule"><xsl:if test="@type='regex' or @type = 'identifier'">
	private static Pattern pattern<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if> = null;
</xsl:if></xsl:for-each></xsl:for-each>
	// Declare all patterns as Strings - To allow returning the exact regex's to a user if needed<xsl:for-each select="validation"><xsl:for-each select="rule"><xsl:if test="@type='regex' or @type = 'identifier'">
	private static String patternString<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if> = &quot;<xsl:value-of select="."/>&quot;;
</xsl:if></xsl:for-each></xsl:for-each>
	static
	{
		// Initialize all patterns
<xsl:for-each select="validation"><xsl:for-each select="rule"><xsl:if test="@type='regex' or @type = 'identifier'">
		try
		{
			pattern<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if> = patternCompiler.compile(patternString<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>);
		}
		catch (MalformedPatternException mpe)
		{
			pattern<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if> = null;
		}
</xsl:if></xsl:for-each></xsl:for-each>
	}

	public <xsl:value-of select="$data-type-name"/>(Table table, String name)
	{
		super(table, name);
<xsl:for-each select="sqldefn">
		setSqlDefn(&quot;<xsl:value-of select="@dbms"/>&quot;, &quot;<xsl:value-of select="."/>&quot;);
</xsl:for-each>
<xsl:if test="java-class">		setDataClassName(&quot;<xsl:value-of select="$java-class-spec"/>&quot;);
</xsl:if>
<xsl:for-each select="default">
		setDefaultSqlExprValue(&quot;<xsl:value-of select="@dbms"/>&quot;, &quot;<xsl:value-of select="."/>&quot;);
</xsl:for-each>
<xsl:if test="java-default">		setDefaultValue(ValueSourceFactory.getSingleOrStaticValueSource(&quot;<xsl:value-of select="java-default"/>&quot;));
</xsl:if>
<xsl:if test="size">		setSize(<xsl:value-of select="size"/>);
</xsl:if>	}
<!--************************************************************************-->
<!-- Add Validation Code ***************************************************-->
<!--************************************************************************-->
	public BasicDataValidationResult getValidationResult(<xsl:value-of select="$java-class-spec"/> value)
	{
		boolean status = true;
		BasicDataValidationResult bdvResult = new BasicDataValidationResult(this.getName());
		int ruleNum = 0;
<xsl:for-each select="validation">
<xsl:for-each select="rule">
<xsl:variable name="validation-_gen-rule-name"><xsl:choose><xsl:when test="@name">&quot;<xsl:value-of select="@name"/>&quot;</xsl:when><xsl:otherwise>&quot;Rule #&quot; + ruleNum</xsl:otherwise></xsl:choose></xsl:variable>
<xsl:variable name="success-message"><xsl:choose><xsl:when test="@success-message">&quot;<xsl:value-of select="@success-message"/>&quot;</xsl:when><xsl:otherwise>&quot;Success!&quot;</xsl:otherwise></xsl:choose></xsl:variable>
<xsl:variable name="success-message-or-null"><xsl:choose><xsl:when test="@success-message">&quot;<xsl:value-of select="@success-message"/>&quot;</xsl:when><xsl:otherwise>&quot;Value is null and not required&quot;</xsl:otherwise></xsl:choose></xsl:variable>
<xsl:variable name="failure-message"><xsl:choose><xsl:when test="@failure-message">&quot;<xsl:value-of select="@failure-message"/>&quot;</xsl:when><xsl:otherwise>&quot;Failure!&quot;</xsl:otherwise></xsl:choose></xsl:variable>
		{

			// Set status to true at the beginning of this validation rule
			status = true;
	<xsl:choose>
		<xsl:when test="@type = 'java'">
			// Validation Rule: Java
			<xsl:value-of select="."/>
		</xsl:when>
		<xsl:when test="@type = 'regex'">
			// Validation Rule: Regex
			if (null == value)
			{
				bdvResult.addResultInfo (ruleName<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>, true, "Value is null and not required!");
			} else {
				if (null == pattern<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>)
				{
					status = false;
				}
				else
				{
					status = matcher<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>.matches (value.toString(), pattern<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>);
				}
		</xsl:when>
		<xsl:when test="@type = 'min'">
			// Validation Rule: Numeric Min
			if (null == value)
			{
				bdvResult.addResultInfo (ruleName<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>, true, "Value is null and not required!");
			} else {
				Class className = value.getClass();
				Class[] interfaces = className.getInterfaces();
				boolean isComparable = false;

				for (int i = 0; i &lt; interfaces.length &amp;&amp; ! isComparable; i ++)
				{
					if (interfaces[i].getName().equals("Ljava.lang.Comparable;"))
					{
						isComparable = true;
					}
				}

				if (isComparable)
				{
					int comparison = value.compareTo (new <xsl:value-of select="$java-class-spec"/> (<xsl:value-of select="."/>));

					if (comparison &lt; 0)
					{
						status = false;
					}
				}
		</xsl:when>
		<xsl:when test="@type = 'max'">
			// Validation Rule: Numeric Max
			if (null == value)
			{
				bdvResult.addResultInfo (ruleName<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>, true, "Value is null and not required!");
			} else {
				Class className = value.getClass();
				Class[] interfaces = className.getInterfaces();
				boolean isComparable = false;

				for (int i = 0; i &lt; interfaces.length &amp;&amp; ! isComparable; i ++)
				{
					if (interfaces[i].getName().equals("Ljava.lang.Comparable;"))
					{
						isComparable = true;
					}
				}

				if (isComparable)
				{
					int comparison = value.compareTo (new <xsl:value-of select="$java-class-spec"/> (<xsl:value-of select="."/>));

					if (comparison &gt; 0)
					{
						// The value of this column is more than the maximum specified
						status = false;
					}
				}
		</xsl:when>
		<xsl:when test="@type = 'min-length'">
			// Validation Rule: String Length Min
			if (null == value)
			{
				bdvResult.addResultInfo (ruleName<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>, true, "Value is null and not required!");
			} else {
				String className = value.getClass().getName();

				if (className.equals("Ljava.lang.String;"))
				{
					if (<xsl:value-of select="."/> &gt; value.length())
					{
						status = false;
					}
				}
		</xsl:when>
		<xsl:when test="@type = 'max-length'">
			// Validation Rule: String Length Max
			if (null == value)
			{
				bdvResult.addResultInfo (ruleName<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>, true, "Value is null and not required!");
			} else {
				String className = value.getClass().getName();

				if (className.equals("Ljava.lang.String;"))
				{
					if (<xsl:value-of select="."/> &lt; value.length())
					{
						// The length of this column's data is more than the maximum specified
						status = false;
					}
				}
		</xsl:when>
		<xsl:when test="@type = 'identifier'">
			// Validation Rule: Identifier
			if (null == value)
			{
				bdvResult.addResultInfo (ruleName<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>, true, "Value is null and not required!");
			} else {
				if (null == pattern<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>)
				{
					status = false;
				} else {
					status = matcher<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>.matches (value.toString(), pattern<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>);
				}
		</xsl:when>
		<xsl:when test="@type = 'required'">
			// Validation Rule: Required
			<xsl:choose>
				<xsl:when test=". = 'yes'">
			if (isRequired() &amp;&amp; null == value)
			{
				bdvResult.addResultInfo (ruleName<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>, false, <xsl:value-of select="$failure-message"/>);
			} else {
				</xsl:when>
				<xsl:otherwise>
			{
				status = true;
				</xsl:otherwise></xsl:choose>
		</xsl:when></xsl:choose>

				// Test the status at the end of this rule and create an appropriate entry in
				// the BasicDataValidationResult object
				if (null == bdvResult) throw new NullPointerException ("Could not initialize BasicDataValidationResult");

<xsl:choose><xsl:when test="@type = 'regex' or @type = 'identifier'">
				if (null == pattern<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>)
				{
					bdvResult.addResultInfo (ruleName<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>, status, super.INVALID_REGEX);
				}
				else
				{
					bdvResult.addResultInfo (ruleName<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>, status, status ? <xsl:value-of select="$success-message"/> : <xsl:value-of select="$failure-message"/>);
				}</xsl:when><xsl:otherwise>
				bdvResult.addResultInfo (ruleName<xsl:value-of select="@_gen-java-identifier-name"/><xsl:if test="@_gen-java-constant-name = 'NUMBER_'"><xsl:value-of select="position() - 1"/></xsl:if>, status, status ? <xsl:value-of select="$success-message"/> : <xsl:value-of select="$failure-message"/>);</xsl:otherwise></xsl:choose>
			}
		}
</xsl:for-each>
</xsl:for-each>

		return bdvResult;
	}

	public boolean isValid(<xsl:value-of select="$java-class-spec"/> value)
	{
		boolean status = true;
		BasicDataValidationResult bdvResult = getValidationResult(value);

		if (null == bdvResult) throw new UnknownValidationResultException();

		return bdvResult.isValid();
	}

<!--************************************************************************-->
<!-- End Validation Code ***************************************************-->
<!--************************************************************************-->
	public <xsl:value-of select="$java-class-spec"/> getDefaultValue() { return defaultValue; }
	public void setDefaultValue(<xsl:value-of select="$java-class-spec"/> value) { defaultValue = value; }
<xsl:choose>
<xsl:when test="java-date-format-instance">
	public static DateFormat getDefaultDateFormat() { return defaultDateFormat; }
	public DateFormat getDateFormat() { return dateFormat; }
	public void setDateFormat(DateFormat value) { value = dateFormat; }
	public <xsl:value-of select="$java-class-spec"/> parse(String text) throws ParseException { return dateFormat.parse(text); }
	public String format(<xsl:value-of select="$java-class-spec"/> value) { return value != null ? dateFormat.format(value) : null; }
	public String format(DialogContext dc, <xsl:value-of select="$java-class-spec"/> value) { return value != null ? dateFormat.format(value) : null; }
    public Object getValueForSqlBindParam(<xsl:value-of select="$java-class-spec"/> value) { return value != null ? new java.sql.Date(value.getTime()) : null; }
</xsl:when>
<xsl:when test="java-type">
	public <xsl:value-of select="$java-class-spec"/> parse(String text) { return new <xsl:value-of select="$java-class-spec"/>(text); }
	public String format(<xsl:value-of select="$java-class-spec"/> value) { return value != null ? value.toString() : null; }
	public String format(DialogContext dc, <xsl:value-of select="$java-class-spec"/> value) { return value != null ? value.toString() : null; }
    public Object getValueForSqlBindParam(<xsl:value-of select="$java-class-spec"/> value) { return value; }
</xsl:when>
<xsl:otherwise>
	public <xsl:value-of select="$java-class-spec"/> parse(String text) { return text; }
	public String format(<xsl:value-of select="$java-class-spec"/> value) { return value != null ? value.toString() : null; }
	public String format(DialogContext dc, <xsl:value-of select="$java-class-spec"/> value) { return value != null ? value.toString() : null; }
  public Object getValueForSqlBindParam(<xsl:value-of select="$java-class-spec"/> value) { return value; }
</xsl:otherwise>
</xsl:choose>
}
</xsl:template>
</xsl:stylesheet>
