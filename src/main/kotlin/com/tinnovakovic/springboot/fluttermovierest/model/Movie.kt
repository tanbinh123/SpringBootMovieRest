package com.tinnovakovic.springboot.fluttermovierest.model

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator
import javax.persistence.*


@Entity
@Table(
    name = "movie",
    uniqueConstraints = [UniqueConstraint(name = "unique_movie_constraints", columnNames = ["mdb_id"])]
)
data class Movie(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    val id: Int,
    @Column(name = "mdb_id", nullable = false, columnDefinition = "TEXT")
    val mDbId: String,
    @Column(name = "poster_path", nullable = false, columnDefinition = "TEXT")
    val posterPath: String,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "movie_detail_id", referencedColumnName = "id")
    var movieDetail: MovieDetail,
    @ManyToMany(mappedBy = "favMovies")
    val appUsers: Set<AppUser>
)