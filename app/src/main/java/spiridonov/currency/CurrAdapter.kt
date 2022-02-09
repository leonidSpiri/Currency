package spiridonov.currency

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.each_currency.view.*


class CurrAdapter(private var currList: ArrayList<CurrCard>) :
    RecyclerView.Adapter<CurrAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtName: TextView = itemView.txtName
        var txtCode: TextView = itemView.txtID
        var txtValue: TextView = itemView.txtValue
        var txtPrevious: TextView = itemView.txtPrevious
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.each_currency, parent, false)
    )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtName.text = currList[position].name
        holder.txtCode.text = currList[position].code
        holder.txtValue.text = currList[position].value
        holder.txtPrevious.text = currList[position].previous
    }

    override fun getItemCount(): Int = currList.size
}