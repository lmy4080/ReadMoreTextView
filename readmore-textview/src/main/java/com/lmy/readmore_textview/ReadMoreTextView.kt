package com.lmy.readmore_textview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat

class ReadMoreTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var suffixMoreText: String? = null
    private var suffixLessText: String? = null
    private var suffixTextColor: Int? = null
    private var suffixTextStyle: Int? = null
    private var isUnderLine: Boolean? = null
    private var collapsedMaxLine: Int? = null
    private var isCollapsed: Boolean? = null

    private var mListener: ChangeListener? = null

    private var areTheSameText: Boolean = false

    var state: State = State.COLLAPSED
        private set(value) {
            field = value
            text = when (value) {
                State.EXPANDED -> expandText
                State.COLLAPSED -> collapseText
            }
            mListener?.onStateChanged(state)
        }

    private var originalText: String = ""
    private var collapseText: SpannableString = SpannableString("")
    private var expandText: SpannableString = SpannableString("")

    init {
        setupAttributes(context, attrs, defStyleAttr)
        setupOnClickListener()
    }

    override fun setOnClickListener(onClickListener: OnClickListener?) {
        throw UnsupportedOperationException("You can not use OnClickListener in ReadMoreTextView")
    }

    private fun setupOnClickListener() {
        super.setOnClickListener { toggle() }
    }

    fun setOnChangeListener(listener: ChangeListener) {
        mListener = listener
    }

    private fun toggle() {
        when (state) {
            State.EXPANDED -> collapse()
            State.COLLAPSED -> expand()
        }
    }

    private fun collapse() {
        if (areTheSameText || collapseText.isEmpty()) {
            return
        }
        state = State.COLLAPSED
    }

    private fun expand() {
        if (areTheSameText || expandText.isEmpty()) {
            return
        }
        state = State.EXPANDED
    }

    @SuppressLint("CustomViewStyleable")
    private fun setupAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ReadMoreTextView, defStyleAttr, 0)

        suffixMoreText =
            typedArray.getString(R.styleable.ReadMoreTextView_suffixMoreText) ?: ""
        suffixLessText =
            typedArray.getString(R.styleable.ReadMoreTextView_suffixLessText) ?: ""
        suffixTextColor =
            typedArray.getColor(R.styleable.ReadMoreTextView_textColor, ContextCompat.getColor(context, android.R.color.black))
        suffixTextStyle =
            typedArray.getInt(R.styleable.ReadMoreTextView_suffixTextStyle, Typeface.NORMAL)
        isUnderLine =
            typedArray.getBoolean(R.styleable.ReadMoreTextView_isUnderLine, false)
        isCollapsed =
            typedArray.getBoolean(R.styleable.ReadMoreTextView_isCollapsed, false)
        collapsedMaxLine =
            typedArray.getInt(R.styleable.ReadMoreTextView_collapsedMaxLine, Integer.MAX_VALUE)

        typedArray.recycle()
    }

    fun setupText(str: String) {
        originalText = str
        text = originalText
        post {
            setAreTheSameText()
            setCollapsedText(originalText)
            setExpandedText(originalText)
            maxLines = Integer.MAX_VALUE
            state = if(isCollapsed!!) State.COLLAPSED else State.EXPANDED
        }
    }

    private fun setAreTheSameText() {
        areTheSameText = lineCount <= collapsedMaxLine!!
    }

    private fun setCollapsedText(originalText: String) {
        collapseText = if(lineCount <= collapsedMaxLine!!) {
            SpannableString(originalText)
        } else {
            getSpannableString(getCollapsedString(originalText, suffixMoreText!!, collapsedMaxLine!!), suffixMoreText!!)
        }
    }

    private fun setExpandedText(originalText: String) {
        expandText = if(lineCount <= collapsedMaxLine!!) {
            SpannableString(originalText)
        } else {
            getSpannableString(getExpandedString(originalText, suffixLessText!!), suffixLessText!!)
        }
    }

    private fun getSpannableString(
        subText: String,
        suffixText: String
    ): SpannableString {

        val spannableString = SpannableString(subText)
        spannableString.setSpan(
            ForegroundColorSpan(suffixTextColor!!),
            spannableString.length - suffixText.length,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            StyleSpan(suffixTextStyle!!),
            spannableString.length - suffixText.length,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if(isUnderLine!!) {
            spannableString.setSpan(
                UnderlineSpan(),
                spannableString.length - suffixText.length,
                spannableString.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannableString
    }

    private fun getCollapsedString(
        subText: String,
        suffixText: String,
        maxLine: Int
    ): String {

        val lastLineStartIndex = layout.getLineVisibleEnd(maxLine - 2) + 1
        val lastLineEndIndex = layout.getLineVisibleEnd(maxLine - 1)
        val lastLineText = subText.substring(lastLineStartIndex, lastLineEndIndex)

        val bounds = Rect()
        paint.getTextBounds(lastLineText, 0, lastLineText.length, bounds)

        var adjustCutCount = -1
        do {
            adjustCutCount++
            val cutText = lastLineText.substring(0, lastLineText.length - adjustCutCount)
            val replacedText = cutText + suffixText
            paint.getTextBounds(replacedText, 0, replacedText.length, bounds)
            val replacedTextWidth = bounds.width()
        } while (replacedTextWidth > width)

        return subText.substring(0, lastLineEndIndex - 1 - adjustCutCount) + suffixText
    }

    private fun getExpandedString(
        subText: String,
        suffixText: String
    ): String {
        return subText + suffixText
    }

    enum class State {
        EXPANDED, COLLAPSED
    }

    interface ChangeListener {
        fun onStateChanged(state: State)
    }
}