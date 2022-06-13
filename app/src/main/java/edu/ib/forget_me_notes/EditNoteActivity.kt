package edu.ib.forget_me_notes

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import edu.ib.forget_me_notes.model.Note
import kotlinx.android.synthetic.main.activity_edit_note.*
import com.google.android.gms.tasks.Continuation

class EditNoteActivity : AppCompatActivity() {

    private lateinit var noteID: String

    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageNotePictureRef: StorageReference? = null

    var pickedBitMap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        noteID = intent.getStringExtra("noteId").toString()

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageNotePictureRef = FirebaseStorage.getInstance().reference.child("User notes")

        close_edit_btn.setOnClickListener {
            val intent = Intent(this@EditNoteActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        save_edit_btn.setOnClickListener {
            if (checker == "clicked") {
                updateImageAndInfo()
            } else {
                updateInfo()
            }
        }

        noteInfo()

        delete_btn.setOnClickListener {
            deleteNote()

            val intent = Intent(this@EditNoteActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    fun pickNewPhoto(view: View){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
        } else {
            val galeriIntext = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntext,2)
        }
        checker == "clicked"
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
                    edit_plant_photo.setImageBitmap(pickedBitMap)
                }
                else {
                    pickedBitMap = MediaStore.Images.Media.getBitmap(this.contentResolver,imageUri)
                    edit_plant_photo.setImageBitmap(pickedBitMap)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateImageAndInfo(){

        when {
            imageUri == null -> Toast.makeText(this, "Add image", Toast.LENGTH_LONG).show()

            TextUtils.isEmpty(edit_light_txt.text.toString()) -> Toast.makeText(this, "Add light level", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(edit_water_txt.text.toString()) -> Toast.makeText(this, "Add water level", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(edit_ground_txt.text.toString()) -> Toast.makeText(this, "Add ground type", Toast.LENGTH_LONG).show()

            TextUtils.isEmpty(edit_plant_nick.text.toString()) -> Toast.makeText(this, "Add plant nick", Toast.LENGTH_LONG).show()

            TextUtils.isEmpty(edit_plant_name.text.toString()) -> Toast.makeText(this, "Add plant name", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(edit_plant_info.text.toString()) -> Toast.makeText(this, "Add info about plant", Toast.LENGTH_LONG).show()

            else -> {

                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Editing Note")
                progressDialog.setMessage("Please wait, work in progress...")
                progressDialog.show()

                val fileRef = storageNotePictureRef!!.child(System.currentTimeMillis().toString() + ".jpg")

                val uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->

                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }

                    return@Continuation fileRef.downloadUrl

                }).addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Notes")

                        val noteMap = HashMap<String, Any>()
                        noteMap["name"] = edit_plant_name.text.toString()
                        noteMap["nick"] = edit_plant_nick.text.toString()
                        noteMap["info"] = edit_plant_info.text.toString()
                        noteMap["light"] = edit_light_txt.text.toString().lowercase()
                        noteMap["water"] = edit_water_txt.text.toString().lowercase()
                        noteMap["ground"] = edit_ground_txt.text.toString().lowercase()
                        noteMap["noteimage"] = myUrl

                        ref.child(noteID).updateChildren(noteMap)

                        Toast.makeText(
                            this,
                            "Note has been updated successfully.",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(this@EditNoteActivity, MainActivity::class.java)
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

    private fun updateInfo() {

        when {
            TextUtils.isEmpty(edit_light_txt.text.toString()) -> Toast.makeText(this, "Add light level", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(edit_water_txt.text.toString()) -> Toast.makeText(this, "Add water level", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(edit_ground_txt.text.toString()) -> Toast.makeText(this, "Add ground type", Toast.LENGTH_LONG).show()

            TextUtils.isEmpty(edit_plant_nick.text.toString()) -> Toast.makeText(this, "Add plant nick", Toast.LENGTH_LONG).show()

            TextUtils.isEmpty(edit_plant_name.text.toString()) -> Toast.makeText(this, "Add plant name", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(edit_plant_info.text.toString()) -> Toast.makeText(this, "Add info about plant", Toast.LENGTH_LONG).show()

            else -> {
                val noteRef = FirebaseDatabase.getInstance().reference.child("Notes")

                val noteMap = HashMap<String, Any>()
                noteMap["name"] = edit_plant_name.text.toString()
                noteMap["nick"] = edit_plant_nick.text.toString()
                noteMap["info"] = edit_plant_info.text.toString()
                noteMap["light"] = edit_light_txt.text.toString().lowercase()
                noteMap["water"] = edit_water_txt.text.toString().lowercase()
                noteMap["ground"] = edit_ground_txt.text.toString().lowercase()

                noteRef.child(noteID).updateChildren(noteMap)

                Toast.makeText(this, "Note has been updated successfully.", Toast.LENGTH_LONG).show()

                val intent = Intent(this@EditNoteActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun noteInfo() {
        val noteRef = FirebaseDatabase.getInstance().reference.child("Notes").child(noteID)

        noteRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val note = snapshot.getValue(Note::class.java)
                    Picasso.get().load(note!!.getNoteimage()).rotate(90F).placeholder(R.drawable.leaf).into(edit_plant_photo)
                    edit_light_txt.setText(note.getLight())
                    edit_water_txt.setText(note.getWater())
                    edit_ground_txt.setText(note.getGround())
                    edit_plant_nick.setText(note.getNick())
                    edit_plant_name.setText(note.getName())
                    edit_plant_info.setText(note.getInfo())
                }
            }

            override fun onCancelled(error: DatabaseError) { }

        })
    }

    private fun deleteNote() {
        val noteRef = FirebaseDatabase.getInstance().reference.child("Notes")
        noteRef.child(noteID).removeValue()
    }

}