package com.rahul.newsnow.UI.Fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rahul.newsnow.Adapters.NewsAdapters
import com.rahul.newsnow.R
import com.rahul.newsnow.UI.NewsActivity
import com.rahul.newsnow.UI.NewsViewModel
import com.rahul.newsnow.Utils.Constants
import com.rahul.newsnow.Utils.Resource
import com.rahul.newsnow.databinding.FragmentArticlesBinding
import com.rahul.newsnow.databinding.FragmentHeadLinesBinding

class HeadLinesFragment : Fragment(R.layout.fragment_head_lines) {

    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapters: NewsAdapters
    lateinit var retryButton: Button
    lateinit var errorText: TextView
    lateinit var itemHeadLinesError: CardView
    lateinit var binding: FragmentHeadLinesBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeadLinesBinding.bind(view)


        itemHeadLinesError = view.findViewById(R.id.itemHeadlinesError)

        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view : View = inflater.inflate(R.layout.item_error,null)

        retryButton = view.findViewById(R.id.retryButton)
        errorText = view.findViewById(R.id.errorText)

        newsViewModel = (activity as NewsActivity).newsViewModel
        setUpHeadLinesRecycler()

        newsAdapters.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("articles",it)
            }
            findNavController().navigate(R.id.action_headlinesFragment_to_articleFragment,bundle)

        }

        newsViewModel.headLines.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success<*> ->{
                    hideProgressBar()
                    hideErrorMessage()
                    response.date?.let { newsResponse ->
                        newsAdapters.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults/Constants.QUERY_PAGE_SIZE+2
                        isLastPage = newsViewModel.headLinesPages == totalPages
                        if (isLastPage){
                            binding.recyclerHeadlines.setPadding(0,0,0,0)

                        }
                    }
                }
                is Resource.Error<*> ->{
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity,"Sorry Error : $message",Toast.LENGTH_LONG).show()
                        showErrorMessage(message)
                    }
                }
                is Resource.Loading<*> ->{

                    showProgressBar()
                }
            }
        })

        retryButton.setOnClickListener{

            newsViewModel.getHeadLines("in")

        }
    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false

    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideErrorMessage() {
        itemHeadLinesError.visibility = View.INVISIBLE
    }

    private fun showErrorMessage(message: String) {
        itemHeadLinesError.visibility = View.VISIBLE
        errorText.text = message
        isError = true
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate =
                isNoErrors && isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                newsViewModel.getHeadLines("in")
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling = true
        }
    }


    private fun setUpHeadLinesRecycler() {
        newsAdapters = NewsAdapters()
        binding.recyclerHeadlines.apply {
            adapter = newsAdapters
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@HeadLinesFragment.scrollListener)
        }

    }
}