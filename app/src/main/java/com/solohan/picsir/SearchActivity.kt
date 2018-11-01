package com.solohan.picsir

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.inputmethod.EditorInfo
import com.google.gson.GsonBuilder
import com.solohan.picsir.dto.FlickrResponse
import com.solohan.picsir.dto.Photo
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.*
import java.io.IOException

class SearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recycler_view_search.layoutManager = LinearLayoutManager(this)

        // 검색 시작
        btn_search_search.isFocusable = false
        btn_search_search.setOnClickListener {
            // editText 로 부터 검색어 읽어서 함수의 argument 로 전달
            // 아직 띄어쓰기는 구현 안함 (한 단어만)
            val searchWord = edit_text_search.text.toString()
            Log.d("SearchActivity", "searchWord = $searchWord")
            searchImageWithWord(searchWord)
        }
        btn_search_search.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                val searchWord = edit_text_search.text.toString()
                Log.d("SearchActivity", "action search Word = $searchWord")
                searchImageWithWord(searchWord)
                true
            }else{
                false
            }
        }
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
                    val flickrResponse = gson.fromJson(body, FlickrResponse::class.java)
                    Log.d("SearchActivity", "flickrResponse = $flickrResponse")
                    Log.d("SearchActivity", "first photo title = ${flickrResponse.photos.photo[0].title}")
                    Log.d("SearchActivity", "first photo img url = ${flickrResponse.photos.photo[0].getImgUrl()}")

                    runOnUiThread {
                        Log.d("SearchActivity", "runOnUiThread recycler view adapter start")

                        recycler_view_search.adapter = PhotoAdapter(flickrResponse.photos.photo)

                        /*
                        이렇게 하면 다시 SearchActivity 로 갔을 때 검색한 정보가 없어진다.
                        viewManager = GridLayoutManager(this@SearchActivity, 2)
                        viewAdapter = PhotoAdapter(flickrResponse.photos.photo)

                        recyclerView = findViewById<RecyclerView>(R.id.recycler_view_search).apply {
                            setHasFixedSize(true)
                            layoutManager = viewManager
                            adapter = viewAdapter
                        }
                        */
                    }

                    //val gson = GsonBuilder().setPrettyPrinting().create()
                    //var picList = gson.fromJson(body)

                    //Log.d("SearchActivity", "gson: $gson")
                    //Log.d("SearchActivity", "flickrResponse: $flickrResponse")
                }else{
                    Log.d("SearchActivity", "response Unsuccessful")
                }
            }
        })
    }
}
