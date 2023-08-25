package com.example.ourapplicationglamour3000.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.ourapplicationglamour3000.filters.FilterCategoryClass
import com.example.ourapplicationglamour3000.activities.ItemDetailActivity
import com.example.ourapplicationglamour3000.models.ModelClass
import com.example.ourapplicationglamour3000.databinding.RowCategoryBinding
import com.google.firebase.database.FirebaseDatabase

class AdapterCategoryClass :RecyclerView.Adapter<AdapterCategoryClass.HolderCategory>,Filterable {

    private val context: Context
    public var categoryArrayList: ArrayList<ModelClass>
    private var filterList: ArrayList<ModelClass>

    private var filter: FilterCategoryClass? = null

    private lateinit var binding: RowCategoryBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context),parent,false)

        return HolderCategory(binding.root)
    }
    constructor(context: Context, categoryArrayList: ArrayList<ModelClass>) {
        this.context = context
        this.categoryArrayList = categoryArrayList
        this.filterList = categoryArrayList

    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {

        val model = categoryArrayList[position]
        val id = model.id
        val categoryTitle = model.categoryTitle
        val categoryGoal = model.categoryGoal
        val timestamp = model.timestamp
        val uid = model.uid

        holder.categoryTv.text = categoryTitle

        holder.deleteBtn.setOnClickListener{
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete this category?")
                .setPositiveButton("Confirm"){a, d->
                    Toast.makeText(context, "Deleting...",
                        Toast.LENGTH_SHORT).show()
                    deleteCategory(model,holder)
                }
                .setNegativeButton("Cancel"){a, d->
                    a.dismiss()
                }
                .show()
        }

        //handle click, start item details activity, also has item image, title
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ItemDetailActivity::class.java)
            intent.putExtra("categoryId", id)
            intent.putExtra("categoryTitle", categoryTitle)
            context.startActivity(intent)
        }
    }



    private fun deleteCategory(model: ModelClass, holder: HolderCategory) {
        val id = model.id

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Category deleted...",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e->
                Toast.makeText(context, "Unable to delete due to ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }

    //ViewHolder class to hold/init UI views for row_category
    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView){

        //init UI views
        var categoryTv: TextView = binding.categoryTv
        var deleteBtn: ImageButton = binding.deleteBtn
    }

    override fun getFilter(): Filter {

        if (filter == null){
            filter = FilterCategoryClass(filterList, this)
        }
        return filter as FilterCategoryClass
    }

}