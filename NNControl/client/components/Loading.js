import Footer from "./Footer";
import Animator from "./Animator";

export default function Loading() {

    return (
        <Animator>
            <div className="flex flex-col h-screen">
                <div className="bg-indigo-950 flex justify-center items-center h-full">
                    <div className="flex flex-col items-center justify-center bg-indigo-900 w-5/6 h-5/6 rounded-xl">
                        <h1 className="text-8xl font-bold text-indigo-50 text-center">Loading</h1>
                    </div>
                </div>
                <Footer />
            </div>
        </Animator>
    )

}
