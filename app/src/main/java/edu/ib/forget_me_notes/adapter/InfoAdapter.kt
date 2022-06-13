package edu.ib.forget_me_notes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import edu.ib.forget_me_notes.R
import edu.ib.forget_me_notes.fragments.InfoFragment
import edu.ib.forget_me_notes.model.Info

class InfoAdapter(
    private var mContext: Context, private var mInfo: List<Info>,
    private var isFragment: Boolean = false
) : RecyclerView.Adapter<InfoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.info_item_layout, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val info = mInfo[position]
        holder.infoName.text = info.getInfoname()
        Picasso.get().load(info.getImage()).placeholder(R.drawable.leaf)
            .into(holder.infoImage)


        holder.itemView.setOnClickListener{
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("infoId", info.getIid())
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, InfoFragment()).commit()
        }
    }

    override fun getItemCount(): Int {
        return mInfo.size
    }

    class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        var infoImage: CircleImageView = itemView.findViewById(R.id.info_image_search)
        var infoName: TextView = itemView.findViewById(R.id.info_name_search)

    }
}