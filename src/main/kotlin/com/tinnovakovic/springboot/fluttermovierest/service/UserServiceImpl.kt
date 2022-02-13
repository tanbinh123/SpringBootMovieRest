package com.tinnovakovic.springboot.fluttermovierest.service

import com.tinnovakovic.springboot.fluttermovierest.repo.MovieRepo
import com.tinnovakovic.springboot.fluttermovierest.repo.UserRepo
import com.tinnovakovic.springboot.fluttermovierest.model.AppUser
import com.tinnovakovic.springboot.fluttermovierest.model.AppUserDetail
import com.tinnovakovic.springboot.fluttermovierest.model.Movie
import com.tinnovakovic.springboot.fluttermovierest.repo.AppUserDetailRepo
import com.tinnovakovic.springboot.fluttermovierest.rest_models.RestAppUser
import com.tinnovakovic.springboot.fluttermovierest.rest_models.RestMovie
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException

@Service
class UserServiceImpl(
    private val userRepo: UserRepo,
    private val userDetailRepo: AppUserDetailRepo,
    val movieRepo: MovieRepo
) : UserService {

    // to return movieIds you need to query the app_user_movie table, but we only want to do this in AppUserDetail
    override fun getUsers(): List<RestAppUser> {
        return userRepo.findAll().map {
            RestAppUser(id = it.id, username = it.username, email = it.email)
        }
    }

    // to return movieIds you need to query the app_user_movie table, but we only want to do this in AppUserDetail
    override fun getUser(id: Int): RestAppUser {
        userRepo.findById(id).let {
            return if (it.isPresent) {
                RestAppUser(id = it.get().id, username = it.get().username, email = it.get().email)
            } else {
                throw NoSuchElementException("Could not find a user with an id of $id.")
            }
        }
    }

    override fun createUser(restAppUser: RestAppUser): RestAppUser {
        return if (userRepo.findById(restAppUser.id).isEmpty) { //this check has to be done by email or username,
            // because the user will not provide a SQL id when creating an account
            userRepo.save(
                AppUser(
                    id = -1,
                    username = restAppUser.username,
                    email = restAppUser.email,
                    appUserDetail = AppUserDetail(
                        id = -1,
                        username = restAppUser.username,
                        email = restAppUser.email,
                        favMovies = emptySet()
                    )
                )
            )
            restAppUser
        } else {
            throw IllegalArgumentException("A user with the 'email' ${restAppUser.email} already exists")
        }
    }

    // prevent changing email and username. Unneeded feature
//    override fun updateUser(restAppUser: RestAppUser): RestAppUser {
//        userRepo.findById(restAppUser.id).let {
//            return if (it.isPresent) {
//                userRepo.save(
//                    AppUser(
//                        id = restAppUser.id,
//                        username = restAppUser.username,
//                        email = restAppUser.email,
//                        movies = it.get().movies
//                    )
//                )
//
//                restAppUser
//            } else {
//                throw NoSuchElementException("Could not find a user with an 'email' of ${restAppUser.email}.")
//            }
//        }
//    }

    override fun deleteUser(id: Int) {
        if (userRepo.findById(id).isPresent) {
            userRepo.deleteById(id)
        } else {
            throw NoSuchElementException("Could not find a user with an 'id' of ${id}.")
        }
    }

    override fun saveMovie(id: Int, restMovie: RestMovie): RestMovie {

        // get the SqlMovie for the Movie we want to save to User
        val sqlMovie: Movie = movieRepo.findById(restMovie.id).get()

        // get the sqlUser
        userDetailRepo.findById(id).let {
            return if (it.isPresent) {
                // get the movies currently in the user
                val userSqlMovies: MutableSet<Movie> = it.get().favMovies as MutableSet<Movie>
                userSqlMovies.add(sqlMovie)
                userDetailRepo.save(
                    AppUserDetail(
                        id = it.get().id,
                        username = it.get().username,
                        email = it.get().email,
                        favMovies = userSqlMovies
                    )
                )

                restMovie
            } else {
                throw NoSuchElementException("Could not find a user with an 'id' of ${id}.")

            }
        }
    }
}