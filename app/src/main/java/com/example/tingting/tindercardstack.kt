package com.example.tingting


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.tingting.databinding.FragmentTindercardstackBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.yuyakaido.android.cardstackview.*


class tindercardstack : Fragment() {
    private val TAG = "MainActivity"
    private lateinit var binding : FragmentTindercardstackBinding
    lateinit var adapter: CardStackAdapter
    lateinit var database :DatabaseReference

    companion object {
        fun newInstance() = tindercardstack()
    }

    private lateinit var  manager: CardStackLayoutManager
    private lateinit var cardStackView: CardStackView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTindercardstackBinding.inflate(layoutInflater)
        cardStackView = binding.cardstackview



        manager = CardStackLayoutManager(context, object : CardStackListener{
            override fun onCardDragging(direction: Direction, ratio: Float) {
            }

            override fun onCardSwiped(direction: Direction) {
                Log.d(
                    "CardStackView",
                    "onCardSwiped: p=" + manager.topPosition + " d=" + direction.name
                )
                if (direction == Direction.Right) {
                    Toast.makeText(context, "Direction Right", Toast.LENGTH_SHORT).show()
                }
                if (direction == Direction.Top) {
                    Toast.makeText(context, "Direction Top", Toast.LENGTH_SHORT).show()
                }
                if (direction == Direction.Left) {
                    Toast.makeText(context, "Direction Left", Toast.LENGTH_SHORT).show()
                }
                if (direction == Direction.Bottom) {
                    Toast.makeText(context, "Direction Bottom", Toast.LENGTH_SHORT).show()
                }

//                if (manager.topPosition == adapter.itemCount - 5) {
//                    paginate()
//                }
            }

            override fun onCardRewound() {
                Log.d("CardStackView", "onCardRewound: " + manager.topPosition)
            }

            override fun onCardCanceled() {
                Log.d("CardStackView", "onCardRewound: " + manager.topPosition)
            }

            override fun onCardAppeared(view: View, position: Int) {
                val tv = view.findViewById<TextView>(R.id.item_name)
                Log.d("CardStackView", "onCardAppeared: " + position + ", nama: " + tv.text)
            }

            override fun onCardDisappeared(view: View, position: Int) {
                val tv = view.findViewById<TextView>(R.id.item_name)
                Log.d("CardStackView", "onCardAppeared: " + position + ", nama: " + tv.text)
            }
        })

        val mDatabaseReference = FirebaseDatabase.getInstance().reference

        manager.setStackFrom(StackFrom.None)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.cardStackListener

        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        adapter = CardStackAdapter(createSpots(FirebaseAuth.getInstance().uid!!))
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator = DefaultItemAnimator()





        binding.ivUndof.setOnClickListener {
            if (manager.topPosition < adapter.itemCount) {
                val setting = RewindAnimationSetting.Builder()
                    .setDirection(Direction.Bottom)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(DecelerateInterpolator())
                    .build()
                manager.setRewindAnimationSetting(setting)
                cardStackView.rewind()
            }
        }

        binding.ivHeart.setOnClickListener {
            if (manager.topPosition < adapter.itemCount) {
                val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
                manager.setSwipeAnimationSetting(setting)
                cardStackView.swipe()
            }
        }
        binding.ivClose.setOnClickListener {
            if (manager.topPosition < adapter.itemCount) {
                val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
            }
        }

        return binding.root
    }


    private fun createSpots(id_user:String): List<Spot> {
        val spots = ArrayList<Spot>()
        val rootRef = FirebaseDatabase.getInstance().reference
        val messageRef = rootRef.child("Users")

        val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {

                    val id = ds.child("id").getValue(String::class.java)
                    val gender = ds.child("gender").getValue(String::class.java)
                    var  display: String? =dataSnapshot.child(id_user).child("display").getValue(String::class.java)
//                    if(id.equals(id_user)) {
//                        display = ds.child("display").getValue(String::class.java).toString()
//                    }
                    Log.i("display1", "${display}")
                        if (id != id_user && ( display == "All" || gender == display)) {
                            val name = ds.child("name").getValue(String::class.java)
                            val photo = ds.child("avatar").getValue(String::class.java)
                            spots.add(
                                Spot(
                                    name = name.toString(),
                                    city = "Kyoto",
                                    url = photo.toString(),
                                    id_user = id.toString()
                                )
                            )
                        }

//                    }
//                    else if (id != id_user && gender === display){
//                        val name = ds.child("name").getValue(String::class.java)
//                        val photo = ds.child("avatar").getValue(String::class.java)
//                        spots.add(Spot(name = name.toString(), city = "Kyoto", url =photo.toString()))
//                    }
                }
                cardStackView.adapter =   CardStackAdapter(spots)
            }


            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("TAG", databaseError.getMessage()) //Don't ignore errors!
            }
        }
        messageRef.addListenerForSingleValueEvent(valueEventListener)

//        spots.add(Spot(name = "Yasaka Shrine", city = "Kyoto", url = "https://source.unsplash.com/Xq1ntWruZQI/600x800"))
//        spots.add(Spot(name = "Fushimi Inari Shrine", city = "Kyoto", url = "https://source.unsplash.com/NYyCqdBOKwc/600x800"))
//        spots.add(Spot(name = "Bamboo Forest", city = "Kyoto", url = "https://source.unsplash.com/buF62ewDLcQ/600x800"))
//        spots.add(Spot(name = "Brooklyn Bridge", city = "New York", url = "https://source.unsplash.com/THozNzxEP3g/600x800"))
//        spots.add(Spot(name = "Empire State Building", city = "New York", url = "https://source.unsplash.com/USrZRcRS2Lw/600x800"))
//        spots.add(Spot(name = "The statue of Liberty", city = "New York", url = "https://source.unsplash.com/PeFk7fzxTdk/600x800"))
//        spots.add(Spot(name = "Louvre Museum", city = "Paris", url = "https://source.unsplash.com/LrMWHKqilUw/600x800"))
//        spots.add(Spot(name = "Eiffel Tower", city = "Paris", url = "https://source.unsplash.com/HN-5Z6AmxrM/600x800"))
//        spots.add(Spot(name = "Big Ben", city = "London", url = "https://source.unsplash.com/CdVAUADdqEc/600x800"))
//        spots.add(Spot(name = "Great Wall of China", city = "China", url = "https://source.unsplash.com/AWh9C-QjhE4/600x800"))
         return spots
    }
    private fun setupButton() {
        binding = FragmentTindercardstackBinding.inflate(layoutInflater)

//        val skip = binding.skip_button
//        skip.setOnClickListener {
//            val setting = SwipeAnimationSetting.Builder()
//                .setDirection(Direction.Left)
//                .setDuration(Duration.Normal.duration)
//                .setInterpolator(AccelerateInterpolator())
//                .build()
//            manager.setSwipeAnimationSetting(setting)
//            cardStackView.swipe()
//        }

        val rewind = binding.ivUndof
        rewind.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Bottom)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
        }

//        val like = findViewById<View>(com.yuyakaido.android.cardstackview.R.id.like_button)
//        like.setOnClickListener {
//            val setting = SwipeAnimationSetting.Builder()
//                .setDirection(Direction.Right)
//                .setDuration(Duration.Normal.duration)
//                .setInterpolator(AccelerateInterpolator())
//                .build()
//            manager.setSwipeAnimationSetting(setting)
//            cardStackView.swipe()
//        }
    }



}



