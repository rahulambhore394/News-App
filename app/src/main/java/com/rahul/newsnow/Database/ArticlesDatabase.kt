package com.rahul.newsnow.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.rahul.newsnow.Models.Article
import kotlinx.coroutines.internal.synchronized

@Database(
    entities = [Article::class],
    version = 1
)

@TypeConverters(Converters::class)
abstract class ArticlesDatabase : RoomDatabase(){

    abstract fun getArticleDAO() : ArticleDAO

    companion object {
        @Volatile
        private var instance: ArticlesDatabase? = null
        private var LOCK = Any()


        operator fun invoke(context: Context) = instance ?: kotlin.synchronized(LOCK) {
            instance ?: createDatabase(context).also {
                instance = it

            }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            ArticlesDatabase::class.java, "Articles_DB.db"
        ).build()
    }
}