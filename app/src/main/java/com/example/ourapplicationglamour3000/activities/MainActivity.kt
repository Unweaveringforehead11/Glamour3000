package com.example.ourapplicationglamour3000.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.ourapplicationglamour3000.R

class MainActivity : AppCompatActivity() {

    private lateinit var login: Button
    private lateinit var signup: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login=findViewById(R.id.loginBtn)
        signup=findViewById(R.id.signupBtn)

        //Handle click login
        login.setOnClickListener{
            //move to login page
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Handle click sign up
        signup.setOnClickListener{
            //move to login page
            val intent= Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
}