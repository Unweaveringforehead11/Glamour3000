package com.example.ourapplicationglamour3000.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler.Value
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.ourapplicationglamour3000.MyApplication
import com.example.ourapplicationglamour3000.R
import com.example.ourapplicationglamour3000.adapters.AdapterItemFavorite
import com.example.ourapplicationglamour3000.databinding.ActivityProfileBinding
import com.example.ourapplicationglamour3000.models.ModelItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    //View binding
    private lateinit var binding:ActivityProfileBinding

    //Firebase auth
    private lateinit var auth:FirebaseAuth

    private lateinit var user:FirebaseUser

    private lateinit var itemsArrayList: ArrayList<ModelItems>
    private lateinit var adapterItemFavorite: AdapterItemFavorite

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        loadUserInfo()
        loadFavoriteItems()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.profileEditBtn.setOnClickListener {
            startActivity(Intent(this,ProfileEditActivity::class.java))
        }

        binding.accountStatusTv.setOnClickListener {
            if(user.isEmailVerified){
                Toast.makeText(this,"Already verified...",Toast.LENGTH_SHORT).show()
            }
            else{
                emailVerificationDialog()
            }
        }
    }

    private fun emailVerificationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Verify Email")
            .setMessage("Are you sure you want to send email verification instructions to your email ${user.email}")
            .setPositiveButton("SEND"){d,e ->
                sendEmailVerification()
            }
            .setNegativeButton("CANCEL"){d,e ->
                d.dismiss()
            }
            .show()
    }

    private fun sendEmailVerification() {
        progressDialog.setMessage("Sending email verification instructions to email ${user.email}")
        progressDialog.show()

        user.sendEmailVerification()
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Instructions sent! Check your email ${user.email}",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e ->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to send instructions to your email ${user.email} due to ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadFavoriteItems() {

        itemsArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(auth.uid!!).child("Favorites")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    itemsArrayList.clear()
                    for (ds in snapshot.children){
                        val itemId = "${ds.child("itemId").value}"

                        val modelItem = ModelItems()
                        modelItem.id = itemId

                        itemsArrayList.add(modelItem)
                    }
                    binding.favoriteItemsCountTv.text = "${itemsArrayList.size}"

                    adapterItemFavorite = AdapterItemFavorite(this@ProfileActivity,itemsArrayList)

                    binding.favoriteRv.adapter = adapterItemFavorite
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun loadUserInfo() {

        if (user.isEmailVerified){
            binding.accountStatusTv.text = "Verified"
        }
        else{
            binding.accountStatusTv.text = "Not Verified"
        }
        //Load user info
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(auth.uid!!)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Get user info
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val userType = "${snapshot.child("userType").value}"

                    val formattedDate = MyApplication.formatTimeStamp(timestamp.toLong())

                    binding.nameTv.text = name
                    binding.emailTv.text = email
                    binding.memberDateTv.text = formattedDate
                    binding.accountTypeTv.text = userType

                    try {
                        Glide.with(this@ProfileActivity)
                            .load(profileImage)
                            .placeholder(R.drawable.person_gray)
                            .into(binding.profileIv)
                    }
                    catch (e: Exception){

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}