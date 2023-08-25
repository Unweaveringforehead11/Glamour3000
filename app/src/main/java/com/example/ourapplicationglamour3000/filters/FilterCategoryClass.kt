package com.example.ourapplicationglamour3000.filters

import android.widget.Filter
import com.example.ourapplicationglamour3000.models.ModelClass
import com.example.ourapplicationglamour3000.adapters.AdapterCategoryClass

class FilterCategoryClass: Filter {

    //arraylist in which we want to search
    private var filterList:ArrayList<ModelClass>

    //adapter in which filter needs to be implemented
    private var adapterCategory: AdapterCategoryClass

    constructor(filterList: ArrayList<ModelClass>, adapterCategory: AdapterCategoryClass) {
        this.filterList = filterList
        this.adapterCategory = adapterCategory
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()

        //value should not be null and not empty
        if (constraint != null && constraint.isNotEmpty()){

            constraint = constraint.toString().uppercase()
            val filteredModels:ArrayList<ModelClass> = ArrayList()
            for (i in 0 until filterList.size){
                //validate
                if (filterList[i].categoryTitle.uppercase().contains(constraint)){
                    //add to filtered list
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            //search value is either null or empty
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        //apply filter changes
        adapterCategory.categoryArrayList = results.values as ArrayList<ModelClass>
        //notify changes
        adapterCategory.notifyDataSetChanged()
    }
}