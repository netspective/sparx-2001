package com.netspective.sparx.util.set;

public class IntSpan implements Cloneable
{
    public static String emptyString = "-";

    boolean negInf;
    boolean posInf;
    IntList edges;

    public IntSpan()
    {
        negInf = false;
        posInf = false;
        edges = new IntList();
    }

    public IntSpan(String runList)
    {
        this();
        runList = StripWhitespace(runList);

        if (runList.equals("-"))
            return;  // empty set;

        if (runList.equals("(-)"))
        {
            negInf = true;
            posInf = true;
            return;  // Z
        }

        java.util.StringTokenizer st = new java.util.StringTokenizer(runList, ",");

        while (st.hasMoreTokens())
        {
            String run = st.nextToken();

            if (run.startsWith("(-"))
                addOpenNeg(run);
            else if (run.endsWith("-)"))
                addOpenPos(run);
            else if (run.indexOf('-', 1) < 0)
                addSingle(run);
            else
                addDouble(run);
        }
    }

    private String StripWhitespace(String s)
    {
        StringBuffer sb = new StringBuffer();
        java.util.StringTokenizer st = new java.util.StringTokenizer(s);

        while (st.hasMoreTokens())
            sb.append(st.nextToken());

        return sb.toString();
    }

    private void addOpenNeg(String run)
    {
        negInf = true;
        edges.add(run.substring(2));
    }

    private void addOpenPos(String run)
    {
        int dash = run.lastIndexOf('-');
        if (dash > -1)
            run = run.substring(0, dash);
        int lower = Integer.parseInt(run);

        boolean lGap = edges.size() == 0 || lower - 1 - edges.getI(-1) > 0;
        if (lGap)
            edges.add(lower - 1);
        else
            edges.pop();

        posInf = true;
    }

    private void addSingle(String run)
    {
        int upper = Integer.parseInt(run);
        addClosed(upper, upper);
    }

    private void addDouble(String run)
    {
        int pos = run.indexOf('-', 1);

        String st = run.substring(0, pos);
        int lower = Integer.parseInt(st);

        st = run.substring(pos + 1);
        int upper = Integer.parseInt(st);

        addClosed(lower, upper);
    }

    private void addClosed(int lower, int upper)
    {
        boolean lGap = edges.size() == 0 || lower - 1 - edges.getI(-1) > 0;
        if (lGap)
            edges.add(lower - 1);
        else
            edges.pop();

        edges.add(upper);
    }

    public IntSpan(int[] elements)
    {
        this();

        int[] element = new int[elements.length];
        System.arraycopy(elements, 0, element, 0, elements.length);
        java.util.Arrays.sort(element);

        for (int i = 0; i < element.length; i++)
        {
            int top = edges.size() - 1;
            int topEdge = 0;
            if (top >= 0)
                topEdge = edges.getI(top);

            if (top >= 0 && topEdge == element[i])
                continue;    // skip duplicates

            if (top >= 0 && topEdge == element[i] - 1)
            {
                edges.set(top, element[i]);
            }
            else
            {
                edges.add(element[i] - 1);
                edges.add(element[i]);
            }
        }
    }

    public Object clone()
    {
        IntSpan clone = new IntSpan();

        clone.negInf = negInf;
        clone.posInf = posInf;
        clone.edges = (IntList) (edges.clone());

        return clone;
    }

    public String toString()
    {
        return runList();
    }

    public String runList()
    {
        if (empty()) return emptyString;
        if (universal()) return "(-)";

        StringBuffer sb = new StringBuffer();
        int i = 0;

        if (negInf)
        {
            int upper = edges.getI(0);
            sb.append("(-" + Integer.toString(upper));
            i = 1;
        }

        while (i < edges.size() - 1)
        {
            if (i > 0) sb.append(",");

            int lower = edges.getI(i);
            int upper = edges.getI(i + 1);

            if (lower + 1 == upper)
                sb.append(Integer.toString(upper));
            else
                sb.append(Integer.toString(lower + 1) + "-" +
                          Integer.toString(upper));

            i += 2;
        }

        if (posInf)
        {
            if (i > 0)
                sb.append(",");
            int lower = edges.getI(i);
            sb.append(Integer.toString(lower + 1) + "-)");
        }

        return sb.toString();
    }

    public int[] elements()
    {
        if (negInf || posInf)
            return null;

        int[] list = new int[cardinality()];
        int l = 0;

        for (int i = 0; i < edges.size(); i += 2)
        {
            int lower = edges.getI(i);
            int upper = edges.getI(i + 1);

            for (int n = lower + 1; n <= upper; n++)
                list[l++] = n;
        }

        return list;
    }

    public static IntSpan union(IntSpan a, IntSpan b)
    {
        IntSpan s = new IntSpan();

        s.negInf = a.negInf || b.negInf;

        boolean inA = a.negInf;
        boolean inB = b.negInf;

        int iA = 0;
        int iB = 0;

        while (iA < a.edges.size() && iB < b.edges.size())
        {
            int xA = a.edges.getI(iA);
            int xB = b.edges.getI(iB);

            if (xA < xB)
            {
                iA++;
                inA = !inA;
                if (!inB)
                    s.edges.add(xA);
            }
            else if (xB < xA)
            {
                iB++;
                inB = !inB;
                if (!inA)
                    s.edges.add(xB);
            }
            else
            {
                iA++;
                iB++;
                inA = !inA;
                inB = !inB;
                if (inA == inB)
                    s.edges.add(xA);
            }
        }

        if (iA < a.edges.size() && !inB)
            for (int i = iA; i < a.edges.size(); i++)
                s.edges.add(a.edges.getI(i));

        if (iB < b.edges.size() && !inA)
            for (int i = iB; i < b.edges.size(); i++)
                s.edges.add(b.edges.getI(i));

        s.posInf = a.posInf || b.posInf;

        return s;
    }

    public static IntSpan intersect(IntSpan a, IntSpan b)
    {
        a.invert();
        b.invert();

        IntSpan s = union(a, b);

        a.invert();
        b.invert();
        s.invert();

        return s;
    }

    public static IntSpan diff(IntSpan a, IntSpan b)
    {
        b.invert();

        IntSpan s = intersect(a, b);

        b.invert();

        return s;
    }

    private void invert()
    {
        negInf = !negInf;
        posInf = !posInf;
    }

    public static IntSpan xor(IntSpan a, IntSpan b)
    {
        IntSpan s = new IntSpan();

        s.negInf = a.negInf ^ b.negInf;

        int iA = 0;
        int iB = 0;

        while (iA < a.edges.size() && iB < b.edges.size())
        {
            int xA = a.edges.getI(iA);
            int xB = b.edges.getI(iB);

            if (xA < xB)
            {
                iA++;
                s.edges.add(xA);
            }
            else if (xB < xA)
            {
                iB++;
                s.edges.add(xB);
            }
            else
            {
                iA++;
                iB++;
            }
        }

        if (iA < a.edges.size())
            for (int i = iA; i < a.edges.size(); i++)
                s.edges.add(a.edges.getI(i));

        if (iB < b.edges.size())
            for (int i = iB; i < b.edges.size(); i++)
                s.edges.add(b.edges.getI(i));

        s.posInf = a.posInf ^ b.posInf;

        return s;
    }

    public static IntSpan complement(IntSpan s)
    {
        IntSpan c = (IntSpan) (s.clone());
        c.invert();
        return c;
    }

    public static boolean superset(IntSpan a, IntSpan b)
    {
        return diff(b, a).empty();
    }

    public static boolean subset(IntSpan a, IntSpan b)
    {
        return diff(a, b).empty();
    }

    public static boolean equal(IntSpan a, IntSpan b)
    {
        if (a.negInf ^ b.negInf) return false;
        if (a.posInf ^ b.posInf) return false;

        if (a.edges.size() != b.edges.size())
            return false;

        for (int i = 0; i < a.edges.size(); i++)
            if (a.edges.getI(i) != b.edges.getI(i))
                return false;

        return true;
    }

    public static boolean equivalent(IntSpan a, IntSpan b)
    {
        return a.cardinality() == b.cardinality();
    }

    public int cardinality()
    {
        if (negInf || posInf) return -1;

        int cardinality = 0;

        for (int i = 0; i < edges.size() - 1; i += 2)
        {
            int lower = edges.getI(i);
            int upper = edges.getI(i + 1);
            cardinality += upper - lower;
        }

        return cardinality;
    }

    public boolean empty()
    {
        return !negInf && edges.size() == 0 && !posInf;
    }

    public boolean finite()
    {
        return !negInf && !posInf;
    }

    public boolean negInfite()
    {
        return negInf;
    }

    public boolean posInfite()
    {
        return posInf;
    }

    public boolean infinite()
    {
        return negInf || posInf;
    }

    public boolean universal()
    {
        return negInf && edges.size() == 0 && posInf;
    }

    public boolean member(int n)
    {
        boolean inSet = negInf;

        for (int i = 0; i < edges.size(); i++)
        {
            if (inSet)
            {
                if (n <= edges.getI(i))
                    return true;
                inSet = false;
            }
            else
            {
                if (n <= edges.getI(i))
                    return false;
                inSet = true;
            }
        }

        return inSet;
    }

    public void insert(int n)
    {
        boolean inSet = negInf;

        int i;
        for (i = 0; i < edges.size(); i++)
        {
            if (inSet)
            {
                if (n <= edges.getI(i))
                    return;
                inSet = false;
            }
            else
            {
                if (n <= edges.getI(i))
                    break;
                inSet = true;
            }
        }

        if (inSet)
            return;

        boolean lGap = i == 0 || n - 1 - edges.getI(i - 1) > 0;
        boolean rGap = i == edges.size() || edges.getI(i) - n > 0;

        if (lGap && rGap)
        {
            edges.add(i, n);
            edges.add(i, n - 1);
        }
        else if (!lGap && rGap)
        {
            edges.inc(i - 1);
        }
        else if (lGap && !rGap)
        {
            edges.dec(i);
        }
        else
        {
            edges.remove(i - 1);
            edges.remove(i - 1);
        }
    }

    public void remove(int n)
    {
        boolean inSet = negInf;

        int i;
        for (i = 0; i < edges.size(); i++)
        {
            if (inSet)
            {
                if (n <= edges.getI(i))
                    break;
                inSet = false;
            }
            else
            {
                if (n <= edges.getI(i))
                    return;
                inSet = true;
            }
        }

        if (!inSet)
            return;

        boolean lGap = i == 0 || n - 1 - edges.getI(i - 1) > 0;
        boolean rGap = i == edges.size() || edges.getI(i) - n > 0;

        if (lGap && rGap)
        {
            edges.add(i, n);
            edges.add(i, n - 1);
        }
        else if (!lGap && rGap)
        {
            edges.inc(i - 1);
        }
        else if (lGap && !rGap)
        {
            edges.dec(i);
        }
        else
        {
            edges.remove(i - 1);
            edges.remove(i - 1);
        }
    }

    public Integer min()
    {
        return empty() || negInf ? null : new Integer(edges.getI((0)) + 1);
    }

    public Integer max()
    {
        int i = edges.size() - 1;
        return empty() || posInf ? null : new Integer(edges.getI(i));
    }

    public static interface Testable
    {
        boolean test(int n);
    }

    public IntSpan grep(Testable predicate)
    {
        if (infinite())
            return null;

        IntSpan s = new IntSpan();

        for (int i = 0; i < edges.size(); i += 2)
            for (int n = edges.getI(i) + 1; n <= edges.getI(i + 1); n++)
                if (predicate.test(n))
                    s.addClosed(n, n);

        return s;
    }

    public static interface Mappable
    {
        int[] map(int n);
    }

    public IntSpan map(Mappable trans)
    {
        if (infinite())
            return null;

        IntSpan s = new IntSpan();

        for (int i = 0; i < edges.size(); i += 2)
        {
            for (int n = edges.getI(i) + 1; n <= edges.getI(i + 1); n++)
            {
                int[] elements = trans.map(n);

                for (int j = 0; j < elements.length; j++)
                    s.insert(elements[j]);
            }
        }
        return s;
    }

    public Iterator first()
    {
        if (negInf)
            throw new java.util.NoSuchElementException("Set.IntSpan.first");

        return empty() ? new Iterator() : new Iterator(min());
    }

    public Iterator last()
    {
        if (posInf)
            throw new java.util.NoSuchElementException("Set.IntSpan.last");

        return empty() ? new Iterator() : new Iterator(max());
    }

    public Iterator start(int n)
    {
        if (!member(n))
            throw new java.util.NoSuchElementException("Set.IntSpan.start");

        return new Iterator(new Integer(n));
    }

    public class Iterator implements java.util.Iterator
    {
        int n, nRemove, iLo, iHi;

        private Iterator()
        {
            n = 0;
            nRemove = 0;
            iLo = 0;
            iHi = 0;
        }

        private Iterator(Integer start)
        {
            n = start.intValue();

            boolean inSet = negInf;
            int i;
            for (i = 0; i < edges.size(); i++)
            {
                if (inSet && n <= edges.getI(i))
                    break;
                inSet = !inSet;
            }

            iHi = i;
            iLo = i - 1;
        }

        public boolean hasNext()
        {
            return posInf || edges.size() > 0 && n <= edges.getI(-1);
        }

        public boolean hasPrevious()
        {
            return negInf || edges.size() > 0 && edges.getI(0) < n;
        }

        public Object next()
        {
            if (!hasNext())
                throw new java.util.NoSuchElementException("Set.IntSpan.Iterator.next");

            int nEdges = edges.size();
            if (iHi < nEdges && n <= edges.getI(iHi) || nEdges <= iHi)
            {
                Integer i = new Integer(n);
                nRemove = n;
                n++;
                return i;
            }

            iLo += 2;
            iHi += 2;

            n = edges.getI(iLo) + 1;
            nRemove = n;
            return new Integer(n);
        }

        public Object previous()
        {
            if (!hasPrevious())
                throw new java.util.NoSuchElementException("Set.IntSpan.Iterator.previous");

            int nEdges = edges.size();
            if (iLo < 0 || 0 <= iLo && iLo < nEdges && edges.getI(iLo) < n)
            {
                Integer i = new Integer(n);
                nRemove = n;
                n--;
                return i;
            }

            iLo -= 2;
            iHi -= 2;

            n = edges.getI(iHi);
            nRemove = n;
            return new Integer(n);
        }

        public void remove()
        {
        }

        public String toString()
        {
            return (new Integer(n)).toString();
        }
    }
}

class IntList extends java.util.ArrayList implements Cloneable
{
    void add(int n)
    {
        add(new Integer(n));
    }

    void add(int i, int n)
    {
        add(i, new Integer(n));
    }

    void add(String s)
    {
        add(new Integer(s));
    }

    void set(int i, int n)
    {
        set(i, new Integer(n));
    }

    int getI(int i)
    {
        if (i < 0)
            i += size();

        return ((Integer) get(i)).intValue();
    }

    void inc(int i)
    {
        int n = ((Integer) get(i)).intValue();
        set(i, new Integer(n + 1));
    }

    void dec(int i)
    {
        int n = ((Integer) get(i)).intValue();
        set(i, new Integer(n - 1));
    }

    void pop()
    {
        int i = size();
        if (i > 0)
            remove(i - 1);
    }

    public Object clone()
    {
        IntList clone = new IntList();

        for (int i = 0; i < size(); i++)
            clone.add(get(i));

        return clone;
    }

}

