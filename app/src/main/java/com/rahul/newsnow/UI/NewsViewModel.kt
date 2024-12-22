package com.rahul.newsnow.UI

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.rahul.newsnow.Models.Article
import com.rahul.newsnow.Models.NewsResponse
import com.rahul.newsnow.Repository.NewsRepository
import com.rahul.newsnow.Utils.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class NewsViewModel(app : Application, val newsRepository: NewsRepository) : AndroidViewModel(app) {

    val headLines : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var headLinesPages = 1
    var headLinesResponse : NewsResponse? = null
    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPages = 1
    var searchNewsResponse : NewsResponse? = null
    var newsSearchQuery : String? = null
    var oldSearchQuery : String? = null


    init {
        getHeadLines("in")
    }

    fun getHeadLines(countryCode : String) = viewModelScope.launch {
        headLinesInternet(countryCode)
    }
    fun searchNews(searchQuery : String) = viewModelScope.launch {
        searchNewsInternet(searchQuery)
    }


    private fun handleHeadLinesResponse(response : Response<NewsResponse>) : Resource<NewsResponse> {
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                headLinesPages++
                if (headLinesResponse == null){
                    headLinesResponse = resultResponse
                }else{
                    val oldArticles = headLinesResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(headLinesResponse ?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response : Response<NewsResponse>) : Resource<NewsResponse> {
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                if (searchNewsResponse == null || newsSearchQuery != oldSearchQuery){
                    searchNewsPages = 1
                    oldSearchQuery = newsSearchQuery
                    searchNewsResponse = resultResponse
                }else{
                    searchNewsPages++
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?:resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    fun addToFavourite(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getFavouriteNews() = newsRepository.getFavouriteNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticles(article)
    }
    fun internetConnection(context : Context) : Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE)as ConnectivityManager).apply {
            return getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }?:false
        }
    }
    private suspend fun headLinesInternet(countryCode : String){
        headLines.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())){
                val response = newsRepository.getHeadLines(countryCode,headLinesPages)
                headLines.postValue(handleHeadLinesResponse(response))

            }else{
                headLines.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t : Throwable){
            when(t){
                is IOException -> headLines.postValue(Resource.Error("Unable To Connect"))
                else -> headLines.postValue(Resource.Error("No Signal"))
            }
        }
    }
    private suspend fun searchNewsInternet(searchQuery: String){
        newsSearchQuery = searchQuery
            searchNews.postValue(Resource.Loading())
        try {
            if (internetConnection(this.getApplication())){
                val response = newsRepository.searchNews(searchQuery,searchNewsPages)
                searchNews.postValue(handleHeadLinesResponse(response))

            }else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t : Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Unable To Connect"))
                else -> searchNews.postValue(Resource.Error("No Signal"))
            }
        }
    }
}