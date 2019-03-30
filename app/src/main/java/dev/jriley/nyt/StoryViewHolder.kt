package dev.jriley.nyt

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jriley.nyt.data.Story
import kotlinx.android.synthetic.main.list_item_main.view.*

class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var data: Story
    val title: TextView = itemView.title
    val by: TextView = itemView.by
}