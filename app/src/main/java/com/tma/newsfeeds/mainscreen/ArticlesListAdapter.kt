package com.tma.newsfeeds.mainscreen

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.tma.data.logmanager.LogDebug
import com.tma.data.model.Article
import com.tma.newsfeeds.R
import com.tma.newsfeeds.baseglide.GlideApp
import com.tma.newsfeeds.detailscreen.DetailActivity
import kotlinx.android.synthetic.main.item_article_in_recyclerview.view.*


class ArticlesListAdapter(private var articlesList: ArrayList<Article>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    private lateinit var requestOptions: RequestOptions
    //.signature(ObjectKey("key will be change in 12h"))
    lateinit var context: Context
    private var isPortrait: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Inflate the custom view from xml layout file
        context = parent.context
        isPortrait =
            (context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)

        requestOptions = RequestOptions().centerCrop()
            .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_glide_place_holder))
            .error(ContextCompat.getDrawable(context, R.drawable.ic_glide_place_holder))

        return if (viewType == VIEW_TYPE_ITEM) {
            val v = LayoutInflater.from(context)
                .inflate(R.layout.item_article_in_recyclerview, parent, false)
            ViewHolder(v)
        } else {
            val v = LayoutInflater.from(context)
                .inflate(R.layout.recyclerview_bottom_progressbar, parent, false)
            ProgressViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {

            val article = articlesList[position]

            GlideApp.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(article.imageUrl)
                .into(holder.imageView)


            if (isPortrait) {
                if (article.portraitContent == null) {
                    //Portrait screen: only show first sentence in article.Content + ...
                    //The first line displays about (> 60) characters
                    //The second line displays rest of text + ...
                    //Set ellipsize="end" and maxLines=2 in XML file to handle case length of sentence > width of TextView
                    if (article.content.length > 100) {
                        article.portraitContent =
                            article.content.substring(0, article.content.indexOf('.', 60)) + "..."
                    } else {
                        article.portraitContent = article.content
                    }
                }

                holder.tvContent.text = article.portraitContent
            } else {
                holder.tvContent.text = article.content
            }
            holder.tvTitle.text = article.title
            holder.tvUpdatedTime.text =
                context.getString(R.string.updated_time, article.lastModifiedDate)

            holder.linItemArticle.setOnClickListener {
                DetailActivity.article = article
                context.startActivity(Intent(context, DetailActivity::class.java))
            }

        } else {
            //Do whatever you want. Or nothing !!
        }
    }

    override fun getItemCount(): Int {
        // Return the size of list
        // Returns the total number of items in the data set held by the adapter.
        return articlesList.size
    }

    // This two methods useful for avoiding duplicate item
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return if (articlesList[position].id != -1L)
            VIEW_TYPE_ITEM
        else
            VIEW_TYPE_LOADING
    }

    fun addBottomProgressBar() {
        articlesList.add(Article())
        notifyItemInserted(articlesList.size - 1)
    }

    fun removeBottomProgressBar(isNotifyItemRemoved: Boolean) {
        LogDebug.e("NewsFeeds", "removeBottomProgressBar")
        if (articlesList[articlesList.size - 1].id == -1L) {
            articlesList.removeAt(articlesList.size - 1)
            LogDebug.e("NewsFeeds", "removeBottomProgressBar 1")
            if (isNotifyItemRemoved) {
                LogDebug.e("NewsFeeds", "removeBottomProgressBar 2")
                notifyItemRemoved(articlesList.size)
            }
        }
    }



    fun updateArticlesList(articlesList: ArrayList<Article>) {
        this.articlesList = articlesList
        notifyDataSetChanged()
    }

    /*
        RecyclerView.ViewHolder
            A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.imageView
        val tvTitle: TextView = itemView.tv_title
        val tvContent: TextView = itemView.tv_content
        val tvUpdatedTime: TextView = itemView.tv_updated_time
        val linItemArticle: View = itemView.lin_item_article
    }

    class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}