import { createContext, useState, useContext, useCallback } from "react";

const InvitationContext = createContext({
  refreshFlag: 0,
  triggerRefresh: () => {},
});

export const InvitationProvider = ({ children }: { children: React.ReactNode }) => {
  const [refreshFlag, setRefreshFlag] = useState(0);

  const triggerRefresh = useCallback(() => {
    setRefreshFlag((prev) => prev + 1); 
  }, []);

  return (
    <InvitationContext.Provider value={{ refreshFlag, triggerRefresh }}>
      {children}
    </InvitationContext.Provider>
  );
};

export const useInvitationContext = () => useContext(InvitationContext);