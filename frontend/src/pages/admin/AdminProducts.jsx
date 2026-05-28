import { useState, useEffect } from 'react';
import { useLang } from '../../context/LanguageContext';
import { Plus, Pencil, Trash2, Upload, X } from 'lucide-react';
import { adminProductApi, adminCategoryApi, adminBrandApi, adminTagApi } from '../../api/adminApi';
import { useToast } from '../../components/admin/Toast';
import { formatPrice } from '../../utils/format';
import './Admin.css';

const empty = { name:'', description:'', oldPrice:0, sku:'', stock:0, categoryId:'', brandId:'' };

const AdminProducts = () => {
  const { t } = useLang();
  const [items, setItems] = useState([]);
  const [cats, setCats] = useState([]);
  const [brands, setBrands] = useState([]);
  const [tags, setTags] = useState([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(null);
  const [form, setForm] = useState(empty);
  const [selectedFiles, setSelectedFiles] = useState([]);
  const [previewUrls, setPreviewUrls] = useState([]);
  const [saving, setSaving] = useState(false);
  const [uploading, setUploading] = useState(false);
  const { addToast } = useToast();
  const [msg, setMsg] = useState('');

  const load = () => {
    setLoading(true);
    adminProductApi.getAll(page, 10).then(d => {
      setItems(d?.items || []);
      setTotalPages(d?.meta?.totalPages || 1);
    }).catch(() => {}).finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, [page]);
  useEffect(() => {
    adminCategoryApi.getAll(1,100).then(d => setCats(d?.items||[])).catch(()=>{});
    adminBrandApi.getAll(1,100).then(d => setBrands(d?.items||[])).catch(()=>{});
    adminTagApi.getAll(1,100).then(d => setTags(d?.items||[])).catch(()=>{});
  }, []);

  const openCreate = () => {
    setForm({...empty}); setSelectedFiles([]); setPreviewUrls([]);
    setModal('create'); setMsg('');
  };

  const openEdit = (item) => {
    setForm({
      name: item.name||'', description: item.description||'',
      oldPrice: item.oldPrice||0, sku: item.sku||'',
      stock: item.stock||0, categoryId: item.category?.id||'',
      brandId: item.brand?.id||''
    });
    setSelectedFiles([]);
    setPreviewUrls(item.imageUrl || []);
    setModal(item); setMsg('');
  };

  const close = () => { setModal(null); setSelectedFiles([]); setPreviewUrls([]); };

  const handleFileSelect = (e) => {
    const files = Array.from(e.target.files);
    setSelectedFiles(prev => [...prev, ...files]);
    files.forEach(f => {
      const reader = new FileReader();
      reader.onload = (ev) => setPreviewUrls(prev => [...prev, ev.target.result]);
      reader.readAsDataURL(f);
    });
    e.target.value = '';
  };

  const removePreview = (idx) => {
    setPreviewUrls(prev => prev.filter((_,i) => i !== idx));
    // Only remove from selectedFiles if it's a new file (not existing URL)
    const existingCount = (modal !== 'create' && modal?.imageUrl) ? modal.imageUrl.length : 0;
    if (idx >= existingCount) {
      setSelectedFiles(prev => prev.filter((_,i) => i !== (idx - existingCount)));
    }
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      const body = { ...form, oldPrice: Number(form.oldPrice), stock: Number(form.stock) };
      let productId;
      if (modal === 'create') {
        const created = await adminProductApi.create(body);
        productId = created.id;
      } else {
        await adminProductApi.update(modal.id, body);
        productId = modal.id;
      }

      // Upload images if any
      if (selectedFiles.length > 0 && productId) {
        setUploading(true);
        try {
          await adminProductApi.uploadImages(productId, selectedFiles);
        } catch (e) {
          addToast('Sản phẩm đã lưu nhưng upload ảnh lỗi: ' + e.message, 'error');
          setSaving(false); setUploading(false);
          load(); close();
          return;
        }
        setUploading(false);
      }

      addToast(modal === 'create' ? 'Thêm sản phẩm thành công!' : 'Cập nhật sản phẩm thành công!');
      close(); load();
    } catch (e) { addToast(e.message, 'error'); }
    setSaving(false);
  };

  const handleDelete = async (id) => {
    if(!confirm('Xóa sản phẩm?')) return;
    try { await adminProductApi.delete(id); addToast('Đã xóa sản phẩm!'); load(); } catch(e) { addToast(e.message, 'error'); }
  };

  return (
    <div>
      <div className="adm-page-header">
        <h1 className="adm-page-title">{t('admin.products')}</h1>
        <button className="adm-btn adm-btn-primary" onClick={openCreate}><Plus size={16}/> Add Product</button>
      </div>
      <div className="adm-card">
        {loading ? <div className="adm-empty">Loading...</div> : items.length === 0 ? <div className="adm-empty">No products yet.</div> : (
          <table className="adm-table">
            <thead><tr><th>Image</th><th>Name</th><th>SKU</th><th>Price</th><th>Stock</th><th>Category</th><th>Brand</th><th>Actions</th></tr></thead>
            <tbody>{items.map(item => (
              <tr key={item.id}>
                <td>{item.imageUrl?.[0] ? <img src={item.imageUrl[0]} className="adm-table-img" alt=""/> : '—'}</td>
                <td><strong>{item.name}</strong></td>
                <td>{item.sku}</td>
                <td>{formatPrice(item.oldPrice)}</td>
                <td>{item.stock}</td>
                <td>{item.category?.name || '—'}</td>
                <td>{item.brand?.name || '—'}</td>
                <td><div className="adm-table-actions">
                  <button className="adm-btn adm-btn-outline adm-btn-sm" onClick={() => openEdit(item)}><Pencil size={14}/></button>
                  <button className="adm-btn adm-btn-danger adm-btn-sm" onClick={() => handleDelete(item.id)}><Trash2 size={14}/></button>
                </div></td>
              </tr>
            ))}</tbody>
          </table>
        )}
        {totalPages > 1 && <div className="adm-pagination">{[...Array(totalPages)].map((_,i)=> <button key={i} className={`adm-pg-btn ${page===i+1?'active':''}`} onClick={()=>setPage(i+1)}>{i+1}</button>)}</div>}
      </div>

      {modal && (
        <div className="adm-modal-overlay" onClick={close}>
          <div className="adm-modal" onClick={e=>e.stopPropagation()} style={{maxWidth: 640}}>
            <h3 className="adm-modal-title">{modal==='create'?'Add Product':'Edit Product'}</h3>
            {msg && <div className="adm-msg adm-msg-error">{msg}</div>}

            <div className="adm-form-group"><label>Name</label><input value={form.name} onChange={e=>setForm({...form,name:e.target.value})}/></div>
            <div className="adm-form-row">
              <div className="adm-form-group"><label>SKU</label><input value={form.sku} onChange={e=>setForm({...form,sku:e.target.value})}/></div>
              <div className="adm-form-group"><label>Price</label><input type="number" value={form.oldPrice} onChange={e=>setForm({...form,oldPrice:e.target.value})}/></div>
            </div>
            <div className="adm-form-group"><label>Stock</label><input type="number" value={form.stock} onChange={e=>setForm({...form,stock:e.target.value})}/></div>
            <div className="adm-form-row">
              <div className="adm-form-group"><label>Category</label><select value={form.categoryId} onChange={e=>setForm({...form,categoryId:e.target.value})}><option value="">-- Select --</option>{cats.map(c=><option key={c.id} value={c.id}>{c.name}</option>)}</select></div>
              <div className="adm-form-group"><label>Brand</label><select value={form.brandId} onChange={e=>setForm({...form,brandId:e.target.value})}><option value="">-- Select --</option>{brands.map(b=><option key={b.id} value={b.id}>{b.name}</option>)}</select></div>
            </div>
            <div className="adm-form-group"><label>Description</label><textarea rows={3} value={form.description} onChange={e=>setForm({...form,description:e.target.value})}/></div>

            {/* Image Upload Section */}
            <div className="adm-form-group">
              <label>Product Images</label>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginBottom: 8 }}>
                {previewUrls.map((url, idx) => (
                  <div key={idx} style={{ position: 'relative', width: 72, height: 72 }}>
                    <img src={url} alt="" style={{ width: 72, height: 72, objectFit: 'cover', borderRadius: 8, border: '1px solid #eaecf0' }}/>
                    <button
                      type="button"
                      onClick={() => removePreview(idx)}
                      style={{ position: 'absolute', top: -6, right: -6, width: 20, height: 20, borderRadius: '50%', background: '#ea4335', color: '#fff', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 0 }}
                    ><X size={12}/></button>
                  </div>
                ))}
                <label className="adm-file-input" style={{ width: 72, height: 72, display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer', margin: 0 }}>
                  <Upload size={20} color="#667085"/>
                  <input type="file" hidden multiple accept="image/*" onChange={handleFileSelect}/>
                </label>
              </div>
              {selectedFiles.length > 0 && <div style={{ fontSize: 12, color: '#667085' }}>{selectedFiles.length} file(s) mới sẽ được upload</div>}
            </div>

            <div className="adm-modal-actions">
              <button className="adm-btn adm-btn-outline" onClick={close}>Cancel</button>
              <button className="adm-btn adm-btn-primary" onClick={handleSave} disabled={saving || uploading}>
                {uploading ? 'Uploading images...' : saving ? 'Saving...' : 'Save'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminProducts;
