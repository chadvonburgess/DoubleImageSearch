import com.google.gson.annotations.SerializedName

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewspaperApiService {

    // Example GET request to fetch newspaper issues with pagination support.
    // Adjust the path, query parameters, and URL as per your API's actual endpoint.
    @GET("http://chroniclingamerica.loc.gov/search/pages/results/?proxtext={search}&format=json")
    suspend fun getNewspaperIssues(
        @Path("query") query: String,  // The search query
        @Query("startIndex") startIndex: Int = 0,  // Pagination start index
        @Query("itemsPerPage") itemsPerPage: Int = 100  // Number of items per page
    ): NewspaperResponse

}


data class NewspaperResponse(
    @SerializedName("totalItems") val totalItems: Int,
    @SerializedName("endIndex") val endIndex: Int,
    @SerializedName("startIndex") val startIndex: Int,
    @SerializedName("itemsPerPage") val itemsPerPage: Int,
    @SerializedName("items") val items: List<NewspaperIssue>
)

data class NewspaperIssue(
    @SerializedName("sequence") val sequence: Int,
    @SerializedName("county") val county: List<String>,
    @SerializedName("edition") val edition: String?,
    @SerializedName("frequency") val frequency: String,
    @SerializedName("id") val id: String,
    @SerializedName("subject") val subject: List<String>,
    @SerializedName("city") val city: List<String>,
    @SerializedName("date") val date: String,
    @SerializedName("title") val title: String,
    @SerializedName("end_year") val endYear: Int,
    @SerializedName("note") val note: List<String>,
    @SerializedName("state") val state: List<String>,
    @SerializedName("section_label") val sectionLabel: String,
    @SerializedName("type") val type: String,
    @SerializedName("place_of_publication") val placeOfPublication: String,
    @SerializedName("start_year") val startYear: Int,
    @SerializedName("edition_label") val editionLabel: String,
    @SerializedName("publisher") val publisher: String,
    @SerializedName("language") val language: List<String>,
    @SerializedName("alt_title") val altTitle: List<String>,
    @SerializedName("lccn") val lccn: String,
    @SerializedName("country") val country: String,
    @SerializedName("ocr_eng") val ocrEng: String,
    @SerializedName("batch") val batch: String,
    @SerializedName("title_normal") val titleNormal: String,
    @SerializedName("url") val url: String,
    @SerializedName("place") val place: List<String>,
    @SerializedName("page") val page: String
)