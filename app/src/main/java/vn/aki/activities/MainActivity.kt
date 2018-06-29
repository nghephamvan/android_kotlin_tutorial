package vn.aki.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import vn.aki.myapplication.R
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var app: Context

    @Inject
    lateinit var test: String

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_content.text = test
                //baseContext.getText(R.string.hello_aki)

    }
}
