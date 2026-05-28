import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import HealthAI from '../pages/HealthAI';
import { healthApi } from '../api/healthApi';

// ---------------------------------------------------------------------------
// Mocks
// ---------------------------------------------------------------------------
const mockNavigate = vi.fn();

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useNavigate: () => mockNavigate };
});

vi.mock('../api/healthApi', () => ({
  healthApi: {
    chat: vi.fn(),
    analyze: vi.fn(),
    ping: vi.fn(),
  },
}));

vi.mock('../api/cartApi', () => ({
  cartApi: {
    addItem: vi.fn(),
    getMyCart: vi.fn(),
    updateItem: vi.fn(),
    removeItem: vi.fn(),
    clearCart: vi.fn(),
  },
}));

const mockFetchCart = vi.fn();
const mockOpenCart = vi.fn();

vi.mock('../context/CartContext', () => ({
  useCart: () => ({
    fetchCart: mockFetchCart,
    openCart: mockOpenCart,
    cart: [],
    cartTotal: 0,
    cartCount: 0,
    isCartOpen: false,
  }),
  CartProvider: ({ children }) => children,
}));

vi.mock('../context/AuthContext', () => ({
  useAuth: () => ({ isAuthenticated: true }),
  AuthProvider: ({ children }) => children,
}));

function renderHealthAI() {
  return render(
    <MemoryRouter>
      <HealthAI />
    </MemoryRouter>
  );
}

const chatTextarea = () =>
  document.querySelector('.chat-input-row textarea') ||
  screen.getByRole('textbox');
const sendButton = () => screen.getByRole('button', { name: /gửi/i });

// ---------------------------------------------------------------------------
// Pure formula spec — the AI service is expected to return values consistent
// with these formulas. Tests below use them to derive the mocked AI response.
// ---------------------------------------------------------------------------
const calculateBMI = (heightCm, weightKg) => {
  const m = heightCm / 100;
  return Number((weightKg / (m * m)).toFixed(2));
};

const calculateBMR = (weightKg, heightCm, age, gender) => {
  // Mifflin–St Jeor
  const base = 10 * weightKg + 6.25 * heightCm - 5 * age;
  return gender === 'male' ? base + 5 : base - 161;
};

const ACTIVITY_FACTORS = {
  sedentary: 1.2,
  light: 1.375,
  moderate: 1.55,
  active: 1.725,
  very_active: 1.9,
};

const calculateTDEE = (bmr, activityLevel) =>
  Math.round(bmr * (ACTIVITY_FACTORS[activityLevel] ?? 1.2));

const classifyBMI = (bmi) => {
  if (bmi < 18.5) return 'Thiếu cân';
  if (bmi < 25) return 'Bình thường';
  if (bmi < 30) return 'Thừa cân';
  return 'Béo phì';
};

describe('Health formulas (calculation spec)', () => {
  it('calculateBMI = weight (kg) / height (m)^2', () => {
    expect(calculateBMI(170, 60)).toBeCloseTo(20.76, 1);
    expect(calculateBMI(170, 78)).toBeCloseTo(26.99, 1);
    expect(calculateBMI(160, 45)).toBeCloseTo(17.58, 1);
    expect(calculateBMI(175, 95)).toBeCloseTo(31.02, 1);
  });

  it('calculateBMR uses Mifflin–St Jeor with +5 for male, -161 for female', () => {
    // Male, 28y, 170cm, 78kg → 10*78 + 6.25*170 - 5*28 + 5 = 780 + 1062.5 - 140 + 5 = 1707.5
    expect(calculateBMR(78, 170, 28, 'male')).toBeCloseTo(1707.5, 5);
    // Female, 25y, 160cm, 55kg → 10*55 + 6.25*160 - 5*25 - 161 = 550 + 1000 - 125 - 161 = 1264
    expect(calculateBMR(55, 160, 25, 'female')).toBeCloseTo(1264, 5);
  });

  it('calculateTDEE = BMR × activity factor', () => {
    const bmr = calculateBMR(78, 170, 28, 'male'); // 1707.5
    expect(calculateTDEE(bmr, 'sedentary')).toBe(Math.round(1707.5 * 1.2));
    expect(calculateTDEE(bmr, 'moderate')).toBe(Math.round(1707.5 * 1.55));
    expect(calculateTDEE(bmr, 'very_active')).toBe(Math.round(1707.5 * 1.9));
  });

  it('classifyBMI uses WHO thresholds', () => {
    expect(classifyBMI(17)).toBe('Thiếu cân');
    expect(classifyBMI(18.5)).toBe('Bình thường');
    expect(classifyBMI(22)).toBe('Bình thường');
    expect(classifyBMI(27)).toBe('Thừa cân');
    expect(classifyBMI(31)).toBe('Béo phì');
  });
});

// ---------------------------------------------------------------------------
// Component behavior — mocked AI service
// ---------------------------------------------------------------------------
describe('HealthAI - rendering', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders the welcome assistant message on first load', () => {
    renderHealthAI();
    expect(
      screen.getByText(/mình có thể tính bmi/i)
    ).toBeInTheDocument();
  });

  it('disables the send button when the input is empty', () => {
    renderHealthAI();
    expect(sendButton()).toBeDisabled();
  });

  it('enables the send button once the user types something', () => {
    renderHealthAI();
    fireEvent.change(chatTextarea(), { target: { value: '170cm 60kg' } });
    expect(sendButton()).not.toBeDisabled();
  });
});

describe('HealthAI - mock AI service', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('parses a free-form profile message and calls healthApi.chat with structured fields', async () => {
    healthApi.chat.mockResolvedValue({
      stage: 'collecting',
      assistant_message: 'Đã nhận thông tin',
    });

    renderHealthAI();
    const text = 'Mình cao 1m70, nặng 78kg, 28 tuổi, nam, giảm cân, 7 ngày';
    fireEvent.change(chatTextarea(), { target: { value: text } });
    fireEvent.click(sendButton());

    await waitFor(() => expect(healthApi.chat).toHaveBeenCalledTimes(1));

    const arg = healthApi.chat.mock.calls[0][0];
    expect(arg.message).toBe(text);
    expect(arg.includeProducts).toBe(true);
    expect(arg.userProfile).toEqual({
      height_cm: 170,
      weight_kg: 78,
      age: 28,
      gender: 'male',
      goal: 'lose_weight',
    });
    expect(arg.planDays).toBe('7');
  });

  it('renders BMI, classification and goal returned by the AI', async () => {
    const bmi = calculateBMI(170, 78);          // 26.99
    const status = classifyBMI(bmi);            // 'Thừa cân'

    healthApi.chat.mockResolvedValue({
      stage: 'ready',
      assistant_message: 'Phân tích xong',
      bmi,
      bmi_status: status,
      nutrition_goal: 'lose_weight',
      plan_days: 7,
      body_assessment: 'Bạn đang ở mức thừa cân, nên ưu tiên giảm mỡ.',
      recommended_products: [],
      cart_actions: {},
      meal_plan: [],
    });

    renderHealthAI();
    fireEvent.change(chatTextarea(), {
      target: { value: '1m70, 78kg, 28 tuổi, nam, giảm cân, 7 ngày' },
    });
    fireEvent.click(sendButton());

    expect(await screen.findByText('Phân tích xong')).toBeInTheDocument();
    expect(screen.getByText(String(bmi))).toBeInTheDocument();
    expect(screen.getByText(status)).toBeInTheDocument();
    // Goal label from the GOAL_LABELS map in HealthAI.jsx
    expect(screen.getByText('Giảm cân')).toBeInTheDocument();
    // plan_days value cell
    expect(screen.getByText('7')).toBeInTheDocument();
    expect(
      screen.getByText(/bạn đang ở mức thừa cân/i)
    ).toBeInTheDocument();
  });

  it('shows an error bubble when healthApi.chat rejects', async () => {
    healthApi.chat.mockRejectedValue(new Error('boom'));

    renderHealthAI();
    fireEvent.change(chatTextarea(), { target: { value: '1m70, 60kg' } });
    fireEvent.click(sendButton());

    expect(
      await screen.findByText(/không thể gọi ai chat lúc này/i)
    ).toBeInTheDocument();
    expect(
      screen.getByText(/đang gặp lỗi kết nối tạm thời/i)
    ).toBeInTheDocument();
  });

  it('does not call healthApi.chat when the input is whitespace only', () => {
    renderHealthAI();
    fireEvent.change(chatTextarea(), { target: { value: '     ' } });
    // Button stays disabled because the trimmed value is empty
    expect(sendButton()).toBeDisabled();
    expect(healthApi.chat).not.toHaveBeenCalled();
  });
});
