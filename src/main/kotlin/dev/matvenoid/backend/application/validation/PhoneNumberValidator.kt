package dev.matvenoid.feature.auth.application.validation

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dev.matvenoid.backend.application.validation.PhoneNumber
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PhoneNumberValidator : ConstraintValidator<PhoneNumber, String> {

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value.isNullOrBlank()) return true

        val phoneUtil = PhoneNumberUtil.getInstance()
        try {
            val numberProto = phoneUtil.parse(value, "RU")

            if (!phoneUtil.isValidNumber(numberProto)) return false

            val regionCode = phoneUtil.getRegionCodeForNumber(numberProto)
            return "RU".equals(regionCode, ignoreCase = true)

        } catch (e: NumberParseException) {
            return false
        }
    }
}
