export const API_PREFIX = "/api"
export const BACKEND_URL = "http://localhost:8080"


export async function request<T>(
  endpoint: string,
  options?: RequestInit,
  contentIsJson?: boolean,
  responseAsText?: false
): Promise<T>;

export async function request(
  endpoint: string,
  options: RequestInit,
  contentIsJson: boolean,
  responseAsText: true
): Promise<string>;

export async function request<T>(
  endpoint: string,
  options: RequestInit = {},
  contentIsJson: boolean = true,
  responseAsText: boolean = false
): Promise<T | string> {
  const response = await fetch(`${BACKEND_URL}${API_PREFIX}${endpoint}`, {
    headers: {
      "Content-Type": contentIsJson ? "application/json" : "text/plain",
      ...options.headers,
    },
    ...options,
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || "API Error");
  }

  if (response.status === 204 || response.headers.get("Content-Length") === "0") {
    return null as any;
  }

  return responseAsText ? await response.text() : await response.json();
}
