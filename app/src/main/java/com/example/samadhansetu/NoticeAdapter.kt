package com.example.samadhansetu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.samadhansetu.databinding.ItemNoticeBinding
import java.text.SimpleDateFormat
import java.util.*

class NoticeAdapter(private val noticeList: List<Notice>) :
    RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>() {

    inner class NoticeViewHolder(private val binding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notice: Notice) {
            binding.tvNoticeTitle.text = notice.title
            binding.tvNoticeDescription.text = notice.description

            // Format the timestamp into a readable date
            notice.timestamp?.let {
                val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                binding.tvNoticeDate.text = "Posted on: ${sdf.format(Date(it))}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoticeViewHolder {
        val binding = ItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoticeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        holder.bind(noticeList[position])
    }

    override fun getItemCount() = noticeList.size
}