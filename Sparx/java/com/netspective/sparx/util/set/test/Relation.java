package com.netspective.sparx.util.set.test;

import com.netspective.sparx.util.set.IntSpan;

public class Relation
{
    boolean ok;

    static String sets[] =
            {
                "-", "(-)", "(-0", "0-)", "1", "5", "1-5", "3-7", "1-3,8,10-23"
            };

    static int[][] equal =
            {
                {1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1},
            };

    static int[][] equivalent =
            {
                {1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 1, 1, 0, 0, 0},
                {0, 0, 0, 0, 1, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 1},
            };

    static int[][] superset =
            {
                {1, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 1, 0, 0, 0, 0, 0, 0},
                {1, 0, 0, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 1, 0, 0, 0, 0},
                {1, 0, 0, 0, 0, 1, 0, 0, 0},
                {1, 0, 0, 0, 1, 1, 1, 0, 0},
                {1, 0, 0, 0, 0, 1, 0, 1, 0},
                {1, 0, 0, 0, 1, 0, 0, 0, 1},
            };

    static int[][] subset =
            {
                {1, 1, 1, 1, 1, 1, 1, 1, 1, },
                {0, 1, 0, 0, 0, 0, 0, 0, 0, },
                {0, 1, 1, 0, 0, 0, 0, 0, 0, },
                {0, 1, 0, 1, 0, 0, 0, 0, 0, },
                {0, 1, 0, 1, 1, 0, 1, 0, 1, },
                {0, 1, 0, 1, 0, 1, 1, 1, 0, },
                {0, 1, 0, 1, 0, 0, 1, 0, 0, },
                {0, 1, 0, 1, 0, 0, 0, 1, 0, },
                {0, 1, 0, 1, 0, 0, 0, 0, 1, },
            };

    public Relation()
    {
        ok = true;
    }

    // System.out.println(runList);

    public void run()
    {
        for (int i = 0; i < sets.length; i++)
        {
            for (int j = 0; j < sets.length; j++)
            {
                IntSpan A = new IntSpan(sets[i]);
                IntSpan B = new IntSpan(sets[j]);

                relation(A, B, new Equal(), equal[i][j]);
                relation(A, B, new Equivalent(), equivalent[i][j]);
                relation(A, B, new Subset(), subset[i][j]);
                relation(A, B, new Superset(), superset[i][j]);
            }
        }

        Object[] args = {ok ? "" : "not "};
        String msg = java.text.MessageFormat.format("Relation {0}ok", args);
        System.out.println(msg);
    }

    void relation(IntSpan A, IntSpan B, Operable op, int expected)
    {
        if (op.relation(A, B) ^ expected == 1)
        {
            Object[] args = {A, op, B};
            String problem = java.text.MessageFormat.format("{0} {1} {2}", args);
            System.out.println(problem);
            ok = false;
        }
    }

    static private interface Operable
    {
        boolean relation(IntSpan a, IntSpan b);
    }

    static class Equal implements Operable
    {
        public boolean relation(IntSpan a, IntSpan b)
        {
            return IntSpan.equal(a, b);
        }

        public String toString()
        {
            return "equal";
        }
    }

    static class Equivalent implements Operable
    {
        public boolean relation(IntSpan a, IntSpan b)
        {
            return IntSpan.equivalent(a, b);
        }

        public String toString()
        {
            return "equivalent";
        }
    }

    static class Subset implements Operable
    {
        public boolean relation(IntSpan a, IntSpan b)
        {
            return IntSpan.subset(a, b);
        }

        public String toString()
        {
            return "subset";
        }
    }

    static class Superset implements Operable
    {
        public boolean relation(IntSpan a, IntSpan b)
        {
            return IntSpan.superset(a, b);
        }

        public String toString()
        {
            return "superset";
        }
    }
}
