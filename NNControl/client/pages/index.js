import APIOffline from "@/components/APIOffline";
import Animator from "@/components/Animator";
import ConfirmPopup from "@/components/ConfirmPopup";
import ControlBar from "@/components/ControlBar";
import ErrorPopup from "@/components/ErrorPopup";
import Footer from "@/components/Footer";
import Header from "@/components/Header";
import Loading from "@/components/Loading";
import Sidebar from "@/components/Sidebar";
import { getUser } from "@/components/api";
import { BACKEND_URL_WS } from "@/components/constant";
import { useRouter } from "next/router";
import { useEffect, useRef, useState } from "react";
import { MdArrowDropDownCircle } from "react-icons/md";
import io from "socket.io-client"


function manageSocket(socket, setSocket, setError, setErrorMessage, setErrorSeconds, setServers) {
    
    if (socket == null) {
        console.info("Connect to socket")
        setSocket(io(BACKEND_URL_WS, {
            withCredentials: true
        }))
        return
    }

    socket.on("connect", () => {
        console.info("Socket connected")
        socket.emit("get_servers", {})
    })

    socket.on("servers", (data) => {
        console.info("Got servers")
        setServers(data.servers)
    })

    socket.on("nikocraft_error", (data) => {
        console.error("Socket error: " + data.error)
        setError(data.error)
        setErrorMessage(data.message)
        setErrorSeconds(10)
    })

    return () => socket.close()

}


export default function Home({
        username, setUsername,
        admin, setAdmin,
        status, setStatus,
        statusText, setStatusText
    }) {

    const router = useRouter()

    const consoleOutput = useRef()
    const [servers, setServers] = useState([])
    const [socket, setSocket] = useState(null)
    const [commandInput, setCommandInput] = useState("")
    const [autoScroll, setAutoScroll] = useState(true)
    const [error, setError] = useState("api_unreachable")
    const [errorMessage, setErrorMessage] = useState("This is a test error!")
    const [errorSeconds, setErrorSeconds] = useState(0)
    const [online, setOnline] = useState(false)
    const [mode, setMode] = useState("off")
    const [showConfirm, setShowConfirm] = useState(false)
    const [confirmMessage, setConfirmMessage] = useState("")
    const [confirmYes, setConfirmYes] = useState(() => () => {})
    const [confirmNo, setConfirmNo] = useState(() => () => {})

    const submit = async () => {
        consoleOutput.current.value = consoleOutput.current.value + commandInput + "\n"
        setCommandInput("")
        if (autoScroll) {
            consoleOutput.current.scrollTop = consoleOutput.current.scrollHeight
        }
        setErrorSeconds(10)
    }

    const toggleAutoScroll = () => {
        if (!autoScroll) consoleOutput.current.scrollTop = consoleOutput.current.scrollHeight
        setAutoScroll(!autoScroll)
    }

    const changeMode = (newMode) => {
        setConfirmYes(() => () => {
            setMode(newMode)
            if (newMode == "off")
                setOnline(false)
            else setOnline(true)
            setShowConfirm(false)
        })
        setConfirmNo(() => () => {
            setShowConfirm(false)
        })
        setConfirmMessage("Are you sure, that you want to change the mode of this server?")
        setShowConfirm(true)
    }

    useEffect(() => {
        if (username == "") getUser(router, setStatus, setStatusText, setUsername, setAdmin)
    }, [])

    useEffect(() => {
        manageSocket(socket, setSocket, setError, setErrorMessage, setErrorSeconds, setServers)
    }, [socket])

    useEffect(() => {
        const interval = setInterval(() => {
            if (errorSeconds > 0) {
                setErrorSeconds(errorSeconds - 1)
            }
        }, 1000);
        return () => clearInterval(interval);
    }, [errorSeconds])

    if (status != 200) return (
        <APIOffline status={status} statusText={statusText} />
    )

    if (username == "") return (
        <Loading />
    )

    return (
        <Animator>
            <ErrorPopup error={error} errorMessage={errorMessage} errorSeconds={errorSeconds} />
            <ConfirmPopup message={confirmMessage} yes={confirmYes} no={confirmNo} show={showConfirm} />
            <div className="flex flex-col h-screen">
                <Header username={username} admin={admin} />
                <div className="flex height-between">
                    <Sidebar servers={servers}/>
                    <div className="bg-indigo-950 flex flex-col justify-center items-center w-full">
                        <div className="bg-indigo-900 w-5/6 h-3/4 rounded-xl p-3">
                            <div className="relative">
                                {
                                    (autoScroll) ? (
                                        <button className="bg-indigo-500 rounded absolute right-2 top-2 p-1
                                        text-indigo-50 text-4xl hover:bg-indigo-600 transition-colors
                                        duration-150 border-indigo-400 hover:border-indigo-300 border-2"
                                        onClick={toggleAutoScroll}><MdArrowDropDownCircle /></button>
                                    ) : (
                                        <button className="bg-gray-500 rounded absolute right-2 top-2 p-1
                                        text-gray-50 text-4xl hover:bg-gray-600 transition-colors
                                        duration-150 border-gray-400 hover:border-gray-300 border-2"
                                        onClick={toggleAutoScroll}><MdArrowDropDownCircle /></button>
                                    )
                                }
                            </div>
                            <textarea className="bg-indigo-300 text-indigo-950 rounded-xl font-mono
                            border-indigo-200 border-2 resize-none h-full w-full p-1"
                            wrap="hard"
                            ref={consoleOutput}
                            readOnly></textarea>
                        </div>
                        <ControlBar commandInput={commandInput} setCommandInput={setCommandInput}
                            submit={submit} online={online} mode={mode} changeMode={changeMode}/>
                    </div>
                </div>
                <Footer />
            </div>
        </Animator>
    )

}
