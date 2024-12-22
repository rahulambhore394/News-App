package com.rahul.newsnow.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rahul.newsnow.Models.Article


@Dao
interface ArticleDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article) : Long

    @Query("Select * from articles")
    fun getAllArticles():LiveData<List<Article>>

    @Delete
    suspend fun deleteArticles(article: Article)
}