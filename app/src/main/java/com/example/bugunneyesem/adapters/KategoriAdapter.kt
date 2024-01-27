package com.example.bugunneyesem.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bugunneyesem.filters.FiltreKategori
import com.example.bugunneyesem.models.ModelKategori
import com.example.bugunneyesem.activities.TarifListActivityAdmin
import com.example.bugunneyesem.databinding.KategoriSatirBinding
import com.google.firebase.database.FirebaseDatabase


class KategoriAdapter :RecyclerView.Adapter<KategoriAdapter.HolderKategori>, Filterable {
    private val context: Context
    public var kategoriArrayList: ArrayList<ModelKategori>
    private var filterList: ArrayList<ModelKategori>

    private var filter: FiltreKategori? = null
    private lateinit var binding: KategoriSatirBinding

    constructor(context: Context, kategoriArrayList: ArrayList<ModelKategori>) {
        this.context = context
        this.kategoriArrayList = kategoriArrayList
        this.filterList = kategoriArrayList
    }


    inner class HolderKategori(itemView: View): RecyclerView.ViewHolder(itemView){
        var kategoriTv:TextView = binding.kategoriTv
        var silButton:ImageButton = binding.silButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderKategori {
        binding = KategoriSatirBinding.inflate(LayoutInflater.from(context), parent,false)
        return HolderKategori(binding.root)
    }

    override fun getItemCount(): Int {
        return kategoriArrayList.size
    }

    override fun onBindViewHolder(holder: HolderKategori, position: Int) {
        //veri al
        val model = kategoriArrayList[position]
        val id = model.id
        val kategori = model.kategori
        val uid = model.uid
        val timestamp = model.timestamp

        //veri işle
        holder.kategoriTv.text = kategori

        //veri sil
        holder.silButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Sil")
                .setMessage("Kategoriyi silmek istediğinden emin misin?")
                .setPositiveButton("Sil"){a, d->
                    Toast.makeText(context,"Siliniyor",Toast.LENGTH_SHORT).show()
                    deleteKategori(model, holder)
                }
                .setNegativeButton("İptal"){a,d->
                    a.dismiss()

                }
                .show()
        }
        //sonradan ekledim admin özel tarifler listesi
        holder.itemView.setOnClickListener {
            val intent = Intent(context, TarifListActivityAdmin::class.java)
            intent.putExtra("kategoriID", id)
            intent.putExtra("kategori", kategori)
            context.startActivity(intent)
        }
    }

    private fun deleteKategori(model: ModelKategori, holder: HolderKategori){
        val id = model.id
        val ref = FirebaseDatabase.getInstance().getReference("Kategoriler")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context,"Silindi",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                Toast.makeText(context,"${e.message}'dan dolayı silinemedi",Toast.LENGTH_SHORT).show()
            }
    }

    override fun getFilter(): Filter {
        if(filter == null){
            filter = FiltreKategori(filterList, this)
        }
        return filter as FiltreKategori
    }
}