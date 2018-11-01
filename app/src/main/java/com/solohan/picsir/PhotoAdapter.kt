package com.solohan.picsir

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.solohan.picsir.dto.Photo
import kotlinx.android.synthetic.main.item_pic.view.*

class PhotoAdapter(val photo: List<Photo>): RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>(){
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
        // 이미지 정보
        // _m.jpg : 소형, _s.jpg : 작은 사각형, _t.jpg : 썸네일, _z.jpg : 중형, _b.jpg : 대형
        GlideApp.with(holder.image.context)
                .load(photo[position].getImgUrl() + "_z.jpg")
                .into(holder.image)

        holder.photo = photo[position]
    }

    class PhotoViewHolder(view: View, var photo: Photo? = null) : RecyclerView.ViewHolder(view){

        companion object {
            const val PHOTO_TITLE_KEY = "PHOTO_TITLE"
            const val PHOTO_URL_KEY = "PHOTO_URL"
        }

        val image = view.iv_pic_image!!
        val title = view.tv_pic_title!!

        init {
            view.setOnClickListener {

                val intent = Intent(view.context, DetailActivity::class.java)
                intent.putExtra(PHOTO_TITLE_KEY, photo?.title)
                intent.putExtra(PHOTO_URL_KEY, photo?.getImgUrl() + "_z.jpg")
                view.context.startActivity(intent)
            }
        }
    }
}