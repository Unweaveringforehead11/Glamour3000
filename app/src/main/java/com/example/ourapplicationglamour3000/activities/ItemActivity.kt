package com.example.ourapplicationglamour3000.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.example.ourapplicationglamour3000.models.ModellClass
import com.example.ourapplicationglamour3000.R
import com.example.ourapplicationglamour3000.adapters.AdapterItemClass
import com.example.ourapplicationglamour3000.databinding.ActivityItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

class   ItemActivity : AppCompatActivity() {
    private lateinit var logout: ImageButton
    private lateinit var btnAddItem: Button

    private lateinit var binding: ActivityItemBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var back: ImageButton

    private lateinit var itemArrayList:ArrayList<ModellClass>
    private lateinit var adapterItem: AdapterItemClass

    private companion object{
        const val TAG = "ITEM_ACTIVITY_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logout = findViewById(R.id.logoutBtn)
        btnAddItem=findViewById(R.id.addItemBtn1)

        auth = FirebaseAuth.getInstance()
        checkUser()
        loadCategories()

        back = findViewById(R.id.backBtn)


        back.setOnClickListener{
            val intent= Intent(this, HomepageActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.searchET.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //called as when the user types anything
                try {
                    adapterItem.filter.filter(s)
                }catch (e: Exception){
                    Log.d(TAG,"onTextChanged: ${e.message}")
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                //called as when the user types anything
                try {
                    adapterItem.filter.filter(p0)
                }catch (e: Exception){
                    Log.d(TAG,"onTextChanged: ${e.message}")
                }
            }
        })

        btnAddItem.setOnClickListener {
            Toast.makeText(
                baseContext, "Add a new item...",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
            finish()
        }
        logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, SplashAcitivity::class.java))
            finish()
        }
    }

    private fun loadCategories() {
        itemArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemArrayList.clear()
                for(ds in snapshot.children){
                    val model = ds.getValue(ModellClass::class.java)

                    itemArrayList.add(model!!)
                }

                adapterItem = AdapterItemClass(this@ItemActivity,itemArrayList)

                binding.itemsRv.adapter = adapterItem
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun checkUser() {

        val firebaseUser = auth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        else{

            val email = firebaseUser.email
            binding.subTitleTv.text = email
        }
    }


}