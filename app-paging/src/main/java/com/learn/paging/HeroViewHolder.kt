package com.learn.paging

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HeroViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.hero_item, parent, false)) {
    var hero: Hero? = null
        private set

    private val nameView = itemView.findViewById<TextView>(R.id.tv_name)

    @SuppressLint("SetTextI18n")
    fun bindTo(item: HeroListItem?) {
        if (item is HeroListItem.Separator) {
            nameView.text = "${item.name} Hero"
            nameView.setTypeface(null, Typeface.BOLD)
        } else {
            nameView.text = item?.name
            nameView.setTypeface(null, Typeface.NORMAL)
        }
        hero = (item as? HeroListItem.Item)?.hero
        nameView.text = item?.name
    }
}