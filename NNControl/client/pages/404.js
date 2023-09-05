import Animator from "@/components/Animator";
import Footer from "@/components/Footer";
import Link from "next/link";
import { useRouter } from "next/router";
import { useEffect, useState } from "react";

export default function PageNotFound() {

    const router = useRouter()

    const [wait, setWait] = useState(10);

    useEffect(() => {
        const interval = setInterval(() => {
            setWait(wait - 1)
            if (wait <= 0) router.push("/")
        }, 1000);
        return () => clearInterval(interval);
    }, [wait])

    return (
        <Animator>
            <div className="flex flex-col h-screen">
                <div className="bg-indigo-950 flex justify-center items-center h-full">
                    <div className="flex flex-col items-center justify-center bg-indigo-900 w-5/6 h-5/6 rounded-xl">
                        <h1 className="text-8xl font-bold text-indigo-50 text-center
                            pt-8">404</h1>
                        <h2 className="text-4xl text-indigo-50 text-center
                            pt-5 mb-20">Error</h2>
                        <div className="text-3xl text-indigo-50 pb-8">The requested page couldn't be found!</div>
                        <div className="text-2xl text-indigo-50 pb-8 italic">
                            Redirect to <Link href="/" className="underline hover:font-bold">Home</Link> in {wait} seconds ...</div>
                    </div>
                </div>
                <Footer />
            </div>
        </Animator>
    )

}