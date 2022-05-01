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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.tingting.databinding.FragmentFirstBinding
import com.example.tingting.utils.Entity.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.yuyakaido.android.cardstackview.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding
    private lateinit var adapter: CardStackAdapter

    companion object {
        fun newInstance() = tindercardstack()
    }

    private lateinit var manager: CardStackLayoutManager
    private lateinit var cardStackView: CardStackView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFirstBinding.inflate(layoutInflater)
        cardStackView = binding.cardstackview

        manager = CardStackLayoutManager(context, object : CardStackListener {
            override fun onCardDragging(direction: Direction, ratio: Float) {
            }

            override fun onCardSwiped(direction: Direction) {
                Log.d(
                    "CardStackView",
                    "onCardSwiped: p=" + manager.topPosition + " d=" + direction.name
                )
                if (direction == Direction.Right) {
                    Toast.makeText(context, "Direction Right", Toast.LENGTH_SHORT).show()
                    val currentIndex = manager.topPosition - 1
                    addVisited(adapter.getSpots()[currentIndex].id_user)
                    addMatch(adapter.getSpots()[currentIndex].id_user, adapter.getSpots()[currentIndex].name)

                }
                if (direction == Direction.Top) {
                    Toast.makeText(context, "Direction Top", Toast.LENGTH_SHORT).show()
                    val currentIndex = manager.topPosition - 1
                    addVisited(adapter.getSpots()[currentIndex].id_user)
                }
                if (direction == Direction.Left) {
                    Toast.makeText(context, "Direction Left", Toast.LENGTH_SHORT).show()
                    val currentIndex = manager.topPosition - 1
                    addVisited(adapter.getSpots()[currentIndex].id_user)

                }
                if (direction == Direction.Bottom) {
                    Toast.makeText(context, "Direction Bottom", Toast.LENGTH_SHORT).show()
                    val currentIndex = manager.topPosition - 1
                    addVisited(adapter.getSpots()[currentIndex].id_user)
                }

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
        adapter =
            CardStackAdapter(binding, createSpots(FirebaseAuth.getInstance().currentUser!!.uid))
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
                    .setDirection(Direction.Right)
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
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
                manager.setSwipeAnimationSetting(setting)
                cardStackView.swipe()
            }
        }

        return binding.root
    }


    private fun createSpots(id_user: String): List<Spot> {
        val spots = ArrayList<Spot>()
        val check = ArrayList<String>()
        val rootRef = FirebaseDatabase.getInstance().reference
        val messageRef = rootRef.child("Users")

        val messageRef1 = rootRef.child("Visited").child(id_user)

        messageRef1.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (ds in dataSnapshot.children) {
                    val id = ds.getValue(String::class.java)
                    check.add(id.toString())
                }

                messageRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (ds in dataSnapshot.children) {
                            val gender = ds.child("gender").getValue(String::class.java)
                            val display: String? = dataSnapshot.child(id_user).child("display")
                                .getValue(String::class.java)
                            var check_id = true
                            for (i in 0 until check.size) {
                                if (ds.key == check[i]) {
                                    check_id = false
                                    break
                                }
                            }

                            if (check_id) {
                                if (ds.key != id_user && (display == "All" || gender == display)) {
                                    val name = ds.child("name").getValue(String::class.java)
                                    val photo = ds.child("avatar").getValue(String::class.java)
                                    spots.add(
                                        Spot(
                                            name = name.toString(),
                                            city = "Kyoto",
                                            url = photo.toString(),
                                            id_user = ds.key!!
                                        )
                                    )
                                }
                            }
                        }
                        cardStackView.adapter = CardStackAdapter(binding, spots)
                    }


                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("TAG", databaseError.message)
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("TAG", databaseError.message) //Don't ignore errors!
            }
        })
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


    fun addVisited(targetId: String) {
        val userId = FirebaseAuth.getInstance().uid!!
        FirebaseDatabase.getInstance().getReference("/Visited/$userId/$targetId").setValue(targetId)
    }

    fun addMatch(targetId: String, name: String) {


        lifecycleScope.launch {

            withContext(Dispatchers.IO) {
                val userId = FirebaseAuth.getInstance().uid!!

                FirebaseDatabase.getInstance().getReference("/Match/$userId/$targetId")
                    .setValue(targetId)

                val notify = Notification(
                    "$name has liked you",
                    false,
                    time = System.currentTimeMillis().toString(),
                    null,
                    targetId
                )

                FirebaseDatabase.getInstance().getReference("/Notify/$targetId").push().setValue(notify)


                FirebaseDatabase.getInstance().getReference("/Match/$targetId/$userId")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            if (p0.exists()) {

                                val action = FirstFragmentDirections.actionHomepageToCongratulation(targetId = targetId)
                                Navigation.findNavController(view!!).navigate(action)

                                Log.i("TAG", p0.value.toString())

                                FirebaseDatabase.getInstance()
                                    .getReference("/Matched/$userId/$targetId")
                                    .setValue(targetId)
                                FirebaseDatabase.getInstance()
                                    .getReference("/Matched/$targetId/$userId")
                                    .setValue(userId)

                                val notify = Notification(
                                    "You got a match!",
                                    false,
                                    time = System.currentTimeMillis().toString(),
                                    null,
                                    targetId
                                )

                                FirebaseDatabase.getInstance().getReference("/Notify/$targetId").push().setValue(notify)

                            }
                        }
                    })
            }

        }

    }


}