package ao.ao.cfr.abs;


import ao.ai.cfr.abs.AbstractionDomain;
import ao.ai.cfr.abs.AbstractionDomainBuilder;
import ao.ai.cfr.abs.impl.DigestDomainBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;

public class DigestDomainBuilderTest
{
    @Test
    public void oneIsIndexed() {
        AbstractionDomainBuilder<String> domainBuilder = new DigestDomainBuilder<String>();

        domainBuilder.add("foo");

        AbstractionDomain<String> domain = domainBuilder.build();

        SortedSet<Integer> indexes = new TreeSet<Integer>();

        indexes.add(domain.indexOf("foo"));

        assertEquals(new TreeSet<Integer>(Arrays.asList(0)), indexes);
        assertEquals(1, domain.size());
    }


    @Test
    public void stringsAreIndexed() {
        AbstractionDomainBuilder<String> domainBuilder = new DigestDomainBuilder<String>();

        domainBuilder.add("foo");
        domainBuilder.add("bar");
        domainBuilder.add("baz");

        AbstractionDomain<String> domain = domainBuilder.build();

        SortedSet<Integer> indexes = new TreeSet<Integer>();

        indexes.add(domain.indexOf("foo"));
        indexes.add(domain.indexOf("bar"));
        indexes.add(domain.indexOf("baz"));

        assertEquals(new TreeSet<Integer>(Arrays.asList(0, 1, 2)), indexes);
        assertEquals(3, domain.size());
    }
}
