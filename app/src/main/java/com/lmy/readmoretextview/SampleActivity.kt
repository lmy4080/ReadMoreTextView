package com.lmy.readmoretextview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lmy.readmore_textview.ReadMoreTextView

class SampleActivity : AppCompatActivity() {

    private val tvSample1: ReadMoreTextView by lazy { findViewById(R.id.tv_sample1) }
    private val tvSample2: ReadMoreTextView by lazy { findViewById(R.id.tv_sample2) }
    private val longText: String = "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy. Various versions have evolved over the years, sometimes by accident, sometimes on purpose (injected humour and the like)"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        tvSample1.setupText(longText)
        tvSample1.setOnChangeListener(object: ReadMoreTextView.ChangeListener {
            override fun onStateChanged(state: ReadMoreTextView.State) {
                when(state) {
                    ReadMoreTextView.State.COLLAPSED -> {
                        // Todo
                    }
                    ReadMoreTextView.State.EXPANDED -> {
                        // Todo
                    }
                }
            }
        })

        tvSample2.setupText(longText)
        tvSample1.setOnChangeListener(object: ReadMoreTextView.ChangeListener {
            override fun onStateChanged(state: ReadMoreTextView.State) {
                when(state) {
                    ReadMoreTextView.State.COLLAPSED -> {
                        // Todo
                    }
                    ReadMoreTextView.State.EXPANDED -> {
                        // Todo
                    }
                }
            }
        })
    }
}