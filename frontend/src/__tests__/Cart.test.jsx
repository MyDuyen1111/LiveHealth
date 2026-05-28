import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import { formatPrice } from '../utils/format';
import { CartProvider, useCart } from '../context/CartContext';
import { LanguageProvider } from '../context/LanguageContext';
import Cart from '../pages/Cart';
import { cartApi } from '../api/cartApi';

vi.mock('../api/cartApi', () => ({
  cartApi: {
    getMyCart: vi.fn(),
    addItem: vi.fn(),
    updateItem: vi.fn(),
    removeItem: vi.fn(),
    clearCart: vi.fn(),
  },
}));

vi.mock('../context/AuthContext', () => ({
  useAuth: () => ({ isAuthenticated: true }),
  AuthProvider: ({ children }) => children,
}));

// --- Probe to capture the live CartContext value -----------------------------
let captured;
function Probe() {
  captured = useCart();
  return null;
}

function renderProvider() {
  return render(
    <MemoryRouter>
      <CartProvider>
        <Probe />
      </CartProvider>
    </MemoryRouter>
  );
}

function renderCartPage() {
  return render(
    <MemoryRouter>
      <LanguageProvider>
        <CartProvider>
          <Cart />
        </CartProvider>
      </LanguageProvider>
    </MemoryRouter>
  );
}

// =============================================================================
// formatVND (formatPrice)
// =============================================================================
describe('formatVND (formatPrice)', () => {
  it('formats positive integers with vi-VN thousand separators and "đ" suffix', () => {
    expect(formatPrice(1000)).toBe('1.000đ');
    expect(formatPrice(12500)).toBe('12.500đ');
    expect(formatPrice(1234567)).toBe('1.234.567đ');
  });

  it('formats zero as "0đ"', () => {
    expect(formatPrice(0)).toBe('0đ');
  });

  it('formats small values without separators', () => {
    expect(formatPrice(7)).toBe('7đ');
    expect(formatPrice(999)).toBe('999đ');
  });
});

// =============================================================================
// CartProvider: addToCart / updateQuantity / removeFromCart / calculateTotal
// =============================================================================
describe('CartContext - addToCart', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    captured = undefined;
  });

  it('calls cartApi.addItem with productId and quantity and updates state from the response', async () => {
    cartApi.getMyCart.mockResolvedValue({ items: [], finalTotalAmount: 0 });
    cartApi.addItem.mockResolvedValue({
      items: [
        { id: 'item-1', productName: 'Whey Protein', price: 500000, quantity: 2, subtotal: 1000000 },
      ],
      finalTotalAmount: 1000000,
    });

    renderProvider();
    await waitFor(() => expect(captured?.cart).toEqual([]));

    await act(async () => {
      await captured.addToCart({ id: 'prod-1' }, 2);
    });

    expect(cartApi.addItem).toHaveBeenCalledWith('prod-1', 2);
    expect(captured.cart).toHaveLength(1);
    expect(captured.cart[0].productName).toBe('Whey Protein');
    expect(captured.cartCount).toBe(2);
    expect(captured.isCartOpen).toBe(true);
  });

  it('defaults quantity to 1 when omitted', async () => {
    cartApi.getMyCart.mockResolvedValue({ items: [], finalTotalAmount: 0 });
    cartApi.addItem.mockResolvedValue({ items: [], finalTotalAmount: 0 });

    renderProvider();
    await waitFor(() => expect(captured).toBeTruthy());

    await act(async () => {
      await captured.addToCart({ id: 'prod-9' });
    });

    expect(cartApi.addItem).toHaveBeenCalledWith('prod-9', 1);
  });
});

describe('CartContext - updateQuantity', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    captured = undefined;
  });

  it('calls cartApi.updateItem with itemId and new quantity', async () => {
    cartApi.getMyCart.mockResolvedValue({
      items: [{ id: 'i1', productName: 'A', price: 100, quantity: 1, subtotal: 100 }],
      finalTotalAmount: 100,
    });
    cartApi.updateItem.mockResolvedValue({
      items: [{ id: 'i1', productName: 'A', price: 100, quantity: 5, subtotal: 500 }],
      finalTotalAmount: 500,
    });

    renderProvider();
    await waitFor(() => expect(captured.cart).toHaveLength(1));

    await act(async () => {
      await captured.updateQuantity('i1', 5);
    });

    expect(cartApi.updateItem).toHaveBeenCalledWith('i1', 5);
    expect(captured.cart[0].quantity).toBe(5);
    expect(captured.cartTotal).toBe(500);
  });

  it('delegates to removeFromCart when quantity drops below 1', async () => {
    cartApi.getMyCart
      .mockResolvedValueOnce({
        items: [{ id: 'i1', productName: 'A', price: 100, quantity: 1, subtotal: 100 }],
        finalTotalAmount: 100,
      })
      .mockResolvedValueOnce({ items: [], finalTotalAmount: 0 });
    cartApi.removeItem.mockResolvedValue({});

    renderProvider();
    await waitFor(() => expect(captured.cart).toHaveLength(1));

    await act(async () => {
      await captured.updateQuantity('i1', 0);
    });

    expect(cartApi.removeItem).toHaveBeenCalledWith('i1');
    expect(cartApi.updateItem).not.toHaveBeenCalled();
  });
});

describe('CartContext - removeFromCart', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    captured = undefined;
  });

  it('calls cartApi.removeItem and refetches the cart', async () => {
    cartApi.getMyCart
      .mockResolvedValueOnce({
        items: [{ id: 'i1', productName: 'A', price: 100, quantity: 2, subtotal: 200 }],
        finalTotalAmount: 200,
      })
      .mockResolvedValueOnce({ items: [], finalTotalAmount: 0 });
    cartApi.removeItem.mockResolvedValue({});

    renderProvider();
    await waitFor(() => expect(captured.cart).toHaveLength(1));

    await act(async () => {
      await captured.removeFromCart('i1');
    });

    expect(cartApi.removeItem).toHaveBeenCalledWith('i1');
    await waitFor(() => expect(captured.cart).toHaveLength(0));
  });
});

describe('CartContext - calculateTotal', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    captured = undefined;
  });

  it('uses finalTotalAmount when provided by the server', async () => {
    cartApi.getMyCart.mockResolvedValue({
      items: [
        { id: 'i1', productName: 'A', price: 1000, quantity: 2, subtotal: 2000 },
        { id: 'i2', productName: 'B', price: 1500, quantity: 1, subtotal: 1500 },
      ],
      itemsTotalAmount: 3500,
      finalTotalAmount: 3200, // e.g. after a discount
    });

    renderProvider();
    await waitFor(() => expect(captured.cart).toHaveLength(2));

    expect(captured.cartTotal).toBe(3200);
    expect(captured.cartCount).toBe(3);
  });

  it('falls back to itemsTotalAmount when finalTotalAmount is missing', async () => {
    cartApi.getMyCart.mockResolvedValue({
      items: [{ id: 'i1', productName: 'A', price: 1000, quantity: 2, subtotal: 2000 }],
      itemsTotalAmount: 2000,
    });

    renderProvider();
    await waitFor(() => expect(captured.cart).toHaveLength(1));

    expect(captured.cartTotal).toBe(2000);
  });

  it('returns 0 when the cart is empty', async () => {
    cartApi.getMyCart.mockResolvedValue({ items: [], finalTotalAmount: 0 });

    renderProvider();
    await waitFor(() => expect(captured).toBeTruthy());

    expect(captured.cartTotal).toBe(0);
    expect(captured.cartCount).toBe(0);
  });
});

// =============================================================================
// Cart page UI - end-to-end through CartProvider with mocked cartApi
// =============================================================================
describe('Cart page UI', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders the empty state when the cart has no items', async () => {
    cartApi.getMyCart.mockResolvedValue({ items: [], finalTotalAmount: 0 });

    renderCartPage();

    await waitFor(() => {
      expect(screen.getByText(/giỏ hàng của bạn đang trống/i)).toBeInTheDocument();
    });
  });

  it('renders cart rows with formatted VND prices', async () => {
    cartApi.getMyCart.mockResolvedValue({
      items: [
        {
          id: 'i1',
          productName: 'Whey Protein',
          productImageUrl: 'http://x/p.png',
          price: 500000,
          quantity: 2,
          subtotal: 1000000,
        },
      ],
      finalTotalAmount: 1000000,
    });

    renderCartPage();

    expect(await screen.findByText('Whey Protein')).toBeInTheDocument();
    expect(screen.getByText('500.000đ')).toBeInTheDocument();
    // Two cells (item subtotal + summary total) both show 1.000.000đ
    expect(screen.getAllByText('1.000.000đ').length).toBeGreaterThanOrEqual(2);
  });

  it('+ button calls cartApi.updateItem with quantity + 1', async () => {
    cartApi.getMyCart.mockResolvedValue({
      items: [
        { id: 'i1', productName: 'A', price: 100, quantity: 2, subtotal: 200 },
      ],
      finalTotalAmount: 200,
    });
    cartApi.updateItem.mockResolvedValue({
      items: [{ id: 'i1', productName: 'A', price: 100, quantity: 3, subtotal: 300 }],
      finalTotalAmount: 300,
    });

    renderCartPage();
    await screen.findByText('A');

    fireEvent.click(screen.getByText('+'));

    await waitFor(() => {
      expect(cartApi.updateItem).toHaveBeenCalledWith('i1', 3);
    });
  });

  it('remove (X) button calls cartApi.removeItem with the item id', async () => {
    cartApi.getMyCart
      .mockResolvedValueOnce({
        items: [{ id: 'i1', productName: 'A', price: 100, quantity: 1, subtotal: 100 }],
        finalTotalAmount: 100,
      })
      .mockResolvedValueOnce({ items: [], finalTotalAmount: 0 });
    cartApi.removeItem.mockResolvedValue({});

    renderCartPage();
    await screen.findByText('A');

    fireEvent.click(document.querySelector('.ct-remove'));

    await waitFor(() => {
      expect(cartApi.removeItem).toHaveBeenCalledWith('i1');
    });
  });
});
