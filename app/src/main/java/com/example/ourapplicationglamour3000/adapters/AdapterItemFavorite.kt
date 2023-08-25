package com.example.ourapplicationglamour3000.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ourapplicationglamour3000.MyApplication
import com.example.ourapplicationglamour3000.activities.ItemDetailsViewActivity
import com.example.ourapplicationglamour3000.databinding.RowItemFavoritesBinding
import com.example.ourapplicationglamour3000.models.ModelItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterItemFavorite : RecyclerView.Adapter<AdapterItemFavorite.HolderItemFavorite>{

    private val context: Context

    private var itemsArrayList: ArrayList<ModelItems>

    private lateinit var binding: RowItemFavoritesBinding

    constructor(context: Context, itemsArrayList: ArrayList<ModelItems>) {
        this.context = context
        this.itemsArrayList = itemsArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderItemFavorite {
        binding = RowItemFavoritesBinding.inflate(LayoutInflater.from(context),parent,false)

        return HolderItemFavorite(binding.root)
    }

    override fun getItemCount(): Int {
        return itemsArrayList.size
    }

    override fun onBindViewHolder(holder: HolderItemFavorite, position: Int) {
        val model = itemsArrayList[position]

        loadItemDetails(model,holder)

        holder.itemView.setOnClickListener {
            val intent = Intent(context,ItemDetailsViewActivity::class.java)
            intent.putExtra("id",model.id)
            context.startActivity(intent)
        }

        binding.removeFavBtn.setOnClickListener {
            MyApplication.removeFromFavorite(context,model.id)
        }
    }

    private fun loadItemDetails(model: ModelItems, holder: HolderItemFavorite) {
        val itemId = model.id

        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.child(itemId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Get data
                    val id = "${snapshot.child("id").value}"
                    val itemCategory = "${snapshot.child("itemCategory").value}"
                    val itemCategoryId = "${snapshot.child("itemCategoryId").value}"
                    val itemDate = "${snapshot.child("itemDate").value}"
                    val itemDescription = "${snapshot.child("itemDescription").value}"
                    val itemImage = "${snapshot.child("itemImage").value}"
                    val itemName = "${snapshot.child("itemName").value}"
                    val itemNotes = "${snapshot.child("itemNotes").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val uid = "${snapshot.child("uid").value}"

                    model.isFavorite = true
                    model.id = id
                    model.itemCategory = itemCategory
                    model.itemCategoryId = itemCategoryId
                    model.itemDate = itemDate
                    model.itemDescription = itemDescription
                    model.itemImage = itemImage
                    model.itemName = itemName
                    model.itemNotes = itemNotes
                    model.timestamp = timestamp.toLong()
                    model.uid = uid

                    //Load item category
                    MyApplication.loadCategory(itemCategoryId,binding.categoryTV)
                    //Load item thumbnail, pages count
                    MyApplication.loadItemImgFromUrlSinglePage("$itemImage", "$itemName")
                    //Load item image size
                    MyApplication.loadImageSize("$itemImage","$itemName",binding.sizeTv)

                    holder.titleTv.text = itemName
                    holder.descriptionTv.text = itemDescription
                    holder.notesTv.text = itemNotes
                    holder.dateTV.text = itemDate
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    inner class HolderItemFavorite(itemView: View):RecyclerView.ViewHolder(itemView){

        var titleTv = binding.titleTv
        var descriptionTv = binding.descriptionTv
        var notesTv = binding.notesTv
        var categoryTV = binding.categoryTV
        var sizeTv = binding.sizeTv
        var dateTV = binding.dateTV
    }


}