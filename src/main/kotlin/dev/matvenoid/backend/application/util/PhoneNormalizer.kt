package dev.matvenoid.backend.application.util

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.NumberParseException
import org.springframework.security.authentication.BadCredentialsException

data class NormalizedPhone(
    val forDb: String,
    val forDisplay: String
)

fun String.normalizePhone(): NormalizedPhone = try {
    val util   = PhoneNumberUtil.getInstance()
    val proto  = util.parse(this, "RU")
    NormalizedPhone(
        forDb = proto.nationalNumber.toString(),
        forDisplay = util.format(proto, PhoneNumberUtil.PhoneNumberFormat.E164)
    )
} catch (_: NumberParseException) {
    throw BadCredentialsException("Некорректный формат номера телефона: $this")
}
