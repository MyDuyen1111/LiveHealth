import { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useLang } from '../context/LanguageContext';
import { paymentApi } from '../api/paymentApi';
import { formatPrice } from '../utils/format';
import './PaymentPage.css';

const QUICK_AMOUNTS = [50000, 100000, 200000, 500000, 1000000, 2000000];

const PaymentPage = () => {
  const { t } = useLang();
  const location = useLocation();

  // Nhận dữ liệu từ Checkout redirect (nếu có)
  const orderState = location.state || {};
  const fromCheckout = !!orderState.orderId;

  const [amount, setAmount] = useState(
    orderState.amount ? String(orderState.amount) : ''
  );
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Nếu từ checkout, lock amount (không cho sửa)
  const numericAmount = parseInt(amount.replace(/\D/g, ''), 10) || 0;

  useEffect(() => {
    if (orderState.amount) {
      setAmount(String(orderState.amount));
    }
  }, []);

  const handleQuickAmount = (val) => {
    if (fromCheckout) return; // lock khi đến từ checkout
    setAmount(val.toString());
    setError('');
  };

  const handleAmountChange = (e) => {
    if (fromCheckout) return; // lock khi đến từ checkout
    const raw = e.target.value.replace(/\D/g, '');
    setAmount(raw);
    setError('');
  };

  const handlePay = async () => {
    if (numericAmount < 10000) {
      setError(t('payment.errMinAmount'));
      return;
    }
    setLoading(true);
    setError('');
    try {
      const paymentUrl = await paymentApi.createPayment(numericAmount);
      if (paymentUrl && typeof paymentUrl === 'string') {
        window.location.href = paymentUrl;
      } else {
        setError(t('payment.errNoUrl'));
      }
    } catch (err) {
      if (err.status === 403) {
        setError(t('payment.err403'));
      } else {
        setError(err.message || t('payment.errDefault'));
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="pay-page">
      {/* Breadcrumb */}
      <div className="co-breadcrumb">
        <div className="container co-bc-inner">
          <Link to="/">🏠</Link>
          <span>›</span>
          {fromCheckout && (
            <>
              <Link to="/checkout">{t('payment.breadcrumbCheckout') || 'Thanh toán'}</Link>
              <span>›</span>
            </>
          )}
          <span className="bc-active">{t('payment.breadcrumb')}</span>
        </div>
      </div>

      <div className="container pay-container">
        {/* Left — payment form */}
        <div className="pay-card">

          {/* Order info banner (chỉ hiện khi đến từ Checkout) */}
          {fromCheckout && (
            <div className="pay-order-banner">
              <div className="pay-order-icon">🛍️</div>
              <div className="pay-order-info">
                <p className="pay-order-label">
                  {t('payment.orderCreated') || 'Đơn hàng đã được tạo'}
                </p>
                {orderState.orderNumber && (
                  <p className="pay-order-num">
                    #{orderState.orderNumber}
                  </p>
                )}
                <p className="pay-order-hint">
                  {t('payment.completePayment') || 'Vui lòng hoàn tất thanh toán qua VNPay để xác nhận đơn hàng.'}
                </p>
              </div>
            </div>
          )}

          {/* Header */}
          <div className="pay-header">
            <div className="pay-logo-wrap">
              <img
                src="https://vnpay.vn/s1/statics.vnpay.vn/2023/9/06ncktiwd6dc1694418196384.png"
                alt="VNPay"
                className="pay-vnpay-logo"
              />
            </div>
            <h1 className="pay-title">{t('payment.title')}</h1>
            <p className="pay-subtitle">{t('payment.subtitle')}</p>
          </div>

          {/* Amount input */}
          <div className="pay-section">
            <label className="pay-label" htmlFor="pay-amount">
              {t('payment.amountLabel')}
            </label>
            <div className="pay-input-wrap">
              <input
                id="pay-amount"
                type="text"
                inputMode="numeric"
                className={`pay-input ${fromCheckout ? 'pay-input--locked' : ''} ${error ? 'pay-input--error' : ''}`}
                value={numericAmount > 0 ? numericAmount.toLocaleString('vi-VN') : ''}
                onChange={handleAmountChange}
                placeholder={t('payment.amountPlaceholder')}
                readOnly={fromCheckout}
              />
              <span className="pay-currency">{t('payment.currency')}</span>
              {fromCheckout && (
                <span className="pay-lock-icon" title="Số tiền được tính từ đơn hàng">🔒</span>
              )}
            </div>
            {error && (
              <div className={`pay-error ${error.includes('403') || error.includes('Admin') ? 'pay-error--403' : ''}`}>
                {error}
              </div>
            )}

            {/* Quick pick — ẩn khi từ checkout */}
            {!fromCheckout && (
              <>
                <div className="pay-quick-label">{t('payment.quickLabel')}</div>
                <div className="pay-quick-grid">
                  {QUICK_AMOUNTS.map((v) => (
                    <button
                      key={v}
                      type="button"
                      className={`pay-quick-btn ${numericAmount === v ? 'active' : ''}`}
                      onClick={() => handleQuickAmount(v)}
                    >
                      {formatPrice(v)}
                    </button>
                  ))}
                </div>
              </>
            )}
          </div>

          {/* Summary */}
          {numericAmount >= 10000 && (
            <div className="pay-summary">
              <div className="pay-summary-row">
                <span>{t('payment.summaryAmount')}</span>
                <span className="pay-summary-val">{formatPrice(numericAmount)}</span>
              </div>
              <div className="pay-summary-row">
                <span>{t('payment.summaryMethod')}</span>
                <span className="pay-summary-val">VNPay</span>
              </div>
              {fromCheckout && orderState.orderNumber && (
                <div className="pay-summary-row">
                  <span>{t('payment.summaryOrder') || 'Đơn hàng'}</span>
                  <span className="pay-summary-val">#{orderState.orderNumber}</span>
                </div>
              )}
            </div>
          )}

          {/* CTA */}
          <button
            id="btn-pay-vnpay"
            className="pay-btn"
            onClick={handlePay}
            disabled={loading || numericAmount < 10000}
          >
            {loading ? (
              <span className="pay-spinner" />
            ) : (
              <>
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2">
                  <rect x="1" y="4" width="22" height="16" rx="2" ry="2"/>
                  <line x1="1" y1="10" x2="23" y2="10"/>
                </svg>
                {t('payment.payBtn')}
              </>
            )}
          </button>

          <p className="pay-secure-note">{t('payment.secureNote')}</p>

          {/* Back to checkout (nếu từ checkout) */}
          {fromCheckout && (
            <Link to="/checkout" className="pay-back-link">
              ← {t('payment.backToCheckout') || 'Quay lại trang đặt hàng'}
            </Link>
          )}
        </div>

        {/* Right — info panel (ẩn khi từ checkout để gọn) */}
        {!fromCheckout && (
          <div className="pay-info">
            <h2 className="pay-info-title">{t('payment.guideTitle')}</h2>
            <ol className="pay-steps">
              <li>
                <span className="pay-step-num">1</span>
                <div><strong>{t('payment.step1Title')}</strong><p>{t('payment.step1Desc')}</p></div>
              </li>
              <li>
                <span className="pay-step-num">2</span>
                <div><strong>{t('payment.step2Title')}</strong><p>{t('payment.step2Desc')}</p></div>
              </li>
              <li>
                <span className="pay-step-num">3</span>
                <div><strong>{t('payment.step3Title')}</strong><p>{t('payment.step3Desc')}</p></div>
              </li>
              <li>
                <span className="pay-step-num">4</span>
                <div><strong>{t('payment.step4Title')}</strong><p>{t('payment.step4Desc')}</p></div>
              </li>
            </ol>

            <div className="pay-banks">
              <p className="pay-banks-title">{t('payment.banksTitle')}</p>
              <div className="pay-bank-badges">
                <span>{t('payment.bank1')}</span>
                <span>{t('payment.bank2')}</span>
                <span>{t('payment.bank3')}</span>
                <span>{t('payment.bank4')}</span>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default PaymentPage;
