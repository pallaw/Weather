/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pallaw.weatherapp.feature.weather.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.pallaw.weatherapp.core.data.WeatherRepository
import com.pallaw.weatherapp.feature.weather.ui.WeatherUiState.Error
import com.pallaw.weatherapp.feature.weather.ui.WeatherUiState.Loading
import com.pallaw.weatherapp.feature.weather.ui.WeatherUiState.Success
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    val uiState: StateFlow<WeatherUiState> = weatherRepository
        .weathers.map<List<String>, WeatherUiState> { Success(data = it) }
        .catch { emit(Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    fun addWeather(name: String) {
        viewModelScope.launch {
            weatherRepository.add(name)
        }
    }
}

sealed interface WeatherUiState {
    object Loading : WeatherUiState
    data class Error(val throwable: Throwable) : WeatherUiState
    data class Success(val data: List<String>) : WeatherUiState
}
