package com.github.hkupty.maple.logger;

import com.github.hkupty.maple.logger.event.LoggingEventBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.spi.LoggingEventAware;

public abstract class BaseLogger implements Logger, LoggingEventAware {
    protected String name;
    protected LoggingEventBuilderFactory eventBuilderFactory;
    private final String repr;

    BaseLogger(String name, LoggingEventBuilderFactory eventBuilderFactory) {
        this.name = name;
        this.setEventBuilderFactory(eventBuilderFactory);

        repr = this.getClass().getName() + "{" +
                "name='" + name + '\'' +
                '}';
    }

    public void setEventBuilderFactory(LoggingEventBuilderFactory eventBuilderFactory) {
        this.eventBuilderFactory = eventBuilderFactory;
    }

    public LoggingEventBuilderFactory getEventBuilderFactory() {
        return eventBuilderFactory;
    }

    @Override
    public String toString() {
        return repr;
    }
}