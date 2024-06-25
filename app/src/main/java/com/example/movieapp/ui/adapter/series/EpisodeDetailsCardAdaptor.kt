package com.example.movieapp.ui.adapter.series

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.databinding.EpisodeCardViewBinding

class EpisodeDetailsCardAdaptor: RecyclerView.Adapter<EpisodeDetailsCardAdaptor.EpisodeDetailsViewHolder>() {
    private val list: MutableList<Int> = mutableListOf()
    fun setList(newList: List<Int>){
        list.clear()
        list.addAll(newList)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EpisodeDetailsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = EpisodeCardViewBinding.inflate(layoutInflater, parent, false)
        return EpisodeDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EpisodeDetailsViewHolder, position: Int) {
        holder.bind(position + 1)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class EpisodeDetailsViewHolder(private val binding: EpisodeCardViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(int: Int){
            binding.episodeNo.text = "Eps $int"
        }
    }

}