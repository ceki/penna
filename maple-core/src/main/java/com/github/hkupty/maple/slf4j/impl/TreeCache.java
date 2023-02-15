package com.github.hkupty.maple.slf4j.impl;


import com.github.hkupty.maple.logger.BaseLogger;
import com.github.hkupty.maple.logger.event.LoggingEventBuilderFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class TreeCache {
    private record Entry(String[] identifier, BaseLogger logger, ArrayList<Entry> children){
        static Entry create(String[] identifier, BaseLogger logger) {
            return new Entry(identifier, logger, new ArrayList<>());
        }
    }

    private final Entry ROOT;

    public TreeCache(BaseLogger root) {
        ROOT = Entry.create(new String[]{}, root);
    }

    private Entry search(Entry cursor, String[] identifier, int index){
       for(var childEntry : cursor.children) {
           if (childEntry.identifier.length > index) {
               if (childEntry.identifier[index].equals(identifier[index])){
                   if (index == identifier.length - 1) {
                       return childEntry;
                   } else {
                       return search(childEntry, identifier, index + 1);
                   }

               }
           }
       }
       return null;
    }

    public Logger find(String[] identifier) {
        if(identifier.length == 0) {
            return ROOT.logger;
        }
        var entry = search(ROOT, identifier, 0);
        if (!Objects.isNull(entry)) {
            return entry.logger;
        }
        return null;
    }

     public Logger createRecursively(String[] identifier, BiFunction<BaseLogger, String[], BaseLogger> filler){
        var cursor = ROOT;
        for(int index = 1; index <= identifier.length; index++) {
            var slicedIdentifier = Arrays.copyOfRange(identifier, 0, index);
            var child = search(cursor, slicedIdentifier, index);
            if (Objects.isNull(child)) {
                child = Entry.create(slicedIdentifier, filler.apply(cursor.logger, slicedIdentifier));
                cursor.children.add(child);
            }
            cursor = child;

        }

        return cursor.logger;
    }

    private void traverse(Entry base, Consumer<BaseLogger> update) {
        update.accept(base.logger);
        for (int index = 0; index < base.children.size(); index++){
            traverse(base.children.get(index), update);
        }
    }

    public void updateLoggerEventFactory(String[] hierarchyIdentifier, LoggingEventBuilderFactory factory) {
        Entry base;
        if (hierarchyIdentifier.length == 0) {
            base = ROOT;
        } else {
            base = search(ROOT, hierarchyIdentifier, 0);
        }
        if (base != null) {
            traverse(base, logger -> {
                logger.setEventBuilderFactory(factory);
            });

        }
    }
}
