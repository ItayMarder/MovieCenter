package com.app.moviecenter.singup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.moviecenter.R
import com.app.moviecenter.login.UserCredentials
import com.app.moviecenter.utils.checkCredentials
import com.app.moviecenter.utils.isString
import com.app.moviecenter.utils.manageViews

class SignUpFragment : Fragment() {

    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var signUpButton: Button
    private lateinit var messageBox: TextView
    private lateinit var progressBarSignUp: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(
            R.layout.fragment_signup, container, false
        )
        emailInput = view.findViewById(R.id.email_input)
        passwordInput = view.findViewById(R.id.password_input)
        firstNameInput = view.findViewById(R.id.first_name_input)
        lastNameInput = view.findViewById(R.id.last_name_input)
        signUpButton = view.findViewById(R.id.sign_up_button)
        messageBox = view.findViewById(R.id.message_box)
        progressBarSignUp = view.findViewById(R.id.progress_bar_sign_up)

        handleSignUpClick(signUpButton)
        observeSignUpResult()
        return view
    }

    override fun onResume() {
        resetParameters()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        signUpViewModel.clearSignUpResult()
    }

    private fun resetParameters() {
        emailInput.text.clear()
        passwordInput.text.clear()
        firstNameInput.text.clear()
        lastNameInput.text.clear()
        messageBox.text = ""
    }

    private fun observeSignUpResult() {
        signUpViewModel.signUpResult.observe(viewLifecycleOwner) { result: String ->
            if (result == "Success") {
                findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
            } else {
                resetParameters()
                manageViews(
                    emailInput, passwordInput, firstNameInput, messageBox,
                    lastNameInput, signUpButton, messageBox, mode = "VISIBLE"
                )
                messageBox.text = result
            }
            progressBarSignUp.visibility = View.GONE
        }
    }

    private fun handleSignUpClick(signUpButton: Button) {
        signUpButton.setOnClickListener {
            messageBox.visibility = View.INVISIBLE
            val credentials =
                UserCredentials(emailInput.text.toString(), passwordInput.text.toString())
            val userProperties =
                UserProperties(firstNameInput.text.toString(), lastNameInput.text.toString())
            if (checkCredentials(credentials, emailInput, passwordInput) &&
                checkUserProperties(userProperties, firstNameInput, lastNameInput)
            ) {
                manageViews(
                    emailInput, passwordInput, firstNameInput, messageBox,
                    lastNameInput, signUpButton, mode = "GONE"
                )
                progressBarSignUp.visibility = View.VISIBLE
                signUpViewModel.signUpUser(credentials, userProperties)
            }
        }
    }

    private fun checkUserProperties(
        userProperties: UserProperties, firstNameInput: EditText,
        lastNameInput: EditText
    ): Boolean {
        if (userProperties.firstName.isEmpty() || !isString(userProperties.firstName)) {
            firstNameInput.error = "Enter valid first name"
        } else if (userProperties.lastName.isEmpty() || !isString(userProperties.lastName)) {
            lastNameInput.error = "Enter valid last name"
        } else {
            return true
        }
        return false
    }
}