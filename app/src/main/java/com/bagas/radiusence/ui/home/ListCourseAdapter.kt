package com.bagas.radiusence.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bagas.radiusence.data.model.Courses
import com.bagas.radiusence.databinding.ItemRowCoursesBinding

class ListCourseAdapter(val listCourses: List<Courses>): RecyclerView.Adapter<ListCourseAdapter.ListCourseVH>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListCourseAdapter.ListCourseVH {
        val binding = ItemRowCoursesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListCourseVH(binding)
    }

    override fun onBindViewHolder(holder: ListCourseAdapter.ListCourseVH, position: Int) {
        val item = listCourses[position]

        holder.tvName.text = item.name
        holder.tvMeet.text = "Pertemuan ${item.meet}"
        holder.tvTime.text = item.time

        Glide.with(holder.itemView.context)
            .load(item.thumbnail)
            .centerCrop()
            .into(holder.tvThumbnail)

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listCourses[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int = listCourses.size

    inner class ListCourseVH(binding: ItemRowCoursesBinding): RecyclerView.ViewHolder(binding.root) {
        val tvName = binding.tvItemCourse
        val tvMeet = binding.tvItemMeet
        val tvTime = binding.tvItemTime
        val tvThumbnail = binding.tvItemThumbnail
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Courses)
    }
}