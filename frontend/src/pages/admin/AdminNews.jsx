import { useState, useEffect } from 'react';
import { Plus, Pencil, Trash2, Upload, X } from 'lucide-react';
import { adminNewsApi, adminNewsCategoryApi } from '../../api/adminApi';
import { useToast } from '../../components/admin/Toast';
import { useLang } from '../../context/LanguageContext';
import './Admin.css';

const empty = { title: '', content: '', newsCategoryId: '' };

const AdminNews = () => {
  const [items, setItems] = useState([]);
  const [cats, setCats] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(null);
  const [form, setForm] = useState(empty);
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState('');
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState('');
  const [uploading, setUploading] = useState(false);

  const { addToast } = useToast();
  const { t } = useLang();

  const load = () => {
    setLoading(true);
    adminNewsApi.getAll(page, 10).then(d => {
      setItems(d?.items || []);
      setTotalPages(d?.meta?.totalPages || 1);
    }).catch(() => {}).finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, [page]);
  useEffect(() => {
    adminNewsCategoryApi.getAll(1, 100).then(d => setCats(d?.items || [])).catch(() => {});
  }, []);

  const openCreate = () => {
    setForm({ ...empty });
    setSelectedFile(null);
    setPreviewUrl('');
    setModal('create');
    setMsg('');
  };

  const openEdit = (item) => {
    setForm({
      title: item.title || '',
      content: item.content || '',
      newsCategoryId: item.category?.id || ''
    });
    setSelectedFile(null);
    setPreviewUrl(item.thumbnailUrl || '');
    setModal(item);
    setMsg('');

  };

  const close = () => {
    setModal(null);
    setSelectedFile(null);
    setPreviewUrl('');
  };

  const handleFileSelect = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedFile(file);
      const reader = new FileReader();
      reader.onload = (ev) => setPreviewUrl(ev.target.result);
      reader.readAsDataURL(file);
    }
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      let newsId;
      if (modal === 'create') {
        const created = await adminNewsApi.create(form);
        newsId = created.id;
      } else {
        await adminNewsApi.update(modal.id, form);
        newsId = modal.id;
      }

      if (selectedFile && newsId) {
        setUploading(true);
        try {
          await adminNewsApi.uploadThumbnail(newsId, selectedFile);
        } catch (e) {
          addToast('Lưu bài viết thành công nhưng lỗi tải ảnh: ' + e.message, 'error');
        }
        setUploading(false);
      }

      addToast(modal === 'create' ? 'Đã thêm bài viết!' : 'Đã cập nhật bài viết!');
      close();
      load();
    } catch (e) {
      addToast(e.message, 'error');
    }
    setSaving(false);
  };

  const handleDelete = async (id) => {
    if (!confirm(t('admin.deleteConfirm'))) return;
    try {
      await adminNewsApi.delete(id);
      addToast('Đã xóa!');
      load();
    } catch(e) {
      addToast(e.message, 'error');
    }
  };

  return (
    <div>
      <div className="adm-page-header">
        <h1 className="adm-page-title">{t('admin.news')}</h1>
        <button className="adm-btn adm-btn-primary" onClick={openCreate}>
          <Plus size={16}/> {t('admin.add')} {t('admin.news')}
        </button>
      </div>
      <div className="adm-card">
        {loading ? <div className="adm-empty">{t('admin.loading')}</div> : items.length === 0 ? <div className="adm-empty">{t('admin.noData')}</div> : (
          <table className="adm-table">
            <thead>
              <tr>
                <th>{t('admin.thumbnail')}</th>
                <th>{t('admin.title')}</th>
                <th>{t('admin.category')}</th>
                <th>{t('admin.actions')}</th>
              </tr>
            </thead>
            <tbody>
              {items.map(item => (
              <tr key={item.id}>
                <td>{item.thumbnailUrl ? <img src={item.thumbnailUrl} className="adm-table-img" alt=""/> : '—'}</td>
                <td><strong>{(item.title || '').substring(0, 60)}</strong></td>
                <td>{item.category?.name || '—'}</td>
                <td>
                  <div className="adm-table-actions">
                    <button className="adm-btn adm-btn-outline adm-btn-sm" onClick={() => openEdit(item)}><Pencil size={14}/></button>
                    <button className="adm-btn adm-btn-danger adm-btn-sm" onClick={() => handleDelete(item.id)}><Trash2 size={14}/></button>
                  </div>
                </td>
              </tr>
            ))}
            </tbody>
          </table>
        )}
        {totalPages > 1 && (
          <div className="adm-pagination">
            {[...Array(totalPages)].map((_, i) => (
              <button key={i} className={`adm-pg-btn ${page === i + 1 ? 'active' : ''}`} onClick={() => setPage(i + 1)}>{i + 1}</button>
            ))}
          </div>
        )}
      </div>

      {modal && (
        <div className="adm-modal-overlay" onClick={close}>
          <div className="adm-modal" onClick={e => e.stopPropagation()} style={{ maxWidth: 640 }}>
            <h3 className="adm-modal-title">{modal === 'create' ? `${t('admin.add')} ${t('admin.news')}` : `${t('admin.edit')} ${t('admin.news')}`}</h3>
            {msg && <div className="adm-msg adm-msg-error">{msg}</div>}

            <div className="adm-form-group">
              <label>{t('admin.title')}</label>
              <input value={form.title} onChange={e => setForm({...form, title: e.target.value})}/>
            </div>

            <div className="adm-form-group">
              <label>{t('admin.category')}</label>
              <select value={form.newsCategoryId} onChange={e => setForm({...form, newsCategoryId: e.target.value})}>
                <option value="">{t('admin.select')}</option>
                {cats.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
              </select>
            </div>

            <div className="adm-form-group">
              <label>{t('admin.thumbnail')}</label>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                {previewUrl ? (
                  <div style={{ position: 'relative', width: 120, height: 80 }}>
                    <img src={previewUrl} alt="" style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: 6, border: '1px solid #eaecf0' }}/>
                    <button
                      type="button"
                      onClick={() => { setPreviewUrl(''); setSelectedFile(null); }}
                      style={{ position: 'absolute', top: -6, right: -6, width: 20, height: 20, borderRadius: '50%', background: '#ea4335', color: '#fff', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 0 }}
                    ><X size={12}/></button>
                  </div>
                ) : (
                  <label className="adm-file-input" style={{ width: 120, height: 80, display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer', margin: 0 }}>
                    <Upload size={20} color="#667085"/>
                    <input type="file" hidden accept="image/*" onChange={handleFileSelect}/>
                  </label>
                )}
              </div>
            </div>

            <div className="adm-form-group">
              <label>{t('admin.content')}</label>
              <textarea rows={8} value={form.content} onChange={e => setForm({...form, content: e.target.value})}/>
            </div>

            <div className="adm-modal-actions">
              <button className="adm-btn adm-btn-outline" onClick={close}>{t('admin.cancel')}</button>
              <button className="adm-btn adm-btn-primary" onClick={handleSave} disabled={saving || uploading}>
                {uploading ? t('admin.uploadingImages') : saving ? t('admin.saving') : t('admin.save')}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminNews;
