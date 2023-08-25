package com.example.ourapplicationglamour3000.activities

import android.app.*
import android.app.Notification
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.ourapplicationglamour3000.*
import com.example.ourapplicationglamour3000.R
import com.example.ourapplicationglamour3000.models.ModelClass
import com.example.ourapplicationglamour3000.databinding.ActivityAddItemBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class AddItemActivity : AppCompatActivity() {

    private lateinit var txtView2: TextView
    private lateinit var btnDatePicker: Button
    private lateinit var btnSubmit: Button
    private lateinit var itemName: EditText
    private lateinit var itemDesc: EditText
    private lateinit var itemNotes: EditText
    private lateinit var category: TextView

    //view binding
    private lateinit var binding: ActivityAddItemBinding

    //firebase auth
    private lateinit var auth: FirebaseAuth

    //progress dialog
    private lateinit var progressDialog: ProgressDialog

    private var TAG = "ITEM_ADD_TAG"

    //Image uri
    private var imageUri: Uri? = null

    private lateinit var categoryArrayList:ArrayList<ModelClass>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        txtView2 = findViewById(R.id.txtView2)
        btnDatePicker = findViewById(R.id.btnDatePicker)
        btnSubmit = findViewById(R.id.submitBtn)
        itemName = findViewById(R.id.itemName)
        itemDesc = findViewById(R.id.itemDescription)
        itemNotes = findViewById(R.id.itemNotes)
        category = findViewById(R.id.itemCategoryTv)

        loadItemCategories()
        auth = Firebase.auth

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener{
            val intent= Intent(this, ItemActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.itemIv.setOnClickListener {
            showImageAttachMenu()

        }

        btnSubmit.setOnClickListener {
            validateData()
        }

        binding.itemCategoryTv.setOnClickListener{
            itemCategoryPickDialog()
        }

        val myCalendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLable(myCalendar)
        }

        btnDatePicker.setOnClickListener{
            DatePickerDialog(this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(
                Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun checkItems() {
        val ref1 = FirebaseDatabase.getInstance().getReference("Items")

        ref1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val itemCount = dataSnapshot.childrenCount

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channelId = "item_channel"
                val channelName = "Item Channel"
                val description = "Notification channel for item updates"

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                        this.description = description
                    }
                    notificationManager.createNotificationChannel(channel)
                }

                val notificationId = 1
                val notificationBuilder = NotificationCompat.Builder(this@AddItemActivity, channelId)
                    .setSmallIcon(R.drawable.star_gray)
                    .setContentTitle("Achievement Unlocked!!")

                when (itemCount) {
                    0L -> {

                    }
                    1L -> {
                        notificationBuilder.setContentText("Starter: Added the first item to the app")
                    }
                    3L -> {
                        notificationBuilder.setContentText("Collector: Added three items to the app")
                    }
                    10L -> {
                        notificationBuilder.setContentText("Packrat: Added 10 items to the app")
                    }
                    else -> {
                        // For other item counts, display a generic message
                        notificationBuilder.setContentText("Items added: $itemCount")
                    }
                }

                notificationManager.notify(notificationId, notificationBuilder.build())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
            }
        })
    }


    private fun showImageAttachMenu(){

        //Show popup menu with options Camera, Gallery to pick image

        //Setup popup menu
        val popupMenu = PopupMenu(this,binding.itemIv)
        popupMenu.menu.add(Menu.NONE,0,0,"Camera")
        popupMenu.menu.add(Menu.NONE,1,1,"Gallery")
        popupMenu.show()

        //Handle popup menu item click
        popupMenu.setOnMenuItemClickListener { item->

            val id = item.itemId
            if (id == 0){
                pickImageCamera()
            }
            else if(id ==1){
                pickImageGallery()
            }

            true
        }
    }

    private fun pickImageGallery() {

        //Intent to pick image from gallery
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private fun pickImageCamera() {
        //Intent to pick image from camera
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"Temp Title")
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{ result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data

                binding.itemIv.setImageURI(imageUri)
            }
            else{
                Toast.makeText(this, "Cancelled",Toast.LENGTH_SHORT).show()
            }
        }
    )

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imageUri = data!!.data

                binding.itemIv.setImageURI(imageUri)
            }
            else{
                Toast.makeText(this, "Cancelled",Toast.LENGTH_SHORT).show()
            }
        }
    )

    private var name=""
    private var description=""
    private var itemCat=""
    private var notes=""
    private var date=""
    private var itemCount = 0

    private fun validateData() {
        //validate data
        Log.d(TAG,"ValidateData: Validating data")

        //get data
        name = itemName.text.toString().trim()
        description = itemDesc.text.toString().trim()
        itemCat = category.toString().trim()
        notes = itemNotes.text.toString().trim()
        date = txtView2.text.toString().trim()


        if (TextUtils.isEmpty(name)){
            Toast.makeText(baseContext, "Enter item name...",
                Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(description)) {
            Toast.makeText(baseContext, "Enter item description...",
                Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(itemCat)) {
            Toast.makeText(baseContext, "Select item category...",
                Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(notes)) {
            Toast.makeText(baseContext, "Enter item notes...",
                Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(date)) {
            Toast.makeText(baseContext, "Select the date...",
                Toast.LENGTH_SHORT).show()
        }else if (imageUri == null) {
            Toast.makeText(baseContext, "Capture item...",
                Toast.LENGTH_SHORT).show()
        }
        else  {
            uploadImgToStorage()
        }
    }

    private fun uploadImgToStorage() {
        //Upload image to firebase storage
        Log.d(TAG,"uploadImgToStorage: Uploading to storage...")

        progressDialog.setMessage("Uploading image")
        progressDialog.show()

        val timestamp = System.currentTimeMillis()

        val filePathAndName = "Items/$timestamp"
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(imageUri!!)
            .addOnSuccessListener{taskSnapshot ->
                Log.d(TAG,"uploadImgToStorage: Image uploaded, now getting url...")

                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while(!uriTask.isSuccessful);
                val uploadedImageUrl = "${uriTask.result}"

                uploadImgInfoToDb(uploadedImageUrl,timestamp)
            }.addOnFailureListener { e ->
            Log.d(TAG,"uploadImgToStorage: Failed to upload image to storage due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(baseContext, "Failed to upload image to storage due to ${e.message}",
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImgInfoToDb(uploadedImageUrl: String, timestamp: Long) {
        Log.d(TAG,"uploadImgInfoToDb: uploading to Db")

        progressDialog.setMessage("Uploading image details")
        progressDialog.show()

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["itemImage"] = "$uploadedImageUrl"
        hashMap["itemName"] = name
        hashMap["itemDescription"] = description
        hashMap["itemCategory"] = itemCat
        hashMap["itemCategoryId"] = "$selectedItemCategoryId"
        hashMap["itemNotes"] = notes
        hashMap["itemDate"] = date
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = "${auth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG,"uploadImgInfoToDb: Uploaded to Db")

                progressDialog.dismiss()
                Toast.makeText(baseContext, "Added successfully...",
                    Toast.LENGTH_SHORT).show()

                imageUri = null
                val intent= Intent(this, ItemActivity::class.java)
                startActivity(intent)
                checkItems()
                finish()
            }
            .addOnFailureListener{e->
                Log.d(TAG,"uploadImgInfoToDb: Failed to upload/add due to ${e.message}")

                progressDialog.dismiss()
                Toast.makeText(baseContext, "Failed to upload/add due to ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }

        val itemCountRef = ref.child("itemCount")
        itemCountRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentItemCount = snapshot.getValue(Int::class.java) ?: 0
                val newItemCount = currentItemCount + 1


                hashMap["itemCount"] = newItemCount
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun updateLable(myCalender: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        txtView2.text = sdf.format(myCalender.time)
    }

    private var selectedItemCategoryTitle = ""
    private var selectedItemCategoryId = ""

    private fun itemCategoryPickDialog(){
        Log.d(TAG,"itemCategoryPickDialog: Showing item category pick dialog")

        val itemsCategoryArray = arrayOfNulls<String>(categoryArrayList.size)
        for(i in categoryArrayList.indices){
            itemsCategoryArray[i] = categoryArrayList[i].categoryTitle
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Item Category")
            .setItems(itemsCategoryArray){dialog, which ->

                selectedItemCategoryTitle = categoryArrayList[which].categoryTitle
                selectedItemCategoryId = categoryArrayList[which].id

                binding.itemCategoryTv.text = selectedItemCategoryTitle

                Log.d(TAG,"itemCategoryPickDialog: Selected Item ID: $selectedItemCategoryId")
                Log.d(TAG,"itemCategoryPickDialog: Selected Item Category Title: $selectedItemCategoryTitle")
            }
            .show()
    }

    private fun loadItemCategories() {

        Log.d(TAG,"loadItemCategories: Loading item categories")

        categoryArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()
                for(ds in snapshot.children){
                    val model = ds.getValue(ModelClass::class.java)

                    categoryArrayList.add(model!!)
                    Log.d(TAG,"onDataChange: ${model.categoryTitle}")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}