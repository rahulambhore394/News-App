package com.rahul.newsnow.Repository

import com.rahul.newsnow.APIS.RetrofitInstance
import com.rahul.newsnow.Database.ArticlesDatabase
import com.rahul.newsnow.Models.Article

class NewsRepository(val db : ArticlesDatabase) {

    suspend fun getHeadLines(countryCode : String , pageNumber : Int) =
        RetrofitInstance.api.getHeadLines(countryCode,pageNumber)

    suspend fun searchNews(searchQuery : String , pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery,pageNumber)

    suspend fun upsert(article : Article) = db.getArticleDAO().upsert(article)

    fun getFavouriteNews() = db.getArticleDAO().getAllArticles()

    suspend fun deleteArticles(article: Article) = db.getArticleDAO().deleteArticles(article)
}