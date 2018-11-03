package com.solohan.picsir.activities

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import com.solohan.picsir.R
import com.solohan.picsir.utils.GlideApp
import com.solohan.picsir.utils.ManagePermissions
import com.solohan.picsir.utils.PhotoAdapter
import com.solohan.picsir.utils.toast
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.dialog_detail.view.*
import okhttp3.*
import java.io.IOException

class DetailActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_REQUEST_CODE = 11
    }

    private lateinit var managePermissions: ManagePermissions
    var photoSaveFlag = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val list = listOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        managePermissions = ManagePermissions(this, list, PERMISSION_REQUEST_CODE)

        val photoTitle = intent.getStringExtra(PhotoAdapter.PhotoViewHolder.PHOTO_TITLE_KEY)
        supportActionBar?.title = photoTitle

        val photoUrl = intent.getStringExtra(PhotoAdapter.PhotoViewHolder.PHOTO_URL_KEY)
        GlideApp.with(this)
                .load(photoUrl)
                .into(iv_photo_detail)

        fab_save_detail.setOnClickListener {
            // 파일 저장
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                managePermissions.checkPermissions()

            if(photoSaveFlag)
                savePhoto(photoUrl, photoTitle)
        }

        iv_photo_detail.setOnClickListener {
            val mBuilder = AlertDialog.Builder(this@DetailActivity)
            val mView = layoutInflater.inflate(R.layout.dialog_detail, null)
            GlideApp.with(this)
                    .load(photoUrl)
                    .into(mView.dialog_detail_photo_view)
            mBuilder.setView(mView)
            val mDialog = mBuilder.create()
            mDialog.show()
        }
    }

    private fun savePhoto(downloadUrl: String, downloadTitle: String){
        progress_bar_download.visibility = View.VISIBLE
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
                        progress_bar_download.visibility = View.INVISIBLE
                        Log.d("DetailActivity", "saved in -> ${Uri.parse(savedImageURL)}")
                        Toast.makeText(applicationContext, "Saved: ${Uri.parse(savedImageURL)}", Toast.LENGTH_LONG).show()
                    }catch (e: IOException){
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_REQUEST_CODE -> {
                val isPermissionsGranted = managePermissions.processPermissionResult(requestCode, permissions, grantResults)

                photoSaveFlag = if(isPermissionsGranted){
                    toast("권한 승인")
                    true
                }else{
                    toast("권한 거부")
                    false
                }
                return
            }
        }
    }
}
