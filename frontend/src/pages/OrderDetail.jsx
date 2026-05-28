import { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Check, Star } from 'lucide-react';
import { useLang } from '../context/LanguageContext';
import { orderApi } from '../api/orderApi';
import { reviewApi } from '../api/reviewApi';
import { formatPrice } from '../utils/format';
import AccountSidebar from '../components/AccountSidebar';
import './OrderDetail.css';

const steps = [
  { key: 'orderReceived', stepNum: '01' },
  { key: 'processing', stepNum: '02' },
  { key: 'onTheWay', stepNum: '03' },
  { key: 'delivered', stepNum: '04' },
];

const statusToStep = {
  PENDING: 1, RECEIVED: 1, PROCESSING: 2,
  ON_THE_WAY: 3, SHIPPED: 3, DELIVERED: 4, COMPLETED: 4,
};

const cancellableStatuses = ['ORDER_RECEIVED', 'PROCESSING'];

const getAddressText = (address, zipLabel) => [
  address.streetAddress || address.street,
  address.state || address.city,
  address.country,
  address.zipCode ? `${zipLabel}: ${address.zipCode}` : '',
].filter(Boolean).join(', ');

const OrderDetail = () => {
  const { t } = useLang();
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [canceling, setCanceling] = useState(false);
  const [cancelMessage, setCancelMessage] = useState('');
  const [cancelError, setCancelError] = useState('');
  
  const [reviewProduct, setReviewProduct] = useState(null);
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState('');
  const [submittingReview, setSubmittingReview] = useState(false);

  useEffect(() => {
    orderApi.getOrderById(id)
      .then(d => setOrder(d))
      .catch(() => setOrder(null))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="container" style={{ padding: '80px 0', textAlign: 'center' }}>Đang tải...</div>;
  if (!order) return <div className="container" style={{ padding: '80px 0', textAlign: 'center' }}>Không tìm thấy đơn hàng.</div>;

  const currentStep = statusToStep[order.status] || 1;
  const items = order.orderItems || order.items || [];
  const billing = order.billingAddress || {};
  const shipping = order.shippingAddress || billing;
  const contactEmail = order.contactEmail || billing.email || shipping.email || '';
  const contactPhone = order.contactPhone || billing.phone || shipping.phone || '';
  const orderNumber = order.orderNumber || (order.id ? `#${order.id.substring(0, 6)}` : '');
  const paymentMethodName = order.paymentMethodName || order.paymentMethod?.name || 'N/A';
  const subtotal = order.subtotal ?? items.reduce((sum, item) => sum + (item.subtotal || 0), 0);
  const shippingFee = order.shippingFee ?? order.shippingCost ?? 0;
  const canCancel = cancellableStatuses.includes(order.status);
  const showReviewAction = order.status === 'DELIVERED' || order.status === 'COMPLETED';

  const handleCancelOrder = async () => {
    if (!window.confirm(t('orderDetail.cancelConfirm'))) return;

    setCanceling(true);
    setCancelError('');
    setCancelMessage('');

    try {
      await orderApi.cancelOrder(order.id);
      const refreshedOrder = await orderApi.getOrderById(order.id);
      setOrder(refreshedOrder);
      setCancelMessage(t('orderDetail.cancelSuccess'));
    } catch (err) {
      setCancelError(t(err.message) || err.message || t('orderDetail.cancelError'));
    } finally {
      setCanceling(false);
    }
  };

  const handleReviewSubmit = async (e) => {
    e.preventDefault();
    if (!reviewProduct) return;
    setSubmittingReview(true);
    try {
      const pId = reviewProduct.productId || reviewProduct.id;
      await reviewApi.createReview(pId, rating, comment);
      alert(t('review.success'));
      setReviewProduct(null);
    } catch (err) {
      alert(err.message || t('review.error'));
    } finally {
      setSubmittingReview(false);
    }
  };

  return (
    <div className="od-wrap">
      <div className="od-breadcrumb">
        <div className="container od-bc-inner">
          <Link to="/">🏠</Link><span>›</span>
          <span>{t('account.breadcrumb')}</span><span>›</span>
          <Link to="/account/orders">{t('orderHistory.breadcrumb')}</Link><span>›</span>
          <span className="bc-active">{t('orderDetail.breadcrumb')}</span>
        </div>
      </div>

      <div className="container od-layout">
        <AccountSidebar activeItem="orders" />
        <div className="od-main">
          <div className="od-card">
            <div className="od-header">
              <div className="od-header-left">
                <h2 className="od-title">{t('orderDetail.title')}</h2>
                <span className="od-meta">• {order.createdAt ? new Date(order.createdAt).toLocaleDateString('vi-VN') : ''} • {items.length} {t('dashboard.products')}</span>
              </div>
              <div className="od-header-actions">
                {canCancel && (
                  <button
                    type="button"
                    className="od-cancel-btn"
                    onClick={handleCancelOrder}
                    disabled={canceling}
                  >
                    {canceling ? t('orderDetail.canceling') : t('orderDetail.cancelOrder')}
                  </button>
                )}
                <Link to="/account/orders" className="od-back-link">{t('orderDetail.backToList')}</Link>
              </div>
            </div>

            {cancelMessage && <div className="od-alert od-alert-success">{cancelMessage}</div>}
            {cancelError && <div className="od-alert od-alert-error">{cancelError}</div>}

            <div className="od-info-row">
              <div className="od-address-card">
                <span className="od-label">{t('orderDetail.billingAddress')}</span>
                <h4>{order.userFullName || 'Khách hàng'}</h4>
                <p>{getAddressText(billing, t('orderDetail.zipCode')) || 'N/A'}</p>
                <div className="od-addr-detail"><span className="od-addr-label">EMAIL</span><p>{contactEmail || 'N/A'}</p></div>
                <div className="od-addr-detail"><span className="od-addr-label">PHONE</span><p>{contactPhone || 'N/A'}</p></div>
              </div>
              <div className="od-address-card">
                <span className="od-label">{t('orderDetail.shippingAddress')}</span>
                <h4>{order.userFullName || 'Khách hàng'}</h4>
                <p>{getAddressText(shipping, t('orderDetail.zipCode')) || 'N/A'}</p>
                <div className="od-addr-detail"><span className="od-addr-label">EMAIL</span><p>{contactEmail || 'N/A'}</p></div>
                <div className="od-addr-detail"><span className="od-addr-label">PHONE</span><p>{contactPhone || 'N/A'}</p></div>
              </div>
              <div className="od-summary-card">
                <div className="od-summary-row"><span className="od-summary-label">{t('orderDetail.orderId')}</span><span className="od-summary-value">{orderNumber}</span></div>
                <div className="od-summary-row"><span className="od-summary-label">{t('orderDetail.status')}</span><span className={`od-status od-status-${String(order.status).toLowerCase()}`}>{t(`orderDetail.status.${order.status}`)}</span></div>
                <div className="od-summary-row"><span className="od-summary-label">{t('orderDetail.paymentMethod')}</span><span className="od-summary-value">{paymentMethodName}</span></div>
                <div className="od-summary-divider" />
                <div className="od-summary-row"><span className="od-summary-label">{t('orderDetail.subtotal')}</span><span className="od-summary-value">{formatPrice(subtotal)}</span></div>
                <div className="od-summary-row"><span className="od-summary-label">{t('orderDetail.shipping')}</span><span className="od-summary-value">{shippingFee > 0 ? formatPrice(shippingFee) : t('orderDetail.free')}</span></div>
                <div className="od-summary-divider" />
                <div className="od-summary-row od-total-row"><span className="od-summary-label">{t('orderDetail.total')}</span><span className="od-total-value">{formatPrice(order.totalAmount || 0)}</span></div>
              </div>
            </div>

            <div className="od-tracker">
              {steps.map((step, i) => {
                const isDone = i + 1 < currentStep;
                const isCurrent = i + 1 === currentStep;
                return (
                  <div key={step.key} className={`od-step ${isDone ? 'done' : ''} ${isCurrent ? 'current' : ''}`}>
                    <div className="od-step-circle">{isDone ? <Check size={16}/> : <span>{step.stepNum}</span>}</div>
                    {i < steps.length - 1 && <div className="od-step-line" />}
                    <p className="od-step-label">{t(`orderDetail.${step.key}`)}</p>
                  </div>
                );
              })}
            </div>

            <div className="od-products-table-wrap">
              <table className="od-products-table">
                <thead>
                  <tr>
                    <th>{t('orderDetail.product')}</th>
                    <th>{t('orderDetail.price')}</th>
                    <th>{t('orderDetail.quantity')}</th>
                    <th>{t('orderDetail.subtotalCol')}</th>
                    {showReviewAction && <th>{t('review.write')}</th>}
                  </tr>
                </thead>
                <tbody>
                  {items.length > 0 ? items.map(item => (
                    <tr key={item.id || item.productId}>
                      <td className="od-product-cell">
                        <img src={item.productImageUrl || 'https://placehold.co/'} alt={item.productName}/>
                        <span>{item.productName}</span>
                      </td>
                      <td>{formatPrice(item.price)}</td>
                      <td>x{item.quantity}</td>
                      <td className="od-subtotal-val">{formatPrice(item.subtotal)}</td>
                      {showReviewAction && (
                        <td>
                          <button
                            type="button"
                            className="od-review-btn"
                            onClick={() => {
                              setReviewProduct(item);
                              setRating(5);
                              setComment('');
                            }}
                          >
                            {t('review.write')}
                          </button>
                        </td>
                      )}
                    </tr>
                  )) : (
                    <tr>
                      <td colSpan={showReviewAction ? 5 : 4} className="od-empty-products">{t('orderDetail.noProducts')}</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>

      {/* Review Modal */}
      {reviewProduct && (
        <div className="adm-modal-overlay" style={{ zIndex: 300 }} onClick={() => setReviewProduct(null)}>
          <div className="adm-modal" style={{ maxWidth: 500 }} onClick={e => e.stopPropagation()}>
            <h3 className="adm-modal-title">{t('review.write')}</h3>
            <p style={{ fontSize: '13px', color: '#666', marginBottom: '15px' }}>{reviewProduct.productName}</p>
            
            <form onSubmit={handleReviewSubmit}>
              <div style={{ marginBottom: '15px', display: 'flex', alignItems: 'center', gap: '10px' }}>
                <span style={{ fontSize: '14px', fontWeight: '500' }}>{t('review.rating')}:</span>
                <div style={{ display: 'flex', gap: '6px' }}>
                  {[1, 2, 3, 4, 5].map((star) => (
                    <button
                      type="button"
                      key={star}
                      onClick={() => setRating(star)}
                      style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 0 }}
                    >
                      <Star size={24} fill={star <= rating ? '#FF8A00' : 'none'} color={star <= rating ? '#FF8A00' : '#DADADA'} />
                    </button>
                  ))}
                </div>
              </div>

              <div style={{ marginBottom: '20px' }}>
                <label style={{ display: 'block', fontSize: '14px', fontWeight: '500', marginBottom: '6px' }}>{t('review.comment')}:</label>
                <textarea
                  value={comment}
                  onChange={e => setComment(e.target.value)}
                  placeholder="Viết nhận xét của bạn..."
                  rows={4}
                  style={{
                    width: '100%',
                    padding: '10px',
                    borderRadius: '8px',
                    border: '1px solid #d0d5dd',
                    fontSize: '14px',
                    outline: 'none',
                    fontFamily: 'inherit',
                    resize: 'vertical'
                  }}
                  required
                />
              </div>

              <div className="adm-modal-actions">
                <button 
                  type="button" 
                  className="adm-btn adm-btn-outline" 
                  onClick={() => setReviewProduct(null)}
                  disabled={submittingReview}
                >
                  {t('admin.close')}
                </button>
                <button 
                  type="submit" 
                  className="adm-btn" 
                  disabled={submittingReview}
                >
                  {submittingReview ? 'Đang gửi...' : t('review.submit')}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default OrderDetail;
