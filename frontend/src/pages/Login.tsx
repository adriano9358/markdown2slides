export const Login: React.FC = () => {
    return (
      <div className="d-flex justify-content-center align-items-center vh-100 bg-light">
        <div className="text-center p-5 border rounded shadow bg-white">
          <h2 className="mb-4">Login Required</h2>
          <a
            href="http://localhost:8080/oauth2/authorization/google"
            className="btn btn-primary"
          >
            Login with Google
          </a>
        </div>
      </div>
    );
  };
