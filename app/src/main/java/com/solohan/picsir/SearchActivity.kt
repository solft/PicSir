package com.solohan.picsir

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.solohan.picsir.dto.FlickrResponse
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.*
import java.io.IOException

class SearchActivity : AppCompatActivity() {
    var flickrResponse :FlickrResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recycler_view_search.layoutManager = LinearLayoutManager(this)

        // soft keyboard 의 search 키 눌렀을 때
        edit_text_search.setOnEditorActionListener { v, actionId, _ ->
            v.clearFocus()
            v.hideKeyboard()
            Log.d("SearchActivity", "v.text = ${v.text}")
            if(v.text.toString() != "" && actionId == EditorInfo.IME_ACTION_SEARCH){
                val searchWord = v.text.toString()
                Log.d("SearchActivity", "action search Word = $searchWord")
                searchImageWithWord(searchWord)
                true
            }else if(v.text.toString() == ""){
                // 입력 안했을 때
                Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
                false
            }else{
                false
            }
        }


        // popup menu
        iv_setting.setOnClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.menu_switch_linear -> {
                        if(flickrResponse != null){
                            recycler_view_search.layoutManager = LinearLayoutManager(this)
                            recycler_view_search.adapter = PhotoAdapter(flickrResponse!!.photos.photo)
                            true
                        }else{
                            false
                        }
                    }
                    R.id.menu_switch_grid -> {
                        if(flickrResponse != null){
                            recycler_view_search.layoutManager = GridLayoutManager(this, 2)
                            recycler_view_search.adapter = PhotoAdapter(flickrResponse!!.photos.photo)
                            true
                        }else{
                            false
                        }
                    }
                    R.id.menu_license -> {
                        val intent = Intent(this, LicenceActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.inflate(R.menu.menu_main)
            popupMenu.show()
        }
    }

    // soft keyboard 숨기기
    fun View.hideKeyboard(){
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }


    fun searchImageWithWord(searchWord: String){
        Log.d("SearchActivity", "검색 시작")
        val API_KEY = "cef74a9189ee4116482425e2489bc861"

        Log.d("SearchActivity", "searchImageWithWord searchWord : $searchWord")
        val url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=$API_KEY&text=$searchWord&safe_search=1&content_type=1&sort=interestingness-desc&per_page=50&format=json"

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d("SearchActivity", "OkHttp 응답 실패")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("SearchActivity", "OkHttp 응답 받음")

                if(response.isSuccessful){
                    Log.d("SearchActivity", "response Successful")

                    var body = response.body()?.string()
                    body = body?.replace("jsonFlickrApi(", "")
                    body = body?.replace(")", "")

                    Log.d("SearchActivity", "body = $body")

                    val gson = GsonBuilder().setPrettyPrinting().create()
                    flickrResponse = gson.fromJson(body, FlickrResponse::class.java)
                    Log.d("SearchActivity", "flickrResponse = $flickrResponse")
                    Log.d("SearchActivity", "first photo title = ${flickrResponse!!.photos.photo[0].title}")
                    Log.d("SearchActivity", "first photo img url = ${flickrResponse!!.photos.photo[0].getImgUrl()}")

                    runOnUiThread {
                        Log.d("SearchActivity", "runOnUiThread recycler view adapter start")
                        iv_enter_keyword.visibility = View.INVISIBLE
                        recycler_view_search.visibility = View.VISIBLE
                        recycler_view_search.adapter = PhotoAdapter(flickrResponse!!.photos.photo)
                    }
                }else{
                    Log.d("SearchActivity", "response Unsuccessful")
                }
            }
        })
    }
}
