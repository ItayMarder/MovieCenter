import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.moviecenter.profile.UserMetaData
import com.app.moviecenter.singup.UserProperties
import com.app.moviecenter.utils.RequestStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileViewModel : ViewModel() {
    private val _showProfilePhoto = MutableLiveData<Uri?>()
    private val _uploadProfileImageResult = MutableLiveData<RequestStatus>()
    private val _changeNameResult = MutableLiveData<UserProperties?>()
    val showProfilePhoto: LiveData<Uri?> get() = _showProfilePhoto
    val uploadProfileImageResult: LiveData<RequestStatus> get() = _uploadProfileImageResult
    val changeNameResult: LiveData<UserProperties?> get() = _changeNameResult
    private val _initializeUserDataStatus = MutableLiveData<UserMetaData?>()

    val initializeUserDataStatus: MutableLiveData<UserMetaData?> get() = _initializeUserDataStatus

    private val storage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getProfileImage(userMetaData: UserMetaData) {
        val gsReference = storage.reference.child(userMetaData.profilePhoto)
        gsReference.downloadUrl
            .addOnSuccessListener { uri ->
                _showProfilePhoto.value = uri
            }
            .addOnCompleteListener {
                _showProfilePhoto.value = null
            }
    }

    fun uploadProfileImage(userMetaData: UserMetaData, imageUri: Uri) {
        val imageRef: StorageReference = storage.getReference(userMetaData.profilePhoto)

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                getProfileImage(userMetaData)
            }
            .addOnFailureListener {
                _uploadProfileImageResult.value = RequestStatus.FAILURE
            }
            .addOnProgressListener {
                _uploadProfileImageResult.value = RequestStatus.IN_PROGRESS
            }
            .addOnCompleteListener {
                _uploadProfileImageResult.value = RequestStatus.SUCCESS
            }
    }

    fun changeUserName(userMetaData: UserMetaData) {
        val user: MutableMap<String, Any> = HashMap()
        user["firstName"] = userMetaData.firstName.replaceFirstChar(Char::titlecase)
        user["lastName"] = userMetaData.lastName.replaceFirstChar(Char::titlecase)
        user["profilePhoto"] = userMetaData.profilePhoto
        db.collection("Users").document(userMetaData.email)
            .update(user)
            .addOnSuccessListener {
                _changeNameResult.value = UserProperties(
                    user["firstName"] as String,
                    user["lastName"] as String
                )
            }
            .addOnFailureListener {
                _changeNameResult.value = null
            }
    }

    fun getUserMetaData() {
        val auth = FirebaseAuth.getInstance()
        var newUserMetaData = UserMetaData("", "", "", "")
        val user = auth.currentUser
        val email = user?.email
        if (user != null) {
            db.collection("Users").document(email.toString())
                .get()
                .addOnSuccessListener {
                    newUserMetaData = UserMetaData(
                        email = user.email!!,
                        firstName = it.data?.get("firstName") as String,
                        lastName = it.data!!["lastName"] as String,
                        profilePhoto = it.data!!["profilePhoto"] as String
                    )

                }
                .addOnCompleteListener {
                    initializeUserDataStatus.value = newUserMetaData
                }
        }
    }
}

