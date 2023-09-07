import { useRouter } from "next/router";
import { useEffect, useState } from "react";
import { apiRequest } from "@/components/api";
import APIOffline from "@/components/APIOffline";
import Footer from "@/components/Footer";
import Animator from "@/components/Animator";


async function getUser(router, setStatus, setStatusText, setUsername, setAdmin) {

    console.info("Get user")

    const data = await apiRequest("/user", {}, setStatus, setStatusText)
    if (data == null) return

    if (data.error != "success") {
        return
    }

    console.info("User '" + data.username + "' (admin: " + data.admin + ")")

    setUsername(data.username)
    setAdmin(data.admin)

    router.push("/")

}


async function loginUser(router, setStatus, setStatusText, setError, setErrorMessage, username, password) {

    console.info("Log in as '" + username + "'")

    const data = await apiRequest("/user/login", {
        username: username,
        password: password
    }, setStatus, setStatusText)
    if (data == null) return

    if (data.error == "success") {
        console.info("Log in successful")
        router.push("/")
        return
    }

    console.log("Login failed: " + data.error)
    setError(data.error)
    setErrorMessage(data.message)

}


export default function UserLogin({
        username, setUsername,
        admin, setAdmin,
        status, setStatus,
        statusText, setStatusText
    }) {

    const router = useRouter()

    const [error, setError] = useState("")
    const [errorMessage, setErrorMessage] = useState("")
    const [usernameInput, setUsernameInput] = useState("")
    const [passwordInput, setPasswordInput] = useState("")

    useEffect(() => {
        getUser(router, setStatus, setStatusText, setUsername, setAdmin)
    }, [])

    if (status != 200) return (
        <APIOffline status={status} statusText={statusText} />
    )

    const submit = async () => {
        if (usernameInput.length == 0) {
            setError("empty_field")
            setErrorMessage("The username field is empty!")
            return
        }
        if (passwordInput.length == 0) {
            setError("empty_field")
            setErrorMessage("The password field is empty!")
            return
        }
        setError("")
        setErrorMessage("")
        getUser(router, setStatus, setStatusText, setUsername, setAdmin)
        loginUser(router, setStatus, setStatusText,
            setError, setErrorMessage, usernameInput, passwordInput)
    }

    const handleKeyPress = (e) => {
        if (e.key == "Enter") {
            submit()
        }
    }

    return (
        <Animator>
            <div className="flex flex-col h-screen">
                <div className="bg-indigo-950 flex justify-center items-center h-full">
                    <div className="flex flex-col items-center justify-between bg-indigo-900 w-5/6 h-5/6 rounded-xl">
                        <h1 className="text-6xl font-bold text-indigo-50 text-center
                            pt-8 mb-6">Login</h1>
                        <div className="grid grid-cols-3 grid-rows-2 gap-3 mb-6">
                            <div className="text-indigo-50 text-3xl text-right pr-3">
                                Username
                            </div>
                            <div className="col-span-2">
                                <input className="bg-indigo-300 text-indigo-950 rounded text-3xl px-1
                                    hover:bg-indigo-400 transition-colors placeholder-indigo-400
                                    duration-150 border-indigo-200 hover:border-indigo-100 border-2"
                                    type="text" value={usernameInput} autoFocus
                                    placeholder="admin"
                                    onChange={(e) => setUsernameInput(e.target.value)}
                                    onKeyDown={handleKeyPress}></input>
                            </div>
                            <div className="text-indigo-50 text-3xl text-right pr-3">
                                Password
                            </div>
                            <div className="col-span-2">
                                <input className="bg-indigo-300 text-indigo-950 rounded text-3xl px-1
                                    hover:bg-indigo-400 transition-colors placeholder-indigo-400
                                    duration-150 border-indigo-200 hover:border-indigo-100 border-2"
                                    type="password" value={passwordInput}
                                    placeholder="1234"
                                    onChange={(e) => setPasswordInput(e.target.value)}
                                    onKeyDown={handleKeyPress}></input>
                            </div>
                        </div>

                        {(error == "") ? <></> : (
                            <div className="text-red-200 bg-red-700 py-2 px-4 mb-6
                                rounded-xl text-xl">{error}: {errorMessage}</div>
                        )}
                        <button className="bg-indigo-500 px-20 pt-2 pb-3 mb-8 rounded-xl
                            text-indigo-50 text-3xl hover:bg-indigo-600 transition-colors
                            duration-150 border-indigo-400 hover:border-indigo-300 border-2"
                            onClick={submit}>Login</button>
                    </div>
                </div>
                <Footer />
            </div>
        </Animator>
    )

}
