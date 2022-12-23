package com.oss.abraakadabraaapp.utils.customView

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.oss.abraakadabraaapp.R

class CustomTextView(context: Context?, attrs: AttributeSet?) :
    AppCompatTextView(context!!, attrs) {
    override fun setTypeface(tf: Typeface?, style: Int) {
        val tfValue = when (style) {
            1 ->
                ResourcesCompat.getFont(context, R.font.montserrat_regular);
            else ->
                ResourcesCompat.getFont(context, R.font.montserrat_bold);
        }
        super.setTypeface(tfValue, 0)
    }
}