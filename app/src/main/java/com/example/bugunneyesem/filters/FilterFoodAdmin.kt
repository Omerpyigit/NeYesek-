package com.example.bugunneyesem.filters

import android.widget.Filter
import com.example.bugunneyesem.adapters.TarifAdapterAdmin
import com.example.bugunneyesem.models.ModelTarif

//recycler view'de yemekleri filtreleme ve arama
class FilterFoodAdmin : Filter{

    var filterList: ArrayList<ModelTarif>
    var adapterImgAdmin: TarifAdapterAdmin

    constructor(filterList: ArrayList<ModelTarif>, adapterImgAdmin: TarifAdapterAdmin){
        this.filterList = filterList
        this.adapterImgAdmin = adapterImgAdmin
    }

    override fun performFiltering(constraint: CharSequence): FilterResults {
        var constraint:CharSequence? = constraint //arama için value
        val results = FilterResults()

        if (constraint != null && constraint.isNotEmpty()){
            constraint = constraint.toString().lowercase()
            val filteredModels = ArrayList<ModelTarif>()
            for (i in filteredModels.indices){
                if(filterList[i].baslik.lowercase().contains(constraint)){
                    //aranan değer listedeki değere yakınsa filtered listeye ekle
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

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
       adapterImgAdmin.foodArrayList = results!!.values as ArrayList<ModelTarif>

        adapterImgAdmin.notifyDataSetChanged()
    }

}