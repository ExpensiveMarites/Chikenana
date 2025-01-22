package com.example.project_withfirebase

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.chip.Chip

class Sides_menu : AppCompatActivity() {
    private lateinit var drinksChip: Chip
    private lateinit var dessertsChip: Chip
    private lateinit var sidesChip: Chip
    private lateinit var specialsChip: Chip
    private lateinit var mainCourseChip: Chip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sides_menu)

        mainCourseChip = findViewById<Chip>(R.id.main_course_btn)
        mainCourseChip.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        drinksChip = findViewById<Chip>(R.id.drink_btn)
        drinksChip.setOnClickListener {
            val intent = Intent(this, Drinks_menu::class.java)
            startActivity(intent)
        }
        dessertsChip = findViewById<Chip>(R.id.desserts_btn)
        dessertsChip.setOnClickListener {
            val intent = Intent(this, Dessert_menu::class.java)
            startActivity(intent)
        }

        sidesChip = findViewById<Chip>(R.id.sides_btn)
        sidesChip.setOnClickListener {
            val intent = Intent(this, Sides_menu::class.java)
            startActivity(intent)
        }

        specialsChip = findViewById<Chip>(R.id.specials_btn)
        specialsChip.setOnClickListener {
            val intent = Intent(this, Specials_Menu::class.java)
            startActivity(intent)
        }
    }
}