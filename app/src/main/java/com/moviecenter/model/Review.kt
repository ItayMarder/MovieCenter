package com.app.moviecenter.model

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.moviecenter.base.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.util.Date

enum class GenreType(val type: String) {
    HORROR("horror"),
    COMEDY("comedy"),
    CRIME("crime"),
    ACTION("action"),
    FICTION("fiction"),
    DRAMA("drama"),
    ROMANCE("romance"),
    THRILL("thrill"),
    MUSICAL("musical"),
    FANTASY("fantasy"),
    HISTORY("history"),
    OTHER("other");

    companion object {
        fun fromString(type: String): GenreType {
            return entries.find { it.type.equals(type, ignoreCase = true) }
                ?: OTHER
        }
    }
}

@Entity
data class Review(
    @PrimaryKey var name: String,
    var genreType: GenreType = GenreType.OTHER,
    var description: String = "",
    var imdbId: String = "",
    var rating: Double = 0.0,
    var picture: String = "",
    var id: String = "",
    var userEmail: String = "",
    var createdAt: Long = Date().time,
    var lastUpdated: Long? = null
) {

    companion object {

        var lastUpdated: Long
            get() {
                return MyApplication.Globals
                    .appContext?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                    ?.getLong(GET_LAST_UPDATED, 0) ?: 0
            }
            set(value) {
                MyApplication.Globals
                    ?.appContext
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.edit()
                    ?.putLong(GET_LAST_UPDATED, value)?.apply()
            }


        const val NAME_KEY = "name"
        const val GENRETYPE_KEY = "genreType"
        const val DESCRIPTION_KEY = "description"
        const val IMDBID_KEY = "imdbId"
        const val RATING_KEY = "rating"
        const val PICTURE_KEY = "picture"
        const val ID_KEY = "id"
        const val USEREMAIL_KEY = "userEmail"
        const val CREATEDAT_KEY = "createdAt"
        const val LAST_UPDATED = "lastUpdated"
        const val GET_LAST_UPDATED = "get_last_updated"

        fun fromJSON(json: Map<String, Any>): Review {
            val name = json[NAME_KEY] as? String ?: ""
            val genreType = GenreType.fromString(json[GENRETYPE_KEY] as String) as? GenreType ?: GenreType.OTHER
            val description = json[DESCRIPTION_KEY] as? String ?: ""
            val imdbId = json[IMDBID_KEY] as? String ?: ""
            val rating = json[RATING_KEY] as? Double ?: 0.0
            val picture = json[PICTURE_KEY] as? String ?: ""
            val id = json[ID_KEY] as? String ?: ""
            val userEmail = json[USEREMAIL_KEY] as? String ?: ""
            val createdAt = json[CREATEDAT_KEY] as? Long ?: Date().time

            val review = Review(
                name,
                genreType,
                description,
                imdbId,
                rating,
                picture,
                id,
                userEmail,
                createdAt
            )

            val timestamp: Timestamp? = json[LAST_UPDATED] as? Timestamp
            timestamp?.let {
                review.lastUpdated = it.seconds
            }

            return review
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                NAME_KEY to name,
                GENRETYPE_KEY to genreType,
                DESCRIPTION_KEY to description,
                IMDBID_KEY to imdbId,
                RATING_KEY to rating,
                PICTURE_KEY to picture,
                ID_KEY to id,
                USEREMAIL_KEY to userEmail,
                CREATEDAT_KEY to createdAt,
                LAST_UPDATED to FieldValue.serverTimestamp()
            )
        }
}