package com.example.coctailslist10gr

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var buttonContainer: LinearLayout
    private lateinit var recipeDetailsContainer: LinearLayout
    private val cocktailList = mutableListOf<Cocktail>()
    private val activeButtons = mutableMapOf<Button, Boolean>() // Track button states
    private val activeCocktails = mutableMapOf<Cocktail, LinearLayout>() // Track active cocktails and their views

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        buttonContainer = findViewById(R.id.buttonContainer)
        recipeDetailsContainer = findViewById(R.id.recipeDetailsContainer)

        // Load cocktails and create buttons
        if (loadCocktails()) {
            addResetButton()
            createCocktailButtons()
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
            e.printStackTrace()
            false
        }
    }

    private fun addResetButton() {
        val resetButton = Button(this).apply {
            text = "RESET ALL"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(4, 4, 4, 4)
            }
            setBackgroundColor(Color.RED)
            setTextColor(Color.WHITE)
            textSize = 14f
            setTypeface(null, Typeface.BOLD)
        }

        resetButton.setOnClickListener { resetAll() }
        buttonContainer.addView(resetButton, 0)
    }

    private fun createCocktailButtons() {
        for (cocktail in cocktailList) {
            val button = Button(this).apply {
                text = cocktail.title.uppercase()
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(4, 4, 4, 4)
                }
                setBackgroundColor(Color.LTGRAY)
                setTextColor(Color.BLACK)
                textSize = 14f
                setTypeface(null, Typeface.BOLD)
            }

            activeButtons[button] = false

            button.setOnClickListener {
                toggleButtonState(button, cocktail)
            }

            buttonContainer.addView(button)
        }
    }

    private fun toggleButtonState(button: Button, cocktail: Cocktail) {
        val isPressed = activeButtons[button] ?: false

        if (isPressed) {
            button.setBackgroundColor(Color.LTGRAY)
            activeButtons[button] = false
            activeCocktails[cocktail]?.let { recipeDetailsContainer.removeView(it) }
            activeCocktails.remove(cocktail)
        } else {
            if (activeCocktails.size < 4) {
                button.setBackgroundColor(Color.GREEN)
                activeButtons[button] = true
                addRecipeView(cocktail)
            }
        }
    }

    private fun addRecipeView(cocktail: Cocktail) {
        val recipeLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            ).apply {
                setMargins(4, 4, 4, 4)
            }
            setBackgroundColor(Color.LTGRAY)
            setPadding(8, 8, 8, 8)
            setOnClickListener {
                // Remove recipe view on click
                activeCocktails.remove(cocktail)
                recipeDetailsContainer.removeView(this)
                resetButtonState(cocktail)
            }
        }

        // Dynamically build details, skipping empty fields and ordering topUp correctly
        val details = mutableListOf<String>()

        details.add(cocktail.title.uppercase())
        details.add(cocktail.base)

        if (!cocktail.liqueur1.isNullOrEmpty()) details.add(cocktail.liqueur1)
        if (!cocktail.liqueur2.isNullOrEmpty()) details.add(cocktail.liqueur2)
        if (!cocktail.puree.isNullOrEmpty()) details.add(cocktail.puree)
        if (!cocktail.syrup.isNullOrEmpty()) details.add(cocktail.syrup)
        if (!cocktail.juice.isNullOrEmpty()) details.add(cocktail.juice)
        if (!cocktail.bitters.isNullOrEmpty()) details.add(cocktail.bitters)

        // Add topUp after bitters, if present
        if (!cocktail.topUp.isNullOrEmpty()) details.add(cocktail.topUp)

        if (!cocktail.garnish.isNullOrEmpty()) details.add(cocktail.garnish)
        details.add(cocktail.glass)

        for (detail in details) {
            val textView = TextView(this).apply {
                text = formatDetailText(detail)
                textSize = 14f
                setTextColor(Color.BLACK)
                setTypeface(null, Typeface.BOLD)
            }
            recipeLayout.addView(textView)

            // Add a line separator after each detail
            val separator = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
                ).apply {
                    setMargins(0, 4, 0, 4)
                }
                setBackgroundColor(Color.DKGRAY)
            }
            recipeLayout.addView(separator)
        }

        recipeDetailsContainer.addView(recipeLayout)
        activeCocktails[cocktail] = recipeLayout
    }

    /**
     * Formats detail text to apply different colors to the dose inside parentheses.
     */
    private fun formatDetailText(detail: String): SpannableString {
        val spannable = SpannableString(detail)
        val regex = Regex("\\((.*?)\\)") // Match text inside parentheses
        val match = regex.find(detail)

        match?.let {
            val start = it.range.first
            val end = it.range.last + 1
            spannable.setSpan(
                android.text.style.ForegroundColorSpan(Color.RED), // Color for ml dose
                start,
                end,
                android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannable
    }


    private fun resetButtonState(cocktail: Cocktail) {
        val button = activeButtons.keys.find { it.text == cocktail.title.uppercase() }
        button?.let {
            it.setBackgroundColor(Color.LTGRAY)
            activeButtons[it] = false
        }
    }

    private fun resetAll() {
        activeButtons.forEach { (button, _) ->
            button.setBackgroundColor(Color.LTGRAY)
            activeButtons[button] = false
        }
        recipeDetailsContainer.removeAllViews()
        activeCocktails.clear()
    }
}
