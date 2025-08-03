package com.yoni.nanitapp.presentation.birthday

import androidx.annotation.DrawableRes

data class ThemeResources(
    @DrawableRes val backgroundDrawable: Int,
    @DrawableRes val borderedIcon: Int,
    @DrawableRes val filledIcon: Int,
    @DrawableRes val smallIcon: Int,
    @DrawableRes val cameraIcon: Int
)