import * as React from "react"
import { AuthContext } from "../providers/AuthProvider"
import { Navigate, useLocation } from "react-router-dom"

export function AuthRequire({ children }: { children: React.ReactNode }) {
    const { user, loading } = React.useContext(AuthContext)
    const location = useLocation()
    
    if (loading) return <div>Loading...</div>
    if (user) return <>{children}</>
    return <Navigate to={"/login"} state={{ source: location.pathname }} />
}
