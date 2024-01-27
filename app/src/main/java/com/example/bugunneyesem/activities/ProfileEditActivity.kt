package com.example.bugunneyesem.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.bugunneyesem.R
import com.example.bugunneyesem.databinding.ActivityProfileEditBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    private lateinit var firebaseAuth: FirebaseAuth

    //image uri
    private var imageUri: Uri?= null

    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Lütfen Bekleyin")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        loadUserInfo()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        //startActivity(Intent(this,ProfileActivity::class.java))
        }

        binding.profileTv.setOnClickListener{
            showImageAttachMenu()
        }

        binding.updateBtn.setOnClickListener {
            validateData()
        }
    }

    private var name = ""
    private fun validateData(){

        name = binding.nameEt.text.toString().trim()

        if(name.isEmpty()){
           Toast.makeText(this,"Kullanıcı Adı Girin",Toast.LENGTH_SHORT).show()
        }
        else{
            if(imageUri==null){
                //resim olmadan güncelle
                updateProfile("")
            }
            else{
                uploadImage()
            }
        }
    }

    private fun updateProfile(uploadedImgUrl: String){
        progressDialog.setMessage("Profil güncelleniyor...")

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["kadi"] = "$name"
        if(imageUri != null ){
            hashMap["profilPhoto"] = uploadedImgUrl
        }

        val referance = FirebaseDatabase.getInstance().getReference("Users")
        referance.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this,"Profil Güncellendi",Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Güncelleme Başarısız ${e.message}",Toast.LENGTH_SHORT).show()
            }

    }

    private fun uploadImage(){
        progressDialog.setMessage("Profil Resmi Yükleniyor")
        progressDialog.show()

        val filePathAndName = "ProfileImages/"+firebaseAuth.uid
        val ref = FirebaseStorage.getInstance().getReference(filePathAndName)
        var uploadTask = ref.putFile(imageUri!!)
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
                updateProfile(downloadUri)
            } else {
                // Handle failures
                // ...
                Toast.makeText(this,"Resim Ekleme Başarısız",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadUserInfo(){
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val kadi = "${snapshot.child("kadi").value}"
                    val kayit_tarih = "${snapshot.child("kayit_tarih").value}"
                    val profilPhoto = "${snapshot.child("profilPhoto").value}"


                    //verileri aktar
                    binding.nameEt.setText(kadi)

                    //set image
                    try {
                        Glide.with(this@ProfileEditActivity)
                            .load(profilPhoto)
                            .placeholder(R.drawable.ic_profile_gray)
                            .into(binding.profileTv)
                    }
                    catch (e: Exception){

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun showImageAttachMenu(){
        //popup menü Kamera/Galeri seçimi için

        val popupMenu = PopupMenu(this,binding.profileTv)
        popupMenu.menu.add(Menu.NONE,0,0,"Kamera")
        popupMenu.menu.add(Menu.NONE,1,1,"Galeri")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {item->
            val id = item.itemId
            if(id==0){
                //Kameraya tıklandığında
                pickImageCamera()
            }
            else if(id==1){
                //galeriye tıklandığında
                pickImageGallery()
            }

            true
        }
    }

    private fun pickImageCamera(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Temp_Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)


    }

    private fun pickImageGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)

    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data
                //imageUri = data!!.data

                binding.profileTv.setImageURI(imageUri)
            }
            else{
                Toast.makeText(this,"İptal Edildi",Toast.LENGTH_SHORT).show()
            }
        }
    )

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> {result->
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data
                imageUri = data!!.data

                binding.profileTv.setImageURI(imageUri)
            }
            else{
                Toast.makeText(this,"İptal Edildi",Toast.LENGTH_SHORT).show()
            }
        }
    )
}