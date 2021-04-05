package com.jj.base.baseclass;


import com.jj.base.common.ThreadManager;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class FlowableUseCase<T, Params> {

    protected Scheduler threadExecutor;
    protected Scheduler postExecutionThread;
    private final CompositeDisposable disposables;

    public FlowableUseCase(Scheduler threadExecutor, Scheduler postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
        this.disposables = new CompositeDisposable();
    }

    public FlowableUseCase(int threadID, int postThreadID) {
        this.threadExecutor = ThreadManager.getScheduler(threadID);
        this.postExecutionThread = ThreadManager.getScheduler(postThreadID);
        this.disposables = new CompositeDisposable();
    }

    protected abstract Flowable<T> buildUseCaseFlowable(Params params);

    public void execute(DefaultSubscriber<T> observer, Params params) {
        final Flowable<T> observable = this.buildUseCaseFlowable(params)
                .subscribeOn(threadExecutor)
                .observeOn(postExecutionThread)
                .doFinally(() -> removeDisposable(observer));

        addDisposable(observable.subscribeWith(observer));
    }

    public void dispose() {
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }

    protected void addDisposable(Disposable disposable) {
        disposables.add(disposable);
    }

    private void removeDisposable(Disposable disposable) {
        disposables.remove(disposable);
    }

    public interface RequestValues {
    }

    public interface ResponseValue {
    }
}
