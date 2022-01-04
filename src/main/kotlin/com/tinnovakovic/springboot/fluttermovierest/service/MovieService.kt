package com.tinnovakovic.springboot.fluttermovierest.service

import com.tinnovakovic.springboot.fluttermovierest.datasource.MovieDataSource
import com.tinnovakovic.springboot.fluttermovierest.model.Movie
import org.springframework.stereotype.Service

@Service
class MovieService(private val dataSource: MovieDataSource) {

    fun getMovies(): List<Movie> = dataSource.retrieveMovies()
    fun getMovie(id: Int): Movie = dataSource.retrieveMovie(id)
    fun createMovie(movie: Movie): Movie = dataSource.createMovie(movie)
    fun updateMovie(movie: Movie): Movie = dataSource.updateMovie(movie)
    fun deleteMovie(id: Int): Unit = dataSource.deleteMovie(id)
}