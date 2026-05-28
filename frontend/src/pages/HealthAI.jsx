import { useEffect, useMemo, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Activity,
  Utensils,
  MessageCircle,
  Send,
  ShoppingCart,
  Bot,
  ClipboardList,
} from 'lucide-react';
import { healthApi } from '../api/healthApi';
import { cartApi } from '../api/cartApi';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { formatPrice } from '../utils/format';
import './HealthAI.css';

const GOAL_LABELS = {
  gain_weight: 'Tăng cân',
  maintain_weight: 'Duy trì cân nặng',
  lose_weight: 'Giảm cân',
  eat_healthy: 'Ăn lành mạnh',
};

const INITIAL_CHAT_PROFILE = {
  height_cm: '',
  weight_kg: '',
  age: '',
  gender: '',
  goal: '',
  plan_days: '',
};

const COMMA_INPUT_TEMPLATE = [
  { field: 'height_cm', hint: 'chiều cao: 170cm' },
  { field: 'weight_kg', hint: 'cân nặng: 60kg' },
  { field: 'age', hint: 'tuổi: 22' },
  { field: 'gender', hint: 'giới tính: nam' },
  { field: 'goal', hint: 'mục tiêu: tăng cân' },
  { field: 'plan_days', hint: 'số ngày: 2' },
];

const DEFAULT_CHAT_INPUT_HINT = COMMA_INPUT_TEMPLATE.map((item) => item.hint).join(', ');

const HEALTH_AI_CHAT_STORAGE_KEY = 'livehealth.healthAi.chatSession';

const createChatMessageId = () => `${Date.now()}-${Math.random().toString(36).slice(2, 9)}`;

const createInitialChatHistory = () => [
  {
    id: 'welcome',
    role: 'assistant',
    text: 'Mình có thể tính BMI, đánh giá thể trạng, gợi ý thực đơn và đề xuất sản phẩm mua ngay. Bạn có thể bắt đầu bằng chiều cao + cân nặng.',
  },
];

const loadChatSession = () => {
  if (typeof window === 'undefined') return null;

  try {
    const rawSession = window.sessionStorage.getItem(HEALTH_AI_CHAT_STORAGE_KEY);
    if (!rawSession) return null;

    const parsedSession = JSON.parse(rawSession);
    if (!parsedSession || typeof parsedSession !== 'object') return null;

    return parsedSession;
  } catch {
    return null;
  }
};

const saveChatSession = (session) => {
  if (typeof window === 'undefined') return;

  try {
    window.sessionStorage.setItem(HEALTH_AI_CHAT_STORAGE_KEY, JSON.stringify(session));
  } catch {
    // Storage can fail in private mode or when quota is exceeded; chat still works in memory.
  }
};

const normalizeVi = (text) =>
  (text || '')
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/đ/g, 'd')
    .replace(/Đ/g, 'D')
    .toLowerCase();

const parseNumber = (raw) => {
  if (!raw) return undefined;
  const value = Number(String(raw).replace(',', '.'));
  return Number.isNaN(value) ? undefined : value;
};

const parsePositiveInt = (raw, fallback = 1) => {
  const value = parseInt(raw, 10);
  if (Number.isNaN(value) || value < 1) return fallback;
  return value;
};

const inferProfileHints = (message) => {
  const text = message || '';
  const normalized = normalizeVi(text);
  const result = {};

  const m170 = normalized.match(/\b([1-2])\s*m\s*([0-9]{2})\b/);
  if (m170) {
    result.height_cm = String(parseInt(m170[1], 10) * 100 + parseInt(m170[2], 10));
  } else {
    const heightMatch = normalized.match(/\b([0-9]+(?:[.,][0-9]+)?)\s*(cm|m)\b/);
    if (heightMatch) {
      const value = parseNumber(heightMatch[1]);
      if (value !== undefined) {
        result.height_cm = String(heightMatch[2] === 'm' && value < 3 ? value * 100 : value);
      }
    } else {
      const heightKeywordMatch = normalized.match(/\b(?:cao|chieu cao|height)\b\D{0,16}([0-9]+(?:[.,][0-9]+)?)\b/);
      if (heightKeywordMatch) {
        const value = parseNumber(heightKeywordMatch[1]);
        if (value !== undefined) result.height_cm = String(value < 3 ? value * 100 : value);
      }
    }
  }

  const weightMatch = normalized.match(/\b([0-9]+(?:[.,][0-9]+)?)\s*kg\b/);
  if (weightMatch) {
    const value = parseNumber(weightMatch[1]);
    if (value !== undefined) result.weight_kg = String(value);
  } else {
    const weightKeywordMatch = normalized.match(/\b(?:nang|can nang|weight)\b\D{0,16}([0-9]+(?:[.,][0-9]+)?)\b/);
    if (weightKeywordMatch) {
      const value = parseNumber(weightKeywordMatch[1]);
      if (value !== undefined) result.weight_kg = String(value);
    }
  }

  const ageMatch = normalized.match(/\b([1-9][0-9]?)\s*(?:tuoi|t)\b/) || normalized.match(/\btuoi\b\D{0,12}([1-9][0-9]?)\b/);
  if (ageMatch) {
    result.age = ageMatch[1];
  }

  if (/\bnu\b/.test(normalized)) result.gender = 'female';
  if (/\bnam\b/.test(normalized) && !/\b[0-9]+\s*nam\b/.test(normalized)) result.gender = 'male';

  if (/(tang can|bulk|tang co)/.test(normalized)) result.goal = 'gain_weight';
  if (/(duy tri|giu can|on dinh)/.test(normalized)) result.goal = 'maintain_weight';
  if (/(giam can|giam mo|siet can|cat can)/.test(normalized)) result.goal = 'lose_weight';
  if (/(an lanh manh|eat clean|can bang)/.test(normalized)) result.goal = 'eat_healthy';

  const dayKeywordMatch = normalized.match(/\b(?:so ngay|bao nhieu ngay|may ngay|thuc don|len thuc don|lam thuc don|plan days|days)\b\D{0,24}([1-9][0-9]?)\b/);
  const dayMatch = normalized.match(/\b([1-9][0-9]?)\s*ngay\b/);
  if (dayKeywordMatch) {
    result.plan_days = dayKeywordMatch[1];
  } else if (dayMatch) {
    result.plan_days = dayMatch[1];
  } else {
    const weekKeywordMatch = normalized.match(/\b(?:so tuan|bao nhieu tuan|may tuan)\b\D{0,24}([1-9][0-9]?)\b/);
    const weekMatch = normalized.match(/\b([1-9][0-9]?)\s*tuan\b/);
    const matchedWeeks = weekKeywordMatch || weekMatch;
    if (matchedWeeks) {
      result.plan_days = String(parseInt(matchedWeeks[1], 10) * 7);
    }
  }

  return result;
};

const buildUserProfilePayload = (profile) => {
  const payload = {};

  const heightCm = parseNumber(profile.height_cm);
  const weightKg = parseNumber(profile.weight_kg);
  const age = parseInt(profile.age, 10);

  if (heightCm) payload.height_cm = heightCm;
  if (weightKg) payload.weight_kg = weightKg;
  if (!Number.isNaN(age) && age > 0) payload.age = age;
  if (profile.gender === 'male' || profile.gender === 'female') payload.gender = profile.gender;
  if (profile.goal) payload.goal = profile.goal;

  return payload;
};

const mergeProfileFromResponse = (currentProfile, responseProfile) => {
  if (!responseProfile) return currentProfile;

  const next = { ...currentProfile };
  if (responseProfile.height_cm !== null && responseProfile.height_cm !== undefined) next.height_cm = String(responseProfile.height_cm);
  if (responseProfile.weight_kg !== null && responseProfile.weight_kg !== undefined) next.weight_kg = String(responseProfile.weight_kg);
  if (responseProfile.age !== null && responseProfile.age !== undefined) next.age = String(responseProfile.age);
  if (responseProfile.gender) next.gender = responseProfile.gender;
  if (responseProfile.goal) next.goal = responseProfile.goal;
  if (responseProfile.plan_days !== null && responseProfile.plan_days !== undefined) next.plan_days = String(responseProfile.plan_days);

  return next;
};

const getMissingChatProfileFields = (profile) =>
  COMMA_INPUT_TEMPLATE
    .map((item) => item.field)
    .filter((field) => !profile[field]);

const buildHintForFields = (fields) => {
  const hints = COMMA_INPUT_TEMPLATE
    .filter((item) => fields.includes(item.field))
    .map((item) => item.hint);
  return hints.join(', ');
};

const looksLikeProfileInput = (value) => {
  const normalized = normalizeVi(value);
  return /,|cao|chieu cao|height|nang|can nang|weight|kg|tuoi|gioi tinh|\bnam\b|\bnu\b|muc tieu|tang can|giam can|duy tri|an lanh manh|ngay|tuan/.test(normalized);
};

const buildCommaInputAssist = (value, profile, serverHint) => {
  const trimmed = value.trim();

  if (trimmed && looksLikeProfileInput(trimmed)) {
    const completedCount = Math.min(
      trimmed.split(',').map((part) => part.trim()).filter(Boolean).length,
      COMMA_INPUT_TEMPLATE.length
    );
    const remainingHints = COMMA_INPUT_TEMPLATE.slice(completedCount).map((item) => item.hint);
    return remainingHints.join(', ');
  }

  if (trimmed) {
    return serverHint || DEFAULT_CHAT_INPUT_HINT;
  }

  return buildHintForFields(getMissingChatProfileFields(profile)) || serverHint || DEFAULT_CHAT_INPUT_HINT;
};

const ChatProductCard = ({
  product,
  checked,
  onToggle,
  showCheckbox,
  onAddToCart,
  onBuyNow,
  disableActions,
}) => {
  const img = product.image || 'https://placehold.co/?text=No+Image';

  return (
    <div className={`chat-product-card ${product.in_stock ? '' : 'sold-out'} ${showCheckbox ? '' : 'no-checkbox'}`}>
      {showCheckbox && (
        <div className="chat-product-check">
          <input
            type="checkbox"
            checked={checked}
            disabled={!product.in_stock}
            onChange={() => onToggle(product.id)}
            aria-label={`Chọn sản phẩm ${product.name}`}
          />
        </div>
      )}
      <Link to={`/product/${product.id}`} className="chat-product-thumb">
        <img src={img} alt={product.name} />
      </Link>
      <div className="chat-product-info">
        <Link to={`/product/${product.id}`} className="chat-product-name">{product.name}</Link>
        <div className="chat-product-meta">
          <span className="chat-product-price">{formatPrice(product.price || 0)}</span>
          {product.unit && <span className="chat-product-unit">/ {product.unit}</span>}
        </div>
        <div className={`chat-product-stock ${product.in_stock ? 'in-stock' : 'out-stock'}`}>
          {product.availability || (product.in_stock ? 'Còn hàng' : 'Hết hàng')}
        </div>
        <div className="chat-product-actions">
          <Link
            to={`/product/${product.id}`}
            className="chat-product-action view"
          >
            Xem hàng
          </Link>
          <button
            type="button"
            className="chat-product-action"
            disabled={disableActions || !product.in_stock}
            onClick={() => onAddToCart(product.id)}
          >
            Thêm giỏ
          </button>
          <button
            type="button"
            className="chat-product-action buy-now"
            disabled={disableActions || !product.in_stock}
            onClick={() => onBuyNow(product.id)}
          >
            Đặt hàng
          </button>
        </div>
      </div>
    </div>
  );
};

const ChatResultContent = ({
  data,
  selectedProductIds,
  onSetSelectedProductIds,
  cartLoading,
  chatLoading,
  onToggleProduct,
  onAddProductIds,
  onQuickAdd,
  onQuickBuyNow,
  onGoToCart,
}) => {
  if (!data || data.stage !== 'ready') return null;

  const products = data.recommended_products || [];
  const cartActions = data.cart_actions || {};
  const supportsMultiSelect = cartActions?.supports_multi_select ?? true;
  const addSelectedAction = cartActions?.add_selected_action || {
    label: 'Thêm sản phẩm đã chọn vào giỏ',
    default_quantity: 1,
  };
  const addAllAction = cartActions?.add_all_action || {
    label: 'Thêm tất cả còn hàng',
    default_quantity: 1,
  };
  const goToCartAction = cartActions?.go_to_cart_action || {
    label: 'Đi tới giỏ hàng',
    path: '/cart',
  };
  const inStockProductIds = products.filter((p) => p.in_stock).map((p) => String(p.id));
  const actionProductIds = Array.isArray(addAllAction?.product_ids) ? addAllAction.product_ids.map(String) : [];
  const addAllProductIds = actionProductIds.filter((id) => inStockProductIds.includes(id));
  const finalAddAllProductIds = addAllProductIds.length ? addAllProductIds : inStockProductIds;

  return (
    <div className="chat-result in-chat">
      <h3><ClipboardList size={18} /> Kết quả tư vấn</h3>

      <div className="chat-summary-grid">
        <div className="summary-item">
          <span>BMI</span>
          <strong>{data.bmi ?? '--'}</strong>
        </div>
        <div className="summary-item">
          <span>Phân loại</span>
          <strong>{data.bmi_status || '--'}</strong>
        </div>
        <div className="summary-item">
          <span>Mục tiêu</span>
          <strong>{GOAL_LABELS[data.nutrition_goal] || '--'}</strong>
        </div>
        <div className="summary-item">
          <span>Số ngày</span>
          <strong>{data.plan_days || '--'}</strong>
        </div>
      </div>

      {data.body_assessment && (
        <div className="chat-assessment">{data.body_assessment}</div>
      )}

      {data.meal_plan?.length > 0 && (
        <div className="chat-meal-plan">
          <h4><Utensils size={16} /> Thực đơn gợi ý</h4>
          <div className="chat-meal-days">
            {data.meal_plan.map((dayItem) => (
              <div className="chat-meal-day" key={dayItem.day}>
                <div className="chat-meal-day-title">Ngày {dayItem.day}</div>
                <ul>
                  <li><strong>Sáng:</strong> {dayItem.breakfast}</li>
                  <li><strong>Trưa:</strong> {dayItem.lunch}</li>
                  <li><strong>Tối:</strong> {dayItem.dinner}</li>
                  <li><strong>Phụ:</strong> {dayItem.snacks}</li>
                </ul>
              </div>
            ))}
          </div>
        </div>
      )}

      {products.length > 0 ? (
        <div className="chat-product-section">
          <h4><ShoppingCart size={16} /> Sản phẩm phù hợp từ database</h4>

          {supportsMultiSelect && (
            <div className="chat-product-tools">
              <button
                type="button"
                className="chat-tool-btn"
                onClick={() => onSetSelectedProductIds(inStockProductIds)}
                disabled={!inStockProductIds.length}
              >
                Chọn tất cả còn hàng
              </button>
              <button
                type="button"
                className="chat-tool-btn"
                onClick={() => onSetSelectedProductIds([])}
              >
                Bỏ chọn
              </button>
              <span>{selectedProductIds.length} sản phẩm đã chọn</span>
            </div>
          )}

          <div className="chat-product-grid">
            {products.map((product) => (
              <ChatProductCard
                key={product.id}
                product={product}
                checked={selectedProductIds.includes(String(product.id))}
                onToggle={onToggleProduct}
                showCheckbox={supportsMultiSelect}
                onAddToCart={onQuickAdd}
                onBuyNow={onQuickBuyNow}
                disableActions={cartLoading || chatLoading}
              />
            ))}
          </div>

          <div className="chat-action-row">
            {supportsMultiSelect && (
              <button
                type="button"
                className="chat-action-btn primary"
                disabled={!selectedProductIds.length || cartLoading}
                onClick={() => onAddProductIds(selectedProductIds, addSelectedAction.default_quantity || 1, false)}
              >
                {cartLoading ? 'Đang thêm...' : (addSelectedAction.label || 'Thêm sản phẩm đã chọn vào giỏ')}
              </button>
            )}
            <button
              type="button"
              className="chat-action-btn"
              disabled={!finalAddAllProductIds.length || cartLoading}
              onClick={() => onAddProductIds(finalAddAllProductIds, addAllAction.default_quantity || 1, false)}
            >
              {addAllAction.label || 'Thêm tất cả còn hàng'}
            </button>
            <button
              type="button"
              className="chat-action-btn"
              onClick={() => onGoToCart(typeof goToCartAction.path === 'string' && goToCartAction.path ? goToCartAction.path : '/cart')}
            >
              {goToCartAction.label || 'Đi tới giỏ hàng'}
            </button>
          </div>
        </div>
      ) : (
        <div className="chat-no-products">Hiện chưa có sản phẩm phù hợp để hiển thị từ database.</div>
      )}

      {data.disclaimer && <div className="chat-disclaimer">{data.disclaimer}</div>}
    </div>
  );
};

const HealthAI = () => {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const { fetchCart, openCart } = useCart();

  const savedChatSession = useMemo(() => loadChatSession(), []);

  const [chatInput, setChatInput] = useState(() => savedChatSession?.chatInput || '');
  const [chatLoading, setChatLoading] = useState(false);
  const [chatError, setChatError] = useState('');
  const [chatProfile, setChatProfile] = useState(() => ({
    ...INITIAL_CHAT_PROFILE,
    ...(savedChatSession?.chatProfile || {}),
  }));
  const [chatInputHint, setChatInputHint] = useState(() => savedChatSession?.chatInputHint || DEFAULT_CHAT_INPUT_HINT);
  const [chatHistory, setChatHistory] = useState(() => (
    Array.isArray(savedChatSession?.chatHistory) && savedChatSession.chatHistory.length
      ? savedChatSession.chatHistory
      : createInitialChatHistory()
  ));
  const [selectedProductsByResult, setSelectedProductsByResult] = useState(() => (
    savedChatSession?.selectedProductsByResult && typeof savedChatSession.selectedProductsByResult === 'object'
      ? savedChatSession.selectedProductsByResult
      : {}
  ));
  const [cartLoading, setCartLoading] = useState(false);

  const chatThreadRef = useRef(null);

  useEffect(() => {
    saveChatSession({
      chatHistory,
      chatProfile,
      chatInputHint,
      selectedProductsByResult,
      chatInput,
    });
  }, [chatHistory, chatProfile, chatInputHint, selectedProductsByResult, chatInput]);

  useEffect(() => {
    if (chatThreadRef.current) {
      chatThreadRef.current.scrollTop = chatThreadRef.current.scrollHeight;
    }
  }, [chatHistory, chatLoading]);

  const chatInputAssist = useMemo(
    () => buildCommaInputAssist(chatInput, chatProfile, chatInputHint),
    [chatInput, chatProfile, chatInputHint]
  );

  const sendChat = async (e) => {
    e.preventDefault();
    const trimmed = chatInput.trim();
    if (!trimmed || chatLoading) return;

    const hints = inferProfileHints(trimmed);
    const missingBeforeSend = getMissingChatProfileFields(chatProfile);
    const bareDays = trimmed.match(/^\s*([1-9][0-9]?)\s*[.!?]?\s*$/);
    if (!hints.plan_days && missingBeforeSend.length === 1 && missingBeforeSend[0] === 'plan_days' && bareDays) {
      hints.plan_days = bareDays[1];
    }
    const mergedProfile = { ...chatProfile, ...hints };
    const userProfilePayload = buildUserProfilePayload(mergedProfile);

    setChatProfile(mergedProfile);
    setChatInput('');
    setChatError('');
    setChatHistory((prev) => [...prev, { id: createChatMessageId(), role: 'user', text: trimmed }]);
    setChatLoading(true);

    try {
      const data = await healthApi.chat({
        message: trimmed,
        userProfile: userProfilePayload,
        planDays: mergedProfile.plan_days,
        includeProducts: true,
      });

      const readyData = data?.stage === 'ready' ? data : null;
      const assistantMessageId = createChatMessageId();
      if (readyData) {
        setSelectedProductsByResult((prev) => ({ ...prev, [assistantMessageId]: [] }));
      }
      setChatHistory((prev) => [
        ...prev,
        {
          id: assistantMessageId,
          role: 'assistant',
          text: data.assistant_message || 'Mình đã xử lý xong yêu cầu của bạn.',
          data: readyData,
        },
      ]);
      setChatProfile((prev) => mergeProfileFromResponse(prev, data?.user_profile));
      if (data?.input_hint) {
        setChatInputHint(data.input_hint);
      } else if (data?.stage === 'ready') {
        setChatInputHint(DEFAULT_CHAT_INPUT_HINT);
      }
    } catch (err) {
      console.error(err);
      setChatError('Không thể gọi AI chat lúc này. Bạn thử lại sau nhé.');
      setChatHistory((prev) => [
        ...prev,
        {
          id: createChatMessageId(),
          role: 'assistant',
          text: 'Mình đang gặp lỗi kết nối tạm thời, bạn vui lòng thử lại sau ít phút.',
        },
      ]);
    } finally {
      setChatLoading(false);
    }
  };

  const setSelectedProductIdsForResult = (resultId, productIds) => {
    if (!resultId) return;
    setSelectedProductsByResult((prev) => ({
      ...prev,
      [resultId]: [...new Set((productIds || []).filter(Boolean).map(String))],
    }));
  };

  const toggleProductSelection = (resultId, productId) => {
    if (!resultId) return;
    const id = String(productId);
    setSelectedProductsByResult((prev) => {
      const current = prev[resultId] || [];
      const next = current.includes(id)
        ? current.filter((currentId) => currentId !== id)
        : [...current, id];
      return { ...prev, [resultId]: next };
    });
  };

  const addProductIdsToCart = async (productIds, quantity = 1, redirectToCart = false) => {
    const ids = [...new Set((productIds || []).filter(Boolean).map(String))];
    if (!ids.length) {
      setChatError('Bạn chưa chọn sản phẩm còn hàng để thêm vào giỏ.');
      return;
    }

    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    setCartLoading(true);
    setChatError('');
    try {
      const qty = parsePositiveInt(quantity, 1);
      for (const id of ids) {
        await cartApi.addItem(id, qty);
      }
      await fetchCart();
      openCart();
      if (redirectToCart) {
        navigate('/cart');
      }
    } catch (err) {
      console.error(err);
      setChatError(err.message || 'Thêm vào giỏ chưa thành công. Bạn thử lại giúp mình nhé.');
    } finally {
      setCartLoading(false);
    }
  };

  const handleQuickAdd = async (productId) => {
    await addProductIdsToCart([productId], 1, false);
  };

  const handleQuickBuyNow = async (productId) => {
    await addProductIdsToCart([productId], 1, true);
  };

  return (
    <div className="container ai-page">
      <div className="ai-header">
        <div className="ai-icon-wrapper"><Activity size={48} className="ai-icon" /></div>
        <h1>AI Sức Khỏe & Dinh Dưỡng</h1>
        <p>Chat tư vấn BMI, gợi ý thực đơn theo ngày/tuần và thêm sản phẩm vào giỏ hàng ngay trong hội thoại.</p>
      </div>

      <div className="ai-content">
        <section className="ai-chat">
          <div className="chat-section-head">
            <h2><MessageCircle size={20} /> Chat AI Tư Vấn & Mua Hàng</h2>
            <p>Bạn có thể nhắn tự nhiên như: "Mình cao 1m70, nặng 78kg, 28 tuổi, nam, giảm cân, 7 ngày".</p>
          </div>

          <div className="chat-thread" ref={chatThreadRef}>
            {chatHistory.map((item, idx) => (
              <div key={item.id || `${item.role}-${idx}`} className={`chat-bubble ${item.role}${item.data ? ' has-result' : ''}`}>
                <div className="chat-bubble-role">
                  {item.role === 'assistant' ? <><Bot size={14} /> AI</> : 'Bạn'}
                </div>
                <p>{item.text}</p>
                {item.data && (
                  <ChatResultContent
                    data={item.data}
                    selectedProductIds={selectedProductsByResult[item.id] || []}
                    onSetSelectedProductIds={(productIds) => setSelectedProductIdsForResult(item.id, productIds)}
                    cartLoading={cartLoading}
                    chatLoading={chatLoading}
                    onToggleProduct={(productId) => toggleProductSelection(item.id, productId)}
                    onAddProductIds={addProductIdsToCart}
                    onQuickAdd={handleQuickAdd}
                    onQuickBuyNow={handleQuickBuyNow}
                    onGoToCart={(path) => navigate(path)}
                  />
                )}
              </div>
            ))}
            {chatLoading && (
              <div className="chat-bubble assistant loading">
                <div className="chat-bubble-role"><Bot size={14} /> AI</div>
                <p>AI đang phân tích yêu cầu của bạn...</p>
              </div>
            )}
          </div>

          <div className="chat-input-stack">
            <form className="chat-input-row" onSubmit={sendChat}>
              <textarea
                value={chatInput}
                onChange={(e) => setChatInput(e.target.value)}
                placeholder={chatInputHint || 'Nhập câu hỏi dinh dưỡng của bạn...'}
                rows={3}
                onKeyDown={(e) => {
                  if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    sendChat(e);
                  }
                }}
              />
              <button type="submit" className="chat-send-btn" disabled={chatLoading || !chatInput.trim()}>
                <Send size={16} /> Gửi
              </button>
            </form>
            {chatInputAssist && (
              <div className="chat-input-assist">
                Gợi ý tiếp: <span>{chatInputAssist}</span>
              </div>
            )}
          </div>

          {chatError && <div className="ai-error">{chatError}</div>}
        </section>
      </div>
    </div>
  );
};

export default HealthAI;
