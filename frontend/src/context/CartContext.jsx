import { createContext, useState, useContext, useEffect, useCallback } from 'react';
import { useAuth } from './AuthContext';
import { cartApi } from '../api/cartApi';
import './CartContext.css';

const CartContext = createContext();
const OUT_OF_STOCK_MESSAGE = 'Sản phẩm đã hết hàng.';
const QUANTITY_EXCEEDED_MESSAGE = 'Số lượng vượt quá số lượng hiện có.';

export function CartProvider({ children }) {
  const { isAuthenticated } = useAuth();
  const [cartData, setCartData] = useState(null);
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [cartNotice, setCartNotice] = useState('');

  const openCart = () => setIsCartOpen(true);
  const closeCart = () => setIsCartOpen(false);
  const showCartNotice = (message) => setCartNotice(message);

  useEffect(() => {
    if (!cartNotice) return undefined;

    const timeoutId = window.setTimeout(() => {
      setCartNotice('');
    }, 3000);

    return () => window.clearTimeout(timeoutId);
  }, [cartNotice]);

  const fetchCart = useCallback(async () => {
    if (!isAuthenticated) {
      setCartData(null);
      return;
    }
    try {
      const data = await cartApi.getMyCart();
      setCartData(data);
    } catch {
      setCartData(null);
    }
  }, [isAuthenticated]);

  useEffect(() => {
    fetchCart();
  }, [fetchCart]);

  const cart = cartData?.items || [];
  const cartTotal = cartData?.finalTotalAmount || cartData?.itemsTotalAmount || 0;
  const cartCount = cart.reduce((count, item) => count + item.quantity, 0);

  const addToCart = async (product, quantity = 1) => {
    const stock = Number(product?.stock);
    const requestedQuantity = Number(quantity) || 1;

    if (Number.isFinite(stock) && stock <= 0) {
      showCartNotice(OUT_OF_STOCK_MESSAGE);
      return { success: false, error: OUT_OF_STOCK_MESSAGE };
    }

    if (Number.isFinite(stock)) {
      const existingQuantity = cart.find(item => item.productId === product?.id)?.quantity || 0;
      if (requestedQuantity > stock || existingQuantity + requestedQuantity > stock) {
        showCartNotice(QUANTITY_EXCEEDED_MESSAGE);
        return { success: false, error: QUANTITY_EXCEEDED_MESSAGE };
      }
    }

    try {
      const data = await cartApi.addItem(product.id, requestedQuantity);
      setCartData(data);
      openCart();
      return { success: true };
    } catch (err) {
      console.error('Add to cart failed:', err);
      showCartNotice(err.message || 'Thêm vào giỏ chưa thành công.');
      return { success: false, error: err.message };
    }
  };

  const removeFromCart = async (itemId) => {
    try {
      await cartApi.removeItem(itemId);
      await fetchCart();
    } catch (err) {
      console.error('Remove from cart failed:', err);
    }
  };

  const updateQuantity = async (itemId, quantity) => {
    if (quantity < 1) {
      return removeFromCart(itemId);
    }
    try {
      const data = await cartApi.updateItem(itemId, quantity);
      setCartData(data);
    } catch (err) {
      console.error('Update quantity failed:', err);
      showCartNotice(err.message || 'Cập nhật giỏ hàng chưa thành công.');
    }
  };

  const clearCart = async () => {
    try {
      await cartApi.clearCart();
      setCartData(null);
    } catch (err) {
      console.error('Clear cart failed:', err);
    }
  };

  return (
    <CartContext.Provider
      value={{
        cart,
        cartData,
        addToCart,
        removeFromCart,
        updateQuantity,
        clearCart,
        cartTotal,
        cartCount,
        isCartOpen,
        openCart,
        closeCart,
        fetchCart,
      }}
    >
      {children}
      {cartNotice && (
        <div className="cart-notice" role="alert">
          {cartNotice}
        </div>
      )}
    </CartContext.Provider>
  );
}

export const useCart = () => useContext(CartContext);
