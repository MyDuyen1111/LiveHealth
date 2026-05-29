import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { LayoutDashboard, ShoppingBag, ShoppingCart, Settings, LogOut, Shield, MessageSquare } from 'lucide-react';
import { useLang } from '../context/LanguageContext';
import { useAuth } from '../context/AuthContext';
import { get } from '../api/apiClient';
import './AccountSidebar.css';

const navItems = [
  { key: 'dashboard', icon: LayoutDashboard, path: '/account', labelKey: 'account.dashboard' },
  { key: 'orders', icon: ShoppingBag, path: '/account/orders', labelKey: 'account.orderHistory' },
  { key: 'cart', icon: ShoppingCart, path: '/cart', labelKey: 'account.shoppingCart' },
  { key: 'contacts', icon: MessageSquare, path: '/account/contacts', labelKey: 'account.contactMessages' },
  { key: 'settings', icon: Settings, path: '/account/settings', labelKey: 'account.settings' },
];

const AccountSidebar = ({ activeItem }) => {
  const { t } = useLang();
  const { logout, user } = useAuth();
  const [unreadCount, setUnreadCount] = useState(0);

  useEffect(() => {
    get('/contact/my-unread-count')
      .then(res => setUnreadCount(res || 0))
      .catch(() => {});
  }, [activeItem]);

  const handleLogout = (e) => {
    e.preventDefault();
    logout();
    window.location.href = '/login';
  };

  return (
    <div className="acc-sidebar-wrap">
    <aside className="acc-sidebar">
      <h3 className="acc-sidebar-title">{t('account.navigation')}</h3>
      <ul className="acc-sidebar-nav">
        {navItems.map(item => {
          const Icon = item.icon;
          const isActive = activeItem === item.key;
          return (
            <li key={item.key}>
              <Link
                to={item.path}
                className={`acc-sidebar-link ${isActive ? 'active' : ''}`}
              >
                <Icon size={20} />
                <span>{t(item.labelKey)}</span>
                {item.key === 'contacts' && unreadCount > 0 && (
                  <span className="acc-sidebar-badge" style={{
                    background: '#ef4444',
                    color: '#ffffff',
                    fontSize: '11px',
                    fontWeight: '700',
                    padding: '2px 7px',
                    borderRadius: '10px',
                    marginLeft: 'auto',
                    display: 'inline-block',
                    lineHeight: '1.2'
                  }}>
                    {unreadCount}
                  </span>
                )}
              </Link>
            </li>
          );
        })}
        <li>
          <a href="#" className="acc-sidebar-link" onClick={handleLogout}>
            <LogOut size={20} />
            <span>{t('account.logout')}</span>
          </a>
        </li>
      </ul>
    </aside>
    {user?.role === 'ADMIN' && (
      <Link to="/admin" className="acc-admin-btn">
        <Shield size={18} />
        {t('admin.adminPanel')}
      </Link>
    )}
    </div>
  );
};

export default AccountSidebar;
