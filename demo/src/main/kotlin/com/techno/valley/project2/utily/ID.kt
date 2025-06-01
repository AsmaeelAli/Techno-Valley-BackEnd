package com.techno.valley.project2.utily

import com.techno.valley.project2.feature.users.model.dto.EmailDto

typealias ID = Long

fun EmailDto.cleaned(): String = this.email.trim().lowercase()