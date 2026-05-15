export const API_URL = 'http://localhost:8080/api';

export const getToken = () => localStorage.getItem('token');

export const authHeaders = () => {
  const token = getToken();
  return {
    'Content-Type': 'application/json',
    ...(token && { Authorization: `Bearer ${token}` }),
  };
};
