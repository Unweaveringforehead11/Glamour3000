package com.example.ourapplicationglamour3000.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ourapplicationglamour3000.*
import com.example.ourapplicationglamour3000.activities.ItemDetailsViewActivity
import com.example.ourapplicationglamour3000.activities.ItemEditActivity
import com.example.ourapplicationglamour3000.databinding.ActivityRowItemDetailBinding
import com.example.ourapplicationglamour3000.filters.FilterItemDetailsClass
import com.example.ourapplicationglamour3000.models.ModelItems

class AdapterItemDetails :RecyclerView.Adapter<AdapterItemDetails.HolderItemDetails>, Filterable{

    private var context:Context
    public var itemDetailsArrayList: ArrayList<ModelItems>
    private var filterList:ArrayList<ModelItems>

    private lateinit var binding: ActivityRowItemDetailBinding


    private var filter: FilterItemDetailsClass? = null

    constructor(context: Context, itemDetailsArrayList: ArrayList<ModelItems>) : super() {
        this.context = context
        this.itemDetailsArrayList = itemDetailsArrayList
        this.filterList = itemDetailsArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderItemDetails {
        binding = ActivityRowItemDetailBinding.inflate(LayoutInflater.from(context), parent,false)

        return HolderItemDetails((binding.root))
    }

    override fun getItemCount(): Int {
        return itemDetailsArrayList.size
    }

    override fun onBindViewHolder(holder: HolderItemDetails, position: Int) {
        val model = itemDetailsArrayList[position]
        val id = model.id
        val itemImage = model.itemImage
        val itemName = model.itemName
        val itemDescription = model.itemDescription
        val itemCategory = model.itemCategory
        val itemCategoryId = model.itemCategoryId
        val itemNotes = model.itemNotes
        val itemDate = model.itemDate
        val timestamp = model.timestamp

        val formattedDate = MyApplication.formatTimeStamp(timestamp)
        //Set data
        holder.categoryTv.text = itemCategory
        holder.dateTv.text = formattedDate
        holder.descriptionTv.text = itemDescription
        holder.titleTv.text = itemName
        holder.itemNotes.text = itemNotes

        //Load category
        MyApplication.loadCategory(itemCategoryId, holder.categoryTv)

        //Load image size
        MyApplication.loadImageSize(itemImage, itemName, holder.sizeTv)

        //We don't need page number here, pass null for page number || load image thumbnail
        MyApplication.loadItemImgFromUrlSinglePage(
            itemImage,
            itemName
        )

        //Handle click, show dialog with option 1)Edit Item, 2)Delete Item
        holder.moreBtn.setOnClickListener {
            moreOptionDialog(model, holder)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ItemDetailsViewActivity::class.java)
            intent.putExtra("id", id)
            context.startActivity(intent)
        }
    }


    private fun moreOptionDialog(model: ModelItems, holder: HolderItemDetails) {
        //Get id, url, item name
        val itemId = model.id
        val itemImage = model.itemImage
        val itemName = model.itemName

        //Options to show in dialog
        val options = arrayOf("Edit","Delete")

        //Alert dialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(options){dialog, position->
                //Handle option click
                if (position == 0){
                    //Edit is clicked
                    val intent = Intent(context, ItemEditActivity::class.java)
                    intent.putExtra("itemId", itemId)//Passed itemId, will be used to edit the item
                    context.startActivity(intent)
                }
                else if(position == 1){
                    //Delete is clicked
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Confirm"){a, d->
                            Toast.makeText(context, "Deleting...",
                                Toast.LENGTH_SHORT).show()
                            MyApplication.deleteItem(context, itemId, itemImage, itemName)
                        }
                        .setNegativeButton("Cancel"){a, d->
                            a.dismiss()
                        }
                        .show()
                }
            }
            .show()
    }

    inner class HolderItemDetails(itemView: View) : RecyclerView.ViewHolder(itemView){

//        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val categoryTv = binding.categoryTV
        val dateTv = binding.dateTV
        val sizeTv = binding.sizeTv
        val moreBtn = binding.moreBtn
        val itemNotes = binding.notesTv
    }

    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterItemDetailsClass(filterList,this)
        }
        return filter as FilterItemDetailsClass
    }


}