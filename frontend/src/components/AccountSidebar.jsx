import { Link } from 'react-router-dom';
import { LayoutDashboard, ShoppingBag, ShoppingCart, Settings, LogOut, Shield } from 'lucide-react';
import { useLang } from '../context/LanguageContext';
import { useAuth } from '../context/AuthContext';
import './AccountSidebar.css';

const navItems = [
  { key: 'dashboard', icon: LayoutDashboard, path: '/account', labelKey: 'account.dashboard' },
  { key: 'orders', icon: ShoppingBag, path: '/account/orders', labelKey: 'account.orderHistory' },
  { key: 'cart', icon: ShoppingCart, path: '/cart', labelKey: 'account.shoppingCart' },
  { key: 'settings', icon: Settings, path: '/account/settings', labelKey: 'account.settings' },
];

const AccountSidebar = ({ activeItem }) => {
  const { t } = useLang();
  const { logout, user } = useAuth();

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
