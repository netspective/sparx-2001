/*
 * Created by IntelliJ IDEA.
 * User: Shahid N. Shah
 * Date: Sep 2, 2002
 * Time: 4:04:26 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.netspective.test.sparx.util.value;

import junit.framework.TestCase;

import com.netspective.sparx.util.value.ValueSourceFactory;
import com.netspective.sparx.util.value.SingleValueSource;
import com.netspective.sparx.util.value.ConfigurationExprValue;
import com.netspective.sparx.util.value.StaticValue;

public class ValueSourceFactoryTest extends TestCase
{
    public ValueSourceFactoryTest(String name)
    {
        super(name);
    }

    public void testValueSourceTokens()
    {
        ValueSourceFactory.ValueSourceTokens vst = new ValueSourceFactory.ValueSourceTokens("test-id:abc");
        assertTrue(vst.isValid());
        assertTrue(vst.isEscaped() == false);
        assertTrue(vst.getIdOrClassName().equals("test-id"));
        assertTrue(vst.getParams().equals("abc"));

        vst = new ValueSourceFactory.ValueSourceTokens("test-id\\:abc");
        assertTrue(vst.isValid() == false);
        assertTrue(vst.isEscaped() == true);

        vst = new ValueSourceFactory.ValueSourceTokens("this is a simple expression");
        assertTrue(vst.isValid() == false);
        assertTrue(vst.isEscaped() == false);
    }

    public void testGetSingleValueSource()
    {
        SingleValueSource svs = ValueSourceFactory.getSingleValueSource("simple-expr:this is ${my.expr}");
        assertTrue(svs != null);
        assertTrue(svs.getClass().getName().equals(ConfigurationExprValue.class.getName()));

        svs = ValueSourceFactory.getSingleValueSource("simple-expr\\:this is ${my.expr}");
        assertTrue(svs == null);
    }

    public void testGetSingleOrStaticValueSource()
    {
        /* test a simple expression that should return the same string */
        String simpleExpr = "This is a string";
        SingleValueSource svs = ValueSourceFactory.getSingleOrStaticValueSource(simpleExpr);
        assertTrue(svs != null);
        assertTrue(svs.getClass().getName().equals(StaticValue.class.getName()));
        assertTrue(svs.getValue(null).equals(simpleExpr));

        /* test an expression that has an escaped colon and should return string without the backslash */
        String escapedExpr = "This is a string with an escaped colon (\\:)";
        String escapeRemovedExpr = "This is a string with an escaped colon (:)";
        svs = ValueSourceFactory.getSingleOrStaticValueSource(escapedExpr);
        assertTrue(svs != null);
        assertTrue(svs.getClass().getName().equals(StaticValue.class.getName()));
        assertTrue(svs.getValue(null).equals(escapeRemovedExpr));

        /* test a simple expression that should return the same string */
        String configExpr = "simple-expr:this is ${my.expr}";
        svs = ValueSourceFactory.getSingleOrStaticValueSource(configExpr);
        assertTrue(svs != null);
        assertTrue(svs.getClass().getName().equals(ConfigurationExprValue.class.getName()));
    }
}
