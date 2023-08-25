package com.example.ourapplicationglamour3000.filters

import android.widget.Filter
import com.example.ourapplicationglamour3000.models.ModellClass
import com.example.ourapplicationglamour3000.adapters.AdapterItemClass


class FilterItemClass : Filter {

    private var filterList:ArrayList<ModellClass>

    private var adapterItem: AdapterItemClass

    constructor(filterList: ArrayList<ModellClass>, adapterItem: AdapterItemClass) {
        this.filterList = filterList
        this.adapterItem= adapterItem
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()

        if (constraint != null && constraint.isNotEmpty()){

            constraint = constraint.toString().uppercase()
            val filteredModels:ArrayList<ModellClass> = ArrayList()
            for (i in 0 until filterList.size){
                if (filterList[i].itemName.uppercase().contains(constraint)){
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapterItem.itemArrayList = results.values as ArrayList<ModellClass>

        adapterItem.notifyDataSetChanged()
    }


}