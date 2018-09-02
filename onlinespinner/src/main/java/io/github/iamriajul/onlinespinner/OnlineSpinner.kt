package io.github.iamriajul.onlinespinner

import android.content.Context
import android.support.v7.widget.AppCompatSpinner
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpGet
import org.json.JSONArray

private const val TAG = "OnlineSpinner"
class OnlineSpinner : LinearLayout {
    private var spinner: Spinner
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.orientation = LinearLayout.VERTICAL

        // setup label/hin
        val ta = context.obtainStyledAttributes(attrs, R.styleable.OnlineSpinner)
        val label = LayoutInflater.from(context).inflate(R.layout.label, null, false) as TextView
        val hintText = ta.getString(R.styleable.OnlineSpinner_hint)
        if (hintText.isNullOrEmpty()) {
            label.text = "Hint"
        } else {
            label.text = hintText
        }
        addView(label)

        // setup actual spinner
        val isSearchable = ta.getBoolean(R.styleable.OnlineSpinner_isSearchable, true)
        spinner = if (isSearchable) {
            SearchableSpinner(context, attrs)
        } else {
            AppCompatSpinner(context, attrs)
        }
        addView(spinner)

        ta.recycle()

    }

    fun getSelectedItemId(itemName: String? = null): Int {
        val data = this.tag as JSONArray
        for (i in 0..data.length().minus(1)) {
            val item =  data.getJSONObject(i)
            if (item.getString(itemName) == spinner.selectedItem) return item.getInt("id")
        }
        return -1
    }

    fun getSelectedItemText() = spinner.selectedItem.toString()

    fun load(activity: ActivityWithOnlineSpinner, request: Request, defaultValue: Int? = null, itemName: String? = null) {
        var itemNameLocal = itemName
        var defaultValueString: String? = null
        request.responseJson { _, response, result ->
            if (response.statusCode == 200) {
                val data = result.get().array()
                val items = arrayListOf<String>()
                for (i in 0..data.length().minus(1)) {
                    val item =  data.getJSONObject(i)
                    if (itemNameLocal == null) {
                        itemNameLocal = item.keys().asSequence().elementAt(1)
                    }
                    if (item.getInt("id") == defaultValue) {
                        defaultValueString = item.getString(itemNameLocal)
                    }
                    items.add(item.getString(itemNameLocal))
                }

                spinner.adapter = OnlineSpinnerArrayAdapter(this.context, android.R.layout.simple_list_item_1, items)
                if (defaultValue != null) {
                    val selectedIndex = items.indexOf(defaultValueString)
                    spinner.setSelection(selectedIndex)
                }
                this.tag = data
                activity.fieldsLoaded++
                if (activity.fieldsLoaded == activity.totalFieldsCount) activity.hideLoader()
            } else {
                if (BuildConfig.DEBUG) {
                    val error = "${resources.getResourceEntryName(this.id)} : ${response.statusCode} : ${response.responseMessage}";
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    Log.e(TAG, error)
                }
            }
        }
    }

    fun load(activity: ActivityWithOnlineSpinner, dataUrl: String, defaultValue: Int? = null, itemName: String? = null, vararg headers: Pair<String, Any>?) {
        load(activity, dataUrl.httpGet().header(*headers), defaultValue, itemName)
    }

    fun load(activity: ActivityWithOnlineSpinner, dataUrl: String, defaultValue: Int? = null, itemName: String? = null) {
        load(activity, dataUrl.httpGet(), defaultValue, itemName)
    }


    fun getSelectedItemId(): Int {
        val data = this.tag as JSONArray
        val item = data.getJSONObject(0)
        val itemName = item.keys().asSequence().elementAt(1)
        return this.getSelectedItemId(itemName)
    }

}