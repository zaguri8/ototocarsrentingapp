
package com.example.ototocarsrentingapp.auth.Validator;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.ototocarsrentingapp.model.CarManufacturer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Validator {
    // Map שמכיל את כל הדגמים לפי יצרן
    private static final Map<CarManufacturer, List<String>> validModelsByMake = new HashMap<>();
    private static final Map<String, List<String>> validTrimsByModel = new HashMap<>();
    //"^" אומר שהבדיקה מתחילה מהתו הראשון
    //"[]" קבוצת תווים שיכולה להפויעA-Z
    //"$" סוף המחרוזת
    //"*" צריך להופיע 0 או יותר פעמים
    //"+" צריך להופיע פעם אחת לפחות
    //"{n}" צריך להופיע בדיוק n פעמים
    //"{n,m}" צריך להופיע בין N לM פעמים
    //"?=" חייב להכיל
    //"?!" אסור להכיל

    //regex לכל תכונה user
    //============================================================

    //שם פרטי
    private static final String nameRegex = "^[א-תA-Za-z]{2,20}$";
    //שם משפחה
    private static final String familyNameRegex =  "^[א-תA-Za-z]{2,20}$";

    // אימייל: חייב @, נקודה בדומיין, ללא רווחים
    private static final String emailRegex =  "^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$";

    //מספר טלפון
    private static final String phoneNumRegex = "^05[0-9]{8}$";

    //תאריך לידה
    private static final String birthDateRegex = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/([0-9]{4})$";

    // כתובת: אותיות, מספרים, רווחים, נקודה, פסיק, מינוס; 5-100 תווים
    private static final String addressRegex =
            "^[A-Za-z0-9א-ת\\s.,\\-]{5,100}$";

    // עיר: אותיות עבריות ורווחים; 2-50 תווים
    private static final String cityRegex =  "^[א-תA-Za-z]{2,20}$";

    // מיקוד: 5 ספרות

    // סיסמה חזקה: לפחות 8 תווים, אות גדולה, ספרה, תו מיוחד, ללא רווחים
    private static final String passwordRegex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=\\S+$).{8,}$";

    //============================================================
    //regex לכל תכונה renter
    private static final String driverLicense = "^\\d{7,8}$";//מספר רישיון רכב של הנהג

    //============================================================
    //regex לכל תכונה Seller

    // מספר רישוי ישראלי: 7–8 ספרות בלבד
    public static final String LICENSE_PLATE_REGEX = "^\\d{7,9}$";//לוחית רישוי

    // קילומטרים: מספר שלם חיובי (לא מתחיל ב־0)
    public static final String KILOMETERS_REGEX = "^[1-9]\\d*(?:[.,]\\d+)*$";
    // מספר מושבים: ספרה אחת בין 1 ל־9
    public static final String SEATS_NUMBER_REGEX = "^[1-7]$";

    // שנת רכב: טווח סביר של שנים (1950–2029)
    public static final String YEAR_REGEX = "^(19[5-9]\\d|20[0-2]\\d)$";


    //מתודות ולידציה לכל תכונה
    //============================================================

    //מתודה שבודקת אם השם תקין
    public static ValidationResult validateFirstName(String firstName) {
        if (firstName == null) {
            return new ValidationResult("נא להזין שם פרטי", false);
        }

        firstName = firstName.trim();
        if (firstName.isEmpty()) {
            return new ValidationResult("נא להזין שם פרטי", false);
        }

        if (!firstName.matches(nameRegex)) {
            return new ValidationResult("שם פרטי לא תקין (2–20 אותיות בעברית)", false);
        }

        return new ValidationResult(null, true);
    }
    //מתודה שבודקת אם שם המשפחה תקין
    public static ValidationResult validateLastName(String lastName) {
        if (lastName == null) {
            return new ValidationResult("נא להזין שם משפחה", false);
        }

        lastName = lastName.trim();
        if (lastName.isEmpty()) {
            return new ValidationResult("נא להזין שם משפחה", false);
        }

        if (!lastName.matches(familyNameRegex)) {
            return new ValidationResult("שם משפחה לא תקין (2–20 אותיות בעברית)", false);
        }

        return new ValidationResult(null, true);
    }

    //מתודה שבודקת האםם התאריך לידה תקין
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ValidationResult validateBirthDate(String birthDate) {
        if (birthDate == null) {
            return new ValidationResult("נא להזין תאריך לידה", false);
        }
        birthDate = birthDate.trim();
        if (birthDate.isEmpty()) {
            return new ValidationResult("נא להזין תאריך לידה", false);
        }
        if (!birthDate.matches(birthDateRegex)) {
            return new ValidationResult("תאריך לידה לא תקין: יש להזין בפורמט DD/MM/YYYY (לדוגמה 05/11/2008)", false);
        }

        // 6) בדיקת "תאריך אמיתי" באמצעות LocalDate + STRICT
        //    STRICT אומר: אל תסלח על תאריכים לא קיימים כמו 31/02/2008
        try {
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

            LocalDate date = LocalDate.parse(birthDate, formatter);

            int year = date.getYear();
            int currentYear = LocalDate.now().getYear();

            if (year < 1900 || year > currentYear) {
                return new ValidationResult("שנת לידה לא סבירה", false);
            }

        } catch (DateTimeParseException e) {
            // אם parse נכשל -> זה לא תאריך אמיתי
            return new ValidationResult("תאריך לידה לא תקין", false);
        }

        // אם עבר הכל - תקין
        return new ValidationResult(null, true);
    }

    //מתודה שבודקת האם האימייל תקין
    public static ValidationResult validateEmail(String email) {
        if (email == null) return new ValidationResult("נא להזין אימייל", false);

        email = email.trim();
        if (email.isEmpty()) return new ValidationResult("נא להזין אימייל", false);
        if (email.contains(" ")) return new ValidationResult("אימייל לא יכול להכיל רווחים", false);
        if (!email.matches(emailRegex)) return new ValidationResult("אימייל לא תקין: יש להזין כתובת בפורמט name@example.com", false);

        return new ValidationResult(null, true);
    }

    //מתודה שבודקת האם המספר טלפון תקין
    public static ValidationResult validatePhone(String phone) {
        if (phone == null) return new ValidationResult("נא להזין מספר טלפון", false);

        phone = phone.trim();
        if (phone.isEmpty()) return new ValidationResult("נא להזין מספר טלפון", false);

        phone = phone.replaceAll("[\\s-]", "");
        if (!phone.matches(phoneNumRegex))  return new ValidationResult("מספר טלפון לא תקין: יש להזין מספר נייד ישראלי בן 10 ספרות שמתחיל ב-05 ", false);

        return new ValidationResult(null, true);
    }

    //מתודה שבודקת האם הכתובת תקינה
    public static ValidationResult validateAddress(String address) {
        if (address == null) return new ValidationResult("נא להזין כתובת", false);

        address = address.trim();
        if (address.isEmpty())  return new ValidationResult("כתובת לא תקינה: יש להזין 5–100 ", false);

        // מנרמל רווחים מרובים לרווח אחד
        address = address.replaceAll("\\s+", " ");

        if (!address.matches(addressRegex)) return new ValidationResult("כתובת לא תקינה", false);

        return new ValidationResult(null, true);
    }

    //מתודה שבודקת האם העיר תקינה
    public static ValidationResult validateCity(String city) {
        if (city == null) return new ValidationResult("נא להזין עיר", false);

        city = city.trim();
        if (city.isEmpty()) return new ValidationResult("נא להזין עיר", false);

        city = city.replaceAll("\\s+", " ");
        if (!city.matches(cityRegex)) return new ValidationResult("שם העיר לא תקין: יש להזין 2–50 תווים בעברית בלבד (מותר רווחים)", false);

        return new ValidationResult(null, true);
    }

    //מתודה שבודקת האם הסיסמה תקינה
    public static ValidationResult validatePassword(String password) {
        if (password == null) return new ValidationResult("נא להזין סיסמה", false);

        password = password.trim();
        if (password.isEmpty()) return new ValidationResult("נא להזין סיסמה", false);

        if (!password.matches(passwordRegex)) {
            return new ValidationResult("סיסמה לא תקינה: לפחות 8 תווים, אות גדולה, ספרה, תו מיוחד, ללא רווחים", false);
        }

        return new ValidationResult(null, true);
    }

    //מתודה שבודקת האם הסיסמה והאישור סיסמה זהות
    public static ValidationResult validateConfirmPassword(String password, String confirmPassword) {
        if (password == null || password.trim().isEmpty()) {
            return new ValidationResult("נא להזין סיסמה לפני אימות", false);
        }

        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            return new ValidationResult("נא לאשר סיסמה", false);
        }

        password = password.trim();
        confirmPassword = confirmPassword.trim();

        if (!confirmPassword.equals(password)) {
            return new ValidationResult("הסיסמה ואישור הסיסמה אינם תואמים", false);
        }

        return new ValidationResult(null, true);
    }
    //מתודות בדיקה עבור שדות של renter
    public static ValidationResult validateLicenseNumber(String licenseNumber) {
        if (licenseNumber == null) {
            return new ValidationResult("נא להזין מספר רישיון", false);
        }

        licenseNumber = licenseNumber.trim();

        if (licenseNumber.isEmpty()) {
            return new ValidationResult("נא להזין מספר רישיון", false);
        }
        if (!licenseNumber.matches(driverLicense)){
            return new ValidationResult("נא להזין מספר רישיון נהיגה תקין (7–8 ספרות בלבד)", false);
        }
        return new ValidationResult(null, true);
    }

    //-----------------------------------------------------------
    //מתודות בדיקה עבור כל השדות של SELLER
    //שלוחית רישוי תקינה
    public static ValidationResult  validateLicensePlate(String licensePlate) {
        if(licensePlate == null) return new ValidationResult("נא להזין מספר רישיון", false);

        licensePlate = licensePlate.trim();
        if(licensePlate.isEmpty()) return new ValidationResult("נא להזין מספר רישיון", false);
        if(!licensePlate.matches(LICENSE_PLATE_REGEX)) return new ValidationResult("נא להזין מספר לוחית רישיו  תקינה (7–9 ספרות בלבד)", false);

        return new ValidationResult(null, true);
    }
    //בדיקה שמספר הקילומטרים תקין
    public static ValidationResult validateKilometers(String kilometers) {
        if(kilometers == null) {
            return new ValidationResult("נא להזין מספר קילומטרים", false);

        }

        kilometers = kilometers.trim();

        if(kilometers.isEmpty()) {
            return new ValidationResult("נא להזין מספר קילומטרים", false);

        }
        if(!kilometers.matches(KILOMETERS_REGEX)) {
            return new ValidationResult("נא  להזין מספר קילומטרים תקין (מספר חיובי בלבד)", false);

        }
        return new ValidationResult(null, true);
    }
    //בדיקה שמספר השנים תקין
    public static ValidationResult validateYears(String years) {
        if (years == null) {
            return new ValidationResult("נא להזין מספר שנים", false);
        }

        years = years.trim();

        if (years.isEmpty()) {
            return new ValidationResult("נא להזין מספר שנים", false);
        }
        if (!years.matches(YEAR_REGEX)) {
            return new ValidationResult("נא להזין מספר שנים תקין (בין 1950-2026)", false);
        }
        return new ValidationResult(null, true);
    }
    //בדיקה שמספר הכיסאות תקין
    public static ValidationResult validateSeatsNumbers(String capacity) {
        if (capacity == null) {
            return new ValidationResult("נא להזין מספר כיסאות", false);
        }

        capacity = capacity.trim();

        if (capacity.isEmpty()) {
            return new ValidationResult("נא להזין מספר כיסאות", false);
        }
        if (!capacity.matches(SEATS_NUMBER_REGEX)) {
            return new ValidationResult("נא להזין מספר כיסאות תקין (ספרה חיובית בלבד)", false);
        }
        return new ValidationResult(null, true);
    }

    //-----------------------------------------------------------------------------
    // validModelsByMake טעינת ערכים קבועים לתוך מבנה הנתונים MAP
    // static block – נטען פעם אחת בלבד

    static {

        validModelsByMake.put(CarManufacturer.FIAT, Arrays.asList(
                "500",
                "Panda",
                "Punto",
                "Tipo",
                "Doblo"
        ));

        validModelsByMake.put(CarManufacturer.SUZUKI, Arrays.asList(
                "Swift",
                "Baleno",
                "Vitara",
                "SX4",
                "Jimny"
        ));

        validModelsByMake.put(CarManufacturer.TOYOTA, Arrays.asList(
                "Corolla",
                "Yaris",
                "Camry",
                "RAV4",
                "Prius",
                "C-HR"
        ));

        validModelsByMake.put(CarManufacturer.HYUNDAI, Arrays.asList(
                "i10",
                "i20",
                "i30",
                "Elantra",
                "Tucson",
                "Kona"
        ));

        validModelsByMake.put(CarManufacturer.KIA, Arrays.asList(
                "Picanto",
                "Rio",
                "Ceed",
                "Sportage",
                "Sorento"
        ));

        validModelsByMake.put(CarManufacturer.SKODA, Arrays.asList(
                "Fabia",
                "Octavia",
                "Superb",
                "Kodiaq"
        ));

        validModelsByMake.put(CarManufacturer.VOLKSWAGEN, Arrays.asList(
                "Polo",
                "Golf",
                "Passat",
                "Tiguan"
        ));

        validModelsByMake.put(CarManufacturer.MAZDA, Arrays.asList(
                "2",
                "3",
                "6",
                "CX-3",
                "CX-5"
        ));

        validModelsByMake.put(CarManufacturer.HONDA, Arrays.asList(
                "Civic",
                "Accord",
                "Jazz",
                "CR-V"
        ));

        validModelsByMake.put(CarManufacturer.FORD, Arrays.asList(
                "Fiesta",
                "Focus",
                "Mondeo",
                "Escape"
        ));

        validModelsByMake.put(CarManufacturer.CHEVROLET, Arrays.asList(
                "Spark",
                "Cruze",
                "Malibu"
        ));

        validModelsByMake.put(CarManufacturer.BMW, Arrays.asList(
                "116i",
                "118i",
                "320i",
                "X1",
                "X3",
                "X5"
        ));

        validModelsByMake.put(CarManufacturer.MERCEDES, Arrays.asList(
                "A-Class",
                "C-Class",
                "E-Class",
                "GLA",
                "GLC"
        ));

        validModelsByMake.put(CarManufacturer.AUDI, Arrays.asList(
                "A1",
                "A3",
                "A4",
                "A6",
                "Q3",
                "Q5"
        ));

        validModelsByMake.put(CarManufacturer.TESLA, Arrays.asList(
                "Model 3",
                "Model S",
                "Model X",
                "Model Y"
        ));

        validModelsByMake.put(CarManufacturer.BYD, Arrays.asList(
                "Atto 3",
                "Seal",
                "Dolphin"
        ));

        validModelsByMake.put(CarManufacturer.NISSAN, Arrays.asList(
                "Micra",
                "Sentra",
                "Altima",
                "Qashqai",
                "X-Trail"
        ));

        validModelsByMake.put(CarManufacturer.MITSUBISHI, Arrays.asList(
                "Space Star",
                "ASX",
                "Outlander"
        ));

        validModelsByMake.put(CarManufacturer.PEUGEOT, Arrays.asList(
                "108",
                "208",
                "308",
                "3008",
                "5008"
        ));
    }
    //-----------------------------------------------------------------------------------
    //פונקציה סטטית שבודקת האם היצרן של הרכב והמודל של הרכב תקינים
    public static ValidationResult validateModel(CarManufacturer carType, String userModel) {

        // בדיקת null
        if (carType == null) {
            return new ValidationResult("יש לבחור יצרן רכב", false);
        }

        if (userModel == null || userModel.trim().isEmpty()) {
            return new ValidationResult("יש להזין דגם רכב", false);
        }

        List<String> validModels = validModelsByMake.get(carType);

        if (validModels == null) {
            return new ValidationResult("לא נמצאו דגמים ליצרן שנבחר", false);
        }

        for (String model : validModels) {
            if (model.equalsIgnoreCase(userModel.trim())) {
                return new ValidationResult(null, true);
            }
        }

        return new ValidationResult("הדגם אינו שייך ליצרן שנבחר", false);
    }
    //-----------------------------------------------------------------------------------
    //טעינת ערכים קבועים לתוך המשתנה MAP validTrimsByModel
    static {
        validTrimsByModel.put("500", Arrays.asList("Pop", "Lounge", "Sport"));
        validTrimsByModel.put("Panda", Arrays.asList("Pop", "City", "Cross"));
        validTrimsByModel.put("Punto", Arrays.asList("Pop", "Easy", "Lounge"));
        validTrimsByModel.put("Tipo", Arrays.asList("Pop", "Easy", "Lounge"));
        validTrimsByModel.put("Doblo", Arrays.asList("Base", "Cargo", "Adventure"));

        validTrimsByModel.put("Swift", Arrays.asList("GA", "GL", "GLX"));
        validTrimsByModel.put("Baleno", Arrays.asList("GA", "GL", "GLX"));
        validTrimsByModel.put("Vitara", Arrays.asList("Base", "Mid", "Top"));
        validTrimsByModel.put("SX4", Arrays.asList("Base", "GLX"));
        validTrimsByModel.put("Jimny", Arrays.asList("Base", "Adventure"));

        validTrimsByModel.put("Corolla", Arrays.asList("LE", "SE", "XLE"));
        validTrimsByModel.put("Yaris", Arrays.asList("L", "LE", "XLE"));
        validTrimsByModel.put("Camry", Arrays.asList("LE", "SE", "XSE"));
        validTrimsByModel.put("RAV4", Arrays.asList("LE", "XLE", "Limited"));
        validTrimsByModel.put("Prius", Arrays.asList("Standard", "LE", "XLE"));
        validTrimsByModel.put("C-HR", Arrays.asList("LE", "XLE", "Limited"));

        validTrimsByModel.put("i10", Arrays.asList("Classic", "Comfort", "Premium"));
        validTrimsByModel.put("i20", Arrays.asList("Classic", "Comfort", "Premium"));
        validTrimsByModel.put("i30", Arrays.asList("Classic", "Comfort", "Premium"));
        validTrimsByModel.put("Elantra", Arrays.asList("SE", "SEL", "Limited"));
        validTrimsByModel.put("Tucson", Arrays.asList("SE", "SEL", "Limited"));
        validTrimsByModel.put("Kona", Arrays.asList("SE", "SEL", "Limited"));

        validTrimsByModel.put("Picanto", Arrays.asList("LX", "EX", "GT-Line"));
        validTrimsByModel.put("Rio", Arrays.asList("LX", "EX", "GT-Line"));
        validTrimsByModel.put("Ceed", Arrays.asList("LX", "EX", "GT"));
        validTrimsByModel.put("Sportage", Arrays.asList("LX", "EX", "GT-Line"));
        validTrimsByModel.put("Sorento", Arrays.asList("LX", "EX", "SX"));

        validTrimsByModel.put("Fabia", Arrays.asList("Active", "Ambition", "Style"));
        validTrimsByModel.put("Octavia", Arrays.asList("Active", "Ambition", "Style"));
        validTrimsByModel.put("Superb", Arrays.asList("Active", "Ambition", "Style"));
        validTrimsByModel.put("Kodiaq", Arrays.asList("Active", "Ambition", "Style"));

        validTrimsByModel.put("Polo", Arrays.asList("Trendline", "Comfortline", "Highline"));
        validTrimsByModel.put("Golf", Arrays.asList("Trendline", "Comfortline", "Highline"));
        validTrimsByModel.put("Passat", Arrays.asList("Trendline", "Comfortline", "Highline"));
        validTrimsByModel.put("Tiguan", Arrays.asList("Trendline", "Comfortline", "Highline"));

        validTrimsByModel.put("2", Arrays.asList("Prime", "Style", "Luxury"));
        validTrimsByModel.put("3", Arrays.asList("Prime", "Style", "Luxury"));
        validTrimsByModel.put("6", Arrays.asList("Prime", "Style", "Luxury"));
        validTrimsByModel.put("CX-3", Arrays.asList("Prime", "Style", "Luxury"));
        validTrimsByModel.put("CX-5", Arrays.asList("Prime", "Style", "Luxury"));

        validTrimsByModel.put("Civic", Arrays.asList("LX", "Sport", "EX"));
        validTrimsByModel.put("Accord", Arrays.asList("LX", "Sport", "EX"));
        validTrimsByModel.put("Jazz", Arrays.asList("LX", "Sport", "EX"));
        validTrimsByModel.put("CR-V", Arrays.asList("LX", "EX", "Touring"));

        validTrimsByModel.put("Fiesta", Arrays.asList("S", "SE", "Titanium"));
        validTrimsByModel.put("Focus", Arrays.asList("S", "SE", "Titanium"));
        validTrimsByModel.put("Mondeo", Arrays.asList("S", "SE", "Titanium"));
        validTrimsByModel.put("Escape", Arrays.asList("S", "SE", "Titanium"));

        validTrimsByModel.put("Spark", Arrays.asList("LS", "LT", "Premier"));
        validTrimsByModel.put("Cruze", Arrays.asList("LS", "LT", "Premier"));
        validTrimsByModel.put("Malibu", Arrays.asList("LS", "LT", "Premier"));

        validTrimsByModel.put("116i", Arrays.asList("Standard", "Sport", "Luxury"));
        validTrimsByModel.put("118i", Arrays.asList("Standard", "Sport", "Luxury"));
        validTrimsByModel.put("320i", Arrays.asList("Standard", "Sport", "Luxury"));
        validTrimsByModel.put("X1", Arrays.asList("Standard", "Sport", "Luxury"));
        validTrimsByModel.put("X3", Arrays.asList("Standard", "Sport", "Luxury"));
        validTrimsByModel.put("X5", Arrays.asList("Standard", "Sport", "Luxury"));

        validTrimsByModel.put("A-Class", Arrays.asList("A180", "A200", "A250"));
        validTrimsByModel.put("C-Class", Arrays.asList("C180", "C200", "C250"));
        validTrimsByModel.put("E-Class", Arrays.asList("E200", "E250", "E350"));
        validTrimsByModel.put("GLA", Arrays.asList("GLA180", "GLA200", "GLA250"));
        validTrimsByModel.put("GLC", Arrays.asList("GLC200", "GLC250", "GLC300"));

        validTrimsByModel.put("A1", Arrays.asList("Attraction", "Ambition", "S-Line"));
        validTrimsByModel.put("A3", Arrays.asList("Attraction", "Ambition", "S-Line"));
        validTrimsByModel.put("A4", Arrays.asList("Attraction", "Ambition", "S-Line"));
        validTrimsByModel.put("A6", Arrays.asList("Attraction", "Ambition", "S-Line"));
        validTrimsByModel.put("Q3", Arrays.asList("Attraction", "Ambition", "S-Line"));
        validTrimsByModel.put("Q5", Arrays.asList("Attraction", "Ambition", "S-Line"));

        validTrimsByModel.put("Model 3", Arrays.asList("Standard", "Long Range", "Performance"));
        validTrimsByModel.put("Model S", Arrays.asList("Standard", "Long Range", "Plaid"));
        validTrimsByModel.put("Model X", Arrays.asList("Standard", "Long Range", "Plaid"));
        validTrimsByModel.put("Model Y", Arrays.asList("Standard", "Long Range", "Performance"));

        validTrimsByModel.put("Atto 3", Arrays.asList("Standard", "Long Range"));
        validTrimsByModel.put("Seal", Arrays.asList("Standard", "Long Range"));
        validTrimsByModel.put("Dolphin", Arrays.asList("Standard"));

        validTrimsByModel.put("Micra", Arrays.asList("Visia", "Acenta", "Tekna"));
        validTrimsByModel.put("Sentra", Arrays.asList("S", "SV", "SL"));
        validTrimsByModel.put("Altima", Arrays.asList("S", "SV", "SL"));
        validTrimsByModel.put("Qashqai", Arrays.asList("Visia", "Acenta", "Tekna"));
        validTrimsByModel.put("X-Trail", Arrays.asList("Visia", "Acenta", "Tekna"));

        validTrimsByModel.put("Space Star", Arrays.asList("Invite", "Intense", "Instyle"));
        validTrimsByModel.put("ASX", Arrays.asList("Invite", "Intense", "Instyle"));
        validTrimsByModel.put("Outlander", Arrays.asList("Invite", "Intense", "Instyle"));

        validTrimsByModel.put("108", Arrays.asList("Access", "Active", "Allure"));
        validTrimsByModel.put("208", Arrays.asList("Access", "Active", "Allure"));
        validTrimsByModel.put("308", Arrays.asList("Access", "Active", "Allure"));
        validTrimsByModel.put("3008", Arrays.asList("Access", "Active", "Allure"));
        validTrimsByModel.put("5008", Arrays.asList("Access", "Active", "Allure"));
    }
    //---------------------------------------------------------------------------------------
    //פונקציה שבודקת אם רמת הגימור שהמשתמש הכניס תואם לסוג דגם רכב שהוא הכניס
    /**
     * בודקת אם ה-Trim שהמשתמש הכניס תקין עבור דגם הרכב שנבחר
     *
     * @param model     שם הרכב שהמשתמש בחר
     * @param userTrim  רמת הגימור שהמשתמש הזין
     * @return ValidationResult עם מידע האם הקלט תקין והודעת שגיאה במידת הצורך
     */
    public static ValidationResult validateTrim(String model, String userTrim) {

        // בדיקה אם המשתמש לא הכניס מודל
        if (model == null || model.trim().isEmpty()) {
            // מחזירים הודעת שגיאה ומציינים שהקלט אינו תקין
            return new ValidationResult("יש לבחור דגם רכב", false);
        }

        // בדיקה אם המשתמש לא הכניס Trim
        if (userTrim == null || userTrim.trim().isEmpty()) {
            return new ValidationResult("יש להזין רמת גימור (Trim)", false);
        }

        // שולפים מהרשימה (Map) את כל רמות הגימור שמותאמות למודל שהמשתמש הכניס
        List<String> validTrims = validTrimsByModel.get(model.trim());

        // אם אין רשימת Trim עבור המודל הזה, מחזירים שגיאה
        if (validTrims == null) {
            return new ValidationResult("לא נמצאו רמות גימור עבור הדגם שנבחר", false);
        }

        // עוברים על כל Trim חוקי עבור המודל
        for (String trim : validTrims) {
            // השוואה ללא תלות באותיות גדולות או קטנות
            if (trim.equalsIgnoreCase(userTrim.trim())) {
                // אם נמצא Trim תואם, הקלט תקין
                return new ValidationResult(null, true);
            }
        }

        // אם לא נמצא Trim תואם, מחזירים שגיאה
        return new ValidationResult("רמת הגימור אינה תקינה עבור הדגם שנבחר", false);
    }
}
