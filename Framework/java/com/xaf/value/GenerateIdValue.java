package com.xaf.value;

import java.security.*;
import java.util.*;

public class GenerateIdValue extends ValueSource
{
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