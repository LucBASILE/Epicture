package basile.luc.epicture


import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import basile.luc.epicture.R.id.movies_list
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class SearchFragment : Fragment() {

    private lateinit var searchAdapter : SearchAdapter

    //Get input -> query
    companion object {
        fun newInstance(query: String): SearchFragment {
            val fragment = SearchFragment()
            val args = Bundle()
            args.putString("query", query)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchAdapter = SearchAdapter()
        search_list.layoutManager = LinearLayoutManager(activity)
        search_list.adapter = searchAdapter
        val query = this.arguments?.getString("query")
        Log.d("Debug", "input: $query")
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://api.imgur.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        val apiSearch = retrofit.create(ApiSearch::class.java)
        if (query != null) {
            apiSearch.getSearch(query)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ searchAdapter.setImages(it.data)},
                            {
                                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                            })
        }
    }

    inner class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ImageViewHolder>() {
        private val images: MutableList<Image> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            return ImageViewHolder(layoutInflater.inflate(R.layout.item_movie_layout, parent, false))
        }

        override fun getItemCount(): Int {
            return images.size
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            holder.bindModel(images[position])
            println(images[position].link)
            println(images[position].images?.get(0)?.link)
        }

        fun setImages(data: List<Image>) {
            images.addAll(data)
            notifyDataSetChanged()
        }

        inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val movieTitleTxt: TextView = itemView.findViewById(R.id.movieTitle)
            val movieAvatarImage: ImageView = itemView.findViewById(R.id.movieAvatar)

            fun bindModel(movie: Image) {
                movieTitleTxt.text = movie.title
                if (movie.images?.get(0) != null) {
                    if (movie.images?.get(0).link?.substring(movie.images?.get(0).link!!.length - 3)!!.compareTo("gif") == 0) {
                        println("Luc la pupute")
                        Glide
                                .with(requireContext())
                                .asGif()
                                .load(movie.images?.get(0).link)
                                .apply(RequestOptions().override(300, 300))
                                .into(movieAvatarImage)
                    } else if (movie.images?.get(0).link?.substring(movie.images?.get(0).link!!.length - 3)!!.compareTo("mp4") == 0) {
                        Glide
                                .with(requireContext())
                                .load(movie.images?.get(0).link)
                                .into(movieAvatarImage)
                    } else {
                        Glide
                                .with(requireContext())
                                .load(movie.images?.get(0).link)
                                .apply(RequestOptions().override(300, 300))
                                .into(movieAvatarImage)
                    }
                } else {
                    Glide
                            .with(requireContext())
                            .load(movie.link)
                            .into(movieAvatarImage)
                }
            }
        }
    }

}
