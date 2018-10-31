package com.solohan.picsir

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.gson.GsonBuilder
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

        // 임시 데이터셋 시작
        val picArray = ArrayList<Photo>()

        picArray.add(Photo("id1", "secret1", "server1", 1, "title1"))
        picArray.add(Photo("id2", "secret2", "server2", 2, "title2"))
        picArray.add(Photo("id3", "secret3", "server3", 3, "title3"))
        picArray.add(Photo("id4", "secret4", "server4", 4, "title4"))
        picArray.add(Photo("id5", "secret5", "server5", 5, "title5"))
        picArray.add(Photo("id6", "secret6", "server6", 6, "title6"))
        // 임시 데이터셋 끝


        // recyclerView 시작
        /*
        viewManager = GridLayoutManager(this, 2)
        viewAdapter = PhotoAdapter(picArray)

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view_search).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        */
        // recyclerView 끝

        // 검색 시작
        btn_search_search.isFocusable = false
        btn_search_search.setOnClickListener {
            // editText 로 부터 검색어 읽어서 함수의 argument 로 전달
            // 아직 띄어쓰기는 구현 안함 (한 단어만)
            val searchWord = edit_text_search.text.toString()
            Log.d("SearchActivity", "searchWord = $searchWord")
            searchImageWithWord(searchWord)
        }
    }


    fun searchImageWithWord(searchWord: String){
        Log.d("SearchActivity", "검색 시작")
        val API_KEY = "cef74a9189ee4116482425e2489bc861"

        Log.d("SearchActivity", "searchImageWithWord searchWord : $searchWord")
        val url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=$API_KEY&text=$searchWord&safe_search=1&content_type=1&sort=interestingness-desc&per_page=5&format=json"

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
                        viewManager = GridLayoutManager(this@SearchActivity, 2)
                        viewAdapter = PhotoAdapter(flickrResponse.photos.photo)

                        recyclerView = findViewById<RecyclerView>(R.id.recycler_view_search).apply {
                            setHasFixedSize(true)
                            layoutManager = viewManager
                            adapter = viewAdapter
                        }
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
