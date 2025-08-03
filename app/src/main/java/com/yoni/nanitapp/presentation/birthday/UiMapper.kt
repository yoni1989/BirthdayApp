package com.yoni.nanitapp.presentation.birthday

import com.yoni.nanitapp.R
import com.yoni.nanitapp.domain.AgeInfo
import com.yoni.nanitapp.domain.AgeUnit
import com.yoni.nanitapp.domain.BirthdayTheme

fun BirthdayTheme.toThemeResources(): ThemeResources {
    return when (this) {
        BirthdayTheme.PELICAN -> ThemeResources(
            backgroundDrawable = R.drawable.bg_android_pelican,
            borderedIcon = R.drawable.ic_baby_face_circle_blue_bordered,
            filledIcon = R.drawable.ic_baby_face_circle_blue_filled,
            smallIcon = R.drawable.ic_baby_face_blue_small,
            cameraIcon = R.drawable.ic_camera_blue
        )

        BirthdayTheme.FOX -> ThemeResources(
            backgroundDrawable = R.drawable.bg_fox,
            borderedIcon = R.drawable.ic_baby_face_circle_green_bordered,
            filledIcon = R.drawable.ic_baby_face_circle_green_filled,
            smallIcon = R.drawable.ic_baby_face_green_small,
            cameraIcon = R.drawable.ic_camera_green
        )

        BirthdayTheme.ELEPHANT -> ThemeResources(
            backgroundDrawable = R.drawable.bg_android_elephant,
            borderedIcon = R.drawable.ic_baby_face_circle_yellow_bordered,
            filledIcon = R.drawable.ic_baby_face_circle_yellow_filled,
            smallIcon = R.drawable.ic_baby_face_yellow_small,
            cameraIcon = R.drawable.ic_camera_yellow
        )
    }
}

fun Int.toNumberIconResource(): Int {
    return when (this) {
        0 -> R.drawable.ic_number_00
        1 -> R.drawable.ic_number_01
        2 -> R.drawable.ic_number_02
        3 -> R.drawable.ic_number_03
        4 -> R.drawable.ic_number_04
        5 -> R.drawable.ic_number_05
        6 -> R.drawable.ic_number_06
        7 -> R.drawable.ic_number_07
        8 -> R.drawable.ic_number_08
        9 -> R.drawable.ic_number_09
        10 -> R.drawable.ic_number_10
        11 -> R.drawable.ic_number_11
        12 -> R.drawable.ic_number_12
        else -> R.drawable.ic_number_00
    }
}

fun AgeInfo.toDisplayText(): String {
    return when (unit) {
        AgeUnit.MONTHS -> if (value == 1) "MONTH OLD!" else "MONTHS OLD!"
        AgeUnit.YEARS -> if (value == 1) "YEAR OLD!" else "YEARS OLD!"
    }
}