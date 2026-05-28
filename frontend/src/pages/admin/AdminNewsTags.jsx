import { useState, useEffect } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import { adminNewsTagApi } from '../../api/adminApi';
import { useToast } from '../../components/admin/Toast';
import './Admin.css';

const AdminNewsTags = () => {
  const [items, setItems] = useState([]); const [loading, setLoading] = useState(true);
  const [modal, setModal] = useState(null); const [form, setForm] = useState({ name:'' });
  const [saving, setSaving] = useState(false);  const [msg, setMsg] = useState('');
  const { addToast } = useToast();

  const load = () => {setLoading(true);adminNewsTagApi.getAll(1,100).then(d=>setItems(d?.items||[])).catch(()=>{}).finally(()=>setLoading(false));};
  useEffect(()=>{load();},[]);

  const openCreate = () => {setForm({name:''});setModal('create');setMsg('');};
  const openEdit = (item) => {setForm({name:item.name||''});setModal(item);setMsg('');};
  const close = () => setModal(null);
  const handleSave = async () => {setSaving(true);try{modal==='create'?await adminNewsTagApi.create(form):await adminNewsTagApi.update(modal.id,form);close();load();    } catch (e) { addToast(e.message, 'error'); }setSaving(false);};
  const handleDelete = async (id) => {if(!confirm('Xóa?'))return;try{await adminNewsTagApi.delete(id);addToast('Đã xóa!');load();}catch(e){addToast(e.message, 'error');}};

  return (
    <div>
      <div className="adm-page-header"><h1 className="adm-page-title">News Tags</h1><button className="adm-btn adm-btn-primary" onClick={openCreate}><Plus size={16}/> Add</button></div>
      <div className="adm-card">
        {loading?<div className="adm-empty">Loading...</div>:items.length===0?<div className="adm-empty">No items.</div>:(<table className="adm-table"><thead><tr><th>Name</th><th>Actions</th></tr></thead><tbody>{items.map(item=>(<tr key={item.id}><td><strong>{item.name}</strong></td><td><div className="adm-table-actions"><button className="adm-btn adm-btn-outline adm-btn-sm" onClick={()=>openEdit(item)}><Pencil size={14}/></button><button className="adm-btn adm-btn-danger adm-btn-sm" onClick={()=>handleDelete(item.id)}><Trash2 size={14}/></button></div></td></tr>))}</tbody></table>)}
      </div>
      {modal&&(<div className="adm-modal-overlay" onClick={close}><div className="adm-modal" onClick={e=>e.stopPropagation()}><h3 className="adm-modal-title">{modal==='create'?'Add':'Edit'} News Tag</h3>{msg&&<div className="adm-msg adm-msg-error">{msg}</div>}<div className="adm-form-group"><label>Name</label><input value={form.name} onChange={e=>setForm({...form,name:e.target.value})}/></div><div className="adm-modal-actions"><button className="adm-btn adm-btn-outline" onClick={close}>Cancel</button><button className="adm-btn adm-btn-primary" onClick={handleSave} disabled={saving}>{saving?'Saving...':'Save'}</button></div></div></div>)}
    </div>
  );
};
export default AdminNewsTags;
