package com.example.tingting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation
import com.example.tingting.activity.MainActivity
import com.example.tingting.databinding.FragmentAddImageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddImage.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddImage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddImage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddImage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private lateinit var binding: FragmentAddImageBinding
    private lateinit var auth: FirebaseAuth
    private var images: MutableList<Uri?> ?= null
    private val PICK_IMAGES_CODE = 0
    private var position = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        binding = FragmentAddImageBinding.inflate(layoutInflater)

        ///////////////////////////////////////////////////////////////////////
        binding.btnComplete.setOnClickListener{
            // tạo thông tin thành công -> Chuyển đến main activity
            val mDatabaseReference = FirebaseDatabase.getInstance().reference
            mDatabaseReference
                .child("Users")
                .child(auth.currentUser?.uid.toString())
                .child("firstTimeLogin").setValue(false)


            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener{
            val action = AddImageDirections.actionAddImageToInputUserPreferences()
            Navigation.findNavController(binding.root).navigate(action)
        }


        var listButtonAddImage =
            arrayListOf(
                binding.ivAddImage1,
                binding.ivAddImage2,
                binding.ivAddImage3,
                binding.ivAddImage4,
                binding.ivAddImage5,
                binding.ivAddImage6)

        listButtonAddImage.forEach { buttonAddImage ->
            buttonAddImage.setOnClickListener{
                pickImageIntent()
            }
        }
        return binding.root
    }

    private fun pickImageIntent(){
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), PICK_IMAGES_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_CODE || requestCode == Activity.RESULT_OK) {
            images = mutableListOf()
            if (data!!.clipData != null){
                val count: Int = data.clipData!!.itemCount
                for (i in 0 until count){
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    images!!.add(imageUri)
                    Log.i("hihi", images!!.toString())
                }
            }
            else {
                val imageUri = data.data
                images!!.add(imageUri)
            }

            var listButtonAddImage =
                arrayListOf(
                    binding.ivAddImage1,
                    binding.ivAddImage2,
                    binding.ivAddImage3,
                    binding.ivAddImage4,
                    binding.ivAddImage5,
                    binding.ivAddImage6)

            listButtonAddImage.forEach { buttonAddImage ->
                buttonAddImage.setImageResource(R.drawable.placeholder_img  )
                buttonAddImage.setBackgroundResource(R.drawable.da_background_tab)
            }
            for (i in 0 until images!!.size){
                listButtonAddImage[i].setImageURI(images!![i])
                listButtonAddImage[i].setBackgroundResource(R.drawable.da_background_tab)
            }

        }
    }
}