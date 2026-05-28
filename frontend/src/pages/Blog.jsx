import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Search, Tag, User, MessageCircle } from 'lucide-react';
import { useLang } from '../context/LanguageContext';
import { newsApi, newsCategoryApi, newsTagApi } from '../api/newsApi';
import './Blog.css';

const Blog = () => {
  const { t } = useLang();
  const [page, setPage] = useState(1);
  const [posts, setPosts] = useState([]);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [categories, setCategories] = useState([]);
  const [tags, setTags] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const perPage = 6;

  useEffect(() => {
    setLoading(true);
    newsApi.getAll(page, perPage, selectedCategory).then(data => {
      setPosts(data?.items || []);
      setTotalPages(data?.meta?.totalPages || 1);
      setTotalElements(data?.meta?.totalElement || 0);
    }).catch(() => setPosts([])).finally(() => setLoading(false));
  }, [page, selectedCategory]);

  useEffect(() => {
    newsCategoryApi.getAll().then(data => {
      setCategories(data?.items || []);
    }).catch(() => {});

    newsTagApi.getAll().then(data => {
      setTags((data?.items || []).map(t => t.name));
    }).catch(() => {});
  }, []);

  return (
    <div className="blog-wrap">
      {/* Breadcrumb */}
      <div className="blog-breadcrumb">
        <div className="container blog-bc-inner">
          <Link to="/">🏠</Link>
          <span>›</span>
          <span className="bc-active">{t('blog.breadcrumb')}</span>
        </div>
      </div>

      <div className="container blog-layout">
        {/* ── SIDEBAR ── */}
        <aside className="blog-sidebar">
          {/* Search */}
          <div className="blog-search-box">
            <input type="text" placeholder={t('blog.search')} />
            <Search size={18} className="blog-search-icon" />
          </div>

          {/* Top Categories */}
          <div className="blog-sb-widget">
            <h3 className="blog-sb-title">{t('blog.topCategories')}</h3>
            <ul className="blog-cat-list">
              {categories.map((cat) => (
                <li
                  key={cat.id}
                  onClick={() => { setSelectedCategory(cat.id === selectedCategory ? null : cat.id); setPage(1); }}
                  style={{ cursor: 'pointer', fontWeight: cat.id === selectedCategory ? 'bold' : 'normal', color: cat.id === selectedCategory ? 'var(--primary-color)' : 'inherit' }}
                >
                  <span>{cat.name}</span>
                  <span className="blog-cat-count">{cat.totalNews || 0}</span>
                </li>
              ))}
            </ul>
          </div>

          {/* Popular Tags */}
          <div className="blog-sb-widget">
            <h3 className="blog-sb-title">{t('blog.popularTag')}</h3>
            <div className="blog-tag-list">
              {tags.map((tag, i) => (
                <span key={i} className={`blog-tag ${i === 1 ? 'active' : ''}`}>{tag}</span>
              ))}
            </div>
          </div>

          {/* Recently Added */}
          <div className="blog-sb-widget">
            <h3 className="blog-sb-title">{t('blog.recentlyAdded')}</h3>
            {posts.slice(0, 3).map(post => (
              <Link to={`/blog/${post.id}`} key={post.id} className="blog-recent-item">
                <img src={post.thumbnailUrl || 'https://placehold.co/600x400'} alt={post.title} />
                <div>
                  <span className="blog-recent-title">{(post.title || '').substring(0, 50)}...</span>
                  <span className="blog-recent-date">📅 {post.createdAt ? new Date(post.createdAt).toLocaleDateString('vi-VN') : ''}</span>
                </div>
              </Link>
            ))}
          </div>
        </aside>

        {/* ── MAIN ── */}
        <div className="blog-main">
          {/* Top bar */}
          <div className="blog-topbar">
            <div className="blog-sort">
              <span>Sort by:</span>
              <select>
                <option>Latest</option>
                <option>Oldest</option>
                <option>Popular</option>
              </select>
            </div>
            <span className="blog-result-count">
              <strong>{totalElements}</strong> {t('blog.resultsFound')}
            </span>
          </div>

          {/* Blog Grid */}
          {loading ? (
            <div style={{ textAlign: 'center', padding: '40px' }}>Đang tải...</div>
          ) : (
            <div className="blog-grid">
              {posts.map(post => (
                <Link to={`/blog/${post.id}`} key={post.id} className="blog-card">
                  <div className="blog-card-img">
                    <img src={post.thumbnailUrl || 'https://placehold.co/600x400'} alt={post.title} />
                    <div className="blog-card-date">
                      <span className="day">{post.createdAt ? new Date(post.createdAt).getDate() : ''}</span>
                      <span className="month">{post.createdAt ? new Date(post.createdAt).toLocaleDateString('en', { month: 'short' }) : ''}</span>
                    </div>
                  </div>
                  <div className="blog-card-body">
                    <div className="blog-card-meta">
                      <span><Tag size={14} /> {post.category?.name || t('blog.food')}</span>
                      <span><User size={14} /> {t('blog.byAdmin')}</span>
                      <span><MessageCircle size={14} /> {post.commentsCount || 0} {t('blog.comments')}</span>
                    </div>
                    <h3>{post.title}</h3>
                    <span className="blog-read-more">{t('blog.readMore')}</span>
                  </div>
                </Link>
              ))}
            </div>
          )}

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="blog-pagination">
              {[...Array(totalPages)].map((_, i) => (
                <button
                  key={i}
                  className={`blog-pg-btn ${page === i + 1 ? 'blog-pg-active' : ''}`}
                  onClick={() => setPage(i + 1)}
                >{i + 1}</button>
              ))}
              <button
                className="blog-pg-btn blog-pg-next"
                onClick={() => setPage(Math.min(page + 1, totalPages))}
              >›</button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Blog;
