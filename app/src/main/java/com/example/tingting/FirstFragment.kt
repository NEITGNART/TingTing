package com.example.tingting

import android.location.Geocoder
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
import com.example.tingting.utils.Entity.LatLng
import com.example.tingting.utils.Entity.Notification
import com.example.tingting.utils.Global.getDistance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yuyakaido.android.cardstackview.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding
    private lateinit var adapter: CardStackAdapter

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
                    addMatch(adapter.getSpots()[currentIndex].id_user)

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
            if (manager.topPosition > 0) {
                val setting = RewindAnimationSetting.Builder()
                    .setDirection(Direction.Bottom)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(DecelerateInterpolator())
                    .build()
                manager.setRewindAnimationSetting(setting)
                removeVisited(adapter.getSpots()[manager.topPosition - 1].id_user)
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

    fun removeVisited(targetId: String) {
        val userId = FirebaseAuth.getInstance().uid!!
        FirebaseDatabase.getInstance().getReference("/Visited/$userId/$targetId").removeValue()
    }


    private fun createSpots(id_user: String): List<Spot> {
        val spots = ArrayList<Spot>()
        val visited = mutableMapOf<String, Int>()
        val rootRef = FirebaseDatabase.getInstance().reference


        val userRef = rootRef.child("Users")
        val visitedRef = rootRef.child("Visited").child(id_user)

        cardStackView.adapter =
            CardStackAdapter(binding, spots)

        FirebaseDatabase.getInstance().getReference("/Users/$id_user/address").get()
            .addOnSuccessListener {
                val latlng_user: LatLng? = it.getValue(LatLng::class.java)

                if (latlng_user != null) {
                    FirebaseDatabase.getInstance().getReference("/Setting/$id_user/age/min").get()
                        .addOnSuccessListener {
                            val ageMin = it.getValue(Int::class.java)!! + 18

                            FirebaseDatabase.getInstance().getReference("/Setting/$id_user/age/max")
                                .get()
                                .addOnSuccessListener {
                                    val ageMax = it.getValue(Int::class.java)!! + 18

                                    visitedRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {
                                            TODO("Not yet implemented")
                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            for (ds in p0.children) {
                                                val id = ds.getValue(String::class.java)
                                                visited[id!!] = 1
                                            }
                                            Log.i("visited", visited.toString())
                                            userRef.addValueEventListener(object :
                                                ValueEventListener {
                                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                    for (ds in dataSnapshot.children) {

                                                        val gender =
                                                            ds.child("gender")
                                                                .getValue(String::class.java)?: continue
                                                        val display: String? =
                                                            dataSnapshot.child(id_user).child("display")
                                                                .getValue(String::class.java)

                                                        val latlng =
                                                            ds.child("address")
                                                                .getValue(LatLng::class.java)?: continue

                                                        val birthDate =
                                                            ds.child("birthDate")
                                                                .getValue(String::class.java)?: continue

                                                        val yearOfBirth =
                                                            birthDate!!.split("/")[2].toInt()
                                                        val age = Calendar.getInstance()
                                                            .get(Calendar.YEAR) - yearOfBirth

                                                        if (ds.key in visited) continue

                                                        if (age in ageMin..ageMax) {
                                                            FirebaseDatabase.getInstance()
                                                                .getReference("/Setting/$id_user/distance/max")
                                                                .get().addOnSuccessListener {
                                                                    val distanceMax =
                                                                        (it.getValue(Int::class.java)?.toInt()?.plus(1))?.times(5)
                                                                    Log.i("KC", distanceMax.toString())

                                                                    if (ds.key != id_user && (display == "All" || gender == display)) {

                                                                        val kc = getDistance(
                                                                            latlng!!.latitude,
                                                                            latlng!!.longitude,
                                                                            latlng_user!!.latitude,
                                                                            latlng_user!!.longitude
                                                                        )

                                                                        if (kc < (distanceMax!! + 1)) {

                                                                            val name = ds.child("name")
                                                                                .getValue(String::class.java)
                                                                            val photo =
                                                                                ds.child("avatar")
                                                                                    .getValue(String::class.java)

                                                                            val geocoder =
                                                                                Geocoder(binding.root.context)
                                                                            val addresses =
                                                                                geocoder.getFromLocation(
                                                                                    latlng!!.latitude,
                                                                                    latlng!!.longitude,
                                                                                    1
                                                                                )

                                                                            val address =
                                                                                addresses[0]
                                                                                    .getAddressLine(0)
                                                                            val list_address: List<String> =
                                                                                address!!.split(", ")
                                                                            var address_user: String =
                                                                                list_address[2]
                                                                            for (i in 3 until list_address.size) {
                                                                                address_user =
                                                                                    address_user + ", " + list_address[i]
                                                                            }
                                                                            val ad = address_user

                                                                            FirebaseDatabase.getInstance().getReference("/Setting/${ds.key}/favorite").get().addOnSuccessListener { itAnotherUser ->
                                                                                val hashFavorite = mutableMapOf<String, Int>()
                                                                                for (data in itAnotherUser.children){
                                                                                    hashFavorite[data.key.toString()] = 1
                                                                                }
                                                                                FirebaseDatabase.getInstance().getReference("/Setting/$id_user/favorite").get().addOnSuccessListener {itUser ->
                                                                                    for (data in itUser.children)
                                                                                        if (data.key in hashFavorite || data.key == "You select nothing") {
                                                                                            spots.add(
                                                                                                Spot(
                                                                                                    name = name.toString() + " - " + "✈ " + kc + " km",
                                                                                                    city = ad,
                                                                                                    url = photo.toString(),
                                                                                                    id_user = ds.key!!
                                                                                                )
                                                                                            )
                                                                                            cardStackView.adapter =
                                                                                                CardStackAdapter(
                                                                                                    binding,
                                                                                                    spots
                                                                                                )
                                                                                            break
                                                                                        }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                        }
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    TODO("Not yet implemented")
                                                }
                                            }
                                            )
                                        }

                                    })

                                }
                        }
                }
            }



        return spots
    }


    fun addVisited(targetId: String) {
        val userId = FirebaseAuth.getInstance().uid!!
        FirebaseDatabase.getInstance().getReference("/Visited/$userId/$targetId").setValue(targetId)
    }

    fun addMatch(targetId: String) {


        lifecycleScope.launch {

            withContext(Dispatchers.IO) {
                val userId = FirebaseAuth.getInstance().uid!!

                FirebaseDatabase.getInstance().getReference("/Match/$userId/$targetId")
                    .setValue(targetId)


                FirebaseDatabase.getInstance()
                    .getReference("/Users/${FirebaseAuth.getInstance().uid}/name")
                    .get().addOnSuccessListener {
                        val notify = Notification(
                            "${it.getValue(String::class.java)} has liked you",
                            false,
                            time = System.currentTimeMillis().toString(),
                            null,
                            FirebaseAuth.getInstance().uid!!,
                        )
                        FirebaseDatabase.getInstance().getReference("/Notify/$targetId").push()
                            .setValue(notify)
                        FirebaseDatabase.getInstance().getReference("/SeenNotify/$targetId")
                            .setValue(true)
                    }



                FirebaseDatabase.getInstance().getReference("/Match/$targetId/$userId")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            if (p0.exists()) {

                                val action =
                                    FirstFragmentDirections.actionHomepageToCongratulation(targetId = targetId)
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
                                    FirebaseAuth.getInstance().uid!!,
                                )

                                FirebaseDatabase.getInstance().getReference("/Notify/$targetId")
                                    .push().setValue(notify)
                                FirebaseDatabase.getInstance().getReference("/SeenNotify/$targetId")
                                    .setValue(true)
                            }
                        }
                    })
            }

        }

    }


}