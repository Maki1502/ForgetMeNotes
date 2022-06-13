package edu.ib.forget_me_notes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import edu.ib.forget_me_notes.model.Note
import edu.ib.forget_me_notes.R
import edu.ib.forget_me_notes.fragments.NoteFragment

class NoteAdapter(
    private val mContext: Context,
    private val mNote: List<Note>) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.note_layout, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val note = mNote[position]

        Picasso.get().load(note.getNoteimage()).into(holder.plantImage)

        holder.plantImage.rotation = 90F

        holder.name.text = note.getName()
        holder.nick.text = note.getNick()

        holder.itemView.setOnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("noteId", note.getNoteid())
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NoteFragment()).commit()
        }

    }

    override fun getItemCount(): Int {
        return mNote.size
    }

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        var plantImage: CircleImageView

        var nick: TextView
        var name: TextView

        init {
            plantImage = itemView.findViewById(R.id.plant_image_post)
            nick = itemView.findViewById(R.id.plant_nick_post)
            name = itemView.findViewById(R.id.plant_name_post)
        }
    }
}