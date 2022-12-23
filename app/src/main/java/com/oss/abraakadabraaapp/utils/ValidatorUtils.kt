package com.oss.abraakadabraaapp.utils

import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import java.util.regex.Matcher
import java.util.regex.Pattern

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = textPaint.linkColor
                textPaint.isUnderlineText = true
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance()
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun EditText.filterEmoji() {
    this.filters = arrayOf<InputFilter>(EmojiExcludeFilter())
}

fun EditText.filterEmojiWithCharacterLimit(limit: Int) {
    filters = arrayOf(EmojiExcludeFilter(), InputFilter.LengthFilter(limit))
}


private class EmojiExcludeFilter : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        for (i in start until end) {
            val type = Character.getType(source[i])
            if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt()) {
                return ""
            }
        }
        return null
    }
}


val numberRegex = Regex("^[0-9]")

fun EditText.filterNumber() {
    filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
        source.filter { numberRegex.matches(it.toString()) }

    }, InputFilter.LengthFilter(10))
}

fun EditText.filterByDataType(filterType: Int) {
    this.filters = arrayOf<InputFilter>(InputFilterAlphanumeric(filterType))
}

/**
 * Returns the filter of the editText'es CharSequence value when [filterType] is:
 * 1 -> letters; 2 -> letters and digits; 3 -> digits;
 * 4 -> digits and dots
 */
class InputFilterAlphanumeric(private val filterType: Int) : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence {
        (source as? SpannableStringBuilder)?.let { sourceAsSpannableBuilder ->
            for (i in (end - 1) downTo start) {
                val currentChar = source[i]
                when (filterType) {
                    1 -> {
                        if (!currentChar.isLetter() && !currentChar.isWhitespace()) {
                            sourceAsSpannableBuilder.delete(i, i + 1)
                        }
                    }
                    2 -> {
                        if (!currentChar.isLetterOrDigit() && !currentChar.isWhitespace()) {
                            sourceAsSpannableBuilder.delete(i, i + 1)
                        }
                    }
                    3 -> {
                        if (!currentChar.isDigit()) {
                            sourceAsSpannableBuilder.delete(i, i + 1)
                        }
                    }
                    4 -> {
                        if (!currentChar.isDigit() || !currentChar.toString().contains(".")) {
                            sourceAsSpannableBuilder.delete(i, i + 1)
                        }
                    }
                }
            }
            return source
        } ?: run {
            val filteredStringBuilder = StringBuilder()
            for (i in start until end) {
                val currentChar = source?.get(i)
                when (filterType) {
                    1 -> {
                        if (currentChar?.isLetter()!! || currentChar.isWhitespace()) {
                            filteredStringBuilder.append(currentChar)
                        }
                    }
                    2 -> {
                        if (currentChar?.isLetterOrDigit()!! || currentChar.isWhitespace()) {
                            filteredStringBuilder.append(currentChar)
                        }
                    }
                    3 -> {
                        if (currentChar?.isDigit()!!) {
                            filteredStringBuilder.append(currentChar)
                        }
                    }
                    4 -> {
                        if (currentChar?.isDigit()!! || currentChar.toString().contains(".")) {
                            filteredStringBuilder.append(currentChar)
                        }
                    }
                }
            }
            return filteredStringBuilder
        }
    }
}

object ValidatorUtils {

    fun isValidPassword(password: String): Boolean {
        password.let {
            val passwordPattern =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\[\\]\\\\~`\"|•√π÷×¶∆}{=°^¢<>,.&$¥€%©®™✓@#₹_+()/*':;!?])(?=\\S+$).{6,}$"
            val passwordMatcher = Regex(passwordPattern)
            return passwordMatcher.find(password) != null
        }
    }

    fun isMobileValidate(mobileNumber: String): Boolean {
        mobileNumber.let {
            val phonePattern: Pattern = Pattern.compile("^[0-9]{10}")
            val phoneMs: Matcher = phonePattern.matcher(mobileNumber)
            return phoneMs.matches()
        }
    }

}