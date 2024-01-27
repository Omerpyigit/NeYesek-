package com.example.bugunneyesem.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.bugunneyesem.FoodsUserFragment
import com.example.bugunneyesem.databinding.ActivityTariflerPageUserBinding
import com.example.bugunneyesem.models.ModelKategori
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TariflerPageUser : AppCompatActivity() {
    lateinit var binding: ActivityTariflerPageUserBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var kategoriArrayList: ArrayList<ModelKategori>
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTariflerPageUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()



        setupWithViewPagerAdapter(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.backBtn.setOnClickListener{
            startActivity(Intent(this, ManiAnasayfa::class.java))
        }
        binding.fabBtn.setOnClickListener{
            startActivity(Intent(this, TarifEkleUser::class.java))
        }
    }

    private fun setupWithViewPagerAdapter(viewPager: ViewPager){
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this)

        kategoriArrayList = ArrayList()

        //veri tabanından kategorileri yükleme
        val ref = FirebaseDatabase.getInstance().getReference("Kategoriler")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                kategoriArrayList.clear()

                val modelAll = ModelKategori("01", "Hepsi", 1, "")
                val modelMostViewed = ModelKategori("01", "En Çok Görüntülenen", 1, "")

                kategoriArrayList.add(modelAll)
                kategoriArrayList.add(modelMostViewed)
                //viewPagerAdapter'a ekleme yeni kategori eklerken burayada eklenmeli
                viewPagerAdapter.addFragment(
                    FoodsUserFragment.newInstance(
                        "${modelAll.id}",
                        "${modelAll.kategori}",
                        "${modelAll.uid}"
                    ), modelAll.kategori
                )
                viewPagerAdapter.addFragment(
                    FoodsUserFragment.newInstance(
                        "${modelMostViewed.id}",
                        "${modelMostViewed.kategori}",
                        "${modelMostViewed.uid}"
                    ), modelMostViewed.kategori
                )

                //refresh list
                viewPagerAdapter.notifyDataSetChanged()

                //firebase db den load diğer kategoriler için
                for(ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelKategori::class.java)
                    kategoriArrayList.add(model!!)
                    //viewPagerAdapter a ekleme
                    viewPagerAdapter.addFragment(
                        FoodsUserFragment.newInstance(
                            "${model.id}",
                            "${model.kategori}",
                            "${model.uid}"
                        ), model.kategori
                    )

                    viewPagerAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"SetupWithPageAdapter hata", Toast.LENGTH_LONG).show()
            }

        })

        //setup adapter to viewpager
        viewPager.adapter = viewPagerAdapter
    }

    class ViewPagerAdapter(fm: FragmentManager, behavior: Int, context: Context): FragmentPagerAdapter(fm, behavior){
        private val fragmentsList: ArrayList<FoodsUserFragment> = ArrayList()
        //kategori başlıklarının listesi for tab
        private val fragmentTitleList: ArrayList<String> = ArrayList()

        private val context: Context

        init {
            this.context = context
        }

        override fun getCount(): Int {
            return fragmentsList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentsList[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitleList[position]
        }

        public fun addFragment(fragment: FoodsUserFragment, title: String){
            fragmentsList.add(fragment)
            fragmentTitleList.add(title)
        }

    }
}