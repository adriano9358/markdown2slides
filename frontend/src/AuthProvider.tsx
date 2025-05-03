import * as React from "react"

type AuthContextType = {
    username: string | undefined;
    setUsername: (v: string | undefined) => void;
    loading: boolean;
}

export const AuthContext = React.createContext<AuthContextType>({
    username: undefined,
    setUsername: () => { throw Error("Not implemented!") },
    loading: true,
})

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [user, setUser] = React.useState<string | undefined>(undefined)
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
            setUser(data.name)
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
        <AuthContext.Provider value={{ username: user, setUsername: setUser, loading }}>
            {children}
        </AuthContext.Provider>
    )
}
