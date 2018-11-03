package io.github.iamriajul.onlinespinner

import android.content.Context
import android.support.v7.widget.AppCompatSpinner
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpGet
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

private const val TAG = "OnlineSpinner"
class OnlineSpinner : LinearLayout {
    private var spinner: Spinner
    private var isOptional: Boolean = false
    private var select: String = "Select"
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

        // Set Select Text
        val selectText = ta.getString(R.styleable.OnlineSpinner_selectText)
        select = if (selectText.isNullOrEmpty()) "Select" else selectText

        // setup actual spinner
        val isSearchable = ta.getBoolean(R.styleable.OnlineSpinner_isSearchable, true)
        spinner = if (isSearchable) {
            SearchableSpinner(context)
        } else {
            AppCompatSpinner(context)
        }
        addView(spinner)

        val layout_height = attrs?.getAttributeIntValue("http://schemas.android.com/apk/res/android", "layout_height", LayoutParams.WRAP_CONTENT)
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, layout_height ?: LayoutParams.WRAP_CONTENT)
        spinner.layoutParams = layoutParams

        isOptional = ta.getBoolean(R.styleable.OnlineSpinner_isOptional, false)

        ta.recycle()

    }

    fun getSelectedItemId(itemName: String): Int {
        val data = this.tag as JSONArray
        for (i in 0..data.length().minus(1)) {
            val item =  data.getJSONObject(i)
            if (item.getString(itemName) == spinner.selectedItem) return item.getInt("id")
        }
        return -1
    }

    fun getSelectedItem(itemName: String): JSONObject? {
        val data = this.tag as JSONArray
        for (i in 0..data.length().minus(1)) {
            val item =  data.getJSONObject(i)
            if (item.getString(itemName) == spinner.selectedItem) return item
        }
        return null
    }

    fun getSelectedItemText() = spinner.selectedItem.toString()

    fun load(activity: ActivityWithOnlineSpinner, data: JSONArray, defaultValue: Int? = null, itemName: String? = null) {
        var itemNameLocal = itemName
        var defaultValueString: String? = null

        var dataFinal = data
        // Setting Optional Item
        if (isOptional) {
            val dataWithSelectItem = JSONArray()
            dataWithSelectItem.put(JSONObject("{id:0, name:'$select'}"))
            for (i in 0 until data.length()) {
                dataWithSelectItem.put(data.getJSONObject(i))
            }
            dataFinal = dataWithSelectItem
        }

        val items = arrayListOf<String>()
        for (i in 0 until dataFinal.length()) {
            val item =  dataFinal.getJSONObject(i)
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
        this.tag = dataFinal
        activity.fieldsLoaded++
        if (activity.fieldsLoaded == activity.totalFieldsCount) {
            activity.hideLoader()
            activity.onOnlineSpinnerCompleted()
        }
    }

    fun load(activity: ActivityWithOnlineSpinner, request: Request, defaultValue: Int? = null, itemName: String? = null, callback: (data: JSONArray) -> Unit = {}) {
        var itemNameLocal = itemName
        var defaultValueString: String? = null
        request.responseJson { _, response, result ->
            if (response.statusCode == 200) {
                val data = result.get().array()
                load(activity, data, defaultValue, itemName)
                callback(data)
            } else {
                if (BuildConfig.DEBUG) {
                    val error = "${resources.getResourceEntryName(this.id)} : ${response.statusCode} : ${response.responseMessage}";
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                    Log.e(TAG, error)
                }
            }
        }
    }

    fun load(activity: ActivityWithOnlineSpinner, dataUrl: String, defaultValue: Int? = null, itemName: String? = null, vararg headers: Pair<String, Any>?, callback: (data: JSONArray) -> Unit = {}) {
        load(activity, dataUrl.httpGet().header(*headers), defaultValue, itemName)
    }

    fun load(activity: ActivityWithOnlineSpinner, dataUrl: String, defaultValue: Int? = null, itemName: String? = null, callback: (data: JSONArray) -> Unit = {}) {
        load(activity, dataUrl.httpGet(), defaultValue, itemName)
    }

    fun setOnItemSelectedListener(listener: AdapterView.OnItemSelectedListener) { spinner.onItemSelectedListener = listener }

    fun setOnItemSelectedListener(itemName: String, onItemSelected: (id: Int, item: JSONObject?) -> Unit) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onItemSelected(getSelectedItemId(itemName), getSelectedItem(itemName))
            }
        }
    }

    fun setOnItemSelectedListener(onItemSelected: (id: Int, item: JSONObject?) -> Unit) {
        setOnItemSelectedListener(getItemName(), fun(id: Int, item: JSONObject?) {
            onItemSelected(id, item)
        })
    }

    /**
     * @throws Exception when you try to get data before loading data.
     */
    private fun getItemName(): String {
        if (this.tag !is JSONArray) throw Exception("You must load OnlineSpinner before accessing value, or You can implement the `onOnlineSpinnerCompleted` method in your activity to be safe.")
        val data = this.tag as JSONArray
        val item = data.getJSONObject(0)
        return item.keys().asSequence().elementAt(1)
    }

    fun getSelectedItemId(): Int {
        return this.getSelectedItemId(getItemName())
    }

    fun getSelectedItem(): JSONObject? {
        return this.getSelectedItem(getItemName())
    }

}