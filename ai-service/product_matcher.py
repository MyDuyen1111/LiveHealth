"""
Product Matcher — Query MySQL DB for products and match with LLM recommendations
"""
import re
import unicodedata

_STOPWORDS = {
    "va",
    "voi",
    "hoac",
    "khong",
    "it",
    "nhieu",
    "mon",
    "thuc",
    "pham",
    "bua",
    "sang",
    "trua",
    "toi",
    "phu",
    "ngay",
    "cho",
    "theo",
}

try:
    import mysql.connector as mysql_connector
except ImportError:
    mysql_connector = None


def get_db_connection(host: str, port: int, user: str, password: str, database: str):
    if mysql_connector is None:
        raise RuntimeError("mysql-connector-python is not installed")
    return mysql_connector.connect(
        host=host, port=port, user=user, password=password, database=database,
        charset="utf8mb4", collation="utf8mb4_general_ci",
    )


def fetch_all_products(db_config: dict) -> list[dict]:
    """Fetch all products with category, brand, and image from MySQL."""
    conn = get_db_connection(**db_config)
    cursor = conn.cursor(dictionary=True)
    cursor.execute("""
        SELECT
            BIN_TO_UUID(p.id) AS id,
            p.name,
            p.description,
            p.sku,
            p.old_price AS price,
            p.stock,
            c.name AS category,
            b.name AS brand,
            pi.image_url AS image
        FROM product p
        LEFT JOIN category c ON p.category_id = c.id
        LEFT JOIN brand b ON p.brand_id = b.id
        LEFT JOIN product_image pi ON p.id = pi.product_id
        WHERE p.stock > 0
        ORDER BY p.name
    """)
    products = cursor.fetchall()
    cursor.close()
    conn.close()
    return products


def build_product_catalog(products: list[dict]) -> str:
    """Build a text catalog for LLM context."""
    lines = []
    for p in products:
        lines.append(f"- {p['name']} ({p['category']}, {int(p['price']):,}đ)")
    return "\n".join(lines)


def _normalize_text(text: str) -> str:
    value = unicodedata.normalize("NFD", text or "")
    value = "".join(ch for ch in value if not unicodedata.combining(ch))
    value = value.replace("đ", "d").replace("Đ", "D").lower()
    value = re.sub(r"[^a-z0-9\s]", " ", value)
    return re.sub(r"\s+", " ", value).strip()


def _tokenize(text: str) -> list[str]:
    normalized = _normalize_text(text)
    return [w for w in normalized.split() if len(w) >= 3 and w not in _STOPWORDS and not w.isdigit()]


def match_products(products: list[dict], recommended_names: list[str], limit: int = 10) -> list[dict]:
    """Match LLM-recommended product names with actual products in DB.

    Uses phrase + token overlap scoring on normalized text to improve
    matching for Vietnamese names with/without accents.
    """
    if limit < 1 or not products:
        return []

    raw_keywords = [str(name).strip() for name in (recommended_names or []) if str(name).strip()]
    if not raw_keywords:
        return []

    keyword_phrases_raw: list[str] = []
    keyword_phrases_normalized: list[str] = []
    keyword_tokens: set[str] = set()

    for kw in raw_keywords:
        kw_raw = kw.lower()
        kw_norm = _normalize_text(kw)
        if len(kw_raw) >= 3:
            keyword_phrases_raw.append(kw_raw)
        if len(kw_norm) >= 3:
            keyword_phrases_normalized.append(kw_norm)
        keyword_tokens.update(_tokenize(kw))

    scored: list[tuple[int, int, dict]] = []
    for idx, product in enumerate(products):
        product_name = str(product.get("name") or "")
        product_category = str(product.get("category") or "")
        product_brand = str(product.get("brand") or "")
        haystack_raw = f"{product_name} {product_category} {product_brand}".lower()
        haystack_norm = _normalize_text(haystack_raw)
        haystack_tokens = set(_tokenize(haystack_raw))

        score = 0
        for phrase in keyword_phrases_raw:
            if phrase in haystack_raw:
                score += 9
        for phrase in keyword_phrases_normalized:
            if phrase in haystack_norm:
                score += 7

        token_overlap = len(keyword_tokens.intersection(haystack_tokens))
        score += token_overlap * 3

        if score > 0:
            scored.append((score, idx, product))

    scored.sort(key=lambda item: (-item[0], item[1]))

    results: list[dict] = []
    seen_ids: set[str] = set()
    for _, _, product in scored:
        product_id = str(product.get("id"))
        if product_id in seen_ids:
            continue
        results.append(product)
        seen_ids.add(product_id)
        if len(results) >= limit:
            break

    return results
