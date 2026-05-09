// jwtHelper.js

/**
 * Decodes a JWT token payload into a JavaScript object
 * يفك تشفير الجزء الأوسط من التوكن ويعيد البيانات على شكل كائن
 */
function decodeToken(token) {
    if (!token) return null;
  
    try {
      const base64Url = token.split('.')[1]; // الجزء الأوسط من التوكن
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/'); // تحويل Base64-URL إلى Base64 عادي
      const jsonPayload = decodeURIComponent(
        atob(base64).split('').map(c =>
          '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
        ).join('')
      );
  
      return JSON.parse(jsonPayload); // تحويل JSON String إلى كائن
    } catch (error) {
      console.error("Invalid JWT token:", error);
      return null;
    }
  }
  
  /**
   * Saves the JWT token to localStorage
   * يحفظ التوكن في التخزين المحلي للمتصفح
   */
  function saveToken(token) {
    if (typeof token === 'string') {
      localStorage.setItem("accessToken", token);
    }
  }
  
  /**
   * Retrieves the JWT token from localStorage
   * يجلب التوكن من التخزين المحلي
   */
  function getToken() {
    return localStorage.getItem("accessToken");
  }
  
  /**
   * Clears the token from localStorage
   * يحذف التوكن عند تسجيل الخروج
   */
  function clearToken() {
    localStorage.removeItem("accessToken");
  }
  
  /**
   * Checks if the JWT token is expired
   * يتحقق من تاريخ انتهاء صلاحية التوكن
   */
  function isTokenExpired() {
    const token = getToken();
    const decoded = decodeToken(token);
    if (!decoded || !decoded.exp) return true;
  
    const now = Date.now() / 1000; // الوقت الحالي بالثواني
    return decoded.exp < now;
  }
  
  /**
   * Gets user information from the token if not expired
   * يجلب معلومات المستخدم إذا كان التوكن ما زال صالحًا
   */
  function getUserInfo() {
    if (isTokenExpired()) {
      console.warn("Token expired. Logging out...");
      clearToken();
      return null;
    }
    return decodeToken(getToken());
  }
  
  /**
   * Exposes functions globally via window.JWT
   * تصدير الدوال لاستخدامها في الملفات الأخرى
   */
  window.JWT = {
    decodeToken,
    saveToken,
    getToken,
    clearToken,
    isTokenExpired,
    getUserInfo
  };
  