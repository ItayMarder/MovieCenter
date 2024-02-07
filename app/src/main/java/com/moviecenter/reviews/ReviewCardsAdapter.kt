package com.app.moviecenter.reviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.moviecenter.R
import com.app.moviecenter.common.ReviewBaseFragment
import com.app.moviecenter.model.Model
import com.app.moviecenter.model.Review
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth


class ReviewCardsAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewCardsAdapter.ReviewViewHolder>() {
    private var onReviewItemClickListener: OnReviewItemClickListener? = null
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email as String

    interface OnReviewItemClickListener {
        fun onReviewItemClicked(
            reviewId: String, reviewEmail: String,
            holder: ReviewViewHolder, mode: String
        )
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.card_image)
        val title: TextView = itemView.findViewById(R.id.card_title)
        val type: TextView = itemView.findViewById(R.id.card_type)
        val description: TextView = itemView.findViewById(R.id.card_description)
        val rating: TextView = itemView.findViewById(R.id.card_rating)
        val deleteCardButton: Button = itemView.findViewById(R.id.delete_card_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.review_card_item, parent, false)
        return ReviewViewHolder(view)
    }

    fun setOnReviewItemClickListener(listener: ReviewBaseFragment) {
        this.onReviewItemClickListener = listener
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        Model.instance.getReviewPic(review.picture).addOnSuccessListener {
            Glide.with(holder.itemView)
                .load(it)
                .into(holder.image)
        }

        holder.title.text = review.name
        holder.type.text = review.genreType.toString()
        holder.description.text = review.description
        holder.rating.text = "IMDB Rating: ${review.rating}"
        handleObjectPulling(holder, userEmail, review)
        handleClicksCard(holder, position)
    }

    private fun handleClicksCard(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.deleteCardButton.setOnClickListener {
            onReviewItemClickListener?.onReviewItemClicked(
                review.id, review.userEmail,
                holder, "DeleteCard"
            )
        }
    }

    private fun handleObjectPulling(holder: ReviewViewHolder, userEmail: String, review: Review) {
        if (review.userEmail == userEmail) {
            holder.deleteCardButton.visibility = View.VISIBLE
        } else {
            holder.deleteCardButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return reviews.size
    }
}