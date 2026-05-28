import { useState, useEffect } from 'react';
import { adminContactApi } from '../../api/adminApi';
import { useToast } from '../../components/admin/Toast';
import { useLang } from '../../context/LanguageContext';
import { Mail, Reply, Eye, CheckCircle, Clock } from 'lucide-react';
import './Admin.css';

const AdminContacts = () => {
  const [items, setItems] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [selectedMessage, setSelectedMessage] = useState(null);
  const [replyText, setReplyText] = useState('');
  const [replying, setReplying] = useState(false);
  const { addToast } = useToast();
  const { t } = useLang();

  const load = () => {
    setLoading(true);
    adminContactApi.getAll(page, 15)
      .then(d => {
        setItems(d?.items || []);
        setTotalPages(d?.meta?.totalPages || 1);
      })
      .catch((err) => {
        addToast(err.message || 'Lỗi tải danh sách tin nhắn', 'error');
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, [page]);

  const handleOpenMessage = async (id) => {
    try {
      const details = await adminContactApi.getById(id);
      setSelectedMessage(details);
      setReplyText(details.reply || '');
    } catch (err) {
      addToast(err.message || 'Lỗi tải chi tiết tin nhắn', 'error');
    }
  };

  const handleSendReply = async (e) => {
    e.preventDefault();
    if (!replyText.trim()) {
      addToast('Vui lòng nhập nội dung phản hồi', 'error');
      return;
    }

    setReplying(true);
    try {
      await adminContactApi.reply(selectedMessage.id, replyText);
      addToast(t('contact.replySuccess'), 'success');
      setSelectedMessage(null);
      setReplyText('');
      load();
    } catch (err) {
      addToast(err.message || 'Lỗi gửi phản hồi', 'error');
    } finally {
      setReplying(false);
    }
  };

  return (
    <div>
      <div className="adm-page-header">
        <h1 className="adm-page-title">{t('contact.messagesTitle')}</h1>
      </div>
      <div className="adm-card">
        {loading ? (
          <div className="adm-empty">{t('admin.loading')}</div>
        ) : items.length === 0 ? (
          <div className="adm-empty">{t('contact.noMessages')}</div>
        ) : (
          <table className="adm-table">
            <thead>
              <tr>
                <th>{t('contact.sender')}</th>
                <th>Email</th>
                <th>{t('contact.subjectCol')}</th>
                <th>{t('admin.date')}</th>
                <th>{t('admin.status')}</th>
                <th>{t('admin.actions')}</th>
              </tr>
            </thead>
            <tbody>
              {items.map(msg => (
                <tr key={msg.id}>
                  <td><strong>{msg.name}</strong></td>
                  <td>{msg.email}</td>
                  <td style={{ maxWidth: 200, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                    {msg.subject}
                  </td>
                  <td>{msg.createdAt ? new Date(msg.createdAt).toLocaleDateString('vi-VN') + ' ' + new Date(msg.createdAt).toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' }) : ''}</td>
                  <td>
                    {msg.status === 'REPLIED' ? (
                      <span className="adm-status adm-status-completed" style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
                        <CheckCircle size={12} />
                        {t('admin.orderStatus.COMPLETED')}
                      </span>
                    ) : (
                      <span className="adm-status adm-status-pending" style={{ display: 'inline-flex', alignItems: 'center', gap: 4 }}>
                        <Clock size={12} />
                        {t('admin.orderStatus.PENDING')}
                      </span>
                    )}
                  </td>
                  <td>
                    <button 
                      className="adm-btn adm-btn-outline adm-btn-sm" 
                      onClick={() => handleOpenMessage(msg.id)}
                      style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}
                    >
                      <Eye size={14} />
                      <span>{t('admin.viewDetails')}</span>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        {totalPages > 1 && (
          <div className="adm-pagination">
            {[...Array(totalPages)].map((_, i) => (
              <button 
                key={i} 
                className={`adm-pg-btn ${page === i + 1 ? 'active' : ''}`} 
                onClick={() => setPage(i + 1)}
              >
                {i + 1}
              </button>
            ))}
          </div>
        )}
      </div>

      {selectedMessage && (
        <div className="adm-modal-overlay" onClick={() => setSelectedMessage(null)}>
          <div className="adm-modal" onClick={e => e.stopPropagation()} style={{ maxWidth: 650 }}>
            <h3 className="adm-modal-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <Mail size={20} />
              <span>{t('contact.messageDetail')}</span>
            </h3>

            <div style={{ display: 'flex', flexDirection: 'column', gap: 12, marginBottom: 20 }}>
              <div style={{ padding: '12px 16px', background: '#f8f9fa', borderRadius: 8 }}>
                <p style={{ margin: '4px 0', fontSize: 13 }}><strong>{t('contact.sender')}:</strong> {selectedMessage.name} &lt;{selectedMessage.email}&gt;</p>
                <p style={{ margin: '4px 0', fontSize: 13 }}><strong>{t('admin.date')}:</strong> {selectedMessage.createdAt ? new Date(selectedMessage.createdAt).toLocaleString('vi-VN') : ''}</p>
                <p style={{ margin: '4px 0', fontSize: 13 }}><strong>{t('contact.subjectCol')}:</strong> {selectedMessage.subject}</p>
              </div>

              <div>
                <h4 style={{ fontSize: 14, fontWeight: 'bold', marginBottom: 6, color: '#333' }}>{t('contact.messageCol')}</h4>
                <div style={{ 
                  padding: '14px', 
                  background: '#fcfcfc', 
                  border: '1px solid #eaecf0', 
                  borderRadius: 8, 
                  fontSize: 14, 
                  lineHeight: 1.6,
                  color: '#475467',
                  whiteSpace: 'pre-wrap'
                }}>
                  {selectedMessage.message}
                </div>
              </div>

              {selectedMessage.status === 'REPLIED' ? (
                <div>
                  <h4 style={{ fontSize: 14, fontWeight: 'bold', marginBottom: 6, color: 'var(--green)' }}>{t('contact.replyTitle')}</h4>
                  <div style={{ 
                    padding: '14px', 
                    background: '#f4f9f4', 
                    border: '1px solid rgba(22, 163, 74, 0.2)', 
                    borderRadius: 8, 
                    fontSize: 14, 
                    lineHeight: 1.6,
                    color: '#2C742F',
                    whiteSpace: 'pre-wrap'
                  }}>
                    {selectedMessage.reply}
                  </div>
                  {selectedMessage.repliedAt && (
                    <span style={{ fontSize: 11, color: '#667085', marginTop: 4, display: 'block' }}>
                      Phản hồi lúc: {new Date(selectedMessage.repliedAt).toLocaleString('vi-VN')}
                    </span>
                  )}
                </div>
              ) : (
                <form onSubmit={handleSendReply}>
                  <h4 style={{ fontSize: 14, fontWeight: 'bold', marginBottom: 6, color: '#333' }}>{t('contact.replyTitle')}</h4>
                  <textarea 
                    value={replyText} 
                    onChange={e => setReplyText(e.target.value)} 
                    placeholder={t('contact.replyPlaceholder')}
                    rows={4} 
                    style={{ 
                      width: '100%', 
                      padding: 12, 
                      borderRadius: 8, 
                      border: '1px solid #d0d5dd',
                      fontSize: 14,
                      outline: 'none',
                      fontFamily: 'inherit',
                      resize: 'vertical'
                    }}
                    required
                  />
                  <div className="adm-modal-actions" style={{ marginTop: 15 }}>
                    <button 
                      type="button" 
                      className="adm-btn adm-btn-outline" 
                      onClick={() => setSelectedMessage(null)}
                      disabled={replying}
                    >
                      {t('admin.close')}
                    </button>
                    <button 
                      type="submit" 
                      className="adm-btn" 
                      disabled={replying}
                      style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}
                    >
                      <Reply size={16} />
                      <span>{replying ? t('contact.sending') : t('contact.replyBtn')}</span>
                    </button>
                  </div>
                </form>
              )}
            </div>

            {selectedMessage.status === 'REPLIED' && (
              <div className="adm-modal-actions" style={{ borderTop: '1px solid #eee', paddingTop: 15 }}>
                <button className="adm-btn" onClick={() => setSelectedMessage(null)}>{t('admin.close')}</button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminContacts;
