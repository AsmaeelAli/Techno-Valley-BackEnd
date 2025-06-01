package com.techno.valley.project2.feature.users.usecase

import com.sendgrid.Method
import com.sendgrid.Request
import com.sendgrid.SendGrid
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Content
import com.sendgrid.helpers.mail.objects.Email
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class EmailSender {

    @Value("\${sendgrid.api.key}")
    private lateinit var sendGridApiKey: String

    @Value("\${sendgrid.sender.email}")
    private lateinit var senderEmail: String

    operator fun invoke(name: String, email: String, code: String): String {
        val contentHtml = """ 
        <html>
  <head>
    <style>
      body {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        background: linear-gradient(135deg, #e0ecff, #f4f7fc);
        padding: 40px;
        color: #333;
      }

      .container {
        max-width: 600px;
        margin: 0 auto;
        background: #ffffff;
        border-radius: 16px;
        padding: 40px 30px;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        animation: fadeIn 0.6s ease-in-out;
      }

      @keyframes fadeIn {
        from {
          opacity: 0;
          transform: translateY(20px);
        }
        to {
          opacity: 1;
          transform: translateY(0);
        }
      }

      .header {
        text-align: center;
        color: #2c6cb0;
        margin-bottom: 25px;
      }

      .header h1 {
        margin: 0;
        font-size: 28px;
        letter-spacing: 1px;
      }

      p {
        font-size: 16px;
        line-height: 1.6;
        margin-bottom: 15px;
      }

      .code {
        background: #e9f3ff;
        padding: 18px 0;
        border-radius: 10px;
        font-size: 28px;
        font-weight: bold;
        color: #2a4a77;
        text-align: center;
        letter-spacing: 4px;
        box-shadow: inset 0 2px 5px rgba(0, 0, 0, 0.05);
        margin: 20px 0;
        user-select: all;
      }

      .footer {
        margin-top: 30px;
        text-align: center;
        font-size: 13px;
        color: #999;
        border-top: 1px solid #eee;
        padding-top: 15px;
      }

      .footer p {
        margin: 0;
      }
    </style>
  </head>
  <body>
    <div class="container">
      <div class="header">
        <h1>Verification Code</h1>
      </div>
      <p>Hi $name,</p>
      <p>Thanks for registering. Please use the verification code below:</p>
      <div class="code">$code</div>
      <p>This code will expire in 3 minutes.</p>
      <div class="footer">
        <p>TECHNO-VALLEY Team</p>
      </div>
    </div>
  </body>
</html>

        """.trimIndent()

        val mail = Mail(
            Email(senderEmail),
            "Your Verification Code",
            Email(email),
            Content("text/html", contentHtml),
        )
        val request = Request()

        return try {
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = mail.build()

            val sendGrid = SendGrid(sendGridApiKey)
            val response = sendGrid.api(request)

            return when (response.statusCode) {
                in 200..299 -> {
                    println("Email sent successfully.")
                    "sent"
                }

                else -> {
                    println("SendGrid error: ${response.body}")
                    "SendGrid error: ${response.body}"
                }
            }
        } catch (ex: IOException) {
            println("IOException occurred: ${ex.message}")
            "Exception: ${ex.message}"
        }
    }
}
