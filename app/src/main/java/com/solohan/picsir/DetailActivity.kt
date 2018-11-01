package com.solohan.picsir

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val photoTitle = intent.getStringExtra(PhotoAdapter.PhotoViewHolder.PHOTO_TITLE_KEY)
        supportActionBar?.title = photoTitle

        val photoUrl = intent.getStringExtra(PhotoAdapter.PhotoViewHolder.PHOTO_URL_KEY)
        GlideApp.with(this)
                .load(photoUrl)
                .into(iv_photo_detail)
    }
}
