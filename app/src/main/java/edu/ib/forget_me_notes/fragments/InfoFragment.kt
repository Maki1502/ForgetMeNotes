package edu.ib.forget_me_notes.fragments

import android.content.Context
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
import edu.ib.forget_me_notes.R
import edu.ib.forget_me_notes.model.Info
import kotlinx.android.synthetic.main.fragment_info.view.*

class InfoFragment : Fragment() {

    private lateinit var infoID: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_info, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.infoID = pref.getString("infoId", "none").toString()
        }

        view.close_info_show_btn.setOnClickListener {
            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()).commit()
        }

        showInfo()

        return view
    }

    private fun showInfo() {
        val noteRef = FirebaseDatabase.getInstance().reference.child("Plants").child(infoID)

        noteRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val info = snapshot.getValue(Info::class.java)
                    Picasso.get().load(info!!.getImage()).placeholder(R.drawable.leaf).into(view?.plant_info_photo)
                    view?.plant_info_name?.text = info.getInfoname()
                    view?.info_light_txt?.text = info.getPlace()
                    view?.info_water_txt?.text = info.getWatering()
                    view?.info_ground_txt?.text = info.getSoil()
                    view?.plant_info_desc?.text = info.getDescription()
                    view?.plant_info_fert?.text = info.getFertilizer()
                }
            }

            override fun onCancelled(error: DatabaseError) { }

        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("infoId", firebaseUser.uid)
        pref?.apply()

    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("infoId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("infoId", firebaseUser.uid)
        pref?.apply()
    }
}