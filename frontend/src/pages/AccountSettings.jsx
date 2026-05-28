import { useState, useRef } from 'react';
import { Link } from 'react-router-dom';
import { Eye, EyeOff } from 'lucide-react';
import { useLang } from '../context/LanguageContext';
import { useAuth } from '../context/AuthContext';
import { userApi } from '../api/userApi';
import AccountSidebar from '../components/AccountSidebar';
import './AccountSettings.css';

const AccountSettings = () => {
  const { t } = useLang();
  const { user, refreshProfile } = useAuth();
  const [showCurrent, setShowCurrent] = useState(false);
  const [showNew, setShowNew] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);
  const [saving, setSaving] = useState(false);
  const [msg, setMsg] = useState('');
  const [pwMsg, setPwMsg] = useState('');
  const fileRef = useRef(null);

  const billing = user?.address || {};

  const handleProfileSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMsg('');
    const fd = new FormData(e.target);
    try {
      await userApi.updateProfile({
        personalInformation: {
          firstName: fd.get('firstName'),
          lastName: fd.get('lastName'),
          phone: fd.get('phone'),
        }
      });
      await refreshProfile();
      setMsg('Cập nhật thông tin thành công!');
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (err) {
      setMsg(err.message || 'Lỗi cập nhật thông tin');
    }
    setSaving(false);
  };

  const handleBillingSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setMsg('');
    const fd = new FormData(e.target);
    try {
      await userApi.updateBillingAddress({
        companyName: fd.get('bCompany'),
        streetAddress: fd.get('bStreet'),
        country: fd.get('bCountry'),
        state: fd.get('bState'),
        zipCode: parseInt(fd.get('bZip') || '0', 10)
      });
      await refreshProfile();
      setMsg('Cập nhật địa chỉ thanh toán thành công!');
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (err) {
      setMsg(err.message || 'Lỗi cập nhật địa chỉ');
    }
    setSaving(false);
  };

  const handleAvatar = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    setSaving(true);
    setMsg('');
    try {
      await userApi.uploadAvatar(file);
      await refreshProfile();
      setMsg('Cập nhật ảnh đại diện thành công!');
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch (err) {
      setMsg(err.message || 'Lỗi cập nhật ảnh đại diện');
    }
    setSaving(false);
  };

  const handlePasswordSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    setPwMsg('');
    const fd = new FormData(e.target);
    const oldPassword = fd.get('oldPassword');
    const newPassword = fd.get('newPassword');
    const confirmPassword = fd.get('confirmPassword');

    if (newPassword !== confirmPassword) {
      setPwMsg('Mật khẩu mới và xác nhận mật khẩu không khớp!');
      setSaving(false);
      return;
    }

    try {
      await userApi.changePassword({ oldPassword, newPassword, confirmPassword });
      setPwMsg('Đổi mật khẩu thành công!');
      e.target.reset();
      setShowCurrent(false);
      setShowNew(false);
      setShowConfirm(false);
    } catch (err) {
      setPwMsg(err.message || 'Lỗi đổi mật khẩu');
    }
    setSaving(false);
  };

  return (
    <div className="as-wrap">
      <div className="as-breadcrumb">
        <div className="container as-bc-inner">
          <Link to="/">🏠</Link><span>›</span>
          <span>{t('account.breadcrumb')}</span><span>›</span>
          <span className="bc-active">{t('settings.breadcrumb')}</span>
        </div>
      </div>

      <div className="container as-layout">
        <AccountSidebar activeItem="settings" />
        <div className="as-main">
          {msg && <div style={{padding:'10px 16px',background:'#e8f5e9',color:'#2e7d32',borderRadius:8,marginBottom:16}}>{msg}</div>}

          <div className="as-section">
            <h2 className="as-section-title">{t('settings.accountSettings')}</h2>
            <form onSubmit={handleProfileSave} className="as-form">
              <div className="as-form-row">
                <div className="as-form-group"><label>{t('settings.firstName')}</label><input name="firstName" type="text" defaultValue={user?.firstName || ''}/></div>
                <div className="as-form-group"><label>{t('settings.lastName')}</label><input name="lastName" type="text" defaultValue={user?.lastName || ''}/></div>
              </div>
              <div className="as-form-group"><label>{t('settings.email')}</label><input name="email" type="email" value={user?.email || ''} readOnly className="as-readonly" title="Email không thể thay đổi tại đây"/></div>
              <div className="as-form-group"><label>{t('settings.phone')}</label><input name="phone" type="tel" defaultValue={user?.phone || ''}/></div>
              <button type="submit" className="as-save-btn" disabled={saving}>{saving ? 'Đang lưu...' : t('settings.saveChanges')}</button>
            </form>
            <div className="as-avatar-section">
              <img src={user?.linkAvatar || `https://ui-avatars.com/api/?name=${encodeURIComponent((user?.firstName||'U'))}&size=120&rounded=true`} alt="Avatar" className="as-avatar-img"/>
              <input type="file" ref={fileRef} style={{display:'none'}} accept="image/*" onChange={handleAvatar}/>
              <button className="as-choose-img-btn" onClick={() => fileRef.current?.click()}>{t('settings.chooseImage')}</button>
            </div>
          </div>

          <div className="as-section">
            <h2 className="as-section-title">{t('settings.billingAddress')}</h2>
            <form onSubmit={handleBillingSave} className="as-form">
              <div className="as-form-row as-form-row-3">
                <div className="as-form-group"><label>{t('settings.firstName')}</label><input name="bFirstName" type="text" value={user?.firstName||''} readOnly className="as-readonly"/></div>
                <div className="as-form-group"><label>{t('settings.lastName')}</label><input name="bLastName" type="text" value={user?.lastName||''} readOnly className="as-readonly"/></div>
                <div className="as-form-group"><label>{t('settings.companyName')} <span className="as-optional">({t('settings.optional')})</span></label><input name="bCompany" type="text" defaultValue={billing.companyName||''}/></div>
              </div>
              <div className="as-form-group"><label>{t('settings.streetAddress')}</label><input name="bStreet" type="text" defaultValue={billing.streetAddress||''} required/></div>
              <div className="as-form-row as-form-row-3">
                <div className="as-form-group"><label>{t('settings.country')}</label><input name="bCountry" type="text" defaultValue={billing.country||'Vietnam'} required/></div>
                <div className="as-form-group"><label>{t('settings.states')}</label><input name="bState" type="text" defaultValue={billing.state||''} required/></div>
                <div className="as-form-group"><label>{t('settings.zipCode')}</label><input name="bZip" type="text" defaultValue={billing.zipCode||''} /></div>
              </div>
              <div className="as-form-row">
                <div className="as-form-group"><label>{t('settings.email')}</label><input name="bEmail" type="email" value={user?.email||''} readOnly className="as-readonly"/></div>
                <div className="as-form-group"><label>{t('settings.phone')}</label><input name="bPhone" type="tel" value={user?.phone||''} readOnly className="as-readonly"/></div>
              </div>
              <button type="submit" className="as-save-btn" disabled={saving}>{saving ? 'Đang lưu...' : t('settings.saveChanges')}</button>
            </form>
          </div>

          <div className="as-section">
            <h2 className="as-section-title">{t('settings.changePassword')}</h2>
            {pwMsg && <div style={{padding:'10px 16px',background: pwMsg.includes('Lỗi') || pwMsg.includes('không khớp') ? '#ffebee' : '#e8f5e9', color: pwMsg.includes('Lỗi') || pwMsg.includes('không khớp') ? '#c62828' : '#2e7d32',borderRadius:8,marginBottom:16}}>{pwMsg}</div>}
            <form onSubmit={handlePasswordSave} className="as-form">
              <div className="as-form-group as-pw-group"><label>{t('settings.currentPassword')}</label><div className="as-pw-wrap"><input name="oldPassword" type={showCurrent?'text':'password'} placeholder="Password" required/><button type="button" className="as-pw-toggle" onClick={()=>setShowCurrent(!showCurrent)}>{showCurrent?<EyeOff size={18}/>:<Eye size={18}/>}</button></div></div>
              <div className="as-form-row">
                <div className="as-form-group as-pw-group"><label>{t('settings.newPassword')}</label><div className="as-pw-wrap"><input name="newPassword" type={showNew?'text':'password'} placeholder="Password" required minLength="6"/><button type="button" className="as-pw-toggle" onClick={()=>setShowNew(!showNew)}>{showNew?<EyeOff size={18}/>:<Eye size={18}/>}</button></div></div>
                <div className="as-form-group as-pw-group"><label>{t('settings.confirmPassword')}</label><div className="as-pw-wrap"><input name="confirmPassword" type={showConfirm?'text':'password'} placeholder="Password" required minLength="6"/><button type="button" className="as-pw-toggle" onClick={()=>setShowConfirm(!showConfirm)}>{showConfirm?<EyeOff size={18}/>:<Eye size={18}/>}</button></div></div>
              </div>
              <button type="submit" className="as-save-btn" disabled={saving}>{saving ? 'Đang lưu...' : t('settings.changePasswordBtn')}</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AccountSettings;
