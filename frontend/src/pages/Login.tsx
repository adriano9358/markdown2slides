import { OAUTH_ENDPOINT } from "../auth/oauth_endpoint";
import { API_PREFIX, BACKEND_URL } from "../http/request"

export const Login: React.FC = () => {
  return (
    <div className="d-flex justify-content-center align-items-center vh-100 bg-light overflow-hidden">
      <div className="card shadow-lg border-0 rounded-3" style={{ maxWidth: '400px', width: '100%' }}>
        <div className="card-body p-4">
          <div className="text-center mb-4">
            <img
              src="/logo.png"
              alt="Logo"
              style={{ width: '100px' }}
              className="mb-3"
            />
            <h2 className="h4">Welcome Back</h2>
            <p className="text-muted">Please login to continue</p>
          </div>
          <div className="d-flex justify-content-center">
          <a
            href={OAUTH_ENDPOINT}
            className="btn btn-outline-secondary btn-lg"
          >
            {/* Google Logo */}
            <img
              src="/google_logo.png" 
              alt="Google"
              style={{
                width: '24px', 
                height: '24px',
                marginRight: '10px', 
              }}
            />
            Login with Google
          </a>
          </div>
        </div>
      </div>
    </div>
  );
};
