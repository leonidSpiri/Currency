package spiridonov.currency.presentation

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter

@SuppressLint("DiscouragedApi")
@BindingAdapter("textCurrLogo")
fun setTextCurrLogo(textView: TextView, code:String?){
    if (code != null) {
        val resID =
            textView.context.resources?.getIdentifier(code, "string", textView.context.packageName)
                ?: 0
        if (resID != 0) {
            textView.text = textView.context.getString(resID)
        } else {
            textView.text = code
        }
    }
}