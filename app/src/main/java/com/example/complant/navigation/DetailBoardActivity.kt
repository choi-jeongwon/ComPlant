package com.example.complant.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.complant.R
import kotlinx.android.synthetic.main.activity_detail_board.*

class DetailBoardActivity : AppCompatActivity() {

    var explainTitle: String? = null
    var explain: String? = null
    var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_board)


        explainTitle = intent.getStringExtra("explainTitle")
        explain = intent.getStringExtra("explain")
        imageUrl = intent.getStringExtra("imageUrl")

        boarditem_explain_textview_title.setText(explainTitle)
        boarditem_explain_textview.setText(explain)

        Glide.with(this).load(imageUrl).into(boarditem_imageview_content)
    }
}