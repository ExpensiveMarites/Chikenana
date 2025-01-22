package com.example.project_withfirebase

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class Order(val imageUrls: List<String>, val orderData: String, val position: String)

private lateinit var switchDelivery: SwitchCompat
private lateinit var inputAddress: EditText
private lateinit var inputCity: EditText
private lateinit var inputPayment: EditText

class Order_activity : AppCompatActivity() {
    private lateinit var orderAdapter: OrderAdapter
    private val orders = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        switchDelivery = findViewById(R.id.switchDelivery)
        inputAddress = findViewById(R.id.inputAddress)
        inputCity = findViewById(R.id.inputCity)
        inputPayment = findViewById(R.id.inputPayment)

        val totalPrice = intent.getDoubleExtra("total_price", 0.0)
        val totalPriceTextView = findViewById<TextView>(R.id.tvSubtotalAmount)
        totalPriceTextView.text = "Total: $totalPrice PHP"
        val tvPaymentAmount = findViewById<TextView>(R.id.tvPaymentAmount)
        val placeOrderButton = findViewById<Button>(R.id.place_order_btn)

        val recyclerView = findViewById<RecyclerView>(R.id.order_list)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        orderAdapter = OrderAdapter(orders)
        recyclerView.adapter = orderAdapter

        val orderId = intent.getStringExtra("order_id")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("orders").child(orderId!!)

        switchDelivery.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                inputAddress.visibility = View.VISIBLE
                inputCity.visibility = View.VISIBLE

                val tvDeliveryCharge = findViewById<TextView>(R.id.tvDeliveryChargeAmount)
                tvDeliveryCharge.text = "₱45"

            } else {
                inputAddress.visibility = View.GONE
                inputCity.visibility = View.GONE

                val tvDeliveryCharge = findViewById<TextView>(R.id.tvDeliveryChargeAmount)
                tvDeliveryCharge.text = "₱0"
            }
        }


        val tvtotalAmount = findViewById<TextView>(R.id.tvTotalAmount)
        val paymentBtn = findViewById<Button>(R.id.payment_btn)
        paymentBtn.setOnClickListener {
            val payment = inputPayment.text.toString()
            val name = findViewById<EditText>(R.id.inputName).text.toString()
            val address = inputAddress.text.toString()
            val city = inputCity.text.toString()
            val cardNumber = findViewById<EditText>(R.id.inputCardNumber).text.toString()

            if (!switchDelivery.isChecked) {
                if (name.isEmpty() || payment.isEmpty()) {
                    Toast.makeText(this, "Please fill in the name and payment fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else {
                if (name.isEmpty() || address.isEmpty() || city.isEmpty() || cardNumber.isEmpty() || payment.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            if (payment.toDoubleOrNull() == null) {
                Toast.makeText(this, "Invalid payment amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val paymentAmount = payment.toDouble()
            if (paymentAmount < totalPrice) {
                Toast.makeText(this, "Payment amount must be at least ₱$totalPrice", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
                val totalPriceAmount = if (switchDelivery.isChecked) paymentAmount - totalPrice  + 45.0 else paymentAmount - totalPrice
                tvPaymentAmount.text = "₱ $paymentAmount"
                tvtotalAmount.text = "₱${String.format("%.2f", totalPriceAmount)}"
            }


        placeOrderButton.setOnClickListener {
            val intent = Intent(this, Delivering::class.java)
            startActivity(intent)
        }


        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val orderData = dataSnapshot.child("order_data").value as String
                val imageUrlsSnapshot = dataSnapshot.child("image_urls")

                val imageUrls = ArrayList<String>()
                imageUrlsSnapshot.children.forEach {
                    imageUrls.add(it.value as String)
                }

                val order = Order(imageUrls, orderData, orderId)
                orders.add(order) // Add the new order to the list
                orderAdapter.notifyDataSetChanged() // Notify the adapter of the changes
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error occurred while fetching data: $error")
                // Display an error message to the user
            }
        })
    }

    class OrderAdapter(private val orders: MutableList<Order>) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_order_adapter, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val order = orders[position]
            val formattedPosition = String.format("Order #%03d", position + 1)
            holder.orderPositionTextView.text = formattedPosition
            holder.textView.text = order.orderData

            val imageAdapter = ImageAdapter(order.imageUrls)
            holder.recyclerView.adapter = imageAdapter
            holder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        }

        override fun getItemCount(): Int {
            return orders.size}

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val orderPositionTextView: TextView = itemView.findViewById(R.id.order_position_text_view)
            val textView: TextView = itemView.findViewById(R.id.text_view)
            val recyclerView: RecyclerView = itemView.findViewById(R.id.image_recycler_view)
        }
    }

    class ImageAdapter(private val imageUrls: List<String>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.image_adapter, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(holder.itemView.context).load(imageUrls[position]).into(holder.imageView)
        }

        override fun getItemCount(): Int {
            return imageUrls.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.image_view)
        }
    }
}