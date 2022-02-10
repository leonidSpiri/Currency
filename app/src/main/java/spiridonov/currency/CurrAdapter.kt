package spiridonov.currency

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.each_currency.view.*


class CurrAdapter(private var currList: ArrayList<CurrCard>, private val context: Context) :
    RecyclerView.Adapter<CurrAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtName: TextView = itemView.txtName
        var txtCode: TextView = itemView.txtID
        var txtValue: TextView = itemView.txtValue
        var txtPrevious: TextView = itemView.txtPrevious
        var imgStar: ImageView = itemView.imgStar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.each_currency, parent, false)
    )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtName.text = currList[position].name
        holder.txtCode.text = currList[position].code
        holder.txtValue.text = currList[position].value
        holder.txtPrevious.text = currList[position].previous
        holder.imgStar.setImageResource(R.drawable.star_no_rate)
        val msp = context.getSharedPreferences("AppMemory", Context.MODE_PRIVATE)
        var favouriteList = arrayListOf<String>()
        if (msp.contains(KEY_FAVOURITE) && msp.getString(KEY_FAVOURITE, " , ") != "")
            favouriteList = msp.getString(KEY_FAVOURITE, "")?.split(",") as ArrayList<String>
        if (favouriteList.contains(currList[position].code)){
            holder.imgStar.setImageResource(R.drawable.star_rate)
        }

        holder.imgStar.setOnClickListener {
            if (msp.contains(KEY_FAVOURITE) && msp.getString(KEY_FAVOURITE, " , ") != "")
                favouriteList = msp.getString(KEY_FAVOURITE, "")?.split(",") as ArrayList<String>

            if (favouriteList.contains(currList[position].code)){
                favouriteList.remove(currList[position].code)
                holder.imgStar.setImageResource(R.drawable.star_no_rate)
            }
            else {
                favouriteList.add(currList[position].code)
                holder.imgStar.setImageResource(R.drawable.star_rate)
            }
            var buff = ""
            favouriteList.forEach {
                if (it != "") buff += "$it,"
            }
            Log.d("onBindViewHolder", "value $buff")
            val editor = msp.edit()
            editor.putString(KEY_FAVOURITE, buff)
            editor.apply()
        }
    }

    override fun getItemCount(): Int = currList.size

    companion object {
        const val KEY_FAVOURITE = "favourite"
    }
}