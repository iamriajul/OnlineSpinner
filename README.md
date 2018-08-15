# OnlineSpinnerView

help you to implement Spinner view with Online Data support + Searchable + callback and more.

This Library was Inspired by the [Select2](https://select2.org) web library.

Usage:
---

layout:

Searchable
---
```xml
<io.github.iamriajul.onlinespinner.OnlineSpinner
    android:id="@+id/example"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:hint="Example hint"
    app:title="Select a Example" />
```

Not Searchable
---
```xml
<io.github.iamriajul.onlinespinner.OnlineSpinner
    android:id="@+id/example2"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:hint="Example2 hint"
    app:isSearchable="false" />
```
code:

Your api url must return json, with id, second_column (second_column can be custom specified when using load method or you can leave null to detect automatically) field like this  
```json
[
    {"id":"0", "second_column":"Item 1"},
    {"id":"1", "second_column":"Item 2"},
    {"id":"2", "second_column":"Item 3"}
]
```
####**If you have have multiple column in a row like this**
 ```json
 [
     {"id":"0", "timestamp":"1534216256", "lang": "Arabic"},
     {"id":"1", "timestamp":"1534216256", "lang": "English"},
     {"id":"2", "timestamp":"1534216256", "lang": "Bengali"}
 ]
 ```
#####**Then you can specify which column to use in user interface, like this: **
```kotlin
example2.load(this, "http://example.com/language/all", 0, "lang") // custom specified column name and selected is Arabic
```

Your Activity or Fragment must `ActivityWithOnlineSpinner` interface.

Kotlin
---
Loading Data
---
```kotlin
showLoader()
// Change dataUrl with your real data url
example.load(this, "http://example.com/country/all", 5)
example2.load(this, "http://example.com/language/all", 0, "lang") // custom specified column name
// It will call hideLoader when all spinner successfully loaded with data.
```

Getting Selected Item Id (Example On Submit Handle)
---
```kotlin
submitBtn.setOnClickListener{
    val exampleId: Int = example.getSelectedItemId()
    val example2Id: Int = example2.getSelectedItemId("lang")
    // Here is your data, You can process this data as you want.
}
```
Implementing Callback
---
```kotlin
override var totalFieldsCount: Int = 2 // Total OnlineSpinner fields in this activity is using
override var fieldsLoaded: Int = 0 // it should be 0

override fun hideLoader() {
    // Hide loading animation, or anything you want.
}

override fun showLoader() {
    // Show loading animation, or anything you want.
}
```

Java
---
```java
showLoader()
OnlineSpinner example = (OnlineSpinner) findViewById(R.id.example);
OnlineSpinner example2 = (OnlineSpinner) findViewById(R.id.example2);
// Change dataUrl with your real data url
example.load(this, "http://example.com/country/all", 5)
example2.load(this, "http://example.com/language/all", 3, "lang") // custom specified column name
// It will call hideLoader when all spinner successfully loaded with data.
```

Fields
======================
Name | Description
--- | ---
`totalFieldsCount: Int` | Count of OnlineSpinner in your current Activity, this field is to help the library to detect if all Spinner loaded or not to trigger `hideLoader()`
`fieldsLoaded: Int` | This field should be init with 0, It will be incremented by the Library with each OnlineSpinner loaded.


Methods
======================
Name | Description
--- | ---
`showLoader()` | Call this method once before start calling `OnlineSpinner.load()`
`hideLoader()` | This method will be called by the Library when all the Spinner loading will complete successfully.


dependency
---
Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
	// ...
	maven { url 'https://jitpack.io' }
    }
}
```
add dependency：

```groovy
dependencies {
        implementation 'com.github.iamriajul:OnlineSpinner:1.0'
}
```

Thanks...