import * as React from "react"


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

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [user, setUser] = React.useState<AuthUser | undefined>(undefined)
    const [loading, setLoading] = React.useState(true)

    React.useEffect(() => {
        //console.log("Fetching user...")
        fetch("http://localhost:8080/user", {
            credentials: "include"
        })
        .then(res => {
            //console.log("Response status:", res.status)
            if (!res.ok) throw new Error("Not authenticated")
            return res.json()
        })
        .then(data => {
            //console.log("User data received:", data)
            setUser({ id:data.id, name: data.name, email: data.email });
        })
        .catch(err => {
            //console.warn("Fetch failed:", err)
            setUser(undefined)
        })
        .finally(() => {
            //console.log("Loading finished")
            setLoading(false)
        })
    }, [])

    return (
        <AuthContext.Provider value={{ user, setUser, loading }}>
            {children}
        </AuthContext.Provider>
    )
}
