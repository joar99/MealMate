package com.example.edamamnutrition.Adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.edamamnutrition.FavoriteDatabase.Favorite
import com.example.edamamnutrition.FavoriteDatabase.FavoriteDatabase
import com.example.edamamnutrition.R
import com.example.edamamnutrition.UserDatabase.UserDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteAdapter(var favoriteList: MutableList<Favorite>): RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    //SETTING UP A VARIABLE
    private lateinit var mListener: onItemClickListener

    //Page Two : Paragraph 2
    interface onItemClickListener {

        fun onItemClick(position: Int)

    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    //Page 3 : Paragraph 2
    fun setData(list: MutableList<Favorite>) {
        favoriteList = list
        notifyDataSetChanged()
    }

    fun removeFavorite(position: Int) {
        favoriteList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        println("OnCreateViewHolder")
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.favorite_recycler_rows, parent, false)
        return FavoriteViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val currentItem = favoriteList[position]
        val userDB = UserDatabase.getDatabase(holder.foodName.context)
        val favDB = FavoriteDatabase.getDatabase(holder.foodName.context)
        CoroutineScope(Dispatchers.IO).launch {
            val user = userDB.userDao().getUser(1)
            val cals = user.calories
            if (currentItem.calories > cals) {
                withContext(Main) {
                    holder.itemView.setBackgroundColor(Color.parseColor("#FF0000"))
                }
            } else {
                withContext(Main) {
                    holder.itemView.setBackgroundColor(Color.parseColor("#C4BFBE"))
                }
            }
        }

        //Page 2 : Paragraph 2
        holder.eatOnClick(userDB, currentItem, this)
        holder.favRemoveOnClick(currentItem, this, favDB, favoriteList, position)

        holder.favorite.setImageResource(R.drawable.green_heart)
        holder.foodName.setText(currentItem.label)
        holder.calories.setText(currentItem.calories.toString())

        //Page 2 : Paragraph 3
        Glide.with(holder.foodPicture.context)
            .load(currentItem.image)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.foodPicture)

    }

    override fun getItemCount(): Int {
        return favoriteList.size
    }

    class FavoriteViewHolder(itemView: View, listener: onItemClickListener): RecyclerView.ViewHolder(itemView) {
        val foodName: TextView = itemView.findViewById(R.id.favorite_recycler_tv)
        val calories: TextView = itemView.findViewById(R.id.favorite_recycler_calories)
        val foodPicture: ImageView = itemView.findViewById(R.id.favorite_recycler_iw)
        val eatBtn: Button = itemView.findViewById(R.id.eat_recycler_btn)
        val favorite: ImageButton = itemView.findViewById(R.id.favorite_recycler_favbtn)
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

        //Page 3 : Paragraph 3
        fun favRemoveOnClick(currentItem: Favorite, adapter: FavoriteAdapter, favDB: FavoriteDatabase, favoriteList: MutableList<Favorite>, position: Int) {
            favorite.setOnClickListener {
                val builder = AlertDialog.Builder(foodName.context)
                builder.setTitle("Warning")
                builder.setMessage("Do You Wish To Remove ${currentItem.label} From Favorites?")
                builder.setPositiveButton("Remove") { dialog, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        favDB.favoriteDao().deleteFavorite(currentItem.label)
                        withContext(Main) {
                            Toast.makeText(foodName.context, "Item Successfully Removed", Toast.LENGTH_SHORT).show()
                            favoriteList.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                    }
                }
                //Setting up the negative button and passing lambda function as argument
                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            }
        }

        fun eatOnClick(userDB: UserDatabase, currentItem: Favorite, adapter: FavoriteAdapter) {
            eatBtn.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val userCals = userDB.userDao().getUser(1)
                    val calsToWithdraw = userCals.calories - currentItem.calories
                    userDB.userDao().updateCals(calsToWithdraw, 1)
                }
                Toast.makeText(foodName.context, "Successfully Updated Intake", Toast.LENGTH_SHORT).show()
                adapter.notifyDataSetChanged()
            }
        }
    }

}