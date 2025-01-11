package com.example.coctailslist10gr

import IngredientAdapter
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var buttonContainer: LinearLayout
    private lateinit var ingredientRecyclerView: RecyclerView
    private lateinit var ingredientAdapter: IngredientAdapter

    private val cocktailList = mutableListOf<Cocktail>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        buttonContainer = findViewById(R.id.buttonContainer)
        ingredientRecyclerView = findViewById(R.id.ingredientRecyclerView)

        // Set up RecyclerView
        ingredientRecyclerView.layoutManager = LinearLayoutManager(this)
        ingredientAdapter = IngredientAdapter(emptyList())
        ingredientRecyclerView.adapter = ingredientAdapter

        // Load data and create buttons
        if (loadCocktails()) {
            createCocktailButtons()
        } else {
            Toast.makeText(this, "No cocktails found to display.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCocktails(): Boolean {
        return try {
            val inputStream = assets.open("cocktails.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val type = object : TypeToken<List<Cocktail>>() {}.type
            cocktailList.addAll(Gson().fromJson(reader, type))
            reader.close()
            true
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load cocktails: ${e.message}", Toast.LENGTH_LONG).show()
            false
        }
    }

    private fun createCocktailButtons() {
        if (cocktailList.isEmpty()) {
            Toast.makeText(this, "No cocktails available.", Toast.LENGTH_SHORT).show()
            return
        }

        for (cocktail in cocktailList) {
            val button = Button(this).apply {
                text = cocktail.name
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 0) // Add spacing between buttons
                }
                setBackgroundColor(resources.getColor(R.color.purple_700, null))
                setTextColor(resources.getColor(R.color.white, null))
                setOnClickListener {
                    // Display ingredients in RecyclerView
                    showIngredients(cocktail.ingredients)
                }
            }
            buttonContainer.addView(button)
        }
    }

    private fun showIngredients(ingredients: List<String>) {
        ingredientAdapter.updateIngredients(ingredients)
    }
}
