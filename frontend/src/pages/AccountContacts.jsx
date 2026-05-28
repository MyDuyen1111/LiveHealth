import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useLang } from '../context/LanguageContext';
import { get } from '../api/apiClient';
import AccountSidebar from '../components/AccountSidebar';
import { Clock, MessageSquare, Send } from 'lucide-react';
import './AccountContacts.css';

const AccountContacts = () => {
  const { t } = useLang();
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    get('/contact/my-messages')
      .then((res) => {
        setMessages(res || []);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleString('vi-VN', {
      hour: '2-digit',
      minute: '2-digit',
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  };

  return (
    <div className="usr-contacts-wrap">
      {/* Breadcrumb */}
      <div className="usr-contacts-bc">
        <div className="container usr-contacts-bc-inner">
          <Link to="/">🏠</Link>
          <span>›</span>
          <span>{t('account.breadcrumb')}</span>
          <span>›</span>
          <span className="bc-active">{t('contactMessages.breadcrumb')}</span>
        </div>
      </div>

      <div className="container usr-contacts-layout">
        <AccountSidebar activeItem="contacts" />

        <div className="usr-contacts-main">
          <div className="usr-contacts-header">
            <h3>{t('contactMessages.title')}</h3>
          </div>

          <div className="usr-contacts-content">
            {loading ? (
              <div className="usr-contacts-loading">{t('admin.loading')}</div>
            ) : messages.length === 0 ? (
              <div className="usr-contacts-empty-card">
                <MessageSquare size={48} className="usr-contacts-empty-icon" />
                <p>{t('contactMessages.noMessages')}</p>
                <Link to="/contact" className="usr-contacts-btn-contact">
                  {t('header.contact')}
                </Link>
              </div>
            ) : (
              <div className="usr-contacts-list">
                {messages.map((msg) => (
                  <div key={msg.id} className="usr-msg-card">
                    <div className="usr-msg-card-header">
                      <div className="usr-msg-subject-row">
                        <h4 className="usr-msg-subject">{msg.subject}</h4>
                        <span className={`usr-msg-badge usr-msg-badge--${(msg.status || '').toLowerCase()}`}>
                          {msg.status === 'REPLIED' ? t('contactMessages.replied') : t('contactMessages.pending')}
                        </span>
                      </div>
                      <div className="usr-msg-meta">
                        <Clock size={14} />
                        <span>
                          {t('contactMessages.sentAt')} {formatDate(msg.createdAt)}
                        </span>
                      </div>
                    </div>

                    <div className="usr-msg-card-body">
                      <p className="usr-msg-text">{msg.message}</p>
                    </div>

                    {msg.status === 'REPLIED' && msg.reply && (
                      <div className="usr-msg-reply-box">
                        <div className="usr-msg-reply-header">
                          <Send size={14} className="usr-reply-icon" />
                          <strong>{t('contactMessages.replyFromAdmin')}</strong>
                          {msg.repliedAt && (
                            <span className="usr-reply-time">
                              ({formatDate(msg.repliedAt)})
                            </span>
                          )}
                        </div>
                        <p className="usr-msg-reply-text">{msg.reply}</p>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default AccountContacts;
