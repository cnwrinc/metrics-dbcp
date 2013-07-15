package com.cnwr.metrics.dbcp;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;

import static com.codahale.metrics.MetricRegistry.name;

public class InstrumentedBasicDataSource extends BasicDataSource {
    
    private MetricRegistry registry;
    private Timer getConnectionTimer;
    /**
     * Instrumented the given BasicDataSource instance with a series of timers and gauges.
     * 
     */
    public void instrument(MetricRegistry registry,
            final BasicDataSource datasource) {
        final String prefix = name(datasource.getClass(), datasource.getUrl());        

        registry.register(name(prefix, "initialsize"), new Gauge<Integer>() {
            public Integer getValue() {
                return datasource.getInitialSize();
            }
        });
        registry.register(name(prefix, "maxactive"), new Gauge<Integer>() {
            public Integer getValue() {
                return datasource.getMaxActive();
            }
        });
        registry.register(name(prefix, "maxidle"), new Gauge<Integer>() {
            public Integer getValue() {
                return datasource.getMaxIdle();
            }
        });
        registry.register(name(prefix, "maxopenpreparedstatements"), new Gauge<Integer>() {
            public Integer getValue() {
                return datasource.getMaxOpenPreparedStatements();
            }
        });
        registry.register(name(prefix, "maxwait"), new Gauge<Long>() {
            public Long getValue() {
                return datasource.getMaxWait();
            }
        });
        registry.register(name(prefix, "minevictableidletimemillis"), new Gauge<Long>() {
            public Long getValue() {
                return datasource.getMinEvictableIdleTimeMillis();
            }
        });
        registry.register(name(prefix, "minidle"), new Gauge<Integer>() {
            public Integer getValue() {
                return datasource.getMinIdle();
            }
        });
        registry.register(name(prefix, "numactive"), new Gauge<Integer>() {
            public Integer getValue() {
                return datasource.getNumActive();
            }
        });
        registry.register(name(prefix, "numidle"), new Gauge<Integer>() {
            public Integer getValue() {
                return datasource.getNumIdle();
            }
        });
        registry.register(name(prefix, "numtestsperevictionrun"), new Gauge<Integer>() {
            public Integer getValue() {
                return datasource.getNumTestsPerEvictionRun();
            }
        });
        registry.register(name(prefix, "timebetweenevictionrunsmillis"), new Gauge<Long>() {
            public Long getValue() {
                return datasource.getTimeBetweenEvictionRunsMillis();
            }
        });
        
        this.getConnectionTimer = registry.timer(name(prefix, "getconnection"));
        final JmxReporter reporter = JmxReporter.forRegistry(registry).build();
        reporter.start();
    }
    
    public void afterPropertiesSet() throws IllegalArgumentException {
        if (registry == null) {
            throw new IllegalArgumentException("registry must be specified");
        }
        this.instrument(registry, this);
    }
    
    public void setMetricRegistry(String registryName) {
        final MetricRegistry registry = SharedMetricRegistries.getOrCreate(registryName);
        this.registry = registry;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        final Timer.Context ctx = getConnectionTimer.time();
        try {
            return super.getConnection();
        } finally {
            ctx.stop();
        }
    }
    
}
