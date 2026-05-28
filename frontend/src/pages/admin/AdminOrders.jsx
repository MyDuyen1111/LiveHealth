import { useState, useEffect } from 'react';
import { adminOrderApi } from '../../api/adminApi';
import { useToast } from '../../components/admin/Toast';
import { formatPrice } from '../../utils/format';
import { useLang } from '../../context/LanguageContext';
import './Admin.css';

const statuses = ['ORDER_RECEIVED','PROCESSING','ON_THE_WAY','DELIVERED','COMPLETED','CANCELLED'];

const AdminOrders = () => {
  const [items,setItems]=useState([]); 
  const [page,setPage]=useState(1); 
  const [totalPages,setTotalPages]=useState(1);
  const [loading,setLoading]=useState(true);
  const [detailModal,setDetailModal]=useState(null);
  const { addToast } = useToast();
  const { t } = useLang();

  const load=()=>{
    setLoading(true);
    adminOrderApi.getAll(page,15)
      .then(d=>{
        setItems(d?.items||[]);
        setTotalPages(d?.meta?.totalPages||1);
      })
      .catch(()=>{})
      .finally(()=>setLoading(false));
  };

  useEffect(()=>{
    load();
  },[page]);

  const updateStatus=async(id,status)=>{
    try{
      await adminOrderApi.updateStatus(id,status);
      addToast('Cập nhật trạng thái thành công!');
      load();
    }catch(e){
      addToast(e.message, 'error');
    }
  };

  const openDetail = async (id) => {
    try {
      const details = await adminOrderApi.getById(id);
      setDetailModal(details);
    } catch (e) {
      addToast(e.message || 'Lỗi tải chi tiết đơn hàng', 'error');
    }
  };

  return (
    <div>
      <div className="adm-page-header">
        <h1 className="adm-page-title">{t('admin.orders')}</h1>
      </div>
      <div className="adm-card">
        {loading ? (
          <div className="adm-empty">{t('admin.loading')}</div>
        ) : items.length === 0 ? (
          <div className="adm-empty">{t('admin.noData')}</div>
        ) : (
          <table className="adm-table">
            <thead>
              <tr>
                <th>{t('admin.orderNumber')}</th>
                <th>{t('admin.date')}</th>
                <th>{t('admin.customer')}</th>
                <th>{t('admin.total')}</th>
                <th>{t('admin.orderStatus')}</th>
                <th>{t('admin.updateStatus')}</th>
                <th>{t('admin.actions')}</th>
              </tr>
            </thead>
            <tbody>
              {items.map(o=>(
                <tr key={o.id}>
                  <td><strong>#{o.orderNumber||o.id?.substring(0,6)}</strong></td>
                  <td>{o.createdAt?new Date(o.createdAt).toLocaleDateString('vi-VN'):''}</td>
                  <td>{o.userEmail||'—'}</td>
                  <td>{formatPrice(o.totalAmount||0)}</td>
                  <td>
                    <span className={`adm-status adm-status-${(o.status||'').toLowerCase()}`}>
                      {t(`admin.orderStatus.${o.status}`)}
                    </span>
                  </td>
                  <td>
                    <select 
                      value={o.status||''} 
                      onChange={e=>updateStatus(o.id,e.target.value)} 
                      style={{padding:'6px 10px',borderRadius:6,border:'1px solid #d0d5dd',fontSize:13}}
                    >
                      {statuses.map(s=><option key={s} value={s}>{t(`admin.orderStatus.${s}`)}</option>)}
                    </select>
                  </td>
                  <td>
                    <button className="adm-btn adm-btn-outline adm-btn-sm" onClick={() => openDetail(o.id)}>
                      {t('admin.viewDetails')}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        {totalPages > 1 && (
          <div className="adm-pagination">
            {[...Array(totalPages)].map((_,i)=>(
              <button 
                key={i} 
                className={`adm-pg-btn ${page===i+1?'active':''}`} 
                onClick={()=>setPage(i+1)}
              >
                {i+1}
              </button>
            ))}
          </div>
        )}
      </div>

      {detailModal && (
        <div className="adm-modal-overlay" onClick={() => setDetailModal(null)}>
          <div className="adm-modal" onClick={e => e.stopPropagation()} style={{ maxWidth: 800 }}>
            <h3 className="adm-modal-title">
              {t('admin.orderDetails')} #{detailModal.orderNumber}
            </h3>
            
            <div className="adm-detail-grid" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20, marginBottom: 20 }}>
              <div>
                <h4 style={{ borderBottom: '1px solid #eee', paddingBottom: 6, marginBottom: 10, fontSize: 14, color: '#333' }}>
                  {t('admin.customerInfo')}
                </h4>
                <p style={{ margin: '6px 0', fontSize: 13 }}><strong>{t('admin.fullName')}:</strong> {detailModal.userFullName || t('admin.customer')}</p>
                <p style={{ margin: '6px 0', fontSize: 13 }}><strong>{t('admin.contactEmail')}:</strong> {detailModal.contactEmail || '—'}</p>
                <p style={{ margin: '6px 0', fontSize: 13 }}><strong>{t('admin.contactPhone')}:</strong> {detailModal.contactPhone || '—'}</p>
                <p style={{ margin: '6px 0', fontSize: 13 }}><strong>{t('admin.paymentMethod')}:</strong> {detailModal.paymentMethodName || detailModal.paymentMethod?.name || 'COD'}</p>
              </div>
              <div>
                <h4 style={{ borderBottom: '1px solid #eee', paddingBottom: 6, marginBottom: 10, fontSize: 14, color: '#333' }}>
                  {t('admin.shippingAddress')}
                </h4>
                <p style={{ margin: '6px 0', fontSize: 13 }}><strong>{t('admin.address')}:</strong> {detailModal.shippingAddress?.streetAddress || detailModal.shippingAddress?.street || '—'}</p>
                <p style={{ margin: '6px 0', fontSize: 13 }}><strong>{t('admin.cityState')}:</strong> {detailModal.shippingAddress?.state || detailModal.shippingAddress?.city || '—'}</p>
                <p style={{ margin: '6px 0', fontSize: 13 }}><strong>{t('admin.country')}:</strong> {detailModal.shippingAddress?.country || '—'}</p>
                <p style={{ margin: '6px 0', fontSize: 13 }}><strong>{t('admin.note')}:</strong> {detailModal.note || t('admin.noNotes')}</p>
              </div>
            </div>

            <h4 style={{ borderBottom: '1px solid #eee', paddingBottom: 6, marginBottom: 10, fontSize: 14, color: '#333' }}>
              {t('admin.productList')}
            </h4>
            <div style={{ maxHeight: 200, overflowY: 'auto', marginBottom: 20 }}>
              <table className="adm-table" style={{ width: '100%' }}>
                <thead>
                  <tr>
                    <th>{t('wishlist.product')}</th>
                    <th style={{ textAlign: 'center' }}>{t('admin.unitPrice')}</th>
                    <th style={{ textAlign: 'center' }}>{t('admin.quantity')}</th>
                    <th style={{ textAlign: 'right' }}>{t('admin.subtotalCol')}</th>
                  </tr>
                </thead>
                <tbody>
                  {(detailModal.orderItems || detailModal.items || []).map(item => (
                    <tr key={item.id}>
                      <td>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                          {item.productImage && (
                            <img src={item.productImage} alt={item.productName} style={{ width: 40, height: 40, objectFit: 'cover', borderRadius: 4 }} />
                          )}
                          <strong>{item.productName}</strong>
                        </div>
                      </td>
                      <td style={{ textAlign: 'center' }}>{formatPrice(item.price || item.unitPrice || 0)}</td>
                      <td style={{ textAlign: 'center' }}>{item.quantity}</td>
                      <td style={{ textAlign: 'right' }}>{formatPrice((item.price || item.unitPrice || 0) * item.quantity)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: 8, borderTop: '1px solid #eee', paddingTop: 15, marginBottom: 20 }}>
              <div style={{ fontSize: 13 }}>{t('admin.subtotal')}: <strong>{formatPrice(detailModal.subtotal || 0)}</strong></div>
              <div style={{ fontSize: 13 }}>{t('admin.shippingFee')}: <strong>{formatPrice(detailModal.shippingFee || detailModal.shippingCost || 0)}</strong></div>
              {detailModal.discountPercentage > 0 && (
                <div style={{ fontSize: 13 }}>{t('admin.discount')}: <strong>{detailModal.discountPercentage}%</strong></div>
              )}
              <div style={{ fontSize: 16, fontWeight: 'bold', color: 'var(--green)' }}>
                {t('admin.total')}: {formatPrice(detailModal.totalAmount || 0)}
              </div>
            </div>

            <div className="adm-modal-actions">
              <button className="adm-btn adm-btn-outline" onClick={() => setDetailModal(null)}>{t('admin.close')}</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
export default AdminOrders;
