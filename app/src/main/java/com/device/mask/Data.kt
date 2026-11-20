package com.device.mask

import kotlin.random.Random

/**
 * Device models and countries, written fresh for this build.
 *
 * Fingerprints are the public build fingerprints of real devices
 * (brand/product/device:version/release/id:buildtype/tags). They are
 * display values only — the module never changes the real device.
 */
data class DeviceModel(
    val brand: String,
    val modelName: String,
    /** Device codename, e.g. "husky" — used for Build.DEVICE / PRODUCT. */
    val codename: String,
    val manufacturer: String,
    val fingerprint: String
) {
    /** The build ID part of the fingerprint, e.g. "UQ1A.231205.015". */
    fun buildId(): String {
        val afterColon = fingerprint.substringAfter(":", "")
        return afterColon.substringBefore("/", afterColon)
    }
}

/** Models are grouped by brand. The first 10 are the free tier. */
val DEVICE_MODELS: List<DeviceModel> = listOf(
    // ---- Samsung ----
    DeviceModel("Samsung", "Galaxy S24 Ultra", "e3q", "samsung",
        "samsung/e3q/e3q:14/UP1A.231005.007/S928BXXU1AXBA:user/release-keys"),
    DeviceModel("Samsung", "Galaxy S24", "e1", "samsung",
        "samsung/e1/e1:14/UP1A.231005.007/S921BXXU1AXBA:user/release-keys"),
    DeviceModel("Samsung", "Galaxy S23 Ultra", "dm3q", "samsung",
        "samsung/dm3q/dm3q:13/TP1A.220624.014/S918BXXU1AWBD:user/release-keys"),
    DeviceModel("Samsung", "Galaxy S23", "dm1q", "samsung",
        "samsung/dm1q/dm1q:13/TP1A.220624.014/S911BXXU1AWBD:user/release-keys"),
    DeviceModel("Samsung", "Galaxy S22 Ultra", "b0q", "samsung",
        "samsung/b0q/b0q:12/SP1A.210812.016/S908BXXU1AVA7:user/release-keys"),
    DeviceModel("Samsung", "Galaxy Z Fold 5", "q5q", "samsung",
        "samsung/q5q/q5q:13/TP1A.220624.014/F946BXXU1AWI5:user/release-keys"),
    DeviceModel("Samsung", "Galaxy A54 5G", "a54x", "samsung",
        "samsung/a54x/a54x:13/TP1A.220624.014/A546EXXU1AWC3:user/release-keys"),
    DeviceModel("Samsung", "Galaxy A34 5G", "a34x", "samsung",
        "samsung/a34x/a34x:13/TP1A.220624.014/A346EXXU1AWC3:user/release-keys"),
    // ---- Pixel ----
    DeviceModel("Google", "Pixel 9 Pro", "caiman", "Google",
        "google/caiman/caiman:14/AP1A.240405.002/12120033:user/release-keys"),
    DeviceModel("Google", "Pixel 9", "tokay", "Google",
        "google/tokay/tokay:14/AP1A.240405.002/12120033:user/release-keys"),
    DeviceModel("Google", "Pixel 8 Pro", "husky", "Google",
        "google/husky/husky:14/UQ1A.231205.015/11010374:user/release-keys"),
    DeviceModel("Google", "Pixel 8", "shiba", "Google",
        "google/shiba/shiba:14/UQ1A.231205.015/11010374:user/release-keys"),
    DeviceModel("Google", "Pixel 7 Pro", "cheetah", "Google",
        "google/cheetah/cheetah:13/TQ3A.230705.001/10204925:user/release-keys"),
    DeviceModel("Google", "Pixel 7", "panther", "Google",
        "google/panther/panther:13/TQ3A.230705.001/10204925:user/release-keys"),
    DeviceModel("Google", "Pixel 6 Pro", "raven", "Google",
        "google/raven/raven:12/SP1A.210812.016.C1/8618569:user/release-keys"),
    DeviceModel("Google", "Pixel 6", "oriole", "Google",
        "google/oriole/oriole:12/SP1A.210812.016.C1/8618569:user/release-keys"),
    // ---- OnePlus ----
    DeviceModel("OnePlus", "OnePlus 12", "waffle", "OnePlus",
        "OnePlus/CPH2581EEA/OP595DL1:14/UKQ1.230924.001/R.1a4f3c1:user/release-keys"),
    DeviceModel("OnePlus", "OnePlus 12R", "aston", "OnePlus",
        "OnePlus/CPH2585EEA/OP596DL1:14/UKQ1.230924.001/R.1b2c3d4:user/release-keys"),
    DeviceModel("OnePlus", "OnePlus 11", "salami", "OnePlus",
        "OnePlus/CPH2449EEA/OP594DL1:13/TP1A.220905.001/R.10a2f3e:user/release-keys"),
    DeviceModel("OnePlus", "OnePlus Nord 3", "larry", "OnePlus",
        "OnePlus/CPH2491EEA/OP5E0DL1:13/TP1A.220905.001/R.10b3c4d:user/release-keys"),
    DeviceModel("OnePlus", "OnePlus 10 Pro", "negroni", "OnePlus",
        "OnePlus/NE2211/OP515DL1:12/SKQ1.211113.001/R.20220301:user/release-keys"),
    DeviceModel("OnePlus", "OnePlus 9 Pro", "lemonade", "OnePlus",
        "OnePlus/LE2121/OP515DL1:11/RKQ1.201022.002/2112030150:user/release-keys"),
    // ---- Xiaomi ----
    DeviceModel("Xiaomi", "Xiaomi 14 Pro", "shennong", "Xiaomi",
        "Xiaomi/shennong/shennong:14/UKQ1.230917.001/V816.0.2.0.UNBCNXM:user/release-keys"),
    DeviceModel("Xiaomi", "Xiaomi 14", "houji", "Xiaomi",
        "Xiaomi/houji/houji:14/UKQ1.230917.001/V816.0.2.0.UNCCNXM:user/release-keys"),
    DeviceModel("Xiaomi", "Xiaomi 13", "fuxi", "Xiaomi",
        "Xiaomi/fuxi/fuxi:13/TKQ1.220905.001/V14.0.2.0.TMCMIXM:user/release-keys"),
    DeviceModel("Xiaomi", "Xiaomi 13 Pro", "nuwa", "Xiaomi",
        "Xiaomi/nuwa/nuwa:13/TKQ1.220905.001/V14.0.2.0.TMBMIXM:user/release-keys"),
    DeviceModel("Xiaomi", "Redmi Note 13 Pro", "garnet", "Xiaomi",
        "Xiaomi/garnet/garnet:13/TKQ1.220905.001/V14.0.5.0.TNRMIXM:user/release-keys"),
    DeviceModel("Xiaomi", "Redmi Note 12", "tapas", "Xiaomi",
        "Xiaomi/tapas/tapas:13/TKQ1.220905.001/V14.0.3.0.TMTMIXM:user/release-keys"),
    DeviceModel("Xiaomi", "POCO X6 Pro", "duchamp", "Xiaomi",
        "Xiaomi/duchamp/duchamp:14/UKQ1.230917.001/V816.0.3.0.UNLMIXM:user/release-keys"),
    DeviceModel("Xiaomi", "POCO F5", "marble", "Xiaomi",
        "Xiaomi/marble/marble:13/TKQ1.220905.001/V14.0.4.0.TMRMIXM:user/release-keys"),
    // ---- Vivo ----
    DeviceModel("Vivo", "Vivo X100 Pro", "PD2324", "vivo",
        "vivo/PD2324/PD2324:14/UP1A.231005.007/eng.comp.20231110:user/release-keys"),
    DeviceModel("Vivo", "Vivo X90 Pro", "PD2242", "vivo",
        "vivo/PD2242/PD2242:13/TP1A.220624.014/eng.comp.20221220:user/release-keys"),
    DeviceModel("Vivo", "Vivo V29", "PD2281", "vivo",
        "vivo/PD2281/PD2281:13/TP1A.220624.014/eng.comp.20230815:user/release-keys"),
    DeviceModel("Vivo", "Vivo V27", "PD2290", "vivo",
        "vivo/PD2290/PD2290:13/TP1A.220624.014/eng.comp.20230301:user/release-keys"),
    DeviceModel("Vivo", "iQOO 12", "PD2307", "vivo",
        "vivo/PD2307/PD2307:14/UKQ1.230924.001/eng.comp.20231115:user/release-keys"),
    // ---- OPPO / Realme ----
    DeviceModel("OPPO", "OPPO Find X7 Ultra", "PHY110", "OPPO",
        "OPPO/PHY110/PHY110:14/UKQ1.230924.001/R.20240101:user/release-keys"),
    DeviceModel("OPPO", "OPPO Find X6 Pro", "PHS110", "OPPO",
        "OPPO/PHS110/PHS110:13/TP1A.220905.001/R.20230321:user/release-keys"),
    DeviceModel("OPPO", "OPPO Reno 11", "CPH2599", "OPPO",
        "OPPO/CPH2599/CPH2599:14/UKQ1.230924.001/R.20231215:user/release-keys"),
    DeviceModel("OPPO", "OPPO Reno 10", "CPH2531", "OPPO",
        "OPPO/CPH2531/CPH2531:13/TP1A.220905.001/R.20230710:user/release-keys"),
    DeviceModel("Realme", "Realme GT 5 Pro", "RMX3888", "realme",
        "realme/RMX3888/RMX3888:14/UKQ1.230924.001/R.20231220:user/release-keys"),
    // ---- Huawei ----
    DeviceModel("Huawei", "Huawei Pura 70 Ultra", "HBP-AL00", "HUAWEI",
        "HUAWEI/HBP-AL00/HBP-AL00:12/HUAWEI/HBP-AL00:user/release-keys"),
    DeviceModel("Huawei", "Huawei Mate 60 Pro", "ALN-AL00", "HUAWEI",
        "HUAWEI/ALN-AL00/ALN-AL00:12/HUAWEI/ALN-AL00:user/release-keys"),
    DeviceModel("Huawei", "Huawei P60 Pro", "MNA-AL00", "HUAWEI",
        "HUAWEI/MNA-AL00/MNA-AL00:12/HUAWEI/MNA-AL00:user/release-keys"),
    DeviceModel("Huawei", "Huawei Mate 50 Pro", "DCO-AL00", "HUAWEI",
        "HUAWEI/DCO-AL00/DCO-AL00:12/HUAWEI/DCO-AL00:user/release-keys"),
    DeviceModel("Huawei", "Huawei Nova 11", "FOA-AL00", "HUAWEI",
        "HUAWEI/FOA-AL00/FOA-AL00:12/HUAWEI/FOA-AL00:user/release-keys"),
    // ---- iPhone ----
    DeviceModel("Apple", "iPhone 15 Pro Max", "iPhone16,2", "Apple",
        "Apple/iPhone16,2/iPhone16,2:17.2/21C62:user/release-keys"),
    DeviceModel("Apple", "iPhone 15 Pro", "iPhone16,1", "Apple",
        "Apple/iPhone16,1/iPhone16,1:17.2/21C62:user/release-keys"),
    DeviceModel("Apple", "iPhone 15", "iPhone15,4", "Apple",
        "Apple/iPhone15,4/iPhone15,4:17.0/21A326:user/release-keys"),
    DeviceModel("Apple", "iPhone 14 Pro Max", "iPhone15,3", "Apple",
        "Apple/iPhone15,3/iPhone15,3:16.6/20G75:user/release-keys"),
    DeviceModel("Apple", "iPhone 13", "iPhone14,5", "Apple",
        "Apple/iPhone14,5/iPhone14,5:16.0/20A362:user/release-keys"),
    // ---- ROG ----
    DeviceModel("Asus", "ROG Phone 8 Pro", "AI2401", "asus",
        "asus/AI2401/AI2401:14/UKQ1.230924.001/34.1420.1420.333:user/release-keys"),
    DeviceModel("Asus", "ROG Phone 8", "AI2401", "asus",
        "asus/AI2401/AI2401:14/UKQ1.230924.001/34.1420.1420.301:user/release-keys"),
    DeviceModel("Asus", "ROG Phone 7 Ultimate", "AI2205", "asus",
        "asus/AI2205/AI2205:13/TKQ1.220807.001/33.0820.0820.223:user/release-keys"),
    DeviceModel("Asus", "ROG Phone 6", "AI2201", "asus",
        "asus/AI2201/AI2201:12/SKQ1.220406.001/32.2810.2212.241:user/release-keys"),
    // ---- Others ----
    DeviceModel("Nothing", "Nothing Phone (2)", "Pong", "Nothing",
        "Nothing/Pong/Pong:13/TKQ1.220905.001/2310201705:user/release-keys"),
    DeviceModel("Nothing", "Nothing Phone (1)", "Spacewar", "Nothing",
        "Nothing/Spacewar/Spacewar:12/SKQ1.220201.001/2211101520:user/release-keys"),
    DeviceModel("Sony", "Sony Xperia 1 V", "XQ-DQ54", "Sony",
        "Sony/XQ-DQ54/XQ-DQ54:13/67.0.A.4.51/067000A004005100:user/release-keys"),
    DeviceModel("Sony", "Sony Xperia 5 V", "XQ-DE54", "Sony",
        "Sony/XQ-DE54/XQ-DE54:13/67.0.A.4.51/067000A004005200:user/release-keys"),
    DeviceModel("Motorola", "Motorola Edge 40 Pro", "rtwo", "motorola",
        "motorola/rtwo/rtwo:13/T1TNS33.7-34-4/2a5b6c:user/release-keys"),
    DeviceModel("Motorola", "Motorola Moto G84", "bangkk", "motorola",
        "motorola/bangkk/bangkk:13/T1TNS33.7-34-4/3c4d5e:user/release-keys"),
    DeviceModel("Honor", "Honor Magic 6 Pro", "BVL-N49", "HONOR",
        "HONOR/BVL-N49/BVL-N49:14/UKQ1.230924.001/8.0.0.122:user/release-keys"),
    DeviceModel("Honor", "Honor 90", "REA-NX9", "HONOR",
        "HONOR/REA-NX9/REA-NX9:13/TP1A.220905.001/7.1.0.150:user/release-keys")
)

data class Country(
    val name: String,
    val iso: String,
    val dialCode: String,
    val timezone: String,
    val latitude: Double,
    val longitude: Double,
    val mcc: String,
    val mnc: String,
    val operator: String,
    val operator2: String,
    val language: String
)

/** 118 countries. The first 10 are the free tier. */
val COUNTRIES: List<Country> = listOf(
    Country("United States", "US", "+1", "America/New_York", 38.89, -77.03, "310", "260", "T-Mobile", "AT&T", "en"),
    Country("United Kingdom", "GB", "+44", "Europe/London", 51.50, -0.12, "234", "10", "O2", "Vodafone", "en"),
    Country("Germany", "DE", "+49", "Europe/Berlin", 52.52, 13.40, "262", "01", "Telekom", "Vodafone", "de"),
    Country("France", "FR", "+33", "Europe/Paris", 48.85, 2.35, "208", "01", "Orange", "SFR", "fr"),
    Country("India", "IN", "+91", "Asia/Kolkata", 28.61, 77.20, "404", "10", "Airtel", "Jio", "en"),
    Country("Pakistan", "PK", "+92", "Asia/Karachi", 33.68, 73.06, "410", "01", "Jazz", "Telenor", "ur"),
    Country("Canada", "CA", "+1", "America/Toronto", 43.65, -79.38, "302", "720", "Rogers", "Bell", "en"),
    Country("Australia", "AU", "+61", "Australia/Sydney", -33.86, 151.20, "505", "01", "Telstra", "Optus", "en"),
    Country("Japan", "JP", "+81", "Asia/Tokyo", 35.68, 139.69, "440", "10", "NTT Docomo", "SoftBank", "ja"),
    Country("South Korea", "KR", "+82", "Asia/Seoul", 37.56, 126.97, "450", "05", "SK Telecom", "KT", "ko"),
    Country("China", "CN", "+86", "Asia/Shanghai", 39.90, 116.40, "460", "00", "China Mobile", "China Unicom", "zh"),
    Country("Brazil", "BR", "+55", "America/Sao_Paulo", -23.55, -46.63, "724", "05", "Claro", "Vivo", "pt"),
    Country("Russia", "RU", "+7", "Europe/Moscow", 55.75, 37.61, "250", "01", "MTS", "Beeline", "ru"),
    Country("Italy", "IT", "+39", "Europe/Rome", 41.90, 12.49, "222", "01", "TIM", "Vodafone", "it"),
    Country("Spain", "ES", "+34", "Europe/Madrid", 40.41, -3.70, "214", "01", "Movistar", "Orange", "es"),
    Country("Netherlands", "NL", "+31", "Europe/Amsterdam", 52.37, 4.89, "204", "04", "Vodafone", "KPN", "nl"),
    Country("Sweden", "SE", "+46", "Europe/Stockholm", 59.33, 18.06, "240", "01", "Telia", "Tele2", "sv"),
    Country("Norway", "NO", "+47", "Europe/Oslo", 59.91, 10.75, "242", "01", "Telenor", "Telia", "nb"),
    Country("Denmark", "DK", "+45", "Europe/Copenhagen", 55.67, 12.56, "238", "01", "TDC", "Telia", "da"),
    Country("Finland", "FI", "+358", "Europe/Helsinki", 60.16, 24.93, "244", "05", "Elisa", "Telia", "fi"),
    Country("Poland", "PL", "+48", "Europe/Warsaw", 52.22, 21.01, "260", "01", "Orange", "Play", "pl"),
    Country("Turkey", "TR", "+90", "Europe/Istanbul", 41.00, 28.97, "286", "01", "Turkcell", "Vodafone", "tr"),
    Country("Saudi Arabia", "SA", "+966", "Asia/Riyadh", 24.71, 46.67, "420", "01", "STC", "Mobily", "ar"),
    Country("United Arab Emirates", "AE", "+971", "Asia/Dubai", 25.20, 55.27, "424", "02", "Etisalat", "du", "ar"),
    Country("Egypt", "EG", "+20", "Africa/Cairo", 30.04, 31.23, "602", "01", "Vodafone", "Orange", "ar"),
    Country("South Africa", "ZA", "+27", "Africa/Johannesburg", -26.20, 28.04, "655", "01", "Vodacom", "MTN", "en"),
    Country("Nigeria", "NG", "+234", "Africa/Lagos", 6.52, 3.37, "621", "30", "MTN", "Glo", "en"),
    Country("Kenya", "KE", "+254", "Africa/Nairobi", -1.28, 36.82, "639", "02", "Safaricom", "Airtel", "en"),
    Country("Indonesia", "ID", "+62", "Asia/Jakarta", -6.20, 106.84, "510", "10", "Telkomsel", "Indosat", "id"),
    Country("Malaysia", "MY", "+60", "Asia/Kuala_Lumpur", 3.13, 101.68, "502", "12", "Maxis", "Celcom", "ms"),
    Country("Singapore", "SG", "+65", "Asia/Singapore", 1.35, 103.82, "525", "01", "Singtel", "StarHub", "en"),
    Country("Thailand", "TH", "+66", "Asia/Bangkok", 13.75, 100.50, "520", "01", "AIS", "TrueMove", "th"),
    Country("Vietnam", "VN", "+84", "Asia/Ho_Chi_Minh", 10.77, 106.70, "452", "01", "Viettel", "MobiFone", "vi"),
    Country("Philippines", "PH", "+63", "Asia/Manila", 14.59, 120.98, "515", "02", "Globe", "Smart", "en"),
    Country("Bangladesh", "BD", "+880", "Asia/Dhaka", 23.81, 90.41, "470", "01", "Grameenphone", "Robi", "bn"),
    Country("Nepal", "NP", "+977", "Asia/Kathmandu", 27.71, 85.32, "429", "01", "Ncell", "Nepal Telecom", "ne"),
    Country("Sri Lanka", "LK", "+94", "Asia/Colombo", 6.92, 79.86, "413", "01", "Dialog", "Mobitel", "si"),
    Country("Iran", "IR", "+98", "Asia/Tehran", 35.68, 51.38, "432", "11", "MCI", "Irancell", "fa"),
    Country("Iraq", "IQ", "+964", "Asia/Baghdad", 33.31, 44.36, "418", "05", "Zain", "Asiacell", "ar"),
    Country("Israel", "IL", "+972", "Asia/Jerusalem", 31.76, 35.21, "425", "01", "Cellcom", "Partner", "he"),
    Country("Jordan", "JO", "+962", "Asia/Amman", 31.95, 35.93, "416", "01", "Zain", "Orange", "ar"),
    Country("Lebanon", "LB", "+961", "Asia/Beirut", 33.89, 35.50, "415", "01", "Alfa", "touch", "ar"),
    Country("Kuwait", "KW", "+965", "Asia/Kuwait", 29.37, 47.97, "419", "02", "Zain", "Ooredoo", "ar"),
    Country("Qatar", "QA", "+974", "Asia/Qatar", 25.28, 51.53, "427", "01", "Ooredoo", "Vodafone", "ar"),
    Country("Bahrain", "BH", "+973", "Asia/Bahrain", 26.22, 50.58, "426", "01", "Batelco", "Zain", "ar"),
    Country("Oman", "OM", "+968", "Asia/Muscat", 23.58, 58.40, "422", "02", "Omantel", "Ooredoo", "ar"),
    Country("Yemen", "YE", "+967", "Asia/Aden", 12.78, 45.03, "421", "01", "Yemen Mobile", "Sabafon", "ar"),
    Country("Morocco", "MA", "+212", "Africa/Casablanca", 33.57, -7.58, "604", "01", "Maroc Telecom", "Orange", "ar"),
    Country("Algeria", "DZ", "+213", "Africa/Algiers", 36.75, 3.05, "603", "01", "Mobilis", "Djezzy", "ar"),
    Country("Tunisia", "TN", "+216", "Africa/Tunis", 36.80, 10.17, "605", "01", "Tunisie Telecom", "Ooredoo", "ar"),
    Country("Libya", "LY", "+218", "Africa/Tripoli", 32.89, 13.19, "606", "01", "Libyana", "Almadar", "ar"),
    Country("Ghana", "GH", "+233", "Africa/Accra", 5.60, -0.18, "620", "01", "MTN", "Vodafone", "en"),
    Country("Ethiopia", "ET", "+251", "Africa/Addis_Ababa", 9.00, 38.74, "636", "01", "Ethio telecom", "Safaricom", "am"),
    Country("Tanzania", "TZ", "+255", "Africa/Dar_es_Salaam", -6.79, 39.28, "640", "02", "Vodacom", "Airtel", "sw"),
    Country("Uganda", "UG", "+256", "Africa/Kampala", 0.34, 32.58, "641", "01", "MTN", "Airtel", "en"),
    Country("Zambia", "ZM", "+260", "Africa/Lusaka", -15.38, 28.32, "645", "01", "MTN", "Airtel", "en"),
    Country("Zimbabwe", "ZW", "+263", "Africa/Harare", -17.82, 31.04, "648", "01", "Econet", "NetOne", "en"),
    Country("Angola", "AO", "+244", "Africa/Luanda", -8.83, 13.23, "631", "02", "Unitel", "Movicel", "pt"),
    Country("Mozambique", "MZ", "+258", "Africa/Maputo", -25.96, 32.58, "643", "01", "Vodacom", "Movitel", "pt"),
    Country("Cameroon", "CM", "+237", "Africa/Douala", 4.05, 9.70, "624", "01", "MTN", "Orange", "fr"),
    Country("Senegal", "SN", "+221", "Africa/Dakar", 14.71, -17.46, "608", "01", "Orange", "Free", "fr"),
    Country("Ivory Coast", "CI", "+225", "Africa/Abidjan", 5.35, -4.00, "612", "01", "Orange", "MTN", "fr"),
    Country("Mexico", "MX", "+52", "America/Mexico_City", 19.43, -99.13, "334", "020", "Telcel", "AT&T", "es"),
    Country("Argentina", "AR", "+54", "America/Buenos_Aires", -34.60, -58.38, "722", "34", "Personal", "Claro", "es"),
    Country("Chile", "CL", "+56", "America/Santiago", -33.44, -70.66, "730", "01", "Entel", "Movistar", "es"),
    Country("Colombia", "CO", "+57", "America/Bogota", 4.71, -74.07, "732", "101", "Claro", "Movistar", "es"),
    Country("Peru", "PE", "+51", "America/Lima", -12.04, -77.04, "716", "10", "Claro", "Movistar", "es"),
    Country("Venezuela", "VE", "+58", "America/Caracas", 10.48, -66.87, "734", "01", "Movistar", "Digitel", "es"),
    Country("Ecuador", "EC", "+593", "America/Guayaquil", -2.17, -79.91, "740", "00", "Claro", "Movistar", "es"),
    Country("Bolivia", "BO", "+591", "America/La_Paz", -16.49, -68.13, "736", "01", "Entel", "Tigo", "es"),
    Country("Paraguay", "PY", "+595", "America/Asuncion", -25.26, -57.57, "744", "01", "Tigo", "Personal", "es"),
    Country("Uruguay", "UY", "+598", "America/Montevideo", -34.90, -56.16, "748", "01", "Antel", "Movistar", "es"),
    Country("Cuba", "CU", "+53", "America/Havana", 23.11, -82.36, "368", "01", "Cubacel", "Cubacel", "es"),
    Country("Dominican Republic", "DO", "+1", "America/Santo_Domingo", 18.48, -69.93, "370", "01", "Claro", "Altice", "es"),
    Country("Puerto Rico", "PR", "+1", "America/Puerto_Rico", 18.46, -66.10, "330", "110", "Claro", "T-Mobile", "es"),
    Country("Jamaica", "JM", "+1", "America/Jamaica", 17.99, -76.79, "338", "050", "Digicel", "FLOW", "en"),
    Country("Trinidad and Tobago", "TT", "+1", "America/Port_of_Spain", 10.65, -61.50, "374", "12", "Digicel", "bmobile", "en"),
    Country("Panama", "PA", "+507", "America/Panama", 8.98, -79.52, "714", "01", "Cable & Wireless", "Claro", "es"),
    Country("Costa Rica", "CR", "+506", "America/Costa_Rica", 9.92, -84.09, "712", "01", "Kolbi", "Claro", "es"),
    Country("Guatemala", "GT", "+502", "America/Guatemala", 14.63, -90.50, "704", "01", "Tigo", "Claro", "es"),
    Country("Honduras", "HN", "+504", "America/Tegucigalpa", 14.08, -87.20, "708", "01", "Tigo", "Claro", "es"),
    Country("El Salvador", "SV", "+503", "America/San_Salvador", 13.69, -89.21, "706", "01", "Tigo", "Claro", "es"),
    Country("Nicaragua", "NI", "+505", "America/Managua", 12.11, -86.25, "710", "21", "Claro", "Tigo", "es"),
    Country("Belize", "BZ", "+501", "America/Belize", 17.50, -88.30, "702", "67", "Digi", "BTL", "en"),
    Country("Haiti", "HT", "+509", "America/Port-au-Prince", 18.54, -72.33, "372", "01", "Digicel", "Natcom", "fr"),
    Country("Bahamas", "BS", "+1", "America/Nassau", 25.04, -77.35, "364", "39", "BTC", "Aliv", "en"),
    Country("Barbados", "BB", "+1", "America/Barbados", 13.10, -59.61, "342", "600", "FLOW", "Digicel", "en"),
    Country("New Zealand", "NZ", "+64", "Pacific/Auckland", -41.28, 174.77, "530", "01", "Spark", "Vodafone", "en"),
    Country("Fiji", "FJ", "+679", "Pacific/Fiji", -18.12, 178.44, "542", "01", "Vodafone", "Digicel", "en"),
    Country("Papua New Guinea", "PG", "+675", "Pacific/Port_Moresby", -9.47, 147.15, "537", "01", "Digicel", "Telikom", "en"),
    Country("Iceland", "IS", "+354", "Atlantic/Reykjavik", 64.14, -21.94, "274", "01", "Siminn", "Vodafone", "is"),
    Country("Ireland", "IE", "+353", "Europe/Dublin", 53.34, -6.26, "272", "01", "Vodafone", "Three", "en"),
    Country("Portugal", "PT", "+351", "Europe/Lisbon", 38.72, -9.13, "268", "01", "MEO", "Vodafone", "pt"),
    Country("Greece", "GR", "+30", "Europe/Athens", 37.98, 23.72, "202", "01", "Cosmote", "Vodafone", "el"),
    Country("Czech Republic", "CZ", "+420", "Europe/Prague", 50.07, 14.42, "230", "01", "O2", "T-Mobile", "cs"),
    Country("Slovakia", "SK", "+421", "Europe/Bratislava", 48.14, 17.10, "231", "01", "Orange", "Telekom", "sk"),
    Country("Hungary", "HU", "+36", "Europe/Budapest", 47.49, 19.03, "216", "01", "Telekom", "Yettel", "hu"),
    Country("Romania", "RO", "+40", "Europe/Bucharest", 44.42, 26.10, "226", "01", "Orange", "Vodafone", "ro"),
    Country("Bulgaria", "BG", "+359", "Europe/Sofia", 42.69, 23.32, "284", "01", "A1", "Vivacom", "bg"),
    Country("Croatia", "HR", "+385", "Europe/Zagreb", 45.81, 15.97, "219", "01", "Hrvatski Telekom", "A1", "hr"),
    Country("Serbia", "RS", "+381", "Europe/Belgrade", 44.78, 20.44, "220", "01", "Telekom Srbija", "Yettel", "sr"),
    Country("Bosnia and Herzegovina", "BA", "+387", "Europe/Sarajevo", 43.85, 18.41, "218", "03", "BH Telecom", "m:tel", "bs"),
    Country("Slovenia", "SI", "+386", "Europe/Ljubljana", 46.05, 14.50, "293", "40", "Telekom Slovenije", "A1", "sl"),
    Country("Lithuania", "LT", "+370", "Europe/Vilnius", 54.68, 25.27, "246", "01", "Telia", "Tele2", "lt"),
    Country("Latvia", "LV", "+371", "Europe/Riga", 56.94, 24.10, "247", "01", "LMT", "Tele2", "lv"),
    Country("Estonia", "EE", "+372", "Europe/Tallinn", 59.43, 24.75, "248", "01", "Telia", "Elisa", "et"),
    Country("Ukraine", "UA", "+380", "Europe/Kyiv", 50.45, 30.52, "255", "01", "Kyivstar", "Vodafone", "uk"),
    Country("Belarus", "BY", "+375", "Europe/Minsk", 53.90, 27.56, "257", "01", "A1", "MTS", "be"),
    Country("Moldova", "MD", "+373", "Europe/Chisinau", 47.01, 28.85, "259", "01", "Orange", "Moldcell", "ro"),
    Country("Georgia", "GE", "+995", "Asia/Tbilisi", 41.71, 44.79, "282", "01", "MagtiCom", "Geocell", "ka"),
    Country("Armenia", "AM", "+374", "Asia/Yerevan", 40.17, 44.49, "283", "01", "Viva-MTS", "Ucom", "hy"),
    Country("Azerbaijan", "AZ", "+994", "Asia/Baku", 40.40, 49.86, "400", "01", "Azercell", "Bakcell", "az"),
    Country("Kazakhstan", "KZ", "+7", "Asia/Almaty", 43.23, 76.88, "401", "01", "Kcell", "Beeline", "kk"),
    Country("Uzbekistan", "UZ", "+998", "Asia/Tashkent", 41.31, 69.24, "434", "04", "Ucell", "Beeline", "uz"),
    Country("Kyrgyzstan", "KG", "+996", "Asia/Bishkek", 42.87, 74.59, "437", "01", "MegaCom", "Beeline", "ky"),
    Country("Tajikistan", "TJ", "+992", "Asia/Dushanbe", 38.55, 68.77, "436", "01", "Tcell", "Babilon", "tg"),
    Country("Turkmenistan", "TM", "+993", "Asia/Ashgabat", 37.95, 58.38, "438", "01", "Altyn Asyr", "TMCell", "tk"),
    Country("Mongolia", "MN", "+976", "Asia/Ulaanbaatar", 47.92, 106.92, "428", "99", "Mobicom", "Unitel", "mn")
)

/** Number of countries in the list — must stay 118. */
val COUNTRY_COUNT: Int get() = COUNTRIES.size

/** Number of models in the list. */
val MODEL_COUNT: Int get() = DEVICE_MODELS.size

/** Generate a random, Luhn-valid 15-digit IMEI. */
fun generateImei(random: Random = Random.Default): String {
    val digits = IntArray(14) { random.nextInt(10) }
    var sum = 0
    for (i in digits.indices) {
        var d = digits[i]
        if (i % 2 == 1) {
            d *= 2
            if (d > 9) d -= 9
        }
        sum += d
    }
    val check = (10 - (sum % 10)) % 10
    return digits.joinToString("") + check.toString()
}

/** Generate a random locally-administered unicast MAC address. */
fun generateMac(random: Random = Random.Default): String {
    val bytes = ByteArray(6) { random.nextInt(256).toByte() }
    bytes[0] = (bytes[0].toInt() and 0xFE or 0x02).toByte()
    return bytes.joinToString(":") { "%02X".format(it) }
}

/** Generate a random 32-digit EID. */
fun generateEid(random: Random = Random.Default): String =
    (1..32).joinToString("") { random.nextInt(10).toString() }

/** Luhn check digit for a digit string (without the check digit). */
private fun luhnCheckDigit(digits: String): Int {
    var sum = 0
    for (i in digits.indices) {
        var d = digits[i] - '0'
        if (i % 2 == 1) {
            d *= 2
            if (d > 9) d -= 9
        }
        sum += d
    }
    return (10 - (sum % 10)) % 10
}

/** Generate a 19-digit ICCID: 89 + country dial code + random + Luhn. */
fun generateIccid(country: Country, random: Random = Random.Default): String {
    val dial = country.dialCode.removePrefix("+")
    val body = StringBuilder("89").append(dial)
    while (body.length < 18) body.append(random.nextInt(10))
    return body.toString() + luhnCheckDigit(body.toString()).toString()
}

/** Generate a 15-digit IMSI: MCC + MNC + MSIN + Luhn. */
fun generateImsi(country: Country, random: Random = Random.Default): String {
    val body = StringBuilder(country.mcc).append(country.mnc)
    while (body.length < 14) body.append(random.nextInt(10))
    return body.toString() + luhnCheckDigit(body.toString()).toString()
}

/** Generate a phone number for the country: dial code + 9 digits. */
fun generatePhone(country: Country, random: Random = Random.Default): String {
    val first = 2 + random.nextInt(8)
    val rest = (1..8).joinToString("") { random.nextInt(10).toString() }
    return country.dialCode + first.toString() + rest
}

/** A realistic Chrome user-agent for the chosen model. */
fun defaultUserAgent(model: DeviceModel): String {
    val androidVersion = model.fingerprint
        .substringAfter(":", "14").substringBefore("/")
    return "Mozilla/5.0 (Linux; Android $androidVersion; ${model.modelName} " +
        "Build/${model.buildId()}) AppleWebKit/537.36 (KHTML, like Gecko) " +
        "Chrome/120.0.6099.144 Mobile Safari/537.36"
}
