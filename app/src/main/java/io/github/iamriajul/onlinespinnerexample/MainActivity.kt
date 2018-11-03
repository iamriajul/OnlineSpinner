package io.github.iamriajul.onlinespinnerexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.github.iamriajul.onlinespinner.ActivityWithOnlineSpinner
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity(), ActivityWithOnlineSpinner {
    override var totalFieldsCount: Int = 3 // Total OnlineSpinner fields in this activity is using
    override var fieldsLoaded: Int = 0 // it should be 0

    override fun hideLoader() {
        // Hide loading animation, or anything you want.
    }

    override fun showLoader() {
        // Show loading animation, or anything you want.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Your api url must return json, with id, second_column (second_column can be custom specified when using load method or you can leave null to detect automatically) field like this [{"id":"0", "second_column":"Item 1"}, {"id":"1", "second_column":"Item 2"}]

        // Change dataUrl with your real data url
        example.load(this, "http://example.com/country/all", 5)
        example2.load(this, "http://example.com/language/all", 3, "lang")
        val dummyJsonArray = JSONArray("[{id:1, name: 'Item 1'}, {id:2, name: 'Item 2'}, {id:3, name: 'Item 3'}]")
        example3.load(this, dummyJsonArray)
        example4.load(this, dummyJsonArray)
    }
}
