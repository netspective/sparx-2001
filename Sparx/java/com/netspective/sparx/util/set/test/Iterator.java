package com.netspective.sparx.util.set.test;

import com.netspective.sparx.util.set.IntSpan;

public class Iterator
{
    boolean ok;

    String[] sets = {"-", "(-)", "(-0", "0-)", "1", "5", "1-5", "3-7", "1-3,8,10-23", };
    boolean[] first = {true, false, false, true, true, true, true, true, true};
    boolean[] last = {true, false, true, false, true, true, true, true, true};
    boolean[] start = {false, true, true, true, false, false, false, false, false};

    public Iterator()
    {
        ok = true;
    }

    public void run()
    {
        create();
        next();
        nextInf();
        prev();
        prevInf();
        start();
        // remove  ();

        Object[] args = {ok ? "" : "not "};
        String msg = java.text.MessageFormat.format("Interator {0}ok", args);
        System.out.println(msg);
    }

    void create()
    {
        for (int i = 0; i < sets.length; i++)
        {
            IntSpan s = new IntSpan(sets[i]);
            create(s, new First(), first[i]);
            create(s, new Last(), last[i]);
            create(s, new Start(), start[i]);
        }
    }

    void create(IntSpan s, Creatable c, boolean expected)
    {
        boolean failed = false;

        try
        {
            IntSpan.Iterator actual = c.create(s);
            if (!expected)
                failed = true;
        }
        catch (java.util.NoSuchElementException e)
        {
            if (expected)
                failed = true;
        }

        if (failed)
        {
            Object[] args = {s, c};
            String problem = java.text.MessageFormat.format("iterator {0} {1} -> failed", args);
            System.out.println(problem);

            ok = false;
        }
    }

    static interface Creatable
    {
        public IntSpan.Iterator create(IntSpan s);
    }

    static class First implements Creatable
    {
        public IntSpan.Iterator create(IntSpan s)
        {
            return s.first();
        }

        public String toString()
        {
            return "first";
        }
    }

    static class Last implements Creatable
    {
        public IntSpan.Iterator create(IntSpan s)
        {
            return s.last();
        }

        public String toString()
        {
            return "last";
        }
    }

    static class Start implements Creatable
    {
        public IntSpan.Iterator create(IntSpan s)
        {
            return s.start(0);
        }

        public String toString()
        {
            return "start";
        }
    }

    void next()
    {
        for (int i = 0; i < sets.length; i++)
        {
            IntSpan s = new IntSpan(sets[i]);
            if (s.infinite() || !first[i])
                continue;

            IntSpan.Iterator it = s.first();
            IntSpan s1 = new IntSpan();

            while (it.hasNext())
                s1.insert(((Integer) it.next()).intValue());

            if (!IntSpan.equal(s, s1))
            {
                Object[] args = {s, s1};
                String problem = java.text.MessageFormat.format("next {0} -> {1}", args);
                System.out.println(problem);

                ok = false;
            }
        }
    }

    void nextInf()
    {
        IntSpan s = new IntSpan("0-)");

        IntSpan.Iterator it = s.first();
        IntSpan s1 = new IntSpan();
        IntSpan s100 = new IntSpan("0-99");

        for (int i = 0; i < 100; i++)
            s1.insert(((Integer) it.next()).intValue());

        if (!IntSpan.equal(s1, s100))
        {
            Object[] args = {s1};
            String problem = java.text.MessageFormat.format("nextInf -> {0}", args);
            System.out.println(problem);

            ok = false;
        }
    }

    void prev()
    {
        for (int i = 0; i < sets.length; i++)
        {
            IntSpan s = new IntSpan(sets[i]);
            if (s.infinite() || !first[i])
                continue;

            IntSpan.Iterator it = s.last();
            IntSpan s1 = new IntSpan();

            while (it.hasPrevious())
                s1.insert(((Integer) it.previous()).intValue());

            if (!IntSpan.equal(s, s1))
            {
                Object[] args = {s, s1};
                String problem = java.text.MessageFormat.format("previous {0} -> {1}", args);
                System.out.println(problem);

                ok = false;
            }
        }
    }

    void prevInf()
    {
        IntSpan s = new IntSpan("(-0");

        IntSpan.Iterator it = s.last();
        IntSpan s1 = new IntSpan();
        IntSpan s100 = new IntSpan("-99-0");

        for (int i = 0; i < 100; i++)
            s1.insert(((Integer) it.previous()).intValue());

        if (!IntSpan.equal(s1, s100))
        {
            Object[] args = {s1};
            String problem = java.text.MessageFormat.format("prevInf -> {0}", args);
            System.out.println(problem);

            ok = false;
        }
    }

    void start()
    {
        IntSpan s = new IntSpan("-20--15,-9--3,-1-5,8-13,17-20");
        IntSpan sPos = IntSpan.intersect(s, new IntSpan(" 0-20"));
        IntSpan sNeg = IntSpan.intersect(s, new IntSpan("-20-0"));

        IntSpan.Iterator itPos = s.start(0);
        IntSpan s1Pos = new IntSpan();

        while (itPos.hasNext())
            s1Pos.insert(((Integer) itPos.next()).intValue());

        if (!IntSpan.equal(sPos, s1Pos))
        {
            Object[] args = {s, s1Pos};
            String problem = java.text.MessageFormat.format("start(0) pos {0} -> {1}", args);
            System.out.println(problem);

            ok = false;
        }

        IntSpan.Iterator itNeg = s.start(0);
        IntSpan s1Neg = new IntSpan();

        while (itNeg.hasPrevious())
            s1Neg.insert(((Integer) itNeg.previous()).intValue());

        if (!IntSpan.equal(sNeg, s1Neg))
        {
            Object[] args = {s, s1Neg};
            String problem = java.text.MessageFormat.format("start(0) neg {0} -> {1}", args);
            System.out.println(problem);

            ok = false;
        }
    }

    void remove()
    {
        class IsEven implements IntSpan.Testable
        {
            public boolean test(int n)
            {
                return (n & 1) == 0;
            }
        }

        IntSpan s = new IntSpan("-20--15,-9--3,-1-5,8-13,17-20");
        IntSpan sAll = (IntSpan) s.clone();
        IntSpan sEven = s.grep(new IsEven());

        IntSpan.Iterator it = s.first();

        while (it.hasNext())
            if ((((Integer) it.next()).intValue() & 1) == 1)
                it.remove();

        if (!IntSpan.equal(s, sEven))
        {
            Object[] args = {sAll, s};
            String problem = java.text.MessageFormat.format("remove {0} -> {1}", args);
            System.out.println(problem);

            ok = false;
        }
    }
}
