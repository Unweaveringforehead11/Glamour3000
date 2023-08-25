package com.example.ourapplicationglamour3000.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.example.ourapplicationglamour3000.*
import com.example.ourapplicationglamour3000.adapters.AdapterCategoryClass
import com.example.ourapplicationglamour3000.databinding.ActivityDashboardUserBinding
import com.example.ourapplicationglamour3000.models.ModelClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardUserActivity : AppCompatActivity() {

    private lateinit var btnAddCategory: Button
    private lateinit var back: ImageButton

    private lateinit var binding: ActivityDashboardUserBinding

    private lateinit var auth: FirebaseAuth

    private lateinit var categoryArrayList:ArrayList<ModelClass>
    private lateinit var adapterCategory: AdapterCategoryClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnAddCategory=findViewById(R.id.addCategoryBtn)

        auth = FirebaseAuth.getInstance()
        checkUser()
        loadCategories()

        back = findViewById(R.id.backBtn)

        binding.chartBtn.setOnClickListener{
            val intent= Intent(this, ChartViewActivity::class.java)
            startActivity(intent)
            finish()
        }

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
                    adapterCategory.filter.filter(s)
                }
                catch (e: Exception){

                }
            }

            override fun afterTextChanged(s: Editable?) {
                //called as when the user types anything
                try {
                    adapterCategory.filter.filter(s)
                }
                catch (e: Exception){

                }
            }

        })

        btnAddCategory.setOnClickListener {
            Toast.makeText(
                baseContext, "Add a new category.",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, AddCategoryActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadCategories() {
        //init arrayList
        categoryArrayList = ArrayList()

        //get all categories from firebase database.....Firebase DB > Categories
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before adding data into it
                categoryArrayList.clear()
                for(ds in snapshot.children){
                    //get data as model
                    val model = ds.getValue(ModelClass::class.java)

                    //add to arrayList
                    categoryArrayList.add(model!!)
                }

                //setup adapter
                adapterCategory = AdapterCategoryClass(this@DashboardUserActivity,categoryArrayList)

                //set adapter to recycle view
                binding.categoriesRv.adapter = adapterCategory
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