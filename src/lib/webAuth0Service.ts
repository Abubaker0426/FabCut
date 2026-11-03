import * as WebBrowser from 'expo-web-browser';
import * as Crypto from 'expo-crypto';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { Platform } from 'react-native';
import Constants, { ExecutionEnvironment } from 'expo-constants';

const AUTH0_DOMAIN = process.env.EXPO_PUBLIC_AUTH0_DOMAIN!;
const AUTH0_CLIENT_ID = process.env.EXPO_PUBLIC_AUTH0_CLIENT_ID!;
const AUTH0_AUDIENCE = process.env.EXPO_PUBLIC_AUTH0_AUDIENCE!;
const AUTH0_SCOPE = process.env.EXPO_PUBLIC_AUTH0_SCOPE ?? 'openid profile email offline_access';
const CREDENTIALS_KEY = 'auth0_credentials';

interface AuthCredentials {
  access_token: string;
  token_type: string;
  refresh_token?: string;
}

// Base64url encode without padding
function base64urlEncode(buffer: Uint8Array): string {
  return btoa(String.fromCharCode(...buffer))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=/g, '');
}

async function generateCodeVerifier(): Promise<string> {
  const bytes = await Crypto.getRandomBytesAsync(32);
  return base64urlEncode(bytes);
}

async function generateCodeChallenge(verifier: string): Promise<string> {
  const digest = await Crypto.digestStringAsync(Crypto.CryptoDigestAlgorithm.SHA256, verifier, {
    encoding: Crypto.CryptoEncoding.BASE64,
  });
  return digest.replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
}

class WebAuth0Service {
  private get redirectUri(): string {
    const isExpoGo = Constants.executionEnvironment === ExecutionEnvironment.StoreClient;
    console.log('[Auth0] executionEnvironment:', Constants.executionEnvironment);
    console.log('[Auth0] isExpoGo:', isExpoGo);
    if (isExpoGo) {
      const uri = 'exp://192.168.5.56:8081/--/android/com.idflo.FabCut/callback';
      console.log('[Auth0] redirectUri (ExpoGo):', uri);
      return uri;
    }
    const uri = 'xyza://dev-q9u-izlr.auth0.com/android/com.idflo.FabCut/callback';
    console.log('[Auth0] redirectUri (DevClient/Prod):', uri);
    return uri;
  }

  async login(): Promise<AuthCredentials> {
    // PKCE flow — same as the Java WebAuthProvider uses
    const codeVerifier = await generateCodeVerifier();
    const codeChallenge = await generateCodeChallenge(codeVerifier);
    const state = base64urlEncode(await Crypto.getRandomBytesAsync(16));

    const redirectUri = this.redirectUri;

    const authUrl =
      `https://${AUTH0_DOMAIN}/authorize?` +
      `client_id=${encodeURIComponent(AUTH0_CLIENT_ID)}&` +
      `response_type=code&` +
      `scope=${encodeURIComponent(AUTH0_SCOPE)}&` +
      `audience=${encodeURIComponent(AUTH0_AUDIENCE)}&` +
      `redirect_uri=${encodeURIComponent(redirectUri)}&` +
      `code_challenge=${encodeURIComponent(codeChallenge)}&` +
      `code_challenge_method=S256&` +
      `state=${encodeURIComponent(state)}`;

    console.log('[Auth0] Full authUrl:', authUrl);
    console.log('[Auth0] redirect_uri being sent:', redirectUri);

    const result = await WebBrowser.openAuthSessionAsync(authUrl, redirectUri);
    console.log('[Auth0] WebBrowser result type:', result.type);

    if (result.type !== 'success' || !result.url) {
      throw new Error(`Login was cancelled or failed: ${result.type}`);
    }

    console.log('[Auth0] callback url:', result.url);

    // Extract code from query params
    const params = new URLSearchParams(result.url.split('?')[1] ?? '');
    const code = params.get('code');
    if (!code) throw new Error('No authorization code in callback URL');

    // Exchange code for tokens
    const tokenRes = await fetch(`https://${AUTH0_DOMAIN}/oauth/token`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        grant_type: 'authorization_code',
        client_id: AUTH0_CLIENT_ID,
        code,
        redirect_uri: redirectUri,
        code_verifier: codeVerifier,
      }).toString(),
    });

    if (!tokenRes.ok) {
      const err = await tokenRes.text();
      throw new Error(`Token exchange failed: ${err}`);
    }

    const tokens = await tokenRes.json();
    if (!tokens.access_token) throw new Error('No access_token in token response');

    const credentials: AuthCredentials = {
      access_token: tokens.access_token,
      token_type: 'Bearer',
      refresh_token: tokens.refresh_token,
    };
    await this.saveCredentials(credentials);
    return credentials;
  }

  async saveCredentials(credentials: AuthCredentials): Promise<void> {
    await AsyncStorage.setItem(CREDENTIALS_KEY, JSON.stringify(credentials));
  }

  async getStoredCredentials(): Promise<AuthCredentials | null> {
    const stored = await AsyncStorage.getItem(CREDENTIALS_KEY);
    if (!stored) return null;
    try {
      const credentials = JSON.parse(stored) as AuthCredentials;
      return credentials.access_token ? credentials : null;
    } catch {
      return null;
    }
  }

  async clearStoredCredentials(): Promise<void> {
    await AsyncStorage.removeItem(CREDENTIALS_KEY);
  }

  async logout(): Promise<void> {
    await this.clearStoredCredentials();
    if (Platform.OS !== 'web') {
      const logoutUrl =
        `https://${AUTH0_DOMAIN}/v2/logout?` +
        `client_id=${encodeURIComponent(AUTH0_CLIENT_ID)}&` +
        `returnTo=${encodeURIComponent(this.redirectUri)}`;
      await WebBrowser.openBrowserAsync(logoutUrl);
    }
  }
}

export const webAuth0Service = new WebAuth0Service();
