/*
 * Description: app.skin.AppDetailReportSkin
 * @author ThuA
 * @created Dec 28, 2001 1:20:48 PM
 * @version 
 */
package app.skin;

import com.netspective.sparx.xaf.skin.HtmlSingleRowReportSkin;

public class AppDetailReportSkin   extends HtmlSingleRowReportSkin
{
    public AppDetailReportSkin()
    {
        this(1, true);
    }

    public AppDetailReportSkin(int tableCols, boolean horizontalLayout)
    {
        super(tableCols, horizontalLayout);
        outerTableAttrs = "width='300' border=0 cellspacing=1 cellpadding=2 bgcolor='#EEEEEE'";
	    innerTableAttrs = "cellpadding='2' cellspacing='0' border='0' width='100%'";
	    frameHdRowAttrs = "bgcolor='#4A74E7'";
	    frameHdFontAttrs = "face='Trebuchet MS,Arial' color=white style='font-size: 10pt;' ";
        frameHdTableRowBgcolorAttrs = "#F4F8FA";
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
