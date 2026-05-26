import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { Star, ShoppingBag, ChevronUp, ChevronDown } from 'lucide-react';
import ProductCard from '../components/product/ProductCard';
import { productApi } from '../api/productApi';
import { reviewApi } from '../api/reviewApi';
import { productAttributeApi } from '../api/productAttributeApi';
import { formatPrice } from '../utils/format';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { useLang } from '../context/LanguageContext';
import './ProductDetail.css';

const ProductDetail = () => {
  const { id } = useParams();
  const { addToCart } = useCart();
  const { isAuthenticated } = useAuth();
  const { t } = useLang();
  const navigate = useNavigate();
  const [qty, setQty] = useState(1);
  const [activeTab, setActiveTab] = useState('desc');
  const [product, setProduct] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [attributes, setAttributes] = useState([]);
  const [related, setRelated] = useState([]);
  const [loading, setLoading] = useState(true);
  const [mainImg, setMainImg] = useState('');
  const [newRating, setNewRating] = useState(5);
  const [newComment, setNewComment] = useState('');

  useEffect(() => {
    setLoading(true);
    productApi.getById(id).then(data => {
      setProduct(data);
      setMainImg(data?.imageUrl?.[0] || '');
      // Fetch related products from same category
      if (data?.category?.id) {
        productApi.getAll(1, 5).then(pData => {
          setRelated((pData?.items || []).filter(p => p.id !== data.id).slice(0, 4));
        }).catch(() => {});
      }
    }).catch(() => setProduct(null)).finally(() => setLoading(false));

    reviewApi.getByProductId(id, 1, 10).then(data => {
      setReviews(data?.items || []);
    }).catch(() => {});

    productAttributeApi.getByProductId(id).then(data => {
      setAttributes(Array.isArray(data) ? data : []);
    }).catch(() => {});
  }, [id]);

  if (loading) return <div className="container" style={{ padding: '80px 0', textAlign: 'center' }}>Đang tải...</div>;
  if (!product) return <div className="container" style={{ padding: '80px 0', textAlign: 'center' }}>{t('pd.notFound')}</div>;

  const discount = product.oldPrice > 0 && product.promotion
    ? Math.round(product.promotion.discountValue || 0)
    : product.oldPrice > 0
    ? Math.round((1 - (product.oldPrice - (product.promotion?.discountValue || 0)) / product.oldPrice) * 100)
    : 0;

  const handleAdd = () => {
    if (Number(product?.stock) <= 0) {
      addToCart(product, qty);
      return;
    }

    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    addToCart(product, qty);
  };

  const handleSubmitReview = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) { navigate('/login'); return; }
    try {
      await reviewApi.createReview(id, newRating, newComment);
      const data = await reviewApi.getByProductId(id, 1, 10);
      setReviews(data?.items || []);
      setNewComment('');
      setNewRating(5);
    } catch {}
  };

  const images = product.imageUrl || [];
  const stockQuantity = Math.max(Number(product.stock) || 0, 0);

  return (
    <div className="pd">
      {/* Breadcrumb */}
      <div className="pd-breadcrumb">
        <div className="container pd-bc-inner">
          <Link to="/">🏠</Link><span>›</span><Link to="/shop">{t('pd.category')}</Link><span>›</span>
          <span className="bc-active">{product.name}</span>
        </div>
      </div>

      {/* Main content */}
      <div className="container pd-main">
        {/* Images */}
        <div className="pd-gallery">
          <div className="pd-thumbs">
            <button className="pd-thumb-nav"><ChevronUp size={18}/></button>
            {images.map((img, i) => (
              <div key={i} className={`pd-thumb ${mainImg === img ? 'active' : ''}`} onClick={() => setMainImg(img)}>
                <img src={img} alt={product.name}/>
              </div>
            ))}
            {images.length === 0 && (
              <div className="pd-thumb active">
                <img src="https://placehold.co/" alt={product.name}/>
              </div>
            )}
            <button className="pd-thumb-nav"><ChevronDown size={18}/></button>
          </div>
          <div className="pd-main-img">
            <img src={mainImg || 'https://placehold.co/'} alt={product.name}/>
          </div>
        </div>

        {/* Info */}
        <div className="pd-info">
          <div className="pd-title-row">
            <h1>{product.name}</h1>
            <div className="pd-stock-group">
              <span className={`pd-stock-badge ${stockQuantity > 0 ? '' : 'out'}`}>
                {stockQuantity > 0 ? t('pd.inStock') : t('pd.outOfStock')}
              </span>
              <span className="pd-stock-quantity">
                {t('pd.stockQuantity')} {stockQuantity}
              </span>
            </div>
          </div>
          <div className="pd-meta-row">
            <div className="pd-stars">
              {[...Array(5)].map((_, i) => (
                <Star key={i} size={14} fill={i < 4 ? '#FF8A00' : '#DADADA'} color={i < 4 ? '#FF8A00' : '#DADADA'}/>
              ))}
            </div>
            <span className="pd-review-count">{reviews.length} {t('pd.review')}</span>
            <span className="pd-sku">• SKU: {product.sku}</span>
          </div>

          <div className="pd-price-row">
            {product.oldPrice > 0 && <span className="pd-old-price">{formatPrice(product.oldPrice)}</span>}
            <span className="pd-price">{formatPrice(product.oldPrice)}</span>
            {discount > 0 && <span className="pd-discount">{discount}{t('pd.off')}</span>}
          </div>

          <div className="pd-brand-row">
            <span>{t('pd.brand')} <strong>{product.brand?.name || 'LiveHealth'}</strong></span>
          </div>

          <p className="pd-desc">{product.description}</p>

          {/* Quantity & Add to Cart */}
          <div className="pd-action-row">
            <div className="pd-qty">
              <button onClick={() => setQty(Math.max(1, qty - 1))}>−</button>
              <span>{qty}</span>
              <button onClick={() => setQty(qty + 1)}>+</button>
            </div>
            <button className="pd-add-btn" onClick={handleAdd}>
              {t('pd.addToCart')} <ShoppingBag size={18}/>
            </button>
          </div>

          <div className="pd-extra">
            <p><strong>{t('pd.category')}</strong> {product.category?.name || ''}</p>
            <p><strong>{t('pd.tag')}</strong> {(product.tags || []).map(tg => tg.name).join(', ') || 'Organic'}</p>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="container pd-tabs-wrap">
        <div className="pd-tab-nav">
          <button className={`pd-tab-btn ${activeTab === 'desc' ? 'active' : ''}`} onClick={() => setActiveTab('desc')}>{t('pd.descriptions')}</button>
          <button className={`pd-tab-btn ${activeTab === 'info' ? 'active' : ''}`} onClick={() => setActiveTab('info')}>{t('pd.additionalInfo')}</button>
          <button className={`pd-tab-btn ${activeTab === 'feedback' ? 'active' : ''}`} onClick={() => setActiveTab('feedback')}>{t('pd.customerFeedback')}</button>
        </div>

        <div className="pd-tab-content">
          {activeTab === 'desc' && (
            <div className="pd-tab-desc">
              <div className="pd-tab-text">
                <p>{product.description}</p>
              </div>
            </div>
          )}
          {activeTab === 'info' && (
            <div className="pd-tab-desc">
              <div className="pd-tab-info-table">
                <table>
                  <tbody>
                    {attributes.map(attr => (
                      <tr key={attr.id}>
                        <td>{attr.attributeKey}</td>
                        <td>{attr.attributeValue}</td>
                      </tr>
                    ))}
                    <tr><td>{t('pd.category')}</td><td>{product.category?.name || ''}</td></tr>
                    <tr><td>{t('pd.stockStatus')}</td><td>{stockQuantity > 0 ? `${t('pd.inStock')} (${stockQuantity})` : t('pd.outOfStock')}</td></tr>
                    <tr><td>{t('pd.tags')}</td><td>{(product.tags || []).map(tg => tg.name).join(', ')}</td></tr>
                  </tbody>
                </table>
              </div>
            </div>
          )}
          {activeTab === 'feedback' && (
            <div className="pd-tab-feedback">
              {reviews.map((r) => (
                <div key={r.id} className="pd-review-card">
                  <div className="pd-review-header">
                    <img src={`https://ui-avatars.com/api/?name=${encodeURIComponent(r.userFullName || 'U')}&size=40&rounded=true`} alt={r.userFullName} className="pd-review-avatar"/>
                    <div>
                      <strong>{r.userFullName}</strong>
                      <div className="pd-review-stars">
                        {[...Array(5)].map((_, j) => (
                          <Star key={j} size={12} fill={j < r.rating ? '#FF8A00' : '#DADADA'} color={j < r.rating ? '#FF8A00' : '#DADADA'}/>
                        ))}
                      </div>
                    </div>
                    <span className="pd-review-date">{r.createdAt ? new Date(r.createdAt).toLocaleDateString('vi-VN') : ''}</span>
                  </div>
                  <p className="pd-review-text">{r.comment}</p>
                </div>
              ))}
              {/* Review form */}
              {isAuthenticated && (
                <form onSubmit={handleSubmitReview} style={{ marginTop: '20px' }}>
                  <div style={{ marginBottom: '10px' }}>
                    <label>Rating: </label>
                    <select value={newRating} onChange={e => setNewRating(Number(e.target.value))}>
                      {[5,4,3,2,1].map(v => <option key={v} value={v}>{v} ★</option>)}
                    </select>
                  </div>
                  <textarea value={newComment} onChange={e => setNewComment(e.target.value)} placeholder="Viết đánh giá..." rows={3} style={{ width: '100%', padding: '8px' }}/>
                  <button type="submit" className="pd-add-btn" style={{ marginTop: '10px' }}>Gửi đánh giá</button>
                </form>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Related Products */}
      <div className="container pd-related">
        <h2>{t('pd.relatedProducts')}</h2>
        <div className="pd-related-grid">
          {related.map(p => <ProductCard key={p.id} product={p}/>)}
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;
