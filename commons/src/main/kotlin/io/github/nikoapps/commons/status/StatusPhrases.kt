package io.github.nikoapps.commons.status

import java.util.Locale

/**
 * The phone status as whole sentences, hand-written for each language, as the time phrases are.
 * Warnings first, because they are why someone asks, then battery, signal and internet. No
 * numbers. All but English need a native speaker's check.
 */
class StatusPhrases(
    val locale: Locale,
    warnings: List<String>,
    battery: List<String>,
    private val charging: String,
    signal: List<String>,
    connection: List<String>,
) {
    private val warnings = StatusWarning.entries.zip(warnings).toMap()
    private val battery = BatteryLevel.entries.zip(battery).toMap()
    private val signal = Signal.entries.zip(signal).toMap()
    private val connection = Connection.entries.zip(connection).toMap()

    init {
        require(warnings.size == StatusWarning.entries.size)
        require(battery.size == BatteryLevel.entries.size)
        require(signal.size == Signal.entries.size)
        require(connection.size == Connection.entries.size)
    }

    fun warning(warning: StatusWarning): String = warnings.getValue(warning)

    fun sentence(status: PhoneStatus): String = buildList {
        status.warnings.forEach { add(warning(it)) }
        status.battery?.let { add(battery.getValue(it)) }
        if (status.charging) add(charging)
        status.signal?.let { add(signal.getValue(it)) }
        status.connection?.let { add(connection.getValue(it)) }
    }.joinToString(" ")
}

/** The status sentences for [locale]'s language, or null when there are none yet. */
fun statusPhrasesFor(locale: Locale): StatusPhrases? = when (locale.language) {
    "en" -> StatusPhrases(
        Locale.ENGLISH,
        warnings = listOf(
            "Airplane mode is on. Calls and messages can't come in.",
            "There is no SIM card.",
            "Do Not Disturb is on. Calls may not ring.",
            "The ringer is on silent.",
            "The ringer is on vibrate only.",
            "The ring volume is all the way down.",
        ),
        battery = listOf(
            "The battery is full.",
            "The battery is almost full.",
            "The battery is about half full.",
            "The battery is low.",
            "The battery is very low. Please charge the phone.",
        ),
        charging = "It is charging.",
        signal = listOf("There is no phone signal.", "The phone signal is weak.", "The phone signal is good.", "The phone signal is strong."),
        connection = listOf("Connected to Wi-Fi.", "Using mobile data.", "No internet."),
    )
    "ne" -> StatusPhrases(
        Locale.forLanguageTag("ne-NP"),
        warnings = listOf(
            "एयरप्लेन मोड खुला छ। कल र सन्देश आउन सक्दैनन्।",
            "सिम कार्ड छैन।",
            "डु नट डिस्टर्ब खुला छ। कल आउँदा घन्टी नबज्न सक्छ।",
            "घन्टी बन्द छ।",
            "घन्टी बन्द छ, कम्पन मात्र हुन्छ।",
            "घन्टीको आवाज पूरै घटाइएको छ।",
        ),
        battery = listOf(
            "ब्याट्री पूरा भरिएको छ।",
            "ब्याट्री लगभग भरिएको छ।",
            "ब्याट्री आधा जति छ।",
            "ब्याट्री कम छ।",
            "ब्याट्री धेरै कम छ। फोन चार्ज गर्नुहोस्।",
        ),
        charging = "चार्ज हुँदैछ।",
        signal = listOf("फोनको सिग्नल छैन।", "फोनको सिग्नल कमजोर छ।", "फोनको सिग्नल राम्रो छ।", "फोनको सिग्नल बलियो छ।"),
        connection = listOf("वाइफाइमा जोडिएको छ।", "मोबाइल डाटा चलिरहेको छ।", "इन्टरनेट छैन।"),
    )
    "hi" -> StatusPhrases(
        Locale.forLanguageTag("hi-IN"),
        warnings = listOf(
            "हवाई जहाज़ मोड चालू है। कॉल और संदेश नहीं आ सकते।",
            "सिम कार्ड नहीं है।",
            "परेशान न करें चालू है। कॉल आने पर घंटी शायद न बजे।",
            "घंटी बंद है।",
            "घंटी बंद है, सिर्फ़ वाइब्रेशन होगा।",
            "घंटी की आवाज़ पूरी कम है।",
        ),
        battery = listOf(
            "बैटरी पूरी भरी है।",
            "बैटरी लगभग भरी है।",
            "बैटरी लगभग आधी है।",
            "बैटरी कम है।",
            "बैटरी बहुत कम है। फ़ोन चार्ज करें।",
        ),
        charging = "चार्ज हो रहा है।",
        signal = listOf("फ़ोन का सिग्नल नहीं है।", "फ़ोन का सिग्नल कमज़ोर है।", "फ़ोन का सिग्नल अच्छा है।", "फ़ोन का सिग्नल मज़बूत है।"),
        connection = listOf("वाई-फ़ाई से जुड़ा है।", "मोबाइल डेटा चल रहा है।", "इंटरनेट नहीं है।"),
    )
    "es" -> StatusPhrases(
        Locale.forLanguageTag("es"),
        warnings = listOf(
            "El modo avión está activado. No pueden entrar llamadas ni mensajes.",
            "No hay tarjeta SIM.",
            "No molestar está activado. Puede que las llamadas no suenen.",
            "El timbre está en silencio.",
            "El timbre está solo en vibración.",
            "El volumen del timbre está al mínimo.",
        ),
        battery = listOf(
            "La batería está llena.",
            "La batería está casi llena.",
            "La batería está más o menos a la mitad.",
            "La batería está baja.",
            "La batería está muy baja. Cargue el teléfono.",
        ),
        charging = "Se está cargando.",
        signal = listOf("No hay señal.", "La señal es débil.", "La señal es buena.", "La señal es fuerte."),
        connection = listOf("Conectado a wifi.", "Usando datos móviles.", "No hay internet."),
    )
    "pt" -> StatusPhrases(
        Locale.forLanguageTag("pt-BR"),
        warnings = listOf(
            "O modo avião está ligado. Chamadas e mensagens não podem chegar.",
            "Não há chip.",
            "Não perturbe está ligado. As chamadas podem não tocar.",
            "A campainha está no silencioso.",
            "A campainha está só no vibrar.",
            "O volume da campainha está no mínimo.",
        ),
        battery = listOf(
            "A bateria está cheia.",
            "A bateria está quase cheia.",
            "A bateria está mais ou menos na metade.",
            "A bateria está fraca.",
            "A bateria está muito fraca. Carregue o celular.",
        ),
        charging = "Está carregando.",
        signal = listOf("Não há sinal.", "O sinal está fraco.", "O sinal está bom.", "O sinal está forte."),
        connection = listOf("Conectado ao wi-fi.", "Usando dados móveis.", "Sem internet."),
    )
    "fr" -> StatusPhrases(
        Locale.forLanguageTag("fr-FR"),
        warnings = listOf(
            "Le mode avion est activé. Les appels et les messages ne peuvent pas arriver.",
            "Il n'y a pas de carte SIM.",
            "Ne pas déranger est activé. Les appels risquent de ne pas sonner.",
            "La sonnerie est en silencieux.",
            "La sonnerie est en vibreur seulement.",
            "Le volume de la sonnerie est au minimum.",
        ),
        battery = listOf(
            "La batterie est pleine.",
            "La batterie est presque pleine.",
            "La batterie est à moitié environ.",
            "La batterie est faible.",
            "La batterie est très faible. Mettez le téléphone en charge.",
        ),
        charging = "Il est en charge.",
        signal = listOf("Il n'y a pas de réseau.", "Le réseau est faible.", "Le réseau est bon.", "Le réseau est fort."),
        connection = listOf("Connecté au wifi.", "Utilise les données mobiles.", "Pas d'internet."),
    )
    // Android still calls Indonesian "in"; newer code says "id".
    "in", "id" -> StatusPhrases(
        Locale.forLanguageTag("id-ID"),
        warnings = listOf(
            "Mode pesawat aktif. Panggilan dan pesan tidak bisa masuk.",
            "Tidak ada kartu SIM.",
            "Jangan Ganggu aktif. Panggilan mungkin tidak berdering.",
            "Dering dalam mode senyap.",
            "Dering hanya bergetar.",
            "Volume dering paling kecil.",
        ),
        battery = listOf(
            "Baterai penuh.",
            "Baterai hampir penuh.",
            "Baterai sekitar setengah.",
            "Baterai lemah.",
            "Baterai sangat lemah. Tolong isi daya ponsel.",
        ),
        charging = "Sedang mengisi daya.",
        signal = listOf("Tidak ada sinyal.", "Sinyal lemah.", "Sinyal bagus.", "Sinyal kuat."),
        connection = listOf("Tersambung ke Wi-Fi.", "Memakai data seluler.", "Tidak ada internet."),
    )
    "bn" -> StatusPhrases(
        Locale.forLanguageTag("bn-BD"),
        warnings = listOf(
            "এয়ারপ্লেন মোড চালু আছে। কল আর মেসেজ আসতে পারবে না।",
            "সিম কার্ড নেই।",
            "বিরক্ত করবেন না চালু আছে। কল এলে রিং না-ও বাজতে পারে।",
            "রিংটোন বন্ধ আছে।",
            "রিংটোন বন্ধ, শুধু ভাইব্রেট হবে।",
            "রিংয়ের আওয়াজ একদম কমানো।",
        ),
        battery = listOf(
            "ব্যাটারি পুরো ভরা।",
            "ব্যাটারি প্রায় ভরা।",
            "ব্যাটারি প্রায় অর্ধেক।",
            "ব্যাটারি কম।",
            "ব্যাটারি খুব কম। ফোন চার্জ দিন।",
        ),
        charging = "চার্জ হচ্ছে।",
        signal = listOf("ফোনের নেটওয়ার্ক নেই।", "ফোনের নেটওয়ার্ক দুর্বল।", "ফোনের নেটওয়ার্ক ভালো।", "ফোনের নেটওয়ার্ক খুব ভালো।"),
        connection = listOf("ওয়াই-ফাইতে যুক্ত।", "মোবাইল ডেটা চলছে।", "ইন্টারনেট নেই।"),
    )
    "ur" -> StatusPhrases(
        Locale.forLanguageTag("ur-PK"),
        warnings = listOf(
            "ہوائی جہاز موڈ آن ہے۔ کال اور پیغام نہیں آ سکتے۔",
            "سم کارڈ نہیں ہے۔",
            "ڈسٹرب نہ کریں آن ہے۔ کال آنے پر گھنٹی شاید نہ بجے۔",
            "گھنٹی بند ہے۔",
            "گھنٹی بند ہے، صرف وائبریشن ہوگی۔",
            "گھنٹی کی آواز بالکل کم ہے۔",
        ),
        battery = listOf(
            "بیٹری پوری بھری ہے۔",
            "بیٹری تقریباً بھری ہے۔",
            "بیٹری تقریباً آدھی ہے۔",
            "بیٹری کم ہے۔",
            "بیٹری بہت کم ہے۔ فون چارج کریں۔",
        ),
        charging = "چارج ہو رہا ہے۔",
        signal = listOf("فون کا سگنل نہیں ہے۔", "فون کا سگنل کمزور ہے۔", "فون کا سگنل اچھا ہے۔", "فون کا سگنل مضبوط ہے۔"),
        connection = listOf("وائی فائی سے جڑا ہے۔", "موبائل ڈیٹا چل رہا ہے۔", "انٹرنیٹ نہیں ہے۔"),
    )
    else -> null
}
