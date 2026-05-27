import { Link } from 'react-router-dom';
import { X } from 'lucide-react';
import { useCart } from '../context/CartContext';
import { useLang } from '../context/LanguageContext';
import { formatPrice } from '../utils/format';
import './Cart.css';

const Cart = () => {
  const { cart, removeFromCart, updateQuantity, cartTotal } = useCart();
  const { t } = useLang();

  if (cart.length === 0) {
    return (
      <div className="cart-page">
        <div className="cart-breadcrumb"><div className="container cart-bc-inner"><Link to="/">🏠</Link><span>›</span><span className="bc-active">{t('cart.breadcrumb')}</span></div></div>
        <div className="container cart-empty">
          <h1>{t('cart.title')}</h1>
          <p>{t('cart.empty')}</p>
          <Link to="/shop" className="cart-btn-green">{t('cart.backToShop')}</Link>
        </div>
      </div>
    );
  }

  return (
    <div className="cart-page">
      {/* Breadcrumb */}
      <div className="cart-breadcrumb">
        <div className="container cart-bc-inner">
          <Link to="/">🏠</Link><span>›</span><span className="bc-active">{t('cart.breadcrumb')}</span>
        </div>
      </div>

      <div className="container">
        <h1 className="cart-title">{t('cart.title')}</h1>

        <div className="cart-layout">
          {/* Table */}
          <div className="cart-table-wrap">
            <table className="cart-table">
              <thead>
                <tr>
                  <th>{t('cart.product')}</th>
                  <th>{t('cart.price')}</th>
                  <th>{t('cart.quantity')}</th>
                  <th>{t('cart.subtotal')}</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {cart.map(item => (
                  <tr key={item.id}>
                    <td className="ct-product">
                      <img src={item.productImageUrl || 'https://placehold.co/'} alt={item.productName}/>
                      <span>{item.productName}</span>
                    </td>
                    <td className="ct-price">{formatPrice(item.price)}</td>
                    <td>
                      <div className="ct-qty">
                        <button onClick={() => updateQuantity(item.id, item.quantity - 1)}>−</button>
                        <span>{item.quantity}</span>
                        <button onClick={() => updateQuantity(item.id, item.quantity + 1)}>+</button>
                      </div>
                    </td>
                    <td className="ct-subtotal">{formatPrice(item.subtotal)}</td>
                    <td><button className="ct-remove" onClick={() => removeFromCart(item.id)}><X size={18}/></button></td>
                  </tr>
                ))}
              </tbody>
            </table>

            <div className="cart-actions">
              <Link to="/shop" className="cart-btn-outline">{t('cart.returnToShop')}</Link>
            </div>
          </div>

          {/* Sidebar */}
          <div className="cart-summary">
            <h3>{t('cart.cartTotal')}</h3>
            <div className="cs-row"><span>{t('cart.subtotalLabel')}</span><span>{formatPrice(cartTotal)}</span></div>
            <div className="cs-row"><span>{t('cart.shipping')}</span><span className="cs-free">{t('cart.free')}</span></div>
            <div className="cs-row cs-total"><span>{t('cart.total')}</span><span>{formatPrice(cartTotal)}</span></div>
            <Link to="/checkout" className="cart-btn-green full">{t('cart.proceedToCheckout')}</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Cart;
