import { useEffect, useState, useRef } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useLang } from '../context/LanguageContext';
import { paymentApi } from '../api/paymentApi';
import './PaymentReturn.css';

const PaymentReturn = () => {
  const { t } = useLang();
  const location = useLocation();
  const [status, setStatus] = useState('loading'); // 'loading' | 'success' | 'failed' | 'direct'
  const [txnInfo, setTxnInfo] = useState(null);
  const [pollingStatus, setPollingStatus] = useState(null);
  const pollingRef = useRef(null);

  const params = new URLSearchParams(location.search);
  const vnpResponseCode = params.get('vnp_ResponseCode');
  const vnpTransactionStatus = params.get('vnp_TransactionStatus');
  const txnRef = params.get('vnp_TxnRef');
  const vnpAmount = params.get('vnp_Amount');
  const bankCode = params.get('vnp_BankCode');
  const orderInfo = params.get('vnp_OrderInfo');

  const amountDisplay = vnpAmount
    ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(
        parseInt(vnpAmount, 10) / 100
      )
    : '';

  const isSuccess = vnpResponseCode === '00' && vnpTransactionStatus === '00';

  useEffect(() => {
    if (!vnpResponseCode) {
      setStatus('direct');
      return;
    }
    setTxnInfo({ txnRef, amountDisplay, bankCode, orderInfo });
    if (isSuccess) {
      setStatus('success');
      if (txnRef) startPolling(txnRef);
    } else {
      setStatus('failed');
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const startPolling = (ref) => {
    let attempts = 0;
    pollingRef.current = setInterval(async () => {
      attempts++;
      try {
        const data = await paymentApi.getPaymentStatus(ref);
        setPollingStatus(data);
        if (data === 'SUCCESS' || data === 'FAILED' || attempts >= 10) {
          clearInterval(pollingRef.current);
        }
      } catch {
        if (attempts >= 10) clearInterval(pollingRef.current);
      }
    }, 2000);
  };

  useEffect(() => () => clearInterval(pollingRef.current), []);

  // Breadcrumb shared
  const Breadcrumb = () => (
    <div className="co-breadcrumb">
      <div className="container co-bc-inner">
        <Link to="/">🏠</Link><span>›</span>
        <Link to="/payment">{t('payment.breadcrumb')}</Link><span>›</span>
        <span className="bc-active">{t('payment.returnBreadcrumb')}</span>
      </div>
    </div>
  );

  // ── Direct access ──
  if (status === 'direct') {
    return (
      <div className="pr-page">
        <Breadcrumb />
        <div className="container pr-center">
          <div className="pr-card pr-card--info">
            <div className="pr-icon">ℹ️</div>
            <h1>{t('payment.directTitle')}</h1>
            <p>{t('payment.directDesc')}</p>
            <Link to="/payment" className="pr-btn">{t('payment.directBtn')}</Link>
          </div>
        </div>
      </div>
    );
  }

  // ── Success ──
  if (status === 'success') {
    return (
      <div className="pr-page">
        <Breadcrumb />
        <div className="container pr-center">
          <div className="pr-card pr-card--success">
            <div className="pr-checkmark">
              <svg viewBox="0 0 52 52">
                <circle cx="26" cy="26" r="25" fill="none"/>
                <path fill="none" d="M14 27l8 8 16-16"/>
              </svg>
            </div>
            <h1>{t('payment.successTitle')}</h1>
            <p className="pr-subtitle">{t('payment.successSubtitle')}</p>

            <div className="pr-info-grid">
              {txnInfo?.txnRef && (
                <div className="pr-info-row">
                  <span>{t('payment.txnRef')}</span>
                  <strong>{txnInfo.txnRef}</strong>
                </div>
              )}
              {txnInfo?.amountDisplay && (
                <div className="pr-info-row">
                  <span>{t('payment.amount')}</span>
                  <strong className="pr-amount">{txnInfo.amountDisplay}</strong>
                </div>
              )}
              {txnInfo?.bankCode && (
                <div className="pr-info-row">
                  <span>{t('payment.bank')}</span>
                  <strong>{txnInfo.bankCode}</strong>
                </div>
              )}
              {txnInfo?.orderInfo && (
                <div className="pr-info-row">
                  <span>{t('payment.orderInfo')}</span>
                  <strong>{decodeURIComponent(txnInfo.orderInfo || '')}</strong>
                </div>
              )}
            </div>

            {/* Polling status badge */}
            {pollingStatus && (
              <div className={`pr-polling-badge pr-polling-badge--${pollingStatus?.toLowerCase()}`}>
                {pollingStatus === 'SUCCESS' && t('payment.pollingSuccess')}
                {pollingStatus === 'PENDING' && t('payment.pollingPending')}
                {pollingStatus === 'FAILED' && t('payment.pollingFailed')}
                {!['SUCCESS','PENDING','FAILED'].includes(pollingStatus) && `${pollingStatus}`}
              </div>
            )}
            {!pollingStatus && txnInfo?.txnRef && (
              <div className="pr-polling-badge pr-polling-badge--pending">
                {t('payment.pollingConfirming')}
              </div>
            )}

            <div className="pr-actions">
              <Link to="/" className="pr-btn pr-btn--outline">{t('payment.backHome')}</Link>
              <Link to="/account/orders" className="pr-btn">{t('payment.viewOrders') || 'Xem đơn hàng'}</Link>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // ── Failed ──
  return (
    <div className="pr-page">
      <Breadcrumb />
      <div className="container pr-center">
        <div className="pr-card pr-card--failed">
          <div className="pr-icon-fail">✕</div>
          <h1>{t('payment.failedTitle')}</h1>
          <p className="pr-subtitle">
            {t('payment.failedSubtitle')}{' '}
            <strong>{vnpResponseCode}</strong>
          </p>
          {txnInfo?.txnRef && (
            <div className="pr-info-grid">
              <div className="pr-info-row">
                <span>{t('payment.txnRef')}</span>
                <strong>{txnInfo.txnRef}</strong>
              </div>
              {txnInfo?.amountDisplay && (
                <div className="pr-info-row">
                  <span>{t('payment.amount')}</span>
                  <strong>{txnInfo.amountDisplay}</strong>
                </div>
              )}
            </div>
          )}
          <div className="pr-actions">
            <Link to="/" className="pr-btn pr-btn--outline">{t('payment.backHome')}</Link>
            <Link to="/payment" className="pr-btn">{t('payment.retry')}</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default PaymentReturn;
