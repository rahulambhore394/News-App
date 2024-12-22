package com.rahul.newsnow.UI.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.rahul.newsnow.Models.Article
import com.rahul.newsnow.R
import com.rahul.newsnow.UI.NewsActivity
import com.rahul.newsnow.UI.NewsViewModel
import com.rahul.newsnow.databinding.FragmentArticlesBinding

class ArticlesFragment : Fragment(R.layout.fragment_articles) {

    lateinit var newsViewModel: NewsViewModel
    val args: ArticlesFragmentArgs by navArgs()
    lateinit var binding: FragmentArticlesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArticlesBinding.bind(view)

        newsViewModel = (activity as NewsActivity).newsViewModel
        val article = args.Article

        binding.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let{
                loadUrl(it)
            }
        }
        binding.fab.setOnClickListener{
            newsViewModel.addToFavourite(article)
            Snackbar.make(view,"Added To Favourite",Snackbar.LENGTH_SHORT).show()
        }
    }



}