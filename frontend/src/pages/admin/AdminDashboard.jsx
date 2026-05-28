import { useState, useEffect } from 'react';
import { adminDashboardApi } from '../../api/adminApi';
import { formatPrice } from '../../utils/format';
import { useLang } from '../../context/LanguageContext';
import { DollarSign, ShoppingBag, Users, Package, ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';
import './Admin.css';

const AdminDashboard = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const { t } = useLang();

  useEffect(() => {
    adminDashboardApi.getStats()
      .then(data => {
        setStats(data);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div>
        <div className="adm-page-header">
          <h1 className="adm-page-title">{t('admin.dashboard')}</h1>
        </div>
        <div className="adm-card" style={{ padding: 40, textAlign: 'center' }}>
          <div className="adm-empty">{t('admin.loading')}</div>
        </div>
      </div>
    );
  }

  return (
    <div>
      <div className="adm-page-header">
        <h1 className="adm-page-title">{t('admin.dashboard')}</h1>
      </div>

      {/* Stats Grid */}
      <div className="adm-stats-grid">
        <div className="adm-stat-card">
          <div className="adm-stat-icon" style={{ background: '#e8f5e9', color: '#2e7d32' }}>
            <DollarSign size={24} />
          </div>
          <div className="adm-stat-info">
            <span className="adm-stat-label">{t('admin.revenue')}</span>
            <span className="adm-stat-value">{formatPrice(stats?.totalRevenue || 0)}</span>
          </div>
        </div>

        <div className="adm-stat-card">
          <div className="adm-stat-icon" style={{ background: '#e3f2fd', color: '#1565c0' }}>
            <ShoppingBag size={24} />
          </div>
          <div className="adm-stat-info">
            <span className="adm-stat-label">{t('admin.ordersCount')}</span>
            <span className="adm-stat-value">{stats?.totalOrders || 0}</span>
          </div>
        </div>

        <div className="adm-stat-card">
          <div className="adm-stat-icon" style={{ background: '#eceff1', color: '#37474f' }}>
            <Users size={24} />
          </div>
          <div className="adm-stat-info">
            <span className="adm-stat-label">{t('admin.usersCount')}</span>
            <span className="adm-stat-value">{stats?.totalUsers || 0}</span>
          </div>
        </div>

        <div className="adm-stat-card">
          <div className="adm-stat-icon" style={{ background: '#fff3e0', color: '#e65100' }}>
            <Package size={24} />
          </div>
          <div className="adm-stat-info">
            <span className="adm-stat-label">{t('admin.productsCount')}</span>
            <span className="adm-stat-value">{stats?.totalProducts || 0}</span>
          </div>
        </div>
      </div>

      {/* Recent Orders Section */}
      <div className="adm-dashboard-layout">
        <div className="adm-card" style={{ padding: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
            <h2 className="adm-dashboard-section-title" style={{ margin: 0 }}>{t('admin.recentOrders')}</h2>
            <Link to="/admin/orders" style={{ display: 'flex', alignItems: 'center', gap: 4, color: '#00B207', fontSize: 13, fontWeight: 600, textDecoration: 'none' }}>
              {t('admin.viewAll')} <ArrowRight size={14} />
            </Link>
          </div>

          {!stats?.recentOrders || stats.recentOrders.length === 0 ? (
            <div className="adm-empty">{t('admin.noData')}</div>
          ) : (
            <table className="adm-table">
              <thead>
                <tr>
                  <th>{t('admin.orderNumber')}</th>
                  <th>{t('admin.date')}</th>
                  <th>{t('admin.customer')}</th>
                  <th>{t('admin.total')}</th>
                  <th>{t('admin.status')}</th>
                </tr>
              </thead>
              <tbody>
                {stats.recentOrders.map(o => (
                  <tr key={o.id}>
                    <td><strong>#{o.orderNumber || o.id?.substring(0, 6)}</strong></td>
                    <td>{o.createdAt ? new Date(o.createdAt).toLocaleDateString('vi-VN') : ''}</td>
                    <td>{o.userEmail || '—'}</td>
                    <td>{formatPrice(o.totalAmount || 0)}</td>
                    <td>
                      <span className={`adm-status adm-status-${(o.status || '').toLowerCase()}`}>
                        {t(`admin.orderStatus.${o.status}`)}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
