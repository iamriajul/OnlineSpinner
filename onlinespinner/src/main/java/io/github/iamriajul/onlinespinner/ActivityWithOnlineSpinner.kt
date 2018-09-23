package io.github.iamriajul.onlinespinner

interface ActivityWithOnlineSpinner {
    var totalFieldsCount: Int // Total Online fields
    var fieldsLoaded: Int // Online fields loaded

    fun hideLoader()
    fun showLoader()
    fun onOnlineSpinnerCompleted() {}
}