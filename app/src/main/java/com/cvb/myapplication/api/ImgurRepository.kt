package com.cvb.myapplication.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class ImgurRepositoryImpl(private val apiService: ImgurAPI): ImgurRepository {

    override suspend fun getImageList(
        query: String,
        sort: String,
        window: String,
        page: Int
    ): Flow<List<ImgurRepoImage>> =  flow {
        val galleryObject = apiService.getImageListInclusive(sort, window, page, query).run { this.data }
        emit(galleryObject.toList())
    }.flowOn(Dispatchers.IO)
        .map {
            it.filter { it.images?.isNotEmpty() == true }.map {
                it.toImgurRepoImage()
            }.filter { return@filter it.type.endsWith("mp4") != true }
        }.catch {
            System.out.println("ImageRepo: ${it}")
            it.printStackTrace()
        }
    }

private fun GalleryObject.toImgurRepoImage(): ImgurRepoImage {
    return ImgurRepoImage(
        this.id ?: "",
        this.images[0].link ?: "",
        this.images[0].title ?: "",
        this.images[0].type?.split("/")?.last() ?: ""
    )
}

data class ImgurRepoImage(val id: String, val link: String, val descript: String = "", val type: String = "")
interface ImgurRepository {

    suspend fun getImageList(query: String,
                             sort: String = "time",
                             window: String = "all",
                             page: Int = 1,): Flow<List<ImgurRepoImage>>

}