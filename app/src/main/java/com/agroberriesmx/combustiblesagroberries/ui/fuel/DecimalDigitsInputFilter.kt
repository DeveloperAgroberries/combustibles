package com.agroberriesmx.combustiblesagroberries.ui.fuel

import android.text.InputFilter
import android.text.Spanned

class DecimalDigitsInputFilter(private val digitsBeforeZero: Int, private val digitsAfterZero: Int) :
    InputFilter {
    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
        val newText = dest?.subSequence(0, dstart).toString() + source?.subSequence(start, end) + dest?.subSequence(dend, dest.length)

        if (newText.isEmpty()) return null

        val regex = "^\\d{0,$digitsBeforeZero}([.]\\d{0,$digitsAfterZero})?$".toRegex()
        return if (newText.matches(regex)) null else ""
    }
}