package com.dicoding.asclepius.view

import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.databinding.ItemRowHistoryBinding
import com.dicoding.asclepius.database.History

class ListHistoryAdapter(private val listHistory: ArrayList<History>) : RecyclerView.Adapter<ListHistoryAdapter.ListViewHolder>() {

    class ListViewHolder(val binding: ItemRowHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemRowHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = listHistory.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (id, image, label, score, inferenceTime, date) = listHistory[position]

        val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
        holder.binding.imgItemPhoto.setImageBitmap(bitmap)
        holder.binding.tvItemLabel.text = label
        holder.binding.tvItemDescription.text = "Skor: ${String.format("%.0f", score * 100)}%\nWaktu: ${date}"

        holder.itemView.setOnClickListener {
            val context = it.context
            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra("HISTORY_ID", id)
            context.startActivity(intent)
        }
    }
}