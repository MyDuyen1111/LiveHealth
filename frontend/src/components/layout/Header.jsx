import { useEffect, useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { ChevronDown, Leaf, Menu, PhoneCall, Search, ShoppingBag, User } from 'lucide-react';
import { useCart } from '../../context/CartContext';
import { useLang } from '../../context/LanguageContext';
import { useAuth } from '../../context/AuthContext';
import { categoryApi } from '../../api/categoryApi';
import { formatPrice } from '../../utils/format';
import './Header.css';

const Header = () => {
  const { cartCount, cartTotal, openCart } = useCart();
  const { lang, setLang, t } = useLang();
  const { isAuthenticated, user } = useAuth();
  const [catOpen, setCatOpen] = useState(false);
  const [categories, setCategories] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    categoryApi.getAll(1, 50)
      .then(data => setCategories(data?.items || []))
      .catch(() => setCategories([]));
  }, []);

  const handleSearch = (event) => {
    event.preventDefault();
    const keyword = searchTerm.trim();
    if (!keyword) {
      navigate('/shop?keyword=');
      return;
    }

    navigate(`/shop?keyword=${encodeURIComponent(keyword)}`);
  };

  return (
    <header className="fresh-header">
      <div className="h-topbar">
        <div className="container h-topbar-inner">
          <p className="h-topbar-note">{t('header.topbarNote')}</p>
          <div className="h-topbar-right">
            <div className="h-lang-switch">
              <button
                className={`h-lang-btn ${lang === 'vi' ? 'active' : ''}`}
                onClick={() => setLang('vi')}
              >VIE</button>
              <button
                className={`h-lang-btn ${lang === 'en' ? 'active' : ''}`}
                onClick={() => setLang('en')}
              >ENG</button>
            </div>
            <div className="h-topbar-sep" />
            {isAuthenticated ? (
              <Link to="/account" className="h-user-link">
                <User size={14} />
                <span>{user.firstName} {user.lastName}</span>
              </Link>
            ) : (
              <Link to="/login">{t('header.login')}</Link>
            )}
          </div>
        </div>
      </div>

      <div className="h-middle">
        <div className="container h-middle-inner">
          <Link to="/" className="h-logo">
            <span className="h-logo-mark"><Leaf size={23} /></span>
            <span className="h-logo-text">LiveHealth</span>
          </Link>

          <form className="h-search" onSubmit={handleSearch} noValidate>
            <Search size={18} />
            <input
              type="text"
              placeholder={t('header.search')}
              value={searchTerm}
              onChange={(event) => setSearchTerm(event.target.value)}
            />
            <button className="h-search-btn" type="submit">{t('header.searchBtn')}</button>
          </form>

          <div className="h-actions">
            <button className="h-action-cart" onClick={openCart} type="button">
              <div className="h-cart-icon">
                <ShoppingBag size={24}/>
                {cartCount > 0 && <span className="h-cart-badge">{cartCount}</span>}
              </div>
              <div className="h-cart-text">
                <span className="h-cart-label">{t('header.cart')}</span>
                <span className="h-cart-total">{formatPrice(cartTotal)}</span>
              </div>
            </button>
          </div>
        </div>
      </div>

      <nav className="h-navbar">
        <div className="container h-navbar-inner">
          <div className="h-navbar-left">
            <div className="h-cat-dropdown">
              <button className="h-navbar-cat-btn" onClick={() => setCatOpen(!catOpen)}>
                <Menu size={17} /> {t('header.categories')} <ChevronDown size={16} className={`h-cat-chevron ${catOpen ? 'open' : ''}`}/>
              </button>
              {catOpen && (
                <div className="h-cat-menu">
                  {categories.map((c) => (
                    <Link key={c.id} to={`/shop?category=${c.id}`} className="h-cat-menu-item" onClick={() => setCatOpen(false)}>
                      {c.name}
                    </Link>
                  ))}
                </div>
              )}
            </div>
            <ul className="h-nav-links">
              <li><NavLink to="/">{t('header.home')}</NavLink></li>
              <li><NavLink to="/shop">{t('header.shop')}</NavLink></li>
              <li><NavLink to="/blog">{t('header.blog')}</NavLink></li>
              <li><NavLink to="/health-ai">{t('header.healthAI')}</NavLink></li>
              <li><NavLink to="/about">{t('header.about')}</NavLink></li>
              <li><NavLink to="/contact">{t('header.contact')}</NavLink></li>
            </ul>
          </div>
          <div className="h-navbar-right">
            <PhoneCall size={18}/>
            <span>(028) 9876 5432</span>
          </div>
        </div>
      </nav>
    </header>
  );
};

export default Header;
