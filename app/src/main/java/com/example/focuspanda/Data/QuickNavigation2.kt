package com.example.focuspanda.Data

import com.example.focuspanda.Model.QuickNavigate1
import com.example.focuspanda.R

class QuickNavigationIterm {
    fun loadQuickNavigationIterm(): List<QuickNavigate1> {
        return listOf(
            QuickNavigate1(
                imageResId = R.drawable.pomodoro,
                feature = "PomodoroTimer",
                details = "Boost productivity with Pomodoro sessions."
            ),
            QuickNavigate1(
                imageResId = R.drawable.flashcards,
                feature = "Flashcards",
                details = "Revise key concepts quickly."
            ),
            QuickNavigate1(
                imageResId = R.drawable.mind,
                feature = "MindMaps",
                details = "Retrace your studies effectively."
            ),
            QuickNavigate1(
                imageResId = R.drawable.focus,
                feature = "FocusMode",
                details = "Minimize distractions for deep work."
            )
        )
    }
}