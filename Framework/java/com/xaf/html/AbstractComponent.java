package com.xaf.html;

import java.io.*;
import java.util.*;

import com.xaf.page.*;
import com.xaf.value.*;

public class AbstractComponent implements Component
{
    public AbstractComponent()
    {
    }

	public void printHtml(PageContext pc, Writer writer) throws IOException
	{
		throw new RuntimeException("AbstractComponent.printHtml is an abstract method.");
	}
}