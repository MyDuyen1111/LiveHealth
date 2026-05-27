import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { X, Star, ShoppingBag, Facebook, Twitter, ChevronUp, ChevronDown } from 'lucide-react';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { useLang } from '../context/LanguageContext';
import { formatPrice } from '../utils/format';
import './QuickViewModal.css';

const QuickViewModal = ({ product, onClose }) => {
  const { addToCart } = useCart();
  const { isAuthenticated } = useAuth();
  const { t } = useLang();
  const navigate = useNavigate();
  const [qty, setQty] = useState(1);
  const [selectedImage, setSelectedImage] = useState(0);

  if (!product) return null;

  const img = product.imageUrl?.[0] || product.img || 'https://placehold.co/';
  const images = product.imageUrl || [img];
  const price = product.price || product.oldPrice || 0;
  const discount = product.promotion ? Math.round(product.promotion.discountValue || 0) : 0;

  const handleAdd = () => {
    if (Number(product?.stock) <= 0) {
      addToCart(product, qty);
      return;
    }

    if (!isAuthenticated) {
      onClose();
      navigate('/login');
      return;
    }
    addToCart(product, qty);
    onClose();
  };

  return (
    <>
      {/* Overlay */}
      <div className="qv-overlay" onClick={onClose} />

      {/* Modal */}
      <div className="qv-modal">
        <button className="qv-close" onClick={onClose} aria-label="Close quick view">
          <X size={22} />
        </button>

        <div className="qv-content">
          {/* Image section */}
          <div className="qv-gallery">
            <div className="qv-thumbs">
              <button className="qv-thumb-nav"><ChevronUp size={16} /></button>
              {images.slice(0, 3).map((imgUrl, i) => (
                <button key={i} className={`qv-thumb ${i === selectedImage ? 'active' : ''}`} onClick={() => setSelectedImage(i)} type="button">
                  <img src={imgUrl} alt={product.name} />
                </button>
              ))}
              <button className="qv-thumb-nav"><ChevronDown size={16} /></button>
            </div>
            <div className="qv-main-img">
              <img src={images[selectedImage] || img} alt={product.name} />
            </div>
          </div>

          {/* Info section */}
          <div className="qv-info">
            <div className="qv-title-row">
              <h2>{product.name}</h2>
              <span className="qv-stock-badge">{product.stock > 0 ? t('quickView.inStock') : 'Hết hàng'}</span>
            </div>

            <div className="qv-meta-row">
              <div className="qv-stars">
                {[...Array(5)].map((_, i) => (
                  <Star key={i} size={14} fill={i < 4 ? '#FF8A00' : '#DADADA'} color={i < 4 ? '#FF8A00' : '#DADADA'} />
                ))}
              </div>
              <span className="qv-review-count">• {t('quickView.review')}</span>
              <span className="qv-sku">• SKU: {product.sku || 'N/A'}</span>
            </div>

            <div className="qv-price-row">
              <span className="qv-price">{formatPrice(price)}</span>
              {discount > 0 && <span className="qv-discount">{discount}%{t('pd.off')}</span>}
            </div>

            <div className="qv-brand-row">
              <span>{t('quickView.brand')} <strong>{product.brand?.name || 'LiveHealth'}</strong></span>
              <div className="qv-share">
                <span>{t('quickView.shareItem')}</span>
                <a href="#" className="qv-social fb"><Facebook size={16} /></a>
                <a href="#" className="qv-social tw"><Twitter size={16} /></a>
              </div>
            </div>

            <p className="qv-desc">{product.description || t('quickView.desc')}</p>

            {/* Quantity & Add to Cart */}
            <div className="qv-action-row">
              <div className="qv-qty">
                <button onClick={() => setQty(Math.max(1, qty - 1))}>−</button>
                <span>{qty}</span>
                <button onClick={() => setQty(qty + 1)}>+</button>
              </div>
              <button className="qv-add-btn" onClick={handleAdd}>
                {t('quickView.addToCart')} <ShoppingBag size={18} />
              </button>
            </div>

            <div className="qv-extra">
              <p><strong>{t('quickView.category')}</strong> {product.category?.name || ''}</p>
              <p><strong>{t('quickView.tag')}</strong> {(product.tags || []).map(tg => tg.name).join(', ') || 'Organic'}</p>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default QuickViewModal;
