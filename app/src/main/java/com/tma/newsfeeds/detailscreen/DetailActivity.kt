package com.tma.newsfeeds.detailscreen

import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.tma.data.model.Article
import com.tma.domain.detailscreen.DetailPresenter
import com.tma.domain.detailscreen.DetailView
import com.tma.newsfeeds.R
import com.tma.newsfeeds.base.BaseActivity
import com.tma.newsfeeds.baseglide.GlideApp
import kotlinx.android.synthetic.main.action_bar.*
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : BaseActivity<DetailPresenter>(), DetailView {

    companion object {
        var article: Article? = null
    }

    lateinit var requestOptions: RequestOptions

    override fun onBackPressed() {
        //clean data
        article = null
        super.onBackPressed()
    }

    override fun getLayout(): Int = R.layout.activity_detail

    override fun initViews() {
        requestOptions = RequestOptions()
            .placeholder(ContextCompat.getDrawable(this, R.drawable.ic_glide_place_holder))
            .error(ContextCompat.getDrawable(this, R.drawable.ic_glide_place_holder))

        ic_back?.run {
            visibility = View.VISIBLE
            setOnClickListener {
                onBackPressed()
            }
        }

        article?.also {
            tv_title.text = it.title
            tv_content.text = it.content
            tv_updated_time.text = getString(R.string.updated_time, it.lastModifiedDate)
            //
            GlideApp.with(this@DetailActivity)
                .setDefaultRequestOptions(requestOptions)
                .load(it.imageUrl)
                .into(imageView)
        }

    }
}
