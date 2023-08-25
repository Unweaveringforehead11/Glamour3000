package com.example.ourapplicationglamour3000.activities

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.ourapplicationglamour3000.MyApplication
import com.example.ourapplicationglamour3000.R
import com.example.ourapplicationglamour3000.databinding.ActivityItemDetailsViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ItemDetailsViewActivity : AppCompatActivity() {

    //View binding
    private lateinit var binding:ActivityItemDetailsViewBinding

    //Item id
    private var itemId = ""

    //will hold a boolean value false/true to indicate either is in current user's favorite list or not
    private var isInFavorite = false

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailsViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get item id from intent
        itemId = intent.getStringExtra("id")!!

        //Init firebase auth
        auth = FirebaseAuth.getInstance()
        loadItemDetails()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        checkIsFavorite()

        //Handle click, add/remove favorite
        binding.favoriteBtn.setOnClickListener{

                if(isInFavorite){
                    MyApplication.removeFromFavorite(this, itemId)
                }
                else{
                    addToFavorite()
                }
        }
    }

    private fun loadItemDetails() {
        //Items > itemId > Details

        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.child(itemId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Get data
                    val id = "${snapshot.child("id").value}"
                    val itemCategory = "${snapshot.child("itemCategory").value}"
                    val itemCategoryId = "${snapshot.child("itemCategoryId").value}"
                    val itemDate = "${snapshot.child("itemDate").value}"
                    val itemDescription = "${snapshot.child("itemDescription").value}"
                    val itemImage = "${snapshot.child("itemImage").value}"
                    val itemName = "${snapshot.child("itemName").value}"
                    val itemNotes = "${snapshot.child("itemNotes").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val uid = "${snapshot.child("uid").value}"

                    //Load item category
                    MyApplication.loadCategory(itemCategoryId,binding.categoryTv)
                    //Load item thumbnail, pages count
                    MyApplication.loadItemImgFromUrlSinglePage("$itemImage", "$itemName")
                    //Load item image size
                    MyApplication.loadImageSize("$itemImage","$itemName",binding.sizeTv)

                    //Set data
                    binding.titleTv.text = itemName
                    binding.descriptionTv.text = itemDescription
                    binding.dateTv.text = itemDate
                    binding.notesTv.text = itemNotes

                    try {
                        Glide.with(this@ItemDetailsViewActivity)
                            .load(itemImage)
                            .placeholder(R.drawable.person_gray)
                            .into(binding.itemIv)
                    }
                    catch (e: Exception){

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun checkIsFavorite(){
        Log.d(ContentValues.TAG,"checkIsFavorite: Checking if item is in fav or not")

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(auth.uid!!).child("Favorites").child(itemId)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isInFavorite = snapshot.exists()
                    if (isInFavorite){
                        //Available in favorites
                        Log.d(ContentValues.TAG,"onDataChange: Available in fav")
                        //Set drawable top icon
                        binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
                            R.drawable.favorite_filled_gray,0,0)
                        binding.favoriteBtn.text = "Remove Favorite"
                    }
                    else{
                        //Not available in favorites
                        Log.d(ContentValues.TAG,"onDataChange: Not available in fav")
                        //Set drawable top icon
                        binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0,
                            R.drawable.favorite_border_gray,0,0)
                        binding.favoriteBtn.text = "Add Favorite"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun addToFavorite(){
        Log.d(ContentValues.TAG,"addToFavorite: Adding to favorites.")
        val timestamp = System.currentTimeMillis()

        //init firebase auth
        auth = FirebaseAuth.getInstance()

        //Setup data to add in db
        val hashMap = HashMap<String, Any>()
        hashMap["itemId"] = itemId
        hashMap["timestamp"] = timestamp

        //save to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(auth.uid!!).child("Favorites").child(itemId)
            .setValue(hashMap)
            .addOnSuccessListener {
                //Added to fav
                Log.d(ContentValues.TAG,"addToFavorite: Added to favorites")
                Toast.makeText(this,"Added to favorites", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Log.d(ContentValues.TAG,"addToFavorite: Failed to add to favorites due to ${e.message}")
                Toast.makeText(this,"Failed to add to favorites due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}