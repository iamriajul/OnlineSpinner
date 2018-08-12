# OnlineSpinnerView

help you to implement Spinner view with Online Data support + Searchable + callback and more.

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

Your api url must return json, with id, second_column (second_column can be custom specified when using load method or you can leave null to detect automatically) field like this [{"id":"0", "second_column":"Item 1"}, {"id":"1", "second_column":"Item 2"}]

Your Activity or Fragment must `ActivityWithOnlineSpinner` interface.

Kotlin
---
```kotlin
// Change dataUrl with your real data url
example.load(this, "http://example.com/country/all", 5)
example2.load(this, "http://example.com/language/all", 3, "lang") // custom specified column name
```

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
OnlineSpinner example = (OnlineSpinner) findViewById(R.id.example);
OnlineSpinner example2 = (OnlineSpinner) findViewById(R.id.example2);
// Change dataUrl with your real data url
example.load(this, "http://example.com/country/all", 5)
example2.load(this, "http://example.com/language/all", 3, "lang") // custom specified column name
```

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