import { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Check } from 'lucide-react';
import { useLang } from '../context/LanguageContext';
import { orderApi } from '../api/orderApi';
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
                <thead><tr><th>{t('orderDetail.product')}</th><th>{t('orderDetail.price')}</th><th>{t('orderDetail.quantity')}</th><th>{t('orderDetail.subtotalCol')}</th></tr></thead>
                <tbody>
                  {items.length > 0 ? items.map(item => (
                    <tr key={item.id || item.productId}>
                      <td className="od-product-cell"><img src={item.productImageUrl || 'https://placehold.co/'} alt={item.productName}/><span>{item.productName}</span></td>
                      <td>{formatPrice(item.price)}</td>
                      <td>x{item.quantity}</td>
                      <td className="od-subtotal-val">{formatPrice(item.subtotal)}</td>
                    </tr>
                  )) : (
                    <tr>
                      <td colSpan={4} className="od-empty-products">{t('orderDetail.noProducts')}</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderDetail;
