package com.netspective.sparx.xaf.theme;

import com.netspective.sparx.xaf.form.DialogSkin;
import com.netspective.sparx.xaf.form.DialogContext;
import com.netspective.sparx.xaf.skin.StandardDialogSkin;
import com.netspective.sparx.util.config.Configuration;

import javax.servlet.http.HttpServletRequest;
import java.io.Writer;
import java.io.IOException;

/**
 * @author Aye Thu
 * Created on Feb 23, 2003 1:26:49 PM
 */
public class LoginDialogSkin extends ThemeDialogSkin
{
    public LoginDialogSkin()
    {
        super();
        setFrameVisible(false);
    }

    public void renderContentsHtml(Writer writer, DialogContext dc, Configuration appConfig, String dialogName, String actionURL, String encType, String heading, int dlgTableColSpan, StringBuffer errorMsgsHtml, StringBuffer fieldsHtml) throws IOException
    {
        // associate a theme with this context
        ThemeFactory tf = ThemeFactory.getInstance(dc);
        Theme theme = tf.getCurrentTheme();
        String imgPath = theme.getCurrentStyle().getImagePath();

        writer.write("        <table class=\"color-input-panel-border\" width=\"50%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
        writer.write("            <tr>");
        writer.write("                <td class=\"panel-content-input\">");
        writer.write("                    <table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">");
        writer.write("                        <tr height=\"30\">");
        writer.write("                            <td class=\"dialog-fields-header\" align=\"left\" valign=\"bottom\" height=\"30\">" +
                heading + "</td>");
        writer.write("                        </tr>");
        writer.write("                        <tr>");
        writer.write("                            <td class=\"dialog-pattern\">");
        writer.write("                                <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
        writer.write("                                    <tr height=\"150\">");
        writer.write("                                        <td align=\"center\" valign=\"middle\" width=\"160\" height=\"150\">" +
                "<img src=\"" + ((HttpServletRequest)dc.getRequest()).getContextPath() + imgPath + "/login/netspective-keys.gif\" " +
                "alt=\"\" height=\"128\" width=\"114\" border=\"0\"></td>");
        writer.write("                                        <td align=\"left\" valign=\"middle\" width=\"200\" height=\"150\">");

        writer.write("          <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
        if(summarizeErrors)
            writer.write(errorMsgsHtml.toString());

        writer.write(
                "<form id='" + dialogName + "' name='" + dialogName + "' action='" + actionURL + "' method='post' " +
                encType + " onsubmit='return(activeDialog.isValid())'>\n" +
                dc.getStateHiddens() + "\n" +
                fieldsHtml +
                "</form>\n");

        writer.write("          </table>\n");

        writer.write("                                        </td>");
        writer.write("                                    </tr>");
        writer.write("                                </table>");
        writer.write("                            </td>");
        writer.write("                        </tr>");
        writer.write("                    </table>");
        writer.write("                </td>");
        writer.write("            </tr>");
        writer.write("        </table>");
    }

}
