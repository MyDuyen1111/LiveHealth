import { Link } from 'react-router-dom';
import { X, ShoppingBag } from 'lucide-react';
import { useCart } from '../context/CartContext';
import { useLang } from '../context/LanguageContext';
import { formatPrice } from '../utils/format';
import './CartSidebar.css';

const CartSidebar = () => {
  const { cart, removeFromCart, cartTotal, cartCount, isCartOpen, closeCart } = useCart();
  const { t } = useLang();

  return (
    <>
      {/* Overlay */}
      <div
        className={`cart-sidebar-overlay ${isCartOpen ? 'open' : ''}`}
        onClick={closeCart}
      />

      {/* Panel */}
      <div className={`cart-sidebar ${isCartOpen ? 'open' : ''}`}>
        {/* Header */}
        <div className="cs-header">
          <h3>{t('cartSidebar.title')} <span>({cartCount})</span></h3>
          <button className="cs-close-btn" onClick={closeCart} aria-label="Close cart">
            <X size={20} />
          </button>
        </div>

        {/* Items */}
        {cart.length === 0 ? (
          <div className="cs-empty">
            <ShoppingBag size={48} />
            <p>{t('cartSidebar.empty')}</p>
          </div>
        ) : (
          <div className="cs-items">
            {cart.map(item => (
              <div className="cs-item" key={item.id}>
                <img
                  src={item.productImageUrl || 'https://placehold.co/'}
                  alt={item.productName}
                  className="cs-item-img"
                />
                <div className="cs-item-info">
                  <p className="cs-item-name">{item.productName}</p>
                  <p className="cs-item-meta">
                    {item.quantity} x <strong>{formatPrice(item.price)}</strong>
                  </p>
                </div>
                <button
                  className="cs-item-remove"
                  onClick={() => removeFromCart(item.id)}
                  aria-label="Remove item"
                >
                  <X size={16} />
                </button>
              </div>
            ))}
          </div>
        )}

        {/* Footer */}
        {cart.length > 0 && (
          <div className="cs-footer">
            <div className="cs-total-row">
              <span>{cartCount} {t('cartSidebar.product')}</span>
              <span>{formatPrice(cartTotal)}</span>
            </div>
            <Link to="/checkout" className="cs-checkout-btn" onClick={closeCart}>
              {t('cartSidebar.checkout')}
            </Link>
            <Link to="/cart" className="cs-goto-btn" onClick={closeCart}>
              {t('cartSidebar.goToCart')}
            </Link>
          </div>
        )}
      </div>
    </>
  );
};

export default CartSidebar;
