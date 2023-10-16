package com.dh.myapplication.ui

//for easier navigation
sealed class AppScreens(val route: String) {


    object DashboardTwo : AppScreens ("DashboardTwo")
    object Permission : AppScreens ("Permission")
    object BarCode : AppScreens ("BarCode")


}

