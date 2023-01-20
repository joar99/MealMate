package com.example.edamamnutrition.Adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.edamamnutrition.FavoriteDatabase.Favorite
import com.example.edamamnutrition.FavoriteDatabase.FavoriteDatabase

import com.example.edamamnutrition.JSONModels.Recipe
import com.example.edamamnutrition.MainActivity

import com.example.edamamnutrition.R
import com.example.edamamnutrition.UserDatabase.UserDatabase
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main

//HERE IS THE ADAPTER FOR THE RECYCLERVIEW FOUND IN THE MAIN ACTIVITY
//REPORT 54
class RecipeAdapter(val foodList: MutableList<Recipe>) :
    RecyclerView.Adapter<RecipeAdapter.FoodViewHolder>() {

    //SETTING UP A VARIABLE
    private lateinit var mListener: onItemClickListener

    //Page 2 : Paragraph 2
    interface onItemClickListener {

        fun onItemClick(position: Int)

    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.main_recycler_rows,
        parent, false)

        return FoodViewHolder(itemView, mListener)

    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        //GETTING THE VALUES FROM THE DATABASES AS THESE HAVE AN EFFECT ON HOW THE VIEW IS DISPLAYED
        val favDB = FavoriteDatabase.getDatabase(holder.label.context)
        val userDB = UserDatabase.getDatabase(holder.label.context)
        //GETTING THE POSITION VALUE
        val currentItem = foodList[position]

        //CHECKS IF THE RECIPE ITEM SHOULD BE MARKED AS FAVORITE BY COMPARING ITS LABEL
        //AGAINST THE LABEL OF A FAVORITE IN THE DB
        CoroutineScope(Dispatchers.IO).launch {
            val fav = favDB.favoriteDao().getFavorite(currentItem.label)
            val user = userDB.userDao().getUser(1)
            val cals = user.calories
            withContext(Main) {
                holder.favorite.setImageResource(
                    if (fav != null) R.drawable.green_heart else R.drawable.ic_add_favorites
                )

                holder.itemView.setBackgroundColor(
                    Color.parseColor(if (currentItem.calories > cals) "#FF0000" else "#C4BFBE")
                )
            }
        }

        //SETTING VALUES FOR ITEMS IN RECYCLERVIEW
        holder.label.setText(currentItem.label)
        holder.calories.setText(currentItem.calories.toString())
        //USING TEXTUTILS TO SPLIT A LIST OF STRINGS AND DISPLAY THEM
        holder.diet.setText(currentItem.dietLabels?.let { TextUtils.join(", ", it) })
        holder.meal.setText(currentItem.dishType?.let { TextUtils.join(", ", it) })
        //USING GLIDE TO DISPLAY THE IMAGE
        //Page 2 : Paragraph 3
        Glide.with(holder.imageView.context)
            .load(currentItem.image)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.imageView)

        //Page 2 : Paragraph 2
        holder.eatOnClick(currentItem, this)
        holder.favoriteOnClick(currentItem, this)
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

        class FoodViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
            val imageView : ImageView = itemView.findViewById(R.id.main_imageview_recycler)
            val label : TextView = itemView.findViewById(R.id.main_recycler_tv)
            val calories: TextView = itemView.findViewById(R.id.main_recycler_tv_calories)
            val eatBtn: Button = itemView.findViewById(R.id.main_select_recycler_btn)
            val diet : TextView = itemView.findViewById(R.id.main_recycler_tv_diet)
            val meal : TextView = itemView.findViewById(R.id.main_recycler_tv_meal)
            val favorite : ImageButton = itemView.findViewById(R.id.main_recycler_favorite)
            init {

                itemView.setOnClickListener {
                    listener.onItemClick(adapterPosition)
                }

            }
            fun eatOnClick(currentItem: Recipe, adapter: RecipeAdapter) {
                eatBtn.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        val userDB = UserDatabase.getDatabase(label.context)
                        val userCals = userDB.userDao().getUser(1)
                        val calsToWithdraw = userCals.calories - currentItem.calories
                        userDB.userDao().updateCals(calsToWithdraw, 1)

                    }
                    Toast.makeText(label.context, "Successfully Updated Intake", Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                }
            }

            fun favoriteOnClick(currentItem: Recipe, adapter: RecipeAdapter) {
                favorite.setOnClickListener {
                    val favorite = Favorite(currentItem.label, currentItem.image, currentItem.url, currentItem.calories)
                    CoroutineScope(Dispatchers.IO).launch {
                        val favDB = FavoriteDatabase.getDatabase(label.context)
                        if (favDB.favoriteDao().getFavorite(currentItem.label) === null) {
                            favDB.favoriteDao().addFavorite(favorite)
                            withContext(Main) {
                                Toast.makeText(label.context, "Added To Favorites", Toast.LENGTH_SHORT).show()
                                adapter.notifyDataSetChanged()
                            }
                        } else {
                            favDB.favoriteDao().deleteFavorite(favorite.label)
                            withContext(Main) {
                                Toast.makeText(label.context, "Removed From Favorites", Toast.LENGTH_SHORT).show()
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }

}

