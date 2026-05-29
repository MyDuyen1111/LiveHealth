import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useWishlist } from '../context/WishlistContext';
import { useCart } from '../context/CartContext';
import { useLang } from '../context/LanguageContext';
import { useAuth } from '../context/AuthContext';
import { formatPrice } from '../utils/format';
import { productApi } from '../api/productApi';
import { Heart, ShoppingBag, Trash2 } from 'lucide-react';
import './Wishlist.css';

const Wishlist = () => {
  const { wishlist, removeFromWishlist } = useWishlist();
  const { addToCart } = useCart();
  const { t } = useLang();
  const { isAuthenticated } = useAuth();
  const [realtimeWishlist, setRealtimeWishlist] = useState(wishlist);

  useEffect(() => {
    if (wishlist.length === 0) {
      setRealtimeWishlist([]);
      return;
    }

    // Set initial fallback items to prevent empty screen flash
    setRealtimeWishlist(prev => {
      // Keep existing fetched properties, or map new items from context
      return wishlist.map(item => {
        const found = prev.find(p => p.id === item.id);
        return found ? { ...item, ...found } : item;
      });
    });

    // Fetch latest info for all wishlist items
    Promise.all(
      wishlist.map(item =>
        productApi.getById(item.id)
          .then(latestProduct => {
            return {
              ...item,
              ...latestProduct,
            };
          })
          .catch(err => {
            console.error(`Failed to refresh wishlist item ${item.id}:`, err);
            return item;
          })
      )
    ).then(refreshedItems => {
      setRealtimeWishlist(refreshedItems);
    });
  }, [wishlist]);

  const handleAddToCart = (product) => {
    addToCart(product);
  };

  return (
    <div className="wl-wrap">
      {/* Breadcrumb */}
      <div className="wl-breadcrumb">
        <div className="container wl-bc-inner">
          <Link to="/">🏠</Link>
          <span>›</span>
          <span className="bc-active">{t('wishlist.breadcrumb')}</span>
        </div>
      </div>

      <div className="container wl-section">
        <h2 className="wl-page-title">{t('wishlist.title')}</h2>

        {wishlist.length === 0 ? (
          <div className="wl-empty-card">
            <div className="wl-empty-icon">
              <Heart size={64} strokeWidth={1} />
            </div>
            <h3>{t('wishlist.empty')}</h3>
            <Link to="/shop" className="wl-continue-btn">
              {t('wishlist.continueShopping')}
            </Link>
          </div>
        ) : (
          <div className="wl-table-container">
            <table className="wl-table">
              <thead>
                <tr>
                  <th>{t('wishlist.product')}</th>
                  <th>{t('wishlist.price')}</th>
                  <th>{t('wishlist.stockStatus')}</th>
                  <th>{t('wishlist.action')}</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {realtimeWishlist.map((product) => {
                  const img = product.imageUrl?.[0] || product.img || 'https://placehold.co/';
                  const stockQuantity = Math.max(Number(product.stock) || 0, 0);
                  const isOutOfStock = stockQuantity === 0;

                  return (
                    <tr key={product.id} className="wl-row">
                      <td className="wl-product-cell">
                        <Link to={`/product/${product.id}`} className="wl-img-link">
                          <img src={img} alt={product.name} className="wl-img" />
                        </Link>
                        <div className="wl-info">
                          <Link to={`/product/${product.id}`} className="wl-name">
                            {product.name}
                          </Link>
                        </div>
                      </td>
                      <td className="wl-price-cell">
                        <span className="wl-price">{formatPrice(product.price || product.oldPrice || 0)}</span>
                        {product.oldPrice > 0 && product.price < product.oldPrice && (
                          <span className="wl-old-price">{formatPrice(product.oldPrice)}</span>
                        )}
                      </td>
                      <td className="wl-stock-cell">
                        <span className={`wl-stock-badge ${isOutOfStock ? 'out' : 'in'}`}>
                          {isOutOfStock ? t('wishlist.outOfStock') : t('wishlist.inStock')}
                        </span>
                      </td>
                      <td className="wl-action-cell">
                        <button
                          className="wl-cart-btn"
                          disabled={isOutOfStock}
                          onClick={() => handleAddToCart(product)}
                        >
                          <span>{t('wishlist.addToCart')}</span>
                          <ShoppingBag size={16} />
                        </button>
                      </td>
                      <td className="wl-remove-cell">
                        <button
                          className="wl-delete-btn"
                          onClick={() => removeFromWishlist(product.id)}
                          aria-label="Remove from wishlist"
                        >
                          <Trash2 size={18} />
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default Wishlist;

