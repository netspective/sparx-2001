package com.xaf.value;

import java.util.*;

public class ValueSourceFactory
{
	private static Hashtable srcClasses = new Hashtable();
	private static Hashtable srcInstances = new Hashtable();
	private static boolean defaultsAvailable = false;

    public static void setupDefaults()
    {
        srcClasses.put("form", DialogFieldValue.class);
		srcClasses.put("formOrRequest", DialogFieldOrRequestParameterValue.class);
        srcClasses.put("query", QueryResultsListValue.class);
		srcClasses.put("dialog-field-types", DialogFieldFactoryListValue.class);
        srcClasses.put("query-defn-fields", QueryDefnFieldsListValue.class);
		srcClasses.put("query-defn-selects", QueryDefnSelectsListValue.class);
        srcClasses.put("sql-comparisons", SqlComparisonsListValue.class);
        srcClasses.put("strings", StringsListValue.class);
		srcClasses.put("string", StaticValue.class);
		srcClasses.put("static", StaticValue.class);
		srcClasses.put("request", RequestParameterValue.class);
		srcClasses.put("request-param", RequestParameterValue.class);
		srcClasses.put("request-attr", RequestAttributeValue.class);
		srcClasses.put("session", SessionAttributeValue.class);
		srcClasses.put("schema-tables", SchemaDocTablesListValue.class);
        srcClasses.put("generate-id", GenerateIdValue.class);
        srcClasses.put("system-property", SystemPropertyValue.class);
        defaultsAvailable = true;
    }

	public static SingleValueSource getSingleValueSource(String source)
	{
        SingleValueSource vs = (SingleValueSource) srcInstances.get(source);
        if(vs != null)
            return vs;

		if(! defaultsAvailable) setupDefaults();

        int delimPos = source.indexOf(':');
        if(delimPos < 0)
            return null;

        String srcType = source.substring(0, delimPos);
		Class vsClass = (Class) srcClasses.get(srcType);
        if(vsClass == null)
        {
            return new StaticValue("Value source '"+ srcType +"' class not found.");
        }
        else
        {
            try
            {
                vs = (SingleValueSource) vsClass.newInstance();
                vs.initializeSource(source.substring(delimPos+1));
                srcInstances.put(source, vs);
            }
            catch(Exception e)
            {
                return new StaticValue(e.toString());
            }

            return vs;
        }
	}

	public static SingleValueSource getStoreValueSource(String source)
	{
		return getSingleValueSource(source);
	}

	public static SingleValueSource getSingleOrStaticValueSource(String source)
	{
		SingleValueSource result = getSingleValueSource(source);
		if(result == null)
			result = new StaticValue(source);
		return result;
	}

	public static ListValueSource getListValueSource(String source)
	{
        ListValueSource vs = (ListValueSource) srcInstances.get(source);
        if(vs != null)
            return vs;

		if(! defaultsAvailable) setupDefaults();

        int delimPos = source.indexOf(':');
        if(delimPos < 0)
            return null;

        String srcType = source.substring(0, delimPos);
		Class vsClass = (Class) srcClasses.get(srcType);
        if(vsClass == null)
        {
            return new ErrorListSource("List source '"+ srcType +"' class not found.");
        }
        else
        {
            try
            {
                vs = (ListValueSource) vsClass.newInstance();
                vs.initializeSource(source.substring(delimPos+1));
                srcInstances.put(source, vs);
            }
            catch(Exception e)
            {
                return new ErrorListSource(e);
            }

            return vs;
        }
	}
}