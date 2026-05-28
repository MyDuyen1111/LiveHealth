import { createContext, useState, useContext, useEffect } from 'react';
import { useAuth } from './AuthContext';

const WishlistContext = createContext();

export function WishlistProvider({ children }) {
  const { user, isAuthenticated } = useAuth();
  const [wishlist, setWishlist] = useState([]);

  // Determine storage key based on authentication status
  const getStorageKey = () => {
    return isAuthenticated && user?.email 
      ? `livehealth_wishlist_${user.email}` 
      : 'livehealth_wishlist_guest';
  };

  // Load wishlist from localStorage on mount or auth change
  useEffect(() => {
    const key = getStorageKey();
    try {
      const stored = localStorage.getItem(key);
      setWishlist(stored ? JSON.parse(stored) : []);
    } catch (e) {
      console.error('Error loading wishlist:', e);
      setWishlist([]);
    }
  }, [user, isAuthenticated]);

  // Save wishlist to localStorage whenever it changes
  const saveWishlist = (items) => {
    setWishlist(items);
    const key = getStorageKey();
    try {
      localStorage.setItem(key, JSON.stringify(items));
    } catch (e) {
      console.error('Error saving wishlist:', e);
    }
  };

  const addToWishlist = (product) => {
    if (!product || !product.id) return;
    if (wishlist.some(item => item.id === product.id)) return;
    const updated = [...wishlist, product];
    saveWishlist(updated);
  };

  const removeFromWishlist = (productId) => {
    const updated = wishlist.filter(item => item.id !== productId);
    saveWishlist(updated);
  };

  const toggleWishlist = (product) => {
    if (!product || !product.id) return;
    if (wishlist.some(item => item.id === product.id)) {
      removeFromWishlist(product.id);
    } else {
      addToWishlist(product);
    }
  };

  const isInWishlist = (productId) => {
    return wishlist.some(item => item.id === productId);
  };

  return (
    <WishlistContext.Provider
      value={{
        wishlist,
        addToWishlist,
        removeFromWishlist,
        toggleWishlist,
        isInWishlist
      }}
    >
      {children}
    </WishlistContext.Provider>
  );
}

export const useWishlist = () => useContext(WishlistContext);
