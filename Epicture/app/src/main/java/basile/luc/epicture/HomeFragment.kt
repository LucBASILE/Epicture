package basile.luc.epicture


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import basile.luc.epicture.R.id.movies_list
import basile.luc.epicture.R.id.parent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


interface ClickListener {
    fun onClick(view: View, position: Int)
    fun onLongClick(view: View, position: Int)
}

internal class RecyclerTouchListener(context: Context,recycleView: RecyclerView, private val clicklistener: ClickListener?
) : RecyclerView.OnItemTouchListener {
    private val gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val child = recycleView.findChildViewUnder(e.x, e.y)
                if (child != null && clicklistener != null) {
                    clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child))
                }
            }
        })
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val child = rv.findChildViewUnder(e.x, e.y)
        if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
            clicklistener.onClick(child, rv.getChildAdapterPosition(child))
        }

        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }
}


class HomeFragment : Fragment() {

    lateinit var imageAdapter : ImagesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageAdapter = ImagesAdapter()
        movies_list.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        movies_list.layoutManager = LinearLayoutManager(activity)
        movies_list.adapter = imageAdapter
        movies_list.addOnItemTouchListener(
            RecyclerTouchListener(
                this!!.activity!!,
                movies_list, object : ClickListener {
                    override fun onClick(view: View, position: Int) {
                        //Values are passing to activity & to fragment as well
                        println("Luc la salope")
                    }

                    override fun onLongClick(view: View, position: Int) {

                    }
                })
        )
        val retrofit : Retrofit = Retrofit.Builder()
            .baseUrl("https://api.imgur.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        val apiImages = retrofit.create(ApiImage::class.java)

        apiImages.getImages()
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ imageAdapter.setImages(it.data) },
                {
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                })
    }

    inner class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {
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
            val movieTitleTxt : TextView = itemView.findViewById(R.id.movieTitle)
            val movieAvatarImage : ImageView = itemView.findViewById(R.id.movieAvatar)

            fun bindModel(movie: Image) {
                movieTitleTxt.text = movie.title
                if (movie.images?.get(0) != null) {
                    if (movie.images?.get(0).link?.substring(movie.images?.get(0).link!!.length-3)!!.compareTo("gif") == 0) {
                        println("Luc la pupute")
                        Glide
                            .with(requireContext())
                            .asGif()
                            .load(movie.images?.get(0).link)
                            .apply(RequestOptions().override(300, 300))
                            .into(movieAvatarImage)
                    }
                    else if (movie.images?.get(0).link?.substring(movie.images?.get(0).link!!.length-3)!!.compareTo("mp4") == 0) {
                        Glide
                            .with(requireContext())
                            .load(movie.images?.get(0).link)
                            .into(movieAvatarImage)
                    }
                    else {
                        Glide
                            .with(requireContext())
                            .load(movie.images?.get(0).link)
                            .apply(RequestOptions().override(300, 300))
                            .into(movieAvatarImage)
                    }
                }
                else {
                    Glide
                        .with(requireContext())
                        .load(movie.link)
                        .into(movieAvatarImage)
                }
            }
        }
    }
}
