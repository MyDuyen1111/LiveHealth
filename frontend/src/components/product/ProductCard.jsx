import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { ShoppingBag, Star, Eye, Heart } from 'lucide-react';
import { useCart } from '../../context/CartContext';
import { useAuth } from '../../context/AuthContext';
import { useLang } from '../../context/LanguageContext';
import { useWishlist } from '../../context/WishlistContext';
import { formatPrice } from '../../utils/format';
import QuickViewModal from '../QuickViewModal';
import './ProductCard.css';

const ProductCard = ({ product }) => {
  const { addToCart } = useCart();
  const { isAuthenticated } = useAuth();
  const { t } = useLang();
  const { toggleWishlist, isInWishlist } = useWishlist();
  const navigate = useNavigate();
  const [showQuickView, setShowQuickView] = useState(false);

  const img = product.imageUrl?.[0] || product.img || 'https://placehold.co/';
  const price = product.price || product.oldPrice || 0;
  const oldPrice = product.promotion ? product.oldPrice : (product.oldPrice && product.oldPrice !== product.price ? product.oldPrice : 0);
  const hasSale = !!product.promotion || (product.oldPrice > 0 && product.price && product.price < product.oldPrice);
  const isFavorite = isInWishlist(product.id);

  const handleAddToCart = () => {
    if (Number(product?.stock) <= 0) {
      addToCart(product);
      return;
    }

    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    addToCart(product);
  };

  return (
    <>
      <div className="pc">
        <div className="pc-img-wrap">
          {hasSale && (
            <span className="pc-badge">{t('product.sale')}</span>
          )}
          <Link to={`/product/${product.id}`}>
            <img src={img} alt={product.name} className="pc-img" />
          </Link>
          <div className="pc-hover-icons">
            <button 
              className="pc-icon-btn" 
              onClick={() => toggleWishlist(product)} 
              aria-label="Add to wishlist"
              style={{ color: isFavorite ? '#ea4335' : '' }}
            >
              <Heart size={18} fill={isFavorite ? '#ea4335' : 'none'} />
            </button>
            <button className="pc-icon-btn" onClick={() => setShowQuickView(true)} aria-label="Quick view"><Eye size={18}/></button>
          </div>
        </div>
        <div className="pc-body">
          <div className="pc-info">
            <Link to={`/product/${product.id}`} className="pc-name">{product.name}</Link>
            <div className="pc-prices">
              <span className="pc-price">{formatPrice(price)}</span>
              {oldPrice > 0 && <span className="pc-old">{formatPrice(oldPrice)}</span>}
            </div>
            <div className="pc-stars">
              {[...Array(5)].map((_, i) => (
                <Star key={i} size={12} fill={i < (product.rating || 4) ? '#FF8A00' : '#DADADA'} color={i < (product.rating || 4) ? '#FF8A00' : '#DADADA'}/>
              ))}
            </div>
          </div>
          <button className="pc-cart-btn" onClick={handleAddToCart} aria-label={t('quickView.addToCart')}>
            <ShoppingBag size={20}/>
          </button>
        </div>
      </div>

      {showQuickView && (
        <QuickViewModal product={product} onClose={() => setShowQuickView(false)} />
      )}
    </>
  );
};

export default ProductCard;
