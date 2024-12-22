package com.rahul.newsnow.UI.Fragments

import android.content.Context
import android.media.RouteListingPreference.Item
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.snackbar.Snackbar
import com.rahul.newsnow.Adapters.NewsAdapters
import com.rahul.newsnow.R
import com.rahul.newsnow.UI.NewsActivity
import com.rahul.newsnow.UI.NewsViewModel
import com.rahul.newsnow.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {


    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapters: NewsAdapters
    lateinit var binding: FragmentFavoriteBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoriteBinding.bind(view)




        newsViewModel = (activity as NewsActivity).newsViewModel
        setUpFavoritesRecycler()

        newsAdapters.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("articles",it)
            }
            findNavController().navigate(R.id.action_favouritesFragment_to_articleFragment,bundle)

        }

       val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN,
           ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
           override fun onMove(
               recyclerView: RecyclerView,
               viewHolder: RecyclerView.ViewHolder,
               target: RecyclerView.ViewHolder
           ): Boolean {
               return true
           }

           override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
               val position = viewHolder.adapterPosition
               val article = newsAdapters.differ.currentList[position]
               newsViewModel.deleteArticle(article)
               Snackbar.make(view, "Removed From Favourites", Snackbar.LENGTH_LONG).apply {
                   setAction("Undo") {
                       newsViewModel.addToFavourite(article)
                   }.show()
               }
           }
       }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.recyclerFavourites)
        }
        newsViewModel.getFavouriteNews().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapters.differ.submitList(articles)
        })
    }


    private fun setUpFavoritesRecycler(){
        newsAdapters = NewsAdapters()
        binding.recyclerFavourites.apply {
            adapter = newsAdapters
            layoutManager = LinearLayoutManager(activity)

        }

}}
