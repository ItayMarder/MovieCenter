package com.app.moviecenter.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.moviecenter.R
import com.app.moviecenter.common.ReviewBaseFragment
import com.app.moviecenter.model.Model
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReviewsFragment : ReviewBaseFragment(), ReviewCardsAdapter.OnReviewItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var addNewReviewButton: FloatingActionButton
    private lateinit var viewModel: ReviewViewModel


    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_reviews
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View? = super.onCreateView(inflater, container, savedInstanceState)
        if (view != null) {
            initViews(view)
        }

        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]
        viewModel.reviews = Model.instance.getAllReviews()

        setupRecyclerView()
        checkInitializationShareViewModel()
        handleAddNewClick()

        observeReviewViewModel()
        observeInitializeUserDataStatus()

        return view
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.reviews_recycler_view)
        addNewReviewButton = view.findViewById(R.id.add_new_review_button)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeReviewViewModel() {
        observeReviewViewModel(recyclerView, viewModel.reviews)
    }

    private fun handleAddNewClick() {
        addNewReviewButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_newReviewFragment)
        }
    }

    private fun observeInitializeUserDataStatus() {
        observeInitializeUserDataStatusBase()
    }
}
