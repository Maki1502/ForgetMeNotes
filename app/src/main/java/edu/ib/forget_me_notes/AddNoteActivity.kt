package edu.ib.forget_me_notes

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import edu.ib.forget_me_notes.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_add_note.*

class AddNoteActivity : AppCompatActivity() {

    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostRef: StorageReference? = null
    var pickedBitMap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        storagePostRef = FirebaseStorage.getInstance().reference.child("User notes")

        close_profile_btn.setOnClickListener {
            val intent = Intent(this@AddNoteActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        save_info_profile_btn.setOnClickListener {
            uploadNote()
        }
    }

    fun pickPhoto(view: View){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
        } else {
            val galeriIntext = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntext,2)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galeriIntext = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntext,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            if (imageUri != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(this.contentResolver,imageUri!!)
                    pickedBitMap = ImageDecoder.decodeBitmap(source)
                    plant_photo.setImageBitmap(pickedBitMap)
                }
                else {
                    pickedBitMap = MediaStore.Images.Media.getBitmap(this.contentResolver,imageUri)
                    plant_photo.setImageBitmap(pickedBitMap)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadNote() {

        when{
            imageUri == null -> Toast.makeText(this, "Please add image first", Toast.LENGTH_LONG).show()

            TextUtils.isEmpty(light_txt.text.toString()) -> Toast.makeText(this, "Add light level", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(water_txt.text.toString()) -> Toast.makeText(this, "Add water level", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(ground_txt.text.toString()) -> Toast.makeText(this, "Add ground type", Toast.LENGTH_LONG).show()

            TextUtils.isEmpty(plant_nick.text.toString()) -> Toast.makeText(this, "Add plant nick", Toast.LENGTH_LONG).show()

            TextUtils.isEmpty(plant_name.text.toString()) -> Toast.makeText(this, "Add plant name", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(plant_info.text.toString()) -> Toast.makeText(this, "Add info about plant", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Account Settings")
                progressDialog.setMessage("Please wait, adding note...")
                progressDialog.show()

                val fileRef = storagePostRef!!.child(System.currentTimeMillis().toString() + ".jpg")

                val uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Notes")

                        val noteid = ref.push().key

                        val noteMap = HashMap<String, Any>()
                        noteMap["noteid"] = noteid!!
                        noteMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        noteMap["name"] = plant_name.text.toString()
                        noteMap["nick"] = plant_nick.text.toString()
                        noteMap["info"] = plant_info.text.toString()
                        noteMap["light"] = light_txt.text.toString().lowercase()
                        noteMap["water"] = water_txt.text.toString().lowercase()
                        noteMap["ground"] = ground_txt.text.toString().lowercase()
                        noteMap["noteimage"] = myUrl

                        ref.child(noteid).updateChildren(noteMap)

                        Toast.makeText(
                            this,
                            "Post has been uploaded successfully.",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(this@AddNoteActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                    }
                }
            }

        }
    }

}