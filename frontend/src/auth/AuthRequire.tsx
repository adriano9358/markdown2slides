import * as React from "react"
import { AuthContext } from "../providers/AuthProvider"
import { Navigate, useLocation } from "react-router-dom"

export function AuthRequire({ children }: { children: React.ReactNode }) {
    const { username, loading } = React.useContext(AuthContext)
    const location = useLocation()
    //console.log("AuthRequire", { username, loading });
    if (loading) return <div>Loading...</div>
    if (username) return <>{children}</>
    return <Navigate to={"/login"} state={{ source: location.pathname }} />
}
