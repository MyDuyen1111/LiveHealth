import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useLang } from '../context/LanguageContext';
import { useAuth } from '../context/AuthContext';
import { orderApi } from '../api/orderApi';
import { formatPrice } from '../utils/format';
import AccountSidebar from '../components/AccountSidebar';
import './AccountDashboard.css';

const AccountDashboard = () => {
  const { t } = useLang();
  const { user } = useAuth();
  const [recentOrders, setRecentOrders] = useState([]);

  useEffect(() => {
    orderApi.getMyOrders(1, 6).then(data => {
      setRecentOrders(data?.items || []);
    }).catch(() => {});
  }, []);

  const getStatusLabel = (status) => {
    switch (status) {
      case 'PROCESSING': return t('dashboard.processing');
      case 'ON_THE_WAY': return t('dashboard.onTheWay');
      case 'COMPLETED': case 'DELIVERED': return t('dashboard.completed');
      default: return status;
    }
  };

  return (
    <div className="dash-wrap">
      {/* Breadcrumb */}
      <div className="dash-breadcrumb">
        <div className="container dash-bc-inner">
          <Link to="/">🏠</Link>
          <span>›</span>
          <span>{t('account.breadcrumb')}</span>
          <span>›</span>
          <span className="bc-active">{t('dashboard.breadcrumb')}</span>
        </div>
      </div>

      <div className="container dash-layout">
        <AccountSidebar activeItem="dashboard" />

        <div className="dash-main">
          {/* Profile + Billing row */}
          <div className="dash-top-row">
            {/* Profile card */}
            <div className="dash-profile-card">
              <div className="dash-avatar">
                <img
                  src={user?.linkAvatar || user?.avatarUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent((user?.firstName || 'U') + '+' + (user?.lastName || ''))}&background=e8f5e9&color=2C742F&size=120&font-size=0.4&rounded=true`}
                  alt="Avatar"
                />
              </div>
              <h3 className="dash-user-name">{user?.firstName || ''} {user?.lastName || ''}</h3>
              <p className="dash-user-role">{user?.role === 'ADMIN' ? 'Admin' : 'Customer'}</p>
              <Link to="/account/settings" className="dash-edit-link">{t('dashboard.editProfile')}</Link>
            </div>

            {/* Billing card */}
            <div className="dash-billing-card">
              <span className="dash-billing-label">{t('dashboard.billingAddress')}</span>
              <h3 className="dash-billing-name">{user?.billingAddress?.firstName || user?.firstName || ''} {user?.billingAddress?.lastName || user?.lastName || ''}</h3>
              <p className="dash-billing-addr">
                {user?.billingAddress?.street || ''} {user?.billingAddress?.city || ''}
              </p>
              <p className="dash-billing-info">{user?.email || ''}</p>
              <p className="dash-billing-info">{user?.phone || ''}</p>
              <Link to="/account/settings" className="dash-edit-link green">{t('dashboard.editAddress')}</Link>
            </div>
          </div>

          {/* Recent orders */}
          <div className="dash-orders-section">
            <div className="dash-orders-header">
              <h3>{t('dashboard.recentOrders')}</h3>
              <Link to="/account/orders" className="dash-view-all">{t('dashboard.viewAll')}</Link>
            </div>

            <div className="dash-orders-table-wrap">
              <table className="dash-orders-table">
                <thead>
                  <tr>
                    <th>{t('dashboard.orderId')}</th>
                    <th>{t('dashboard.date')}</th>
                    <th>{t('dashboard.total')}</th>
                    <th>{t('dashboard.status')}</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  {recentOrders.map((order) => (
                    <tr key={order.id}>
                      <td>#{order.orderNumber || order.id?.substring(0, 6)}</td>
                      <td>{order.createdAt ? new Date(order.createdAt).toLocaleDateString('vi-VN') : ''}</td>
                      <td>{formatPrice(order.totalAmount)}</td>
                      <td>
                        <span className={`dash-status dash-status--${(order.status || '').toLowerCase()}`}>
                          {getStatusLabel(order.status)}
                        </span>
                      </td>
                      <td>
                        <Link to={`/account/orders/${order.id}`} className="dash-view-detail">
                          {t('dashboard.viewDetails')}
                        </Link>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AccountDashboard;
