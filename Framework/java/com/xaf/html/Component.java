package com.xaf.html;

import java.io.*;
import java.util.*;

import com.xaf.page.*;
import com.xaf.value.*;

public interface Component
{
	public void printHtml(PageContext pc, Writer writer) throws IOException;
}