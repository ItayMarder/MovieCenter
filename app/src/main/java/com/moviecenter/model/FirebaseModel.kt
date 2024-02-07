package com.app.moviecenter.model

import android.net.Uri
import com.app.moviecenter.utils.getMovieRank
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.runBlocking

class FirebaseModel {

    private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    companion object {
        const val REVIEWS_COLLECTION_PATH = "Reviews"
    }

    fun getAllReviews(since: Long, callback: (List<Review>) -> Unit) {
        db.collection(REVIEWS_COLLECTION_PATH)
//            .whereGreaterThanOrEqualTo(Review.LAST_UPDATED, Timestamp(since, 0))
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val reviews: MutableList<Review> = mutableListOf()

                        for (json in it.result) {
                            val review = Review.fromJSON(json.data)
                            reviews.add(review)
                        }
                        reviews.forEach { d -> d.rating = runBlocking { getMovieRank(d.imdbId) } }

                        callback(reviews)
                    }

                    false -> callback(listOf())
                }
            }

    }

    fun getMyReviews(since: Long, callback: (List<Review>) -> Unit) {
        val email = currentUser?.email

        db.collection(REVIEWS_COLLECTION_PATH)
            .whereEqualTo("userEmail", email)
//            .whereGreaterThanOrEqualTo(Review.LAST_UPDATED, Timestamp(since, 0))
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val reviews: MutableList<Review> = mutableListOf()

                        for (json in it.result) {
                            val review = Review.fromJSON(json.data)
                            reviews.add(review)
                        }
                        reviews.forEach { d -> d.rating = runBlocking { getMovieRank(d.imdbId) } }

                        callback(reviews)
                    }

                    false -> callback(listOf())
                }
            }

    }

    fun createNewReview(review: Review, attachedPictureUri: Uri, callback: () -> Unit) {
        val imageRefLocation =
            "productPicture/${currentUser?.uid}/${attachedPictureUri.lastPathSegment}"
        val imageRef: StorageReference = storage.getReference(imageRefLocation)

        imageRef.putFile(attachedPictureUri)
            .addOnSuccessListener {
                if (currentUser != null) {
                    currentUser.email?.let {
                        review.userEmail = it
                        review.picture = imageRef.path
                    }
                }
                db.collection(REVIEWS_COLLECTION_PATH).add(review)
                    .addOnSuccessListener { documentReference ->

                        val reviewId = documentReference.id


                        db.collection(REVIEWS_COLLECTION_PATH)
                            .document(reviewId)
                            .update("id", reviewId)
                            .addOnCompleteListener {
                                callback()
                            }
                    }
            }
    }

    fun deleteReview(reviewId: String, callback: () -> Unit) {
        db.collection(REVIEWS_COLLECTION_PATH).document(reviewId).delete()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        callback()
                    }

                    false -> callback()
                }
            }
    }

    fun getReviewPic(picture: String): Task<Uri> {
        return storage.reference.child(picture).downloadUrl
    }
}