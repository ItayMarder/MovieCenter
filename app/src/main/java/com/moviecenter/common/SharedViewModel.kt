package com.app.moviecenter.common

import androidx.lifecycle.ViewModel
import com.app.moviecenter.profile.UserMetaData

class SharedViewModel : ViewModel() {
    var userMetaData: UserMetaData = UserMetaData(
        firstName = "", lastName = "",
        email = "", profilePhoto = ""
    )
}