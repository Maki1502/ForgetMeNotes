package edu.ib.forget_me_notes.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import edu.ib.forget_me_notes.R
import edu.ib.forget_me_notes.adapter.InfoAdapter
import edu.ib.forget_me_notes.model.Info
import kotlinx.android.synthetic.main.fragment_search.view.*
import java.util.ArrayList

class SearchFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var infoAdapter: InfoAdapter? = null
    private var mInfo: MutableList<Info>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.recyclerview_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        mInfo = ArrayList()
        infoAdapter = context?.let { InfoAdapter(it, mInfo as ArrayList<Info>, true) }
        recyclerView?.adapter = infoAdapter

        view.search_edit_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun afterTextChanged(s: Editable?) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (view.search_edit_text.text.toString() != "") {

                    recyclerView?.visibility = View.VISIBLE

                    retrieveInfo()
                    searchInfo(s.toString().lowercase())
                }

            }
        })

        return view
    }

    private fun searchInfo(input: String) {

        val query = FirebaseDatabase.getInstance().reference
            .child("Plants")
            .orderByChild("infoname")
            .startAt(input)
            .endAt(input + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                mInfo?.clear()
                for (snapshot in dataSnapshot.children) {
                    val info = snapshot.getValue(Info::class.java)
                    if (info != null) {
                        mInfo?.add(info)
                    }
                }

                infoAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) { }

        })

    }
    private fun retrieveInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Plants")
        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (view?.search_edit_text?.text.toString() == "") {
                    mInfo?.clear()

                    for (snapshot in dataSnapshot.children) {
                        val info = snapshot.getValue(Info::class.java)
                        if (info != null) {
                            mInfo?.add(info)
                        }
                    }

                    infoAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) { }

        })
    }
}