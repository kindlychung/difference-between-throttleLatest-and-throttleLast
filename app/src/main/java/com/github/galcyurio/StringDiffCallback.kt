package com.github.galcyurio

import androidx.recyclerview.widget.DiffUtil


class StringDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(old: String, new: String): Boolean = true
    override fun areContentsTheSame(old: String, new: String): Boolean = old == new
}