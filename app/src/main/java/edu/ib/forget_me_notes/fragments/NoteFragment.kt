package edu.ib.forget_me_notes.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import edu.ib.forget_me_notes.EditNoteActivity
import edu.ib.forget_me_notes.R
import edu.ib.forget_me_notes.model.Note
import kotlinx.android.synthetic.main.fragment_note.view.*

class NoteFragment : Fragment() {

    private lateinit var noteID: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_note, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.noteID = pref.getString("noteId", "none").toString()
        }

        view.close_note_btn.setOnClickListener {
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
        }

        view.edit_note_btn.setOnClickListener {
            var intent = Intent(context, EditNoteActivity::class.java)
            intent.putExtra("noteId", noteID)
            startActivity(intent)
        }

        noteInfo()

        return view
    }

    private fun noteInfo() {
        val noteRef = FirebaseDatabase.getInstance().reference.child("Notes").child(noteID)

        noteRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val note = snapshot.getValue(Note::class.java)
                    Picasso.get().load(note!!.getNoteimage()).rotate(90F).placeholder(R.drawable.leaf).into(view?.note_photo)
                    view?.note_light_txt?.text = note.getLight()
                    view?.note_water_txt?.text = note.getWater()
                    view?.note_ground_txt?.text = note.getGround()
                    view?.note_plant_nick?.text = note.getNick()
                    view?.note_plant_name?.text = note.getName()
                    view?.note_plant_info?.text = note.getInfo()
                }
            }

            override fun onCancelled(error: DatabaseError) { }

        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("noteId", firebaseUser.uid)
        pref?.apply()

    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("noteId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("noteId", firebaseUser.uid)
        pref?.apply()
    }

}