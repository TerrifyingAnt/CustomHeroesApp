package jg.coursework.customheroesapp.data.api

class ApiException(val code: Int, override val message: String?) : Exception(message)
