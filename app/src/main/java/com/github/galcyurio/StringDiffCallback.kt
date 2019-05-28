package com.github.galcyurio

import android.support.v7.util.DiffUtil

class StringDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(old: String, new: String): Boolean = true
    override fun areContentsTheSame(old: String, new: String): Boolean = old == new
}