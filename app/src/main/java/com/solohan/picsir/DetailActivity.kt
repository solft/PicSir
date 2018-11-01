package com.solohan.picsir

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_detail.*
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

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

        fab_save_detail.setOnClickListener {
            // 파일 저장
            savePhoto(photoUrl, photoTitle)
        }
    }

    private fun savePhoto(downloadUrl: String, downloadTitle: String){
        val okClient = OkHttpClient()
        val okRequest = Request.Builder().url(downloadUrl).build()

        okClient.newCall(okRequest).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d("DetailActivity", "download failure ${e.printStackTrace()}")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("DetailActivity", "response to bitmap")
                val inputStream = response.body()?.byteStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)

                runOnUiThread {
                    try{
                        Log.d("DetailActivity", "try file save")

                        val savedImageURL = MediaStore.Images.Media.insertImage(
                                contentResolver,
                                bitmap,
                                downloadTitle,
                                "Image of $downloadTitle"
                        )

                        Log.d("DetailActivity", "saved in -> ${Uri.parse(savedImageURL)}")
                        Toast.makeText(applicationContext, "Saved: ${Uri.parse(savedImageURL)}", Toast.LENGTH_LONG).show()
                    }catch (e: IOException){
                        e.printStackTrace()
                    }
                }
            }
        })
    }
}
