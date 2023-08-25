package com.example.ourapplicationglamour3000

import android.app.Application
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception


class MyApplication: Application() {

    override fun onCreate(){
        super.onCreate()
    }

    companion object{

        fun formatTimeStamp(timestamp: Long) : String{
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp

            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }

        fun loadImageSize(itemImage: String, itemName: String, sizeTv: TextView){
            val TAG = "IMAGE_SIZE_TAG"

            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(itemImage)
            ref.metadata
                .addOnSuccessListener { storageMetaData ->
                    Log.d(TAG,"loadImageSize: Got metadata")
                    val bytes = storageMetaData.sizeBytes.toDouble()
                    Log.d(TAG,"loadImageSize: Size Bytes $bytes")

                    val kb = bytes/1024
                    val mb = kb/1024
                    if (mb >= 1) {
                        sizeTv.text = "${String.format("$.2f", mb)} MB"
                    }else if (kb >= 1) {
                        sizeTv.text = "${String.format("$.2f", kb)} KB"
                    } else {
                        sizeTv.text = "${String.format("$.2f", bytes)} bytes"
                    }
                }
                .addOnFailureListener { e ->
                    Log.d(TAG,"loadImageSize: Failed to get metadata due o ${e.message}")
                }
        }

        fun loadItemImgFromUrlSinglePage(
            Items: String,
            itemName: String
        ){
            val TAG = "ITEM_THUMBNAIL_TAG"

            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(Items)
            ref.getBytes(Constants.MAX_BYTES_IMAGE)
                .addOnSuccessListener { bytes ->
                    Log.d(TAG,"LoadItemImage: Size Bytes $bytes")

//                    pdfView.fromBytes(bytes)
//                        .pages(0)
//                        .spacing(0)
//                        .swipeHorizontal(false)
//                        .enableSwipe(false)
//                        .onError{ t->
//                            progressBar.visibility = View.INVISIBLE
//                            Log.d(TAG,"loadItemImgFromUrlSinglePage: ${t.message}")
//
//                        }
//                        .onPageError{page, t->
//                            progressBar.visibility = View.INVISIBLE
//                            Log.d(TAG,"loadItemImgFromUrlSinglePage: ${t.message}")
//                        }
//                        .onLoad{ nbPages ->
//                            progressBar.visibility = View.INVISIBLE
//                            Log.d(TAG,"loadItemImgFromUrlSinglePage: $nbPages")
//                            if (pagesTv != null){
//                                pagesTv.text = "$nbPages"
//                            }
//                        }
//                        .load()
                }
                .addOnFailureListener { e->
                    Log.d(TAG,"loadItemImgSize: Failed to get metadata due to ${e.message}")
                }
        }


        fun loadCategory(categoryId: String, categoryTv: TextView){

            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val category  = "${snapshot.child("categoryTitle").value}"
                        categoryTv.text = category
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
        }

        fun deleteItem(context: Context, itemId: String, itemImage: String, itemName: String){
            val TAG = "DELETE_ITEM_TAG"

            Log.d(TAG,"deleteItem: deleting...")

            //Progress dialog
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Please wait")
            progressDialog.setMessage("Deleting $itemName...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            Log.d(TAG,"deleteItem: Deleting from storage...")
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(itemImage)
            storageReference.delete()
                .addOnSuccessListener {
                    Log.d(TAG,"deleteItem: Deleted from storage")
                    Log.d(TAG,"deleteItem: Deleting from db now...")

                    val ref = FirebaseDatabase.getInstance().getReference("Items")
                    ref.child(itemId)
                        .removeValue()
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(context,"Successfully deleted...", Toast.LENGTH_SHORT).show()
                            Log.d(TAG,"deleteItem: Deleted from db too.")
                        }
                        .addOnFailureListener { e->
                            progressDialog.dismiss()
                            Toast.makeText(context,"Failed to delete from db due to ${e.message}...", Toast.LENGTH_SHORT).show()
                            Log.d(TAG,"deleteItem: Failed to delete due to ${e.message}")
                        }
                }
                .addOnFailureListener { e->
                    progressDialog.dismiss()
                    Toast.makeText(context,"Failed to delete from storage due to ${e.message}...", Toast.LENGTH_SHORT).show()
                    Log.d(TAG,"deleteItem: Failed to delete due to ${e.message}")
                }
        }

        fun removeFromFavorite(context:Context,itemId: String){
            Log.d(ContentValues.TAG,"removeFromFavorite: Removing from fav")

            val auth = FirebaseAuth.getInstance()
            //database ref
            val ref = FirebaseDatabase.getInstance().getReference("Users")
            ref.child(auth.uid!!).child("Favorites").child(itemId)
                .removeValue()
                .addOnSuccessListener {
                    Log.d(ContentValues.TAG,"removeFromFavorite: Removed from fav")
                    Toast.makeText(context,"Removed from favorites", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e->
                    Log.d(ContentValues.TAG,"removeFromFavorite: Failed to remove from favorites due to ${e.message}")
                    Toast.makeText(context,"Failed to remove from favorites due to ${e.message}", Toast.LENGTH_SHORT).show()

                }

        }

    }


}


