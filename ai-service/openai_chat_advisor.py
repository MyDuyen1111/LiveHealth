"""
OpenAI Chat Advisor — OpenAI-compatible helper for LiveHealth AI responses.
"""
from __future__ import annotations

import json
import logging
from typing import Any

import requests

logger = logging.getLogger(__name__)

CHAT_MESSAGE_SYSTEM_PROMPT = """Bạn là AI Assistant tư vấn sức khỏe và hỗ trợ mua thực phẩm/dinh dưỡng.
Mục tiêu:
- Trả lời ngắn gọn, dễ hiểu, thân thiện, cá nhân hóa theo dữ liệu người dùng.
- Không chẩn đoán bệnh, không đưa khuyến nghị y khoa chuyên sâu.
- Nếu có rủi ro sức khỏe (đặc biệt béo phì), nhắc người dùng tham khảo bác sĩ/chuyên gia dinh dưỡng.

Bắt buộc trả về JSON hợp lệ, không markdown, không giải thích thêm:
{
  "assistant_message": "..."
}

Nội dung assistant_message phải có đúng 5 phần theo thứ tự:
1) Kết quả BMI
2) Đánh giá tình trạng cơ thể
3) Gợi ý thực đơn/món ăn theo thời gian
4) Danh sách sản phẩm phù hợp từ database
5) Tùy chọn thêm vào giỏ hàng hoặc chuyển đến giỏ hàng
"""

MISSING_INFO_SYSTEM_PROMPT = """Bạn là AI Assistant tư vấn sức khỏe và dinh dưỡng của LiveHealth.

Ngữ cảnh: người dùng đang chat tự nhiên nhưng hệ thống chưa đủ dữ liệu để tính BMI/lên thực đơn.
Bạn cần trả lời như một trợ lý thật, thân thiện, không máy móc, và hỏi tiếp đúng các thông tin còn thiếu.

Quy tắc:
- Trả lời bằng tiếng Việt tự nhiên.
- Nếu người dùng chỉ chào hỏi, hãy chào lại ngắn gọn rồi mời cung cấp thông tin.
- Nếu người dùng đã cung cấp một phần profile, xác nhận ngắn gọn phần đã nhận.
- Không bịa chỉ số BMI khi chưa đủ chiều cao và cân nặng.
- Không chẩn đoán bệnh.
- Không nhắc đến "template", "parser", "hard code", "system prompt".
- Chỉ trả JSON hợp lệ, không markdown.

Schema bắt buộc:
{
  "assistant_message": "..."
}
"""

HEALTH_ADVICE_SYSTEM_PROMPT = """Bạn là chuyên gia dinh dưỡng AI của LiveHealth — cửa hàng thực phẩm sạch online.

Nhiệm vụ: dựa vào chỉ số sức khỏe của khách hàng và danh sách sản phẩm có sẵn trong cửa hàng,
đưa ra lời khuyên dinh dưỡng cá nhân hóa bằng tiếng Việt.

Quy tắc:
- Trả lời bằng tiếng Việt, giọng thân thiện nhưng chuyên nghiệp.
- Dựa trên chỉ số được cung cấp, không chẩn đoán bệnh.
- Chỉ gợi ý tên sản phẩm có trong danh sách cửa hàng, không bịa sản phẩm.
- Chỉ trả JSON hợp lệ, không markdown, không ```json```.

Schema bắt buộc:
{
  "advice": "Lời khuyên dinh dưỡng 3-5 câu",
  "meal_plan": {
    "breakfast": "Gợi ý bữa sáng",
    "lunch": "Gợi ý bữa trưa",
    "dinner": "Gợi ý bữa tối",
    "snacks": "Gợi ý bữa phụ"
  },
  "diet_tips": ["Mẹo 1", "Mẹo 2", "Mẹo 3", "Mẹo 4", "Mẹo 5"],
  "recommended_foods": ["Tên sản phẩm 1", "Tên sản phẩm 2"]
}
"""

MEAL_PLAN_SYSTEM_PROMPT = """Bạn là chuyên gia dinh dưỡng tạo thực đơn thực tế cho người dùng Việt Nam.
Chỉ trả về JSON hợp lệ, không markdown, không giải thích thêm, theo đúng schema:
{
  "meal_plan": [
    {
      "day": 1,
      "breakfast": "...",
      "lunch": "...",
      "dinner": "...",
      "snacks": "..."
    }
  ]
}

Yêu cầu:
- Tạo đúng số ngày người dùng yêu cầu.
- Món ăn thực tế, dễ mua, dễ nấu, phù hợp mục tiêu dinh dưỡng.
- Không để trống bất kỳ bữa nào.
- Ưu tiên tiếng Việt tự nhiên, ngắn gọn, cụ thể món/nhóm thực phẩm.
"""


def _extract_json_object(text: str) -> dict[str, Any] | None:
    if not text:
        return None

    cleaned = text.strip()
    if cleaned.startswith("```"):
        cleaned = cleaned.strip("`").strip()
        if cleaned.lower().startswith("json"):
            cleaned = cleaned[4:].strip()

    try:
        data = json.loads(cleaned)
        if isinstance(data, dict):
            return data
    except json.JSONDecodeError:
        pass

    start = cleaned.find("{")
    end = cleaned.rfind("}")
    if start == -1 or end == -1 or end <= start:
        return None

    candidate = cleaned[start : end + 1]
    try:
        data = json.loads(candidate)
        if isinstance(data, dict):
            return data
    except json.JSONDecodeError:
        return None

    return None


def _post_chat_completion(
    api_key: str,
    api_base: str,
    model: str,
    messages: list[dict[str, str]],
    temperature: float,
    max_tokens: int,
    timeout: int = 10,
) -> str | None:
    if not api_key or not api_base or not model:
        return None

    base_url = api_base.rstrip("/")
    if base_url.endswith("/chat/completions"):
        url = base_url
    elif base_url.endswith("/v1"):
        url = f"{base_url}/chat/completions"
    else:
        url = f"{base_url}/v1/chat/completions"
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json",
    }

    attempts = [
        {"token_param": "max_tokens", "include_temperature": True},
        {"token_param": "max_completion_tokens", "include_temperature": True},
        {"token_param": "max_completion_tokens", "include_temperature": False},
    ]
    last_error = ""

    def extract_content(data: dict[str, Any]) -> str | None:
        choices = data.get("choices") or []
        if not choices:
            return None
        content = (((choices[0] or {}).get("message") or {}).get("content") or "")
        if isinstance(content, list):
            parts: list[str] = []
            for item in content:
                if isinstance(item, dict):
                    parts.append(str(item.get("text") or item.get("content") or ""))
                else:
                    parts.append(str(item))
            content = "\n".join(parts)
        content = str(content).strip()
        return content or None

    try:
        for idx, attempt in enumerate(attempts):
            payload = {
                "model": model,
                "messages": messages,
                attempt["token_param"]: max_tokens,
            }
            if attempt["include_temperature"]:
                payload["temperature"] = temperature

            response = requests.post(url, headers=headers, json=payload, timeout=timeout)
            if response.status_code == 400 and idx < len(attempts) - 1:
                try:
                    last_error = response.json().get("error", {}).get("message", response.text)
                except Exception:
                    last_error = response.text
                continue

            response.raise_for_status()
            return extract_content(response.json())
    except Exception as exc:
        logger.warning("OpenAI chat completion failed: %s%s", exc, f" | last 400: {last_error}" if last_error else "")
        return None

    if last_error:
        logger.warning("OpenAI chat completion failed: %s", last_error)
    return None

def _build_health_advice_prompt(health_data: dict[str, Any], product_catalog: str) -> str:
    gender_vi = "Nam" if str(health_data.get("gender", "")).lower() == "male" else "Nữ"
    return f"""Khách hàng có chỉ số sức khỏe:
- BMI: {health_data.get('bmi')} ({health_data.get('bmi_status')})
- Nhóm BMI: {health_data.get('bmi_category')}
- BMR: {health_data.get('bmr')} kcal/ngày
- TDEE: {health_data.get('tdee')} kcal/ngày
- Lượng calo cần: {health_data.get('daily_calories')} kcal/ngày
- Protein: {health_data.get('protein_g')}g | Carbs: {health_data.get('carbs_g')}g | Fat: {health_data.get('fat_g')}g
- Giới tính: {gender_vi}
- Tuổi: {health_data.get('age', 'N/A')}

Danh sách sản phẩm có trong cửa hàng LiveHealth:
{product_catalog or "Không có sản phẩm nào."}

Hãy trả JSON đúng schema. recommended_foods nên có 8-12 tên sản phẩm chính xác từ danh sách nếu có dữ liệu sản phẩm."""

def _build_missing_info_prompt(context: dict[str, Any]) -> str:
    known_profile = context.get("known_profile") or {}
    missing_labels = context.get("missing_labels") or []
    input_hint = context.get("input_hint") or ""

    known_lines = []
    for key, value in known_profile.items():
        if value is not None and value != "":
            known_lines.append(f"- {key}: {value}")
    known_profile_text = "\n".join(known_lines) if known_lines else "- Chưa có thông tin profile nào."

    missing_text = ", ".join(str(item) for item in missing_labels) if missing_labels else "không rõ"

    return f"""Tin nhắn người dùng:
{context.get("user_message", "")}

Thông tin profile đã biết:
{known_profile_text}

Thông tin còn thiếu:
{missing_text}

Gợi ý format nhập để người dùng có thể gửi một lần:
{input_hint}

Yêu cầu trả lời:
- Viết 2-5 câu hoặc 2 đoạn ngắn.
- Hỏi tiếp các thông tin còn thiếu theo cách thân thiện.
- Nếu có input_hint, đưa ví dụ đó vào cuối câu trả lời.
- Không quá dài."""

def generate_missing_info_message(
    api_key: str,
    api_base: str,
    model: str,
    context: dict[str, Any],
) -> str | None:
    """
    Generate conversational follow-up for incomplete profile data.
    Returns None when unavailable/error so caller can use deterministic fallback.
    """
    if not api_key or not api_base or not model:
        return None

    content = _post_chat_completion(
        api_key=api_key,
        api_base=api_base,
        model=model,
        messages=[
            {"role": "system", "content": MISSING_INFO_SYSTEM_PROMPT},
            {"role": "user", "content": _build_missing_info_prompt(context)},
        ],
        temperature=0.65,
        max_tokens=650,
        timeout=10,
    )
    if not content:
        return None

    parsed = _extract_json_object(content)
    if parsed and isinstance(parsed.get("assistant_message"), str):
        return parsed["assistant_message"].strip()

    return content.strip()

def _fallback_health_advice(health_data: dict[str, Any]) -> dict[str, Any]:
    category = health_data.get("bmi_category", "normal")

    advice_map = {
        "underweight": "Bạn đang thiếu cân. Cần tăng cường protein và calo từ thực phẩm giàu dinh dưỡng.",
        "normal": "Thể trạng của bạn ở mức lý tưởng. Duy trì chế độ ăn cân bằng.",
        "overweight": "Bạn đang thừa cân. Giảm tinh bột nhanh, tăng rau xanh và protein nạc.",
        "obese": "Cần kiểm soát cân nặng. Ưu tiên rau xanh, hoa quả ít đường và trao đổi thêm với chuyên gia dinh dưỡng.",
    }

    tips_map = {
        "underweight": ["Ăn 5-6 bữa nhỏ/ngày", "Bổ sung protein sau tập", "Ưu tiên calo sạch", "Ăn thêm hạt dinh dưỡng", "Không bỏ bữa sáng"],
        "normal": ["Ăn đa dạng 5 nhóm thực phẩm", "Uống đủ nước", "Ăn nhiều rau và trái cây", "Hạn chế đồ chế biến sẵn", "Vận động đều mỗi ngày"],
        "overweight": ["Giảm tinh bột nhanh", "Thay thịt nhiều mỡ bằng cá hoặc đạm nạc", "Ăn rau trước bữa chính", "Hạn chế ăn khuya", "Đi bộ hoặc tập nhẹ đều đặn"],
        "obese": ["Hạn chế đồ chiên và nước ngọt", "Tăng rau xanh", "Ưu tiên protein ít béo", "Kiểm soát khẩu phần", "Tham khảo chuyên gia nếu có bệnh nền"],
    }

    return {
        "advice": advice_map.get(category, advice_map["normal"]),
        "meal_plan": {
            "breakfast": "Yến mạch + trái cây + sữa",
            "lunch": "Cơm vừa đủ + cá/thịt nạc + rau xanh",
            "dinner": "Salad hoặc rau luộc + protein nạc",
            "snacks": "Sữa chua, hạt hoặc trái cây",
        },
        "diet_tips": tips_map.get(category, tips_map["normal"]),
        "recommended_foods": [],
    }

def _normalize_health_advice(data: dict[str, Any] | None, health_data: dict[str, Any]) -> dict[str, Any]:
    fallback = _fallback_health_advice(health_data)
    if not isinstance(data, dict):
        return fallback

    meal_plan = data.get("meal_plan") if isinstance(data.get("meal_plan"), dict) else {}
    normalized_meal_plan = {
        "breakfast": str(meal_plan.get("breakfast") or fallback["meal_plan"]["breakfast"]).strip(),
        "lunch": str(meal_plan.get("lunch") or fallback["meal_plan"]["lunch"]).strip(),
        "dinner": str(meal_plan.get("dinner") or fallback["meal_plan"]["dinner"]).strip(),
        "snacks": str(meal_plan.get("snacks") or fallback["meal_plan"]["snacks"]).strip(),
    }

    diet_tips = [str(item).strip() for item in data.get("diet_tips", []) if str(item).strip()]
    recommended_foods = [str(item).strip() for item in data.get("recommended_foods", []) if str(item).strip()]

    return {
        "advice": str(data.get("advice") or fallback["advice"]).strip(),
        "meal_plan": normalized_meal_plan,
        "diet_tips": diet_tips[:8] or fallback["diet_tips"],
        "recommended_foods": recommended_foods[:12],
    }

def generate_health_advice(
    api_key: str,
    api_base: str,
    model: str,
    health_data: dict[str, Any],
    product_catalog: str,
) -> dict[str, Any]:
    """
    Generate full health advice using OpenAI-compatible chat API.
    Falls back to deterministic advice when the API is unavailable.
    """
    if not api_key or not api_base or not model:
        logger.warning("OpenAI-compatible provider not configured, using fallback health advice")
        return _fallback_health_advice(health_data)

    content = _post_chat_completion(
        api_key=api_key,
        api_base=api_base,
        model=model,
        messages=[
            {"role": "system", "content": HEALTH_ADVICE_SYSTEM_PROMPT},
            {"role": "user", "content": _build_health_advice_prompt(health_data, product_catalog)},
        ],
        temperature=0.55,
        max_tokens=2200,
        timeout=20,
    )
    if not content:
        return _fallback_health_advice(health_data)

    return _normalize_health_advice(_extract_json_object(content), health_data)


def _build_user_prompt(context: dict[str, Any]) -> str:
    preview_days = context.get("meal_plan_preview", [])
    products = context.get("product_preview", [])

    preview_days_text = "\n".join(
        [
            f"- Ngày {d.get('day')}: sáng={d.get('breakfast')}; trưa={d.get('lunch')}; tối={d.get('dinner')}; phụ={d.get('snacks')}"
            for d in preview_days
        ]
    )
    if not preview_days_text:
        preview_days_text = "- (Không có dữ liệu)"

    products_text = "\n".join(
        [
            f"- {p.get('name')} | giá={p.get('price')} | đơn vị={p.get('unit') or 'N/A'} | trạng thái={p.get('availability')}"
            for p in products
        ]
    )
    if not products_text:
        products_text = "- (Hiện chưa tìm thấy sản phẩm phù hợp trong database)"

    return (
        "Dữ liệu ngữ cảnh người dùng:\n"
        f"- user_message: {context.get('user_message', '')}\n"
        f"- bmi: {context.get('bmi')}\n"
        f"- bmi_status: {context.get('bmi_status')}\n"
        f"- bmi_category: {context.get('bmi_category')}\n"
        f"- body_assessment: {context.get('body_assessment')}\n"
        f"- nutrition_goal: {context.get('nutrition_goal')}\n"
        f"- plan_days: {context.get('plan_days')}\n"
        f"- total_products_found: {context.get('products_count', 0)}\n\n"
        "Preview thực đơn:\n"
        f"{preview_days_text}\n\n"
        "Preview sản phẩm gợi ý:\n"
        f"{products_text}\n\n"
        "Yêu cầu cách viết:\n"
        "- Dùng tiếng Việt tự nhiên, tối đa khoảng 8-12 dòng.\n"
        "- Nêu rõ BMI số + phân loại.\n"
        "- Nhắc thực đơn theo số ngày cụ thể.\n"
        "- Nêu có thể chọn nhiều sản phẩm và thêm vào giỏ một lượt.\n"
    )


def _build_meal_plan_prompt(context: dict[str, Any]) -> str:
    goal_vi = {
        "gain_weight": "tăng cân",
        "maintain_weight": "duy trì cân nặng",
        "lose_weight": "giảm cân",
        "eat_healthy": "ăn lành mạnh",
    }.get(context.get("nutrition_goal"), "ăn lành mạnh")

    return (
        "Dữ liệu người dùng để sinh thực đơn:\n"
        f"- user_message: {context.get('user_message', '')}\n"
        f"- bmi: {context.get('bmi')}\n"
        f"- bmi_status: {context.get('bmi_status')}\n"
        f"- bmi_category: {context.get('bmi_category')}\n"
        f"- nutrition_goal: {goal_vi}\n"
        f"- plan_days: {context.get('plan_days')}\n"
        f"- age: {context.get('age')}\n"
        f"- gender: {context.get('gender')}\n\n"
        "Hãy tạo thực đơn đầy đủ bữa sáng/trưa/tối/phụ cho từng ngày."
    )


def _normalize_day_item(item: dict[str, Any], day_number: int) -> dict[str, Any] | None:
    breakfast = str(item.get("breakfast") or "").strip()
    lunch = str(item.get("lunch") or "").strip()
    dinner = str(item.get("dinner") or "").strip()
    snacks = str(item.get("snacks") or "").strip()
    if not all([breakfast, lunch, dinner, snacks]):
        return None
    return {
        "day": day_number,
        "breakfast": breakfast,
        "lunch": lunch,
        "dinner": dinner,
        "snacks": snacks,
    }


def _validate_meal_plan(raw_plan: Any, plan_days: int) -> list[dict[str, Any]] | None:
    if not isinstance(raw_plan, list):
        return None

    normalized: list[dict[str, Any]] = []
    for idx, item in enumerate(raw_plan):
        if not isinstance(item, dict):
            continue
        normalized_item = _normalize_day_item(item, idx + 1)
        if normalized_item:
            normalized.append(normalized_item)
        if len(normalized) >= plan_days:
            break

    if not normalized:
        return None

    while len(normalized) < plan_days:
        seed = normalized[len(normalized) % len(normalized)]
        normalized.append(
            {
                "day": len(normalized) + 1,
                "breakfast": seed["breakfast"],
                "lunch": seed["lunch"],
                "dinner": seed["dinner"],
                "snacks": seed["snacks"],
            }
        )

    return normalized


def generate_meal_plan(
    api_key: str,
    api_base: str,
    model: str,
    context: dict[str, Any],
) -> list[dict[str, Any]] | None:
    """
    Generate dynamic meal plan using OpenAI-compatible chat API.
    Returns None when unavailable/error so caller can fallback safely.
    """
    if not api_key or not api_base or not model:
        return None

    plan_days = max(1, min(int(context.get("plan_days") or 7), 30))
    user_prompt = _build_meal_plan_prompt({**context, "plan_days": plan_days})

    content = _post_chat_completion(
        api_key=api_key,
        api_base=api_base,
        model=model,
        messages=[
            {"role": "system", "content": MEAL_PLAN_SYSTEM_PROMPT},
            {"role": "user", "content": user_prompt},
        ],
        temperature=0.7,
        max_tokens=min(2200, 180 * plan_days + 500),
        timeout=15,
    )
    if not content:
        return None

    parsed = _extract_json_object(content)
    meal_plan = _validate_meal_plan((parsed or {}).get("meal_plan"), plan_days) if parsed else None
    if meal_plan:
        return meal_plan

    retry_content = _post_chat_completion(
        api_key=api_key,
        api_base=api_base,
        model=model,
        messages=[
            {"role": "system", "content": MEAL_PLAN_SYSTEM_PROMPT},
            {
                "role": "user",
                "content": (
                    f"{user_prompt}\n\n"
                    "Lưu ý thêm: bắt buộc trả đúng JSON schema {\"meal_plan\":[...]}."
                ),
            },
        ],
        temperature=0.3,
        max_tokens=min(2200, 180 * plan_days + 500),
        timeout=15,
    )
    if not retry_content:
        return None

    retry_parsed = _extract_json_object(retry_content)
    return _validate_meal_plan((retry_parsed or {}).get("meal_plan"), plan_days) if retry_parsed else None


def generate_chat_message(
    api_key: str,
    api_base: str,
    model: str,
    context: dict[str, Any],
) -> str | None:
    """
    Generate assistant message from OpenAI-compatible Chat Completions API.
    Returns None when unavailable/error so caller can fallback.
    """
    content = _post_chat_completion(
        api_key=api_key,
        api_base=api_base,
        model=model,
        messages=[
            {"role": "system", "content": CHAT_MESSAGE_SYSTEM_PROMPT},
            {"role": "user", "content": _build_user_prompt(context)},
        ],
        temperature=0.4,
        max_tokens=900,
        timeout=8,
    )
    if not content:
        return None

    parsed = _extract_json_object(content)
    if parsed and isinstance(parsed.get("assistant_message"), str):
        return parsed["assistant_message"].strip()

    return content
