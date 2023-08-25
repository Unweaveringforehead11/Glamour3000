package com.example.ourapplicationglamour3000.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.ourapplicationglamour3000.filters.FilterItemClass
import com.example.ourapplicationglamour3000.models.ModellClass
import com.example.ourapplicationglamour3000.databinding.RowItemBinding
import com.google.firebase.database.FirebaseDatabase

class AdapterItemClass : RecyclerView.Adapter<AdapterItemClass.HolderItem>, Filterable{

    private val context: Context
    public var itemArrayList: ArrayList<ModellClass>
    private var filterList: ArrayList<ModellClass>

    private var filter: FilterItemClass? = null
    private lateinit var binding: RowItemBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderItem {
        binding = RowItemBinding.inflate(LayoutInflater.from(context),parent,false)

        return HolderItem(binding.root)
    }

    override fun onBindViewHolder(holder: HolderItem, position: Int) {
        val model = itemArrayList[position]

        val id = model.id
        val itemName = model.itemName
        val itemDescription = model.itemDescription
        val itemCategory = model.itemCategory
        val itemCategoryId = model.itemCategoryId
        val itemNotes = model.itemNotes
        val itemDate = model.itemDate
        val timestamp = model.timestamp
        val uid = model.uid

        holder.itemTv.text = itemName

        holder.deleteBtn.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Confirm"){a, d->
                    Toast.makeText(context, "Deleting...",
                        Toast.LENGTH_SHORT).show()
                    deleteItem(model,holder)
                }
                .setNegativeButton("Cancel"){a, d->
                    a.dismiss()
                }
                .show()
        }
    }

    constructor(context: Context, itemArrayList: ArrayList<ModellClass>) {
        this.context = context
        this.itemArrayList = itemArrayList
        this.filterList = itemArrayList
    }

    override fun getItemCount(): Int {
        return itemArrayList.size
    }


    private fun deleteItem(model: ModellClass, holder: HolderItem) {
        val id = model.id

        val ref = FirebaseDatabase.getInstance().getReference("Items")
        ref.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Item deleted...",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                Toast.makeText(context, "Unable to delete due to ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

    inner class HolderItem(itemView: View): RecyclerView.ViewHolder(itemView){

        var itemTv: TextView = binding.itemTv
        var deleteBtn: ImageButton = binding.deleteBtn
    }
    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterItemClass(filterList, this)
        }
        return filter as FilterItemClass
    }
}