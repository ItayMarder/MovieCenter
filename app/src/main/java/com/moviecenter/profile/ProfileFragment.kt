package com.app.moviecenter.profile

import ProfileViewModel
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.moviecenter.R
import com.app.moviecenter.common.ReviewBaseFragment
import com.app.moviecenter.common.SharedViewModel
import com.app.moviecenter.reviews.ReviewCardsAdapter
import com.app.moviecenter.singup.UserProperties
import com.app.moviecenter.utils.RequestStatus
import com.app.moviecenter.utils.isString
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout

class ProfileFragment : ReviewBaseFragment(), ReviewCardsAdapter.OnReviewItemClickListener {
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var addNewReviewButton: FloatingActionButton
    private lateinit var editProfileButton: FloatingActionButton
    private lateinit var userProfileString: TextView
    private lateinit var userEmailString: TextView
    private lateinit var firstNameInput: TextInputLayout
    private lateinit var lastNameInput: TextInputLayout
    private lateinit var changeProfilePictureButton: Button
    private lateinit var saveNewDetailsButton: Button
    private lateinit var progressBarProfilePhoto: ProgressBar
    private lateinit var firstNameInputText: EditText
    private lateinit var lastNameInputText: EditText
    private lateinit var profileImage: ImageView

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_profile
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View? = super.onCreateView(inflater, container, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        if (view != null) {
            initViews(view)
        }
        initializeUserName()
        initializeUserEmail()
        handleChangeProfilePicture()
        handleAddNewClick()
        handleEditProfileClick()
        checkInitializationShareViewModel()
        handleSaveClick()
        observeShowProfilePhoto()
        observeChangeName()
        observeUploadProfileImage()

        profileViewModel.getProfileImage(sharedViewModel.userMetaData)
        return view
    }

    private fun initViews(view: View) {
        userProfileString = view.findViewById(R.id.user_profile_string)
        userEmailString = view.findViewById(R.id.user_email_string)
        changeProfilePictureButton = view.findViewById(R.id.change_profile_picture_button)
        progressBarProfilePhoto = view.findViewById(R.id.progress_bar_profile_photo)
        profileImage = view.findViewById(R.id.profile_image)
        addNewReviewButton = view.findViewById(R.id.add_new_review_button)
        saveNewDetailsButton = view.findViewById(R.id.save_new_details_button)
        editProfileButton = view.findViewById(R.id.edit_profile_button)
        firstNameInput = view.findViewById(R.id.firstNameInputLayout)
        lastNameInput = view.findViewById(R.id.lastNameInputLayout)
        firstNameInputText = view.findViewById(R.id.first_name_input_text)
        lastNameInputText = view.findViewById(R.id.last_name_input_text)
    }

    private fun handleAddNewClick() {
        addNewReviewButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_newReviewFragment)
        }
    }

    private fun handleSaveClick() {
        saveNewDetailsButton.setOnClickListener {
            val newFirstName = firstNameInputText.text.toString()
            val newLastName = lastNameInputText.text.toString()

            val userProperties = UserProperties(newFirstName, newLastName)
            if (checkUserProperties(userProperties)) {
                val newUser = sharedViewModel.userMetaData
                newUser.firstName = userProperties.firstName
                newUser.lastName = userProperties.lastName
                profileViewModel.changeUserName(newUser)
            }

            firstNameInput.visibility = View.INVISIBLE
            lastNameInput.visibility = View.INVISIBLE
            saveNewDetailsButton.visibility = View.INVISIBLE
            changeProfilePictureButton.visibility = View.INVISIBLE
        }
    }


    private fun handleEditProfileClick() {
        editProfileButton.setOnClickListener {
            firstNameInput.visibility = View.VISIBLE
            lastNameInput.visibility = View.VISIBLE
            saveNewDetailsButton.visibility = View.VISIBLE
            changeProfilePictureButton.visibility = View.VISIBLE
        }
    }

    private fun initializeUserName() {
        "${sharedViewModel.userMetaData.firstName}${sharedViewModel.userMetaData.lastName?: ""}'s Profile".also { userProfileString.text = it }
    }

    private fun initializeUserEmail() {
        sharedViewModel.userMetaData.email.also { userEmailString.text = it }
    }

    private val pickImageContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { profileViewModel.uploadProfileImage(sharedViewModel.userMetaData, it) }
        }

    private fun handleChangeProfilePicture() {
        changeProfilePictureButton.setOnClickListener {
            pickImageContract.launch("image/*")
        }
    }


    private fun checkUserProperties(userProperties: UserProperties): Boolean {
        if (userProperties.firstName.isEmpty() || !isString(userProperties.firstName)) {
            showDialogResponse("Enter valid first name")
        } else if (userProperties.lastName.isEmpty() || !isString(userProperties.lastName)) {
            showDialogResponse("Enter valid last name")
        } else {
            return true
        }
        return false
    }

    private fun observeChangeName() {
        profileViewModel.changeNameResult.observe(viewLifecycleOwner) { result: UserProperties? ->
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                if (result != null) {
                    sharedViewModel.userMetaData.firstName = result.firstName
                    sharedViewModel.userMetaData.lastName = result.lastName
                    showDialogResponse("Name changed successfully")
                    initializeUserName()
                    initializeUserEmail()
                    profileViewModel.changeNameResult.removeObservers(viewLifecycleOwner)

                } else {
                    showDialogResponse("Error while changing your name")
                }
            }
        }
    }

    private fun observeShowProfilePhoto() {
        profileViewModel.showProfilePhoto.observe(viewLifecycleOwner) { result: Uri? ->
            if (result is Uri) {
                Glide.with(this)
                    .load(result)
                    .into(profileImage)
            } else {
                profileImage.visibility = View.VISIBLE
                progressBarProfilePhoto.visibility = View.GONE
            }
        }
    }

    private fun observeUploadProfileImage() {
        profileViewModel.uploadProfileImageResult.observe(viewLifecycleOwner) { result: RequestStatus ->
            when (result) {
                RequestStatus.SUCCESS -> {
                    profileImage.visibility = View.VISIBLE
                    progressBarProfilePhoto.visibility = View.GONE
                }

                RequestStatus.IN_PROGRESS -> {
                    profileImage.visibility = View.GONE
                    progressBarProfilePhoto.visibility = View.VISIBLE
                }

                RequestStatus.FAILURE -> {
                    Toast.makeText(requireContext(), "Upload failed", Toast.LENGTH_SHORT).show()
                }

                else -> {
                }
            }
        }
    }
}
