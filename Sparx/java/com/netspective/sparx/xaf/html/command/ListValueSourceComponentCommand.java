package com.netspective.sparx.xaf.html.command;

import com.netspective.sparx.util.value.ListValueSource;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.xaf.html.ComponentCommandException;
import com.netspective.sparx.xaf.skin.SkinFactory;

import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.Writer;

public class ListValueSourceComponentCommand extends AbstractComponentCommand
{
    static public final String COMMAND_ID = "lvs";
    static public final String LVSTYPENAME_REFERENCE = "reference";
    static public final String LVSTYPENAME_INSTANCE  = "instance";
    static public final int LVSTYPE_UNKNOWN = 0;
    static public final int LVSTYPE_REFERENCE = 1;
    static public final int LVSTYPE_INSTANCE  = 2;

    static public final Documentation DOCUMENTATION = new Documentation(
                "Displays the contents of a ListValueSource.",
                new Documentation.Parameter[]
                    {
                        new Documentation.Parameter("value-source-type", true, new String[] { LVSTYPENAME_REFERENCE, LVSTYPENAME_INSTANCE }, LVSTYPENAME_INSTANCE,
                                "The value-source-type parameter may be either 'reference' or 'instance'. When it is set " +
                                "to reference, the value-source-spec parameter is basically the value-source spefication like xxx:yyy. " +
                                "When the value-source-type parameter is set to 'reference' it means that the value-source-spec is actually " +
                                "a single value source that points to an actual ListValueSource at runtime)."),
                        new Documentation.Parameter("value-source-spec", true, null, null, "The value source specification (depends upon value-source-type)."),
                        new Documentation.Parameter("is-popup", false, new String[] { "yes", "popup", "no" }, "popup", "Declares whether or not the lvs command is being used in a popup window."),
                        new Documentation.Parameter("url-formats", false, null, null, "The urlFormats parameter is one or more "+
                                "comma-separated URL formats that may override those within a report."),
                        new StatementComponentCommand.SkinParameter()
                    });

    private int valueSourceType;
    private String valueSourceSpec;
    private SingleValueSource valueSourceReference;
    private ListValueSource valueSourceInstance;
    private boolean isPopup;
    private String skinName;
    private String[] urlFormats;

    public Documentation getDocumentation()
    {
         return DOCUMENTATION;
    }

    public String getParametersDelimiter()
    {
        return ";";
    }

    public void setCommand(StringTokenizer params)
    {
        setValueSourceType(params.nextToken());
        setValueSourceSpec(params.nextToken());

        if(params.hasMoreTokens())
        {
            String isPopupStr = params.nextToken();
            if(isPopupStr.length() == 0 || isPopupStr.equals(PARAMVALUE_DEFAULT) || isPopupStr.equals("yes") || isPopupStr.equals("popup"))
                setPopup(true);
            else if(isPopupStr.equals("no"))
                setPopup(false);
        }
        else
            setPopup(true);

        if(params.hasMoreTokens())
        {
            String urlFormatsStr = params.nextToken();
            if(urlFormatsStr.length() == 0 || urlFormatsStr.equals(PARAMVALUE_DEFAULT))
                setUrlFormats(null);
            else
            {
                StringTokenizer urlFmtTokenizer = new StringTokenizer(urlFormatsStr, ",");
                List urlFormatsList = new ArrayList();
                while(urlFmtTokenizer.hasMoreTokens())
                {
                    String urlFormat = urlFmtTokenizer.nextToken();
                    if(urlFormat.length() == 0 || urlFormat.equals(PARAMVALUE_DEFAULT))
                        urlFormatsList.add(null);
                    else
                        urlFormatsList.add(urlFormat);
                }
                setUrlFormats((String[]) urlFormatsList.toArray(new String[urlFormatsList.size()]));
            }
        }
        else
            setUrlFormats(null);

        if(params.hasMoreTokens())
        {
            setSkinName(params.nextToken());
            if(skinName.length() == 0 || skinName.equals(PARAMVALUE_DEFAULT))
                setSkinName(null);
        }
        else
            setSkinName(null);
    }

    public int getValueSourceType()
    {
        return valueSourceType;
    }

    public String getValueSourceTypeName()
    {
        return valueSourceType == LVSTYPE_REFERENCE ? LVSTYPENAME_REFERENCE : LVSTYPENAME_INSTANCE;
    }

    public void setValueSourceType(int valueSourceType)
    {
        this.valueSourceType = valueSourceType;
    }

    public void setValueSourceType(String valueSourceType)
    {
        if(valueSourceType.equals(LVSTYPENAME_REFERENCE))
            setValueSourceType(LVSTYPE_REFERENCE);
        else
            setValueSourceType(LVSTYPE_INSTANCE);
    }

    public String getValueSourceSpec()
    {
        return valueSourceSpec;
    }

    public void setValueSourceSpec(String valueSourceSpec)
    {
        this.valueSourceSpec = valueSourceSpec;
        if(valueSourceType == LVSTYPE_INSTANCE)
            setValueSourceInstance(ValueSourceFactory.getListValueSource(valueSourceSpec));
        else
            setValueSourceReference(ValueSourceFactory.getSingleValueSource(valueSourceSpec));
    }

    public boolean isPopup()
    {
        return isPopup;
    }

    public void setPopup(boolean popup)
    {
        isPopup = popup;
    }

    public String getSkinName()
    {
        return skinName;
    }

    public void setSkinName(String skinName)
    {
        this.skinName = skinName;
    }

    public String[] getUrlFormats()
    {
        return urlFormats;
    }

    public void setUrlFormats(String[] urlFormats)
    {
        this.urlFormats = urlFormats;
    }

    public SingleValueSource getValueSourceReference()
    {
        return valueSourceReference;
    }

    public void setValueSourceReference(SingleValueSource valueSourceReference)
    {
        this.valueSourceReference = valueSourceReference;
    }

    public ListValueSource getValueSourceInstance()
    {
        return valueSourceInstance;
    }

    public void setValueSourceInstance(ListValueSource valueSourceInstance)
    {
        this.valueSourceInstance = valueSourceInstance;
    }

    public String getCommand()
    {
        String delim = getParametersDelimiter();
        StringBuffer sb = new StringBuffer(getValueSourceSpec());
        sb.append(delim);
        sb.append(isPopup ? "popup" : "no");
        sb.append(delim);
        if(urlFormats != null)
        {
            for(int i = 0; i < urlFormats.length; i++)
            {
                if(i > 0) sb.append(",");
                sb.append(urlFormats[i]);
            }
        }
        else
            sb.append(PARAMVALUE_DEFAULT);
        sb.append(delim);
        sb.append(skinName != null ? skinName : PARAMVALUE_DEFAULT);
        return sb.toString();
    }

    public void handleCommand(ValueContext vc, Writer writer, boolean unitTest) throws ComponentCommandException, IOException
    {
        switch(valueSourceType)
        {
            case LVSTYPE_REFERENCE:
                if(valueSourceReference != null)
                {
                    Object actualInstance = valueSourceReference.getObjectValue(vc);
                    if(actualInstance != null)
                    {
                        if(actualInstance instanceof ListValueSource)
                            ((ListValueSource) actualInstance).renderItemsHtml(vc, writer, urlFormats, skinName != null ? SkinFactory.getReportSkin(skinName) : null, isPopup);
                        else
                            writer.write("ListValueSource reference '"+ valueSourceSpec +"' is a "+ actualInstance+ ", not a ListValueSource in " + this.getClass().getName() +".handleValueSource().");
                    }
                }
                else
                    writer.write("ListValueSource reference '"+ valueSourceSpec +"' not found in "+ this.getClass().getName() +".handleValueSource().");
                break;

            case LVSTYPE_INSTANCE:
                if(valueSourceInstance != null)
                    valueSourceInstance.renderItemsHtml(vc, writer, urlFormats, skinName != null ? SkinFactory.getReportSkin(skinName) : null, isPopup);
                else
                    writer.write("ListValueSource instance '"+ valueSourceSpec +"' not found in "+ this.getClass().getName() +".handleValueSource().");
                break;
        }
    }
}
