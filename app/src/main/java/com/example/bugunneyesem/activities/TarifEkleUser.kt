package com.example.bugunneyesem.activities

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.bugunneyesem.databinding.ActivityTarifEkleUserBinding
import com.example.bugunneyesem.models.ModelKategori
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class TarifEkleUser : AppCompatActivity() {
    private lateinit var binding: ActivityTarifEkleUserBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var imgUri : Uri? = null
    //kategoriler
    private lateinit var kategoriArrayList: ArrayList<ModelKategori>


    private val TAG = "TARIF_IMG_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityTarifEkleUserBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        loadTarifKategorileri()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Bilgilendirme")
            .setMessage("Tarifin malzemelerini eklerken her malzeme arasına , (virgül) ekleyiniz")
            .setPositiveButton("TAMAM"){a, d->
                a.dismiss()
            }
            .show()

        binding.backbutton3.setOnClickListener{

            startActivity(Intent(this, TariflerPageUser::class.java))
        }

        //fotograf yükleme
        binding.attachBut.setOnClickListener{
            selectImage()
        }

        //kategorileri göstere tıklandığında
        binding.kategoriTvv.setOnClickListener{
            kategoriPickDialog()
        }

        binding.kaydetBut2.setOnClickListener {
            validateData()
        }
    }

    private var title = ""
    private var aciklama = ""
    private var kategori = ""
    private var malzemeler = ""
    private var tarifDetay = ""

    private fun validateData(){
        Log.d(TAG, "validateData: validating data")
        val timestamp = System.currentTimeMillis()
        title = binding.titleEt.text.toString().trim()
        aciklama = binding.descriptionEt.text.toString().trim()
        malzemeler = binding.malzemelerEt.text.toString().trim()
        tarifDetay = binding.detayEt.text.toString().trim()
        kategori = binding.kategoriTvv.text.toString().trim()

        //eksik veri varmı uyarı
        if(title.isEmpty()){
            Toast.makeText(this,"Tarif İsmi Giriniz ",Toast.LENGTH_SHORT).show()
        }
        else if(aciklama.isEmpty()){
            Toast.makeText(this,"Tarif Açıklama Giriniz ",Toast.LENGTH_SHORT).show()
        }
        else if(malzemeler.isEmpty()){
            Toast.makeText(this,"Tarifin Malzemelerini Giriniz ",Toast.LENGTH_SHORT).show()
        }
        else if(tarifDetay.isEmpty()){
            Toast.makeText(this,"Tarifin Detayını Giriniz ",Toast.LENGTH_SHORT).show()
        }
        else if(kategori.isEmpty()){
            Toast.makeText(this,"Kategori Seçiniz ",Toast.LENGTH_SHORT).show()
        }
        else if(imgUri == null){
            Toast.makeText(this,"Lütfen SAG USTTEN Resim Ekleyiniz",Toast.LENGTH_SHORT).show()
        }
        else{
            getImgFromStorage()
            //uploadImgToStorage()
        }






    }

    private fun getImgFromStorage(){
        val timestamp = System.currentTimeMillis()
        val filePathandName = "Tarifler/$timestamp"

        val ref = FirebaseStorage.getInstance().getReference(filePathandName)
        var uploadTask = ref.putFile(imgUri!!)
        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                uploadTarifToDb(downloadUri, timestamp)
            } else {
                // Handle failures
                // ...
                Toast.makeText(this,"Resim Ekleme Başarısız",Toast.LENGTH_SHORT).show()
            }
        }


    }

    /*
    private fun uploadImgToStorage(){
        Log.d(TAG, "Fotograf database'e aktarılıyor")

        val timestamp = System.currentTimeMillis()
        val filePathandName = "Tarifler/$timestamp"


        val storageReference = FirebaseStorage.getInstance().getReference(filePathandName)
        storageReference.putFile(imgUri!!)
            .addOnSuccessListener {taskSnapshot-> //taskSnapshot çalışan snapshot
                Log.d(TAG,"uploadImgToStorage aşamasında")
                Toast.makeText(this,"Resim Ekleme Başarılı URL alınıyor...",Toast.LENGTH_SHORT).show()

                //yüklenen img dosyasının url'sini alma
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while(!uriTask.isSuccessful);
                val uploadadImgUrl = "${uriTask.result}"

                //tek başına çalışıyo val uploadadImgUrl = snapshot.storage.downloadUrl.toString()
                //tüm tarifi database'e gönderme

                uploadTarifToDb(uploadadImgUrl, timestamp)

            }
            .addOnFailureListener{
                Log.d(TAG,"Resim database'e gönderilemedi")
                Toast.makeText(this,"Resim Ekleme Başarısız",Toast.LENGTH_SHORT).show()
            }
    }
    */


    private fun uploadTarifToDb(uploadadImgUrl: String, timestamp: Long){
        Log.d(TAG,"uploadImgToDB calisti resim storage'de")
        Log.d(TAG,"uploadTarifToDb basladi")

        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["baslik"] = "$title"
        hashMap["aciklama"] = "$aciklama"
        hashMap["malzemeler"] = "$malzemeler"
        hashMap["tarifDetay"] = "$tarifDetay"
        hashMap["kategoriId"] = "$selectedCategoryId"
        hashMap["img"] = "$uploadadImgUrl"
        hashMap["timestamp"] = timestamp
        hashMap["viewsCount"] = 0
        hashMap["favoriler"] = 0

        val ref = FirebaseDatabase.getInstance().getReference("Tarifler")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadTarifToDb basarili")
                Toast.makeText(this,"Başarılı ",Toast.LENGTH_SHORT).show()
                imgUri = null
            }
            .addOnFailureListener{e->
                Toast.makeText(this,"uploadTarifToDb HATA ${e.message}",Toast.LENGTH_SHORT).show()

            }

    }


    private fun loadTarifKategorileri(){
        Log.d(TAG,"loadTarifKategorileri: Tarif kategorileri yükleniyor")
        kategoriArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Kategoriler")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                kategoriArrayList.clear()
                for (ds in snapshot.children){
                    val model = ds.getValue(ModelKategori::class.java)

                    kategoriArrayList.add(model!!)
                    Log.d(TAG,"loadTarifKategorileri onDataChange: ${model.kategori}")
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "loadTarifKategorileri HATA")
            }

        } )

    }
    private var selectedCategoryId = ""
    private var selectedCategoryIsim = ""

    private fun kategoriPickDialog() {
        Log.d(TAG, "categoryPickDialog: Tarif kategorileri gösteriliyor")

        val kategorilerArray = arrayOfNulls<String>(kategoriArrayList.size)
        for(i in kategoriArrayList.indices){
            kategorilerArray[i] = kategoriArrayList[i].kategori
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Kategori Seç")
            .setItems(kategorilerArray){dialog, which->

                selectedCategoryIsim = kategoriArrayList[which].kategori
                selectedCategoryId = kategoriArrayList[which].id
                //textview de göster
                binding.kategoriTvv.text = selectedCategoryIsim

                Log.d(TAG, "kategoriPickDialog: Selected Kategori ID: $selectedCategoryId")
                Log.d(TAG, "kategoriPickDialog: Selected Kategori ISIM: $selectedCategoryIsim")
            }
            .show()
    }

    private fun selectImage(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        imgActivityResultLauncher.launch(intent)

    }

    val imgActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{result ->
            if(result.resultCode == RESULT_OK){
                Log.d(TAG, "Resim Secildi")
                imgUri = result.data!!.data
            }
            else{
                Log.d(TAG,"Resim secme basarısız")
                Toast.makeText(this,"Başarısız ",Toast.LENGTH_SHORT).show()
            }

        }
    )


}