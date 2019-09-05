package ru.skillbranch.devintensive.extensions

import ru.skillbranch.devintensive.models.data.User

fun User.fullName() = "$firstName $lastName"
