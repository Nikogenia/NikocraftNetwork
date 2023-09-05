import APIOffline from "@/components/APIOffline";
import Animator from "@/components/Animator";
import Footer from "@/components/Footer";
import { apiRequest } from "@/components/api";
import { useRouter } from "next/router";
import { useEffect } from "react";


async function logoutUser(router, setStatus, setStatusText, setUsername, setAdmin) {

    console.info("Logout user")

    const data = await apiRequest("/user/logout", {}, setStatus, setStatusText)
    if (data == null) return

    if (data.error != "success") {
        console.log("Failed to logout: " + data.error)
        router.push("/user/login")
        return
    }

    console.info("Logout successful")

    setUsername("")
    setAdmin(false)

    router.push("/user/login")

}


export default function UserLogout({
        username, setUsername,
        admin, setAdmin,
        status, setStatus,
        statusText, setStatusText
    }) {

    const router = useRouter()

    useEffect(() => {
        logoutUser(router, setStatus, setStatusText, setUsername, setAdmin)
        const interval = setInterval(() => {
            if (status == 200) {
                logoutUser(router, setStatus, setStatusText, setUsername, setAdmin)
            }
        }, 6000);
        return () => clearInterval(interval);
    }, [status])

    if (status != 200) return (
        <APIOffline status={status} statusText={statusText} />
    )

    return (
        <Animator>
            <div className="flex flex-col h-screen">
                <div className="bg-indigo-950 flex justify-center items-center h-full">
                    <div className="flex flex-col items-center justify-center bg-indigo-900 w-5/6 h-5/6 rounded-xl">
                        <h1 className="text-6xl font-bold text-indigo-50 text-center
                            pt-8 mb-20">Logout</h1>
                        <div className="text-3xl text-indigo-50 pb-8">Logout is in progress ...</div>
                    </div>
                </div>
                <Footer />
            </div>
        </Animator>
    )

}
