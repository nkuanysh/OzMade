package com.example.ozmade.main.seller.products.edit

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ozmade.main.seller.data.SellerRepository
import com.example.ozmade.main.seller.products.add.AddProductState
import com.example.ozmade.main.seller.products.add.SellerCategory
import com.example.ozmade.network.model.ProductRequest
import com.example.ozmade.utils.ImageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerEditProductViewModel @Inject constructor(
    private val repo: SellerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddProductState())
    val state: StateFlow<AddProductState> = _state

    private var loadedId: Int? = null

    fun load(productId: Int) {
        if (loadedId == productId) return
        loadedId = productId

        viewModelScope.launch {
            _state.value = AddProductState(loading = true)

            runCatching {
                val dto = repo.getProductDetails(productId)

                _state.update { st ->
                    val rawPhotos = dto.images?.takeIf { it.isNotEmpty() } 
                        ?: listOfNotNull(dto.imageUrl)

                    val formattedPhotos = rawPhotos.map { 
                        Uri.parse(ImageUtils.formatImageUrl(it)) 
                    }

                    val catTitles = dto.categories ?: listOfNotNull(dto.type)
                    
                    // Format price to remove .0 if it's an integer
                    val formattedPrice = if (dto.price != null) {
                        if (dto.price % 1 == 0.0) dto.price.toInt().toString() 
                        else dto.price.toString()
                    } else ""

                    st.copy(
                        loading = false,
                        error = null,
                        success = false,
                        title = dto.title,
                        description = dto.description,
                        priceText = formattedPrice,
                        photos = formattedPhotos,
                        selectedCategories = SellerCategory.entries
                            .filter { cat -> catTitles.any { it.equals(cat.title, ignoreCase = true) || it.equals(cat.backendValue, ignoreCase = true) } }
                            .toSet(),
                        weightText = dto.weight ?: "",
                        heightText = dto.heightCm ?: "",
                        widthText = dto.widthCm ?: "",
                        depthText = dto.depthCm ?: "",
                        composition = dto.composition ?: "",
                        youtubeUrl = dto.youtubeUrl ?: ""
                    )
                }
            }.onFailure { e ->
                _state.update { it.copy(loading = false, error = e.message ?: "Ошибка загрузки") }
            }
        }
    }

    fun onPickPhotos(new: List<Uri>) {
        _state.update { st ->
            val merged = (st.photos + new).distinct().take(10)
            st.copy(photos = merged)
        }
    }

    fun removePhoto(uri: Uri) {
        _state.update { it.copy(photos = it.photos.filterNot { p -> p == uri }) }
    }

    fun movePhoto(fromIndex: Int, toIndex: Int) {
        _state.update { st ->
            val list = st.photos.toMutableList()
            if (fromIndex !in list.indices || toIndex !in list.indices) return@update st
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            st.copy(photos = list)
        }
    }

    fun setTitle(v: String) = _state.update { it.copy(title = v) }
    fun setPriceText(v: String) = _state.update { it.copy(priceText = v) }
    fun setDescription(v: String) = _state.update { it.copy(description = v) }
    fun setYoutube(v: String) = _state.update { it.copy(youtubeUrl = v) }

    fun setWeight(v: String) = _state.update { it.copy(weightText = v) }
    fun setHeight(v: String) = _state.update { it.copy(heightText = v) }
    fun setWidth(v: String) = _state.update { it.copy(widthText = v) }
    fun setDepth(v: String) = _state.update { it.copy(depthText = v) }
    fun setComposition(v: String) = _state.update { it.copy(composition = v) }

    fun toggleCategory(c: SellerCategory) {
        _state.update { st ->
            val set = st.selectedCategories.toMutableSet()
            if (!set.add(c)) set.remove(c)
            st.copy(selectedCategories = set)
        }
    }

    fun dismissError() = _state.update { it.copy(error = null) }
    fun consumeSuccess() = _state.update { it.copy(success = false) }

    fun save(productId: Int) {
        val st = _state.value
        if (!st.isValid || st.loading) return

        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null, success = false) }

            val req = toProductRequest(st)
            
            val result = repo.updateProductWithPhotos(
                productId = productId,
                photoUris = st.photos,
                request = req
            )

            result.onSuccess {
                _state.update { it.copy(loading = false, success = true) }
            }.onFailure { e ->
                _state.update { it.copy(loading = false, error = e.message ?: "Ошибка сохранения") }
            }
        }
    }

    private fun toProductRequest(st: AddProductState): ProductRequest {
        val price = st.priceValue ?: 0.0

        return ProductRequest(
            name = st.title.trim(),
            description = st.description.trim(),
            price = price,
            type = st.selectedCategories.firstOrNull()?.backendValue ?: "",
            categories = st.selectedCategories.map { it.backendValue },
            imageUrl = null,
            images = null,
            weight = st.weightText.trim().ifBlank { null },
            heightCm = st.heightText.trim().ifBlank { null },
            widthCm = st.widthText.trim().ifBlank { null },
            depthCm = st.depthText.trim().ifBlank { null },
            composition = st.composition.trim().ifBlank { null },
            youtubeUrl = st.youtubeUrl.trim().ifBlank { null }
        )
    }
}
