package com.netspective.sparx.xaf.html.command;

import com.netspective.sparx.util.value.ListValueSource;
import com.netspective.sparx.util.value.ValueContext;
import com.netspective.sparx.util.value.ValueSourceFactory;
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

    private String valueSourceSpec;
    private ListValueSource valueSource;
    private boolean isPopup;
    private String skinName;
    private String[] urlFormats;

    public Documentation getDocumentation()
    {
        return new Documentation(
                "Displays the contents of a ListValueSource. Unlike most of the other commands, this command has " +
                "parameters separated by semi-colons instead of commas since commas may be found within a value-source "+
                "specification. The value-source parameter is basically the value-source spefication like xxx:yyy. The "+
                "isPopup parameter may be '-' or 'true', 'popup', or 'no'. The urlFormats parameter is one or more "+
                "comma-separated URL formats that may override those within a report.",
                new Documentation.Parameter[]
                    {
                        new Documentation.Parameter("value-source-spec", true),
                        new Documentation.Parameter("is-popup", false, "yes"),
                        new Documentation.Parameter("url-formats", false),
                        new Documentation.Parameter("skin-name", false),
                    }
        );
    }

    public String getParametersDelimiter()
    {
        return ";";
    }

    public void setCommand(StringTokenizer params)
    {
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

    public String getValueSourceSpec()
    {
        return valueSourceSpec;
    }

    public void setValueSourceSpec(String valueSourceSpec)
    {
        this.valueSourceSpec = valueSourceSpec;
        setValueSource(ValueSourceFactory.getListValueSource(valueSourceSpec));
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

    public ListValueSource getValueSource()
    {
        return valueSource;
    }

    public void setValueSource(ListValueSource valueSource)
    {
        this.valueSource = valueSource;
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
        if(valueSource != null)
            valueSource.renderChoicesHtml(vc, writer, urlFormats, skinName != null ? SkinFactory.getReportSkin(skinName) : null, true);
        else
            writer.write("ListValueSource '"+ valueSourceSpec +"' not found in "+ this.getClass().getName() +".handleValueSource().");
    }
}
