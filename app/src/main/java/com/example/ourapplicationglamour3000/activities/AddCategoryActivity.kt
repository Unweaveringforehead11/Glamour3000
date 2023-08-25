package com.example.ourapplicationglamour3000.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.ourapplicationglamour3000.R
import com.example.ourapplicationglamour3000.databinding.ActivityAddCategoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var back: ImageButton
    private lateinit var submit: Button
    private lateinit var catTitle: EditText
    private lateinit var catGoal: EditText

    //view binding
    private lateinit var binding: ActivityAddCategoryBinding

    //firebase auth
    private lateinit var auth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        //init progress dialog, will show while creating account
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        catTitle=findViewById(R.id.categoryEt)
        catGoal=findViewById(R.id.categoryGl)
        back = findViewById(R.id.backBtn)
        submit = findViewById(R.id.submitBtn)

        back.setOnClickListener{
            val intent= Intent(this, DashboardUserActivity::class.java)
            startActivity(intent)
            finish()
        }

        submit.setOnClickListener {
            validateData()
        }

    }

    private var catTitle1=""
    private var catGoal1=""

    private fun validateData() {

        catTitle1 = catTitle.text.toString().trim()
        catGoal1 = catGoal.text.toString().trim()
        if (TextUtils.isEmpty(catTitle1)){
            Toast.makeText(baseContext, "Enter category title...",
                Toast.LENGTH_SHORT).show()
        }
        if (TextUtils.isEmpty(catGoal1)) {
            Toast.makeText(baseContext, "Enter category goal...",
                Toast.LENGTH_SHORT).show()
        }
        else  {
            addCategoryFirebase()
        }
    }

    private fun addCategoryFirebase() {
        progressDialog.show()
        val timestamp = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["categoryTitle"] = catTitle1
        hashMap["categoryGoal"] = catGoal1
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${auth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(baseContext, "Added successfully...",
                    Toast.LENGTH_SHORT).show()
                val intent= Intent(this, DashboardUserActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(baseContext, "Failed to add due to ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }
}