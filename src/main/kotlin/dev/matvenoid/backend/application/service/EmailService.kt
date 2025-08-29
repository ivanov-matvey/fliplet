package dev.matvenoid.backend.application.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Service
class EmailService(
    private val mailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine
) {
    @Value($$"${spring.mail.username}")
    private lateinit var fromEmail: String

    @Value($$"${app.mail.verification.subject}")
    private lateinit var verificationSubject: String

    private val logger = LoggerFactory.getLogger(EmailService::class.java)

    @Async
    fun sendVerificationEmail(email: String, code: String) {
        try {
            val context = Context().apply {
                setVariable("verificationCode", code)
            }

            val htmlContent = templateEngine.process("verification-email", context)

            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true, "UTF-8")

            helper.setTo(email)
            helper.setFrom(fromEmail)
            helper.setSubject(verificationSubject)
            helper.setText(htmlContent, true)

            mailSender.send(message)

            logger.info("Verification email sent ({})", email)
        } catch (e: MailException) {
            logger.error("Failed to send verification email to {}: {}", email, e.message)
        } catch (e: Exception) {
            logger.error("An unexpected error occurred while sending email to {}: {}", email, e.message)
        }
    }
}
