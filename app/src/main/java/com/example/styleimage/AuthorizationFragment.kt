package com.example.styleimage

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.styleandimage.SupportFunctions.SharedPreference
import com.example.styleimage.Constant.AESEncyption
import com.example.styleimage.Constant.checkSA
import com.example.styleimage.dataBase.connectUser
import com.example.styleimage.databinding.FragmentAuthorizationBinding
import es.dmoral.toasty.Toasty
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class AuthorizationFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuthorizationBinding.inflate(inflater)

        val listUser = connectUser()
        val sharedPreference = SharedPreference(requireContext())
        if (sharedPreference.getValueString("phone") != null && sharedPreference.getValueString("password") != null && checkSA) {
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }

        binding.bAuto.setOnClickListener{
            sharedPreference.saveStr("phone", binding.etPhoneAuto.editableText.toString(), "password", "")
            var status = false
            if (binding.etPhoneAuto.text.toString().trim().length == 11) {
                for (i in listUser){
                    val password = AESEncyption.decrypt(i.password)
                    val phone = AESEncyption.decrypt(i.phone)
                    if (binding.etPhoneAuto.text.toString() == phone && binding.etPasswordAuto.text.toString() == password){
                        status = true
                        if (binding.cbAuto.isChecked) {
                            sharedPreference.saveStr("phone", binding.etPhoneAuto.editableText.toString(),
                                                    "password", binding.etPasswordAuto.editableText.toString())
                            checkSA = true
                        } else {
                            sharedPreference.removeValue("password")
                        }
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                        break
                    }
                }
            }else Toasty.error(requireContext(),"Данные введены неверно!", Toast.LENGTH_SHORT).show()
            if (!status) Toasty.error(requireContext(),"Данные введены неверно!", Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }


    companion object {
        fun newInstance() = AuthorizationFragment()}
}