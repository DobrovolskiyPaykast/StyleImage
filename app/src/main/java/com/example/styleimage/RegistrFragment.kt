package com.example.styleimage

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.styleimage.Constant.AESEncyption
import com.example.styleimage.dataBase.addUser
import com.example.styleimage.dataBase.connectUser
import com.example.styleimage.databinding.FragmentRegistrBinding
import es.dmoral.toasty.Toasty
import java.security.SecureRandom
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.concurrent.thread


class RegistrFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        val binding = FragmentRegistrBinding.inflate(inflater)
        binding.bRegistr.setOnClickListener{
            val listData = connectUser()
            var check = true
            for (i in listData){
                if (AESEncyption.decrypt(i.phone) == binding.etPhoneRegistr.text. toString() || AESEncyption.decrypt(i.mail) == binding.etMailRegist.text.toString()){
                    check = false
                }
            }
            if (binding.etPasswordRegistr.text.toString().isNotEmpty() &&
                binding.etMailRegist.text.toString().isNotEmpty() &&
                binding.etNameRegistr.text.toString().isNotEmpty() &&
                binding.etPhoneRegistr.text.toString().isNotEmpty()){
                if (binding.etMailRegist.text.toString().contains("@") && binding.etMailRegist.text.toString().contains(".")){
                    if (binding.etPhoneRegistr.text.length == 11){
                        if (check){

                            val name = binding.etNameRegistr.text.toString()
                            val password = AESEncyption.encrypt(binding.etPasswordRegistr.text.toString())
                            val phone = AESEncyption.encrypt(binding.etPhoneRegistr.text.toString())
                            val mail = AESEncyption.encrypt(binding.etMailRegist.text.toString())
                            sendCode(name, phone, mail, password)

                        }else Toasty.error(requireContext(),"Аккаунт уже существует!", Toast.LENGTH_SHORT).show()
                    }else Toasty.error(requireContext(),"Длина номера должна быть равна 11!", Toast.LENGTH_SHORT).show()
                }else Toasty.error(requireContext(),"E-Mail введен не корректно!", Toast.LENGTH_SHORT).show()
            }else Toasty.error(requireContext(),"Необходимо заполнить все поля!", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    fun sendCode(name: String, phone: String, mail: String, password: String){
        val rand = SecureRandom()
        var code = rand.nextInt(10000)
        while (code < 1000){
            code = rand.nextInt(10000)
        }

        sendMail(AESEncyption.decrypt(mail), code)
        showCustomDialog(name, phone, mail, password, code)
    }


    fun sendMail(mail: String, code: Int) {
        thread {
            try {
                val props = Properties()
                props.put("mail.smtp.host", "smtp.yandex.ru")
                props.put("mail.smtp.auth", "true")
                props.put("mail.smtp.port", "465")
                props.put("mail.smtp.ssl.enable", "true")
                val session: Session = Session.getInstance(props,
                    object : Authenticator() {
                        override fun getPasswordAuthentication(): PasswordAuthentication {
                            return PasswordAuthentication("styleandimage@yandex.ru", "lzxlsardotqqgedv")
                        }
                    })
                val message: Message = MimeMessage(session)
                message.setFrom(InternetAddress("styleandimage@yandex.ru", "StyleAndImage"))
                message.setRecipient(
                    Message.RecipientType.TO,
                    InternetAddress(mail))
                message.subject = "Регистрация в приложении StyleAndImage"
                message.setText("\nВаш проверочный код - $code.\n\nВведите этот код в приложении StyleAndImage, чтобы активировать свою учетную запись.\n")
                Transport.send(message)
                println("Sent message successfully....")
            } catch (mex: Exception) {
                println("******* ERROR sending Email *******")
                mex.printStackTrace()
            }
        }.join()
    }

    private var timer: CountDownTimer? = null
    private lateinit var alertDialog: AlertDialog
    private fun showCustomDialog(name:String, phone:String, mail:String, password:String, code: Int) {
        val inflater: LayoutInflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.dialog_verification, null)

        val etCheckCodeVerification = dialogView.findViewById<TextView>(R.id.etCheckCodeVerification)
        val bVerification: Button = dialogView.findViewById(R.id.bVerification)
        val tvVerification: TextView = dialogView.findViewById(R.id.tvVerification)

        bVerification.setOnClickListener {
            if (etCheckCodeVerification.text.toString() == code.toString()){
                addUser(name, phone, mail, password)
                Toasty.success(requireContext(),"Аккаунт успешно создан!", Toast.LENGTH_SHORT).show()
                alertDialog.cancel()
            }
            else{
                Toasty.error(requireContext(),"Код введен не верно!", Toast.LENGTH_SHORT).show()
            }
        }

        timer = object : CountDownTimer(70000, 1000){
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                tvVerification.text = "Отправить код ещё раз можно через: ${millisUntilFinished/1000}"
            }

            override fun onFinish() {
                val content = SpannableString("Отправить код ещё раз")
                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                tvVerification.text = content
                tvVerification.setOnClickListener{
                    sendCode(name, phone, mail, password)
                    alertDialog.cancel()
                }
            }
        }.start()


        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        dialogBuilder.setOnDismissListener { }
        dialogBuilder.setView(dialogView)

        alertDialog = dialogBuilder.create()
        alertDialog.show()
    }


    companion object {fun newInstance() = RegistrFragment()}
}