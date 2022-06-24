package com.meide.dbengine.processors;


/**
 * 抽象出来可以层层嵌套执行的runner
 * 通过 {@link AbstractChainRunner#addChain(BaseProcessorChain)} 添加额外的执行链。
 * 每个链都可以拦截参数、处理结果。
 *
 * @date 2022-1-6
 */
public abstract class AbstractChainRunner<T> extends BaseProcessorChain<T> {

    private BaseProcessorChain<T> chain;
    private BaseProcessorChain<T> lastChain;

    /**
     * 启动函数
     * @param runningInfo
     * @return
     * @throws Exception
     */
    public Object run(T runningInfo) throws Exception {
        if (null != chain) {
            return chain.doProcessor(runningInfo);
        }
        return this.doProcessor(runningInfo);
    }

    /**
     * 启动函数请用上面的run
     * @param runningInfo
     * @return
     * @throws Exception
     */
    @Override
    public Object doProcessor(T runningInfo) throws Exception {
        return this.doInnerProcessor(runningInfo);
    }

    public abstract Object doInnerProcessor(T runningInfo) throws Exception;

    /**
     * 可以添加多个链式的调用。 不要用next！
     *
     * @param baseProcessorChain<T>
     * @return
     */
    public AbstractChainRunner<T> addChain(BaseProcessorChain<T> baseProcessorChain) {
        if (chain == null) {
            chain = baseProcessorChain;
        } else {
            lastChain.setNext(baseProcessorChain);
        }
        lastChain = baseProcessorChain;
        lastChain.setNext(this);
        return this;
    }

}
