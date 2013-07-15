package com.cnwr.metrics.dbcp;

import static com.codahale.metrics.MetricRegistry.name;

import java.util.SortedMap;

import org.junit.Before;
import org.junit.Test;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;


public class InstrumentedBasicDataSourceTest {

    private InstrumentedBasicDataSource datasource = null;
    private MetricRegistry registry = null;
    
    @Before
    public void setUp() throws Exception {
        final InstrumentedBasicDataSource ds = new InstrumentedBasicDataSource();
        ds.setUrl("jdbc://test");
        ds.setMinIdle(1);
        ds.setMetricRegistry("test");
        this.datasource = ds;
        this.registry = SharedMetricRegistries.getOrCreate("test");
    }

    @Test
    public void sanityCheck() {
        @SuppressWarnings("rawtypes")
        final SortedMap<String, Gauge> g = registry.getGauges();
        @SuppressWarnings("unchecked")
        Gauge<Integer> minIdle = g.get(name(this.datasource.getClass(), this.datasource.getUrl(), "minidle"));
        org.junit.Assert.assertNotNull(minIdle);
        
        org.junit.Assert.assertEquals("minidle != 1", new Integer(1), minIdle.getValue());
    }

}
