package com.xaf.sql.query;

/**
 * Title:        The Extensible Application Platform
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Netspective Communications Corporation
 * @author Shahid N. Shah
 * @version 1.0
 */

import java.sql.*;
import java.text.*;

import com.xaf.form.*;
import com.xaf.value.SingleValueSource;
import com.xaf.value.ValueSourceFactory;
import com.xaf.value.StaticValue;
import org.w3c.dom.Element;

public class ResultSetNavigatorButtonsField extends DialogField
{
    static public SingleValueSource SUBMIT_CAPTION = new StaticValue(" OK ");
    static public SingleValueSource FIRST_CAPTION = new StaticValue(" First ");
    static public SingleValueSource PREV_CAPTION = new StaticValue(" Previous ");
    static public SingleValueSource NEXT_CAPTION = new StaticValue(" Next ");
    static public SingleValueSource LAST_CAPTION = new StaticValue(" Last ");
    static public SingleValueSource DONE_CAPTION = new StaticValue(" Done ");

    private SingleValueSource submitCaption = SUBMIT_CAPTION;
    private SingleValueSource firstCaption = FIRST_CAPTION;
    private SingleValueSource prevCaption = PREV_CAPTION;
    private SingleValueSource nextCaption = NEXT_CAPTION;
    private SingleValueSource lastCaption = LAST_CAPTION;
    private SingleValueSource doneCaption = DONE_CAPTION;
    private SingleValueSource doneUrl;

	public ResultSetNavigatorButtonsField()
	{
		this("rs_nav_buttons");
		setFlag(DialogField.FLDFLAG_INVISIBLE);
	}

	public ResultSetNavigatorButtonsField(String name)
	{
		super(name, null);
		setFlag(DialogField.FLDFLAG_INVISIBLE);
	}

    public void importFromXml(Element elem)
    {
        super.importFromXml(elem);

        String captionText = elem.getAttribute("submit-caption");
        if(captionText.length() > 0)
            submitCaption = ValueSourceFactory.getSingleOrStaticValueSource(captionText);

        captionText = elem.getAttribute("first-caption");
        if(captionText.length() > 0)
            firstCaption = ValueSourceFactory.getSingleOrStaticValueSource(captionText);

        captionText = elem.getAttribute("previous-caption");
        if(captionText.length() > 0)
            prevCaption = ValueSourceFactory.getSingleOrStaticValueSource(captionText);

        captionText = elem.getAttribute("next-caption");
        if(captionText.length() > 0)
            nextCaption = ValueSourceFactory.getSingleOrStaticValueSource(captionText);

        captionText = elem.getAttribute("last-caption");
        if(captionText.length() > 0)
            lastCaption = ValueSourceFactory.getSingleOrStaticValueSource(captionText);

        captionText = elem.getAttribute("done-caption");
        if("invisible".equals(captionText))
            doneCaption = null;
        else if(captionText.length() > 0)
            doneCaption = ValueSourceFactory.getSingleOrStaticValueSource(captionText);

        String doneUrlText = elem.getAttribute("done-url");
        if(doneUrlText.length() > 0)
            doneUrl = ValueSourceFactory.getSingleOrStaticValueSource(doneUrlText);
        else
            doneUrl = null;
    }

    public String getControlHtml(DialogContext dc)
	{
		String attrs = dc.getSkin().getDefaultControlAttrs();
		QuerySelectScrollState state = (QuerySelectScrollState)	dc.getRequest().getAttribute(dc.getTransactionId() + "_state");
		if(state == null)
	   		return "<input type='submit' name='"+ dc.getDialog().getResetContextParamName() +"' value='"+ submitCaption.getValue(dc) +"' " + attrs + "> ";

		boolean isScrollable = state.isScrollable();
        int activePage = state.getActivePage();
        int lastPage = state.getTotalPages();

		StringBuffer html = new StringBuffer("<center>");
        if (lastPage > 0)
        {
            html.append("<nobr>Page ");
            html.append(state.getActivePage());
            if(isScrollable)
            {
                html.append(" of ");
                html.append(state.getTotalPages());
            }
            html.append("</nobr>&nbsp;&nbsp;");
            if(activePage > 1)
                html.append("<input type='submit' name='rs_nav_first' value='"+ firstCaption.getValue(dc) +"' " + attrs + "> ");

            if(activePage > 2)
                html.append("<input type='submit' name='rs_nav_prev' value='"+ prevCaption.getValue(dc) +"' " + attrs + "> ");

            boolean hasMoreRows = false;
            try
            {
                if(state.hasMoreRows())
                {
                    html.append("<input type='submit' name='rs_nav_next' value='"+ nextCaption.getValue(dc) +"' " + attrs + "> ");
                    hasMoreRows = true;
                }
            }
            catch(SQLException e)
            {
                html.append(e.toString());
            }

            if(isScrollable)
            {
                if(activePage < lastPage)
                    html.append("<input type='submit' name='rs_nav_last' value='"+ lastCaption.getValue(dc) +"' " + attrs + "> ");
                html.append("&nbsp;&nbsp;<nobr>");
                html.append(NumberFormat.getNumberInstance().format(state.getTotalRows()));
                html.append(" total rows</nobr>");
            }
            else if(hasMoreRows)
            {
                html.append("&nbsp;&nbsp;<nobr>");
                html.append(NumberFormat.getNumberInstance().format(state.getRowsProcessed()));
                html.append(" rows so far</nobr>");
            }
            else
            {
                html.append("&nbsp;&nbsp;<nobr>");
                html.append(NumberFormat.getNumberInstance().format(state.getRowsProcessed()));
                html.append(" total rows</nobr>");
            }

        }

        if(doneCaption != null)
        {
            if(doneUrl == null)
                html.append("&nbsp;&nbsp;<input type='submit' name='"+ dc.getDialog().getResetContextParamName() +"' value='"+ doneCaption.getValue(dc) +"' " + attrs + "> ");
            else
                html.append("&nbsp;&nbsp;<input type='button' name='jump' value='"+ doneCaption.getValue(dc) +"' onclick='location.href=\""+ doneUrl.getValue(dc) +"\"'" + attrs + "> ");
        }
		html.append("</center>");

		return html.toString();
	}
}
