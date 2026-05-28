import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { Search, Tag, User, MessageCircle, ArrowRight } from 'lucide-react';
import { useLang } from '../context/LanguageContext';
import { useAuth } from '../context/AuthContext';
import { newsApi, newsCategoryApi, newsTagApi } from '../api/newsApi';
import './BlogDetail.css';

const BlogDetail = () => {
  const { t } = useLang();
  const { id } = useParams();
  const { isAuthenticated } = useAuth();
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [categories, setCategories] = useState([]);
  const [tags, setTags] = useState([]);
  const [recentPosts, setRecentPosts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [commentContent, setCommentContent] = useState('');

  useEffect(() => {
    setLoading(true);
    newsApi.getById(id).then(data => setPost(data)).catch(() => setPost(null)).finally(() => setLoading(false));
    newsApi.getComments(id, 1, 20).then(data => setComments(data?.items || [])).catch(() => {});
    newsApi.getAll(1, 5).then(data => setRecentPosts(data?.items || [])).catch(() => {});
    newsCategoryApi.getAll().then(data => setCategories(data?.items || [])).catch(() => {});
    newsTagApi.getAll().then(data => setTags((data?.items || []).map(t => t.name))).catch(() => {});
  }, [id]);

  const handlePostComment = async (e) => {
    e.preventDefault();
    if (!commentContent.trim()) return;
    try {
      await newsApi.createComment(id, commentContent);
      setCommentContent('');
      const data = await newsApi.getComments(id, 1, 20);
      setComments(data?.items || []);
    } catch {}
  };

  if (loading) return <div className="container" style={{ padding: '80px 0', textAlign: 'center' }}>Đang tải...</div>;
  if (!post) return <div className="container" style={{ padding: '80px 0', textAlign: 'center' }}>Không tìm thấy bài viết.</div>;

  return (
    <div className="bd-wrap">
      {/* Breadcrumb */}
      <div className="bd-breadcrumb">
        <div className="container bd-bc-inner">
          <Link to="/">🏠</Link>
          <span>›</span>
          <Link to="/blog">{t('blog.breadcrumb')}</Link>
          <span>›</span>
          <span className="bc-active">{post.title?.substring(0, 30)}...</span>
        </div>
      </div>

      <div className="container bd-layout">
        {/* ── MAIN ARTICLE ── */}
        <article className="bd-article">
          <img className="bd-featured-img" src={post.thumbnailUrl || 'https://placehold.co/600x400'} alt={post.title} />

          {/* Meta */}
          <div className="bd-meta">
            <span><Tag size={14} /> {post.category?.name || t('blog.food')}</span>
            <span><User size={14} /> {t('blog.byAdmin')}</span>
            <span><MessageCircle size={14} /> {comments.length} {t('blog.comments')}</span>
          </div>

          <h1>{post.title}</h1>

          {/* Author */}
          <div className="bd-author">
            <img className="bd-author-avatar" src={`https://ui-avatars.com/api/?name=Admin&size=40&rounded=true`} alt="Author" />
            <div>
              <div className="bd-author-name">{post.author || 'Admin'}</div>
              <div className="bd-author-date">{post.createdAt ? new Date(post.createdAt).toLocaleDateString('vi-VN') : ''}</div>
            </div>
          </div>

          {/* Content */}
          <div className="bd-content" dangerouslySetInnerHTML={{ __html: post.content || '' }} />

          {/* ── Leave a Comment ── */}
          <div className="bd-comment-form">
            <h3>{t('blogDetail.leaveComment')}</h3>
            <form onSubmit={handlePostComment}>
              <div className="bd-form-row full">
                <textarea
                  placeholder={t('blogDetail.messagePlaceholder')}
                  rows="5"
                  value={commentContent}
                  onChange={e => setCommentContent(e.target.value)}
                ></textarea>
              </div>
              <button type="submit" className="bd-post-comment-btn" disabled={!isAuthenticated}>
                {isAuthenticated ? t('blogDetail.postComment') : 'Đăng nhập để bình luận'}
              </button>
            </form>
          </div>

          {/* ── Comments ── */}
          <div className="bd-comments-section">
            <h3>{t('blogDetail.commentsTitle')}</h3>
            {comments.map(comment => (
              <div key={comment.id} className="bd-comment-item">
                <img className="bd-comment-avatar" src={`https://ui-avatars.com/api/?name=${encodeURIComponent(comment.userFullName || 'U')}&size=40&rounded=true`} alt={comment.userFullName} />
                <div>
                  <div className="bd-comment-header">
                    <span className="bd-comment-name">{comment.userFullName}</span>
                    <span className="bd-comment-date">{comment.createdAt ? new Date(comment.createdAt).toLocaleDateString('vi-VN') : ''}</span>
                  </div>
                  <p className="bd-comment-text">{comment.content}</p>
                </div>
              </div>
            ))}
          </div>
        </article>

        {/* ── SIDEBAR ── */}
        <aside className="bd-sidebar">
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
                <li key={cat.id}>
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
            {recentPosts.slice(0, 3).map(p => (
              <Link to={`/blog/${p.id}`} key={p.id} className="blog-recent-item">
                <img src={p.thumbnailUrl || 'https://placehold.co/600x400'} alt={p.title} />
                <div>
                  <span className="blog-recent-title">{(p.title || '').substring(0, 50)}...</span>
                  <span className="blog-recent-date">📅 {p.createdAt ? new Date(p.createdAt).toLocaleDateString('vi-VN') : ''}</span>
                </div>
              </Link>
            ))}
          </div>
        </aside>
      </div>
    </div>
  );
};

export default BlogDetail;
