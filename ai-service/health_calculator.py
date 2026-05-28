"""
Health Calculator — Medical formulas for BMI, BMR, TDEE
"""


def calculate_bmi(height_cm: int, weight_kg: int) -> tuple[float, str, str]:
    """Calculate BMI using WHO standard.
    Returns: (bmi_value, status_vi, category)
    """
    h = height_cm / 100.0
    bmi = round(weight_kg / (h * h), 1)

    if bmi < 18.5:
        return bmi, "Thiếu cân", "underweight"
    elif bmi < 25:
        return bmi, "Bình thường", "normal"
    elif bmi < 30:
        return bmi, "Thừa cân", "overweight"
    else:
        return bmi, "Béo phì", "obese"


def calculate_bmr(height_cm: int, weight_kg: int, age: int, gender: str) -> float:
    """Calculate BMR using Mifflin-St Jeor equation.
    Male:   10 × weight + 6.25 × height - 5 × age + 5
    Female: 10 × weight + 6.25 × height - 5 × age - 161
    """
    bmr = 10 * weight_kg + 6.25 * height_cm - 5 * age
    if gender.lower() == "male":
        bmr += 5
    else:
        bmr -= 161
    return round(bmr, 1)


ACTIVITY_MULTIPLIERS = {
    "sedentary": 1.2,
    "light": 1.375,
    "moderate": 1.55,
    "active": 1.725,
    "very_active": 1.9,
}


def calculate_tdee(bmr: float, activity_level: str) -> float:
    """Calculate TDEE = BMR × activity multiplier."""
    multiplier = ACTIVITY_MULTIPLIERS.get(activity_level.lower(), 1.55)
    return round(bmr * multiplier, 1)


def calculate_macros(daily_calories: int, bmi_category: str) -> dict:
    """Calculate macro split based on BMI category."""
    ratios = {
        "underweight":  {"protein": 0.25, "carbs": 0.50, "fat": 0.25},
        "normal":       {"protein": 0.25, "carbs": 0.50, "fat": 0.25},
        "overweight":   {"protein": 0.30, "carbs": 0.40, "fat": 0.30},
        "obese":        {"protein": 0.35, "carbs": 0.35, "fat": 0.30},
    }
    r = ratios.get(bmi_category, ratios["normal"])
    return {
        "protein_g": int(daily_calories * r["protein"] / 4),
        "carbs_g": int(daily_calories * r["carbs"] / 4),
        "fat_g": int(daily_calories * r["fat"] / 9),
    }


def full_analysis(height_cm: int, weight_kg: int, age: int, gender: str, activity_level: str) -> dict:
    """Run full health analysis with all formulas."""
    bmi, bmi_status, bmi_category = calculate_bmi(height_cm, weight_kg)
    bmr = calculate_bmr(height_cm, weight_kg, age, gender)
    tdee = calculate_tdee(bmr, activity_level)
    daily_calories = int(round(tdee))
    macros = calculate_macros(daily_calories, bmi_category)

    return {
        "bmi": bmi,
        "bmi_status": bmi_status,
        "bmi_category": bmi_category,
        "bmr": bmr,
        "tdee": tdee,
        "daily_calories": daily_calories,
        **macros,
    }
