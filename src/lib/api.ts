import axios from 'axios';

// Swap to https://api.fabtrakr.com for release
const BASE_URL = 'https://test.api.fabtrakr.com';

export const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

/** Inject bearer token on every request */
export const setAuthToken = (token: string | null) => {
  if (token) {
    api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common['Authorization'];
  }
};
