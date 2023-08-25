package com.example.ourapplicationglamour3000.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.ourapplicationglamour3000.R
import com.example.ourapplicationglamour3000.databinding.ActivityItemEditBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ItemEditActivity : AppCompatActivity() {

    //View binding
    private lateinit var binding: ActivityItemEditBinding

    private companion object{
        private const val TAG = "ITEM_EDIT_TAG"
    }

    private lateinit var tvDatePicker: TextView

    //Item id get from intent started from AdapterItemClass
    private var itemId = ""
    //Progress dialog
    private lateinit var progressDialog: ProgressDialog

    //Arraylist to hold category titles
    private lateinit var categoryTitleArrayList:ArrayList<String>

    //Arraylist to hold category ids
    private lateinit var categoryIdArrayList:ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvDatePicker = findViewById(R.id.txtView2)

        //Get item id to edit the item info
        itemId = intent.getStringExtra("itemId")!!

        //Setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        loadCategories()
        loadItemInfo()

        binding.backBtn.setOnClickListener{
            onBackPressed()
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

        binding.btnDatePicker.setOnClickListener{
            DatePickerDialog(this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(
                Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private fun loadItemInfo() {
        Log.d(TAG,"loadItemInfo: Loading item info")

        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.child(itemId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Get item info
                    selectedCategoryTitle = snapshot.child("itemCategory").value.toString()
                    selectedCategoryId = snapshot.child("itemCategoryId").value.toString()
                    val itemDate = snapshot.child("itemDate").value.toString()
                    val itemDescription = snapshot.child("itemDescription").value.toString()
                    val itemName = snapshot.child("itemName").value.toString()
                    val itemNotes = snapshot.child("itemNotes").value.toString()

                    //set to views
                    binding.txtView2.text = itemDate
                    binding.itemDescription.setText(itemDescription)
                    binding.itemName.setText(itemName)
                    binding.itemNotes.setText(itemNotes)

                    //Load item category info using categoryId
                    Log.d(TAG,"onDataChange: Loading item category info")
                    val refItemCategory = FirebaseDatabase.getInstance().getReference("Categories")
                    refItemCategory.child(selectedCategoryId)
                        .addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                //Get category
                                val category = snapshot.child("categoryTitle").value

                                //Set to textview
                                binding.itemCategoryTv.text = category.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private var name=""
    private var description=""
    private var itemCat=""
    private var notes=""
    private var date=""

    private fun validateData() {
        //Get data
        name = binding.itemName.text.toString().trim()
        description = binding.itemDescription.text.toString().trim()
        itemCat = binding.itemCategoryTv.text.toString().trim()
        notes = binding.itemNotes.text.toString().trim()
        date = binding.txtView2.text.toString().trim()

        if (TextUtils.isEmpty(name)){
            Toast.makeText(baseContext, "Enter item name...",
                Toast.LENGTH_SHORT).show()
        }
        else if (TextUtils.isEmpty(description)) {
            Toast.makeText(baseContext, "Enter item description...",
                Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(selectedCategoryId)) {
            Toast.makeText(baseContext, "Select item category...",
                Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(notes)) {
            Toast.makeText(baseContext, "Enter item note(s)...",
                Toast.LENGTH_SHORT).show()
        }else if (TextUtils.isEmpty(date)) {
            Toast.makeText(baseContext, "Select the date...",
                Toast.LENGTH_SHORT).show()
        }else{
            updateItem()
        }

    }

    private fun updateItem() {
        Log.d(TAG,"updateItem: Updating item info...")

        //Show progress dialog
        progressDialog.setMessage("Updating item info...")
        progressDialog.show()

        val hashMap = HashMap<String, Any>()
        hashMap["itemName"] = name
        hashMap["itemDescription"] = description
        hashMap["itemCategory"] = itemCat
        hashMap["itemNotes"] = notes
        hashMap["itemDate"] = date

        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.child(itemId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Log.d(TAG, "updateItem: Updated successfully...")
                Toast.makeText(this,"Updated successfully...",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Log.d(TAG, "updateItem: Failed to update due to ${e.message}...")
                Toast.makeText(this,"Failed to update due to ${e.message}...",Toast.LENGTH_SHORT).show()
            }

        val intent= Intent(this, ItemDetailActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun updateLable(myCalender: Calendar) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        tvDatePicker.setText(sdf.format(myCalender.time))
    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""

    private fun itemCategoryPickDialog() {
        //Show dialog to pick the category of item. We already got the categories

        //Make string array from arraylist of string
        val categoriesArray = arrayOfNulls<String>(categoryTitleArrayList.size)
        for(i in categoryTitleArrayList.indices){
            categoriesArray[i] = categoryTitleArrayList[i]
        }

        //Alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Category")
            .setItems(categoriesArray){dialog,position ->
                //Handle click, save clicked category id and title
                selectedCategoryId = categoryIdArrayList[position]
                selectedCategoryTitle = categoryTitleArrayList[position]

                //Set to textview
                binding.itemCategoryTv.text = selectedCategoryTitle
            }
            .show() //Show dialog
    }

    private fun loadCategories() {
        Log.d(TAG,"loadCategories: loading categories...")

        categoryIdArrayList = ArrayList()
        categoryTitleArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryIdArrayList.clear()
                categoryTitleArrayList.clear()

                for(ds in snapshot.children){
                    val id = "${ds.child("id").value}"
                    val category = "${ds.child("categoryTitle").value}"

                    categoryIdArrayList.add(id)
                    categoryTitleArrayList.add(category)

                    Log.d(TAG,"onDataChange: Category ID $id")
                    Log.d(TAG,"onDataChange: Category $category")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}