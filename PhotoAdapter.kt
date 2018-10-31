package com.solohan.picsir

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import kotlinx.android.synthetic.main.item_pic.view.*

class PhotoAdapter(private val photo: List<Photo>): RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PhotoViewHolder(layoutInflater.inflate(R.layout.item_pic, parent, false))
    }

    // 데이터셋 크기 리턴
    override fun getItemCount(): Int {
        return photo.size
    }

    // view 에 매칭
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.title.text = photo[position].title
        GlideApp.with(holder.image.context)
                .load(photo[position].getImgUrl() + "_m.jpg")
                .into(holder.image)

    }

    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val image = view.iv_pic_image!!
        val title = view.tv_pic_title!!
    }
}