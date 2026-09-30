// import axios from 'axios';
// import { useAuthStore } from '@/store/authStore';

// const AUTH_CONFIG = {
//   baseURL: 'https://api.fabtrakr.com',
//   auth: {
//     clientId: 'j4jY7eXPLnSi6Ndt2sAGNUiUy0X4vocE',
//     clientSecret: 'UOL5-fDLRjhpUICId3Jefvc-EVB0YhRe_azDuGwLnRjnCO4D_oH5MuahRBqMjn_W',
//     audience: 'https://api.fabtrakr.com',
//     tokenEndpoint: 'https://dev-q9u-izlr.auth0.com/oauth/token',
//   },
// };

// class AuthService {
//   private static instance = new AuthService();

//   private token: string | null = null;
//   private tokenExpiry: Date | null = null;
//   private tokenRequest: Promise<string> | null = null;

//   static getInstance(): AuthService {
//     return this.instance;
//   }

//   private async getNewToken(): Promise<string> {
//     const { data } = await axios.post(AUTH_CONFIG.auth.tokenEndpoint, {
//       client_id: AUTH_CONFIG.auth.clientId,
//       client_secret: AUTH_CONFIG.auth.clientSecret,
//       audience: AUTH_CONFIG.auth.audience,
//       grant_type: 'client_credentials',
//     });

//     if (!data.access_token) throw new Error('No access token received');

//     this.token = data.access_token;
//     this.tokenExpiry = new Date(Date.now() + (data.expires_in - 300) * 1000);

//     // Save to store so axios client interceptor picks it up
//     useAuthStore.getState().setToken(data.access_token);

//     return data.access_token;
//   }

//   async getToken(): Promise<string> {
//     // Return cached token if still valid
//     if (this.token && this.tokenExpiry && this.tokenExpiry > new Date()) {
//       return this.token;
//     }

//     // Deduplicate concurrent token requests
//     if (this.tokenRequest) return this.tokenRequest;

//     this.tokenRequest = this.getNewToken();
//     try {
//       return await this.tokenRequest;
//     } finally {
//       this.tokenRequest = null;
//     }
//   }

//   async request<T = unknown>(
//     method: string,
//     endpoint: string,
//     body?: unknown,
//     queryParams?: Record<string, string | number | boolean> | null
//   ): Promise<T> {
//     const makeRequest = async (token: string): Promise<T> => {
//       let url = `${AUTH_CONFIG.baseURL}${endpoint}`;

//       if (queryParams) {
//         const params = new URLSearchParams();
//         Object.entries(queryParams).forEach(([key, value]) => {
//           if (value !== null && value !== undefined) {
//             params.append(key, String(value));
//           }
//         });
//         url += `?${params.toString()}`;
//       }

//       const response = await axios({
//         method,
//         url,
//         data: body,
//         headers: {
//           Authorization: `Bearer ${token}`,
//           'Content-Type': 'application/json',
//           'Cache-Control': 'no-cache',
//           Version: '8.0',
//         },
//       });

//       return response.data;
//     };

//     try {
//       const token = await this.getToken();
//       return await makeRequest(token);
//     } catch (error: unknown) {
//       if (axios.isAxiosError(error) && error.response?.status === 401) {
//         // Clear token and retry once — same as old JS behaviour
//         this.token = null;
//         this.tokenExpiry = null;
//         useAuthStore.getState().clearAuth();
//         const newToken = await this.getToken();
//         return await makeRequest(newToken);
//       }
//       throw error;
//     }
//   }
// }

// export default AuthService.getInstance();
