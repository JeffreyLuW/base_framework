package com.meide.dbengine.processors;

public interface ProcessorChain<T> {
    Object doProcessor(T info) throws Exception;
}
