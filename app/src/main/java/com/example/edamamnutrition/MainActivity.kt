package com.example.edamamnutrition

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.edamamnutrition.Adapter.RecipeAdapter
import com.example.edamamnutrition.JSONModels.Recipe
import com.example.edamamnutrition.UserDatabase.User
import com.example.edamamnutrition.UserDatabase.UserDatabase
import com.example.edamamnutrition.Utils.APIConnection
import com.example.edamamnutrition.Utils.APICredentials
import com.example.edamamnutrition.Utils.TimeConverter
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.lang.NullPointerException

class MainActivity : AppCompatActivity() {

    //GETTING VARIABLES READY
    private lateinit var mainRecyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var userDB: UserDatabase
    //Page 1 : Paragraph 4
    lateinit var user: User
    lateinit var recipeList: MutableList<Recipe>
    lateinit var nextPage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //SETTING UP RECYCLERVIEW AND GETTING ITEMS FROM DATABASE
        mainRecyclerView = findViewById(R.id.main_recyclerview);
        userDB = UserDatabase.getDatabase(this)
        recipeList = mutableListOf()
        recipeAdapter = RecipeAdapter(recipeList)
        mainRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        mainRecyclerView.setHasFixedSize(true)
        mainRecyclerView.adapter = recipeAdapter
        recipeAdapter.setOnItemClickListener(object: RecipeAdapter.onItemClickListener {
            //PASSING CALLBACK WHEN ITEM IN ADAPTER HAS BEEN CLICKED (PROPER WAY TO HANDLE ONCLICKS IN RECYCLERVIEW)
            //Page 2 : Paragraph 2
            override fun onItemClick(position: Int) {

                var intent: Intent = Intent()
                intent.setAction(Intent.ACTION_VIEW)
                intent.addCategory(Intent.CATEGORY_BROWSABLE)
                intent.setData(Uri.parse(recipeList[position].url))
                startActivity(intent)

            }
        })

        //GETTING ALL BUTTONS
        val editText: EditText = findViewById(R.id.main_edittext);
        val searchBtn: Button = findViewById(R.id.main_search_btn);
        val settingsBtn: ImageButton = findViewById(R.id.main_settings_btn)
        val historyBtn : Button = findViewById(R.id.main_history_btn)
        val loadMoreBtn : Button = findViewById(R.id.main_viewmore_btn)

        //ONCLICK TO NAVIGATE TO SEARCH HISTORY WITH INTENT
        historyBtn.setOnClickListener {
            val intent = Intent(this, FavoriteActivity::class.java)
            startActivity(intent)
        }

        //ONCLICK TO NAVIGATE TO SETTINGS WITH INTENT
        settingsBtn.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        //LOAD THE NEXT DATASET FROM API
        loadMoreBtn.setOnClickListener {
        CoroutineScope(Dispatchers.IO).launch {
            if (nextPage != "") {
                //PASSING THE NEXTPAGE VARIABLE TO FETCHJSON
                val jsonFood = APIConnection.fetchJSON(nextPage)
                //CHECKING IF THE RETURN VALUE IS EMPTY, IF IT IS WE DO NOT WANT TO OVERWRITE THE NEXT PAGE
                if (jsonFood != null) {
                    nextPage = jsonFood._links.next.href
                }
                if (jsonFood != null) {
                    //CHECKS IF THE RETURN VALUE IS NULL AND ONLY ADDS TO LIST IF IT IS NOT EMPTY
                    for (i in 0 until jsonFood.to) {
                        var recipe = APIConnection.parseJSON(jsonFood, i)
                        if (recipe != null) {
                            recipeList.add(recipe)
                        }
                    }
                    withContext(Main) {
                        //HANDLING VIEW CHANGES
                        Toast.makeText(this@MainActivity, "Loading...", Toast.LENGTH_SHORT).show()
                        recipeAdapter.notifyDataSetChanged()
                    }
                }
            } else {
                withContext(Main) {
                    Toast.makeText(this@MainActivity, "No Content", Toast.LENGTH_SHORT).show()
                }
            }

        }

        }

            CoroutineScope(Dispatchers.IO).launch {
                //CHECKING IF THERE IS A USER IN THE DATABASE, IF NOT ONE WILL BE CREATED
                //Page 3 : Paragraph 4
                //USING NULLABLE TYPE OF USER TO GET THE VALUES FROM DB
                val userToGet: User? = userDB.userDao().getUser(1)
                //USING THE ELVIS OPERATOR TO PROVIDE DEFAULT VALUE IF ITS NULL
                user = userToGet ?: SettingsActivity.prePopulate()
                userDB.userDao().addUser(user)

                //GETTING THE QUERY BASED ON TIME OF DAY, USED TO LOAD DEFAULT WHEN USER OPENS PAGE
                val time = TimeConverter.getTime()
                val jsonFood = APIConnection.fetchJSON("${APICredentials.BASE_URL}/api/recipes/v2?type=public&q=${time}${APICredentials.APP_ID}${APICredentials.APP_KEY}")
                //CHECKING IF THE RETURN VALUE IS EMPTY, IF IT IS NOT, WE OVERWRITE THE PREVIOUS VALUE OF NEXTPAGE AND GET THE NEW VALUE AND SET IT TO THE VARIABLE
                if (jsonFood != null) {
                    nextPage = jsonFood._links.next.href
                }

                //Page 1 : Paragraph 3
                if (jsonFood != null) {
                    for (i in 0 until jsonFood.to) {
                        var recipe = APIConnection.parseJSON(jsonFood, i)
                        if (recipe != null) {
                            recipeList.add(recipe)
                        }
                    }
                        withContext(Main) {
                            recipeAdapter.notifyDataSetChanged()
                        }
                }
            }


        searchBtn.setOnClickListener {
            val query = editText.text.toString()
            val url = "${APICredentials.BASE_URL}/api/recipes/v2?type=public&q=${query}${APICredentials.APP_ID}${APICredentials.APP_KEY}&diet=${user.diet}&mealType=${user.priority}"
            if (query != "") {
                CoroutineScope(Dispatchers.IO).launch {
                    val jsonFood = APIConnection.fetchJSON(url)

                    //CLEARING THE LIST SO THAT PREVIOUS VALUES FROM OTHER SEARCHES ARE NOT LOADED
                    recipeList.clear()

                    if (jsonFood != null) {
                        try {
                            nextPage = jsonFood._links.next.href
                        } catch (e: NullPointerException) {
                            nextPage = ""
                        }
                    }

                    if (jsonFood != null) {
                        for (i in 0 until jsonFood.to) {
                            var recipe = APIConnection.parseJSON(jsonFood, i)
                            if (recipe != null) {
                                recipeList.add(recipe)
                            }
                        }
                        withContext(Main) {
                            recipeAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    //OVERRIDING ONRESUME
    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            if (userDB.userDao().getUser(1) == null) {
                val userToPop = SettingsActivity.prePopulate()
                userDB.userDao().addUser(userToPop)
                user = userToPop
            } else {
                user = userDB.userDao().getUser(1)
            }
            GlobalScope.launch(Dispatchers.Main) {
                recipeAdapter.notifyDataSetChanged()
            }

            }
        }
}

