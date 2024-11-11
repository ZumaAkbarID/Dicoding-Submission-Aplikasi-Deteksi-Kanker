package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.databinding.ItemRowNewsBinding

class NewsAdapter : ListAdapter<ArticlesItem, NewsAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
    }

    class MyViewHolder(val binding: ItemRowNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: ArticlesItem){
            Glide.with(binding.imgItemPhoto.context).load(review.urlToImage).into(binding.imgItemPhoto)
            var title = review.title
            if(review.author !== "null") {
                title += " | ${review.author}"
            }
            binding.tvItemTitle.text = title
            binding.tvItemDescription.text = review.description
            binding.btnReadMore.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(review.url))
                binding.root.context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}