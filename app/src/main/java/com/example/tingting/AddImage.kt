package com.example.tingting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.tingting.activity.MainActivity
import com.example.tingting.databinding.FragmentAddImageBinding

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
    private var images: Array<Uri?> ?= null
    private val PICK_IMAGE_CODE = 0
    private var position = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddImageBinding.inflate(layoutInflater)

        ///////////////////////////////////////////////////////////////////////
        binding.btnComplete.setOnClickListener{

            // tạo thông tin thành công -> Chuyển đến main activity
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener{
            val action = AddImageDirections.actionAddImageToInputUserPreferences()
            Navigation.findNavController(binding.root).navigate(action)
        }

        images = emptyArray()

        binding.ivAddImage6.setOnClickListener{
            pickImageIntent()
        }
        return binding.root
    }


    private fun pickImageIntent() {
        val gallery = Intent(Intent.ACTION_PICK)
        gallery.type = "image/*"
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        gallery.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(gallery, PICK_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_CODE){
            if (requestCode == Activity.RESULT_OK){
                if (data!!.clipData != null) {
                    val count = data.clipData!!.itemCount
                    for (i in 0 until count){
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        images != (imageUri)
                    }

                    binding.ivAddImage1.setImageURI(images!![0])
                }
                else {
                    val imageUri = data.data
                    binding.ivAddImage2.setImageURI(imageUri)
                    position
                }
            }
        }
    }
}