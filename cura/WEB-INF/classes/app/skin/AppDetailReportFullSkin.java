/*
 * Created by IntelliJ IDEA.
 * User: Aye Thu
 * Date: Jan 14, 2002
 * Time: 12:12:31 AM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package app.skin;

import com.netspective.sparx.xaf.skin.HtmlSingleRowReportSkin;

public class AppDetailReportFullSkin extends HtmlSingleRowReportSkin
{
    public AppDetailReportFullSkin()
    {
        this(true, 1, true);
    }

    public AppDetailReportFullSkin(boolean fullWidth, int tableCols, boolean horizontalLayout)
    {
        super(fullWidth, tableCols, horizontalLayout);
        outerTableAttrs = "width='100%' border=0 cellspacing=1 cellpadding=2 bgcolor='#EEEEEE'";
	    innerTableAttrs = "cellpadding='2' cellspacing='0' border='0' width='100%'";
	    frameHdRowAttrs = "bgcolor='#4A74E7'";
	    frameHdFontAttrs = "face='Trebuchet MS,Arial' color=white style='font-size: 10pt;' ";
        frameFtRowAttrs = "bgcolor='#c4d6ec'";
        frameFtFontAttrs = "face='verdana,arial,helvetica'  color='#000000'";
	    bannerRowAttrs = "bgcolor='#c4d6ec'";
	    bannerItemFontAttrs = "face='arial,helvetica' size=2";
	    dataHdFontAttrs = "face='verdana,arial' size='2' style='font-size: 8pt;' color='navy'";
	    dataFontAttrs = "face='verdana,arial' size='2' style='font-size: 8pt;'";
	    dataFtFontAttrs = "face='verdana,arial' size='2' style='font-size: 8pt;' color='navy'";
	    rowSepImgSrc = "/shared/resources/images/design/bar.gif";
        captionCellAttrs  = "style='width: 100px'";
    }
}
