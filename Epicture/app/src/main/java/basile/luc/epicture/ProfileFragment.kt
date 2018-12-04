package basile.luc.epicture


import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Date
import java.sql.Timestamp


class ProfileFragment : Fragment() {

    private lateinit var imageAdapter : PostAdapter

    companion object {
        fun newInstance(AccessToken: String?, AccountUsername: String?): ProfileFragment {
            val fragment = ProfileFragment()
            if (AccessToken != null) {
                val args = Bundle()
                args.putString("AccountUsername", AccountUsername)
                args.putString("AccessToken", AccessToken)
                fragment.arguments = args
            }
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println(this.arguments?.getString("AccessToken"))
        if (this.arguments?.getString("AccessToken") != null) {
            println(this.arguments?.getString("AccessToken"))

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://api.imgur.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            val apiImages = retrofit.create(ApiProfile::class.java)

            val call = apiImages.getProfile(this.arguments?.getString("AccountUsername")!!)
            call.enqueue(object : retrofit2.Callback<ProfileReponse> {
                override fun onResponse(call: Call<ProfileReponse>, response: Response<ProfileReponse>) {
                    val body = response.body()
                    println("Profile Passe")
                    if (body != null) {
                        println(body.data.reputation)
                        val username : TextView = view.findViewById(R.id.username)
                        val reputation : TextView = view.findViewById(R.id.reputation)
                        val avatar : ImageView = view.findViewById(R.id.avatar)
                        val date : TextView = view.findViewById(R.id.created)
                        val cover : ImageView = view.findViewById(R.id.cover)
                        username.text = body.data.url
                        reputation.text = "Reputation: " + body.data.reputation.toString() +" • "+ body.data.reputation_name
                        val stamp = Timestamp(System.currentTimeMillis())
                        val dated = Date(stamp.getTime())
                        date.text = "Created: " + dated.toString()
                        Glide
                            .with(requireContext())
                            .load(body.data.avatar)
                            .into(avatar)
                        Glide
                            .with(requireContext())
                            .load(body.data.cover)
                            .into(cover)

                    }
                }

                override fun onFailure(call: Call<ProfileReponse>, t: Throwable) {
                    println("ERROR in profile getting")
                }
            })
            // END UPBAR Call
            imageAdapter = PostAdapter()
            post_list.layoutManager = LinearLayoutManager(activity)
            post_list.adapter = imageAdapter
            val retrofit2 : Retrofit = Retrofit.Builder()
                .baseUrl("https://api.imgur.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            val apiImages2 = retrofit2.create(ApiProfileImages::class.java)

            apiImages2.getProfileImages(this.arguments?.getString("AccountUsername")!!)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ imageAdapter.setImages(it.data) },
                    {
                        Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                    })
        }
    }


    inner class PostAdapter : RecyclerView.Adapter<PostAdapter.ImageViewHolder>() {

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

