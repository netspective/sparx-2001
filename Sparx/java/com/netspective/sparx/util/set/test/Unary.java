package com.netspective.sparx.util.set.test;

import com.netspective.sparx.util.set.IntSpan;

public class Unary
{
    boolean ok;

    static Test[] tests =
            {
                new Test("-", "(-)"),
                new Test("(-1", "2-)"),
                new Test("1", "(-0,2-)"),
                new Test("1-3", "(-0,4-)"),
                new Test("1-3,5-9,15-)", "(-0,4,10-14"),
            };

    public Unary()
    {
        ok = true;
    }

    public void run()
    {

        for (int i = 0; i < tests.length; i++)
        {
            Test t = tests[i];

            complement(t.rl1, t.rl2);
            complement(t.rl2, t.rl1);
        }

        Object[] args = {ok ? "" : "not "};
        String msg = java.text.MessageFormat.format("Unary {0}ok", args);
        System.out.println(msg);
    }

    void complement(String runList, String expected)
    {
        // System.out.println(runList);

        IntSpan set = new IntSpan(runList);
        String actual = IntSpan.complement(set).runList();

        if (!expected.equals(actual))
        {
            Object[] args = {expected, actual};
            String problem = java.text.MessageFormat.format("{0} -> {1}", args);
            System.out.println(problem);
            ok = false;
        }
    }

    static class Test
    {
        String rl1;
        String rl2;

        Test(String rl1, String rl2)
        {
            this.rl1 = rl1;
            this.rl2 = rl2;
        }
    }
}
