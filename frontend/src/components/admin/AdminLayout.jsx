import { NavLink, Outlet, Link } from 'react-router-dom';
import { Package, FolderTree, Tag, Bookmark, Newspaper, MessageSquareQuote, Users, ShoppingCart, Truck, CreditCard, LayoutDashboard, Globe, LogOut } from 'lucide-react';
import { useLang } from '../../context/LanguageContext';
import { useAuth } from '../../context/AuthContext';
import { ToastProvider } from './Toast';
import './AdminLayout.css';

const AdminLayout = () => {
  const { lang, setLang, t } = useLang();
  const { user, logout } = useAuth();

  const nav = [
    { to: '/admin', icon: LayoutDashboard, label: t('admin.dashboard'), end: true },
    { to: '/admin/products', icon: Package, label: t('admin.products') },
    { to: '/admin/categories', icon: FolderTree, label: t('admin.categories') },
    { to: '/admin/brands', icon: Bookmark, label: t('admin.brands') },
    { to: '/admin/tags', icon: Tag, label: t('admin.tags') },
    { to: '/admin/news', icon: Newspaper, label: t('admin.news') },
    { to: '/admin/news-categories', icon: FolderTree, label: t('admin.newsCategories') },
    { to: '/admin/news-tags', icon: Tag, label: t('admin.newsTags') },
    { to: '/admin/testimonials', icon: MessageSquareQuote, label: t('admin.testimonials') },
    { to: '/admin/members', icon: Users, label: t('admin.members') },
    { to: '/admin/orders', icon: ShoppingCart, label: t('admin.orders') },
    { to: '/admin/shipping', icon: Truck, label: t('admin.shipping') },
    { to: '/admin/payment', icon: CreditCard, label: t('admin.payment') },
  ];

  return (
    <ToastProvider>
    <div className="adm-layout">
      <aside className="adm-sidebar">
        <div className="adm-sidebar-header">
          <h2>🛡️ {t('admin.panel')}</h2>
          <div className="adm-sidebar-user">{user?.firstName} {user?.lastName}</div>
        </div>
        <nav className="adm-nav">
          {nav.map(item => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) => `adm-nav-item ${isActive ? 'active' : ''}`}
            >
              <item.icon size={18} />
              <span>{item.label}</span>
            </NavLink>
          ))}
        </nav>
        <div className="adm-sidebar-footer">
          <div className="adm-lang-toggle">
            <Globe size={16} />
            <button className={`adm-lang-btn ${lang === 'vi' ? 'active' : ''}`} onClick={() => setLang('vi')}>VI</button>
            <button className={`adm-lang-btn ${lang === 'en' ? 'active' : ''}`} onClick={() => setLang('en')}>EN</button>
          </div>
          <Link to="/" className="adm-nav-item" style={{ marginTop: 4 }}>
            <LayoutDashboard size={18} />
            <span>{t('admin.backToSite')}</span>
          </Link>
          <button className="adm-nav-item adm-logout-btn" onClick={logout}>
            <LogOut size={18} />
            <span>{t('admin.logout')}</span>
          </button>
        </div>
      </aside>
      <main className="adm-main">
        <Outlet />
      </main>
    </div>
    </ToastProvider>
  );
};

export default AdminLayout;
