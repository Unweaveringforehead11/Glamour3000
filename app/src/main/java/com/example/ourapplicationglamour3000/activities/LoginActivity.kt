package com.example.ourapplicationglamour3000.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ourapplicationglamour3000.R
import com.example.ourapplicationglamour3000.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var login: Button


    //view binding
    private lateinit var binding: ActivityLoginBinding

    //firebase auth
    private lateinit var auth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        //init progress dialog, will show while creating account
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        email = findViewById(R.id.emailEt)
        password = findViewById(R.id.passwordEt)
        login = findViewById(R.id.loginBtn)

        binding.backBtn.setOnClickListener{
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.noAccountTv.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.forgotTv.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        login.setOnClickListener{
            /*Steps
            * 1) Input data
            * 2) Validate data
            * 3) Login - Firebase auth
            * 4) Check user type - Firebase auth
            *    If user - move to user dashboard*/

            validateData()
        }

    }

    private var email1 = " "
    private var password1 = " "

    private fun validateData() {

        email1 = email.text.toString().trim()
        password1 = password.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email1).matches()){
            Toast.makeText(baseContext, "Invalid Email Address...", Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(password1)){
            Toast.makeText(baseContext, "Enter Password...", Toast.LENGTH_SHORT).show()
        }
        else{
            loginUser()
        }
    }

    private fun loginUser() {

        progressDialog.setMessage("Logging In...")
        progressDialog.show()

        auth.signInWithEmailAndPassword(email1,password1)
            .addOnSuccessListener {
                checkUser()
            }
            .addOnFailureListener { e->
                Toast.makeText(baseContext, "Login Failed Due To ${e.message}", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()

            }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun checkUser() {

        progressDialog.setMessage("Checking User...")

        val firebaseUser = auth.currentUser!!

        val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        progressDialog.dismiss()

                        val userType = snapshot.child("userType").value
                        if (userType == "user"){
                            startActivity(Intent(this@LoginActivity, HomepageActivity::class.java))
                            finish()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
    }
}