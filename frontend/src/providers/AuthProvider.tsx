import * as React from "react"
import { getUserInfo } from "../http/userApi";


export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [user, setUser] = React.useState<AuthUser | undefined>(undefined)
    const [loading, setLoading] = React.useState(true)

    React.useEffect(() => {
        getUserInfo().then(data => {
            setUser({ id:data.id, name: data.name, email: data.email });
        })
        .catch(err => {
            setUser(undefined)
        })
        .finally(() => {
            setLoading(false)
        })
    }, [])

    return (
        <AuthContext.Provider value={{ user, setUser, loading }}>
            {children}
        </AuthContext.Provider>
    )
}

type AuthUser = {
    id: string;
    name: string;
    email: string;
};

type AuthContextType = {
    user: AuthUser | undefined;
    setUser: (v: AuthUser | undefined) => void;
    loading: boolean;
}

export const AuthContext = React.createContext<AuthContextType>({
    user: undefined,
    setUser: () => { throw Error("Not implemented!") },
    loading: true,
})
