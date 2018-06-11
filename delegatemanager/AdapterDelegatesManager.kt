package com.example.sandeepsingh.cameraapplication.delegatemanager

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import java.util.Collections.emptyList

/**
 * Created by Sandeep on 6/1/18.
 */
class AdapterDelegatesManager<T> {

    protected var delegates = SparseArrayCompat<AdapterDelegate<T>>()
    protected var fallbackDelegate: AdapterDelegate<T>? = null

    companion object {
        internal val FALLBACK_DELEGATE_VIEW_TYPE = Integer.MAX_VALUE - 1
        private val PAYLOADS_EMPTY_LIST = emptyList<Any>()
    }

    fun addDelegate(delegate: AdapterDelegate<T>): AdapterDelegatesManager<T> {
        // algorithm could be improved since there could be holes,
        // but it's very unlikely that we reach Integer.MAX_VALUE and run out of unused indexes

        var viewType = delegates.size()
        while (delegates.get(viewType) != null) {
            viewType++
            if (FALLBACK_DELEGATE_VIEW_TYPE == viewType) {
                throw IllegalArgumentException("Oops, we are very close to Integer.MAX_VALUE. It seems that there are no more free and unused view type integers left to add another AdapterDelegate.")
            }
        }
        return addDelegate(viewType, false, delegate)
    }

    fun addDelegate(viewType: Int, delegate: AdapterDelegate<T>): AdapterDelegatesManager<T> {
        return addDelegate(viewType, false, delegate)
    }

    fun addDelegate(viewType: Int, allowReplacingDelegate: Boolean, delegate: AdapterDelegate<T>): AdapterDelegatesManager<T> {
        if (null == delegate)
            throw NullPointerException("AdapterDelegate is null")

        if (FALLBACK_DELEGATE_VIEW_TYPE == viewType)
            throw IllegalArgumentException(
                    "An AdapterDelegate is already registered for the viewType = "
                            + viewType
                            + ". Already registered AdapterDelegate is "
                            + delegates.get(viewType))

        delegates.put(viewType, delegate)
        return this
    }

    fun removeDelegate(viewType: Int): AdapterDelegatesManager<T> {
        delegates.remove(viewType)
        return this
    }
    
    fun getItemViewType(items: T, position: Int): Int {
        if (null == items)
            throw NullPointerException("Items datasource is null")

        val delegatesCount = delegates.size()
        for (i in 0 until delegatesCount) {
            val delegate = delegates.valueAt(i)
            if (delegate.isForViewType(items, position))
                return delegates.keyAt(i)
        }

        if (null != fallbackDelegate)
            return FALLBACK_DELEGATE_VIEW_TYPE

        throw NullPointerException("No AdapterDelegate added that matches the position=$position in data source")
    }

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val delegate = getDelegateForViewType(viewType)
                ?: throw NullPointerException("No AdapterDelegate added for ViewType $viewType")

        return delegate.onCreateViewHolder(parent)
    }

    fun onBindViewHolder(items: T, position: Int,
                         viewHolder: RecyclerView.ViewHolder, payloads: List<*>?) {

        val delegate = getDelegateForViewType(viewHolder.itemViewType)
                ?: throw NullPointerException("No delegate found for item at position = "
                        + position
                        + " for viewType = "
                        + viewHolder.itemViewType)
        delegate.onBindViewHolder(items, position, viewHolder, payloads!!)
    }

    fun onBindViewHolder(items: T, position: Int,
                         viewHolder: RecyclerView.ViewHolder) {
        onBindViewHolder(items, position, viewHolder, PAYLOADS_EMPTY_LIST)
    }

    fun onViewRecycled(viewHolder: RecyclerView.ViewHolder) {
        val delegate = getDelegateForViewType(viewHolder.itemViewType)
                ?: throw NullPointerException("No delegate found for "
                        + viewHolder
                        + " for item at position = "
                        + viewHolder.adapterPosition
                        + " for viewType = "
                        + viewHolder.itemViewType)
        delegate.onViewRecycled(viewHolder)
    }

    fun onFailedToRecycleView(viewHolder: RecyclerView.ViewHolder): Boolean {
        val delegate = getDelegateForViewType(viewHolder.itemViewType)
                ?: throw NullPointerException("No delegate found for "
                        + viewHolder
                        + " for item at position = "
                        + viewHolder.adapterPosition
                        + " for viewType = "
                        + viewHolder.itemViewType)
        return delegate.onFailedToRecycleView(viewHolder)
    }

    fun onViewAttachedToWindow(viewHolder: RecyclerView.ViewHolder) {
        val delegate = getDelegateForViewType(viewHolder.itemViewType)
                ?: throw NullPointerException("No delegate found for "
                        + viewHolder
                        + " for item at position = "
                        + viewHolder.adapterPosition
                        + " for viewType = "
                        + viewHolder.itemViewType)
        delegate.onViewAttachedToWindow(viewHolder)
    }

    fun onViewDetachedFromWindow(viewHolder: RecyclerView.ViewHolder) {
        val delegate = getDelegateForViewType(viewHolder.itemViewType)
                ?: throw NullPointerException("No delegate found for "
                        + viewHolder
                        + " for item at position = "
                        + viewHolder.adapterPosition
                        + " for viewType = "
                        + viewHolder.itemViewType)
        delegate.onViewDetachedFromWindow(viewHolder)
    }

    fun setFallbackDelegate(fallbackDelegate: AdapterDelegate<T>?): AdapterDelegatesManager<T> {
        this.fallbackDelegate = fallbackDelegate
        return this
    }

    fun getViewType(delegate: AdapterDelegate<T>): Int {
        if (null == delegate) {
            throw NullPointerException("Delegate is null")
        }

        val index = delegates.indexOfValue(delegate)
        return if (index == -1) {
            -1
        } else delegates.keyAt(index)
    }

    fun getDelegateForViewType(viewType: Int): AdapterDelegate<T>? {

        return delegates.get(viewType) ?: return if (fallbackDelegate == null) {
            null
        } else {
            fallbackDelegate
        }
    }

    fun getFallBackDelegate(): AdapterDelegate<T>? {
        return fallbackDelegate!!
    }

    fun getSize(): Int {
        return delegates.size()
    }

    fun clear() {
        delegates.clear()
    }

}