package com.example.sandeepsingh.cameraapplication.delegatemanager

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * Created by Sandeep on 6/1/18.
 */
abstract class AdapterDelegate<T> {

    abstract fun isForViewType(items: T, position: Int): Boolean

     abstract fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

     abstract fun onBindViewHolder(items: T, position: Int,
                                            holder: RecyclerView.ViewHolder, payloads: List<*>)

     fun onViewRecycled(holder: RecyclerView.ViewHolder) {}

     fun onFailedToRecycleView(holder: RecyclerView.ViewHolder): Boolean {
        return false
    }

     fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {}

     fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {}
}