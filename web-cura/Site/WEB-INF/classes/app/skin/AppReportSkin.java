package app.skin;

import com.xaf.skin.HtmlReportSkin;

public class AppReportSkin   extends HtmlReportSkin
{
    public AppReportSkin()
    {
        super();
	    outerTableAttrs = "border=0 cellspacing=1 cellpadding=2 bgcolor='#EEEEEE'";
	    innerTableAttrs = "cellpadding='1' cellspacing='0' border='0' width='100%'";
	    frameHdRowAttrs = "bgcolor='#8080FF'";
	    frameHdFontAttrs = "face='Arial' size=2 color=white";
        frameHdTableRowBgcolorAttrs = "#F4F8FA";
        frameFtRowAttrs = "bgcolor='#c4d6ec'";
        frameFtFontAttrs = "face='verdana,arial,helvetica' size=2 color='#000000'";
	    bannerRowAttrs = "bgcolor='#c4d6ec'";
	    bannerItemFontAttrs = "face='arial,helvetica' size=2";
	    dataHdFontAttrs = "face='verdana,arial' size='2' style='font-size: 8pt;' color='navy'";
	    dataFontAttrs = "face='verdana,arial' size='2' style='font-size: 8pt;'";
	    dataFtFontAttrs = "face='verdana,arial' size='2' style='font-size: 8pt;' color='navy'";
	    rowSepImgSrc = "/shared/resources/images/design/bar.gif";
    }

}
