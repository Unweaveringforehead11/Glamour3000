package com.example.ourapplicationglamour3000.filters

import android.widget.Filter
import com.example.ourapplicationglamour3000.models.ModelItems
import com.example.ourapplicationglamour3000.adapters.AdapterItemDetails

class FilterItemDetailsClass: Filter {

    private var filterList:ArrayList<ModelItems>

    private var adapterItemDetails: AdapterItemDetails

    constructor(filterList: ArrayList<ModelItems>, adapterItem: AdapterItemDetails) : super() {
        this.filterList = filterList
        this.adapterItemDetails = adapterItem
    }


    override fun performFiltering(constraint: CharSequence?): FilterResults {
        //Value to search
        var constraint:CharSequence? = constraint
        var results = FilterResults()
        //Value to be searched should not be null and not empty
        if (constraint != null && constraint.isNotEmpty()){
            //Change to upper case, or lower case to avoid case sensitivity
            constraint = constraint.toString().lowercase()
            val filteredModels = ArrayList<ModelItems>()
            for (i in filterList.indices){
                //Validate if match
                if (filterList[i].itemName.lowercase().contains(constraint)){
                    //Searched value is similar to value in list, add to filtered list
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            //Searched value is either null or empty, return all data
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapterItemDetails.itemDetailsArrayList = results.values as ArrayList<ModelItems>

        adapterItemDetails.notifyDataSetChanged()
    }
}