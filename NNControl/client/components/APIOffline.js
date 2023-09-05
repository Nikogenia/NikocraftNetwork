import Link from "next/link";
import Footer from "./Footer";
import Animator from "./Animator";

export default function APIOffline({status, statusText}) {

    return (
        <Animator>
        <div className="flex flex-col h-screen">
            <div className="bg-indigo-950 flex justify-center items-center h-full">
                <div className="flex flex-col items-center justify-center bg-indigo-900 w-5/6 h-5/6 rounded-xl">
                    <h1 className="text-8xl font-bold text-indigo-50 text-center
                        pt-8">Offline</h1>
                    <h2 className="text-4xl text-indigo-50 text-center
                        pt-5 mb-20">API Error</h2>
                    <div className="text-3xl text-indigo-50 pb-5">Cannot connect to API!</div>
                    <div className="text-2xl text-indigo-50 pb-5 italic">
                        Trying to reconnect ...</div>
                    <Link href="/" className="underline hover:font-bold text-3xl text-indigo-50 pb-8">Home</Link>
                </div>
            </div>
            <Footer />
        </div>
        </Animator>
    )

}
