package com.xaf.value;

import java.security.*;
import java.util.*;

public class GenerateIdValue extends ValueSource
{
    public SingleValueSource.Documentation getDocumentation()
    {
        return new SingleValueSource.Documentation(
            "Returns a unique value each time the value source is called. The unique value is computed as a "+
            "MD5 message digest hash based on the md5-seed provided with current date/time appended.",
            "md5-seed"
        );
    }

    public String getValue(ValueContext vc)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((valueKey + new Date().toString()).getBytes());
            return md.digest().toString();
        }
        catch(NoSuchAlgorithmException e)
        {
            return "MD5 Algorithm not found: " + e.toString();
        }
    }
}