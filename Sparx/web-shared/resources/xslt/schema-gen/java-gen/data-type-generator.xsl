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

<xsl:for-each select="validate[not(@_gen-is-duplicate)]/import-java-code">
    <xsl:value-of select="final-code"/>
</xsl:for-each>

import com.netspective.sparx.xif.dal.*;
import com.netspective.sparx.xif.dal.validation.UnknownValidationResultException;
import com.netspective.sparx.xif.dal.validation.result.*;
import com.netspective.sparx.xaf.form.DialogContext;

<xsl:if test="java-class and java-class/@package != 'java.lang' and java-class/@package != 'java.util'">import <xsl:value-of select="java-class/@package"/>.*;</xsl:if>

public class <xsl:value-of select="$data-type-name"/> extends AbstractColumn
{
<xsl:if test="java-date-format-instance">	private static DateFormat defaultDateFormat = <xsl:value-of select="java-date-format-instance"/>;
	private DateFormat dateFormat = defaultDateFormat;
</xsl:if>
	private <xsl:value-of select="$java-class-spec"/> defaultValue;
    private static String RULENAME_REQUIRED = "required";

    <xsl:for-each select="validate[not(@_gen-is-duplicate)]/declare-java-code[@type='singleton' and not(@_gen-is-duplicate)]">
        <xsl:value-of select="final-code"/>
    </xsl:for-each>

    <xsl:for-each select="validate[not(@_gen-is-duplicate)]/declare-java-code[not(@type)]">
        <xsl:value-of select="final-code"/>
    </xsl:for-each>

    <xsl:if test="validate[not(@_gen-is-duplicate)]/static-java-code">
    static
    {
    <xsl:for-each select="validate[not(@_gen-is-duplicate)]/static-java-code">
        <xsl:value-of select="final-code"/>
    </xsl:for-each>
    }
    </xsl:if>

	public <xsl:value-of select="$data-type-name"/>(Table table, String name, String dialogFieldName, String xmlNodeName, String servletReqParamName, String servletReqAttrName)
	{
		super(table, name, dialogFieldName, xmlNodeName, servletReqParamName, servletReqAttrName);
<xsl:for-each select="sqldefn">
		setSqlDefn(&quot;<xsl:value-of select="@dbms"/>&quot;, &quot;<xsl:value-of select="."/>&quot;);
</xsl:for-each>
<xsl:if test="java-class">		setDataClassName(&quot;<xsl:value-of select="$java-class-spec"/>&quot;);
</xsl:if>
<xsl:for-each select="default">
		setDefaultSqlExprValue(&quot;<xsl:value-of select="@dbms"/>&quot;, &quot;<xsl:value-of select="."/>&quot;);
</xsl:for-each>
<xsl:if test="size">		setSize(<xsl:value-of select="size"/>);
</xsl:if>	}

    public DataValidationResult getValidationResult(Object _value)
    {
        <xsl:value-of select="$java-class-spec"/> value = (<xsl:value-of select="$java-class-spec"/>) _value;
        BasicDataValidationResult bdvResult = new BasicDataValidationResult(this.getName());

        if(isRequired() &amp;&amp; value == null)
        {
            bdvResult.addResultInfo(RULENAME_REQUIRED, false, getName() + " is required but has no value.");
            return bdvResult;
        }

    <xsl:for-each select="validate[not(@_gen-is-duplicate) and (@event='universal' or not(@event))]/java-code">
        <xsl:value-of select="final-code"/>
    </xsl:for-each>

        return bdvResult;
    }

    public DataValidationResult getInsertValidationResult(Object _value)
    {
        <xsl:value-of select="$java-class-spec"/> value = (<xsl:value-of select="$java-class-spec"/>) _value;
        BasicDataValidationResult bdvResult = new BasicDataValidationResult(this.getName());

        if(isRequired() &amp;&amp; value == null)
        {
            bdvResult.addResultInfo(RULENAME_REQUIRED, false, getName() + " is required but has no value.");
            return bdvResult;
        }

    <xsl:for-each select="validate[not(@_gen-is-duplicate) and @event='insert']/java-code">
        <xsl:value-of select="final-code"/>
    </xsl:for-each>

        return bdvResult;
    }

    public DataValidationResult getUpdateValidationResult(Object _value)
    {
        <xsl:value-of select="$java-class-spec"/> value = (<xsl:value-of select="$java-class-spec"/>) _value;
        BasicDataValidationResult bdvResult = new BasicDataValidationResult(this.getName());

        if(isRequired() &amp;&amp; value == null)
        {
            bdvResult.addResultInfo(RULENAME_REQUIRED, false, getName() + " is required but has no value.");
            return bdvResult;
        }

    <xsl:for-each select="validate[not(@_gen-is-duplicate) and @event='update']/java-code">
        <xsl:value-of select="final-code"/>
    </xsl:for-each>

        return bdvResult;
    }

    public DataValidationResult getDeleteValidationResult(Object _value)
    {
        <xsl:value-of select="$java-class-spec"/> value = (<xsl:value-of select="$java-class-spec"/>) _value;
        BasicDataValidationResult bdvResult = new BasicDataValidationResult(this.getName());

        if(isRequired() &amp;&amp; value == null)
        {
            bdvResult.addResultInfo(RULENAME_REQUIRED, false, getName() + " is required but has no value.");
            return bdvResult;
        }

    <xsl:for-each select="validate[not(@_gen-is-duplicate) and @event='delete']/java-code">
        <xsl:value-of select="final-code"/>
    </xsl:for-each>

        return bdvResult;
    }

/*
	public BasicDataValidationResult getValidationResult(<xsl:value-of select="$java-class-spec"/> value)
	{
		BasicDataValidationResult bdvResult = new BasicDataValidationResult(this.getName());

        if(isRequired() &amp;&amp; value == null)
        {
            bdvResult.addResultInfo(RULENAME_REQUIRED, false, getName() + " is required but has no value.");
            return bdvResult;
        }

    <xsl:for-each select="validate[not(@_gen-is-duplicate)]/java-code">
        <xsl:value-of select="final-code"/>
    </xsl:for-each>

		return bdvResult;
	}
*/

	public boolean isValid(<xsl:value-of select="$java-class-spec"/> value)
	{
		return getValidationResult(value).isValid();
	}

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
	public Object getValueForSqlBindParam(Object value) { return getValueForSqlBindParam((<xsl:value-of select="$java-class-spec"/>) value); }
  public Object getValueForSqlBindParam(<xsl:value-of select="$java-class-spec"/> value) { return value != null ? new <xsl:value-of select="java-sql-class"/>(value.getTime()) : null; }
</xsl:when>
<xsl:when test="java-type">
	public <xsl:value-of select="$java-class-spec"/> parse(String text) { return new <xsl:value-of select="$java-class-spec"/>(text); }
	public String format(<xsl:value-of select="$java-class-spec"/> value) { return value != null ? value.toString() : null; }
	public String format(DialogContext dc, <xsl:value-of select="$java-class-spec"/> value) { return value != null ? value.toString() : null; }
    public Object getValueForSqlBindParam(Object value) { return getValueForSqlBindParam((<xsl:value-of select="$java-class-spec"/>) value); }
    public Object getValueForSqlBindParam(<xsl:value-of select="$java-class-spec"/> value) { return value; }
</xsl:when>
<xsl:otherwise>
	public <xsl:value-of select="$java-class-spec"/> parse(String text) { return text; }
	public String format(<xsl:value-of select="$java-class-spec"/> value) { return value != null ? value.toString() : null; }
	public String format(DialogContext dc, <xsl:value-of select="$java-class-spec"/> value) { return value != null ? value.toString() : null; }
    public Object getValueForSqlBindParam(<xsl:value-of select="$java-class-spec"/> value) { return value; }
    <xsl:if test="$java-class-spec != 'java.lang.Object'">
    public Object getValueForSqlBindParam(Object value) { return getValueForSqlBindParam((<xsl:value-of select="$java-class-spec"/>) value); }
    </xsl:if>
</xsl:otherwise>
</xsl:choose>
}
</xsl:template>
</xsl:stylesheet>
