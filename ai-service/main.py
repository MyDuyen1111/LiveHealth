"""
LiveHealth AI Service — FastAPI + OpenAI-compatible GPT
Runs on port 62000 by default
"""
import logging
import os
import re
from contextlib import asynccontextmanager
from enum import Enum
from typing import Any

from dotenv import load_dotenv
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

from health_calculator import full_analysis
from product_matcher import fetch_all_products, build_product_catalog, match_products
from openai_chat_advisor import (
    generate_chat_message,
    generate_health_advice,
    generate_meal_plan,
    generate_missing_info_message,
)
from chat_assistant import (
    BMI_LABELS,
    PROFILE_FIELD_LABELS,
    build_assessment,
    build_chat_message,
    build_conversational_missing_message,
    build_disclaimer,
    build_meal_plan_from_products,
    build_profile_input_hint,
    build_shopping_keywords_from_meal_plan,
    classify_bmi,
    get_missing_profile_fields,
    goal_from_bmi,
    map_product_for_chat,
    parse_profile_from_text,
)

# ── Config ──
load_dotenv()
logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(name)s: %(message)s")
logger = logging.getLogger("ai-service")

DB_CONFIG = {
    "host": os.getenv("MYSQL_HOST", "localhost"),
    "port": int(os.getenv("MYSQL_PORT", 3306)),
    "user": os.getenv("MYSQL_USER", "root"),
    "password": os.getenv("MYSQL_PASSWORD", "root123"),
    "database": os.getenv("MYSQL_DATABASE", "livehealth"),
}
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY", "")
OPENAI_API_BASE = os.getenv("OPENAI_API_BASE", "https://api.openai.com/v1")
OPENAI_MODEL = os.getenv("OPENAI_MODEL", "gpt-5.4")


# ── Lifespan ──
@asynccontextmanager
async def lifespan(app: FastAPI):
    logger.info("🌿 LiveHealth AI Service starting...")
    logger.info(f"   MySQL: {DB_CONFIG['host']}:{DB_CONFIG['port']}/{DB_CONFIG['database']}")
    logger.info(f"   GPT provider: {'✅ configured' if OPENAI_API_KEY and OPENAI_MODEL and OPENAI_API_BASE else '⚠️ not configured (fallback mode)'}")
    logger.info(f"   GPT model: {OPENAI_MODEL}")
    yield
    logger.info("AI Service shutting down...")


# ── App ──
app = FastAPI(
    title="LiveHealth AI Service",
    description="Phân tích sức khỏe & gợi ý dinh dưỡng bằng AI",
    version="1.0.0",
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:62173", "http://localhost:3000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ── Models ──
class GenderEnum(str, Enum):
    male = "male"
    female = "female"


class ActivityEnum(str, Enum):
    sedentary = "sedentary"
    light = "light"
    moderate = "moderate"
    active = "active"
    very_active = "very_active"


class NutritionGoalEnum(str, Enum):
    gain_weight = "gain_weight"
    maintain_weight = "maintain_weight"
    lose_weight = "lose_weight"
    eat_healthy = "eat_healthy"


class HealthRequest(BaseModel):
    height: int = Field(..., ge=50, le=300, description="Chiều cao (cm)")
    weight: int = Field(..., ge=10, le=500, description="Cân nặng (kg)")
    age: int = Field(..., ge=1, le=150, description="Tuổi")
    gender: GenderEnum = Field(..., description="Giới tính")
    activity_level: ActivityEnum = Field(default=ActivityEnum.moderate, description="Mức vận động")


class ProductOut(BaseModel):
    id: str
    name: str
    description: str | None = None
    price: float
    stock: int
    category: str | None = None
    brand: str | None = None
    image: str | None = None


class HealthResponse(BaseModel):
    # Health metrics
    bmi: float
    bmi_status: str
    bmi_category: str
    bmr: float
    tdee: float
    daily_calories: int
    protein_g: int
    carbs_g: int
    fat_g: int
    # AI advice
    advice: str
    meal_plan: dict | None = None
    diet_tips: list[str]
    # Product recommendations
    recommended_products: list[ProductOut]


class UserProfileInput(BaseModel):
    height_cm: float | None = Field(default=None, description="Chiều cao theo cm")
    weight_kg: float | None = Field(default=None, description="Cân nặng theo kg")
    age: int | None = Field(default=None, ge=1, le=150)
    gender: GenderEnum | None = Field(default=None)
    goal: NutritionGoalEnum | None = Field(default=None)


class ChatProfileState(BaseModel):
    height_cm: float | None = None
    weight_kg: float | None = None
    age: int | None = None
    gender: GenderEnum | None = None
    goal: NutritionGoalEnum | None = None
    plan_days: int | None = None


class MealPlanDay(BaseModel):
    day: int
    breakfast: str
    lunch: str
    dinner: str
    snacks: str


class ProductChatOut(BaseModel):
    id: str
    name: str
    image: str | None = None
    price: float
    unit: str | None = None
    stock: int
    in_stock: bool
    availability: str
    category: str | None = None
    brand: str | None = None


class CartActionsOut(BaseModel):
    supports_multi_select: bool
    add_selected_action: dict[str, Any]
    add_all_action: dict[str, Any]
    go_to_cart_action: dict[str, Any]


class ChatAssistantRequest(BaseModel):
    message: str = Field(default="", description="Tin nhắn người dùng")
    user_profile: UserProfileInput | None = Field(default=None, description="Thông tin hồ sơ đã biết")
    plan_days: int | None = Field(default=None, ge=1, le=30, description="Số ngày muốn gợi ý thực đơn")
    goal: NutritionGoalEnum | None = Field(default=None, description="Mục tiêu dinh dưỡng")
    include_products: bool = Field(default=True, description="Có trả về sản phẩm gợi ý hay không")


class ChatAssistantResponse(BaseModel):
    stage: str
    assistant_message: str
    missing_fields: list[str] = Field(default_factory=list)
    input_hint: str | None = None
    user_profile: ChatProfileState | None = None
    parsed_fields: list[str] = Field(default_factory=list)
    bmi: float | None = None
    bmi_status: str | None = None
    bmi_category: str | None = None
    body_assessment: str | None = None
    nutrition_goal: str | None = None
    plan_days: int | None = None
    meal_plan: list[MealPlanDay] = Field(default_factory=list)
    recommended_products: list[ProductChatOut] = Field(default_factory=list)
    cart_actions: CartActionsOut | None = None
    disclaimer: str | None = None


def _normalize_height_cm(value: float | None) -> float | None:
    if value is None:
        return None
    if value < 3:  # support value in meters, e.g. 1.7
        return value * 100
    return value


def _normalize_goal(value: str | None) -> str | None:
    if value in ("gain_weight", "maintain_weight", "lose_weight", "eat_healthy"):
        return value
    return None


# ── API ──
def _build_profile_state(req: ChatAssistantRequest) -> dict[str, Any]:
    profile = req.user_profile
    state: dict[str, Any] = {
        "height_cm": _normalize_height_cm(profile.height_cm) if profile else None,
        "weight_kg": profile.weight_kg if profile else None,
        "age": profile.age if profile else None,
        "gender": profile.gender.value if (profile and profile.gender) else None,
        "goal": profile.goal.value if (profile and profile.goal) else None,
        "plan_days": req.plan_days,
    }
    if req.goal is not None:
        state["goal"] = req.goal.value
    return state

def _merge_profile_state(state: dict[str, Any], parsed: dict[str, Any]) -> dict[str, Any]:
    merged = dict(state)
    for key, value in parsed.items():
        if value is not None and value != "":
            merged[key] = value
    if merged.get("height_cm") is not None:
        merged["height_cm"] = _normalize_height_cm(float(merged["height_cm"]))
    if merged.get("plan_days") is not None:
        merged["plan_days"] = max(1, min(int(merged["plan_days"]), 30))
    return merged

def _profile_out(state: dict[str, Any]) -> ChatProfileState:
    return ChatProfileState(
        height_cm=state.get("height_cm"),
        weight_kg=state.get("weight_kg"),
        age=state.get("age"),
        gender=state.get("gender"),
        goal=state.get("goal"),
        plan_days=state.get("plan_days"),
    )

def _invalid_profile_field(state: dict[str, Any]) -> tuple[str, str] | None:
    height_cm = state.get("height_cm")
    weight_kg = state.get("weight_kg")
    age = state.get("age")
    plan_days = state.get("plan_days")

    if height_cm is not None and not (50 <= float(height_cm) <= 300):
        return ("height_cm", "Chiều cao có vẻ chưa hợp lệ. Bạn vui lòng nhập lại chiều cao theo cm hoặc m.")
    if weight_kg is not None and not (10 <= float(weight_kg) <= 500):
        return ("weight_kg", "Cân nặng có vẻ chưa hợp lệ. Bạn vui lòng nhập lại cân nặng theo kg.")
    if age is not None and not (1 <= int(age) <= 150):
        return ("age", "Tuổi có vẻ chưa hợp lệ. Bạn vui lòng nhập lại tuổi.")
    if plan_days is not None and not (1 <= int(plan_days) <= 30):
        return ("plan_days", "Số ngày thực đơn cần nằm trong khoảng 1 đến 30 ngày.")
    return None

def _known_profile_context(state: dict[str, Any]) -> dict[str, Any]:
    known: dict[str, Any] = {}
    for key in ("height_cm", "weight_kg", "age", "gender", "goal", "plan_days"):
        value = state.get(key)
        if value is not None and value != "":
            known[PROFILE_FIELD_LABELS.get(key, key)] = value
    return known

def _generate_missing_info_reply(
    message: str,
    profile_state: dict[str, Any],
    missing_fields: list[str],
    fallback_message: str,
) -> str:
    input_hint = build_profile_input_hint(missing_fields)
    assistant_message = generate_missing_info_message(
        api_key=OPENAI_API_KEY,
        api_base=OPENAI_API_BASE,
        model=OPENAI_MODEL,
        context={
            "user_message": message,
            "known_profile": _known_profile_context(profile_state),
            "missing_fields": missing_fields,
            "missing_labels": [PROFILE_FIELD_LABELS.get(field, field) for field in missing_fields],
            "input_hint": input_hint,
        },
    )
    if assistant_message:
        logger.info("GPT missing-info message generated")
        return assistant_message
    return fallback_message

@app.get("/api/ai/health")
async def health_check():
    openai_configured = bool(OPENAI_API_KEY and OPENAI_MODEL and OPENAI_API_BASE)
    return {
        "status": "ok",
        "service": "LiveHealth AI",
        "provider": "openai-compatible",
        "model": OPENAI_MODEL,
        "openai": openai_configured,
        "openai_chat": openai_configured,
        "gemini": False,
    }


@app.post("/api/ai/analyze", response_model=HealthResponse)
async def analyze_health(req: HealthRequest):
    logger.info(f"Analyzing: {req.height}cm, {req.weight}kg, age={req.age}, {req.gender.value}, {req.activity_level.value}")

    # 1. Medical formulas
    health_data = full_analysis(req.height, req.weight, req.age, req.gender.value, req.activity_level.value)
    health_data["gender"] = req.gender.value
    health_data["age"] = req.age

    # 2. Fetch products from DB
    try:
        products = fetch_all_products(DB_CONFIG)
        product_catalog = build_product_catalog(products)
        logger.info(f"Loaded {len(products)} products from DB")
    except Exception as e:
        logger.error(f"DB error: {e}")
        products = []
        product_catalog = "Không có sản phẩm nào."

    # 3. Get AI advice from OpenAI-compatible GPT
    ai_result = generate_health_advice(
        api_key=OPENAI_API_KEY,
        api_base=OPENAI_API_BASE,
        model=OPENAI_MODEL,
        health_data=health_data,
        product_catalog=product_catalog,
    )
    logger.info(f"GPT advice generated, {len(ai_result.get('recommended_foods', []))} food recommendations")

    # 4. Match recommended foods with DB products
    recommended_names = ai_result.get("recommended_foods", [])
    matched_products = match_products(products, recommended_names, limit=10)
    logger.info(f"Matched {len(matched_products)} products from DB")

    # If LLM didn't recommend enough, add some from appropriate categories
    if len(matched_products) < 5 and products:
        category_map = {
            "underweight": ["Thịt", "Trứng", "Sữa"],
            "normal": ["Rau", "Hoa", "Cá"],
            "overweight": ["Rau", "Cá", "Hoa"],
            "obese": ["Rau", "Hoa"],
        }
        target_cats = category_map.get(health_data["bmi_category"], ["Rau"])
        seen = {p["id"] for p in matched_products}
        for p in products:
            if p["id"] not in seen and any(cat in (p["category"] or "") for cat in target_cats):
                matched_products.append(p)
                seen.add(p["id"])
                if len(matched_products) >= 10:
                    break

    # 5. Build response
    product_out = [
        ProductOut(
            id=p["id"], name=p["name"], description=p.get("description"),
            price=float(p["price"]), stock=int(p.get("stock", 0)),
            category=p.get("category"), brand=p.get("brand"), image=p.get("image"),
        )
        for p in matched_products
    ]

    return HealthResponse(
        bmi=health_data["bmi"],
        bmi_status=health_data["bmi_status"],
        bmi_category=health_data["bmi_category"],
        bmr=health_data["bmr"],
        tdee=health_data["tdee"],
        daily_calories=health_data["daily_calories"],
        protein_g=health_data["protein_g"],
        carbs_g=health_data["carbs_g"],
        fat_g=health_data["fat_g"],
        advice=ai_result.get("advice", ""),
        meal_plan=ai_result.get("meal_plan"),
        diet_tips=ai_result.get("diet_tips", []),
        recommended_products=product_out,
    )


@app.post("/api/ai/chat", response_model=ChatAssistantResponse)
async def health_chat_assistant(req: ChatAssistantRequest):
    """
    Chat-oriented endpoint:
    - Parse natural user text into a persisted profile state
    - Ask for missing profile fields
    - Calculate BMI and call OpenAI-compatible GPT for meal plan when profile is complete
    - Return shoppable products from DB with cart actions metadata
    """
    message = req.message or ""
    initial_profile_state = _build_profile_state(req)
    parsed_profile = parse_profile_from_text(message)
    if "plan_days" not in parsed_profile and get_missing_profile_fields(initial_profile_state) == ["plan_days"]:
        bare_days = re.fullmatch(r"\s*([1-9][0-9]?)\s*[.!?]?\s*", message)
        if bare_days:
            parsed_profile["plan_days"] = int(bare_days.group(1))
    parsed_fields = list(parsed_profile.keys())
    profile_state = _merge_profile_state(initial_profile_state, parsed_profile)

    invalid_field = _invalid_profile_field(profile_state)
    if invalid_field:
        field, fallback_message = invalid_field
        profile_state[field] = None
        assistant_message = _generate_missing_info_reply(
            message=message,
            profile_state=profile_state,
            missing_fields=[field],
            fallback_message=fallback_message,
        )
        return ChatAssistantResponse(
            stage="need_more_info",
            assistant_message=assistant_message,
            missing_fields=[field],
            input_hint=build_profile_input_hint([field]),
            user_profile=_profile_out(profile_state),
            parsed_fields=parsed_fields,
        )

    missing_fields = get_missing_profile_fields(profile_state)
    if missing_fields:
        fallback_message = build_conversational_missing_message(missing_fields, profile_state, message)
        assistant_message = _generate_missing_info_reply(
            message=message,
            profile_state=profile_state,
            missing_fields=missing_fields,
            fallback_message=fallback_message,
        )
        return ChatAssistantResponse(
            stage="need_more_info",
            assistant_message=assistant_message,
            missing_fields=missing_fields,
            input_hint=build_profile_input_hint(missing_fields),
            user_profile=_profile_out(profile_state),
            parsed_fields=parsed_fields,
        )

    height_cm = float(profile_state["height_cm"])
    weight_kg = float(profile_state["weight_kg"])
    age = int(profile_state["age"])
    gender = str(profile_state["gender"])
    resolved_goal = _normalize_goal(str(profile_state["goal"])) or goal_from_bmi("normal")
    plan_days = int(profile_state["plan_days"])

    # 2) BMI + formula-based analysis for the normalized AI input
    bmi, bmi_category = classify_bmi(height_cm, weight_kg)
    bmi_status = BMI_LABELS[bmi_category]
    body_assessment = build_assessment(bmi, bmi_category)
    health_data = full_analysis(round(height_cm), round(weight_kg), age, gender, "moderate")

    products: list[dict[str, Any]] = []
    product_catalog = "Không có sản phẩm nào."
    if req.include_products:
        try:
            products = fetch_all_products(DB_CONFIG)
            product_catalog = build_product_catalog(products)
        except Exception as exc:
            logger.error("Cannot fetch products for chat assistant: %s", exc)
            products = []

    ai_context = {
        "user_message": message,
        "height_cm": height_cm,
        "weight_kg": weight_kg,
        "age": age,
        "gender": gender,
        "bmi": bmi,
        "bmi_status": bmi_status,
        "bmi_category": bmi_category,
        "nutrition_goal": resolved_goal,
        "plan_days": plan_days,
        "bmr": health_data["bmr"],
        "tdee": health_data["tdee"],
        "daily_calories": health_data["daily_calories"],
        "protein_g": health_data["protein_g"],
        "carbs_g": health_data["carbs_g"],
        "fat_g": health_data["fat_g"],
    }

    meal_plan = generate_meal_plan(
        api_key=OPENAI_API_KEY,
        api_base=OPENAI_API_BASE,
        model=OPENAI_MODEL,
        context=ai_context,
    ) or build_meal_plan_from_products(products=products, days=plan_days)

    # 4) Product suggestions from DB
    recommended_products: list[dict[str, Any]] = []
    if req.include_products and products:
        try:
            keywords = build_shopping_keywords_from_meal_plan(
                meal_plan=meal_plan,
                user_message=message,
                max_keywords=60,
            )
            if not keywords:
                keywords = [message, resolved_goal.replace("_", " ")]
            matched = match_products(products, keywords, limit=12)

            recommended_products = [map_product_for_chat(item) for item in matched]
        except Exception as exc:
            logger.error("Cannot fetch/match products for chat assistant: %s", exc)
            recommended_products = []

    # 5) Cart actions metadata (for chat UI to render buttons/checkbox flow)
    product_ids_in_stock = [item["id"] for item in recommended_products if item.get("in_stock")]
    cart_actions = CartActionsOut(
        supports_multi_select=True,
        add_selected_action={
            "type": "add_selected_to_cart",
            "label": "Thêm các sản phẩm đã chọn vào giỏ",
            "default_quantity": 1,
        },
        add_all_action={
            "type": "add_all_to_cart",
            "label": "Thêm tất cả vào giỏ",
            "product_ids": product_ids_in_stock,
            "default_quantity": 1,
        },
        go_to_cart_action={
            "type": "go_to_cart",
            "label": "Chuyển đến giỏ hàng",
            "path": "/cart",
        },
    )

    assistant_message = generate_chat_message(
        api_key=OPENAI_API_KEY,
        api_base=OPENAI_API_BASE,
        model=OPENAI_MODEL,
        context={
            "user_message": message,
            "bmi": bmi,
            "bmi_status": bmi_status,
            "bmi_category": bmi_category,
            "body_assessment": body_assessment,
            "nutrition_goal": resolved_goal,
            "plan_days": plan_days,
            "products_count": len(recommended_products),
            "meal_plan_preview": meal_plan[: min(3, len(meal_plan))],
            "product_preview": recommended_products[: min(6, len(recommended_products))],
        },
    ) or build_chat_message(
        bmi=bmi,
        bmi_category=bmi_category,
        goal=resolved_goal,
        days=plan_days,
        products_count=len(recommended_products),
    )

    return ChatAssistantResponse(
        stage="ready",
        assistant_message=assistant_message,
        user_profile=_profile_out(profile_state),
        parsed_fields=parsed_fields,
        bmi=bmi,
        bmi_status=bmi_status,
        bmi_category=bmi_category,
        body_assessment=body_assessment,
        nutrition_goal=resolved_goal,
        plan_days=plan_days,
        meal_plan=[MealPlanDay(**day) for day in meal_plan],
        recommended_products=[ProductChatOut(**item) for item in recommended_products],
        cart_actions=cart_actions,
        disclaimer=build_disclaimer(bmi_category),
    )


if __name__ == "__main__":
    import uvicorn
    reload = os.getenv("RELOAD", "false").lower() == "true"
    uvicorn.run("main:app", host="0.0.0.0", port=int(os.getenv("AI_SERVICE_PORT", 62000)), reload=reload)
