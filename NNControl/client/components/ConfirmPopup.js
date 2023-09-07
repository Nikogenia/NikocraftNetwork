export default function ConfirmPopup({message, yes, no, show}) {

    if (!show) return <></>
    return (
        <>
        <div className="fixed left-0 top-0 right-0 bottom-0 bg-black opacity-50"></div>
        <div className="fixed left-0 top-0 right-0 bottom-0">
            <div className="absolute flex align-middle justify-center top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2">
                <div className="bg-indigo-800 rounded-xl flex flex-col p-4">
                    <div className="text-5xl text-red-200 text-center mb-4">Confirmation</div>
                    <div className="text-2xl text-red-200 text-center mb-6">{message}</div>
                    <div className="flex justify-center">
                        <button className="bg-red-700 rounded py-1 px-14 mr-4
                            text-red-200 text-3xl hover:bg-red-800 transition-color
                            duration-300 border-red-600 hover:border-red-500 border-2"
                            onClick={no}>NO</button>
                        <button className="bg-lime-700 rounded py-1 px-14
                            text-lime-200 text-3xl hover:bg-lime-800 transition-color
                            duration-300 border-lime-600 hover:border-lime-600 border-2"
                            onClick={yes}>YES</button>
                    </div>
                </div>
            </div>
        </div>
        </>
    )

}
