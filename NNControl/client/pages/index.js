import APIOffline from "@/components/APIOffline";
import Animator from "@/components/Animator";
import ControlBar from "@/components/ControlBar";
import ErrorPopup from "@/components/ErrorPopup";
import Footer from "@/components/Footer";
import Header from "@/components/Header";
import Loading from "@/components/Loading";
import Sidebar from "@/components/Sidebar";
import { getUser } from "@/components/api";
import { useRouter } from "next/router";
import { useEffect, useState } from "react";


export default function Home({
        username, setUsername,
        admin, setAdmin,
        status, setStatus,
        statusText, setStatusText
    }) {

    const router = useRouter()

    const [commandInput, setCommandInput] = useState("")

    const submit = async () => {
    }

    useEffect(() => {
        if (username == "") getUser(router, setStatus, setStatusText, setUsername, setAdmin)
    }, [])

    if (status != 200) return (
        <APIOffline status={status} statusText={statusText} />
    )

    if (username == "") return (
        <Loading />
    )

    return (
        <Animator>
            <div className="flex flex-col h-screen">
                <Header username={username} admin={admin} />
                <div className="flex height-between">
                    <Sidebar />
                    <div className="bg-indigo-950 flex flex-col justify-center items-center w-full">
                        <div className="flex flex-col items-center justify-center bg-indigo-900 w-5/6 h-3/4 rounded-xl">
                            <h1 className="text-8xl font-bold text-indigo-50 text-center
                                pt-8">404</h1>
                        </div>
                        <ControlBar commandInput={commandInput} setCommandInput={setCommandInput}
                            submit={submit} online={true}/>
                    </div>
                </div>
                <Footer />
            </div>
            <ErrorPopup />
        </Animator>
    )

}
