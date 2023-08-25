package com.example.ourapplicationglamour3000.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.example.ourapplicationglamour3000.R
import com.example.ourapplicationglamour3000.databinding.ActivityHomepageBinding
import com.google.firebase.auth.FirebaseAuth

class HomepageActivity : AppCompatActivity() {

    private lateinit var btnShowCategory: Button
    private lateinit var btnShowItem: Button
    private lateinit var logout: ImageButton

    private lateinit var binding: ActivityHomepageBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logout = findViewById(R.id.logoutBtn)
        btnShowCategory=findViewById(R.id.categoriesBtn)
        btnShowItem=findViewById(R.id.itemsBtn)

        auth = FirebaseAuth.getInstance()
        checkUser()

        //Handle click, logout
        logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, SplashAcitivity::class.java))
            finish()
        }

        //Handle click, open category
        btnShowCategory.setOnClickListener{
            //move to category page
            val intent= Intent(this, DashboardUserActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Handle click, open item
        btnShowItem.setOnClickListener{
            //move to home page
            val intent= Intent(this, ItemActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Handle click, open profile
        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }
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