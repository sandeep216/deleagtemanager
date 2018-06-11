package com.example.sandeepsingh.cameraapplication

import java.lang.ref.WeakReference

/**
 * Created by Sandeep on 6/1/18.
 */
class BasePresenter<T>(view: T) {

    protected var mView: WeakReference<T>? = WeakReference(view)

    @Throws(NullPointerException::class)
    protected fun getView(): T? {
        return if (mView != null)
            mView!!.get()
        else
            throw NullPointerException("View is unavailable")
    }

    protected fun onDestroy() {
        mView = null
    }
}