package com.app.moviecenter.reviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.moviecenter.model.Review

class ReviewViewModel : ViewModel() {
    var reviews: LiveData<MutableList<Review>>? = null
    var myReviews: LiveData<MutableList<Review>>? = null
}
