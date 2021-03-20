package cn.jj.base.baseclass


import cn.jj.base.common.ThreadManager

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver

abstract class ObservableUseCase<T, Params> constructor(
        private val threadExecutor: Scheduler, private val postExecutionThread: Scheduler) {

    private val disposables: CompositeDisposable

    val isDisposed: Boolean
        get() = disposables.isDisposed

    init {
        this.disposables = CompositeDisposable()
    }

    constructor(threadID: Int, postThreadID: Int) :
            this(ThreadManager.getScheduler(threadID), ThreadManager.getScheduler(postThreadID))

    protected abstract fun buildUseCaseObservable(params: Params): Observable<T>

    fun execute(observer: DisposableObserver<T>, params: Params) {
        val observable = this.buildUseCaseObservable(params)
                .subscribeOn(threadExecutor)
                .observeOn(postExecutionThread)
                .doFinally { removeDisposable(observer) }
        addDisposable(observable.subscribeWith(observer))
    }

    fun dispose() {
        if (!disposables.isDisposed) {
            disposables.dispose()
        }
    }

    private fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    private fun removeDisposable(disposable: Disposable) {
        disposables.remove(disposable)
    }

    interface RequestValues

    interface ResponseValues
}
