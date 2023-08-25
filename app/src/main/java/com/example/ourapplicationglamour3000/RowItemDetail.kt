package com.example.ourapplicationglamour3000

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.ourapplicationglamour3000.activities.ItemDetailActivity
import com.example.ourapplicationglamour3000.adapters.AdapterItemDetails
import com.example.ourapplicationglamour3000.databinding.ActivityItemDetailBinding
import com.example.ourapplicationglamour3000.databinding.ActivityRowItemDetailBinding
import com.example.ourapplicationglamour3000.models.ModelItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RowItemDetail : AppCompatActivity() {

    private lateinit var binding: ActivityRowItemDetailBinding
    //Firebase auth
    private lateinit var auth:FirebaseAuth

    //Item id
    private var itemId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRowItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        //Get item id from intent
        itemId = intent.getStringExtra("id")!!

        loadUserInfo()
    }

    private fun loadUserInfo() {

        //Load item info
        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.child(itemId)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Get user info
                    val itemImage = "${snapshot.child("itemImage").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"

                    /*try {
                        Glide.with(this@RowItemDetail)
                            .load(itemImage)
                            .placeholder(R.drawable.person_gray)
                            .into(binding.itemIv)
                    }
                    catch (e: Exception){

                    }*/
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}