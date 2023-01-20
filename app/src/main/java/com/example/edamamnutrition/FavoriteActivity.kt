package com.example.edamamnutrition

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edamamnutrition.Adapter.FavoriteAdapter
import com.example.edamamnutrition.FavoriteDatabase.Favorite
import com.example.edamamnutrition.FavoriteDatabase.FavoriteDatabase
import com.example.edamamnutrition.UserDatabase.UserDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

class FavoriteActivity : AppCompatActivity() {

    private lateinit var favoriteRecyclerView: RecyclerView
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var favoriteList: MutableList<Favorite>
    private lateinit var userDB: UserDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_history)

        val backBtn: ImageButton = findViewById(R.id.favorite_back_btn)
        backBtn.setOnClickListener {
            finish()
        }

        favoriteRecyclerView = findViewById(R.id.favorites_recycler);
        favoriteList = mutableListOf()
        favoriteAdapter = FavoriteAdapter(favoriteList)
        favoriteRecyclerView.layoutManager = LinearLayoutManager(this@FavoriteActivity)
        favoriteRecyclerView.setHasFixedSize(true)
        favoriteRecyclerView.adapter = favoriteAdapter
        favoriteAdapter.setOnItemClickListener(object: FavoriteAdapter.onItemClickListener {
            //PAGE 2 : Paragraph 2
            override fun onItemClick(position: Int) {
                var intent = Intent()
                intent.setAction(Intent.ACTION_VIEW)
                intent.addCategory(Intent.CATEGORY_BROWSABLE)
                intent.setData(Uri.parse(favoriteList[position].url))
                startActivity(intent)
            }

        })
        Toast.makeText(this@FavoriteActivity, favoriteList.size.toString(), Toast.LENGTH_SHORT).show()

        //Page 3 : Paragraph 2
        var favoriteDB = FavoriteDatabase.getDatabase(this)
        userDB = UserDatabase.getDatabase(this)
        CoroutineScope(Dispatchers.IO).launch {
            var user = userDB.userDao().getUser(1)
            favoriteList = favoriteDB.favoriteDao().getFavorites(user.maxSearch)
            withContext(Main) {
                favoriteAdapter.setData(favoriteList)
            }
        }

    }
}