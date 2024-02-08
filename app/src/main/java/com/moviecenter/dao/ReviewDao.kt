package com.app.moviecenter.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.moviecenter.model.Review

@Dao
interface ReviewDao {

    @Query("SELECT * FROM Review")
    fun getAll(): LiveData<MutableList<Review>>

    @Query("SELECT * FROM Review where userEmail =:email")
    fun getMy(email: String): LiveData<MutableList<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg reviews: Review)

    @Query("DELETE FROM Review WHERE id =:reviewId")
    fun delete(reviewId: String)

    @Query("SELECT * FROM Review WHERE id =:id")
    fun getReviewById(id: String): LiveData<Review>
}