package com.example.project_withfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

data class Item(val id: Int, val name: String, val price: Double, var quantity: Int = 0, val imageUrl: String) {
    fun copy(quantity: Int) = Item(id, name, price, quantity, imageUrl)
}

class MainActivity : AppCompatActivity() {

    private lateinit var parentLayouts: Array<MaterialCardView>
    private lateinit var buttons: Array<Button>
    private lateinit var btnLayouts: Array<LinearLayout>
    private lateinit var quantityTextViews: Array<TextView>
    private var totalQuantity = 0
    private lateinit var cartBtn: FloatingActionButton
    private var cart = ArrayList<Item>()
    private lateinit var settingLines: ImageView
    private lateinit var drinksChip: Chip
    private lateinit var dessertsChip: Chip
    private lateinit var sidesChip: Chip
    private lateinit var specialsChip: Chip
    private lateinit var mainCourseChip: Chip
    private val database = FirebaseDatabase.getInstance()
    private val ordersRef = database.getReference("orders")

    private val items = arrayOf(
        Item(1, "Lime Chicken Thigh", 250.0, imageUrl = "https://firebasestorage.googleapis.com/v0/b/chickenana-7f7e2.appspot.com/o/lime_chicken_nobg.png?alt=media&token=61d154c5-5880-4c82-970f-98c6e0609596"),
        Item(2, "Tuscan Chicken", 280.0, imageUrl = "https://firebasestorage.googleapis.com/v0/b/chickenana-7f7e2.appspot.com/o/tuscan_chicken_nobg.png?alt=media&token=da1ae32c-4586-4376-a1d1-05b44db80c70"),
        Item(3, "Chicken Brocoli", 150.0, imageUrl = "https://firebasestorage.googleapis.com/v0/b/chickenana-7f7e2.appspot.com/o/brocoli_chicken_nobg.png?alt=media&token=4c7f4cd5-0e06-4693-a4cd-e9c9290c8067"),
        Item(4, "Orange Chicken", 150.0, imageUrl = "https://firebasestorage.googleapis.com/v0/b/chickenana-7f7e2.appspot.com/o/orange_chicken_nobg.png?alt=media&token=18d42a80-e320-4a39-a5e2-c525a5120f91"),
        Item(5, "Chicken Permasan", 250.0, imageUrl = "https://firebasestorage.googleapis.com/v0/b/chickenana-7f7e2.appspot.com/o/chicken_permasan_nobg.png?alt=media&token=6594f136-456e-418e-b34b-d243fbabbfa2"),
        Item(6, "Chicken Joy", 99.0, imageUrl = "https://firebasestorage.googleapis.com/v0/b/chickenana-7f7e2.appspot.com/o/chicken_joy_nobg.png?alt=media&token=0cf73452-5a21-4269-babb-104e92964c53"),
        Item(7, "Chicken Curry", 375.0, imageUrl = "https://firebasestorage.googleapis.com/v0/b/chickenana-7f7e2.appspot.com/o/chicken_curry_nobg.png?alt=media&token=aab4f5e2-c0cb-45bd-be88-d4dd15b2904e"),
        Item(8, "Roasted Chicken", 650.0, imageUrl = "https://firebasestorage.googleapis.com/v0/b/chickenana-7f7e2.appspot.com/o/roasted_chicken_nobg.png?alt=media&token=bb98cdbb-108d-45a6-8e6b-7e61cfb4a03e")
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        totalQuantity = 0


        // Initialize views
        parentLayouts = arrayOf(
            findViewById<MaterialCardView>(R.id.cardview_1),
            findViewById<MaterialCardView>(R.id.cardview_2),
            findViewById<MaterialCardView>(R.id.cardview_3),
            findViewById<MaterialCardView>(R.id.cardview_4),
            findViewById<MaterialCardView>(R.id.cardview_5),
            findViewById<MaterialCardView>(R.id.cardview_6),
            findViewById<MaterialCardView>(R.id.cardview_7),
            findViewById<MaterialCardView>(R.id.cardview_8),
        )

        buttons = arrayOf(
            findViewById<Button>(R.id.add_to_cart_1),
            findViewById<Button>(R.id.add_to_cart_2),
            findViewById<Button>(R.id.add_to_cart_3),
            findViewById<Button>(R.id.add_to_cart_4),
            findViewById<Button>(R.id.add_to_cart_5),
            findViewById<Button>(R.id.add_to_cart_6),
            findViewById<Button>(R.id.add_to_cart_7),
            findViewById<Button>(R.id.add_to_cart_8),
        )

        btnLayouts = arrayOf(
            findViewById<LinearLayout>(R.id.btn_layout1),
            findViewById<LinearLayout>(R.id.btn_layout2),
            findViewById<LinearLayout>(R.id.btn_layout3),
            findViewById<LinearLayout>(R.id.btn_layout4),
            findViewById<LinearLayout>(R.id.btn_layout5),
            findViewById<LinearLayout>(R.id.btn_layout6),
            findViewById<LinearLayout>(R.id.btn_layout7),
            findViewById<LinearLayout>(R.id.btn_layout8),
        )

        quantityTextViews = arrayOf(
            findViewById<TextView>(R.id.quantity_1),
            findViewById<TextView>(R.id.quantity_2),
            findViewById<TextView>(R.id.quantity_3),
            findViewById<TextView>(R.id.quantity_4),
            findViewById<TextView>(R.id.quantity_5),
            findViewById<TextView>(R.id.quantity_6),
            findViewById<TextView>(R.id.quantity_7),
            findViewById<TextView>(R.id.quantity_8),
        )

        settingLines = findViewById<ImageView>(R.id.setting_lines)
        settingLines.setOnClickListener {
            logout()
        }

        for (i in 0 until items.size) {
            cart.add(items[i].copy(quantity = 0))
        }

        for (i in 0 until buttons.size) {
            buttons[i].setBackgroundResource(R.drawable.white_round_btn)
            buttons[i].setOnClickListener {
                quantityTextViews[i].visibility = View.VISIBLE
                quantityTextViews[i].text = "0"
                buttons[i].visibility = View.GONE
                btnLayouts[i].visibility =View.VISIBLE
            }
        }

        for (i in 0 until btnLayouts.size) {
            val addBtn = findViewById<Button>(btnLayouts[i].getChildAt(0).id)
            val lessBtn = findViewById<Button>(btnLayouts[i].getChildAt(2).id)

            addBtn.setOnClickListener {
                val quantity = quantityTextViews[i].text.toString().toIntOrNull()?: 0
                val updatedQuantity = quantity + 1
                updateQuantityTextView(quantityTextViews[i], updatedQuantity)
                cart[i] = items[i].copy(quantity = updatedQuantity)
                updateTotalQuantity()
                addToCart(cart[i])
            }

            lessBtn.setOnClickListener {
                val quantity = quantityTextViews[i].text.toString().toIntOrNull()?: 0
                val updatedQuantity = if (quantity > 0) quantity - 1 else 0
                updateQuantityTextView(quantityTextViews[i], updatedQuantity)
                cart[i] = items[i].copy(quantity = updatedQuantity)
                updateTotalQuantity()
                removeFromCart(cart[i])
            }
        }

        mainCourseChip = findViewById<Chip>(R.id.main_course_btn)
        mainCourseChip.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        drinksChip = findViewById<Chip>(R.id.drinks_btn)
        drinksChip.setOnClickListener {
            val intent = Intent(this, Drinks_menu::class.java)
            startActivity(intent)
        }
        dessertsChip = findViewById<Chip>(R.id.dessert_btn)
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

        cartBtn = findViewById<FloatingActionButton>(R.id.add2_Cart_btn)
        cartBtn.setOnClickListener {
            val itemsWithQuantity = cart.filter { it.quantity > 0 }
            if (itemsWithQuantity.isNotEmpty()) {
                val orderData = itemsWithQuantity.map { item -> "${item.name} x ${item.quantity}" }.joinToString(separator = "\n")
                val imageUrls = itemsWithQuantity.map { item -> item.imageUrl }
                val totalPrice = itemsWithQuantity.sumByDouble { it.price * it.quantity }

                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("orders")

                val orderId = myRef.push().key
                myRef.child(orderId!!).child("order_data").setValue(orderData)
                myRef.child(orderId).child("image_urls").setValue(imageUrls)

                val intent = Intent(this, Order_activity::class.java)
                intent.putExtra("order_id", orderId)
                intent.putExtra("total_price", totalPrice)
                startActivity(intent)
            } else {
                // Show a message to the user that they need to add items to the cart
                Toast.makeText(this, "Please add items to the cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addToCart(item: Item) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user!= null) {
            val cartRef = database.getReference("carts").child(user.uid)
            val cartItemRef = cartRef.child(item.id.toString())
            cartItemRef.setValue(item)
                .addOnSuccessListener {
                    updateTotalPrice()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error adding item to cart: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please log in to add items to the cart", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeFromCart(item: Item) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user!= null) {
            val cartRef = database.getReference("carts").child(user.uid)
            val cartItemRef = cartRef.child(item.id.toString())
            cartItemRef.removeValue()
                .addOnSuccessListener {
                    updateTotalPrice()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error removing item from cart: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please log in to remove items from the cart", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateQuantityTextView(quantityTextView: TextView, quantity: Int) {
        quantityTextView.text = quantity.toString()
    }

    private fun updateTotalPrice() {
        val totalPriceTextView = findViewById<TextView>(R.id.total_price)
        val totalPrice = cart.sumByDouble { it.price * it.quantity }
        totalPriceTextView.text = "Total: ${totalPrice} PHP"
    }

    private fun updateTotalQuantity() {
        val orderQuantityTextView = findViewById<TextView>(R.id.order_quantity)
        val totalQuantity = cart.sumBy { it.quantity }
        orderQuantityTextView.text = "Order Quantity: $totalQuantity"
        updateTotalPrice()
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()


        val intent = Intent(this, LogIn_activity::class.java)
        startActivity(intent)
        finish()
    }
}