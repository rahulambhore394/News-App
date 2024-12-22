package com.rahul.newsnow.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rahul.newsnow.Models.Article
import com.rahul.newsnow.R

class NewsAdapters : RecyclerView.Adapter<NewsAdapters.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView)

    lateinit var articleImage : ImageView
    lateinit var articleTitle : TextView
    lateinit var articleDescription : TextView
    lateinit var articleSource : TextView
    lateinit var articleDateTime : TextView


    private val differCallBack = object : DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
       return ArticleViewHolder(
           LayoutInflater.from(parent.context).inflate(R.layout.item_news,parent,false)

       )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener : ((Article) -> Unit)? = null

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        articleImage = holder.itemView.findViewById(R.id.articleImage)
        articleTitle = holder.itemView.findViewById(R.id.articleTitle)
        articleDescription = holder.itemView.findViewById(R.id.articleDescription)
        articleDateTime = holder.itemView.findViewById(R.id.articleDateTime)
        articleSource = holder.itemView.findViewById(R.id.articleSource)

        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(articleImage)
            articleSource.text = article.source?.name
            articleTitle.text= article.title
            articleDescription.text = article.description.toString()
            articleDateTime.text = article.publishedAt

            setOnClickListener{
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }
       fun setOnItemClickListener(listener : (Article) ->Unit){
           onItemClickListener = listener
       }
}