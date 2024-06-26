package com.example.movieapp.ui.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.databinding.ReviewBinding
import com.example.movieapp.data.remote.model.common.Review


class ReviewAdaptor : RecyclerView.Adapter<ReviewAdaptor.ReviewViewHolder>() {

    private val initialReviewList = mutableListOf<Review>()
    private var reviewLines = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ReviewViewHolder(ReviewBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return initialReviewList.size
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(initialReviewList[position])
        holder.reviewMore.setOnClickListener {
            holder.showFullReviewContent()
            holder.toggleMovieReview()
        }
        holder.reviewLess.setOnClickListener {
            holder.showLessReviewContent()
            holder.toggleMovieReview()
        }
    }

    fun setReviewList(newReviewList: List<Review>) {
        initialReviewList.clear()
        initialReviewList.addAll(newReviewList)
    }

    inner class ReviewViewHolder(private val binding: ReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val reviewMore = binding.reviewMore
        val reviewLess = binding.reviewLess
        fun bind(review: Review) {
            binding.reviewAuthor.text = review.author
            binding.reviewCreatedDate.text = review.createdAt.substringBefore("T")
            binding.reviewContent.text = review.content
            showLessReviewContent()
        }

        fun showFullReviewContent() {
            binding.reviewContent.maxLines = Int.MAX_VALUE
            binding.reviewContent.alpha = 1f
        }

        fun showLessReviewContent() { // line count issue here :( getLineCount was not working
                Handler(Looper.getMainLooper()).postDelayed({
                    reviewLines = binding.reviewContent.lineCount
                    if (reviewLines < 8){
                        showFullReviewContent()
                        binding.reviewLess.visibility = View.GONE
                        binding.reviewMore.visibility = View.GONE
                        binding.reviewContent.alpha = 0.7f
                    } else {
                        binding.reviewContent.maxLines = 7
                        binding.reviewContent.alpha = 0.7f
                    }
                }, 100)
        }


        fun toggleMovieReview() {
            if (binding.reviewMore.visibility == View.VISIBLE) {
                binding.reviewMore.visibility = View.GONE
                binding.reviewLess.visibility = View.VISIBLE
            } else {
                binding.reviewMore.visibility = View.VISIBLE
                binding.reviewLess.visibility = View.GONE
            }
        }
    }
}