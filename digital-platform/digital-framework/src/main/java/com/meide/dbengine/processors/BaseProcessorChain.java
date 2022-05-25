package com.meide.dbengine.processors;


public class BaseProcessorChain<T> implements ProcessorChain<T> {

    BaseProcessorChain<T> next;

    public BaseProcessorChain() {
    }

    @Override
    public Object doProcessor(T info) throws Exception {
        Object rs = null;
        if (next != null) {
            rs = next.doProcessor(info);
        }
        return rs;
    }

    public BaseProcessorChain<T> setNext(BaseProcessorChain<T> next) {
        this.next = next;
        return next;
    }

    public BaseProcessorChain getNext() {
        return next;
    }
}
