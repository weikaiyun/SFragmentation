package com.common.weikaiyun.fragmentargument

import androidx.fragment.app.Fragment
import kotlin.properties.ReadWriteProperty

fun <T : Any> argument(): ReadWriteProperty<Fragment, T> =
    FragmentArgumentDelegate()
fun <T : Any> argumentNullable(): ReadWriteProperty<Fragment, T?> =
    FragmentNullableArgumentDelegate()