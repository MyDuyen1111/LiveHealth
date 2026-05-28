"""
Chat Assistant logic for BMI guidance + meal plan + shoppable products.
"""
from __future__ import annotations

import re
import unicodedata
from typing import Any


BMI_LABELS = {
    "underweight": "Thiếu cân",
    "normal": "Bình thường",
    "overweight": "Thừa cân",
    "obese": "Béo phì",
}

GOAL_ALIASES = {
    "gain_weight": ["tang can", "tăng cân", "bulk", "tang co", "tăng cơ"],
    "maintain_weight": ["duy tri", "duy trì", "giu can", "giữ cân", "on dinh", "ổn định"],
    "lose_weight": ["giam can", "giảm cân", "giam mo", "giảm mỡ", "siết cân", "cat can"],
    "eat_healthy": ["an lanh manh", "ăn lành mạnh", "eat clean", "can bang", "cân bằng"],
}

GREETING_PATTERNS = (
    "hello",
    "hi",
    "hey",
    "xin chao",
    "chao",
    "chao ban",
    "alo",
)

_STOPWORDS = {
    "va",
    "voi",
    "hoac",
    "khong",
    "it",
    "nhieu",
    "mot",
    "mon",
    "bua",
    "sang",
    "trua",
    "toi",
    "phu",
    "ngay",
    "cho",
    "theo",
    "tuan",
    "muc",
    "tieu",
    "de",
    "duoc",
}


def _strip_accents_like(text: str) -> str:
    if not text:
        return ""
    text = text.replace("đ", "d").replace("Đ", "D")
    normalized = unicodedata.normalize("NFD", text)
    return "".join(ch for ch in normalized if unicodedata.category(ch) != "Mn")

def _normalize_number(value: str) -> float:
    return float(value.replace(",", "."))

def _clean_text(text: str) -> str:
    lowered = _strip_accents_like(text or "").lower()
    lowered = re.sub(r"[^a-z0-9.,\s]", " ", lowered)
    return re.sub(r"\s+", " ", lowered).strip()

def _split_natural_parts(text: str) -> list[str]:
    parts = [part.strip() for part in re.split(r"[,;\n]+", text or "") if part.strip()]
    whole = (text or "").strip()
    if whole and whole not in parts:
        parts.append(whole)
    return parts


def extract_height_cm(text: str) -> float | None:
    if not text:
        return None

    normalized = _clean_text(text)
    m = re.search(r"\b([1-2])\s*m\s*([0-9]{2})\b", normalized)
    if m:
        return float(int(m.group(1)) * 100 + int(m.group(2)))

    m = re.search(r"\b([0-9]+(?:[.,][0-9]+)?)\s*(cm|m)\b", normalized)
    if m:
        value = _normalize_number(m.group(1))
        unit = m.group(2)
        if unit == "m":
            return value * 100 if value < 3 else value
        return value

    m = re.search(r"\b(?:cao|chieu cao|height)\b\D{0,16}([0-9]+(?:[.,][0-9]+)?)\b", normalized)
    if not m:
        return None

    value = _normalize_number(m.group(1))
    if value < 3:
        return value * 100
    if 50 <= value <= 300:
        return value
    return None

def extract_weight_kg(text: str) -> float | None:
    if not text:
        return None

    normalized = _clean_text(text)
    m = re.search(r"\b([0-9]+(?:[.,][0-9]+)?)\s*kg\b", normalized)
    if m:
        return _normalize_number(m.group(1))

    m = re.search(r"\b(?:nang|can nang|weight)\b\D{0,16}([0-9]+(?:[.,][0-9]+)?)\b", normalized)
    if not m:
        return None

    value = _normalize_number(m.group(1))
    if 10 <= value <= 500:
        return value
    return None

def extract_age(text: str) -> int | None:
    if not text:
        return None

    normalized = _clean_text(text)
    m = re.search(r"\b([1-9][0-9]?)\s*(?:tuoi|t)\b", normalized)
    if m:
        return int(m.group(1))

    m = re.search(r"\btuoi\b\D{0,12}([1-9][0-9]?)\b", normalized)
    if m:
        return int(m.group(1))

    return None

def extract_gender(text: str) -> str | None:
    if not text:
        return None

    normalized = _clean_text(text)
    if re.search(r"\b(nu|female|gai)\b", normalized):
        return "female"
    if re.search(r"\b(nam|male|trai)\b", normalized) and not re.search(r"\b[0-9]+\s*nam\b", normalized):
        return "male"
    return None

def extract_plan_days(text: str) -> int | None:
    if not text:
        return None

    normalized = _clean_text(text)
    m_keyword_days = re.search(
        r"\b(?:so ngay|bao nhieu ngay|may ngay|thuc don|len thuc don|lam thuc don|plan days|days)\b\D{0,24}([1-9][0-9]?)\b",
        normalized,
    )
    if m_keyword_days:
        return int(m_keyword_days.group(1))

    m_days = re.search(r"\b([1-9][0-9]?)\s*ngay\b", normalized)
    if m_days:
        return int(m_days.group(1))

    m_keyword_weeks = re.search(r"\b(?:so tuan|bao nhieu tuan|may tuan)\b\D{0,24}([1-9][0-9]?)\b", normalized)
    if m_keyword_weeks:
        return int(m_keyword_weeks.group(1)) * 7

    m_weeks = re.search(r"\b([1-9][0-9]?)\s*tuan\b", normalized)
    if m_weeks:
        return int(m_weeks.group(1)) * 7

    if "theo tuan" in normalized or "1 tuan" in normalized:
        return 7

    return None

def extract_goal(text: str | None) -> str | None:
    if not text:
        return None

    normalized = _clean_text(text)
    for goal_key, aliases in GOAL_ALIASES.items():
        normalized_aliases = [_clean_text(alias) for alias in aliases]
        if any(alias in normalized for alias in normalized_aliases):
            return goal_key
    return None

def parse_profile_from_text(text: str) -> dict[str, Any]:
    parsed: dict[str, Any] = {}

    for part in _split_natural_parts(text):
        height = extract_height_cm(part)
        if height is not None:
            parsed["height_cm"] = height

        weight = extract_weight_kg(part)
        if weight is not None:
            parsed["weight_kg"] = weight

        age = extract_age(part)
        if age is not None:
            parsed["age"] = age

        gender = extract_gender(part)
        if gender is not None:
            parsed["gender"] = gender

        goal = extract_goal(part)
        if goal is not None:
            parsed["goal"] = goal

        plan_days = extract_plan_days(part)
        if plan_days is not None:
            parsed["plan_days"] = plan_days

    return parsed

def is_small_talk_message(text: str) -> bool:
    normalized = _clean_text(text)
    if not normalized:
        return False

    words = normalized.split()
    if len(words) <= 4 and any(pattern == normalized for pattern in GREETING_PATTERNS):
        return True
    if len(words) <= 6 and any(normalized.startswith(f"{pattern} ") for pattern in GREETING_PATTERNS):
        return True

    return False

REQUIRED_PROFILE_FIELDS = ("height_cm", "weight_kg", "age", "gender", "goal", "plan_days")

PROFILE_FIELD_LABELS = {
    "height_cm": "chiều cao",
    "weight_kg": "cân nặng",
    "age": "tuổi",
    "gender": "giới tính",
    "goal": "mục tiêu",
    "plan_days": "số ngày thực đơn",
}

PROFILE_FIELD_QUESTIONS = {
    "height_cm": "Bạn cho mình biết chiều cao của bạn theo cm hoặc m nhé.",
    "weight_kg": "Bạn cho mình biết cân nặng của bạn theo kg nhé.",
    "age": "Bạn cho mình biết tuổi của bạn nhé.",
    "gender": "Bạn là nam hay nữ?",
    "goal": "Mục tiêu của bạn là tăng cân, giảm cân, duy trì cân nặng hay ăn lành mạnh?",
    "plan_days": "Bạn muốn mình làm thực đơn trong bao nhiêu ngày?",
}

PROFILE_FIELD_INPUT_HINTS = {
    "height_cm": "chiều cao: 170cm",
    "weight_kg": "cân nặng: 60kg",
    "age": "tuổi: 22",
    "gender": "giới tính: nam",
    "goal": "mục tiêu: tăng cân",
    "plan_days": "số ngày: 2",
}

PROFILE_FIELD_DISPLAY_FORMATTERS = {
    "height_cm": lambda value: f"chiều cao {float(value):g}cm",
    "weight_kg": lambda value: f"cân nặng {float(value):g}kg",
    "age": lambda value: f"{int(value)} tuổi",
    "gender": lambda value: "giới tính nam" if value == "male" else "giới tính nữ",
    "goal": lambda value: {
        "gain_weight": "mục tiêu tăng cân",
        "maintain_weight": "mục tiêu duy trì cân nặng",
        "lose_weight": "mục tiêu giảm cân",
        "eat_healthy": "mục tiêu ăn lành mạnh",
    }.get(str(value), "mục tiêu dinh dưỡng"),
    "plan_days": lambda value: f"thực đơn {int(value)} ngày",
}

def get_missing_profile_fields(profile: dict[str, Any]) -> list[str]:
    missing: list[str] = []
    for field in REQUIRED_PROFILE_FIELDS:
        value = profile.get(field)
        if value is None or value == "":
            missing.append(field)
    return missing

def _canonical_profile_fields(fields: list[str]) -> list[str]:
    canonical: list[str] = []
    for item in fields:
        field = "height_cm" if item == "height" else "weight_kg" if item == "weight" else item
        if field in PROFILE_FIELD_LABELS and field not in canonical:
            canonical.append(field)
    return canonical

def _join_vietnamese(items: list[str]) -> str:
    if not items:
        return ""
    if len(items) == 1:
        return items[0]
    if len(items) == 2:
        return f"{items[0]} và {items[1]}"
    return f"{', '.join(items[:-1])} và {items[-1]}"

def build_profile_input_hint(missing_fields: list[str]) -> str:
    canonical_fields = _canonical_profile_fields(missing_fields)
    if not canonical_fields:
        canonical_fields = list(REQUIRED_PROFILE_FIELDS)
    return ", ".join(PROFILE_FIELD_INPUT_HINTS[item] for item in canonical_fields)

def _format_known_profile(profile: dict[str, Any] | None) -> str:
    if not profile:
        return ""

    parts: list[str] = []
    for field in REQUIRED_PROFILE_FIELDS:
        value = profile.get(field)
        if value is None or value == "":
            continue
        formatter = PROFILE_FIELD_DISPLAY_FORMATTERS.get(field)
        if not formatter:
            continue
        try:
            parts.append(formatter(value))
        except (TypeError, ValueError):
            continue
    return _join_vietnamese(parts)

def build_missing_question(missing_fields: list[str]) -> str:
    canonical_fields = _canonical_profile_fields(missing_fields)

    if not canonical_fields:
        return "Bạn bổ sung thêm thông tin còn thiếu để mình lên thực đơn chính xác hơn nhé."

    if len(canonical_fields) == 1:
        return PROFILE_FIELD_QUESTIONS[canonical_fields[0]]

    labels = ", ".join(PROFILE_FIELD_LABELS[item] for item in canonical_fields)
    return f"Mình đã ghi nhận thông tin bạn cung cấp. Bạn bổ sung thêm: {labels}."

def build_conversational_missing_message(
    missing_fields: list[str],
    profile: dict[str, Any] | None = None,
    user_message: str = "",
) -> str:
    canonical_fields = _canonical_profile_fields(missing_fields)
    if not canonical_fields:
        return "Bạn bổ sung thêm thông tin còn thiếu để mình lên thực đơn chính xác hơn nhé."

    known_profile = _format_known_profile(profile)
    missing_labels = _join_vietnamese([PROFILE_FIELD_LABELS[item] for item in canonical_fields])
    input_hint = build_profile_input_hint(canonical_fields)
    next_question = PROFILE_FIELD_QUESTIONS[canonical_fields[0]]

    if is_small_talk_message(user_message) and not known_profile:
        return (
            "Chào bạn, mình là trợ lý dinh dưỡng của LiveHealth. "
            "Mình có thể tính BMI, tư vấn mục tiêu ăn uống, gợi ý thực đơn và sản phẩm phù hợp.\n\n"
            f"Để bắt đầu, bạn gửi giúp mình vài thông tin theo dạng: {input_hint}."
        )

    if known_profile:
        opening = f"Mình đã ghi nhận {known_profile}."
    else:
        opening = (
            "Mình tư vấn được nhé. Để phân tích sát hơn, mình cần vài thông tin cơ bản "
            "thay vì đoán chung chung."
        )

    reasoning = (
        "Cách mình phân tích: chiều cao + cân nặng để tính BMI; tuổi + giới tính để ước tính "
        "nhu cầu năng lượng; mục tiêu + số ngày để lên thực đơn phù hợp."
    )

    if len(canonical_fields) == 1:
        request = next_question
    else:
        request = f"Hiện mình cần thêm {missing_labels}. {next_question}"

    return (
        f"{opening}\n\n"
        f"{reasoning}\n\n"
        f"{request}\n"
        f"Bạn có thể nhắn tiếp theo dạng: {input_hint}."
    )

def classify_bmi(height_cm: float, weight_kg: float) -> tuple[float, str]:
    h_m = height_cm / 100.0
    bmi = round(weight_kg / (h_m * h_m), 1)
    if bmi < 18.5:
        return bmi, "underweight"
    if bmi < 25:
        return bmi, "normal"
    if bmi < 30:
        return bmi, "overweight"
    return bmi, "obese"

def goal_from_bmi(bmi_category: str) -> str:
    if bmi_category == "underweight":
        return "gain_weight"
    if bmi_category in ("overweight", "obese"):
        return "lose_weight"
    return "maintain_weight"

def build_assessment(bmi: float, bmi_category: str) -> str:
    if bmi_category == "underweight":
        return (
            f"BMI của bạn là {bmi}, thuộc nhóm thiếu cân. "
            "Bạn nên ưu tiên thực phẩm giàu năng lượng sạch, protein và ăn đều bữa."
        )
    if bmi_category == "normal":
        return (
            f"BMI của bạn là {bmi}, thuộc nhóm bình thường. "
            "Bạn nên duy trì chế độ ăn cân bằng và vận động đều."
        )
    if bmi_category == "overweight":
        return (
            f"BMI của bạn là {bmi}, thuộc nhóm thừa cân. "
            "Bạn nên giảm bớt tinh bột nhanh, tăng rau xanh và đạm nạc."
        )
    return (
        f"BMI của bạn là {bmi}, thuộc nhóm béo phì. "
        "Bạn nên kiểm soát khẩu phần, ưu tiên thực phẩm ít năng lượng và vận động phù hợp."
    )


def _normalize_text(text: str) -> str:
    lowered = _strip_accents_like(text or "").lower()
    lowered = re.sub(r"[^a-z0-9\s]", " ", lowered)
    return re.sub(r"\s+", " ", lowered).strip()


def build_shopping_keywords_from_meal_plan(
    meal_plan: list[dict[str, Any]],
    user_message: str = "",
    max_keywords: int = 48,
) -> list[str]:
    """
    Build searchable keywords from generated meal plan text.
    This keeps product matching data-driven from AI-generated meals.
    """
    if max_keywords < 1:
        return []

    phrases: list[str] = []
    token_scores: dict[str, int] = {}

    def add_phrase(value: str):
        phrase = (value or "").strip()
        if phrase:
            phrases.append(phrase)

    def score_text(value: str):
        normalized = _normalize_text(value)
        tokens = [w for w in normalized.split() if len(w) >= 3 and w not in _STOPWORDS and not w.isdigit()]
        for token in tokens:
            token_scores[token] = token_scores.get(token, 0) + 1
        for idx in range(len(tokens) - 1):
            pair = f"{tokens[idx]} {tokens[idx + 1]}"
            token_scores[pair] = token_scores.get(pair, 0) + 2

    for day in meal_plan or []:
        for meal_key in ("breakfast", "lunch", "dinner", "snacks"):
            meal_text = str(day.get(meal_key) or "")
            if not meal_text:
                continue
            add_phrase(meal_text)
            score_text(meal_text)

    if user_message:
        score_text(user_message)

    ranked_tokens = sorted(token_scores.items(), key=lambda item: (-item[1], len(item[0])))

    unique: list[str] = []
    seen: set[str] = set()

    for phrase in phrases:
        key = phrase.lower()
        if key in seen:
            continue
        unique.append(phrase)
        seen.add(key)
        if len(unique) >= max_keywords:
            return unique

    for token, _ in ranked_tokens:
        if token in seen:
            continue
        unique.append(token)
        seen.add(token)
        if len(unique) >= max_keywords:
            break

    return unique


def build_meal_plan_from_products(
    products: list[dict[str, Any]],
    days: int,
) -> list[dict[str, Any]]:
    """
    Fallback meal-plan builder using available DB product names.
    Avoids static hard-coded meal libraries.
    """
    days = max(1, min(int(days or 7), 30))
    names: list[str] = []
    seen: set[str] = set()

    for product in products or []:
        name = str(product.get("name") or "").strip()
        if not name:
            continue
        lowered = name.lower()
        if lowered in seen:
            continue
        seen.add(lowered)
        names.append(name)

    if not names:
        return []

    slots: list[list[str]] = [[], [], [], []]  # breakfast, lunch, dinner, snacks
    for idx, name in enumerate(names):
        slots[idx % 4].append(name)

    for idx in range(4):
        if not slots[idx]:
            slots[idx] = names[:]

    plan: list[dict[str, Any]] = []
    for day_idx in range(days):
        breakfast_main = slots[0][day_idx % len(slots[0])]
        breakfast_side = slots[3][day_idx % len(slots[3])]
        lunch_main = slots[1][day_idx % len(slots[1])]
        lunch_side = slots[2][day_idx % len(slots[2])]
        dinner_main = slots[2][day_idx % len(slots[2])]
        dinner_side = slots[1][(day_idx + 1) % len(slots[1])]
        snack_item = slots[3][(day_idx + 1) % len(slots[3])]

        plan.append(
            {
                "day": day_idx + 1,
                "breakfast": f"{breakfast_main} + {breakfast_side}",
                "lunch": f"{lunch_main} + {lunch_side}",
                "dinner": f"{dinner_main} + {dinner_side}",
                "snacks": snack_item,
            }
        )

    return plan


def extract_unit(name: str) -> str | None:
    if not name:
        return None
    m = re.search(
        r"(\d+\s?(kg|g|gr|ml|l|lit|lít|qua|quả|trai|trái|hop|hộp|goi|gói|chai|hu|hũ))",
        _strip_accents_like(name).lower(),
    )
    if not m:
        return None
    return m.group(1)


def map_product_for_chat(product: dict[str, Any]) -> dict[str, Any]:
    stock = int(product.get("stock", 0) or 0)
    return {
        "id": str(product.get("id")),
        "name": product.get("name"),
        "image": product.get("image"),
        "price": float(product.get("price", 0) or 0),
        "unit": extract_unit(product.get("name", "")),
        "stock": stock,
        "in_stock": stock > 0,
        "availability": "Còn hàng" if stock > 0 else "Hết hàng",
        "category": product.get("category"),
        "brand": product.get("brand"),
    }


def build_chat_message(
    bmi: float,
    bmi_category: str,
    goal: str,
    days: int,
    products_count: int,
) -> str:
    goal_vi = {
        "gain_weight": "tăng cân",
        "maintain_weight": "duy trì cân nặng",
        "lose_weight": "giảm cân",
        "eat_healthy": "ăn lành mạnh",
    }[goal]

    return (
        "Kết quả BMI:\n"
        f"- BMI: {bmi} ({BMI_LABELS[bmi_category]})\n\n"
        "Đánh giá tình trạng cơ thể:\n"
        f"- {build_assessment(bmi, bmi_category)}\n\n"
        "Gợi ý thực đơn/món ăn theo thời gian:\n"
        f"- Mình đã lên thực đơn {days} ngày theo mục tiêu {goal_vi}.\n\n"
        "Danh sách sản phẩm phù hợp từ database:\n"
        f"- Tìm thấy {products_count} sản phẩm liên quan để bạn chọn mua ngay.\n\n"
        "Tùy chọn thêm vào giỏ hàng hoặc chuyển đến giỏ hàng:\n"
        "- Bạn có thể chọn nhiều sản phẩm và thêm vào giỏ trong một lần."
    )


def build_disclaimer(bmi_category: str) -> str:
    if bmi_category in ("obese",):
        return (
            "Lưu ý: Đây là tư vấn dinh dưỡng tham khảo. "
            "Bạn nên trao đổi thêm với bác sĩ/chuyên gia dinh dưỡng để có lộ trình an toàn."
        )
    return (
        "Lưu ý: Đây là tư vấn dinh dưỡng tham khảo, không thay thế chẩn đoán y khoa chuyên sâu."
    )
