package com.app.moviecenter.common

import ProfileViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.moviecenter.R
import com.app.moviecenter.model.Model
import com.app.moviecenter.model.Review
import com.app.moviecenter.profile.UserMetaData
import com.app.moviecenter.reviews.ReviewCardsAdapter
import com.app.moviecenter.utils.closeKeyboard
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

abstract class ReviewBaseFragment : Fragment(), ReviewCardsAdapter.OnReviewItemClickListener {
    lateinit var sharedViewModel: SharedViewModel
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(getLayoutResourceId(), container, false)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        return view
    }

    abstract fun getLayoutResourceId(): Int

    fun observeReviewViewModel(
        recyclerView: RecyclerView,
        reviews: LiveData<MutableList<Review>>?
    ) {
        reviews?.observe(viewLifecycleOwner) { currReviews: List<Review> ->
            val reviewCardsAdapter = ReviewCardsAdapter(currReviews)
            reviewCardsAdapter.setOnReviewItemClickListener(this)
            recyclerView.adapter = reviewCardsAdapter
            closeKeyboard(requireContext(), requireView())
        }
    }

    fun observeInitializeUserDataStatusBase() {
        profileViewModel.initializeUserDataStatus.observe(viewLifecycleOwner) { result: UserMetaData? ->
            if (result!!.email != "") {
                sharedViewModel.userMetaData = result
            } else {
                findNavController().navigate(R.id.loginFragment)
            }
        }
    }

    open fun showDialogResponse(message: String) {
        val rootView: View = requireView()
        val snackBar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
        val snackBarView: View = snackBar.view
        snackBarView.setBackgroundColor(resources.getColor(R.color.black))
        val textView: TextView =
            snackBarView.findViewById(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(resources.getColor(R.color.white)) // Set your desired text color
        snackBar.show()
    }

    override fun onReviewItemClicked(
        reviewId: String, reviewEmail: String,
        holder: ReviewCardsAdapter.ReviewViewHolder, mode: String
    ) {
        if (mode == "DeleteCard") {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Review")
                .setMessage("Are you sure you want to delete this review?")
                .setPositiveButton("Delete") { _, _ ->
                    deleteCardHandler(reviewId)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun deleteCardHandler(reviewId: String) {
        Model.instance.deleteReview(reviewId) {}
    }

    fun checkInitializationShareViewModel() {
        if (sharedViewModel.userMetaData.email == "") {
            profileViewModel.getUserMetaData()
        }
    }


}