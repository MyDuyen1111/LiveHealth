import { useState, useEffect } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import { adminMemberApi } from '../../api/adminApi';
import { useToast } from '../../components/admin/Toast';
import './Admin.css';

const empty = { name:'', position:'', description:'' };
const AdminMembers = () => {
  const [items,setItems]=useState([]); const [loading,setLoading]=useState(true);
  const [modal,setModal]=useState(null); const [form,setForm]=useState(empty);
  const [saving,setSaving]=useState(false);  const [msg, setMsg] = useState('');
  const { addToast } = useToast();

  const load=()=>{setLoading(true);adminMemberApi.getAll(1,50).then(d=>setItems(d?.items||[])).catch(()=>{}).finally(()=>setLoading(false));};
  useEffect(()=>{load();},[]);

  const openCreate=()=>{setForm({...empty});setModal('create');setMsg('');};
  const openEdit=(item)=>{setForm({name:item.name||'',position:item.position||'',description:item.description||''});setModal(item);setMsg('');};
  const close=()=>setModal(null);
  const handleSave=async()=>{setSaving(true);try{modal==='create'?await adminMemberApi.create(form):await adminMemberApi.update(modal.id,form);close();load();    } catch (e) { addToast(e.message, 'error'); }setSaving(false);};
  const handleDelete=async(id)=>{if(!confirm('Xóa?'))return;try{await adminMemberApi.delete(id);addToast('Đã xóa!');load();}catch(e){addToast(e.message, 'error');}};
  const handleAvatar=async(id,file)=>{try{await adminMemberApi.uploadAvatar(id,file);load();}catch(e){addToast(e.message, 'error');}};

  return (
    <div>
      <div className="adm-page-header"><h1 className="adm-page-title">Professional Members</h1><button className="adm-btn adm-btn-primary" onClick={openCreate}><Plus size={16}/> Add</button></div>
      <div className="adm-card">
        {loading?<div className="adm-empty">Loading...</div>:items.length===0?<div className="adm-empty">No items.</div>:(
          <table className="adm-table"><thead><tr><th>Avatar</th><th>Name</th><th>Position</th><th>Actions</th></tr></thead><tbody>{items.map(item=>(
            <tr key={item.id}>
              <td>{item.avatarUrl?<img src={item.avatarUrl} className="adm-table-img" alt=""/>:'—'}</td>
              <td><strong>{item.name}</strong></td><td>{item.position||'—'}</td>
              <td><div className="adm-table-actions">
                <label className="adm-btn adm-btn-outline adm-btn-sm" style={{cursor:'pointer'}}>📷<input type="file" hidden accept="image/*" onChange={e=>e.target.files[0]&&handleAvatar(item.id,e.target.files[0])}/></label>
                <button className="adm-btn adm-btn-outline adm-btn-sm" onClick={()=>openEdit(item)}><Pencil size={14}/></button>
                <button className="adm-btn adm-btn-danger adm-btn-sm" onClick={()=>handleDelete(item.id)}><Trash2 size={14}/></button>
              </div></td>
            </tr>))}</tbody></table>
        )}
      </div>
      {modal&&(<div className="adm-modal-overlay" onClick={close}><div className="adm-modal" onClick={e=>e.stopPropagation()}>
        <h3 className="adm-modal-title">{modal==='create'?'Add':'Edit'} Member</h3>{msg&&<div className="adm-msg adm-msg-error">{msg}</div>}
        <div className="adm-form-group"><label>Name</label><input value={form.name} onChange={e=>setForm({...form,name:e.target.value})}/></div>
        <div className="adm-form-group"><label>Position</label><input value={form.position} onChange={e=>setForm({...form,position:e.target.value})}/></div>
        <div className="adm-form-group"><label>Description</label><textarea rows={3} value={form.description} onChange={e=>setForm({...form,description:e.target.value})}/></div>
        <div className="adm-modal-actions"><button className="adm-btn adm-btn-outline" onClick={close}>Cancel</button><button className="adm-btn adm-btn-primary" onClick={handleSave} disabled={saving}>{saving?'Saving...':'Save'}</button></div>
      </div></div>)}
    </div>
  );
};
export default AdminMembers;
