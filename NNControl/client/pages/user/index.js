import APIOffline from "@/components/APIOffline";
import Animator from "@/components/Animator";
import ErrorPopup from "@/components/ErrorPopup";
import Footer from "@/components/Footer";
import Header from "@/components/Header";
import Loading from "@/components/Loading";
import { apiRequest, getUser } from "@/components/api";
import { useRouter } from "next/router";
import { useEffect, useState } from "react";


async function changePassword(setStatus, setStatusText, setChanged, username, password) {

    console.info("Change password for '" + username + "'")

    const data = await apiRequest("/user/password", {
        username: username,
        password: password
    }, setStatus, setStatusText)
    if (data == null) return

    if (data.error == "success") {
        console.info("Password changed")
        setChanged(true)
        return
    }

    console.log("Changing password failed: " + data.error)

}


export default function User({
        username, setUsername,
        admin, setAdmin,
        status, setStatus,
        statusText, setStatusText
    }) {

    const router = useRouter()

    const [passwordInput, setPasswordInput] = useState("")
    const [changed, setChanged] = useState(false)
    const [error, setError] = useState("")
    const [errorMessage, setErrorMessage] = useState("")
    const [errorSeconds, setErrorSeconds] = useState(0)

    useEffect(() => {
        if (username == "") getUser(router, setStatus, setStatusText, setUsername, setAdmin)
    }, [])

    if (status != 200) return (
        <APIOffline status={status} statusText={statusText} />
    )

    const submit = async () => {
        setChanged(false)
        if (passwordInput.length < 4) {
            setError("too_short")
            setErrorMessage("The password need to be at least 4 characters long!")
            setErrorSeconds(10)
            return
        }
        getUser(router, setStatus, setStatusText, setUsername, setAdmin)
        changePassword(setStatus, setStatusText, setChanged, username, passwordInput)
    }

    const handleKeyPress = (e) => {
        if (e.key == "Enter") {
            submit()
        }
    }

    if (username == "") return (
        <Loading />
    )

    return (
        <Animator>
            <ErrorPopup error={error} errorMessage={errorMessage} errorSeconds={errorSeconds} />
            <div className="flex flex-col h-screen">
                <Header username={username} admin={admin} />
                <div className="bg-indigo-950 flex justify-center items-center h-full">
                    <div className="flex flex-col items-center justify-center bg-indigo-900 w-5/6 h-5/6 rounded-xl">
                        <h1 className="text-6xl font-bold text-indigo-50 text-center
                            pt-8">User</h1>
                        <h2 className="text-4xl text-indigo-50 text-center
                            pt-5">{username}</h2>
                        {
                            (admin) ? (
                            <div className="text-3xl text-indigo-50 pt-5">You are an admin!</div>
                            ) : <></>
                        }
                        <div className="grid grid-cols-3 grid-rows-1 gap-3 mb-6 mt-16">
                            <div className="text-indigo-50 text-3xl">
                                Password
                            </div>
                            <div className="col-span-2">
                                <input className="bg-indigo-300 text-indigo-950 rounded text-3xl px-1
                                    hover:bg-indigo-400 transition-color
                                    duration-300 border-indigo-200 hover:border-indigo-100 border-2"
                                    type="password" value={passwordInput}
                                    onChange={(e) => setPasswordInput(e.target.value)}
                                    onKeyDown={handleKeyPress}></input>
                            </div>
                        </div>
                        {
                            (changed) ? (
                                <div className="text-lime-200 bg-lime-700 py-2 px-4 mb-6
                                rounded-xl text-xl">Password changed successfully</div>
                            ) : <></>
                        }
                        <button className="bg-indigo-500 px-20 pt-2 pb-3 mb-8 rounded-xl
                            text-indigo-50 text-3xl hover:bg-indigo-600 transition-color
                            duration-300 border-indigo-400 hover:border-indigo-300 border-2"
                            onClick={submit}>Change Password</button>
                    </div>
                </div>
                <Footer />
            </div>
        </Animator>
    )

}
