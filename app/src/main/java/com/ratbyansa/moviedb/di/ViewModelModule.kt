package com.ratbyansa.moviedb.di

import com.ratbyansa.moviedb.ui.viewmodel.GenreViewModel
import com.ratbyansa.moviedb.ui.viewmodel.MovieViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { GenreViewModel(get()) }
    viewModel { MovieViewModel(get()) }
}