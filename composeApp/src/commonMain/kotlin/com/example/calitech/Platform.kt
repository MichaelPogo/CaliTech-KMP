package com.example.calitech

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform