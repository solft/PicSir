package com.solohan.picsir.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.solohan.picsir.R
import com.solohan.picsir.dto.FlickrResponse
import com.solohan.picsir.dto.Photo
import com.solohan.picsir.utils.PhotoAdapter
import kotlinx.android.synthetic.main.activity_search.*
import okhttp3.*
import java.io.IOException

class SearchActivity : AppCompatActivity() {
    companion object {
        const val API_KEY = "Flickr_API_KEY"
    }
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var photoAdapter: PhotoAdapter

    var searchWord: String = ""
    var photosList: MutableList<Photo> = mutableListOf()
    var currentPage = 0
    var userScrolled = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        linearLayoutManager = LinearLayoutManager(this)
        gridLayoutManager = GridLayoutManager(this, 2)
        recycler_view_search.layoutManager = linearLayoutManager

        // soft keyboard 의 search 키 눌렀을 때
        edit_text_search.setOnEditorActionListener { v, actionId, _ ->
            v.clearFocus()
            v.hideKeyboard()
            Log.d("SearchActivity", "v.text = ${v.text}")
            if(v.text.toString() != "" && actionId == EditorInfo.IME_ACTION_SEARCH){
                searchWord = v.text.toString()
                Log.d("SearchActivity", "action search Word = $searchWord")
                // 첫 검색
                currentPage = 1
                imageLoad(searchWord)
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
                        if(photosList.isNotEmpty()){
                            // linear layout 이 아니면 linear layout 으로 변경
                            if(recycler_view_search.layoutManager != linearLayoutManager){
                                recycler_view_search.layoutManager = linearLayoutManager
                            }
                            true
                        }else{
                            false
                        }
                    }
                    R.id.menu_switch_grid -> {
                        if(photosList.isNotEmpty()){
                            // grid layout 이 아니면 grid layout 으로 변경
                            if(recycler_view_search.layoutManager != gridLayoutManager){
                                recycler_view_search.layoutManager = gridLayoutManager
                            }
                            true
                        }else{
                            false
                        }
                    }
                    R.id.menu_license -> {
                        val intent = Intent(this, LicenseActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }

            popupMenu.inflate(R.menu.menu_main)
            popupMenu.show()
        }

        /*recycler_view_search.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if(!recyclerView!!.canScrollVertically(1)){
                    userScrolled = true
                    tv_progress_guide.visibility = View.VISIBLE
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(userScrolled){
                    Log.d("SearchActivity", "Load more Photo current photo size = ${photosList.size}")
                    Toast.makeText(this@SearchActivity, "load new photo $currentPage", Toast.LENGTH_SHORT).show()
                    userScrolled = false
                    progress_bar_search.visibility = View.VISIBLE
                    imageLoad(searchWord)
                }
            }
        })*/

        recycler_view_search.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if(!recyclerView!!.canScrollVertically(1)){
                    if(userScrolled){
                        progress_bar_search.visibility = View.VISIBLE
                        imageLoad(searchWord)
                        userScrolled = false
                    }
                }
            }
        })

    }

    // soft keyboard 숨기기
    fun View.hideKeyboard(){
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    // 스크롤 밑으로 했을 때 새로운 이미지 로드
    fun imageLoad(searchWord: String){
        Log.d("SearchActivity", "새로운 이미지 로드")
        val url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=$API_KEY&text=$searchWord&safe_search=1&content_type=1&sort=interestingness-desc&per_page=20&page=$currentPage&format=json"

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
                    Log.d("SearchActivity", "first photo title = ${flickrResponse!!.photos.photo[0].title}")
                    Log.d("SearchActivity", "first photo img url = ${flickrResponse.photos.photo[0].getImgUrl()}")

                    runOnUiThread {
                        Log.d("SearchActivity", "runOnUiThread recycler view adapter start")
                        iv_enter_keyword.visibility = View.INVISIBLE
                        recycler_view_search.visibility = View.VISIBLE

                        if(currentPage==1){
                            photosList.clear()
                            photosList.addAll(flickrResponse.photos.photo)
                            currentPage++
                            photoAdapter = PhotoAdapter(photosList)
                            recycler_view_search.adapter = photoAdapter
                            userScrolled = true
                        }
                        else {
                            photosList.addAll(flickrResponse.photos.photo)
                            currentPage++
                            photoAdapter.update(photosList)
                            progress_bar_search.visibility = View.INVISIBLE
                            userScrolled = true
                        }
                    }
                }else{
                    Log.d("SearchActivity", "response Unsuccessful")
                }
            }
        })
    }

}
