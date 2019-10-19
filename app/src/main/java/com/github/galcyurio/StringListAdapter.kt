package com.github.galcyurio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * @author galcyurio
 */
class StringListAdapter : ListAdapter<String, StringListAdapter.ViewHolder>(StringDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
            .let { itemView -> ViewHolder(itemView) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text1.text = getItem(position)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val text1: TextView = itemView.findViewById(android.R.id.text1)
    }
}