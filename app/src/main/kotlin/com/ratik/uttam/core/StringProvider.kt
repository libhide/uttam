package com.ratik.uttam.core

import androidx.annotation.StringRes

/**
 * StringProvider can be injected into ViewModels - or technically anywhere
 * where we need a string and there isn't a context to get it from.
 * Although it is a rare case (or at least it should be) this is a solution for such cases.
 */
interface StringProvider {
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg args: Any): String
}
