package com.feritkeskin.instaclone.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.feritkeskin.instaclone.databinding.ActivityUploadBinding
import com.feritkeskin.instaclone.util.MyPreferences
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    var selectedPicture: Uri? = null
    var pickedBitMap: Bitmap? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()
        auth = Firebase.auth
        db = Firebase.firestore

        binding.uploadShare.setOnClickListener {

            val uuid = UUID.randomUUID()
            val imageName = "$uuid.jpg"
            val storage = Firebase.storage
            val reference = storage.reference
            val imagesReference = reference.child("images").child(imageName)

            if (selectedPicture != null) {
                imagesReference.putFile(selectedPicture!!).addOnSuccessListener { taskSnapshot ->

                    val uploadedPictureReference =
                        storage.reference.child("images").child(imageName)
                    uploadedPictureReference.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()

                        val postMap = hashMapOf<String, Any>()
                        postMap["downloadUrl"] = downloadUrl
                        postMap["userEmail"] = auth.currentUser!!.email.toString()
                        postMap["userName"] = MyPreferences(this).userName.toString()
                        postMap["comment"] = binding.uploadCommentText.text.toString()
                        postMap["date"] = Timestamp.now()

                        println("2. Hello comment: ${postMap.get("comment")}")

                        db.collection("Posts").add(postMap).addOnCompleteListener { task ->

                            if (task.isComplete && task.isSuccessful) {
                                finish()
                            }
                        }
                    }

                }.addOnFailureListener { exception ->
                    Toast.makeText(
                        applicationContext,
                        exception.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.uploadImageView.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            } else {
                val galeriIntext =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntext, 2)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galeriIntext =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntext, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPicture = data.data
            if (selectedPicture != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(this.contentResolver, selectedPicture!!)
                    pickedBitMap = ImageDecoder.decodeBitmap(source)
                    binding.uploadImageView.setImageBitmap(pickedBitMap)
                } else {
                    pickedBitMap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPicture)
                    binding.uploadImageView.setImageBitmap(pickedBitMap)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}