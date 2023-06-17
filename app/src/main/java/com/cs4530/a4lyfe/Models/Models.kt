package com.cs4530.a4lyfe.models

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.util.Base64
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import kotlin.math.roundToLong


@Serializable
data class User(
    var registered: Boolean = false,
    var firstName: String? = null,
    var lastName: String? = null,
//    @Serializable(with = LocalDateSerializer::class)
    var birthday: String? = null,
    var weight: Double? = null,
    var feet: Double? = null,
    var inches: Double? = null,
    var height: Double? = null,
    var goal: Double? = null,
    var lifestyle: String? = null,
    var sex: String? = null,
//    @Serializable(with = BitmapSerializer::class)
    var profileImage: String? = null,
) {
    fun copyClass(copyUser: User) {
        if (copyUser.firstName != null) {
            this.firstName = copyUser.firstName
        }
        if (copyUser.lastName != null) {
            this.lastName = copyUser.lastName
        }
        if (copyUser.birthday != null) {
            this.birthday = copyUser.birthday
        }
        if (copyUser.weight != null) {
            this.weight = copyUser.weight
        }
        if (copyUser.inches != null) {
            this.inches = copyUser.feet
        }
        if (copyUser.feet != null) {
            this.feet = copyUser.feet
        }
        if (copyUser.height != null) {
            this.height = copyUser.height
        }
        if (copyUser.goal != null) {
            this.goal = copyUser.goal
        }
        if (copyUser.lifestyle != null) {
            this.lifestyle = copyUser.lifestyle
        }
        if (copyUser.sex != null) {
            this.sex = copyUser.sex
        }
        if (copyUser.profileImage != null) {
            this.profileImage = copyUser.profileImage
        }
    }


    fun completed(): Boolean {

        if (firstName == null || firstName == "" || lastName == null || lastName == "" || birthday == null || weight == null || height == null || goal == null || lifestyle == null || sex == null) {
            return false
        }
        return true

    }


    companion object {
        @JvmStatic
        fun decode(str: String): User {
            return Json.decodeFromString(this.serializer(), str)

        }

        @JvmStatic
        fun encode(usr: User): String {
            return Json.encodeToString(this.serializer(), usr)
        }
    }
}

@Serializable
class WeatherData {
    // Setters and Getters
    var currentCondition = CurrentCondition()
    var temperature = Temperature()
    var wind = Wind()
    var rain = Rain()
    var snow = Snow()
    var clouds = Clouds()
    var city: String = ""

}

@Serializable
class CurrentCondition {
    var weatherId: Long = 0
    var condition: String? = null
    var descr: String? = null
    var icon: String? = null
    var pressure = 0.0
    var humidity = 0.0
}

@Serializable
class Temperature {
    var temp: Double? = null
    var minTemp: Double? = null
    var maxTemp: Double? = null

    fun getTempInFahrenheit(): Long? {
        return temp?.minus(273.15)?.times((1.8))?.plus(32)?.roundToLong()
    }
}

@Serializable
class Wind {
    var speed = 0.0
    var deg = 0.0
}

@Serializable
class Rain {
    var time: String? = null
    var amount = 0.0
}

@Serializable
class Snow {
    var time: String? = null
    var amount = 0.0
}

@Serializable
class Clouds {
    var perc: Long = 0
}


@Serializable
class HikeData {

    var geometry: HikeGeometry? = null
    val name: String? = null
}

@Serializable
class HikeGeometry {
    var location: HikeLocation? = null
}

@Serializable
class HikeLocation {
    var lat: Double? = null
    var lng: Double? = null
}


@Serializable
data class ExtendableFragmentData(
    var clsName: String? = null,
    var mainText: String? = null,
    var subText: String? = null,
    var icon: Int? = null,
    var permissions: Array<String>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExtendableFragmentData

        if (clsName != other.clsName) return false
        if (mainText != other.mainText) return false
        if (subText != other.subText) return false
        if (icon != other.icon) return false
        if (!permissions.contentEquals(other.permissions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clsName?.hashCode() ?: 0
        result = 31 * result + (mainText?.hashCode() ?: 0)
        result = 31 * result + (subText?.hashCode() ?: 0)
        result = 31 * result + (icon ?: 0)
        result = 31 * result + permissions.contentHashCode()
        return result
    }
}


object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) =
        encoder.encodeString(value.toString())

    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(decoder.decodeString())

}

// convert from bitmap to byte array
fun getBytesFromBitmap(bitmap: Bitmap): ByteArray? {
    val stream = ByteArrayOutputStream()
    bitmap.compress(CompressFormat.JPEG, 70, stream)
    return stream.toByteArray()
}


object BitmapSerializer : KSerializer<Bitmap> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Bitmap", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Bitmap) = encoder.encodeString(
        Base64.encodeToString(
            getBytesFromBitmap(value),
            Base64.NO_WRAP
        )
    )

    override fun deserialize(decoder: Decoder): Bitmap {
        val data = Base64.decode(decoder.decodeString(), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }
}

fun encodeBitMap(bitmap: Bitmap): String {
    return Json.encodeToString(BitmapSerializer, bitmap)
}

fun encodeBitMapFromString(input: String): Bitmap {
    return Json.decodeFromString(BitmapSerializer, input)
}

fun encodeLocalDate(localDate: LocalDate): String {
    return Json.encodeToString(LocalDateSerializer, localDate)
}

fun decodeLocalDate(input: String): LocalDate {
    return Json.decodeFromString(LocalDateSerializer, input)
}

fun encodeWeatherData(weatherData: WeatherData): String {
    return Json.encodeToString(WeatherData.serializer(), weatherData)
}

fun decodeWeatherData(input: String): WeatherData {
    return Json.decodeFromString(WeatherData.serializer(), input)
}

fun encodeHikeList(hikeData: List<HikeData>): String {
    var stringStart = "{"
    for (hikeDatum in hikeData) {
        stringStart += encodeHikeData(hikeDatum)
    }
    return "$stringStart}"
}




fun encodeHikeData(hikeData: HikeData): String {
    return Json.encodeToString(HikeData.serializer(), hikeData)
}

fun decodeHikeData(input: String): HikeData {
    return Json.decodeFromString(HikeData.serializer(), input)
}
