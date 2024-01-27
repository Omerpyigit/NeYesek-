package com.example.bugunneyesem.filters

import android.widget.Filter
import com.example.bugunneyesem.adapters.KategoriAdapter
import com.example.bugunneyesem.models.ModelKategori

class FiltreKategori: Filter {

    private var filterList: ArrayList<ModelKategori>
    private var adaptarCategory: KategoriAdapter

    constructor(filterList: ArrayList<ModelKategori>, adaptarCategory: KategoriAdapter) : super() {
        this.filterList = filterList
        this.adaptarCategory = adaptarCategory
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()

        //boş değer var mı
        if(constraint != null && constraint.isNotEmpty()){


            constraint = constraint.toString().uppercase()
            val filteredModels:ArrayList<ModelKategori> = ArrayList()
            for(i in 0 until filterList.size){
                if(filterList[i].kategori.uppercase().contains(constraint)){
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            //aranan değer searched null or empty
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        //filtre değişikliklerini uygula

        adaptarCategory.kategoriArrayList = results.values as ArrayList<ModelKategori>
        adaptarCategory.notifyDataSetChanged()

    }
}