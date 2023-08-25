package com.example.ourapplicationglamour3000.activities

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.ourapplicationglamour3000.models.ModelItems
import com.example.ourapplicationglamour3000.R
import com.example.ourapplicationglamour3000.adapters.AdapterItemDetails
import com.example.ourapplicationglamour3000.databinding.ActivityItemDetailBinding
import com.example.ourapplicationglamour3000.databinding.ActivityRowItemDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ItemDetailActivity : AppCompatActivity() {

    //category id, title
    private var categoryId = ""
    private var categoryTitle = ""

    private lateinit var binding: ActivityItemDetailBinding
    private lateinit var binding1: ActivityRowItemDetailBinding

    private companion object{
        const val TAG = "ITEM_DETAILS_TAG"
    }

    private lateinit var itemDetailsArrayList: ArrayList<ModelItems>

    private lateinit var  adapterItemDetails: AdapterItemDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding1 = ActivityRowItemDetailBinding.inflate(layoutInflater)
//        setContentView(binding1.root)

        binding.backBtn.setOnClickListener{
            val intent= Intent(this, DashboardUserActivity::class.java)
            startActivity(intent)
            finish()
        }


        //get from intent, that we passed from adapter
        val intent = intent
        categoryId = intent.getStringExtra("categoryId")!!
        categoryTitle = intent.getStringExtra("categoryTitle")!!

        binding.subTitleTv.text = categoryTitle

        loadItemDetailsList()

        binding.searchET.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //called as when the user types anything
                try {
                    adapterItemDetails.filter!!.filter(s)
                }
                catch (e: Exception){
                    Log.d(TAG,"onTextChanged: ${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //called as when the user types anything
                try {
                    adapterItemDetails.filter!!.filter(s)
                }
                catch (e: Exception){
                    Log.d(TAG,"onTextChanged: ${e.message}")
                }
            }

        })
    }

    private fun loadItemDetailsList() {
        itemDetailsArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.orderByChild("itemCategoryId").equalTo(categoryId)
            .addValueEventListener(object: ValueEventListener{
                @SuppressLint("SuspiciousIndentation")
                override fun onDataChange(snapshot: DataSnapshot){
                    itemDetailsArrayList.clear()
                    for (ds in snapshot.children){

                        val model = ds.getValue(ModelItems::class.java)


                        itemDetailsArrayList.add(model!!)
                            Log.d(TAG,"onDataChange: ${model.itemName} ${model.itemCategoryId}")
                    }
                    adapterItemDetails = AdapterItemDetails(this@ItemDetailActivity, itemDetailsArrayList)
                    binding.itemsRv.adapter = adapterItemDetails
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}