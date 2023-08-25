package com.example.ourapplicationglamour3000.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.*
import com.example.ourapplicationglamour3000.R
import com.example.ourapplicationglamour3000.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var signup: Button
    private lateinit var back: ImageButton

    //view binding
    private lateinit var binding:ActivitySignUpBinding

    //firebase auth
    private lateinit var auth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        auth = FirebaseAuth.getInstance()

        //init progress dialog, will show while creating account
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        name = findViewById(R.id.nameEt)
        email = findViewById(R.id.emailEt)
        password = findViewById(R.id.passwordEt)
        confirmPassword = findViewById(R.id.cPasswordEt)
        signup = findViewById<Button?>(R.id.signupBtn)
        back = findViewById(R.id.backBtn)

        back.setOnClickListener{
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        signup.setOnClickListener {
            /* Steps
            * 1) Input data
            * 2) Validate data
            * 3) Create account - Firebase auth
            * 4) Save user info - Firebase Realtime Database */

            validateData()
        }

    }

    private var name1 = " "
    private var email1 = " "
    private var password1 = " "
    private var confirmPassword1 = " "

    private fun validateData() {
        name1 = name.text.toString().trim()
        email1 = email.text.toString().trim()
        password1 = password.text.toString().trim()
        confirmPassword1 = confirmPassword.text.toString().trim()

        if (TextUtils.isEmpty(name1)){
            Toast.makeText(baseContext, "Enter Your Name...", Toast.LENGTH_SHORT).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email1).matches()){
            Toast.makeText(baseContext, "Invalid Email Address...", Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(password1)){
            Toast.makeText(baseContext, "Enter Password...", Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(confirmPassword1)){
            Toast.makeText(baseContext, "Confirm Password...", Toast.LENGTH_SHORT).show()
        }
        else if (password1 != confirmPassword1){
            Toast.makeText(baseContext, "Password Does Not Match...", Toast.LENGTH_SHORT).show()
        }
        else{
            createUserAccount()
        }
    }

    private fun createUserAccount() {

        progressDialog.setMessage("Creating Account...")
        progressDialog.show()

        auth.createUserWithEmailAndPassword(email1,password1)
            .addOnSuccessListener {
            updateUserInfo()
        }
            .addOnFailureListener { e->
                Toast.makeText(baseContext, "Failed Creating An Account Due To ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserInfo() {

        progressDialog.setMessage("Saving User Info...")

        val timestamp = System.currentTimeMillis()

        val uid = auth.uid

        val hashMap: HashMap<String, Any?> = HashMap()
        hashMap["uid"] = uid
        hashMap["email"] = email1
        hashMap["name"] = name1
        hashMap["profileImage"] = ""
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(baseContext, "Account Created...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                finish()
            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(baseContext, "Failed Saving User Info Due To ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}