import AccessDenied from "@/components/AccessDenied"
import Animator from "@/components/Animator"
import ConfirmPopup from "@/components/ConfirmPopup"
import ErrorPopup from "@/components/ErrorPopup"
import Footer from "@/components/Footer"
import Header from "@/components/Header"
import Loading from "@/components/Loading"
import { apiRequest, getUser } from "@/components/api"
import { useRouter } from "next/router"
import { useEffect, useState } from "react"
import { MdDelete } from "react-icons/md"


async function getUsers(setStatus, setStatusText, setUsers) {

    console.info("Get users")

    const data = await apiRequest("/user/list", {}, setStatus, setStatusText)
    if (data == null) return

    if (data.error == "success") {
        console.info("Got users successfully")
        setUsers(data.users)
        return
    }

    console.log("Get users failed: " + data.error)

}

async function unregisterUser(setStatus, setStatusText, setError, setErrorMessage, setErrorSeconds, username) {

    console.info("Unregister user '" + username + "'")

    const data = await apiRequest("/user/unregister", {
        username: username
    }, setStatus, setStatusText)
    if (data == null) return

    if (data.error == "success") {
        console.info("Unregistered user successfully")
        return
    }

    console.log("Unregister user failed: " + data.error)
    setError(data.error)
    setErrorMessage(data.error)
    setErrorSeconds(10)

}

async function registerUser(setStatus, setStatusText, setError, setErrorMessage, setErrorSeconds, username, password, admin) {

    console.info("Register user '" + username + "'")

    const data = await apiRequest("/user/register", {
        username: username,
        password: password,
        admin: admin
    }, setStatus, setStatusText)
    if (data == null) return

    if (data.error == "success") {
        console.info("Registered user successfully")
        return
    }

    console.log("Register user failed: " + data.error)
    setError(data.error)
    setErrorMessage(data.error)
    setErrorSeconds(10)

}


export default function AdminUser({
        username, setUsername,
        admin, setAdmin,
        status, setStatus,
        statusText, setStatusText
    }) {
    
    const router = useRouter()

    const [users, setUsers] = useState([])
    const [error, setError] = useState("")
    const [errorMessage, setErrorMessage] = useState("")
    const [errorSeconds, setErrorSeconds] = useState(0)
    const [showConfirm, setShowConfirm] = useState(false)
    const [confirmMessage, setConfirmMessage] = useState("")
    const [confirmYes, setConfirmYes] = useState(() => () => {})
    const [confirmNo, setConfirmNo] = useState(() => () => {})
    const [usernameInput, setUsernameInput] = useState("")
    const [passwordInput, setPasswordInput] = useState("")
    const [adminInput, setAdminInput] = useState(false)

    useEffect(() => {
        if (username == "") getUser(router, setStatus, setStatusText, setUsername, setAdmin)
        getUsers(setStatus, setStatusText, setUsers)
    }, [])

    useEffect(() => {
        const interval = setInterval(() => {
            if (errorSeconds > 0) {
                setErrorSeconds(errorSeconds - 1)
            }
        }, 1000);
        return () => clearInterval(interval);
    }, [errorSeconds])

    const unregister = async (username) => {
        setConfirmYes(() => async () => {
            await unregisterUser(setStatus, setStatusText, setError, setErrorMessage, setErrorSeconds, username)
            getUsers(setStatus, setStatusText, setUsers)
            setShowConfirm(false)
        })
        setConfirmNo(() => () => {
            setShowConfirm(false)
        })
        setConfirmMessage("Are you sure, that you want to unregister this user?")
        setShowConfirm(true)
    }

    const register = async () => {
        if (usernameInput.length == 0) {
            setError("empty_field")
            setErrorMessage("The username field is empty!")
            setErrorSeconds(10)
            return
        }
        if (passwordInput.length == 0) {
            setError("empty_field")
            setErrorMessage("The password field is empty!")
            setErrorSeconds(10)
            return
        }
        if (passwordInput.length < 4) {
            setError("too_short")
            setErrorMessage("The password need to be at least 4 characters long!")
            setErrorSeconds(10)
            return
        }
        await registerUser(setStatus, setStatusText, setError, setErrorMessage, setErrorSeconds, usernameInput, passwordInput, adminInput)
        getUsers(setStatus, setStatusText, setUsers)
    }

    const handleKeyPress = (e) => {
        if (e.key == "Enter") {
            register()
        }
    }

    if (status != 200) return (
        <APIOffline status={status} statusText={statusText} />
    )

    if (username == "") return (
        <Loading />
    )

    if (!admin) return (
        <AccessDenied />
    )

    return (
        <Animator>
            <ErrorPopup error={error} errorMessage={errorMessage} errorSeconds={errorSeconds} />
            <ConfirmPopup message={confirmMessage} yes={confirmYes} no={confirmNo} show={showConfirm} />
            <div className="flex flex-col h-screen">
                <Header username={username} admin={admin} />
                <div className="bg-indigo-950 flex justify-center items-center height-between">
                    <div className="flex flex-col bg-indigo-900 w-5/6 h-5/6 rounded-xl">
                        <div className="flex flex-col items-center mt-3">
                            <div className="grid grid-cols-3 grid-rows-3 gap-3 mb-4 items-center">
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
                                <div className="text-indigo-50 text-3xl text-right pr-3">
                                    Admin
                                </div>
                                <div className="col-span-2">
                                    <input className="appearance-none w-6 h-6 mt-1 border-2 border-indigo-200
                                        bg-indigo-300 hover:bg-indigo-400 hover:border-indigo-100 rounded
                                        transition-colors duration-150 checked:bg-red-500 checked:border-red-400
                                        checked:hover:bg-red-600 checked:hover:border-red-300"
                                        type="checkbox" checked={adminInput}
                                        onChange={(e) => setAdminInput(e.target.checked)}></input>
                                </div>
                            </div>
                            <button className="bg-indigo-500 px-20 pt-1 pb-2 mb-4 rounded-xl
                            text-indigo-50 text-3xl hover:bg-indigo-600 transition-colors
                            duration-150 border-indigo-400 hover:border-indigo-300 border-2"
                            onClick={register}>Register User</button>
                        </div>
                        <div className="flex flex-col items-center mt-3 overflow-y-auto">
                            {
                                users.map((user, i) => {return (
                                    <div key={i} className="w-full">
                                        <div className="bg-indigo-400 mx-3 mb-3 rounded-xl flex justify-between items-center">
                                            <div className="flex items-center">
                                                <div className="text-3xl text-indigo-950 pt-2 pb-3 pl-3">{user.name}</div>
                                                {(user.admin) ? (
                                                    <div className="text-2xl text-red-600 bg-red-300 font-bold font-mono px-2 rounded-xl ml-3">ADMIN</div>
                                                ) : <></>}
                                            </div>
                                            {(user.name == "admin" | user.name == username) ? <></> : (
                                                <button className="bg-red-500 rounded mr-3 px-5
                                                text-red-50 text-3xl hover:bg-red-600 transition-color
                                                duration-300 border-red-400 hover:border-red-300 border-2"
                                                onClick={() => unregister(user.name)}><MdDelete /></button>
                                            )}
                                        </div>
                                    </div>
                                )})
                            }
                        </div>
                    </div>
                </div>
                <Footer />
            </div>
        </Animator>
    )

}
